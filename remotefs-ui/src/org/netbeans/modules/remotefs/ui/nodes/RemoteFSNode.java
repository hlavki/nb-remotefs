/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.ui.nodes;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.remotefs.api.RemoteFileSystemInfo;
import org.netbeans.modules.remotefs.api.LogInfoList;
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
        this.fsInfo = fsInfo;
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            public void run() {
                refresh();
            }
        });
        LogInfoList.getDefault().addRemoteFSEventListener(this);
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
        return new Action[]{new AddSiteAction()};
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
        refresh();
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

    public synchronized void refresh() {
        setChildren(Children.create(new RemoteFileSystemFactory(fsInfo), true));
    }

    private class AddSiteAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public AddSiteAction() {
            putValue(NAME, "Add Connection...");
        }

        public void actionPerformed(ActionEvent e) {
            fsInfo.createConnection();
        }
    }
}
