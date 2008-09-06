/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.ftp;

import java.awt.Image;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.netbeans.modules.remotefs.api.LogInfo;
import org.netbeans.modules.remotefs.api.RemoteFileSystem;
import org.netbeans.modules.remotefs.api.RemoteFileSystemInfo;
import org.netbeans.modules.remotefs.ftp.client.FTPLogInfo;
import org.netbeans.modules.remotefs.ftp.resources.Bundle;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author hlavki
 */
public class FTPFileSystemInfo implements RemoteFileSystemInfo {

    private static final String PROP_DISPLAY_NAME = "FileSystemInfo.displayName";
    private static final String ICON_PATH = "org/netbeans/modules/remotefs/ftp/resources/globe-sextant-16x16.png";
    private static final Set<String> SUPPORTED_PROTOCOLS = new HashSet<String>(Arrays.asList(new String[]{"ftp"}));

    public String getDisplayName() {
        return NbBundle.getMessage(Bundle.class, PROP_DISPLAY_NAME);
    }

    public Image getIcon() {
        return Utilities.loadImage(ICON_PATH);
    }

    public List<RemoteFileSystem> getConnections() {
        FTPLogInfo logInfo = new FTPLogInfo("localhost", 2121, "admin", "admin");
        return Collections.<RemoteFileSystem>singletonList(new FTPFileSystem(logInfo));
    }

    public HelpCtx getHelp() {
        return null;
    }

    public void createConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<String> getSupportedProtocols() {
        return SUPPORTED_PROTOCOLS;
    }

    public LogInfo createLogInfo(Properties data) {
        return new FTPLogInfo(data);
    }

    public RemoteFileSystem createFileSystem(LogInfo logInfo) {
        return new FTPFileSystem((FTPLogInfo) logInfo);
    }
}
