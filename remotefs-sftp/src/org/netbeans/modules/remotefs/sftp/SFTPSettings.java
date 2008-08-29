/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author hlavki
 */
public class SFTPSettings {

    /** Default property values */
    private static final int DEFAULT_REFRESH_TIME = 60000;
    private static final boolean DEFAULT_ASK_WHICH_FILE = true;
    private static final boolean DEFAULT_REFRESH_SERVER = true;
    private static final boolean DEFAULT_SCAN_CACHE = true;
    private static final boolean DEFAULT_ALWAYS_REFRESH = true;
    private static final boolean DEFAULT_DOWNLOAD_SERVER_CHANGED_FILE = true;
    private static final boolean DEFAULT_OFFLINE_CHANGES = true;
    private static final boolean DEFAULT_ASK_SERVER_CHANGED_FILE = true;
    private static final boolean DEFAULT_ASK_CACHE_EXTERNAL_DELETE = true;
    private static final boolean DEFAULT_CACHE_EXTERNAL_DELETE = true;
    private static final boolean DEFAULT_ASK_SERVER_EXTERNAL_DELETE = true;
    private static final boolean DEFAULT_SERVER_EXTERNAL_DELETE = true;
    /** Property names */
    public static final String PROP_REFRESH_TIME = "refreshTime";
    public static final String PROP_ASK_WHICH_FILE = "askWhichFile";
    public static final String PROP_REFRESH_SERVER = "refreshServer";
    public static final String PROP_SCAN_CACHE = "scanCache";
    public static final String PROP_ALWAYS_REFRESH = "alwaysRefresh";
    public static final String PROP_DOWNLOAD_SERVER_CHANGED_FILE = "downloadServerChangedFile";
    public static final String PROP_OFFLINE_CHANGES = "offlineChanges";
    public static final String PROP_ASK_SERVER_CHANGED_FILE = "askServerChangedFile";
    public static final String PROP_ASK_CACHE_EXTERNAL_DELETE = "askCacheExternalDelete";
    public static final String PROP_CACHE_EXTERNAL_DELETE = "cacheExternalDelete";
    public static final String PROP_ASK_SERVER_EXTERNAL_DELETE = "askServerExternalDelete";
    public static final String PROP_SERVER_EXTERNAL_DELETE = "serverExternalDelete";
    /** Properties */
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private static SFTPSettings instance;

    private SFTPSettings() {
    }

    public synchronized static SFTPSettings getDefault() {
        if (instance == null) {
            instance = new SFTPSettings();
        }
        return instance;
    }

    /**
     * Get the value of refreshTime
     *
     * @return the value of refreshTime
     */
    public int getRefreshTime() {
        return getPreferences().getInt(PROP_REFRESH_TIME, DEFAULT_REFRESH_TIME);
    }

    /**
     * Set the value of refreshTime
     *
     * @param refreshTime new value of refreshTime
     */
    public void setRefreshTime(int refreshTime) {
        int oldRefreshTime = getPreferences().getInt(PROP_REFRESH_TIME, DEFAULT_REFRESH_TIME);
        getPreferences().putLong(PROP_REFRESH_TIME, refreshTime);
        propertyChangeSupport.firePropertyChange(PROP_REFRESH_TIME, oldRefreshTime, refreshTime);
    }

    /**
     * Get the value of askWhichFile
     *
     * @return the value of askWhichFile
     */
    public boolean isAskWhichFile() {
        return getPreferences().getBoolean(PROP_ASK_WHICH_FILE, DEFAULT_ASK_WHICH_FILE);
    }

    /**
     * Set the value of askWhichFile
     *
     * @param askWhichFile new value of askWhichFile
     */
    public void setAskWhichFile(boolean askWhichFile) {
        boolean oldAskWhichFile = getPreferences().getBoolean(PROP_ASK_WHICH_FILE, DEFAULT_ASK_WHICH_FILE);
        getPreferences().putBoolean(PROP_ASK_WHICH_FILE, askWhichFile);
        propertyChangeSupport.firePropertyChange(PROP_ASK_WHICH_FILE, oldAskWhichFile, askWhichFile);
    }

    /**
     * Get the value of refreshServer
     *
     * @return the value of refreshServer
     */
    public boolean isRefreshServer() {
        return getPreferences().getBoolean(PROP_REFRESH_SERVER, DEFAULT_REFRESH_SERVER);
    }

