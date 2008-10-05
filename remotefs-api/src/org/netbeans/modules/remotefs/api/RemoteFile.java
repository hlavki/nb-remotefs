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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/** Object that represent FTP file with cache.
 * @author Libor Martinek
 * @version 1.0
 */
public class RemoteFile {

    private static final Logger log = Logger.getLogger(RemoteFile.class.getName());
    /** Parent RemoteFile object */
    private RemoteFile parent;
    /** Client  */
    private RemoteClient client;
    /** Notify  */
    private Notify notify;
    /** File attributes  */
    private RemoteFileAttributes attrib;
    /** File in cache for local storing */
    protected File file;
    /** Array of children */
    private RemoteFile[] children = new RemoteFile[0];
    private List<RemoteFile> childrenList = new ArrayList<RemoteFile>();
    private RequestProcessor rp;
    /** Path separator */
    private static final String PATH_SEP = "/";
    private Status status;
    private boolean onServer;
    private long cacheLastModified;
    private boolean childrenChanged = false;
    private boolean nextNoChildren = false;

    //***************************************************************************
    /** Creates new RemoteFile
     * @param parent parent FTP file, null for root file
     * @param notify 
     * @param client 
     * @param rp
     * @param onserver 
     * @param file 
     * @param attrib attributes of the file
     * @throws java.io.IOException 
     */
    protected RemoteFile(RemoteFileAttributes attrib, RemoteFile parent, RemoteClient client,
            Notify notify, RequestProcessor rp, File file, boolean onserver) throws IOException {
        this.attrib = attrib;
        this.parent = parent;
        this.client = client;
        this.notify = notify;
        this.file = file;
        this.onServer = onserver;
        this.rp = rp;

        //System.out.println("RemoteFile.RemoteFile: name="+attrib.getName()+" dir="+attrib.isDirectory()+" cache="+file.getPath());

        // Directory of File?
        if (attrib.isDirectory()) {
            status = Status.NOT_CACHED;
            if (client.isConnected()) {
                if (!onserver) {
                    // directory doesn't exist on server. Create new.
                    client.mkdir(getName());
                //System.out.println("RemoteFile: creating dir "+getPath()+" on server");
                }
                // TODO: better upload dir to server, using goOnline
                // upload  contents of directory to server.
                // onserver is false so no unneeded list from server will be retrieved.
            /*String list[] = file.list();
                if (file.exists() && list != null && list.length > 0)
                getChildren();
                 */
                this.onServer = true;
            }
            if (!file.exists()) {
                // directory doesn't exist in cache. Create new.
                file.mkdirs();
                //System.out.println("RemoteFile: creating dir "+file.getPath()+" in cache");
                if (!onserver) // New created directory doesn't contain children
                {
                    status = Status.CACHED;
                } else // Directory exist on server but isn't cached
                {
                    status = Status.NOT_CACHED;
                }
            }
        } // File
        else {
            if (!file.exists()) { // if file doesn't exist in cache..
                if (onserver) {
                    status = Status.NOT_CACHED;
                } // ..but exists on server
                else {
                    status = Status.CACHED;
                } // ..and also doesn't exist on server
                cacheLastModified = file.lastModified();
            } else {  // file exist in cache
                status = Status.CACHED;
                cacheLastModified = file.lastModified();
                int which;
                if (!onserver) {
                    which = 0;
                } // file on server doesn't exist yet
                else {
                    if ((attrib.getSize() == 0 && file.length() == 0) ||
                            (attrib.getSize() == file.length() && attrib.getDate().getTime() == file.lastModified())) {
                        which = -1;
                    } // both files are equal
                    else {
                        which = notify.notifyWhichFile(getName().getFullName(), attrib.getDate(), attrib.getSize(), new Date(file.lastModified()),
                                file.length());
                    }
                }
                if (which == 0) {  // file in cache is the right one
                    status = Status.CHANGED;
                    cacheLastModified = file.lastModified();
                    save();
                //System.out.println("RemoteFile: saving "+getPath()+" to server");
                } else if (which == 1) {  // file on server is the right one
                    file.delete();
                    status = Status.NOT_CACHED;
                    if (notify.isDownloadServerChangedFile()) {
                        load();
                    //System.out.println("RemoteFile: loading "+getPath()+" from server");
                    }
                }
            }
        }
    }

