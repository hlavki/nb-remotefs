/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.api.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.remotefs.api.LogInfo;
import org.netbeans.modules.remotefs.api.RemoteFileSystemInfo;
import org.netbeans.modules.remotefs.api.RemoteFileSystemManager;
import org.netbeans.modules.remotefs.api.UnknownFileSystemException;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.Environment;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.XMLDataObject;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.xml.EntityCatalog;
import org.openide.xml.XMLUtil;
import org.openide.filesystems.Repository;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author hlavki
 */
public class ConnectionPersistenceManager implements Environment.Provider, InstanceCookie.Of {

    /**
     * The path where the connections are registered in the SystemFileSystem.
     */
    public static final String REMOTE_FS_CONNECTIONS_PATH = "RemoteFileSystems"; // NOI18N
    private static final Logger log = Logger.getLogger(ConnectionPersistenceManager.class.getName());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    /**
     * The delay by which the write of the changes is postponed.
     */
    private static final int DELAY = 2000;
    private static FileObject newlyCreated;
    private static LogInfo newlyCreatedInstance;
    private final Reference<XMLDataObject> holder;
    /**
     * The lookup provided through Environment.Provider.
     */
    private Lookup lookup = null;
    private Reference<LogInfo> refLogInfo = new WeakReference<LogInfo>(null);
    private PCL listener;

    private static ConnectionPersistenceManager createProvider() {
        return new ConnectionPersistenceManager();
    }

    private ConnectionPersistenceManager() {
        holder = new WeakReference<XMLDataObject>(null);
    }

    private ConnectionPersistenceManager(XMLDataObject object) {
        holder = new WeakReference<XMLDataObject>(object);
        InstanceContent cookies = new InstanceContent();
        cookies.add(this);
        lookup = new AbstractLookup(cookies);
    }

    private ConnectionPersistenceManager(XMLDataObject object, LogInfo existingInstance) {
        this(object);
        refLogInfo = new WeakReference<LogInfo>(existingInstance);
        attachListener();
    }

    // Environment.Provider methods
    public Lookup getEnvironment(DataObject obj) {
        if (obj.getPrimaryFile() == newlyCreated) {
            return new ConnectionPersistenceManager((XMLDataObject) obj, newlyCreatedInstance).getLookup();
        } else {
            return new ConnectionPersistenceManager((XMLDataObject) obj).getLookup();
        }
    }

    // InstanceCookie.Of methods
    public String instanceName() {
        XMLDataObject obj = getHolder();
        return obj == null ? "" : obj.getName();
    }

    public Class instanceClass() {
        return ConnectionPersistenceManager.class;
    }

    public boolean instanceOf(Class<?> type) {
        return (type.isAssignableFrom(LogInfo.class));
    }

    public Object instanceCreate() throws java.io.IOException, ClassNotFoundException {
        synchronized (this) {
            Object o = refLogInfo.get();
            if (o != null) {
                return o;
            }

            XMLDataObject obj = getHolder();
            if (obj == null) {
                return null;
            }
            FileObject connectionFO = obj.getPrimaryFile();
            Handler handler = new Handler(connectionFO.getNameExt());
            try {
                XMLReader reader = XMLUtil.createXMLReader();
                InputSource is = new InputSource(obj.getPrimaryFile().getInputStream());
                is.setSystemId(connectionFO.getURL().toExternalForm());
                reader.setContentHandler(handler);
                reader.setErrorHandler(handler);
                reader.setEntityResolver(EntityCatalog.getDefault());

                reader.parse(is);
            } catch (SAXException ex) {
                Exception x = ex.getException();
                if (x instanceof java.io.IOException) {
                    throw (IOException) x;
                } else {
                    throw new java.io.IOException(ex.getMessage());
                }
            }

            LogInfo inst = createConnection(handler);
            refLogInfo = new WeakReference<LogInfo>(inst);
            attachListener();
            return inst;
        }
    }

    private XMLDataObject getHolder() {
        return holder.get();
    }

