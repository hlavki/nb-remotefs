/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp.client;

import java.util.logging.Logger;
import org.netbeans.modules.remotefs.api.RemoteFileName;

/**
 *
 * @author hlavki
 */
public class SFTPFileName implements RemoteFileName {

    private static final Logger log = Logger.getLogger(SFTPFileName.class.getName());
    public static final String ROOT_FOLDER = "/";
    public static final String EMPTY_NAME = "";
    private String name;
    private String directory;

    public SFTPFileName(String directory, String name) {
        this.directory = directory;
         this.name = name;
   }

    public SFTPFileName(String directory) {
        this(null, directory);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** Get full name (with whole path).
     * @return  full name*/
    public String getFullName() {
        return directory + (ROOT_FOLDER.equals(directory) ? "" : "/") + name;
    }

    /** Get directory of this filename
     * @return directory of this filename */
    protected String getDirectory() {
        return directory;
    }

    /** Create new name object under this name object.
     * @param name name of new name object
     * @return created name object */
    public RemoteFileName createNew(String name) {
        return new SFTPFileName(getFullName(), name);
    }

    /** Get root
     * @return root */
    public static RemoteFileName getRoot() {
        return new SFTPFileName(ROOT_FOLDER, "");
    }

    @Override
    public String toString() {
        return "[ DIR:" + getDirectory() + " FILE: " + getName() + " ]";
    }
}