    //***************************************************************************
    /** Creates new root
     * @param client
     * @param notify 
     * @param rp 
     * @param file 
     * @throws java.io.IOException 
     */
    public RemoteFile(RemoteClient client, Notify notify, RequestProcessor rp, File file) throws IOException {
        this(new RemoteFileAttributes(client.getRoot(), true), (RemoteFile) null, client, notify, rp, file, true);
    }

    //***************************************************************************
    /** Get root */
    /*  public RemoteFile getRoot(String startdir) throws IOException {
    if (startdir == null || startdir.equals("/") || startdir.equals("")) 
    return this;  // default root
    // parent directory of startdir
    String parentdir = "/";
    // relative name of startdir
    String dir = startdir;
    int lastslash = startdir.lastIndexOf('/');
    if (lastslash == 0) 
    dir = startdir.substring(1);
    if (lastslash > 0) {
    parentdir = startdir.substring(0,lastslash);
    dir = startdir.substring(lastslash+1);
    }
    // get list of parent directory
    RemoteFileAttributes attrs[] = client.list(parentdir);
    boolean found = false;
    if (attrs != null && attrs.length > 0) 
    // found relative startdir in list of parent directory
    for (int i=0; i<attrs.length; i++) {
    if (attrs[i].getName().getName().equals(dir)) {  // found
    RemoteFile remoteFile = this;
    RemoteFile newfile = null;
    StringTokenizer st = new StringTokenizer(startdir,"/");
    // create all RemoteFile above found startdir RemoteFile
    while (st.hasMoreTokens()) {
    String name = st.nextToken();
    newfile = new RemoteFile(new RemoteFileAttributes(remoteFile.getName().createNew(name),true),remoteFile,client, notify,rp,new File(remoteFile.file,name),true);
    remoteFile.children = new RemoteFile[1];
    remoteFile.children[0] = newfile;
    remoteFile = newfile;
    }
    return remoteFile;
    }
    }   
    return null;    
    }
     */
    //***************************************************************************
    /** Returns whole path 
     * @return path of this file
     */
    /*  public String getPath() {
    if (isRoot()) return PATH_SEP;
    else return getDirPath()+getName();
    }    
     */
    //***************************************************************************
    /** Returns path of the parent ending with slash.
     * @return path of the parent ending with slash
     */
    /*  public String getDirPath() {
    if (isRoot()) return PATH_SEP;
    else return parent.getPath()+(parent.isRoot()?"":PATH_SEP);
    }    
     */
    /***************************************************************************
    /** Get all children of this object
     * @return array of children
     */
    public synchronized RemoteFile[] getChildren() {
        childrenChanged = false;
        if (nextNoChildren) {
            nextNoChildren = false;
            return children;
        }
        // FIXME: this is bug!!! I need to wait for getChildrenBlock() before I return children
        rp.post(new Runnable() {

            public void run() {
                try {
                    getChildrenBlock();
                } catch (IOException e) {
                    notify.notifyException(e);
                }
                if (childrenChanged) {
                    nextNoChildren = true;
                    notify.fileChanged(getName().getFullName());
                }
            }
        });

        return children;
    }

