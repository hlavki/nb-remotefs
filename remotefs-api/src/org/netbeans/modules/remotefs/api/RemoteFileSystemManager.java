/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.util.RequestProcessor;

/**
 *
 * @author hlavki
 */
public final class RemoteFileSystemManager implements FileChangeListener {

    private static final Logger log = Logger.getLogger(RemoteFileSystemManager.class.getName());
    private static final String POSITION_ATTR = "position";
    private static final String REMOTE_FS_FOLDER = "org-netbeans-modules-remotefs/remote-file-system/";  // NOI18N

    // Extensions of files
    private static final String INSTANCE_EXT = ".instance";
    private static RemoteFileSystemManager instance;
    private List<RemoteFileSystemInfo> fileSystems;

    private RemoteFileSystemManager() {
        doInit();
    }

    public static synchronized RemoteFileSystemManager getDefault() {
        if (instance == null) {
            instance = new RemoteFileSystemManager();
        }
        return instance;
    }

    public List<RemoteFileSystemInfo> getRemoteFileSystems() {
        return fileSystems;
    }

    // Private methods ---------------------------------------------------------
    private void initFileSystems() {
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject folder = fs.getRoot().getFileObject(REMOTE_FS_FOLDER);
        List<RemoteFsEntry> fsEntries = readFileSystems(folder);
        fileSystems = getListOfFileSystems(fsEntries);
    }

    private synchronized void doInit() {
        initFileSystems();
    }

    private static RemoteFileSystemInfo instantiateFsInfo(FileObject fileObject) {
        try {
            DataObject dobj = DataObject.find(fileObject);
            InstanceCookie ic = dobj.getCookie(InstanceCookie.class);
            Object fsInstance = ic.instanceCreate();

            if (fsInstance instanceof RemoteFileSystemInfo) {
                return (RemoteFileSystemInfo) fsInstance;
            } else {
                return null;
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, null, e);
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, null, e);
        }

        return null;
    }

    /** Read rules from system filesystem */
    private List<RemoteFsEntry> readFileSystems(FileObject folder) {
        List<RemoteFsEntry> fs = new LinkedList<RemoteFsEntry>();

        if (folder == null) {
            return fs;
        }

        Queue<FileObject> q = new LinkedList<FileObject>();

        q.offer(folder);

        while (!q.isEmpty()) {
            FileObject o = q.poll();

            o.removeFileChangeListener(this);
            o.addFileChangeListener(this);

            if (o.isFolder()) {
                q.addAll(Arrays.asList(o.getChildren()));
                continue;
            }

            if (!o.isData()) {
                continue;
            }

            String name = o.getNameExt().toLowerCase();
            Integer position = (Integer) o.getAttribute(POSITION_ATTR);

            if (o.canRead()) {
                RemoteFileSystemInfo fsInfo = null;
                if (name.endsWith(INSTANCE_EXT)) {
                    fsInfo = instantiateFsInfo(o);
                }
                if (fsInfo != null) {
                    fs.add(new RemoteFsEntry(fsInfo, position));
                }
            }
        }
        Collections.sort(fs);
        return fs;
    }

    private List<RemoteFileSystemInfo> getListOfFileSystems(List<RemoteFsEntry> fileSystemInfos) {
        List<RemoteFileSystemInfo> result = new ArrayList<RemoteFileSystemInfo>();
        for (RemoteFsEntry entry : fileSystemInfos) {
            result.add(entry.getFileSystem());
        }
        return result;
    }

    public void fileFolderCreated(FileEvent fe) {
        fileSystemChanged();
    }

    public void fileDataCreated(FileEvent fe) {
        fileSystemChanged();
    }

    public void fileChanged(FileEvent fe) {
        fileSystemChanged();
    }

    public void fileDeleted(FileEvent fe) {
        fileSystemChanged();
    }

    public void fileRenamed(FileRenameEvent fe) {
        fileSystemChanged();
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
        fileSystemChanged();
    }

    private void fileSystemChanged() {
        refreshFs.cancel();
        refreshFs.schedule(50);
    }
    private final RequestProcessor.Task refreshFs =
            new RequestProcessor(RemoteFileSystemManager.class.getName()).create(new Runnable() {

        public void run() {
            doInit();
        }
    });

    private static class RemoteFsEntry implements Comparable<RemoteFsEntry> {

        private final RemoteFileSystemInfo fileSystem;
        private final Integer position;

        public RemoteFsEntry(RemoteFileSystemInfo fileSystem, Integer position) {
            this.fileSystem = fileSystem;
            this.position = position;
        }

        public RemoteFileSystemInfo getFileSystem() {
            return fileSystem;
        }

        public Integer getPosition() {
            return position;
        }

        public int compareTo(RemoteFsEntry o) {
            return position.compareTo(o.getPosition());
        }
    }
}
