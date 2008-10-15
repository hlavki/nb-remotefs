/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.remotefs.api.config;

import java.io.IOException;
import org.openide.filesystems.FileObject;

/**
 * Ability for a project to permit other modules to store arbitrary cache
 * data associated with the project.
 * @see org.netbeans.api.project.Project#getLookup
 * @author Michal Hlavac
 */

public interface CacheDirectoryProvider {

    /**
     * Get a directory in which modules may store disposable cached information
     * about the project, such as an index of classes it contains.
     * This directory should be considered non-sharable by
     * {@link org.netbeans.api.queries.SharabilityQuery}.
     * Modules are responsible for preventing name clashes in this directory by
     * using sufficiently unique names for child files and folders.
     * @return a cache directory
     * @throws IOException if it cannot be created or loaded
     */
    FileObject getCacheDirectory() throws IOException;

}
