/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.api;

/**
 *
 * @author hlavki
 */
public enum FileChangeStatus {

    NO_CHANGE(0),
    LOCAL_CHANGE(1),
    REMOTE_CHANGE(2),
    CONFLICT(3);
    private int priority;

    private FileChangeStatus(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
