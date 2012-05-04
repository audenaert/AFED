/**
 * 
 */
package org.idch.afed;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Neal Audenaert
 */
public interface Feature {
    // TODO support non-contiguous annotations
    //      support transcriptions
    //      support cross references
    //      support page references (anchor to simple shapes on the page)
    //      read van der Somple paper and figure out what is applicable.
    
    /**
     * Returns 
     * @return
     */
    public String getType();
    
    public Collation getCollation();
    
    public int getStartIndex();
    public Image getStartImage();
    
    public int getEndIndex();
    
    public Image getEndImage();
    
    public String getCanonicalName();
    
    public String getDisplayName();
    
    public Map<String, String> getProperties();
    
    public String hasProperty(String prop);
    
    public String getProperty(String prop);
    
    public void setProperty(String prop, String name);
    
    public Feature getParent();
    
    public List<Feature> getChildren();
    
    /** 
     * Returns an iterator that operates over all of the images denoted by this annotation.
     *  
     * @return
     */
    public Iterator<Image> getIterator();

}
