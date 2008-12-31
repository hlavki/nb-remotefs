package org.netbeans.modules.remotefs.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import static org.netbeans.modules.remotefs.api.RemoteFile.Status.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hlavki
 */
public final class RemoteFile {

    public static final Logger log = Logger.getLogger(RemoteFile.class.getName());
    static final String PATH_SEPARATOR = "/";
    private RemoteFileAttributes attributes;
    private RemoteFile parent;
    private RemoteClient client;
    private Status status;
    private RemoteFile[] children = new RemoteFile[0];
    private RemoteFileSystem remoteFs;
    private FileObject file;
    private boolean onServer;

    // state attributes
    private boolean childrenChanged;

    public RemoteFile(RemoteFileAttributes attributes, RemoteFile parent, RemoteClient client,
            RemoteFileSystem remoteFs) throws IOException {
        this.parent = parent;
        this.attributes = attributes;
        this.client = client;
        this.remoteFs = remoteFs;
        status = NOT_CACHED;
        childrenChanged = true;
        createCacheFile();
    }

    public RemoteFile(RemoteClient client, RemoteFileSystem remoteFs, FileObject file) throws IOException {
        this(new RemoteFileAttributes(client.getRoot(), true), (RemoteFile) null, client, remoteFs);
    }

    /**
     * Test whether this file is directory
     * @return true if directory, false otherwise
     */
    public boolean isDirectory() {
        return attributes.isDirectory();
    }

    /**
     * Set if file is directory
     * @return true if directory, false otherwise
     */
    public void setDirectory(boolean directory) {
        attributes.setDirectory(directory);
    }

    /**
     * Test whether this file is root.
     * @return true if root
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Returns name of this file.
     * @return name
     */
    public RemoteFileName getName() {
        return attributes.getName();
    }

    /**
     * Returns list of children
     * @return list of children
     * @throws java.io.IOException
     */
    public RemoteFile[] getChildren() throws IOException {
        if (childrenChanged && remoteFs.isRefreshServer() && client.isConnected()) {
            List<RemoteFile> childrenList = new ArrayList<RemoteFile>();
            log.info("FUCk1: " + getName());
            RemoteFileAttributes[] attrs = client.list(getName());
            for (RemoteFileAttributes attr : attrs) {
                RemoteFile rFile = new RemoteFile(attr, this, client, remoteFs);
                childrenList.add(rFile);
            }
            childrenChanged = false;
            children = childrenList.toArray(children);
        }
        return children;
    }

