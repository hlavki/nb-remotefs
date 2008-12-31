/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.api.config;

import java.io.File;
import java.io.IOException;
import org.netbeans.modules.remotefs.api.LogInfo;
import org.netbeans.modules.remotefs.api.RemoteFile;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * implementation of CacheDirectoryProvider that places the cache directory in the user
 * directory space of the currently running IDE.
 * @author mkleint
 */
public class RemoteFsCacheProvider implements CacheProvider {

    private LogInfo logInfo;

    public RemoteFsCacheProvider(LogInfo logInfo) {
        this.logInfo = logInfo;
    }

    public FileObject getCacheDirectory() throws IOException {
        int code = logInfo.getProperties().hashCode();
        File cacheDir = new File(getCacheRoot(), "" + code);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(cacheDir));
        if (fo != null) {
            return fo;
        }
        throw new IOException("Cannot create a cache directory for project at " + cacheDir); //NOI18N
    }

    private File getCacheRoot() {
        String userdir = System.getProperty("netbeans.user"); //NOI18N
        File file = new File(userdir);
        File root = new File(file, "var" + File.separator + "cache" + File.separator + "remotefs" +
                File.separatorChar + logInfo.getProtocol() + "cache"); //NOI18N
        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }
}

