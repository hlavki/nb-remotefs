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
package org.netbeans.modules.remotefs.api;

import java.util.Date;

/**
 *
 * @author hlavki
 */
public class RemoteFileAttributes {

    private boolean directory;
    private RemoteFileName name;
    private Long size;
    private Date lastModified;
    private boolean writeable;
    private boolean readable;

    public RemoteFileAttributes() {
    }

    public RemoteFileAttributes(RemoteFileName name, boolean directory, Long size, Date lastModified) {
        this.name = name;
        this.directory = directory;
        this.size = size;
        this.lastModified = lastModified;
    }

    public RemoteFileAttributes(RemoteFileName name, boolean directory) {
        this(name, directory, null, null);
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public RemoteFileName getName() {
        return name;
    }

    public void setName(RemoteFileName name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public boolean isReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public boolean isWriteable() {
        return writeable;
    }

    public void setWriteable(boolean writeable) {
        this.writeable = writeable;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RemoteFileAttributes)) {
            return false;
        }
        RemoteFileAttributes rfa = (RemoteFileAttributes) obj;
        return name == null ? rfa.name == null : name.equals(rfa.name) &&
                (size != null && size.equals(rfa.size)) &&
                (lastModified != null && lastModified.equals(rfa.lastModified)) &&
                directory == rfa.directory;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.directory ? 1 : 0);
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 79 * hash + (this.size != null ? this.size.hashCode() : 0);
        hash = 79 * hash + (this.lastModified != null ? this.lastModified.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "[ FILE: " + getName() + " IS_DIR: " + isDirectory() + " SIZE: " + getSize() +
                " DATE: " + getLastModified() + " ]";
    }
}
