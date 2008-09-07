/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s):
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
/*
/* If you wish your version of this file to be governed by only the CDDL
/* or only the GPL Version 2, indicate your decision by adding
/* "[Contributor] elects to include this software in this distribution
/* under the [CDDL or GPL Version 2] license." If you do not indicate a
/* single choice of license, a recipient has the option to distribute
/* your version of this file under either the CDDL, the GPL Version 2 or
/* to extend the choice of license to its licensees as provided above.
/* However, if you add GPL Version 2 code and therefore, elected the GPL
/* Version 2 license, then the option applies only if the new code is
/* made subject to such option by the copyright holder.
 *
 * Contributor(s): Libor Martinek.
 */
package org.netbeans.modules.remotefs.ftp.client;

import java.util.Properties;
import org.netbeans.modules.remotefs.api.LogInfo;
import org.netbeans.modules.remotefs.api.RemoteFileSystem;
import org.netbeans.modules.remotefs.ftp.FTPFileSystem;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.Exceptions;

/** FTPLogInfo stores login information
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPLogInfo extends LogInfo {

    static final long serialVersionUID = 4795532037339960289L;
    private static final String DEFAULT_PROTOCOL = "ftp";
    private static final String PROP_HOST = "host";
    private static final String PROP_PORT = "port";
    private static final String PROP_USER = "user";
    private static final String PROP_PASSWORD = "password";
    private static final String PROP_ROOT_FOLDER = "rootFolder";
    private static final String PROP_PASSIVE_MODE = "passiveMode";

    /** Create empty LogInfo */
    public FTPLogInfo() {
        this("localhost", FTPClient.DEFAULT_PORT, "anonymous", "forteuser@");
    }

    public FTPLogInfo(Properties data) {
        super(data);
    }

    /** Create LogInfo
     * @param host
     * @param port
     * @param user
     * @param password
     * @param passiveMode
     */
    public FTPLogInfo(String host, int port, String user, String password) {
        this(DEFAULT_PROTOCOL, host, port, user, password, false);
    }

    /** Create LogInfo
     * @param host
     * @param port
     * @param user
     * @param password
     * @param passiveMode 
     */
    public FTPLogInfo(String host, int port, String user, String password, boolean passiveMode) {
        this(DEFAULT_PROTOCOL, host, port, user, password, passiveMode);
    }

    /** Create LogInfo
     * @param protocol 
     * @param host
     * @param port
     * @param user
     * @param password 
     */
    public FTPLogInfo(String protocol, String host, int port, String user, String password, boolean passiveMode) {
        super();
        this.setProperty(PROP_PROTOCOL, protocol);
        setHost(host);
        setPort(new Integer(port));
        setUser(user);
        setPassword(password);
        setPassiveMode(passiveMode);
        this.setProperty(PROP_NAME, getDisplayName());
        setRootFolder(FTPFileName.ROOT_FOLDER);
    }

    /**
     * Get hostname
     * @return hostname
     */
    public String getHost() {
        return data.getProperty(PROP_HOST);
    }

    /**
     * Set hostname
     * @param host hostname
     */
    public void setHost(String host) {
        setProperty(PROP_HOST, host);
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
    public void setPassword(String password) {
        setProperty(PROP_PASSWORD, password);
    }

    /**
     * Get port nubmer
     * @return port number
     */
    public Integer getPort() {
        String value = data.getProperty(PROP_PORT);
        return value != null ? Integer.valueOf(value) : null;
    }

    /**
     * Set port number
     * @param port port nubmer
     */
    public void setPort(Integer port) {
        setProperty(PROP_PORT, port.toString());
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
    public void setRootFolder(String rootFolder) {
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
    public void setUser(String user) {
        setProperty(PROP_USER, user);
    }

    /**
     * Get port nubmer
     * @return port number
     */
    public boolean isPassiveMode() {
        String value = data.getProperty(PROP_PASSIVE_MODE);
        return value != null ? Boolean.valueOf(value) : null;
    }

    /**
     * Set port number
     * @param port port nubmer
     */
    public void setPassiveMode(boolean passiveMode) {
        setProperty(PROP_PASSIVE_MODE, Boolean.toString(passiveMode));
    }

    /** Return human redable description of this LogInfo */
    public String getDisplayName() {
        String user = getUser();
        return getProtocol() + "://" + ((user != null && user.equalsIgnoreCase("anonymous")) ? "" : user + "@") +
                getHost() + ((getPort() == FTPClient.DEFAULT_PORT) ? "" : (":" + String.valueOf(getPort())));
    }

    public Node.Property[] getNodeProperties(RemoteFileSystem fs) {
        Node.Property[] props = new Node.Property[5];
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
//            Property prop6 = new PropertySupport.Reflection(site, File.class, "cache");
//            prop6.setName("cache folder");
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return props;
    }
    private RemoteFileSystem fs;

    @Override
    public synchronized RemoteFileSystem createFileSystem() {
        if (fs == null) {
            fs = new FTPFileSystem(this);
        }
        return fs;
    }
}
