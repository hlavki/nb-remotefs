/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.api;

/**
 *
 * @author hlavki
 */
public class UnknownFileSystemException extends Exception {

    private static final long serialVersionUID = 4528657758061867907L;

    public UnknownFileSystemException(String protocol) {
        super("Uknown file system for protocol " + protocol);
    }
}
