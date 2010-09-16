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

import java.net.URL;

/**
 * Some simple helper methods for java.net.URL class.
 * 
 * @author Philip Stoehrer
 */
class URLHelper {


    /**
     * Checks if the given URL contains a password.
     * @param url
     * @return <code>true</code> if a password is given in the URL, <code>false otherwise</code>.
     */
    public static boolean containsPassword(URL url) {
        return containsUser(url) && url.getUserInfo().contains(":");
    }


    /**
     * Checks if the given URL contains a user name.
     * @param url
     * @return <code>true</code> if a user is given in the URL, <code>false otherwise</code>.
     */
    public static boolean containsUser(URL url) {
        return url.getUserInfo() != null && !url.getUserInfo().equals("");
    }

    /**
     * Checks if the given URL contains a port.
     * @param url
     * @return <code>true</code> if a port is given in the URL, <code>false otherwise</code>.
     */
    public static boolean containsPort(URL url) {
        return url.getPort() != -1;
    }

    /**
     * Extracts the password from the given URL
     * @param url
     * @return password if it exists, <code>null</code>otherwise
     */
    public static String extractPassword(URL url) {
        if (containsPassword(url))   {
            return url.getUserInfo().split(":")[1];
        }
        return null;
    }

    /**
     * Extracts the user name from the given URL
     * @param url
     * @return user name
     */
    public static String extractUser(URL url) {
        if (containsUser(url)) {
           return url.getUserInfo().split(":")[0];
        }
        return null;
    }

}
