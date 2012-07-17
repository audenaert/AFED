/**
 * 
 */
package org.idch.afed;

import java.net.URI;


/**
 * @author Neal Audenaert
 */
public interface Facsimile {
    
    public String getId();
    
    public String getName();
    
    public String getDescription();
    
    public String getDateOfOrigin();

    /**
     * @return
     */
    URI getDisplayImage();
    
//    public Designation getDesignation(String schema);
//    
//    public boolean hasDesignation(String schema);
//    
//    public Set<Designation> getDesignations();
//    
//    // FIXME we need a more robust model of metadat
//    public DublinCore getDCMetadata();
//  
//    public Iterable<Collation> listCollations();
    
//    public boolean hasCollation(String collationName);
//    
//    public Collation getCollation(String collationName);
    
//    /**
//     * 
//     * @return
//     */
//    public Iterable<Image> listImages();
    
    

}
