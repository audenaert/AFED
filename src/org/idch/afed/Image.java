/**
 * 
 */
package org.idch.afed;

import java.io.InputStream;
import java.util.Set;

/**
 * Provides a thin wrapper around an image that represents.
 * @author Neal Audenaert
 */
public class Image {
    
    private ImageDelegate delegate;
    
    //=========================================================================
    // CONSTRUCTORS
    //=========================================================================
    
    public Image(Facsimile f, String ctx) {
        this.delegate = DelegateFactory.getImageDelegate(f, ctx);
    }
    
    public Image(Facsimile f, InputStream is, String ctx) {
        this.delegate = null;
    }
    
    public Image(ImageDelegate delegate) {
        this.delegate = delegate;
    }

    ImageDelegate getDelegate() {
        return this.delegate;
    }
    
    //=========================================================================
    // ACCESSORS
    //=========================================================================
    
    /** 
     * Lists the facsimile that this image belongs to.
     * @return
     */
    public Facsimile getFacsimile() {
        return delegate.getFacsimile();
    }
    
    /**
     * Lists the collations that reference this image.
     * @return
     */
    public Set<Collation> listCollations() {
        return delegate.listCollations();
    }
    
    /**
     * Returns a string that identifies how to retrieve this image relative to the 
     * underlying data storage implementation.
     * 
     * @return
     */
    public String getContext() {
        return delegate.getContext();
    }
    
    // TODO need properties mechanism (I want to store the original file name)
}
