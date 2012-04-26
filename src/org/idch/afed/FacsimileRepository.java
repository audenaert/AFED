/**
 * 
 */
package org.idch.afed;

import java.util.List;
import java.util.Map;

/**
 * @author Neal Audenaert
 */
public interface FacsimileRepository {
    
    public Facsimile getFacsimile(String scheme);
    
    public Map<String, String> listFacsimiles();
    
    public List<String> listFacsimiles(String scheme);
    
    // TODO need a generic way of searching

}
