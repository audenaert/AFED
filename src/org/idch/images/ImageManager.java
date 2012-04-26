/**
 * 
 */
package org.idch.images;

import java.io.InputStream;
import java.util.List;

/**
 * @author Neal Audenaert
 */
public interface ImageManager {
    
    public List<String> listNamedFormats();
    
    public NamedFormat getNamedFormat(String fmt);
    
    public void createImage(String ctx, InputStream is, String type);
    
    public List<String> listImages();

}
