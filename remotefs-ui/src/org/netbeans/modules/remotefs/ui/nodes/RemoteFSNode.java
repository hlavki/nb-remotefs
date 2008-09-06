/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.ui.nodes;

import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.remotefs.api.RemoteFileSystemInfo;
import org.netbeans.modules.remotefs.api.config.LogInfoList;
import org.netbeans.modules.remotefs.api.events.RemoteFSEventListener;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 *
 * @author hlavki
 */
public class RemoteFSNode extends AbstractNode implements RemoteFSEventListener {

    private RemoteFileSystemInfo fsInfo;

    public RemoteFSNode(final RemoteFileSystemInfo fsInfo) throws DataObjectNotFoundException {
        super(Children.create(new RemoteFileSystemFactory(fsInfo), true));
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            public void run() {
                refresh(fsInfo);
            }
        });
        LogInfoList.getDefault().addRemoteFSEventListener(this);
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

    public void remoteFSChanged() {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public synchronized void refresh(RemoteFileSystemInfo fsInfo) {
        setChildren(Children.create(new RemoteFileSystemFactory(fsInfo), true));
    }
}
