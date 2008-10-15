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
package org.netbeans.modules.remotefs.ftp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/** Global FTPFileSystem settings
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPSettings {

    static final long serialVersionUID = 6880742148617337695L;
    /** Default property values */
    private static final int DEFAULT_REFRESH_TIME = 60000;
    private static final boolean DEFAULT_ASK_WHICH_FILE = true;
    private static final boolean DEFAULT_REFRESH_SERVER = true;
    private static final boolean DEFAULT_SCAN_CACHE = true;
    private static final boolean DEFAULT_ALWAYS_REFRESH = false;
    private static final boolean DEFAULT_DOWNLOAD_SERVER_CHANGED_FILE = true;
    private static final boolean DEFAULT_OFFLINE_CHANGES = true;
    private static final boolean DEFAULT_ASK_SERVER_CHANGED_FILE = true;
    private static final boolean DEFAULT_ASK_CACHE_EXTERNAL_DELETE = true;
    private static final boolean DEFAULT_CACHE_EXTERNAL_DELETE = false;
    private static final boolean DEFAULT_ASK_SERVER_EXTERNAL_DELETE = true;
    private static final boolean DEFAULT_PASSIVE_MODE = false;
    private static final boolean DEFAULT_SERVER_EXTERNAL_DELETE = false;
    /** Property names */
    public static final String PROP_REFRESH_TIME = "refreshTime";
    public static final String PROP_REFRESH_SERVER = "refreshServer";
    public static final String PROP_SCAN_CACHE = "scanCache";
    public static final String PROP_OFFLINE_CHANGES = "offlineChanges";
    public static final String PROP_DOWNLOAD_SERVER_CHANGED_FILE = "downloadServerChangedFile";
    public static final String PROP_ALWAYS_REFRESH = "alwaysRefresh";
    public static final String PROP_ASK_SERVER_CHANGED_FILE = "askServerChangedFile";
    public static final String PROP_ASK_WHICH_FILE = "askWhichFile";
    public static final String PROP_PASSIVE_MODE = "passiveMode";
    public static final String PROP_ASK_CACHE_EXTERNAL_DELETE = "askCacheExternalDelete";
    public static final String PROP_CACHE_EXTERNAL_DELETE = "cacheExternalDelete";
    public static final String PROP_ASK_SERVER_EXTERNAL_DELETE = "askServerExternalDelete";
    public static final String PROP_SERVER_EXTERNAL_DELETE = "serverExternalDelete";
    /** Properties */
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private static FTPSettings instance;

    /** Creates new FTPSettings */
    private FTPSettings() {
    }

    public synchronized static FTPSettings getDefault() {
        if (instance == null) {
            instance = new FTPSettings();
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
        setIntProperty(PROP_REFRESH_TIME, refreshTime, DEFAULT_REFRESH_TIME);
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
        setBooleanProperty(PROP_ASK_WHICH_FILE, askWhichFile, DEFAULT_ASK_WHICH_FILE);
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
        setBooleanProperty(PROP_REFRESH_SERVER, refreshServer, DEFAULT_REFRESH_SERVER);
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
        setBooleanProperty(PROP_SCAN_CACHE, scanCache, DEFAULT_SCAN_CACHE);
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
        setBooleanProperty(PROP_ALWAYS_REFRESH, alwaysRefresh, DEFAULT_ALWAYS_REFRESH);
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
        setBooleanProperty(PROP_DOWNLOAD_SERVER_CHANGED_FILE, downloadServerChangedFile,
                DEFAULT_DOWNLOAD_SERVER_CHANGED_FILE);
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
        setBooleanProperty(PROP_OFFLINE_CHANGES, offlineChanges, DEFAULT_OFFLINE_CHANGES);
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
        setBooleanProperty(PROP_ASK_SERVER_CHANGED_FILE, askServerChangedFile, DEFAULT_ASK_SERVER_CHANGED_FILE);
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
        setBooleanProperty(PROP_ASK_CACHE_EXTERNAL_DELETE, askCacheExternalDelete, DEFAULT_ASK_CACHE_EXTERNAL_DELETE);
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
        setBooleanProperty(PROP_CACHE_EXTERNAL_DELETE, cacheExternalDelete, DEFAULT_CACHE_EXTERNAL_DELETE);
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
        setBooleanProperty(PROP_ASK_SERVER_EXTERNAL_DELETE, askServerExternalDelete, DEFAULT_ASK_SERVER_EXTERNAL_DELETE);
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
     * Set the value of passiveMode
     *
     * @param passiveMode new value of passiveMode
     */
    public void setPassiveMode(boolean passiveMode) {
        setBooleanProperty(PROP_PASSIVE_MODE, passiveMode, DEFAULT_PASSIVE_MODE);
    }

    /**
     * Get the value of passiveMode
     *
     * @return the value of passiveMode
     */
    public boolean isPassiveMode() {
        return getPreferences().getBoolean(PROP_PASSIVE_MODE, DEFAULT_PASSIVE_MODE);
    }

    /**
     * Set the value of serverExternalDelete
     *
     * @param serverExternalDelete new value of serverExternalDelete
     */
    public void setServerExternalDelete(boolean serverExternalDelete) {
        setBooleanProperty(PROP_SERVER_EXTERNAL_DELETE, serverExternalDelete, DEFAULT_SERVER_EXTERNAL_DELETE);
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

    private void setBooleanProperty(String property, boolean value, boolean defaultValue) {
        boolean oldValue = getPreferences().getBoolean(property, defaultValue);
        getPreferences().putBoolean(PROP_SERVER_EXTERNAL_DELETE, value);
        propertyChangeSupport.firePropertyChange(property, oldValue, value);
    }

    private void setIntProperty(String property, int value, int defaultValue) {
        int oldValue = getPreferences().getInt(property, defaultValue);
        getPreferences().putInt(PROP_SERVER_EXTERNAL_DELETE, value);
        propertyChangeSupport.firePropertyChange(property, oldValue, value);
    }

    private static Preferences getPreferences() {
        return NbPreferences.forModule(FTPSettings.class);
    }
}