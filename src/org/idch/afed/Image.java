/**
 * 
 */
package org.idch.afed;

import java.util.List;

/**
 * Provides a thin wrapper around an image that represents.
 * @author Neal Audenaert
 */
public interface Image {
    
    /** 
     * Lists the facsimile that this image belongs to.
     * @return
     */
    public Facsimile getFacsimile();
    
    /**
     * Lists the collations that reference this image.
     * @return
     */
    public List<Collation> listCollations();
    
    /**
     * 
     * @return
     */
    public String getContext();
    

}
