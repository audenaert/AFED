/**
 * 
 */
package org.idch.afed.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.idch.afed.Collation;
import org.idch.afed.Facsimile;
import org.idch.meta.DublinCore;
import org.idch.ms.Designation;

/**
 * @author Neal Audenaert
 */
public class BasicFacsimile implements Facsimile {

    private String name = null;
    private String description = null;
    private String date = null;
    private DublinCore dc = null;
    
    private Set<Collation> collations = new HashSet<Collation>();
    
    private Map<String, Designation> designations = new HashMap<String, Designation>();

    /** 
     * Returns the name for this document.
     * 
     * @see org.idch.afed.Facsimile#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /** 
     * Sets the name for this document.
     * 
     * @see org.idch.afed.Facsimile#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * Returns a description of this document.
     * 
     * @see org.idch.afed.Facsimile#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets a description for this document. In general, this should be plain 
     * text or lightly formated HTML.
     * 
     * @see org.idch.afed.Facsimile#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String desc) {
        this.description = desc;
    }

    /** 
     * Returns the date this document is thought to have been created.
     * 
     * @see org.idch.afed.Facsimile#getDate()
     */
    @Override
    public String getDate() {
        return this.date;
    }

    /**
     * Sets the date that this documetn is thought to have been created. 
     * 
     * @see org.idch.afed.Facsimile#setDate(java.lang.String)
     */
    @Override
    public void setDate(String date) {
        this.date = date;
    }
    
    
    @Override 
    public DublinCore getDCMetadata() {
        return this.dc;
    }
    
    @Override
    public void setDCMetadata(DublinCore dc) {
        this.dc = dc;
    }
    
    // TODO Create a more robust date mechanism that supports fuzzy historical dates and
    //      associated supporting documentation.
    
    //==============================================================================
    // COLLATION METHODS
    //==============================================================================
    

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#listCollations()
     */
    @Override
    public Set<String> listCollations() {
        // NOTE: We are dynamically generating the set of collation names every time someone
        //       tries to access them. This is time consuming, however, collation looks will 
        //       be relatively rare and the number of collations relatively small, so the 
        //       penalty should be minimal. We can improve the performance later if needed. 
        
        Set<String> result = new HashSet<String>();
        
        for (Collation c : this.collations) {
            result.add(c.getName());
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#hasCollation(java.lang.String)
     */
    @Override
    public boolean hasCollation(String collationName) {
        return this.listCollations().contains(collationName);
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#getCollation(java.lang.String)
     */
    @Override
    public Collation getCollation(String collationName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#createCollation(java.lang.String)
     */
    public Collation createCollation(String collationName) {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#addImage(java.lang.String, java.io.InputStream, java.lang.String)
     */
    @Override
    public void addImage(String subContext, InputStream is, String type) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#listImages()
     */
    @Override
    public List<String> listImages() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#getDesignation(java.lang.String)
     */
    @Override
    public Designation getDesignation(String schema) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#hasDesignation(java.lang.String)
     */
    @Override
    public boolean hasDesignation(String schema) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#setDesignation(org.idch.ms.Designation)
     */
    @Override
    public void setDesignation(Designation d) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#listDesignations()
     */
    @Override
    public Set<Designation> listDesignations() {
        // TODO Auto-generated method stub
        return null;
    }

}
