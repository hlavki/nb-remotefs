/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp.client;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
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

    public void connect() throws IOException {
        try {
            session = jsch.getSession(logInfo.getUser(), logInfo.getHost(), logInfo.getPort());
            session.setUserInfo(logInfo);
            channel = (ChannelSftp) session.openChannel("sftp");
            session.connect();
        } catch (JSchException e) {
            throw new SFTPException(e);
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
    public void get(RemoteFileName what, File where) throws IOException {
        OutputStream fileOut = null;
        InputStream dataIn = null;
        try {
            fileOut = new FileOutputStream(where);
            dataIn = channel.get(what.getFullName(), new SFTPProgressHandle());
            byte[] buffer = new byte[BUFFER];
            int len;
            while ((len = dataIn.read(buffer)) != -1) {
                fileOut.write(buffer, 0, len);
            }
        } catch (SftpException e) {
            throw new SFTPException(e);
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
    }

    /**
     * {@inheritDoc}
     */
    public void put(File what, RemoteFileName where) throws IOException {
        InputStream inData = null;
        OutputStream fileOut = null;
        try {
            inData = new FileInputStream(what);
            fileOut = channel.put(where.getFullName(), new SFTPProgressHandle(), ChannelSftp.OVERWRITE);
            byte[] buffer = new byte[BUFFER];
            int len;
            while ((len = inData.read(buffer)) != -1) {
                fileOut.write(buffer, 0, len);
            }
        } catch (SftpException e) {
            throw new SFTPException(e);
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
    public RemoteFileAttributes[] list(RemoteFileName directory) throws IOException {
        try {
            Vector entries = channel.ls(directory.getFullName());
            RemoteFileAttributes[] result = new RemoteFileAttributes[entries.size()];
            for (int idx = 0; idx < entries.size(); idx++) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) entries.get(idx);
                // FIXME: I don't know how to recognize folder
                result[idx] = new RemoteFileAttributes(new SFTPFileName(entry.getFilename(), directory.getFullName()), false);
            }
            return result;
        } catch (SftpException e) {
            throw new SFTPException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rename(RemoteFileName src, String newName) throws IOException {
        SFTPFileName dst = new SFTPFileName(((SFTPFileName) src).getDirectory(), newName);
        try {
            channel.rename(src.getFullName(), dst.getFullName());
        } catch (SftpException e) {
            throw new SFTPException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void delete(RemoteFileName name) throws IOException {
        try {
            channel.rm(name.getFullName());
        } catch (SftpException e) {
            throw new SFTPException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void mkdir(RemoteFileName name) throws IOException {
        try {
            channel.mkdir(name.getFullName());
        } catch (SftpException e) {
            throw new SFTPException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void rmdir(RemoteFileName name) throws IOException {
        try {
            channel.rmdir(name.getFullName());
        } catch (SftpException e) {
            throw new SFTPException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect() {
        channel.disconnect();
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        channel.exit();
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