    private void attachListener() {
        listener = new PCL();
        LogInfo logInfo = (refLogInfo.get());
        logInfo.addPropertyChangeListener(WeakListeners.propertyChange(listener, logInfo));
    }

    private static LogInfo createConnection(Handler handler) {
        return handler.connInfo;
    }

    /**
     * Creates the XML file describing the specified connection.
     */
    public static DataObject create(LogInfo logInfo) throws IOException {
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource(REMOTE_FS_CONNECTIONS_PATH);
        DataFolder df = DataFolder.findFolder(fo);

        AtomicWriter writer = new AtomicWriter(logInfo, df, convertToFileName(logInfo.getDisplayName()));
        df.getPrimaryFile().getFileSystem().runAtomicAction(writer);
        return writer.holder;
    }

    private static String convertToFileName(String databaseURL) {
        return databaseURL.substring(0, Math.min(32, databaseURL.length())).replaceAll("[^\\p{Alnum}]", "_"); // NOI18N
    }

    /**
     * Removes the file describing the specified connection.
     */
    public static void remove(LogInfo connInfo) throws IOException {
        String name = connInfo.getDisplayName();
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource(REMOTE_FS_CONNECTIONS_PATH); //NOI18N
        DataFolder folder = DataFolder.findFolder(fo);
        DataObject[] objects = folder.getChildren();

        for (int i = 0; i < objects.length; i++) {
            InstanceCookie ic = objects[i].getCookie(InstanceCookie.class);
            if (ic != null) {
                Object obj = null;
                try {
                    obj = ic.instanceCreate();
                } catch (ClassNotFoundException e) {
                    continue;
                }
                if (obj instanceof LogInfo) {
                    LogInfo instance = (LogInfo) obj;
                    if (instance.getDisplayName().equals(name)) {
                        objects[i].delete();
                        break;
                    }
                }
            }
        }
    }

    Lookup getLookup() {
        return lookup;
    }

    static String decodePassword(byte[] bytes) throws CharacterCodingException {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder(); // NOI18N
        ByteBuffer input = ByteBuffer.wrap(bytes);
        int outputLength = (int) (bytes.length * (double) decoder.maxCharsPerByte());
        if (outputLength == 0) {
            return null; // NOI18N
        }
        char[] chars = new char[outputLength];
        CharBuffer output = CharBuffer.wrap(chars);
        CoderResult result = decoder.decode(input, output, true);
        if (!result.isError() && !result.isOverflow()) {
            result = decoder.flush(output);
        }
        if (result.isError() || result.isOverflow()) {
            throw new CharacterCodingException();
        } else {
            return new String(chars, 0, output.position());
        }
    }

    /**
     * Atomic writer for writing a changed/new connection.
     */
    private static final class AtomicWriter implements FileSystem.AtomicAction {

        LogInfo instance;
        MultiDataObject holder;
        String fileName;
        DataFolder parent;

        /**
         * Constructor for writing to an existing file.
         */
        AtomicWriter(LogInfo instance, MultiDataObject holder) {
            this.instance = instance;
            this.holder = holder;
        }

        /**
         * Constructor for creating a new file.
         */
        AtomicWriter(LogInfo instance, DataFolder parent, String fileName) {
            this.instance = instance;
            this.fileName = fileName;
            this.parent = parent;
        }

        public void run() throws java.io.IOException {
            FileLock lck;
            FileObject data;

            if (holder != null) {
                data = holder.getPrimaryEntry().getFile();
                lck = holder.getPrimaryEntry().takeLock();
            } else {
                FileObject folder = parent.getPrimaryFile();
                String fn = FileUtil.findFreeFileName(folder, fileName, "xml"); //NOI18N
                data = folder.createData(fn, "xml"); //NOI18N
                lck = data.lock();
            }

            try {
                OutputStream ostm = data.getOutputStream(lck);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(ostm, "UTF8")); //NOI18N
                write(writer);
                writer.flush();
                writer.close();
                ostm.close();
            } finally {
                lck.releaseLock();
            }