    //***************************************************************************
    /** Get all children of this object
     * @return array of children
     * @throws java.io.IOException 
     */
    public synchronized RemoteFile[] getChildrenBlock() throws IOException {
//        log.info("RemoteFile.getChildren: path="+getPath());
        if (children == null) {
        } //TODO: 

        status = Status.CACHED; // TODO: here or at the end?
        // construct HashMap of existing children list
        Set<String> childrenSet = null;
        HashMap<String, RemoteFile> childrenMap = null;
        if (children == null) {
            childrenSet = new HashSet<String>();
        } else {
            childrenMap = new HashMap<String, RemoteFile>();
            for (int i = 0; i < children.length; i++) {
                if (children[i] != null) {
                    childrenMap.put(children[i].getName().getName(), children[i]);
                }
            }
            // gets set of children names
            childrenSet = childrenMap.keySet();
        }
        //System.out.println("RemoteFile.getChildren: childrenset="+childrenset);

        // construct HashMap from list got from server
        HashMap<String, RemoteFileAttributes> serverMap = new HashMap<String, RemoteFileAttributes>();
        Set<String> serverSet;
        //TODO: refresh x always
        if (notify.isRefreshServer() && onServer && client.isConnected()) {
            RemoteFileAttributes remoteFiles[] = client.list(getName());
            if (remoteFiles != null) {
                for (int i = 0; i < remoteFiles.length; i++) {
                    if (remoteFiles[i] != null) {
                        serverMap.put(remoteFiles[i].getName().getName(), remoteFiles[i]);
                    }
                }
            }
            // gets set of RemoteFiles names
            serverSet = serverMap.keySet();
        } else {
            serverSet = new HashSet<String>();
        }
        //System.out.println("RemoteFile.getChildren: serverset="+serverset);

        // construct HashSet of list of files in cache
        Set<String> cacheset = new HashSet<String>();
        if (notify.isScanCache()) {
            String cache[] = file.list();
            if (cache != null) {
                for (int i = 0; i < cache.length; i++) {
                    cacheset.add(cache[i]);
                }
            }
        }
        //System.out.println("RemoteFile.getChildren: cacheset="+cacheset);

        // construct set 6
        if (!childrenSet.containsAll(cacheset) && !serverSet.containsAll(cacheset)) {
            // construct set of not known (new) files in cache
            Set<String> set6 = new HashSet<String>(cacheset);
            // exclude known files (set 4,1)
            set6.removeAll(childrenSet);
            // exclude files on server (set 2)
            set6.removeAll(serverSet);
            //System.out.println("RemoteFile.getChildren: set6="+set6);
            if (!set6.isEmpty()) {
                Iterator it = set6.iterator();
                while (it.hasNext()) {
                    String name = (String) (it.next());
                    RemoteFileAttributes at = new RemoteFileAttributes(getName().createNew(name), new File(file, name).isDirectory());
                    childrenList.add(new RemoteFile(at, this, client, notify, rp, new File(file, name), false));
                    childrenChanged = true;
                }
            }
        }


        //prepare to set 1+3
        Set<String> set1_3 = new HashSet<String>(serverSet);

        // construct set 2+7
        if (!childrenSet.containsAll(serverSet)) {
            // construct set of not known (new) files on server
            Set<String> set2_7 = new HashSet<String>(serverSet);
            // exclude known files (set 1,3)
            set2_7.removeAll(childrenSet);
            //System.out.println("RemoteFile.getChildren: set2_7="+set2_7);
            if (!set2_7.isEmpty()) {
                Iterator it = set2_7.iterator();
                while (it.hasNext()) {
                    RemoteFileAttributes at = serverMap.get(it.next());
                    childrenList.add(new RemoteFile(at, this, client, notify, rp, new File(file, at.getName().getName()), true));
                    childrenChanged = true;
                }

                // construct set 1+3
                // exclude set 2+7
                set1_3.removeAll(set2_7);

            }
        }

        //System.out.println("RemoteFile.getChildren: set1_3="+set1_3);
        Iterator it = set1_3.iterator();
        while (it.hasNext()) {
            RemoteFileAttributes at = serverMap.get(it.next());
            childrenMap.get(at.getName().getName()).refresh(at);
        }

        // construct set 4+5
        if (!serverSet.containsAll(childrenSet)) {
            // construct set of not children without file in server
            Set<String> set4_5 = new HashSet<String>(childrenSet);
            // exclude file on server
            set4_5.removeAll(serverSet);
            //System.out.println("RemoteFile.getChildren: set5_5="+set4_5);
            if (!set4_5.isEmpty()) {
                Iterator iter = set4_5.iterator();
                while (iter.hasNext()) {
                    String name = (String) (iter.next());
                    RemoteFile f = childrenMap.get(name);
                    if (!onServer) {
                        // set onserver = false if parent (this) file has also onserver==false;
                        f.onServer = false;
                        if (!Status.NOT_CACHED.equals(f.status) && f.file.exists()) // ???
                        {
                            f.status = Status.CHANGED;
                        }
                    }
                    f.refresh(new RemoteFileAttributes(f.getName(), isDirectory()));
                }
            }
        }

        //System.out.println("RemoteFile.getChildren: childrenvector="+childrenvector);
        children = childrenList.toArray(children);
        return children;
    }

