/* Created on       July 5, 2010
 * Author: Neal Audenaert (neal@idch.org)
 * 
 * Last Modified on $Date: $
 * $Revision: $
 * $Log:  $
 *  
 * Copyright Institute for Digital Christian Heritage (IDCH) 
 *           All Rights Reserved.
 */
package org.idch.tzivi;

import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.idch.afed.images.ImageStore;
import org.idch.util.LogService;

/** 
 * Represents configuration information for specifying the paramaters 
 * (e.g., tiles size, size of the smallest layer, porportion of increase
 * between layers) to be used when creating a new tzi.
 * 
 * @author Neal Audenaert
 */
public class TziConfig {
    private static final String LOGGER = TziConfig.class.getName();
    
//==========================================================================   
// STATIC METHODS
//========================================================================== 
       
    public static final String NAME         = "name";
    
    public static final String MIN_WIDTH    = "min-width";
    public static final String MIN_HEIGHT   = "min-height";
    public static final String SCALE        = "min-scale";
    public static final String STEPSIZE     = "stepsize";
    
    public static final String TILE_WIDTH   = "tile-width";
    public static final String TILE_HEIGHT  = "tile-height";
    
    public static final String NO_THUMB     = "no-thumb";
    public static final String THUMB_WIDTH  = "thumb-width";
    public static final String THUMB_HEIGHT = "thumb-height";
    
    public static final String FORMAT       = "format";
    
//==========================================================================   
// CONFIG PROPERTIES
//========================================================================== 
    
    String name      = "unknown"; /** The name of this image. */
    int    tWidth    = 256;  /** Width of each tile. Default is 256 pixels. */
    int    tHeight   = 256;  /** Height of each tile. Default is 256 pixels. */
    
    /** Width of the smallest layer. Default is 256 pixels. */
    int    minWidth  = 256;  

    /** Height of the smallest layer. Default is 256 pixels. */
    int    minHeight = 256;
    
    /** Scale of the smallest layer, specified as a percentage of the source
     *  iamge. Default is 10%. If this value is less than or equal 0, the 
     *  <code>minWidth</code> and <code>minHeight</code> should be used to 
     *  determine the scale. */
    float  minScale     = 0F; 
    
    /** Scale of the largest layer, specified as a percentage of the source
     *  iamge. Default is 100%. This must be greater than 0. Not all creator's 
     *  use this value. */
    float  maxScale     = 1F; 
    
    /** Change in size between new layers. Default is that each layer is 
     *  50% larger than the last. Must be greater than 0. */
    double stepSize  = 0.5;     
    
    /** The format to be used for storing image tiles. Must be one of the 
     *  image formats defined by <code>org.idch.images.ImageUtils</code>. */
    String format = ImageStore.JPEG;
    
    /** Specifies the width of the generated thumbnail image. Default value is
     *  150. */
    int thumbWidth = 150;
    
    /** Specifies the height of the generated thumbnail image. Default value is 
     *  150. */
    int thumbHeight = 150;
    
    /** Indicates whether a thumbnail image should be generated. 
     *  <code>true</code> by default. */
    boolean generateThumb = true;
    
//==========================================================================   
// INSTANCE METHODS AND CONSTRUCTORS
//==========================================================================  
    
    /** Initializes configuration information to reasonable default values. */
    public TziConfig() { /* use default values */ }
    
    /** 
     * Creates a new config by specifying a square tile size, the scale
     * of the smallest layer, and the percentage increase between layers.  
     */
    public TziConfig(int tilesize, float scale, double stepsize) {
        this.tHeight = tilesize;
        this.tWidth  = tilesize;
        
        this.minScale = scale;
        this.stepSize = stepsize;
    }
    
