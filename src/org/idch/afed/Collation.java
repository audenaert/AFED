/**
 * 
 */
package org.idch.afed;

import java.util.List;

/**
 * @author Neal Audenaert
 */
public interface Collation extends List<Image> {
    
    public String getName();
    
    public void setName(String name);
    
    public String getDescription();
    
    public void setDescription(String desc);

}
