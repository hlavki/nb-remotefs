/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.api;

import java.util.Date;

/**
 *
 * @author hlavki
 */
public interface RemoteFileNotify {

    public boolean isRefreshServer();

    public boolean isScanCache();

    public boolean isAlwaysRefresh();

    public void setAlwaysRefresh(boolean alwaysRefresh);

    public int notifyWhichFile(String path, Date file1, long size1, Date file2, long size2);

    public int notifyBothFilesChanged(String path, Date file1, long size1, Date file2, long size2);

    public boolean isDownloadServerChangedFile();

    public boolean notifyServerChanged(String path, Date file1, long size1, Date file2, long size2);

    public boolean notifyCacheExtDelete(String path, boolean isDir);

    public boolean notifyServerExtDelete(String path, boolean isDir);

    public void fileChanged(String path);

    public void notifyException(Exception e);
}
