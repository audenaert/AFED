/**
 * 
 */
package org.idch.afed.legacy;

import java.util.List;

import org.idch.afed.Image;

/**
 * @author Neal Audenaert
 */
public interface CollationDelegate extends List<Image> {
    
    public String getName();
    
    public void setName(String name);
    
    public String getDescription();
    
    public void setDescription(String desc);

}