    //***************************************************************************
    /** Get file attributes for one file. If it doesn't work, disable alwaysRefresh
     * @return
     * @throws java.io.IOException 
     */
    protected RemoteFileAttributes getFileAttributes() throws IOException {
        //if (!notify.isAlwaysRefresh()) return null;
        //System.out.println("RemoteFile.getFileAttributes: path="+getPath());
        //TODO:
        RemoteFileAttributes at[];
        at = client.list(getName());
        if (at == null || at.length == 0) {
            if (!onServer) {
                return null;
            }
            //System.out.println("TESTING alwaysRefresh");
            at = client.list(getParent().getName());
            if (at != null) {
                for (int i = 0; i < at.length; i++) {
                    if (at[i].getName().getName().equals(attrib.getName().getName())) {
                        notify.setAlwaysRefresh(false);
                        //System.out.println("TEST: alwaysRefresh not supported. Disabling.");
                        return null;
                    }
                }
            }
            //System.out.println("TEST: test failed.");
            return null;
        }
        return at[0];
    }

    //***************************************************************************
    protected void refresh() throws IOException {
        refresh(null);
    }

    //***************************************************************************
    protected synchronized void refresh(RemoteFileAttributes at) throws IOException {
        //System.out.println("RemoteFile.refresh: path="+getPath()+" attr:"+(at!=null));
        if (isDirectory()) {
            //getChildren();  ???

            // directory in cache was deleted, if doesn't exist (directory in cache must always exist)
            boolean cacheDeleted = !file.exists();
            // directory in server was deleetd if client is connected, onserver==true and at is unexpectly Epoch */
            boolean serverDeleted = (client.isConnected() && onServer == true && at != null &&
                    at.getDate().getTime() == 0 && at.getSize() == 0);

            // is dir in server was deleted, repair onserver property
            if (serverDeleted) {
                onServer = false;
            }
            // if dir was delete in server and in cache, delete it item from parent
            if (cacheDeleted && serverDeleted) {
                parent.deleteChild(this);
                return;
            }
            // directory deleted only in cache
            if (cacheDeleted && !serverDeleted) {
                // if parent also doesnt' exist refresh it
                if (!parent.file.exists()) {
                    //TODO: ???
                    parent.refresh();
                } else {
                    if (notify.notifyCacheExtDelete(getName().getFullName(), true)) {
                        deleteFile();
                        parent.deleteChild(this);
                    } else {
                        synchronize();
                    //file.mkdirs();
                    //status = NOT_CACHED; 
                    //TODO: pro cely podadresar
                    }
                }
            }
            // directory deleted only from server
            if (!cacheDeleted && serverDeleted) {
                String list[] = file.list();
                // if directory in cache is empty, delete RemoteFile
                if (list == null || list.length == 0) {
                    //TODO: better delete
                    deleteFile();
                    parent.deleteChild(this);
                } else {
                    if (notify.notifyServerExtDelete(getName().getFullName(), true)) {
                        //TODO: better delete
                        deleteFile();
                        parent.deleteChild(this);
                    } else {
                        synchronize();
                    }
                }
            }

        } else {
            //System.out.println("RemoteFile.refresh: serverlast="+attrib.getDate().toString());
            //if (at!=null) System.out.println("RemoteFile.refresh: serverreal="+at.getDate().toString()+" onserver="+onserver);

            boolean serverChanged = false;
            RemoteFileAttributes newAttr = null;
            if (notify.isRefreshServer() && client.isConnected()) {
                if (at == null && notify.isAlwaysRefresh()) {
                    newAttr = getFileAttributes();
                } else {
                    newAttr = at;
                }
                if (newAttr != null) {
                    // if onserver==true but newattr says that file exist on server
                    if (!onServer && !(newAttr.getDate().getTime() == 0 && newAttr.getSize() == 0)) {
                        onServer = true;
                        serverChanged = true;
                    } else {
                        if (onServer) {
                            // date of this file isn't yet known
                            if (attrib.getSize() == newAttr.getSize() && attrib.getDate().getTime() == 0) {
                                attrib.setDate(newAttr.getDate());
                            }
                            // if both files are empty
                            if (attrib.getSize() == 0 && newAttr.getSize() == 0) {
                                serverChanged = false;
                            } else // if size or date differ
                            if (attrib.getSize() != newAttr.getSize() || !attrib.getDate().equals(newAttr.getDate())) {
                                serverChanged = true;
                            }
                        }
                    }
                }
            }

            boolean cacheChanged = false;

            if (status.equals(Status.NOT_CACHED) && (!file.exists() || (file.exists() && file.length() == 0))) // if file realy doesn't exist
            {
                cacheChanged = false;
            } else if (file.lastModified() != cacheLastModified) {
                // if lastmodified date was changed
                cacheChanged = true;
            /*
            System.out.println("RemoteFile.refresh: cachelast="+new Date(cachelastmodified).toString());
            System.out.println("RemoteFile.refresh: cachereal="+new Date(file.lastModified()).toString());
            System.out.println("RemoteFile.refresh: cacheexists:"+file.exists()+" status:"+status);
             */
            }

            // file in cache was deleted if change was detected, status isn't NOT_CACHED, but file doesn't exist
            boolean cacheDeleted = cacheChanged && !file.exists() && !status.equals(Status.NOT_CACHED);
            // file in server was deleted if date is Epoch but onserver==true
            boolean serverDeleted = (onServer == true && newAttr != null &&
                    newAttr.getDate().getTime() == 0 && newAttr.getSize() == 0);

            // repair onserver flag
            if (serverDeleted) {
                onServer = false;
            }

            if (cacheDeleted && serverDeleted) {
                parent.deleteChild(this);
                return;
            }
            if (cacheDeleted && !serverDeleted) {
                status = Status.NOT_CACHED;
                cacheLastModified = 0;
                if (notify.notifyCacheExtDelete(getName().getFullName(), false)) {
                    deleteFile();
                    parent.deleteChild(this);
                }
                cacheChanged = false;
            }
            if (!cacheDeleted && serverDeleted) {
                if (status.equals(Status.NOT_CACHED)) {
                    deleteFile();
                    parent.deleteChild(this);
                    return;
                }
                if (status.equals(Status.CACHED)) {
                    status = Status.CHANGED;
                }
                if (notify.notifyServerExtDelete(getName().getFullName(), false)) {
                    deleteFile();
                    parent.deleteChild(this);
                    return;
                } else {
                    save();
                    serverChanged = false;
                }
            }

            // no modification
            if (!serverChanged && !cacheChanged) {
                return;
            }
            // change only in cache
            if (cacheChanged && !serverChanged) {
                cacheLastModified = file.lastModified();
                if (!status.equals(Status.OPEN)) {
                    status = Status.CHANGED;
                    //System.out.println("RemoteFile.refresh: file "+getPath()+" in cache has changed. Saving");
                    save(); //TODO: save on background
                }
            }

            //change only on server
            if (serverChanged && !cacheChanged) {
                switch (status) {
                    case NOT_CACHED:
                        attrib = newAttr;
                        break;
                    case CACHED:
                        if (notify.notifyServerChanged(getName().getFullName(), newAttr.getDate(), newAttr.getSize(), new Date(file.lastModified()),
                                file.length())) {
                            attrib = newAttr;
                            status = Status.NOT_CACHED;
                            file.delete();
                            if (notify.isDownloadServerChangedFile()) {
                                load();
                            }
                        } else {
                            status = Status.CHANGED;
                            save();
                        }
                        break;
                    case OPEN:
                    case CHANGED:
                        if (serverChanged && cacheChanged) {
                            int which = notify.notifyBothFilesChanged(getName().getFullName(), newAttr.getDate(), newAttr.getSize(),
                                    new Date(file.lastModified()), file.length());
                            if (which == 0) {
                                cacheLastModified = file.lastModified();
                                status = Status.CHANGED;
                                save();
                            } else {
                                attrib = newAttr;
                                status = Status.NOT_CACHED;
                                file.delete();
                                if (notify.isDownloadServerChangedFile()) {
                                    load();
                                }
                            }
                        }
                        break;
                }
            }
            //change both on server and in cache
            if (serverChanged && cacheChanged) {
                int which = notify.notifyBothFilesChanged(getName().getFullName(), newAttr.getDate(), newAttr.getSize(), new Date(file.lastModified()),
                        file.length());
                if (which == 0) {
                    cacheLastModified = file.lastModified();
                    status = Status.CHANGED;
                    save();
                } else {
                    attrib = newAttr;
                    status = Status.NOT_CACHED;
                    file.delete();
                    if (notify.isDownloadServerChangedFile()) {
                        load();
                    }
                }
            }
        }


    }

