/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.api;

import org.netbeans.modules.remotefs.api.config.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.remotefs.api.LogInfo;
import org.netbeans.modules.remotefs.api.RemoteFileSystem;
import org.netbeans.modules.remotefs.api.RemoteFileSystemInfo;
import org.netbeans.modules.remotefs.api.events.RemoteFSEventListener;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author hlavki
 */
public class LogInfoList {

    private static LogInfoList instance;
    private Lookup.Result<LogInfo> result = getLookupResult();
    private final List<RemoteFSEventListener> listeners = new ArrayList<RemoteFSEventListener>(1);

    private LogInfoList() {
        // issue 75204: forces the DataObject's corresponding to the DatabaseConnection's
        // to be initialized and held strongly so the same DatabaseConnection is
        // returns as long as it is held strongly
        result.allInstances();

        result.addLookupListener(new LookupListener() {

            public void resultChanged(LookupEvent e) {
                fireListeners();
            }
        });
    }

    public static synchronized LogInfoList getDefault() {
        if (instance == null) {
            instance = new LogInfoList();
        }
        return instance;
    }

    public LogInfo[] geLogInfosArray() {
        Collection<? extends LogInfo> dicts = result.allInstances();
        return dicts.toArray(new LogInfo[dicts.size()]);
    }

    public Collection<? extends LogInfo> getLogInfos() {
        return result.allInstances();
    }

    public List<RemoteFileSystem> getLogInfosByProtocols(RemoteFileSystemInfo fsInfo) {
        List<RemoteFileSystem> logInfos = new ArrayList<RemoteFileSystem>();
        Set<String> protocols = fsInfo.getSupportedProtocols();
        for (LogInfo logInfo : getLogInfos()) {
            if (protocols.contains(logInfo.getProtocol())) {
                logInfos.add(logInfo.createFileSystem());
            }
        }
        return logInfos;
    }

    public LogInfo getLogInfo(LogInfo impl) {
        if (impl == null) {
            throw new NullPointerException();
        }
        Collection<? extends LogInfo> conns = getLogInfos();
        for (LogInfo conn : conns) {
            if (impl.equals(conn)) {
                return conn;
            }
        }
        return null;
    }

    public void add(LogInfo conn) throws IOException {
        if (conn == null) {
            throw new NullPointerException();
        }
        ConnectionPersistenceManager.create(conn);
    }

    public boolean contains(LogInfo connection) {
        return getLogInfo(connection) != null;
    }

    public void remove(LogInfo connection) throws IOException {
        if (connection == null) {
            throw new NullPointerException();
        }
        ConnectionPersistenceManager.remove(connection);
    }

    public void addRemoteFSEventListener(RemoteFSEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeRemoteFSEventListener(RemoteFSEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void fireListeners() {
        List<RemoteFSEventListener> listenersCopy;

        synchronized (listeners) {
            listenersCopy = new ArrayList<RemoteFSEventListener>(listeners);
        }
        for (RemoteFSEventListener listener : listenersCopy) {
            listener.remoteFSChanged();
        }
    }

    private synchronized Lookup.Result<LogInfo> getLookupResult() {
        return Lookups.forPath(ConnectionPersistenceManager.REMOTE_FS_CONNECTIONS_PATH).lookupResult(LogInfo.class);
    }
}

