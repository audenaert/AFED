/**
 * 
 */
package org.idch.afed.legacy;

import java.util.Set;

import org.idch.afed.legacy.BasicFacsimile;
import org.idch.afed.legacy.Collation;

/**
 * Provides a thin wrapper around an image that represents.
 * @author Neal Audenaert
 */
public interface ImageDelegate {
    
    /** 
     * Lists the facsimile that this image belongs to.
     */
    public BasicFacsimile getFacsimile();
    
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