    //***************************************************************************
    /** Synchronize this directory and all subdirectories with server.
     * @throws java.io.IOException 
     */
    public void synchronize() throws IOException {
        //if (!client.isConnected()) return; // TODO: realy exit?, no!
        if (isDirectory()) {
            // Directory 
            // if directory doesn't exist, create new
            if (!file.exists()) {
                status = Status.NOT_CACHED;
                file.mkdirs();
            }
            // if directory doesn't exist on server, create new
            if (client.isConnected() && !onServer) {
                client.mkdir(getName());
            }
            // TODO: realy always getchildren? , or only on CACHED?
            String list[] = null;
            if (file.exists()) {
                list = file.list();
            }
            //System.out.println("RemoteFile.refreshAll: file="+file.getPath()+" length="+list.length);
            // if directory isn't empty
            if (list != null && list.length > 0) {
                getChildrenBlock();
            }
            for (int i = 0; i < children.length; i++) {
                if (children[i] != null) {
                    children[i].synchronize();
                }
            }
            onServer = true;
        } // File
        else {
            // refresh();  // Not necessary because refresh is called in getChildren, 
            // but in this case first call of uploadAll must be perform on directory
            if (!file.exists()) {
                status = Status.NOT_CACHED;
            }
            if (client.isConnected() && status.equals(Status.CHANGED)) {
                save();
            }
        }


    }

