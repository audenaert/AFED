/**
 * 
 */
package org.idch.afed.images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.idch.util.Scalr;
import org.idch.util.Scalr.Method;

/** 
 * Creates scaled representations of an image that fit inside a bounding box.
 * 
 * @author Neal Audenaert
 */
public class ScaledImageFormatter implements ImageFormatter {
    // TODO Use Jackson to implement JSON based configuration
    //      Add setters for name
    public static enum PARAMS {
        w, h, expandable
    }
    
    private String name;
    private int width = 800;
    private int height = 800;
    private boolean expandable = false;
    
    public ScaledImageFormatter() {
        
    }
    
    public ScaledImageFormatter(String name, int w, int h) {
        this.name = name;
        this.width = w;
        this.height = h;
        this.expandable = false;
    }

    /** 
     * The maximum width of the resulting image. The image will be 
     * scaled proportionally so that it is no wider than this.
     * 
     * @return the maximum width 
     */
    public int getWidth() {
        return this.width;
    }
    
    /** 
     * The maximum height of the resulting image. The image will be 
     * scaled proportionally so that it is no taller than this.
     * 
     * @return the maximum height
     */
    public int getHeight() {
        return this.height;
    }
    
    /**
     * Whether this image should be made bigger (with possible loss of quality) 
     * to maximize its size if it is already smaller than the max width and 
     * height defined for this processor.  
     *  
     * @return <tt>true</tt> if the image's size should be expanded.
     */
    public boolean isExpandable() {
        return this.expandable;
    }
    
    //=========================================================================
    // MUTATORS
    //=========================================================================
    
    /**
     * 
     * @param w
     */
    public void setWidth(int w) {
        this.width = w;
    }
    
    void setWidth(String w) throws ImageProcessorException {
        String emsg = "Supplied {param} is not an integer: {value}";
        if (!StringUtils.isNumeric(w)) {
            throw new ImageProcessorException(emsg, PARAMS.w.toString(), w);
        }
        
        this.width = Integer.parseInt(w);
    }
    
    public void setHeight(int h) {
        this.height = h;
    }
    
    void setHeight(String h) throws ImageProcessorException {
        String emsg = "Supplied {param} is not an integer: {value}";
        if (!StringUtils.isNumeric(h)) {
            throw new ImageProcessorException(emsg, PARAMS.h.toString(), h);
        }
        
        this.height = Integer.parseInt(h);
    }
    
    public void setExpandable(boolean flag) {
        this.expandable = flag;
    }

    public void setExpandable(String value) {
        String emsg = "Could not set {param}. Expected 'true' or 'false' but found '{value}'";
        if (value.equalsIgnoreCase("true")) {
            this.expandable = true;
        } else if (value.equalsIgnoreCase("false")) {
            this.expandable = false;
        } else {
            throw new ImageProcessorException(
                    emsg, PARAMS.expandable.toString(), value);
        }
        
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageProcessor#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageProcessor#process()
     */
    @Override
    public void process(BufferedImage src, ImageContext store) 
            throws ImageProcessorException {
        try {
            // TODO add configure levers to adjust quality and anti-aliasing or not.
            BufferedImage scaledImg = Scalr.resize(src, Method.QUALITY, 
                    this.width, this.height);
            store.store(this.name + ".jpg", scaledImg, "jpg");
            scaledImg.flush();      // make it easier for the GC to free this memory
        } catch (IOException ioe) {
            throw new  ImageProcessorException(ioe);
        } catch (Exception e) {
            String msg = "Could not scale image to fit in " +
                    this.width + " x " + this.height + ": " + e.getLocalizedMessage();
            throw new  ImageProcessorException(msg, e);
        }
    }

    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageProcessor#setParameter(java.lang.String, java.lang.String)
     */
    @Override
    public void setParameter(String param, String value) 
            throws ImageProcessorException {
        String emsg = "Unrecognized parameter: {param}";
        
        switch (PARAMS.valueOf(param)) {
        case w: 
            this.setWidth(value); 
            break;
        case h: 
            this.setHeight(value); 
            break;
        case expandable:
            this.setExpandable(value); 
            break;
        default:
            throw new ImageProcessorException(emsg, param, value);
        }
    }

    /* (non-Javadoc)
     * @see org.idch.afed.images.ImageProcessor#config()
     */
    @Override
    public void config(Map<String, String> params) throws ImageProcessorException {
        for (String key : params.keySet()) {
            if (PARAMS.valueOf(key) != null) {
                this.setParameter(key, params.get(key));
            }
        }
        
    }

}
