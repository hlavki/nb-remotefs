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
import java.io.File;
import java.util.Properties;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.netbeans.modules.remotefs.api.LogInfo;
import org.netbeans.modules.remotefs.api.RemoteFileSystem;
import org.netbeans.modules.remotefs.sftp.SFTPFileSystem;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.Exceptions;

/**
 *
 * @author hlavki
 */
public class SFTPLogInfo extends LogInfo implements UserInfo, UIKeyboardInteractive, Comparable<LogInfo> {

    private static final String PROP_USER = "user";
    private static final String PROP_PASSWORD = "password";
    private static final String PROP_KEY_FILE = "keyFile";
    private static final String DEFAULT_PROTOCOL = "sftp";
    private static final String PROP_ROOT_FOLDER = "rootFolder";
    private static final long serialVersionUID = 1L;
    private final JTextField passwordField;

    public SFTPLogInfo() {
        this("localhost", System.getProperty("user.name"));
    }

    public SFTPLogInfo(Properties data) {
        super(data);
        passwordField = new JPasswordField(20);
    }

    public SFTPLogInfo(String host, String user) {
        this(DEFAULT_PROTOCOL, host, SFTPClient.DEFAULT_PORT, user, null);
    }

    public SFTPLogInfo(String protocol, String host, String user) {
        this(protocol, host, SFTPClient.DEFAULT_PORT, user, null);
    }

    public SFTPLogInfo(String protocol, String host, String user, String password) {
        this(protocol, host, SFTPClient.DEFAULT_PORT, user, password);
    }

    public SFTPLogInfo(String protocol, String host, int port, String user, String password) {
//        this.protocol = protocol;
//        this.host = host;
//        this.port = port;
//        this.user = user;
//        this.password = password;
        super();
        this.setProperty(PROP_PROTOCOL, protocol);
        setHost(host);
        setPort(new Integer(port));
        setUser(user);
        setPassword(password);
        this.setProperty(PROP_NAME, getDisplayName());
        setRootFolder(SFTPFileName.ROOT_FOLDER);
        passwordField = new JPasswordField(20);
    }


    /**
     * Get password
     * @return password
     */
    public String getPassword() {
        return data.getProperty(PROP_PASSWORD);
    }

    /**
     * Set password
     * @param password
     */
    public final void setPassword(String password) {
        setProperty(PROP_PASSWORD, password);
    }

    /**
     * Get root folder
     * @return root folder
     */
    public String getRootFolder() {
        return data.getProperty(PROP_ROOT_FOLDER);
    }

    /**
     * Set root folder
     * @param rootFolder root folder
     */
    public final void setRootFolder(String rootFolder) {
        setProperty(PROP_ROOT_FOLDER, rootFolder);
    }

    /**
     * Get user name
     * @return user name
     */
    public String getUser() {
        return data.getProperty(PROP_USER);
    }

    /**
     * Set user name
     * @param user user name
     */
    public final void setUser(String user) {
        setProperty(PROP_USER, user);
    }

    /**
     * get path to private key file
     * @return private key path
     */
    public String getKeyFile() {
        return data.getProperty(PROP_KEY_FILE);
    }

    /**
     * Set private key path
     * @param keyFile path to private key
     */
    public void setKeyFile(String keyFile) {
        setProperty(PROP_KEY_FILE, keyFile);
    }

    public final String getDisplayName() {
        return getProtocol() + "://" + getUser() + "@" + getHost() +
                (getPort() == SFTPClient.DEFAULT_PORT ? "" : ":" + getPort());
    }

    public String getPassphrase() {
        return getPassword();
    }

    public boolean promptPassword(String message) {
        Object[] ob = {passwordField};
        int result =
                JOptionPane.showConfirmDialog(null, ob, message,
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            setPassword(passwordField.getText());
            System.out.println("Password is " + getPassword());
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
                setPassword(texts[i].getText());
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
        if (getHost().equals(anotherLogInfo.getHost()) && getPort() == anotherLogInfo.getPort() &&
                getUser().equals(anotherLogInfo.getUser())) {
            if (getPassword().equals(anotherLogInfo.getPassword()) || getKeyFile().equals(anotherLogInfo.getKeyFile())) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }

    public Node.Property[] getNodeProperties(RemoteFileSystem fs) {
        Node.Property[] props = new Node.Property[6];
        try {
            props[0] = new PropertySupport.Reflection<String>(fs, String.class, "server");
            props[0].setName("Server name or IP");
            props[1] = new PropertySupport.Reflection<String>(fs, String.class, "username");
            props[1].setName("Username");
            props[2] = new PropertySupport.Reflection<String>(fs, String.class, "password");
            props[2].setName("Password");
            props[3] = new PropertySupport.Reflection<Integer>(fs, int.class, "port");
            props[3].setName("Port");
            props[4] = new PropertySupport.Reflection<String>(fs, String.class, "startDir");
            props[4].setName("Root folder");
            props[5] = new PropertySupport.Reflection<File>(fs, File.class, "getCacheAsFile", null);
            props[5].setName("cache folder");
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return props;
    }
    private RemoteFileSystem fs;

    @Override
    public synchronized RemoteFileSystem createFileSystem() {
        if (fs == null) {
            fs = new SFTPFileSystem(this);
        }
        return fs;
    }
}
