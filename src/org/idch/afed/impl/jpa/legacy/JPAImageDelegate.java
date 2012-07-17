/**
 * 
 */
package org.idch.afed.impl.jpa.legacy;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.idch.afed.legacy.BasicFacsimile;
import org.idch.afed.legacy.Collation;
import org.idch.afed.legacy.FacsimileDelegate;
import org.idch.afed.legacy.ImageDelegate;

/**
 * @author Neal Audenaert
 */
//@Entity
//@Table(name="IMAGES")
public class JPAImageDelegate implements ImageDelegate {
    
    @Id @GeneratedValue 
    private Long id;
    
    private JPAFacsimileDelegate facsimle;
    private String context;
    
    JPAImageDelegate() {
        
    }
    
    public JPAImageDelegate(FacsimileDelegate f, String ctx) {
        if (!(f instanceof JPAFacsimileDelegate)) {
            // FIXME Recover from this
        }
        
        this.facsimle = (JPAFacsimileDelegate)f;
        this.context = ctx;     // In practice, we'll need to mutate this based on our 
                                // underlying storage mechanism.
        
    }
    
    /** Returns the persistent id. */
    Long getJPAId() {
        return this.id;
    }
    
    /** Called by the JPA framework to inject the id. */
    @SuppressWarnings("unused")
    private void setJPAId (Long id) {
        this.id = id;
    }

    /** 
     * Returns the facsimile this image is associated with.
     * @see org.idch.afed.ImageDelegate#getFacsimile()
     */
    @Override
    public BasicFacsimile getFacsimile() {
        return new BasicFacsimile(this.facsimle);
    }

    /** 
     * Returns an unmodifiable set containing the collations that reference 
     * this image. 
     * 
     * @see org.idch.afed.ImageDelegate#listCollations()
     */
    @Override
    public Set<Collation> listCollations() {
        // TODO should lookup from persistence layer
        return new HashSet<Collation>();
    }
    
    /**
     * Returns the string based context identifier that uniquely identifies 
     * this image relative to the facsimile it belongs to. Typically a page
     * or foliation number.  
     * 
     * @see org.idch.afed.ImageDelegate#getContext()
     */
    @Override
    public String getContext() {
        return this.context;
    }
    
    void setContext(String ctx) {
        this.context = ctx;
    }

}
