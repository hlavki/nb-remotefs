/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.ui;

import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.remotefs.api.RemoteFileSystemInfo;
import org.netbeans.modules.remotefs.api.config.LogInfoList;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author hlavki
 */
public class RemoteFSNode extends AbstractNode {

    private RemoteFileSystemInfo fsInfo;

    public RemoteFSNode(RemoteFileSystemInfo fsInfo) throws DataObjectNotFoundException {
        super(new SiteNode.SiteChildren(LogInfoList.getDefault().getLogInfosByProtocols(fsInfo)));
        this.fsInfo = fsInfo;
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public Image getIcon(int type) {
        return fsInfo.getIcon();
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        DataFolder df = getLookup().lookup(DataFolder.class);
        return new Action[]{/** TODO: AddFTPSiteAction.getInstance()*/
                };
    }

    @Override
    public String getHtmlDisplayName() {
        return getName();
    }

    @Override
    public String getName() {
        return fsInfo.getDisplayName();
    }

    public static class RemoteFSChildren extends Children.Keys<RemoteFileSystemInfo> {

        private List<RemoteFileSystemInfo> fsInfos;

        public RemoteFSChildren(List<RemoteFileSystemInfo> fsInfos) {
            this.fsInfos = fsInfos;
            setKeys(fsInfos);
        }

        @Override
        protected Node[] createNodes(RemoteFileSystemInfo key) {
            try {
                return new Node[]{new RemoteFSNode(key)};
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                return new Node[]{};
            }
        }
    }
}
