/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.ui.nodes;

import java.util.List;
import org.netbeans.modules.remotefs.api.RemoteFileSystem;
import org.netbeans.modules.remotefs.api.RemoteFileSystemInfo;
import org.netbeans.modules.remotefs.api.LogInfoList;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author hlavki
 */
public class RemoteFileSystemFactory extends ChildFactory<RemoteFileSystem> {

    private RemoteFileSystemInfo fsInfo;

    public RemoteFileSystemFactory(RemoteFileSystemInfo fsInfo) {
        this.fsInfo = fsInfo;
    }

    @Override
    protected boolean createKeys(List<RemoteFileSystem> toPopulate) {
        toPopulate.addAll(LogInfoList.getDefault().getLogInfosByProtocols(fsInfo));
        // TODO: imeplements Comarable interface in RemoteFileSystem
//        Collections.sort(toPopulate);
        return true;
    }

    @Override
    protected Node createNodeForKey(RemoteFileSystem key) {
        Node result = null;
        try {
            result = new SiteNode(key);
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }
}

