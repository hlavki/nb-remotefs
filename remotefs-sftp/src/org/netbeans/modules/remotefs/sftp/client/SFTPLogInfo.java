/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp.client;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.netbeans.modules.remotefs.api.LogInfo;

/**
 *
 * @author hlavki
 */
public class SFTPLogInfo implements LogInfo, UserInfo, UIKeyboardInteractive, Comparable<LogInfo> {

    private static final String DEFAULT_PROTOCOL = "sftp";
    private static final long serialVersionUID = 1L;
    private String host;
    private int port;
    private String user;
    private String password;
    private String keyFile;
    private String rootFolder;
    private JTextField passwordField;
    private String protocol;

    public SFTPLogInfo() {
        this("localhost", null);
    }

    public SFTPLogInfo(String host, String user) {
        this(DEFAULT_PROTOCOL, host, SFTPClient.DEFAULT_PORT, user, null);
    }

    public SFTPLogInfo(String protocol, String host, String user) {
        this(protocol, host, SFTPClient.DEFAULT_PORT, System.getProperty("user.name"), null);
    }

    public SFTPLogInfo(String protocol, String host, String user, String password) {
        this(protocol, host, SFTPClient.DEFAULT_PORT, user, password);
    }

    public SFTPLogInfo(String protocol, String host, int port, String user, String password) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        rootFolder = SFTPFileName.ROOT_FOLDER;
        passwordField = new JPasswordField(20);
    }

    /**
     * Get hostname
     * @return hostname
     */
    public String getHost() {
        return host;
    }

    /**
     * Set hostname
     * @param host hostname
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Get password
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get port nubmer
     * @return port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Set port number
     * @param port port nubmer
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get root folder
     * @return root folder
     */
    public String getRootFolder() {
        return rootFolder;
    }

    /**
     * Set root folder
     * @param rootFolder root folder
     */
    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    /**
     * Get user name
     * @return user name
     */
    public String getUser() {
        return user;
    }

    /**
     * Set user name
     * @param user user name
     */
    public void setUser(String user) {
        this.user = user;
    }

    public String displayName() {
        return protocol + "://" + user + "@" + host + (port == SFTPClient.DEFAULT_PORT ? "" : ":" + String.valueOf(port));
    }

    public String getPassphrase() {
        return password;
    }

    /**
     * get path to private key file
     * @return private key path
     */
    public String getKeyFile() {
        return keyFile;
    }

    /**
     * Set private key path
     * @param keyFile path to private key
     */
    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    public boolean promptPassword(String message) {
        Object[] ob = {passwordField};
        int result =
                JOptionPane.showConfirmDialog(null, ob, message,
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            password = passwordField.getText();
            System.out.println("Password is " + password);
            return true;
        } else {
            return false;
        }
    }

    public boolean promptPassphrase(String message) {
        return true;
    }

    public boolean promptYesNo(String message) {
        Object[] options = {"yes", "no"};
        int foo = JOptionPane.showOptionDialog(null,
                message,
                "Warning",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        return foo == 0;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
    final GridBagConstraints gbc =
            new GridBagConstraints(0, 0, 1, 1, 1, 1,
            GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0);
    private Container panel;

    public String[] promptKeyboardInteractive(String destination,
            String name,
            String instruction,
            String[] prompt,
            boolean[] echo) {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 0;
        panel.add(new JLabel(instruction), gbc);
        gbc.gridy++;

        gbc.gridwidth = GridBagConstraints.RELATIVE;

        JTextField[] texts = new JTextField[prompt.length];
        for (int i = 0; i < prompt.length; i++) {
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridx = 0;
            gbc.weightx = 1;
            panel.add(new JLabel(prompt[i]), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 1;
            if (echo[i]) {
                texts[i] = new JTextField(20);
            } else {
                texts[i] = new JPasswordField(20);
            }
            panel.add(texts[i], gbc);
            gbc.gridy++;
        }

        if (JOptionPane.showConfirmDialog(null, panel,
                destination + ": " + name,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
            String[] response = new String[prompt.length];
            for (int i = 0; i < prompt.length; i++) {
                response[i] = texts[i].getText();
                password = texts[i].getText();
            }
            return response;
        } else {
            return null;  // cancel
        }
    }

    public int compareTo(LogInfo anotherObj) {
        if (!(anotherObj instanceof SFTPLogInfo)) {
            return -1;
        }
        SFTPLogInfo anotherLogInfo = (SFTPLogInfo) anotherObj;
        if (host.equals(anotherLogInfo.getHost()) && port == anotherLogInfo.getPort() &&
                user.equals(anotherLogInfo.getUser())) {
            if (password.equals(anotherLogInfo.getPassword()) || keyFile.equals(anotherLogInfo.getKeyFile())) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }

    public String getProtocol() {
        return protocol;
    }
}