/**
 * 
 */
package org.idch.afed;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.idch.meta.DublinCore;
import org.idch.ms.Designation;

/**
 * @author Neal Audenaert
 */
public class Facsimile {
    
    /** This class is implemented as a thin interface over an underlying 
     *  persistent object. The delegate is responsible for the details of how
     *  to create and modify the underlying persistent representation of 
     *  facsimile objects. The appropriate delegate to use is discovered 
     *  automagically by the <code>DelegateFactory</code> based on supplied 
     *  configuration information. For more information on how to configure
     *  the <code>DelegateFactory</code>, @see {@link org.isch.afed.DelegateFactory}.
     */
    private FacsimileDelegate delegate;
    
    //=========================================================================
    // CONSTRUCTORS
    //=========================================================================
    
    
    public Facsimile() {
        this.delegate = DelegateFactory.getFacsimileDelegate();
    }
    
    public Facsimile(String name, String description, String date) {
        this.delegate = DelegateFactory.getFacsimileDelegate(name, description, date);
    }
    
    public Facsimile(FacsimileDelegate delegate) {
        this.delegate = delegate;
    }

    //=========================================================================
    // FACSIMILE DESIGNATIONS
    //=========================================================================
    
    /**
     * Returns a unique identifier for this Facsimile according to a 
     * TODO migrate in previous work on MS Designations
     * 
     * @param schema
     * @return
     */
    public Designation getDesignation(String schema) {
        return delegate.getDesignation(schema);
    }
    
    public boolean hasDesignation(String schema) {
        return delegate.hasDesignation(schema);
    }
    
    /**
     * Adds the provided designation. If there is already a designation with the 
     * same scheme, the existing designation will be evicted and returned.
     * 
     * @param d The designation to add.
     * @return The previous designation, if a designation with the same scheme 
     *      was present and evicted.
     */
    public Designation putDesignation(Designation d) {
        return delegate.putDesignation(d);
    }
    
    /**
     * Attempts to remove the designation with the specified scheme.
     *  
     * @param scheme The scheme of the designation to remove.
     * @return The removed designation or <code>null</code> if there was
     *      no designation with the supplied name to remove.
     */
    public Designation removeDesignation(String scheme) {
        return delegate.removeDesignation(scheme);
    }
    
    public Set<Designation> getDesignations() {
        return delegate.getDesignations();
    }
    
    //=========================================================================
    // ACCESSORS
    //=========================================================================
    
    /**
     * 
     * @return
     */
    public String getName() {
        return delegate.getName();
    }
    
    public String getDescription() {
        return delegate.getDescription();
    }
    
    public String getDateOfOrigin() {
        return delegate.getDateOfOrigin();
    }
    
    public DublinCore getDCMetadata() {
        return delegate.getDCMetadata();
    }
  
    //=========================================================================
    // MUTATORS
    //=========================================================================
  
    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.delegate.setName(name);
    }
    
    public void setDescription(String desc) {
        this.delegate.setDescription(desc);
    }
    
    public void setDateOfOrigin(String date) {
        this.delegate.setDateOfOrigin(date);
    }
    
    //=========================================================================
    // COLLATION METHODS
    //=========================================================================
    
    /**
     * 
     * @return
     */
    public Set<String> listCollations() {
        return this.delegate.listCollations();
    }
    
    public boolean hasCollation(String collationName) {
        return this.delegate.hasCollation(collationName);
    }
    
    public Collation getCollation(String collationName) {
        return this.delegate.getCollation(collationName);
    }
    
    public Collation createCollation(String collationName) {
        return this.delegate.createCollation(collationName);
    }
    
    //=========================================================================
    // IMAGE METHODS
    //=========================================================================
    
    /**
     * 
     * @param subContext Indicates the identifier (relative to this facsimile) to 
     *      be used to identify the image. The <code>subContext</code> must be 
     *      unique relative to this facsimile, but does not need to be globaly 
     *      unique. 
     * @param is The input stream containing the image. 
     * @param type The type of image supplied. 
     */
    public void addImage(String subContext, InputStream is, String type) {
        // TODO make type an enum
        this.delegate.addImage(subContext, is, type);
    }
    
    /**
     * 
     * @return
     */
    public List<String> listImages() {
        return this.delegate.listImages();
    }
    
    //=========================================================================
    // OBJECT OVERRIDE METHODS
    //=========================================================================
    
    public String toString() {
        return "[Facsimile: " + this.getName() + "]";
    }
    
    //=========================================================================
    // PERSISTENCE METHODS
    //=========================================================================
    
    public boolean save() {
        return this.delegate.save();
    }
    
    public boolean remove() {
        return this.delegate.remove();
    }
}