    /**
     * Set the value of refreshServer
     *
     * @param refreshServer new value of refreshServer
     */
    public void setRefreshServer(boolean refreshServer) {
        boolean oldRefreshServer = getPreferences().getBoolean(PROP_REFRESH_SERVER, DEFAULT_REFRESH_SERVER);
        getPreferences().putBoolean(PROP_REFRESH_SERVER, refreshServer);
        propertyChangeSupport.firePropertyChange(PROP_REFRESH_SERVER, oldRefreshServer, refreshServer);
    }

    /**
     * Get the value of scanCache
     *
     * @return the value of scanCache
     */
    public boolean isScanCache() {
        return getPreferences().getBoolean(PROP_SCAN_CACHE, DEFAULT_SCAN_CACHE);
    }

    /**
     * Set the value of scanCache
     *
     * @param scanCache new value of scanCache
     */
    public void setScanCache(boolean scanCache) {
        boolean oldScanCache = getPreferences().getBoolean(PROP_SCAN_CACHE, DEFAULT_SCAN_CACHE);
        getPreferences().putBoolean(PROP_SCAN_CACHE, scanCache);
        propertyChangeSupport.firePropertyChange(PROP_SCAN_CACHE, oldScanCache, scanCache);
    }

    /**
     * Get the value of alwaysRefresh
     *
     * @return the value of alwaysRefresh
     */
    public boolean isAlwaysRefresh() {
        return getPreferences().getBoolean(PROP_ALWAYS_REFRESH, DEFAULT_ALWAYS_REFRESH);
    }

    /**
     * Set the value of alwaysRefresh
     *
     * @param alwaysRefresh new value of alwaysRefresh
     */
    public void setAlwaysRefresh(boolean alwaysRefresh) {
        boolean oldAlwaysRefresh = getPreferences().getBoolean(PROP_ALWAYS_REFRESH, DEFAULT_ALWAYS_REFRESH);
        getPreferences().putBoolean(PROP_ALWAYS_REFRESH, alwaysRefresh);
        propertyChangeSupport.firePropertyChange(PROP_ALWAYS_REFRESH, oldAlwaysRefresh, alwaysRefresh);
    }

    /**
     * Get the value of downloadServerChangedFile
     *
     * @return the value of downloadServerChangedFile
     */
    public boolean isDownloadServerChangedFile() {
        return getPreferences().getBoolean(PROP_DOWNLOAD_SERVER_CHANGED_FILE, DEFAULT_DOWNLOAD_SERVER_CHANGED_FILE);
    }

    /**
     * Set the value of downloadServerChangedFile
     *
     * @param downloadServerChangedFile new value of downloadServerChangedFile
     */
    public void setDownloadServerChangedFile(boolean downloadServerChangedFile) {
        boolean oldDownloadServerChangedFile = getPreferences().getBoolean(PROP_DOWNLOAD_SERVER_CHANGED_FILE,
                DEFAULT_DOWNLOAD_SERVER_CHANGED_FILE);
        getPreferences().putBoolean(PROP_DOWNLOAD_SERVER_CHANGED_FILE, downloadServerChangedFile);
        propertyChangeSupport.firePropertyChange(PROP_DOWNLOAD_SERVER_CHANGED_FILE,
                oldDownloadServerChangedFile, downloadServerChangedFile);
    }

    /**
     * Get the value of offlineChanges
     *
     * @return the value of offlineChanges
     */
    public boolean isOfflineChanges() {
        return getPreferences().getBoolean(PROP_OFFLINE_CHANGES, DEFAULT_OFFLINE_CHANGES);
    }

    /**
     * Set the value of offlineChanges
     *
     * @param offlineChanges new value of offlineChanges
     */
    public void setOfflineChanges(boolean offlineChanges) {
        boolean oldOfflineChanges = getPreferences().getBoolean(PROP_OFFLINE_CHANGES, DEFAULT_OFFLINE_CHANGES);
        getPreferences().putBoolean(PROP_OFFLINE_CHANGES, offlineChanges);
        propertyChangeSupport.firePropertyChange(PROP_OFFLINE_CHANGES, oldOfflineChanges, offlineChanges);
    }

    /**
     * Get the value of askServerChangedFile
     *
     * @return the value of askServerChangedFile
     */
    public boolean isAskServerChangedFile() {
        return getPreferences().getBoolean(PROP_ASK_SERVER_CHANGED_FILE, DEFAULT_ASK_SERVER_CHANGED_FILE);
    }

