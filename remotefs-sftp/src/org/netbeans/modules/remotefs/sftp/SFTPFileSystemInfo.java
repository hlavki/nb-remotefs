/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp;

import java.awt.Image;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.remotefs.api.RemoteFileSystem;
import org.netbeans.modules.remotefs.api.RemoteFileSystemInfo;
import org.netbeans.modules.remotefs.sftp.client.SFTPLogInfo;
import org.netbeans.modules.remotefs.sftp.resources.Bundle;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author hlavki
 */
public class SFTPFileSystemInfo implements RemoteFileSystemInfo {

    private static final String PROP_DISPLAY_NAME = "FileSystemInfo.displayName";
    private static final String ICON_PATH = "org/netbeans/modules/remotefs/sftp/resources/globe-sextant-16x16.png";

    public String getDisplayName() {
        return NbBundle.getMessage(Bundle.class, PROP_DISPLAY_NAME);
    }

    public Image getIcon() {
        return Utilities.loadImage(ICON_PATH);
    }

    public List<RemoteFileSystem> getConnections() {
        SFTPLogInfo logInfo = new SFTPLogInfo("localhost", "astar");
        return Collections.<RemoteFileSystem>singletonList(new SFTPFileSystem(logInfo));
    }

    public HelpCtx getHelp() {
        return null;
    }

    public void createConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
