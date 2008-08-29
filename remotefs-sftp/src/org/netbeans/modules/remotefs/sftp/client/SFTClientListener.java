/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp.client;



/**
 *
 * @author hlavki
 */
public interface SFTClientListener {

    /**
     * Called when a response is received from the server.
     * @param responseCode the 3 digit response
     * @param responseText the multi-line text string, lines are terminated by \n
     */
    public void responseReceived(int responseCode, String responseText);

    /**
     * Called when a request is sent to the server.
     * @param command the command sent to the server
     * @param argument the argument sent to the server
     */
    public void requestSent(String command, String argument);
}
