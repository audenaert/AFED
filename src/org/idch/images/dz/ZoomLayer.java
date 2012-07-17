/**
 * 
 */
package org.idch.images.dz;

import java.awt.image.BufferedImage;

/**
 * @author Neal Audenaert
 */
public interface ZoomLayer {

    public int getLayerId();
    
    public int getWidth();
    
    public int getHeight();
    
    public int getNumberOfRows();
    
    public int getNumberOfColumns();
    
    public double getZoomRatio();
    
    public BufferedImage getTile(int r, int c);
}
