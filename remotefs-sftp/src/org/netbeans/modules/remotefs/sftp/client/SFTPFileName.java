/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.sftp.client;

import org.netbeans.modules.remotefs.api.RemoteFileName;

/**
 *
 * @author hlavki
 */
public class SFTPFileName implements RemoteFileName {

    public static final String ROOT_FOLDER = ".";
    public static final String EMPTY_NAME = "";
    private String name;
    private String directory;

    public SFTPFileName(String name, String directory) {
        this.name = name;
        this.directory = directory;
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
        return (ROOT_FOLDER.equals(directory) ? "" : directory) +
                (ROOT_FOLDER.equals(name) ? "" : ROOT_FOLDER) + name;
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
        return new SFTPFileName(EMPTY_NAME, ROOT_FOLDER);
    }
}
