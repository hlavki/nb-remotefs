/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author hlavki
 */
public class SFTPDialogs {

    /** Creates new FTPDialogs */
    private SFTPDialogs() {
    }

    public static int disconnect(String server) {
        Object ops[] = new String[3];
        ops[0] = "Yes";
        ops[1] = "Yes to all";
        ops[2] = "No";
        Object obj = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                "Another filesystem is also connected to server " + server + ".\n" +
                "Do you realy want to diconnect from server?\n" +
                "If you say " + ops[0] + ", only this fileystem will be disconnected, but you will not be able to work offline\n" +
                "If you say " + ops[1] + ", all filesystem will be disconneected and you will be able to work offline",
                "Question", NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE, ops, ops[2]));
        if (obj == ops[0]) {
            return 0;
        }
        if (obj == ops[1]) {
            return 1;
        }
        if (obj == ops[2]) {
            return 2;
        }
        return 2;
    }

    public static boolean connect(String server) {
        Object obj = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                "Another filesystem is now disconnected from server " + server + ".\n" +
                "Do you realy want to connect to server?\n" +
                "If you say Yes, all other filesystems will be connected too.",
                "Question", NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE, null, NotifyDescriptor.YES_OPTION));
        if (obj == NotifyDescriptor.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    public static void startdirNotFound(String startdir, String server) {
        Object ops[] = {"OK"};
        DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                "Starting directory " + startdir + " doesn't exist on server " + server +
                ".\nRoot directory will be used instead.",
                "Startdir invalid", NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.INFORMATION_MESSAGE, ops, null));
    }

    public static void errorConnect(String error) {
        String ops[] = {"OK"};
        DialogDisplayer.getDefault().notify(new NotifyDescriptor("Error during connecting to FTP server:\n" +
                error + "\nSet correct parameters and try to connect again.", "Error", NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.ERROR_MESSAGE, ops, null));
    }

    public static void incorrectPassword(String server) {
        Object ops[] = {"OK"};
        DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                "Another filesystem is also connected to server " + server + " with the same username,\n" +
                "but with other password. Set correct password and try to connect again.",
                "Invalid password", NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.ERROR_MESSAGE, ops, null));
    }

    public static boolean incorrectCache(String oldcache, String newcache, String server) {
        Object obj = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                "You set the cache to " + oldcache + ", but another filesystem is also" +
                "connected\nto server " + server + " with existing cache " + newcache + "\n" +
                "You have to use this existing cache. Do you agree?\n" +
                "If you say No, you will not be connected to server",
                "Question", NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE, null, NotifyDescriptor.YES_OPTION));
        return (obj == NotifyDescriptor.YES_OPTION);
    }
}
