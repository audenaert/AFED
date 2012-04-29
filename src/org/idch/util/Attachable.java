/**
 * 
 */
package org.idch.util;

import javax.persistence.EntityManager;

import org.idch.util.persist.RepositoryAccessException;

/**
 * @author Neal Audenaert
 */
public interface Attachable {

    public boolean isAttached();
    
    public void attach() throws RepositoryAccessException;
    
    public void attach(EntityManager em) throws RepositoryAccessException;
    
    public void detach() throws RepositoryAccessException;
}
