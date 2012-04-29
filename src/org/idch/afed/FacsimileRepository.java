/**
 * 
 */
package org.idch.afed;

import java.util.List;
import java.util.Map;

import org.idch.afed.impl.jpa.JPAFacsimileRepository;
import org.idch.util.persist.RepositoryAccessException;

/**
 * @author Neal Audenaert
 */
public abstract class FacsimileRepository {
    
    public static FacsimileRepository getInstance() {
        // TODO automagically look this up based on config information. For now, 
        //      we're just going to assume JPA.
        return JPAFacsimileRepository.getInstance();
    }
            
    public abstract Facsimile get(String scheme);
    
    public abstract Map<String, String> list();
    
    public abstract List<String> list(String scheme);
    
    public abstract Facsimile create(String name, String description, String date) 
            throws RepositoryAccessException;
    
    public abstract void save(Facsimile f) 
            throws RepositoryAccessException;
    
    // TODO need a generic way of searching

}
