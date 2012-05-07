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
public interface FacsimileDelegate {

    /**
     * Returns a unique identifier for this Facsimile according to a 
     * TODO migrate in previous work on MS Designations
     * 
     * @param schema
     * @return
     */
    public Designation getDesignation(String schema);
    
    public boolean hasDesignation(String schema);
    
    public Designation putDesignation(Designation d);
    
    public Designation removeDesignation(String scheme);
    
    public Set<Designation> getDesignations();
    
    public String getName();
    
    public void setName(String name);
    
    public String getDescription();
    
    public void setDescription(String desc);
    
    public String getDateOfOrigin();
    
    public void setDateOfOrigin(String date);
    
    public DublinCore getDCMetadata();
    
    //==============================================================================
    // COLLATION METHODS
    //==============================================================================
    
    /**
     * 
     * @return
     */
    public Set<String> listCollations();
    
    public boolean hasCollation(String collationName);
    
    public Collation getCollation(String collationName);
    
    public Collation createCollation(String collationName);
    
    //==============================================================================
    // IMAGE METHODS
    //==============================================================================
    
    public Image addImage(String subContext, InputStream is, String type);
    
    public List<String> listImages();
    
    //==============================================================================
    // PERSISTENCE METHODS
    //==============================================================================
    
    public boolean save();
    
    public boolean remove();
}

