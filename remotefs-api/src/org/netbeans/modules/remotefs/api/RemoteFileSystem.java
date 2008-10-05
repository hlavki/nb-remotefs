/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s):
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
/*
/* If you wish your version of this file to be governed by only the CDDL
/* or only the GPL Version 2, indicate your decision by adding
/* "[Contributor] elects to include this software in this distribution
/* under the [CDDL or GPL Version 2] license." If you do not indicate a
/* single choice of license, a recipient has the option to distribute
/* your version of this file under either the CDDL, the GPL Version 2 or
/* to extend the choice of license to its licensees as provided above.
/* However, if you add GPL Version 2 code and therefore, elected the GPL
/* Version 2 license, then the option applies only if the new code is
/* made subject to such option by the copyright holder.
 *
 * Contributor(s): Libor Martinek.
 */
package org.netbeans.modules.remotefs.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.AbstractFileSystem;
import org.openide.filesystems.DefaultAttributes;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.SystemAction;

/** Remote FileSystem class
 * @author Libor Martinek
 * @version 1.0
 */
public abstract class RemoteFileSystem extends AbstractFileSystem
        implements AbstractFileSystem.List, AbstractFileSystem.Info, AbstractFileSystem.Change,
        RemoteFile.Notify, RemoteFile.RequestProcessor {

    private static final Logger log = Logger.getLogger(RemoteFileSystem.class.getName());
    protected static final String DEFAULT_ROOT_DIR = "/";
    private static final String DEFAULT_SEPARATOR = "/";
    /** remote client */
    protected transient RemoteClient client;
    /** root file */
    protected transient RemoteFile rootFile;
    /** Root of cache directory */
    protected File cacheDir = null;
    /** Server start directory */
    protected String startDir = DEFAULT_ROOT_DIR;
    /** path separator */
    protected String separator = DEFAULT_SEPARATOR;
    /** Login information */
    protected LogInfo logInfo;
    /** is read only */
    protected boolean readOnly;
    /** Request processor */
    protected transient RequestProcessor requestProc;

    /** Constructor.
     */
    public RemoteFileSystem() {
        info = this;
        change = this;
        DefaultAttributes a = new DefaultAttributes(info, change, this);
        attr = a;
        list = a;
    }

    /** Return system action for this filesystem
     * @return actions */
    @Override
    public SystemAction[] getActions() {
        SystemAction actions[] = super.getActions();
        SystemAction newActions[] = new SystemAction[actions.length + 4];
        for (int i = 0; i < actions.length; i++) {
            newActions[i] = actions[i];
        }
        newActions[actions.length] = getAction(SynchronizeAction.class);
        newActions[actions.length + 1] = getAction(DownloadAllAction.class);
        newActions[actions.length + 2] = getAction(CleanCacheAction.class);
        newActions[actions.length + 3] = getAction(ConnectAction.class);
        ((ConnectAction) newActions[actions.length + 3]).setFS(this);
        return newActions;
    }

    private SystemAction getAction(Class<? extends SystemAction> clazz) {
        return org.openide.util.SharedClassObject.findObject(clazz, true);
    }

    protected void removeClient() {
        if (client != null) {
            client.close();
            client = null;
        }
        rootFile = null;
    }

    /** Connect to server on background 
     * @param b true for connecting, false for disconnecting */
    public void connectOnBackground(final boolean b) {
        post(new java.lang.Runnable() {

            public void run() {
                setConnected(b);
                getRoot().refresh();
            }
        });
    }

    /** Whether filesystem is connected to server.
     * @return true if fs is connected to server
     */
    public boolean isConnected() {
        if (client == null) {
            return false;
        }
        return client.isConnected();
    }

    /** Connect to or disconnect from server.
     * @param connected true for connecting, false for disconnecting
     */
    public void setConnected(boolean connected) {
        // is new state different?
        if (isConnected() == connected) {
            return;
        }
        if (!connected) {  // will be disconnected
            client.disconnect();
        } else {
            try {
                if (client == null || (client != null && client.compare(logInfo) != 0)) {
                    client = createClient(logInfo, cacheDir);
                    rootFile = null;
                }
                client.connect();
                if (rootFile == null) {
                    RemoteFile root = createRootFile(client, cacheDir);
                    rootFile = root.find(startDir);
                    if (rootFile == null) {
                        startdirNotFound(startDir, logInfo.getDisplayName());
                        startDir = DEFAULT_ROOT_DIR;
                        rootFile = root;
                    }
                }
            } catch (IOException e) {
                if (connected && client != null) {
                    client.close();
                }
                errorConnect(e.toString());
            }
            synchronize(startDir);
        }
        fireFileStatusChanged(new FileStatusEvent(this, getRoot(), true, true));
        //refreshRoot();
        //try { org.openide.loaders.DataObject.find(super.getRoot()).getNodeDelegate().setDisplayName(getDisplayName()); }
        //catch (org.openide.loaders.DataObjectNotFoundException e) {}
        firePropertyChange("connected", null, isConnected() ? Boolean.TRUE : Boolean.FALSE);
    //firePropertyChange(PROP_SYSTEM_NAME, "", getSystemName());
    }

    /**
     * Return node properties for properties window obtained from LogInfo
     * @return
     */
    public Node.Property[] getNodeProperties() {
        return logInfo.getNodeProperties(this);
    }

    /**
     * Return file directed to root directory of cache.
     * @param cacheDirName
     * @return
     * @throws java.io.IOException
     */
    protected final File getCacheRootDirectory(String cacheDirName) throws IOException {
        FileObject fr = Repository.getDefault().getDefaultFileSystem().getRoot();
        FileObject fsCache = fr.getFileObject(cacheDirName);
        FileObject fo = fsCache.getFileObject(this.cacheDir.getName());
        if (fo == null) {
            fo = fsCache.createFolder(this.cacheDir.getName());
        }
        return FileUtil.toFile(fo);
    }

    /** Create new client
     * @param loginfo
     * @param cache
     * @throws IOException
     * @return  */
    public abstract RemoteClient createClient(LogInfo loginfo, File cache) throws IOException;

    /** Create new root file
     * @param client
     * @param cache
     * @throws IOException
     * @return  */
    public RemoteFile createRootFile(RemoteClient client, File cache) throws IOException {
        return new RemoteFile(client, this, this, cache);
    }

    /** Set whether the file system should be read only.
     * @param flag true if it should
     */
    public void setReadOnly(boolean flag) {
        if (flag != readOnly) {
            readOnly = flag;
            firePropertyChange(PROP_READ_ONLY, !flag ? Boolean.TRUE : Boolean.FALSE, flag ? Boolean.TRUE : Boolean.FALSE);
        }
    }

    /* Test whether file system is read only.
     * @return true if file system is read only
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /** Prepare environment by adding the root directory of the file system to the class path.
     * @param environment the environment to add to
     */
//    @Override
//    public void prepareEnvironment(org.openide.filesystems.FileSystem.Environment environment) {
//        environment.addClassPath(cachedir.toString());
//    }
    /** Test whether filesystem is ready to write. If no, throws exception
     * @throws IOException if fs isn't ready to write */
    protected abstract void isReadyToModify() throws IOException;

    /** Test whether filesystem is ready to read. If no, throws exception
     * @throws IOException if fs isn't ready to read */
    protected abstract void isReadyToRead() throws IOException;

    /** Test whether filesystem is ready.
     * @return true, if fs is ready  */
    protected abstract boolean isReady();

    /** Get the RemoteFile for entered name
     * @param name of searching file
     * @return found RemoteFile
     * @throws java.io.IOException 
     */
    protected RemoteFile getRemoteFile(String name) throws IOException {
        RemoteFile remoteFile = rootFile.find(name);
        // hack: if attributes file is not found, create new
        // TODO: is this really neccessary?
//        if (remoteFile == null && (name.endsWith(DefaultAttributes.ATTR_NAME_EXT) || name.endsWith(".nbattrs"))) {
//            createData(name);
//            remoteFile = rootFile.find(name);
//        }
        return remoteFile;
    }

    /** Synchronize specified directory
     * @param name name of directory to synchronize */
    public void synchronize(String name) {
        if (!isReady()) {
            return;
        }
        try {
            final RemoteFile f = getRemoteFile(name);
            if (f != null) {
                post(new Runnable() {

                    public void run() {
                        try {
                            f.synchronize();
                        } catch (IOException e) {
                            Exceptions.printStackTrace(e);
                        }
                    }
                });
            } else {
                log.warning("File " + name + " not found!");
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

    /** Download whole directory with subdirectories to cache.
     * @param name name of directory to download1 */
    public void downloadAll(String name) {
        if (!isReady()) {
            return;
        }
        try {
            final RemoteFile f = getRemoteFile(name);
            if (f != null) {
                post(new Runnable() {

                    public void run() {
                        try {
                            f.downloadAll();
                        } catch (IOException e) {
                            Exceptions.printStackTrace(e);
                        }
                    }
                });
            } else {
                log.warning("File " + name + " not found!");
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

    /** Clean cache. Remove all files from cache.
     * @param name name of directory to clean */
    public void cleanCache(String name) {
        if (!isReady()) {
            return;
        }
        try {
            RemoteFile f = getRemoteFile(name);
            if (f != null) {
                f.cleanCache();
            } else {
                log.warning("File " + name + " not found!");
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

    //
    // List
    //

    /* Scans children for given name
     * @param name
     * @return
     */
    public String[] children(String name) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("START: Name: " + name);
        }
        String[] result = new String[0];
        if (!isReady()) {
            return result;
        }
        try {
            RemoteFile f = getRemoteFile(name);
            if (f != null) {
                if (f.isDirectory()) {
                    result = f.getStringChildren();
                }
            } else {
                log.warning("RemoteFileSystem.children: File " + name + " not found!");
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        return result;
    }

    //
    // Change
    //

    /* Creates new folder named name.
     * @param name name of folder
     * @throws IOException if operation fails
     */
    public void createFolder(String name) throws java.io.IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Name: " + name);
        }
        isReadyToModify();
        RemoteFile f = null;
        String relname = null;
        int lastslash = name.lastIndexOf(separator);
        if (lastslash == -1) {
            relname = name;
            f = rootFile;
        } else {
            relname = name.substring(lastslash + 1);
            f = rootFile.find(name.substring(0, lastslash));
        }
        if (f != null) {
            f.createFolder(relname);
        } else {
            log.warning("Parent of file " + name + " not found!");
        }
    }

    /** Creates new folder and all necessary subfolders
     * @param name
     * @throws IOException
     */
    public void createData(String name) throws IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Name: " + name);
        }
        isReadyToModify();
        RemoteFile f = null;
        String relname = null;
        int lastslash = name.lastIndexOf(separator);
        if (lastslash == -1) {
            relname = name;
            f = rootFile;
        } else {
            relname = name.substring(lastslash + 1);
            f = rootFile.find(name.substring(0, lastslash));
        }
        if (f != null) {
            f.createData(relname);
        } else {
            log.warning("Parent of file " + name + " not found!");
        }
    }

    /* Renames a file.
     * @param oldName old name of the file
     * @param newName new name of the file
     * @throws IOException
     */
    public void rename(String oldName, String newName) throws IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Old name: " + oldName + ", New name: " + newName);
        }
        isReadyToModify();
        RemoteFile of = getRemoteFile(oldName);
        if (of != null) {
            String name = null;
            String oname = oldName, nname = newName;
            if (!oldName.startsWith(startDir)) {
                oname = startDir + oldName;
            }
            if (!newName.startsWith(startDir)) {
                nname = startDir + newName;
            }
            int slash1 = oname.lastIndexOf(separator);
            int slash2 = nname.lastIndexOf(separator);
            if (slash1 != slash2 || !oname.substring(0, slash1).equals(nname.substring(0, slash2))) {
                IOException e = new IOException("Can't rename !!!!!!");
                e.printStackTrace();
                throw e;
            }
            if (slash2 == -1) {
                name = newName;
            } else {
                name = nname.substring(slash2 + 1);
            }
            of.rename(name);
        } else {
            log.warning("File " + oldName + " not found!");
        }
    }

    /* Delete the file. 
     *
     * @param name name of file
     * @throws IOException if the file could not be deleted
     */
    public void delete(String name) throws IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Name: " + name);
        }
        isReadyToModify();
        RemoteFile file = getRemoteFile(name);
        if (file != null) {
            file.delete();
        } else {
            log.warning("File " + name + " not found!");
        }
    }

    //
    // Info
    //

    /*
     * Get last modification time.
     * @param name the file to test
     * @return the date
     */
    public java.util.Date lastModified(String name) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Name: " + name);
        }
        java.util.Date date = new java.util.Date(0);
        if (!isReady()) {
            return date;
        }
        try {
            RemoteFile f = getRemoteFile(name);
            if (f != null) {
                date = f.lastModified();
            } else {
                log.warning("File " + name + " not found!");
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        return date;
    }

    /* Test if the file is folder or contains data.
     * @param name name of the file
     * @return true if the file is folder, false otherwise
     */
    public boolean folder(String name) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Name: " + name);
        }
        if (!isReady()) {
            return true;
        }
        try {
            RemoteFile f = getRemoteFile(name);
            if (f != null) {
                return f.isDirectory();
            } else {
                log.warning("File " + name + " not found!");
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        return true;
    }

    /* Test whether this file can be written to or not.
     * @param name the file to test
     * @return true if file is read-only
     */
    public boolean readOnly(String name) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Name: " + name);
        }
        if (!isReady()) {
            return false;
        }
        try {
            RemoteFile f = getRemoteFile(name);
            if (f != null) {
                return f.isReadOnly();
            } else {
                System.out.println("File " + name + " not found!");
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        return false;
    }

    /** Get the MIME type of the file.
     * Uses {@link FileUtil#getMIMEType}.
     *
     * @param name the file to test
     * @return the MIME type textual representation, e.g. <code>"text/plain"</code>
     */
    public String mimeType(String name) {
        return null;
    }

    /** Get the size of the file.
     *
     * @param name the file to test
     * @return the size of the file in bytes or zero if the file does not contain data (does not
     *  exist or is a folder).
     */
    public long size(String name) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Name: " + name);
        }
        if (!isReady()) {
            return 0;
        }
        try {
            RemoteFile f = getRemoteFile(name);
            if (f != null) {
                return f.getSize();
            } else {
                log.warning("File " + name + " not found!");
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        return 0;
    }

    /** Get input stream.
     *
     * @param name the file to test
     * @return an input stream to read the contents of this file
     * @exception FileNotFoundException if the file does not exists or is invalid
     */
    public InputStream inputStream(String name) throws java.io.FileNotFoundException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Name: " + name);
        }
        InputStream is = null;
        try {
            isReadyToRead();
            RemoteFile f = getRemoteFile(name);
            if (f != null) {
                is = f.getInputStream();
            } else {
                log.warning("File " + name + " not found!");
            }
        } catch (IOException e) {
            throw new FileNotFoundException(e.toString() + " NAME: " + name);
        }
        return is;
    }

    /** Get output stream.
     *
     * @param name the file to test
     * @return output stream to overwrite the contents of this file
     * @exception IOException if an error occures (the file is invalid, etc.)
     */
    public OutputStream outputStream(String name) throws java.io.IOException {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Name: " + name);
        }
        isReadyToModify();
        RemoteFile f = getRemoteFile(name);
        if (f != null) {
            return f.getOutputStream();
        } else {
            log.warning("File " + name + " not found!");
        }
        return null;
    }

    /** Does nothing to lock the file.
     * @param name name of the file
     * @throws IOException
     */
    public void lock(String name) throws IOException {
        log.fine("Locking file " + name);
    }

    /** Does nothing to unlock the file.
     *
     * @param name name of the file
     */
    public void unlock(String name) {
        log.fine("Unlocking file " + name);
    }

    /** Does nothing to mark the file as unimportant.
     *
     * @param name the file to mark
     */
    public void markUnimportant(String name) {
    }

    /** Informs user that startdir was not found on server.
     * @param startdir 
     * @param server  */
    protected abstract void startdirNotFound(String startdir, String server);

    /** Informs user that some error occurs during connecting.
     * @param error  */
    protected abstract void errorConnect(String error);

    /** Run in Request Processor.
     * @param run  */
    public void post(Runnable run) {
        if (requestProc == null) {
            requestProc = new RequestProcessor("Remote Filesystem Request Processor for " + logInfo.getDisplayName());
        }
        requestProc.post(run);
    }
} 
