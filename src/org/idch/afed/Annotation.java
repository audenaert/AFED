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
public interface Annotation {
    // TODO support non-contiguous annotations
    //      support transcriptions
    //      support cross references
    
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
    
    public Annotation getParent();
    
    public List<Annotation> getChildren();
    
    /** 
     * Returns an iterator that operates over all of the images denoted by this annotation.
     *  
     * @return
     */
    public Iterator<Image> getIterator();

}
