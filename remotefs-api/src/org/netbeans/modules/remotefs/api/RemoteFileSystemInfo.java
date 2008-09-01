/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.api;

import java.awt.Image;
import java.util.List;
import org.openide.util.HelpCtx;

/**
 *
 * @author hlavki
 */
public interface RemoteFileSystemInfo {

    String getDisplayName();

    Image getIcon();

    List<RemoteFileSystem> getConnections();

    HelpCtx getHelp();

    void createConnection();
}