    /**
     * Set the value of askServerChangedFile
     *
     * @param askServerChangedFile new value of askServerChangedFile
     */
    public void setAskServerChangedFile(boolean askServerChangedFile) {
        boolean oldAskServerChangedFile = getPreferences().getBoolean(PROP_ASK_SERVER_CHANGED_FILE, DEFAULT_ASK_SERVER_CHANGED_FILE);
        getPreferences().putBoolean(PROP_ASK_SERVER_CHANGED_FILE, askServerChangedFile);
        propertyChangeSupport.firePropertyChange(PROP_ASK_SERVER_CHANGED_FILE, oldAskServerChangedFile, askServerChangedFile);
    }

    /**
     * Get the value of askCacheExternalDelete
     *
     * @return the value of askCacheExternalDelete
     */
    public boolean isAskCacheExternalDelete() {
        return getPreferences().getBoolean(PROP_ASK_CACHE_EXTERNAL_DELETE, DEFAULT_ASK_CACHE_EXTERNAL_DELETE);
    }

    /**
     * Set the value of askCacheExternalDelete
     *
     * @param askCacheExternalDelete new value of askCacheExternalDelete
     */
    public void setAskCacheExternalDelete(boolean askCacheExternalDelete) {
        boolean oldAskCacheExternalDelete = getPreferences().getBoolean(PROP_ASK_CACHE_EXTERNAL_DELETE, DEFAULT_ASK_CACHE_EXTERNAL_DELETE);
        getPreferences().putBoolean(PROP_ASK_CACHE_EXTERNAL_DELETE, askCacheExternalDelete);
        propertyChangeSupport.firePropertyChange(PROP_ASK_CACHE_EXTERNAL_DELETE, oldAskCacheExternalDelete, askCacheExternalDelete);
    }

    /**
     * Get the value of cacheExternalDelete
     *
     * @return the value of cacheExternalDelete
     */
    public boolean isCacheExternalDelete() {
        return getPreferences().getBoolean(PROP_CACHE_EXTERNAL_DELETE, DEFAULT_CACHE_EXTERNAL_DELETE);
    }

    /**
     * Set the value of cacheExternalDelete
     *
     * @param cacheExternalDelete new value of cacheExternalDelete
     */
    public void setCacheExternalDelete(boolean cacheExternalDelete) {
        boolean oldCacheExternalDelete = getPreferences().getBoolean(PROP_CACHE_EXTERNAL_DELETE, DEFAULT_CACHE_EXTERNAL_DELETE);
        getPreferences().putBoolean(PROP_CACHE_EXTERNAL_DELETE, cacheExternalDelete);
        propertyChangeSupport.firePropertyChange(PROP_CACHE_EXTERNAL_DELETE, oldCacheExternalDelete, cacheExternalDelete);
    }

    /**
     * Get the value of askServerExternalDelete
     *
     * @return the value of askServerExternalDelete
     */
    public boolean isAskServerExternalDelete() {
        return getPreferences().getBoolean(PROP_ASK_SERVER_EXTERNAL_DELETE, DEFAULT_ASK_SERVER_EXTERNAL_DELETE);
    }

    /**
     * Set the value of offlineChanges
     *
     * @param offlineChanges new value of offlineChanges
     */
    public void setAskServerExternalDelete(boolean askServerExternalDelete) {
        boolean oldAskServerExternalDelete = getPreferences().getBoolean(PROP_ASK_SERVER_EXTERNAL_DELETE, DEFAULT_ASK_SERVER_EXTERNAL_DELETE);
        getPreferences().putBoolean(PROP_ASK_SERVER_EXTERNAL_DELETE, askServerExternalDelete);
        propertyChangeSupport.firePropertyChange(PROP_ASK_SERVER_EXTERNAL_DELETE, oldAskServerExternalDelete, askServerExternalDelete);
    }

    /**
     * Get the value of serverExternalDelete
     *
     * @return the value of serverExternalDelete
     */
    public boolean isServerExternalDelete() {
        return getPreferences().getBoolean(PROP_SERVER_EXTERNAL_DELETE, DEFAULT_SERVER_EXTERNAL_DELETE);
    }

    /**
     * Set the value of serverExternalDelete
     *
     * @param serverExternalDelete new value of serverExternalDelete
     */
    public void setServerExternalDelete(boolean serverExternalDelete) {
        boolean oldServerExternalDelete = getPreferences().getBoolean(PROP_SERVER_EXTERNAL_DELETE, DEFAULT_SERVER_EXTERNAL_DELETE);
        getPreferences().putBoolean(PROP_SERVER_EXTERNAL_DELETE, serverExternalDelete);
        propertyChangeSupport.firePropertyChange(PROP_SERVER_EXTERNAL_DELETE, oldServerExternalDelete, serverExternalDelete);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private static Preferences getPreferences() {
        return NbPreferences.forModule(SFTPSettings.class);
    }
}
