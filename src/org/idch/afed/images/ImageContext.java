/**
 * 
 */
package org.idch.afed.images;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Provides a lightweight wrapper around an <tt>ImageStore</tt> instance 
 * that interprets the resource paths relative to a root context. This 
 * prevents misbehaving <tt>Formatters</tt> from writing outside of their 
 * allowed context and simplifies access to image resources for a given
 * image. 
 *   
 * @author Neal Audenaert
 */
public class ImageContext implements ImageStore {
    private static final Logger LOGGER = Logger.getLogger(ImageContext.class);

    private final ImageStore delegate;
    private final String rootContext;
    
    public ImageContext(ImageStore delegate, String rootContext) {
        this.delegate = delegate;
        this.rootContext = rootContext;
    }
    
    /** 
     * Returns the indicated image.
     * @see org.idch.afed.images.ImageStore#get(java.lang.String)
     */
    @Override
    public BufferedImage get(String relPath) throws IOException {
        return delegate.get(path(relPath));
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageStore#exists(java.lang.String)
     */
    @Override
    public boolean exists(String relPath) {
        try {
            return delegate.exists(path(relPath));
        } catch (Exception ex) {
            LOGGER.error("Caught exception while checking for object existence.", ex);
        }
        
        return false;
    }

    /**
     * Stores the image under the supplied relative path. If null, this will
     * store the image at the rootContext for this image store.
     *  
     * @see org.idch.afed.images.ImageStore#store(java.lang.String, java.awt.image.BufferedImage)
     */
    @Override
    public void store(String relPath, BufferedImage image) throws IOException {
        delegate.store(path(relPath), image);
    }

    /**
     * @see org.idch.afed.images.ImageStore#store(java.lang.String, java.awt.image.BufferedImage, java.lang.String)
     */
    @Override
    public void store(String relPath, BufferedImage image, String format)
            throws IOException {
        delegate.store(path(relPath), image, format);
        
    }
    
    /** 
     * Translates a relative path into the path space of the delegate image store.
     * 
     * @param relPath The relative path for this 
     * @return
     * @throws IOException
     */
    private String path(String relPath) throws IOException {
        checkPath(relPath);
        return rootContext + (relPath != null ? relPath : "");
    }
    
    /** 
     * Tests the relPath supplied by a formatter to determine if it is valid.
     * This can be implemented (typically by an anonymous inner class) to 
     * provide custom checking for legitimate values. This will throw an
     * <tt>IOException if the supplied relative path is not valid for this 
     * context. 
     * 
     *  @param relPath The relative path at which an image is to be
     *      stored.
     *  @throws IOException if the supplied path is not allowed.
     * */
    protected void checkPath(String relPath) throws IOException {
        if (relPath.contains("..")) {
            // TODO what other security mechanisms do we need?
            throw new IOException(
                    "References to parent directories ('..') are not allowed.");
        }
    }

    /** 
     * Throws a runtime exception. Clients should call connect and close on the 
     * underlying image store instance directly. 
     * 
     * @see org.idch.afed.images.ImageStore#connect()
     */
    @Override
    public void connect() {
        throw new RuntimeException("Attempted to coonnect ImageContext object. " +
                "Use the underlying ImageStore instance directly.");
    }

    /**
     * Throws a runtime exception. Clients should call connect and close on the 
     * underlying image store instance directly. 
     * 
     * @see org.idch.afed.images.ImageStore#close()
     */
    @Override
    public void close() {
        throw new RuntimeException("Attempted to close ImageContext object. " +
                "Use the underlying ImageStore instance directly.");
    }

}
