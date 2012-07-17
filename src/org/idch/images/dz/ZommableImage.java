/**
 * 
 */
package org.idch.images.dz;

import java.awt.image.BufferedImage;

/**
 * @author Neal Audenaert
 */
public interface ZommableImage {
    
    public String getName();
    
    public int[] getTileSize();
    
    public int getNumberOfLayers();
    
    public ZoomLayer getLayer(int layerId);
    
    public ZoomLayer getLayer(double ratio);

    public BufferedImage getThumbnail();
    
    public BufferedImage getTile(int layerId, int r, int c);
    

}
