/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp.client;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.remotefs.api.LogInfo;
import org.netbeans.modules.remotefs.api.RemoteClient;
import org.netbeans.modules.remotefs.api.RemoteFileAttributes;
import org.netbeans.modules.remotefs.api.RemoteFileName;
import org.openide.util.Exceptions;

/**
 *
 * @author hlavki
 */
public class SFTPClient implements RemoteClient {

    private static final Logger log = Logger.getLogger(SFTPClient.class.getName());
    /** Default FTP port number */
    public final static int DEFAULT_PORT = 22;
    private SFTPLogInfo logInfo;
    private Reconnect reconnect;
    private static final JSch jsch = new JSch();
    private Session session;
    private ChannelSftp channel;
    private static final int BUFFER = 1024;

    public SFTPClient(SFTPLogInfo logInfo) {
        this.logInfo = logInfo;
    }

    public RemoteFileName getRoot() {
        return SFTPFileName.getRoot();
    }

    public synchronized void connect() throws IOException {
        try {
            session = jsch.getSession(logInfo.getUser(), logInfo.getHost(), logInfo.getPort());
            session.setUserInfo(logInfo);
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            throw new SFTPException(e.getMessage());
        }
    }

    public boolean isConnected() {
        return session.isConnected();
    }

    /** Compare this login information.
     * @return 0 if login informations are equal;
     *         1 if login informations refer to the same resource but can't be uses to login;
     *        -1 if login informations are different
     * @param loginfo
     */
    public int compare(LogInfo loginfo) {
        return logInfo.compareTo(loginfo);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void get(RemoteFileName what, File where) throws IOException {
        OutputStream fileOut = null;
        InputStream dataIn = null;
        log.fine("Recieving data from " + what.getFullName() + " to " + where.getAbsolutePath());
        try {
            fileOut = new FileOutputStream(where);
            dataIn = channel.get(what.getFullName(), new SFTPProgressHandle());
            byte[] buffer = new byte[BUFFER];
            int len;
            while ((len = dataIn.read(buffer)) != -1) {
                fileOut.write(buffer, 0, len);
            }
        } catch (SftpException e) {
            throw new SFTPException(e.getMessage());
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    Exceptions.printStackTrace(e);
                }
            }
            if (dataIn != null) {
                try {
                    dataIn.close();
                } catch (IOException e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }
        log.fine("Stop recieving data...");
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void put(File what, RemoteFileName where) throws IOException {
        InputStream inData = null;
        OutputStream fileOut = null;
        log.fine("Sending data from " + what.getAbsolutePath() + " to " + where.getFullName());
        try {
            inData = new FileInputStream(what);
            fileOut = channel.put(where.getFullName(), (SFTPProgressHandle) null, ChannelSftp.OVERWRITE);
            byte[] buffer = new byte[BUFFER];
            int len;
            while ((len = inData.read(buffer)) != -1) {
                fileOut.write(buffer, 0, len);
            }
        } catch (SftpException e) {
            throw new SFTPException(e.getMessage());
        } finally {
            if (inData != null) {
                try {
                    inData.close();
                } catch (IOException e) {
                    Exceptions.printStackTrace(e);
                }
            }
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized RemoteFileAttributes[] list(RemoteFileName directory) throws IOException {
        String pwd = null;
        Map<String, RemoteFileAttributes> dirList = new HashMap<String, RemoteFileAttributes>();
        try {
            log.fine("Listing directory " + directory.getFullName());
            Vector entries = channel.ls(directory.getFullName());
            for (int idx = 0; idx < entries.size(); idx++) {
                boolean isDirectory = false;
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) entries.get(idx);
                SftpATTRS attrs = lsEntry.getAttrs();
                String fileName = lsEntry.getFilename();
                if (fileName.length() > 1 && fileName.startsWith(".")) {
                    // hide hidden files
                    continue;
                }
                Date mtime = new Date(attrs.getMTime() * (long) 1000);
                RemoteFileAttributes fileEntry;
                if (attrs.isDir()) {
                    isDirectory = true;
                    if (fileName.equals(".") || fileName.equals("..")) {
                        continue;
                    }
                    fileEntry = new RemoteFileAttributes(new SFTPFileName(directory.getFullName(), fileName),
                            true, attrs.getSize(), mtime);
                } else {
                    if (attrs.isLink()) {
                        try {
                            if (pwd == null && !directory.getFullName().equals(".")) {
                                SftpATTRS newAttrs = channel.stat(directory.getFullName());
                                if (newAttrs.isDir()) {
                                    pwd = channel.pwd();
                                    try {
                                        channel.cd(directory.getFullName());
                                    } catch (SftpException e) {
                                        pwd = null;
                                    }
                                }
                            }
                            SftpATTRS newAttrs = channel.stat(fileName);
                            isDirectory = newAttrs.isDir();
                            attrs = newAttrs;
                            mtime = new Date(attrs.getMTime() * (long) 1000);
                        } catch (SftpException ee) {
                            continue;
                        }
                    }
                    RemoteFileName remoteFile = new SFTPFileName(directory.getFullName(), fileName);
                    fileEntry = new RemoteFileAttributes(remoteFile, isDirectory, attrs.getSize(), mtime);
                }
                if (!dirList.containsKey(fileName)) {
                    log.finer("Listed file: " + fileEntry);
                    dirList.put(fileName, fileEntry);
                }
            }
        } catch (SftpException e) {
            throw new SFTPException(e.getMessage());
        } finally {
            try {
                if (pwd != null) {
                    channel.cd(pwd);
                }
            } catch (SftpException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return dirList.values().toArray(new RemoteFileAttributes[0]);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void rename(RemoteFileName src, String newName) throws IOException {
        SFTPFileName dst = new SFTPFileName(((SFTPFileName) src).getDirectory(), newName);
        log.fine("Renaming " + src.getFullName() + " to " + dst.getFullName());
        try {
            channel.rename(src.getFullName(), dst.getFullName());
        } catch (SftpException e) {
            throw new SFTPException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void delete(RemoteFileName name) throws IOException {
        try {
            log.fine("Deleting " + name.getFullName());
            channel.rm(name.getFullName());
        } catch (SftpException e) {
            throw new SFTPException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void mkdir(RemoteFileName name) throws IOException {
        try {
            String fullPath = name.getFullName();
            log.fine("Creating directory " + fullPath);
            channel.mkdir(fullPath);
        } catch (SftpException e) {
            throw new SFTPException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rmdir(RemoteFileName name) throws IOException {
        try {
            log.fine("Removing directory " + name.getFullName());
            channel.rmdir(name.getFullName());
        } catch (SftpException e) {
            throw new SFTPException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void disconnect() {
        log.fine("Disconnecting channel!");
        session.disconnect();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void close() {
        log.fine("Closing channel!");
        if (channel != null) {
            session.disconnect();
        }
    }

    public void setReconnect(Reconnect reconnect) {
        this.reconnect = reconnect;
    }

    //***************************************************************************
    /** Interface for notify of reconnection. */
    public interface Reconnect {

        /**
         * @param mess message with reason of closed connection
         * @return whether connection should be restored
         */
        public boolean notifyReconnect(String message);
    }
}
