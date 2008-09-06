/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.ui;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.remotefs.api.ConnectAction;
import org.netbeans.modules.remotefs.api.RemoteFileSystem;
import org.netbeans.modules.remotefs.ui.resources.Bundle;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author hlavki
 */
public class SiteNode extends FilterNode {

    private RemoteFileSystem site;
    private static final String ICON_DISCONNECTED_PATH = "org/netbeans/modules/remotefs/ui/resources/connection-closed-16x16.png";
    private static final String ICON_CONNECTED_PATH = "org/netbeans/modules/remotefs/ui/resources/connection-opened-16x16.png";

    public SiteNode(RemoteFileSystem site) throws DataObjectNotFoundException {
        super(DataObject.find(site.getRoot()).getNodeDelegate());
        setValue("isReadonly", "false");
        this.site = site;
    }

    @Override
    public Image getIcon(int type) {
        Image icon = null;
        if (site.isConnected()) {
            icon = Utilities.loadImage(ICON_CONNECTED_PATH);
        } else {
            icon = Utilities.loadImage(ICON_DISCONNECTED_PATH);
        }
        return icon;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

//    @Override
//    public String getHtmlDisplayName() {
//        return "<b><font color=\"00AA00\">" + getName() + "</font></b>";
//    }
    @Override
    public String getDisplayName() {
        return site.getDisplayName();
    }

    @Override
    public String getName() {
        return site.getDisplayName();
    }

    @Override
    public Action[] getActions(boolean context) {
//        &Find... $org.openide.actions.FindAction
//        &New $org.openide.actions.NewTemplateAction
//        Rename... $org.openide.actions.RenameAction
//        Cu&t $org.openide.actions.CutAction
//        &Copy $org.openide.actions.CopyAction
//        &Paste $org.openide.actions.PasteAction
//        &Delete $org.openide.actions.DeleteAction
//        Filesystem Action $org.openide.actions.FileSystemAction
//        Tools $org.openide.actions.ToolsAction
//        &Properties $org.openide.actions.PropertiesAction

        Action[] actions = super.getActions(context);
        List<Action> newActions = new ArrayList<Action>();
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] != null) {
                String clazz = actions[i].getClass().getName();
                if ("org.openide.actions.FileSystemAction".equals(clazz)) {
                    newActions.add(actions[i]);
                }
                if ("org.openide.actions.PropertiesAction".equals(clazz)) {
                    newActions.add(actions[i]);
                }
            }
        }

        newActions.add(getAction(RemoveSiteAction.class));
        return newActions.toArray(new SystemAction[0]);
    }

    private SystemAction getAction(Class<? extends SystemAction> clazz) {
        return org.openide.util.SharedClassObject.findObject(clazz, true);
    }

    @Override
    public Action getPreferredAction() {
        return getAction(ConnectAction.class);
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    public void destroy() throws IOException {
        if (site != null) {
            site.setConnected(false);
            site.cleanCache(site.getRoot().getName());
            site.removeNotify();
        }
        DataObject find = DataObject.find(Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("FTPSites"));
        FileObject[] files = find.getPrimaryFile().getChildren();
        for (int i = 0; i < files.length; i++) {
            String lName = getName().substring(6);//strip off "ftp://" URL notation
            String fileName = files[i].getName();
            if (fileName.equals(lName)) {
                files[i].delete();
            }
        }
//        FIXME: fix it
//        ((RemoteFSNode.RemoteFSChildren)this.getParentNode().getChildren()).remove(this.site);

        super.destroy();

    }

    public Node.Property[] getProperties() {
        return site.getNodeProperties();
    }

    @Override
    public Node.PropertySet[] getPropertySets() {
        Sheet.Set props = new Sheet.Set();
        props.setName("Basic Properties");
        props.put(getProperties());
        return new Node.PropertySet[]{props};
    }

//    @Override
//    protected Sheet createSheet() {
//        Sheet result = super.createSheet();
//        Sheet.Set set = Sheet.createPropertiesSet();
//        Node.Property[] props = getProperties();
//        for (int i = 0; i < props.length; i++) {
//            set.put(props[i]);
//        }
//        result.put(set);
//        return result;
//    }
    public static class RemoveSiteAction extends NodeAction {

        private static final long serialVersionUID = 1L;
        private static final String PROP_NAME = "CTL_SiteNode-RemoveSiteAction.name";

        @Override
        protected void performAction(Node[] activatedNodes) {
            for (int i = 0; i < activatedNodes.length; i++) {
                if (activatedNodes[i] instanceof SiteNode) {
                    try {
                        activatedNodes[i].destroy();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }

        @Override
        protected boolean enable(Node[] activatedNodes) {
            return true;
        }

        @Override
        public String getName() {
            return NbBundle.getMessage(Bundle.class, PROP_NAME);
        }

        @Override
        public HelpCtx getHelpCtx() {
            return new HelpCtx(RemoveSiteAction.class);
        }

        @Override
        protected boolean asynchronous() {
            return false;
        }
    }

    public static class SiteChildren extends Children.Keys<RemoteFileSystem> {

        private List<RemoteFileSystem> fileSystems;

        public SiteChildren(List<RemoteFileSystem> fsInfos) {
            this.fileSystems = fsInfos;
            setKeys(fsInfos);
        }

        @Override
        protected Node[] createNodes(RemoteFileSystem key) {
            try {
                return new Node[]{new SiteNode(key)};
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                return new Node[]{};
            }
        }

        public void add(RemoteFileSystem fsToAdd) {
            fileSystems.add(fsToAdd);
            this.setKeys(fileSystems);
        }

        public void remove(RemoteFileSystem fsToRemove) {
            fileSystems.remove(fsToRemove);
            this.setKeys(fileSystems);
        }
    }
}
