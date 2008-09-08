/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp;

import java.awt.Image;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.netbeans.modules.remotefs.api.LogInfo;
import org.netbeans.modules.remotefs.api.RemoteFileSystem;
import org.netbeans.modules.remotefs.api.RemoteFileSystemInfo;
import org.netbeans.modules.remotefs.api.LogInfoList;
import org.netbeans.modules.remotefs.sftp.client.SFTPLogInfo;
import org.netbeans.modules.remotefs.sftp.resources.Bundle;
import org.netbeans.modules.remotefs.sftp.ui.NewSFTPSiteWizardAction;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author hlavki
 */
public class SFTPFileSystemInfo implements RemoteFileSystemInfo {

    private static final String PROP_DISPLAY_NAME = "FileSystemInfo.displayName";
    private static final String ICON_PATH = "org/netbeans/modules/remotefs/sftp/resources/globe-sextant-16x16.png";
    private static final Set<String> SUPPORTED_PROTOCOLS = new HashSet<String>(Arrays.asList(new String[]{"sftp"}));

    public String getDisplayName() {
        return NbBundle.getMessage(Bundle.class, PROP_DISPLAY_NAME);
    }

    public Image getIcon() {
        return Utilities.loadImage(ICON_PATH);
    }

    public List<RemoteFileSystem> getConnections() {
        SFTPLogInfo logInfo = new SFTPLogInfo("localhost", "astar");
        try {
            LogInfoList.getDefault().add(logInfo);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.<RemoteFileSystem>singletonList(new SFTPFileSystem(logInfo));
    }

    public HelpCtx getHelp() {
        return null;
    }

    public void createConnection() {
        CallableSystemAction action = NewSFTPSiteWizardAction.getInstance();
        action.performAction();
    }

    public Set<String> getSupportedProtocols() {
        return SUPPORTED_PROTOCOLS;
    }

    public LogInfo createLogInfo(Properties data) {
        return new SFTPLogInfo(data);
    }
}
