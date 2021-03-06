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
  
 import org.openide.filesystems.FileObject;
 import org.openide.filesystems.FileStateInvalidException;
 import org.openide.filesystems.FileSystem;
  import org.openide.loaders.DataFolder;
  import org.openide.nodes.Node;
 import org.openide.util.Exceptions;
  import org.openide.util.HelpCtx;
 import org.openide.util.actions.CookieAction;
  
  /** Action for downloading all files.
  *
  * @author Libor Martinek
  */
  public class DownloadAllAction extends CookieAction {
 
   static final long serialVersionUID = 8041760481719982503L;
 
    /** @return DataFolder class */
 
     
     protected Class[] cookieClasses() {
         return new Class[]{DataFolder.class};
         
    }
  
     protected void performAction(Node[] nodes) {
      for (int i = 0; i < nodes.length; i++) {
             DataFolder df = nodes[i].getCookie(DataFolder.class);
        if (df != null) {
                 FileObject fo = df.getPrimaryFile();
          try {
             FileSystem fs = fo.getFileSystem();
                     if (fs instanceof RemoteFileSystem) {
                         ((RemoteFileSystem) fs).downloadAll(fo.getPath());
          }
                 } catch (FileStateInvalidException e) {
                     Exceptions.printStackTrace(e);
          }
        }
      }
    }
  
     protected int mode() {
      return MODE_ALL;
    }
  
     public String getName() {
      return "Download All";
    }
  
     public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
    }
  
     @Override
     protected boolean asynchronous() {
         return false;
  }
 }
