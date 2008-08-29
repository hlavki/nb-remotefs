/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp.client;

import java.io.IOException;

/**
 *
 * @author hlavki
 */
public class SFTPException extends IOException {

    private static final long serialVersionUID = -1475697368758068490L;

    public SFTPException() {
        super();
    }

    public SFTPException(String message) {
        super(message);
    }

    public SFTPException(String message, Throwable cause) {
        super(message, cause);
    }

    public SFTPException(Throwable cause) {
        super(cause);
    }
}
