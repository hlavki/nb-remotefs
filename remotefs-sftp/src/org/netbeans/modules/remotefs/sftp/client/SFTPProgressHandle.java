/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp.client;

import com.jcraft.jsch.SftpProgressMonitor;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author hlavki
 */
public class SFTPProgressHandle implements com.jcraft.jsch.SftpProgressMonitor {

    private ProgressHandle handle;
    private int count = 0;
    private long max = 0;
    private int percent = -1;

    public SFTPProgressHandle() {
    }

    public void init(int op, String src, String dest, long max) {
        this.max = max;
        handle = ProgressHandleFactory.createHandle(((op == SftpProgressMonitor.PUT) ? "put" : "get") + ": " + src);
        count = 0;
        percent = -1;
        handle.progress(this.count);
        handle.start((int) max);
    }

    public boolean count(long count) {
        this.count += count;

        if (percent >= this.count * 100 / max) {
            return true;
        }
        percent = (int) (this.count * 100 / max);

        // "Completed " + this.count + "(" + percent + "%) out of " + max + ".",
        handle.progress(this.count);

        return true;
    }

    public void end() {
        handle.finish();
    }
}
