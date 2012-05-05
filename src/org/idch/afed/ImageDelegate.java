/**
 * 
 */
package org.idch.afed;

import java.util.Set;

/**
 * Provides a thin wrapper around an image that represents.
 * @author Neal Audenaert
 */
public interface ImageDelegate {
    
    /** 
     * Lists the facsimile that this image belongs to.
     */
    public Facsimile getFacsimile();
    
    /**
     * Lists the collations that reference this image.
     */
    public Set<Collation> listCollations();
    
    /**
     * Returns a string that identifies how to retrieve this image relative to the 
     * underlying data storage implementation.
     */
    public String getContext();
    

}
