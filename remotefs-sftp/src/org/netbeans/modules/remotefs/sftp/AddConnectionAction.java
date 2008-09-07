/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.netbeans.modules.remotefs.api.LogInfoList;
import org.netbeans.modules.remotefs.sftp.client.SFTPLogInfo;
import org.openide.util.Exceptions;

public final class AddConnectionAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        try {
            LogInfoList.getDefault().add(new SFTPLogInfo("hlavki", "localhost"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
