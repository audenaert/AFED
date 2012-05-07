/**
 * 
 */
package org.idch.images;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Creates a derived or otherwise formatted representation of an image.
 *  
 * @author Neal Audenaert
 */
public interface ImageFormatter {

    /**
     * 
     * @return
     */
    public String getName(); 

    /**
     * 
     * @param src
     * @param store
     * @throws ImageProcessorException
     */
    public void process(BufferedImage src, ImageContext store) 
            throws ImageProcessorException;
    
    /**
     * 
     * @param param
     * @param value
     * @throws ImageProcessorException
     */
    public void setParameter(String param, String value) 
            throws ImageProcessorException;
    
    /**
     * 
     * @param params
     * @throws ImageProcessorException
     */
    public void config(Map<String, String> params)
            throws ImageProcessorException;
    
    //=========================================================================
    // EXCEPTION CLASSES
    //=========================================================================
    
    public static class ImageProcessorException extends RuntimeException {
        private static final long serialVersionUID = 4586688408522606109L;

        public static enum Type {
            PROCESSING_ERROR, 
            INVALID_CONFIGURATION, 
            INVALID_PARAMETER
        }
        
        private Type type;
        private String param = null;
        private String value = null;
        
        public ImageProcessorException(Type t, String msg) {
            super(msg);
        }
        
        public ImageProcessorException(String msg, String param, String value) {
            super(msg.replace("{param}", param).replace("{param}", "value"));
            
            type = Type.INVALID_PARAMETER;
            this.param = param;
            this.value = value;
        }
        
        public ImageProcessorException(Throwable t) {
            super(t);
            
            type = Type.PROCESSING_ERROR;
        }
        
        public ImageProcessorException(String msg, Throwable t) {
            super(msg, t);
        }
        
    }
    
}