    //***************************************************************************
    /** Download all files in this directory and all subdirectories from server.
     * @throws java.io.IOException 
     */
    public void downloadAll() throws IOException {
        if (!client.isConnected()) {
            return;
        }
        if (isDirectory()) {
            if (!file.exists()) {
                status = Status.NOT_CACHED;
                file.mkdirs();
            }
            getChildren();
            for (int i = 0; i < children.length; i++) {
                if (children[i] != null) {
                    children[i].downloadAll();
                }
            }
        } else {
            if (status.equals(Status.NOT_CACHED)) {
                load();
            }
        }
    }

    //***************************************************************************
    /** Delete all files in cache for this directory and all subdirectories.
     * @throws java.io.IOException 
     */
    public void cleanCache() throws IOException {
        if (isDirectory()) {
            for (int i = 0; i < children.length; i++) {
                if (children[i] != null) {
                    children[i].cleanCache();
                }
            }
        } else {
            if (status.equals(Status.CACHED) && file.exists()) {
                status = Status.NOT_CACHED;
                file.delete();
                cacheLastModified = 0;
            }
        }
    }

    //***************************************************************************
    /** Get all children.
     * @return array of String of children
     * @throws java.io.IOException 
     */
    public String[] getStringChildren() throws IOException {
        getChildren();
        String s[] = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                s[i] = children[i].getName().getName();
            }
        }
        return s;
    }

    //***************************************************************************
    /** Get child specified by the name.
     * @param name name of the child
     * @return found child, or null
     * @throws java.io.IOException 
     */
    public RemoteFile getChild(String name) throws IOException {
        //System.out.println("RemoteFile.getChild: path="+getPath());
        getChildren(); // TODO: get only one file, no all dir
        return getExistingChild(name);
    }

    //***************************************************************************
    public RemoteFile getExistingChild(String name) throws IOException {
        //System.out.println("RemoteFile.getExistingChild: path="+getPath());
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null && children[i].getName().getName().equals(name)) {
                return children[i];
            }
        }
        return null;
    }

    //***************************************************************************
    /** Find the file specified by the name in this directory and all subdirectories. 
     * @param name
     * @return
     * @throws java.io.IOException 
     */
    public RemoteFile find(String name) throws IOException {
        //System.out.println("RemoteFile.find: path="+getPath()+"  name="+name);
        RemoteFile remoteFile = this, newfile;
        if (name.equals(".")) {
            return remoteFile;
        }
        StringTokenizer st = new StringTokenizer(name, "/");
        while (st.hasMoreTokens()) {
            String next = st.nextToken();
            newfile = remoteFile.getExistingChild(next);
            if (newfile == null) {
                newfile = remoteFile.getChild(next);
            }
            remoteFile = newfile;
            if (remoteFile == null) {
                break;
            }
        }
        return remoteFile;
    }

    //***************************************************************************
    /** Return parent object.
     * @return parent object, null if this is root
     */
    public RemoteFile getParent() {
        return parent;
    }

    //***************************************************************************
    /** Test whether this file is directory
     * @return true if directory, false otherwise
     */
    public boolean isDirectory() {
        return attrib.isDirectory();
    }

    //***************************************************************************
    /**  Test whether this file is root.
     * @return true if root
     */
    public boolean isRoot() {
        return parent == null;
    }

    //***************************************************************************
    /** Returns name of this file.
     * @return name
     */
    public RemoteFileName getName() {
        return attrib.getName();
    }

    //***************************************************************************
    /** Load file from server to cache.
     * @throws IOException
     */
    protected void load() throws IOException {
        //System.out.println("RemoteFile.load: path="+getPath());
        if (!client.isConnected()) {
            return;
        }
        if (isDirectory()) {
            return;
        } else {
            if (onServer) {
                //System.out.println("Downloading "+getPath()+" from server");
                client.get(getName(), file);
                file.setLastModified(cacheLastModified = attrib.getDate().getTime());
            }
            status = Status.CACHED;
        }
    }

    //***************************************************************************
    /** Save file from cache to server.
     * @throws IOException
     */
    protected void save() throws IOException {
        //System.out.println("RemoteFile.save: path="+getPath());
        if (isDirectory()) {
            // TODO: ???
        } else if (status.equals(Status.CHANGED) || status.equals(Status.OPEN)) {
            status = Status.CHANGED;
            if (!client.isConnected()) {
                return;
            }
            //System.out.println("Uploading "+getPath()+" to server");
            client.put(file, getName());
            cacheLastModified = file.lastModified();
            attrib.setSize(file.length());
            RemoteFileAttributes rfa = getFileAttributes();
            if (rfa != null) {
                attrib.setDate(rfa.getDate());
                file.setLastModified(rfa.getDate().getTime());
                cacheLastModified = rfa.getDate().getTime();
            } else {
                attrib.setDate(new Date(0));
            } // TODO: get time from server? 
            status = Status.CACHED;
            onServer = true;
        //System.out.println("RemoteFile.save: end. path="+getPath());
        }
    }

    //***************************************************************************
    /** Get InputStream
     * @throws IOException
     * @return ImputStream of the file
     */
    public InputStream getInputStream() throws IOException {
        if (isDirectory()) {
            throw new FileNotFoundException("Can't get inputstream from directory " + file.getPath());
        }
        refresh();
        if (status.equals(Status.NOT_CACHED)) {
            load();
        }
        if (!file.exists()) {
            return null;
        } //file.createNewFile();
        return new FileInputStream(file);
    }

    //***************************************************************************
    /** Returns OutputStream
     * @throws IOException
     * @return OutputStream of the file
     */
    public OutputStream getOutputStream() throws IOException {
        //System.out.println("RemoteFile.getOutputStream: file="+getPath());
        if (isDirectory()) {
            throw new IOException("Can't get outputstream from directory " + file.getPath());
        }
        refresh();
        status = Status.OPEN;
        return new RemoteOutputStream(this);
    }

    //*************************************************************************** 
    /** Get the size of the file
     * @return size
     * @throws java.io.IOException 
     */
    public long getSize() throws IOException {
        //TODO: if size isn't known
        refresh();
        if (status.equals(Status.CHANGED)) {
            return file.length();
        }
        return attrib.getSize();
    }

    //***************************************************************************
    /** Test whether the file is only for reading
     * @return readonly flag
     */
    public boolean isReadOnly() {
        //TODO
        return false;
    }

    //***************************************************************************
    /** Return date of last modification
     * @return last modification date
     * @throws java.io.IOException 
     */
    public Date lastModified() throws IOException {
        //TODO: if data isn't known
        refresh();
        if (!status.equals(Status.NOT_CACHED)) {
            return new Date(cacheLastModified);
        } else {
            return attrib.getDate();
        }
    }

    //***************************************************************************
    /** Delete only this file, no entry in parent file.
     * @throws IOException
     */
    protected void deleteFile() throws IOException {
        if (isDirectory()) {
            if (status.equals(Status.NOT_CACHED)) {
                getChildren();
            }
            for (int i = 0; i < children.length; i++) {
                // if this file doesn't exist on server, set correct onserver also for child
                if (!onServer) {
                    children[i].onServer = false;
                }
                if (children[i] != null) {
                    children[i].deleteFile();
                }
            }
            children = new RemoteFile[0];
            childrenChanged = true;
            childrenList.clear();
            if (onServer && client.isConnected()) {
                client.rmdir(getName());
            }
        } else if (onServer && client.isConnected()) {
            client.delete(getName());
        }
        if (file.exists()) {
            file.delete();
        }
    }

    //***************************************************************************
    /** Delete this file object
     * @throws IOException
     */
    public void delete() throws IOException {
        refresh();
        deleteFile();
        parent.deleteChild(this);
    }

    //***************************************************************************
    /** Delete entry of the specified child
     * @param child fileobject of the child to delete
     */
    protected void deleteChild(RemoteFile child) {
        childrenList.remove(child);
        children = childrenList.toArray(children);
        childrenChanged = true;
    }

    //***************************************************************************
    /** Rename the file
     * @param name new name
     * @throws IOException
     */
    public void rename(String name) throws IOException {
        if (isRoot()) {
            throw new IOException("Cannot rename root of filesystem");
        }
        if (parent.getChild(name) != null) {
            throw new IOException("File " + getName().getFullName() + PATH_SEP + name + " already exists");
        }

        if (client.isConnected()) {
            client.rename(getName(), name);
        } else {
            onServer = false;
        } // TODO: ???
        attrib.getName().setName(name);
        File tmp = new File(file.getParentFile(), name);
        file.renameTo(tmp);
        file = tmp;
    }

    //***************************************************************************
    /** Create new data file object
     * @param name name of the new data file
     * @throws IOException
     * @return created data file
     */
    public RemoteFile createData(String name) throws IOException {
        // get child to test whether already exists
        if (getChild(name) != null) {
            throw new IOException("File " + getName().getFullName() + PATH_SEP + name + " already exists");
        }
        return createFile(new RemoteFileAttributes(getName().createNew(name), false), false);
    }

    //*************************************************************************** 
    /** Create new folder
     * @param name name of the new folder
     * @throws IOException
     * @return created folder
     */
    public RemoteFile createFolder(String name) throws IOException {
        // get child to test whether already exists
        if (getChild(name) != null) {
            throw new IOException("Folder " + getName().getFullName() + PATH_SEP + name + " already exists");
        }
        return createFile(new RemoteFileAttributes(getName().createNew(name), true), false);
    }

    //***************************************************************************   
    /** Create new file 
     * @param a attributes of the new file
     * @return created file
     */
    private RemoteFile createFile(RemoteFileAttributes a, boolean onserver) throws IOException {
        RemoteFile newfile = new RemoteFile(a, this, client, notify, rp, new File(file, a.getName().getName()), onserver);
        childrenList.add(newfile);
        childrenChanged = true;
        children = childrenList.toArray(children);
        return newfile;
    }

    //*************************************************************************** 
    protected interface Notify {

        public boolean isRefreshServer();

        public boolean isScanCache();

        public boolean isAlwaysRefresh();

        public void setAlwaysRefresh(boolean alwaysRefresh);

        public int notifyWhichFile(String path, Date file1, long size1, Date file2, long size2);

        public int notifyBothFilesChanged(String path, Date file1, long size1, Date file2, long size2);

        public boolean isDownloadServerChangedFile();

        public boolean notifyServerChanged(String path, Date file1, long size1, Date file2, long size2);

        public boolean notifyCacheExtDelete(String path, boolean isDir);

        public boolean notifyServerExtDelete(String path, boolean isDir);

        public void fileChanged(String path);

        public void notifyException(Exception e);
    }

    //*************************************************************************** 
    protected interface RequestProcessor {

        public void post(Runnable run);
    }

    static enum Status {

        NOT_CACHED, CACHED, OPEN, CHANGED
    }
}