    /** Get all children.
     * @return array of String of children
     * @throws java.io.IOException
     */
    public String[] getStringChildren() throws IOException {
        getChildren();
        String s[] = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                s[i] = children[i].getName().getName();
            }
        }
        return s;
    }

    public void synchronize() throws IOException {
        log.info("Now synchronizing! It means doing nothing...");
    }

    /** Get child specified by the name.
     * @param name name of the child
     * @return found child, or null
     * @throws java.io.IOException
     */
    public RemoteFile getChild(String name) throws IOException {
        getChildren();
        return getExistingChild(name);
    }

    public RemoteFile getExistingChild(String name) throws IOException {
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null && children[i].getName().getName().equals(name)) {
                return children[i];
            }
        }
        return null;
    }

    public RemoteFile find(String name) throws IOException {
        RemoteFile remoteFile = this, newfile;
        if (name.equals(".")) {
            return remoteFile;
        }
        StringTokenizer st = new StringTokenizer(name, PATH_SEPARATOR);
        while (st.hasMoreTokens()) {
            String next = st.nextToken();
            newfile = remoteFile.getExistingChild(next);
            if (newfile == null) {
                newfile = remoteFile.getChild(next);
            }
            remoteFile = newfile;
            if (remoteFile == null) {
                break;
            }
        }
        return remoteFile;
    }

    public void createFolder(String name) {
        log.info("Creating folder...");
    }

    public void createData(String name) {
        log.info("Creating data...");
    }

    public void rename(String newName) {
        log.info("Renaming...");
    }

    public boolean isReadOnly() {
        return attributes.isReadable() && !attributes.isWriteable();
    }

    public Long getSize() {
        return attributes.getSize();
    }

    public Date getLastModified() {
        return attributes.getLastModified();
    }

    /**
     * Get InputStream
     * @throws IOException
     * @return ImputStream of the file
     */
    public InputStream getInputStream() throws IOException {
        FileChangeStatus chStatus = getFileChangeStatus();
        log.info("INFile Change Status: " + chStatus);
        if (status == NOT_CACHED) {
            load();
        }
        return file != null ? file.getInputStream() : null;
    }

    /** Returns OutputStream
     * @throws IOException
     * @return OutputStream of the file
     */
    public OutputStream getOutputStream() throws IOException {
        FileChangeStatus chStatus = getFileChangeStatus();
        log.info("OUTFile Change Status: " + chStatus);
        status = OPEN;
        return file != null ? new RemoteOutputStream(this) : null;
    }

    /** Get file attributes for one file. If it doesn't work, disable alwaysRefresh
     * @return
     * @throws java.io.IOException
     */
    protected RemoteFileAttributes getFileAttributes() throws IOException {
        //if (!notify.isAlwaysRefresh()) return null;
        //System.out.println("RemoteFile.getFileAttributes: path="+getPath());
        //TODO:
        RemoteFileAttributes at[] = client.list(getName());
        if (at == null || at.length == 0) {
            if (!onServer) {
                return null;
            }
            //System.out.println("TESTING alwaysRefresh");
            at = client.list(getParent().getName());
            if (at != null) {
                for (int i = 0; i < at.length; i++) {
                    if (at[i].getName().getName().equals(attributes.getName().getName())) {
                        remoteFs.setAlwaysRefresh(false);
                        //System.out.println("TEST: alwaysRefresh not supported. Disabling.");
                        return null;
                    }
                }
            }
            //System.out.println("TEST: test failed.");
            return null;
        }
        return at[0];
    }

    public void save() throws IOException {
        log.info("Saving file to remote file system... Status: " + status);
        //System.out.println("RemoteFile.save: path="+getPath());
        if (isDirectory()) {
            // TODO: ???
        } else /* if (status == CHANGED || status == OPEN) */ {
            status = CHANGED;
            if (!client.isConnected()) {
                return;
            }
            //System.out.println("Uploading "+getPath()+" to server");
            // TODO: change client.put method to use FileObject
            client.put(FileUtil.toFile(file), getName());
//            cachelastmodified = file.lastModified();
            attributes.setSize(file.getSize());
            RemoteFileAttributes rfa = getFileAttributes();
            if (rfa != null) {
                attributes.setLastModified(rfa.getLastModified());

                // TODO: fix this
//                file.setLastModified(rfa.getDate().getTime());
                FileUtil.toFile(file).setLastModified(rfa.getLastModified().getTime());
//                cachelastmodified = rfa.getDate().getTime();
            } else {
                attributes.setLastModified(new Date(0));
            } // TODO: get time from server?
            status = CACHED;
            onServer = true;
        //System.out.println("RemoteFile.save: end. path="+getPath());
        }

    }

    public void delete() {
        log.info("Deleting file from remote file system...");
    }

    /** Return parent object.
     * @return parent object, null if this is root
     */
    public RemoteFile getParent() {
        return parent;
    }

    enum Status {

        NOT_CACHED, CACHED, OPEN, CHANGED
    }

    FileObject getFileObject() {
        return file;
    }

    private void createCacheFile() throws IOException {
        if (!this.isDirectory()) {
            String suffix = getFileExtension(attributes.getName());
            File tmpFile = File.createTempFile("file", suffix, FileUtil.toFile(remoteFs.getCache()));
            tmpFile.setLastModified(attributes.getLastModified().getTime());
            tmpFile.deleteOnExit();
            file = FileUtil.toFileObject(tmpFile);
        }
    }

    /** Load file from server to cache.
     * @throws IOException
     */
    protected void load() throws IOException {
        //System.out.println("RemoteFile.load: path="+getPath());
        if (!client.isConnected()) {
            return;
        }
        if (isDirectory()) {
            return;
        } else {
//            if (onServer) {
            //System.out.println("Downloading "+getPath()+" from server");
            client.get(getName(), FileUtil.toFile(file));
//                file.setLastModified(cacheLastModified = attrib.getDate().getTime());
//            }
            status = Status.CACHED;
        }
    }

    private String getFileExtension(RemoteFileName name) {
        String fileName = name.getName();
        int idx = fileName.lastIndexOf('.');
        return idx > -1 ? fileName.substring(idx) : null;
    }

    private FileChangeStatus getFileChangeStatus() throws IOException {
        FileChangeStatus result = FileChangeStatus.NO_CHANGE;
        if (isDirectory()) {
        } else {
            RemoteFileAttributes newRemoteAttrs = getFileAttributes();
            log.info("LOCAL ATTRS: " + attributes);
            log.info("REMOTE ATTRS: " + newRemoteAttrs);
            log.info("LOCAL & REMOTE EQUALS: " + attributes.equals(newRemoteAttrs));
//            if (client.isConnected()) {
//                if (newRemoteAttrs != null) {
//                    // if onserver==false but newattr says that file exist on server
//                    if (!onServer && !(newRemoteAttrs.getLastModified().getTime() == 0 && newRemoteAttrs.getSize() == 0)) {
//                        onServer = true;
//                        result = FileChangeStatus.LOCAL_CHANGE;
//                    } else {
//                        if (onServer) {
//                            // date of this file isn't yet known
//                            if (attributes.getSize() == newRemoteAttrs.getSize() && attributes.getLastModified().getTime() == 0) {
//                                attributes.setLastModified(newRemoteAttrs.getLastModified());
//                            }
//                            // if both files are empty
//                            if (attributes.getSize() == 0 && newRemoteAttrs.getSize() == 0) {
//                                result = FileChangeStatus.NO_CHANGE;
//                            } else // if size or date differ
//                            if (attributes.getSize() != newRemoteAttrs.getSize() ||
//                                    !attributes.getLastModified().equals(newRemoteAttrs.getLastModified())) {
//                                result = FileChangeStatus.LOCAL_CHANGE;
//                            }
//                        }
//                    }
//                }
//            }
        }
        return result;
    }
}