    /** 
     * Initializes the configuration information based on the provided 
     * congiguration parameters. Configuration parameters should be 
     * specified using the symbolic constants defined by the 
     * <tt>ZoomabeImage</tt> class.
     * 
     * @param params Configuration parameters specified according the 
     *      symbolic constants defined by the <tt>ZoomabeImage</tt> class.
     *      
     * @throws ConfigurationException If the configuration parameters 
     *      are not valid. This most often happens when a non-numeric value
     *      is supplied for a numeric field.
     */
    public TziConfig(Map<String, String> params) throws ConfigurationException {
     // extract info for smallest layer
        if (params.containsKey(NAME)) this.name = params.get(NAME);
        
        processTileSize(params);
        processScale(params);
        processSizeConstraints(params);
        processStepSize(params);
        processThumbnail(params);
        processFormat(params);
        
//        this.tWidth    = getInteger(params, ZoomableImage.TILE_WIDTH,  256);
//        this.tHeight   = getInteger(params, ZoomableImage.TILE_HEIGHT, 256);
//        this.stepSize  = getDouble(params, ZoomableImage.STEP_SIZE,    0.5);
//        
//        this.minWidth  = getInteger(params, ZoomableImage.MIN_WIDTH,   256);
//        this.minHeight = getInteger(params, ZoomableImage.MIN_HEIGHT,  256);
//        
//        this.minScale     = (float)getDouble(params, ZoomableImage.SCALE, 0);
//        if ((this.minScale > 1) || (this.minScale < 0)) {
//            LogService.logWarn("Invalid scale for the smallest layer ( " + 
//                    this.minScale + "). Must be between 0 and 1", TziviCreator.logger);
//            this.minScale = 0;
//        } // possibly reset min width and height
    }
    
    
    public void processTileSize(Map<String, String> params) {
        // extract infor for tile sizes
        String tw  = params.get(TILE_WIDTH);
        String th  = params.get(TILE_HEIGHT);
        
        if (StringUtils.isNumeric(tw)) {
            this.tWidth  = Integer.parseInt(tw);
            
            if (!StringUtils.isNumeric(th)) {
                String msg = "Warning: No value supplied for 'tile-height'. " +
                    "Fearlessly pressing on.";
                LogService.logWarn(msg, LOGGER);
            }
        }
        
        if (StringUtils.isNumeric(th)) { 
            this.tHeight = Integer.parseInt(th);
            
            if (!StringUtils.isNumeric(th)) {
                String msg = "Warning: No value supplied for 'tile-width'. " +
                		"Fearlessly pressing on.";
                LogService.logWarn(msg, LOGGER);
            }
        }
    }
    
    /** 
     * Processes the scale of the smallest layer, if defined in the 
     * configuration parameters. By default, the size constaints (max width
     * and height for the smallest layer) are used instead of the scale.
     * 
     * @param params
     * @throws ConfigurationException
     */
    private void processScale(Map<String, String> params) 
        throws ConfigurationException {
        
        String minscale = params.get(SCALE);
        if (StringUtils.isBlank(minscale)) return;
        
        try {
            this.minScale = Float.parseFloat(minscale);
        } catch (NumberFormatException nfe) {
            String msg = "Invalid value for 'scale' (" + minscale + "). " +
                    "Expected a number.";
            
            LogService.logWarn(msg, LOGGER);
            throw new ConfigurationException(msg);
        }
    }
    
    /** 
     * Processes the size constraints (the maximum width and height) of the 
     * smallest layer. These values are used to determine the size of the 
     * smallest layer unless the scale is specified.
     *  
     * @param params
     * @throws ConfigurationException If a bad (i.e. non-numeric) parameter is
     *      specified for either the width or the height.
     */
    private void processSizeConstraints(Map<String, String> params) 
        throws ConfigurationException {
        
        String minwidth  = params.get(MIN_WIDTH);
        String minheight = params.get(MIN_HEIGHT);
        
        // set the maximum width of the smallest layer
        if (StringUtils.isNumeric(minwidth)) {
            this.minWidth = Integer.parseInt(minwidth);
        } else if (StringUtils.isNumeric(minheight)) {
            // if the height is specified, but not the width, remove 
            // the constraint on the width, otherwise, leave the default.
            this.minWidth  = 0;
        }
        
        // set the maximum height of the smallest layer
        if (StringUtils.isNumeric(minheight)) {
            this.minHeight = Integer.parseInt(minheight);
        } else if (StringUtils.isNumeric(minwidth)) {
            // if the width is specified, but not the height, remove 
            // the constraint on the height, otherwise, leave the default.
            this.minHeight  = 0;
        }
        
        // XXX test for bad parameters
    }
    
    /** 
     * Sets the size increase between each layer from the smallest to the 
     * largest layer.
     * 
     * @param params
     * @throws ConfigurationException
     */
    private void processStepSize(Map<String, String> params) 
        throws ConfigurationException {
        String msg = null;
        String step = params.get(STEPSIZE);
        if (!StringUtils.isBlank(step)) {
            try {
                this.stepSize = Double.parseDouble(step);
            } catch (NumberFormatException nfe) {
                msg = "Invalid value for parameter 'step-size'" +
                    " (" + step + "). Expected a number.";
                
                LogService.logWarn(msg, LOGGER);
                throw new ConfigurationException(msg);
            }
        }
    }
    