            if (holder == null) {
                // a new DataObject must be created for instance
                // ensure that the object returned by this DO's InstanceCookie.instanceCreate()
                // method is the same as instance
                newlyCreated = data;
                newlyCreatedInstance = instance;
                holder = (MultiDataObject) DataObject.find(data);
                // ensure the Environment.Provider.getEnvironment() is called for the new DataObject
                holder.getCookie(InstanceCookie.class);
                newlyCreated = null;
                newlyCreatedInstance = null;
            }
        }

        void write(PrintWriter pw) throws IOException {
            pw.println("<?xml version='1.0'?>"); //NOI18N
            pw.println("<!DOCTYPE remote-file-system PUBLIC '-//RemoteFileSystem//DTD RemoteFileSystem 1.0//EN' 'http://www.netbeans.org/dtds/remote-file-system-1_0.dtd'>"); //NOI18N
            pw.println("<remotefs>"); //NOI18N
            pw.println("  <" + Handler.ELEMENT_NAME + " value='" + instance.getDisplayName() + "'/>"); //NOI18N
            for (Map.Entry<Object, Object> entry : instance.getProperties().entrySet()) {
                if (!entry.getKey().equals(LogInfo.PROP_NAME)) {
                    pw.println("  <property name='" + entry.getKey() + "' value='" + entry.getValue() + "'/>"); //NOI18N
                }
            }
            pw.println("</remotefs>"); //NOI18N
        }
    }

    /**
     * SAX handler for reading the XML file.
     */
    private static final class Handler extends DefaultHandler {

        private static final String ELEMENT_NAME = "name"; // NOI18N
        private static final String ELEMENT_PROPERTY = "property"; // NOI18N
        private static final String ATTR_PROPERTY_VALUE = "value"; // NOI18N
        private static final String ATTR_PROPERTY_NAME = "name"; // NOI18N
        private final String connectionFileName;
        Properties data;
        private LogInfo connInfo;

        public Handler(String connectionFileName) {
            // TODO create LogInfo instance through Manager
            this.connectionFileName = connectionFileName;
            this.data = new Properties();
        }

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
            String protocol = data.getProperty(LogInfo.PROP_PROTOCOL);
            try {
                RemoteFileSystemInfo fsInfo = RemoteFileSystemManager.getDefault().getFileSystemInfoByProtocol(protocol);
                connInfo = fsInfo.createLogInfo(data);
            } catch (UnknownFileSystemException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            String value = attrs.getValue(ATTR_PROPERTY_VALUE);
            if (ELEMENT_NAME.equals(qName)) {
                data.setProperty(LogInfo.PROP_NAME, value);
            } else if (ELEMENT_PROPERTY.equals(qName)) {
                String key = attrs.getValue(ATTR_PROPERTY_NAME);
                data.setProperty(key, value);
            }
        }
    }

    private final class PCL implements PropertyChangeListener, Runnable {

        /**
         * The list of PropertyChangeEvent that cause the connection to be saved.
         * Should probably be a set of LogInfo's instead.
         */
        LinkedList<PropertyChangeEvent> keepAlive = new LinkedList<PropertyChangeEvent>();
        RequestProcessor.Task saveTask = null;

        public void propertyChange(PropertyChangeEvent evt) {
            synchronized (this) {
                if (saveTask == null) {
                    saveTask = RequestProcessor.getDefault().create(this);
                }
                keepAlive.add(evt);
            }
            saveTask.schedule(DELAY);
        }

        public void run() {
            PropertyChangeEvent e;

            synchronized (this) {
                e = keepAlive.removeFirst();
            }
            LogInfo conn = (LogInfo) e.getSource();
            XMLDataObject obj = getHolder();
            if (obj == null) {
                return;
            }
            try {
                obj.getPrimaryFile().getFileSystem().runAtomicAction(new AtomicWriter(conn, obj));
            } catch (IOException ex) {
                Logger.getLogger("global").log(Level.INFO, null, ex);
            }
        }
    }
}

