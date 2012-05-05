/**
 * 
 */
package org.idch.afed.images;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Neal Audenaert
 */
public interface ImageStore {
    
    public void connect();
    
    public void close();
    
    public void store(String relPath, BufferedImage image) throws IOException;
    
    public void store(String relPath, BufferedImage image, String format) throws IOException;
    
    public boolean exists(String relPath);
    
    public BufferedImage get(String relPath) throws IOException;
        
}
