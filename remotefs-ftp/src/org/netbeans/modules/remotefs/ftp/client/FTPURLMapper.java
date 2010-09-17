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
/* Contributor(s): Philip Stoehrer
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
 * Contributor(s): Philip Stoehrer
 */
package org.netbeans.modules.remotefs.ftp.client;

import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.remotefs.ftp.FTPFileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.util.lookup.ServiceProvider;

/**
 * Mapping URLs with FTP protocol to a FileObject.
 * @see URLMapper#findFileObject(java.net.URL) 
 * @author Philip Stoehrer
 */
@ServiceProvider(service = URLMapper.class)
public class FTPURLMapper extends URLMapper {

    private static final Logger log = Logger.getLogger(FTPURLMapper.class.getName());
    private final Map<String, FTPFileSystem> fileSystemCache = new HashMap<String, FTPFileSystem>();

    @Override
    public URL getURL(FileObject fo, int type) {
        // TODO What is this method for? Could we do anything usefull?
        return null;
    }

    @Override
    public FileObject[] getFileObjects(URL url) {
        if ("ftp".equalsIgnoreCase(url.getProtocol())) {
            FTPFileSystem fs = findFileSystem(url);
            if (!fs.isConnected()) {
                fs.setConnected(true);
            }
            FileObject fob = fs.getRoot().getFileObject(url.getPath());
            if (fob == null) {
                log.log(Level.SEVERE, "Cannot establish a connection to {0}", url.toExternalForm());
            }
            return new FileObject[]{ fob };

        }
        return null;
    }

    /**
     * Gets the file system of the given URL. Creates the file system if it
     * does not exist already.
     *
     * @param url URL with FTP protocol
     * @return a file system
     */
    private FTPFileSystem findFileSystem(URL url) {
        String key = url.getAuthority();
        if (!fileSystemCache.containsKey(key)) {
            fileSystemCache.put(key, createFileSystem(url));
        }
        return fileSystemCache.get(key);
    }

    /**
     * Creates a new file system for the given URL.
     * @param url URL with FTP protocol
     * @return a new file system
     */
    private FTPFileSystem createFileSystem(URL url) {
        FTPFileSystem fs = new FTPFileSystem();
        try {
            fs.setServer(url.getHost());
            if (URLHelper.containsPassword(url)) {
                fs.setPassword(URLHelper.extractPassword(url));
            }
            if (URLHelper.containsUser(url)) {
                fs.setUsername(URLHelper.extractUser(url));
            }
            if (URLHelper.containsPort(url)) {
                fs.setPort(url.getPort());
            }
            fs.setStartDir("");
            fs.setAlwaysRefresh(true);
        } catch (PropertyVetoException ex) {
            log.log(Level.WARNING, "Cannot set file system property \"{0}\" to value \"{1}\".",
                    new Object[]{
                        ex.getPropertyChangeEvent().getPropertyName(), ex.getPropertyChangeEvent().getNewValue()
                    });
        } finally {
            return fs;
        }
    }
}
