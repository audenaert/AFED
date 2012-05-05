/**
 * 
 */
package org.idch.afed.images;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Neal Audenaert
 */
public class S3ImageStore implements ImageStore {

    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageStore#exists(java.lang.String)
     */
    @Override
    public boolean exists(String relPath) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageStore#get(java.lang.String)
     */
    @Override
    public BufferedImage get(String relPath) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageStore#store(java.lang.String, java.awt.image.BufferedImage)
     */
    @Override
    public void store(String relPath, BufferedImage image) throws IOException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageStore#store(java.lang.String, java.awt.image.BufferedImage, java.lang.String)
     */
    @Override
    public void store(String relPath, BufferedImage image, String format)
            throws IOException {
        // TODO Auto-generated method stub
        
    }
    

    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageStore#connect()
     */
    @Override
    public void connect() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageStore#close()
     */
    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageStore#isConnected()
     */
    @Override
    public boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageStore#isClosed()
     */
    @Override
    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    

}