    public void processThumbnail(Map<String, String> params) 
        throws ConfigurationException {
        
        if (params.containsKey(NO_THUMB) && 
            params.get(NO_THUMB).equalsIgnoreCase("true")) {
            
            this.generateThumb = false;
        }
        
        String msg = null;
        String thumbwidth  = params.get(THUMB_WIDTH);
        String thumbheight = params.get(THUMB_HEIGHT);
        
        // set the maximum width of the smallest layer
        if (StringUtils.isNumeric(thumbwidth)) {
            this.thumbWidth = Integer.parseInt(thumbwidth);
        } else {
            this.thumbWidth  = 150;
            if (!StringUtils.isBlank(thumbwidth)) {
                msg = "Invalid value for 'thumb-width' (" + thumbwidth + "). " +
                        "Expected a number. ";

                LogService.logWarn(msg, LOGGER);
                throw new ConfigurationException(msg);
            }
        }
        
        // set the height of the thumbnail image
        if (StringUtils.isNumeric(thumbheight)) {
            this.thumbHeight = Integer.parseInt(thumbheight);
        } else {
            this.thumbHeight  = 150;
            if (!StringUtils.isBlank(thumbheight)) {
                msg = "Invalid value for 'thumb-height' (" + thumbheight + "). " +
                        "Expected a number. ";

                LogService.logWarn(msg, LOGGER);
                throw new ConfigurationException(msg);
            }
        }
    }
    
    private boolean isFormatValid(String fmt) {
        for (String f : ImageIO.getReaderFormatNames()) {
            if (f.equals(fmt)) { 
                return true;
            }
        }
        return false;
    }
    
    public void processFormat(Map<String, String> params) 
        throws ConfigurationException {
        
        if (!params.containsKey(FORMAT)) return;        // use default value
        
        String fmt = params.get(FORMAT);
        if (!isFormatValid(fmt)) {
            String msg = "Unrecognized image format for tiles: " + fmt;
            
            LogService.logWarn(msg, LOGGER);
            throw new ConfigurationException(msg);
        }
        
        this.format = fmt;
    }
    
    /**
     * Checks the seetings of this config to ensure that the current values are 
     * valid. If the values are not valid, this returns an error message. 
     * Otherwise it ruturns null. 
     * 
     * @returns An error message if the config values are not valid or null if 
     *      they are valid.
     */
    public String testConfig() {
        String result = null;

        if (minScale > 1) {
            result = "Minimum scale must be less than 1. " +
            		"Current value: " + minScale;
        }
        
        if (maxScale <= 0) {
            if (result != null) result += "\n";
            else                result  = "";
            result += "Maximum scale must be greater than 0. " +
                    "Current value: " + minScale;
        }
        
        if ((this.minScale <= 0) && 
                (this.minWidth <= 0) && (this.minHeight <= 0)) {
            if (result != null) result += "\n";
            else                result  = "";
            
            result += "Smallest layer is not specified. At least one of the " +
            "following must be set to a positive number: 'scale', " +
            "'min-width', 'min-height'.";
        }
        
        if (stepSize <= 0) {
            if (result != null) result += "\n";
            else                result  = "";
            result += "Stepsize must be greater than 0. " +
                    "Current value: " + stepSize;
        }
        
            
        if (format == null) {
            if (result != null) result += "\n";
            else                result  = "";
            result += "Image format not recognized: " + format;
        }
        
        return result;
    }
    
    /**
     * Checks the seetings of this config to ensure that the current values are 
     * valid. If the values are not valid, throws a 
     * <code>ConfigurationException</code>.
     * 
     * @throws ConfigurationException If this config object's values are not 
     *      valid.
     */
    public void checkConstraints() throws ConfigurationException {
        String err = testConfig();
        assert (err == null) : err;
        if (err != null) throw new ConfigurationException(err);
    }
    
    

    /** 
     * Returns a string form of the configuration information suitable 
     * for display to a user.
     */
    public String toString() {
        String result = "";
        result += "TZIVI CONFIGURATION INFORMATION\n" +
                  "Name:           " + this.name    + "\n" +
                  "Tile Width:     " + this.tWidth  + "\n" +
                  "Tile Height:    " + this.tHeight + "\n";
        if (minScale > 0) {
            result += "Smallest Layer: " + (this.minScale * 100) + "% of original\n";
        } else {
            result += "Smallest Layer: " + 
                this.minWidth + " x " + this.minHeight + "\n";
        }
        
        if (this.generateThumb) {
            result += "Thumbnail Size: " + 
                this.thumbWidth + " x " + this.thumbHeight + "\n";
        }
        
        result += "Step Size:      " + this.stepSize + "\n";
        result += "Format:         " + this.format + "\n";
        return result;
    }
    
    /** 
     * Simple exception class to represent errors arising while attempting 
     * to specify configuration information. 
     * 
     * @author Neal Audenaert
     */
    @SuppressWarnings("serial") 
    public class ConfigurationException extends Exception {
        ConfigurationException(String msg) { super(msg); }
    }
}