/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.ui;

import java.awt.Image;
import java.util.List;
import org.netbeans.modules.remotefs.api.RemoteFileSystemInfo;
import org.netbeans.modules.remotefs.ui.resources.Bundle;
import org.openide.nodes.AbstractNode;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author hlavki
 */
public class RootNode extends AbstractNode {

    private static final String PROPERTY_NAME = "CTL_RootNode.name";
    private static final String ICON_PATH = "org/netbeans/modules/remotefs/ui/resources/entire-network-16x16.png";

    /** Creates a new instance of RootNode */
    public RootNode(List<RemoteFileSystemInfo> fsInfos) {
        super(new RemoteFSNode.RemoteFSChildren(fsInfos));
    }

    @Override
    public Image getIcon(int type) {
        return Utilities.loadImage(ICON_PATH);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Bundle.class, PROPERTY_NAME);
    }
}
