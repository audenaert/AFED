/* Created on       Oct 2, 2007
 * Last Modified on $Date: 2008-07-17 19:08:25 $
 * $Revision: 1.1 $
 * $Log: ZoomableImage.java,v $
 * Revision 1.1  2008-07-17 19:08:25  neal
 * Reattached NADL Project to a CVS Repository. This time the HTML, JS, and other webcomponents are being backed up as well.
 *
 * Revision 1.1  2007-11-08 15:39:19  neal
 * Creating a general project to provide a consistent codebase for NADL. This is being expanded to include most of the components from the old CSDLCommon and CSDLWeb packages, as I reorganize the package structure and improve those components.
 *
 * 
 * Copyright TEES Center for the Study of Digital Libraries (CSDL),
 *           Neal Audenaert
 *
 * ALL RIGHTS RESERVED. PERMISSION TO USE THIS SOFTWARE MAY BE GRANTED 
 * TO INDIVIDUALS OR ORGANIZATIONS ON A CASE BY CASE BASIS. FOR MORE 
 * INFORMATION PLEASE CONTACT THE DIRECTOR OF THE CSDL. IN THE EVENT 
 * THAT SUCH PERMISSION IS GIVEN IT SHOULD BE UNDERSTOOD THAT THIS 
 * SOFTWARE IS PROVIDED ON AN AS IS BASIS. THIS CODE HAS BEEN DEVELOPED 
 * FOR USE WITHIN A PARTICULAR RESEARCH PROJECT AND NO CLAIM IS MADE AS 
 * TO IS CORRECTNESS, PERFORMANCE, OR SUITABILITY FOR ANY USE.
 */
package org.idch.tzivi.legacy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;

import org.idch.images.dz.TziConfig;
import org.idch.images.dz.TziInfo;
import org.idch.util.xml.XMLUtil;
import org.w3c.dom.Document;

import org.idch.util.Cache;
import org.idch.util.LogService;

/**
 * Represents a tiled, zoomable image (TZI) and provides methods for 
 * retrieving tiles from that image and information about the image.
 * 
 * @author Neal Audenaert
 */
public class ZoomableImage {
    private static final String logger = ZoomableImage.class.getName();
    
    /* ********************************************************************** *
     *                        STATIC MEMBERS                                  *
     * ********************************************************************** */
    private static final Cache<String, ZoomableImage> cache = 
        new Cache<String, ZoomableImage>("Zoomable Images", 25);
    
/* ************************************************************************* *
 *                        SYMBOLIC CONSTANTS                                 *
 * ************************************************************************* */
    
    /** Configuration parameter used to specify the width of tiles in a tzi. */
    public static final String TILE_WIDTH    = "tw"; 
    
    /** Configuration parameter used to specify the height of tiles in a tzi. */
    public static final String TILE_HEIGHT   = "th";
    
    /** 
     * Configuration parameter used to specify the maximum width of the 
     * smallest layer in the tzi. The actual width may be less than this
     * value in order to ensure that the constraint specified by 
     * <tt>MIN_HEIGHT</tt> is also met. If a value is specified for 
     * <tt>MIN_HEIGHT</tt> but not for <tt>MIN_WIDTH</tt>, the width of the 
     * smallest layer will be unconstrained (that is, the smallest layer 
     * will have the height specified by <tt>MIN_HEIGHT</tt>, and a width that
     * matches the aspect ratio of the original image. If no value is provided 
     * for either <tt>MIN_HEIGHT</tt> or <tt>MIN_WIDTH</tt> the default values
     * will be used. If a value is provided for <tt>SCALE</tt>, the 
     * <tt>MIN_HEIGHT</tt> and <tt>MIN_WIDTH</tt> will be ignored. */
    public static final String MIN_WIDTH     = "minWidth";
    
    /** 
     * Configuration parameter used to specify the maximum height of the 
     * smallest layer in the tzi. The actual height may be less than this
     * value in order to ensure that the constraint specified by 
     * <tt>MIN_WIDTH</tt> is also met. If a value is specified for 
     * <tt>MIN_WIDTH</tt> but not for <tt>MIN_HEIGHT</tt>, the height of the 
     * smallest layer will be unconstrained (that is, the smallest layer 
     * will have the width specified by <tt>MIN_WIDTH</tt>, and a height that
     * matches the aspect ratio of the original image. If no value is provided 
     * for either <tt>MIN_WIDTH</tt> or <tt>MIN_HEIGHT</tt> the default values
     * will be used. If a value is provided for <tt>SCALE</tt>, the 
     * <tt>MIN_HEIGHT</tt> and <tt>MIN_WIDTH</tt> will be ignored. */
    public static final String MIN_HEIGHT    = "minHeight";
    
    /** 
     * Specifies the size of the smallest layer as a percentage of the 
     * original image size. If specified, this value will be used instead of 
     * the values provided for <tt>MIN_HEIGHT</tt> and <tt>MIN_WIDTH</tt>. */
    public static final String SCALE         = "scale";
    
    /**
     * The percentage increase between layers. For example, if a step size of 
     * 0.5 is provided, layer 2 will be 50% larger than layer 1. */
    public static final String STEP_SIZE     = "stepSize";
    
    /** The name of the file in which tzi format information is stored. */
    private static final String INFO_FILE = "info.xml";

/* ************************************************************************* *
 *                        STATIC METHODS                                     *
 * ************************************************************************* */
    
    /** 
     * Creates a new TZI from the provided image and stores the TZI in 
     * the specified destination file. If the destination file already exists
     * it will be overwritten.
     * 
     * @param image The source image from which to create the TZI.
     * @param dest The file to which the TZI should be written. By convention, 
     *      this file should have the extension '.tzi'.
     * @param params Parameters describing how the file should be created. 
     *      If parameters are not provided, the default values will be used.
     *      For more information, see the symbolic constants associated with 
     *      this class.
     *      
     * @return the created <tt>ZoomableImage</tt> or null if the image could 
     *      not be created. 
     */
    public static ZoomableImage create(BufferedImage image, File dest, 
            Map<String, String> params) {
        ZoomableImage tzi = null;
        
        try {
            TziConfig config = new TziConfig(params);
            TziviCreator creator = new TziviCreator(image, dest, config);
            creator.process();
            tzi = getZoomableImage(dest);
        } catch (Exception ex) {
            String msg = "An error occured while attempting to create a " +
                    "zoomable image";
            LogService.logError(msg, logger, ex);
        }
        
        return tzi;
    }
    
    /** 
     * Retrieves a <tt>ZoomableImage</tt> object for the zoomable image
     * stored in the specified file. Returns null if the image could not 
     * be retrieved.
     * 
     * @param file A file that references a <tt>ZoomableImage</tt>. 
     */
    public static ZoomableImage getZoomableImage(File file) {
        ZoomableImage zimage = null;

        // sanity checks, make sure the file exists.
        if ((file == null) || !file.exists() || !file.canRead()) {
            String msg = "Could not retrieve zoomable image. " +
                    "Invalid file: " + file; 
            if (!file.exists()) msg += ": Does not exist.";
            if (!file.canRead()) msg += ": Cannot read.";
            LogService.logWarn(msg, logger);
            return zimage;
        }
        
        String path = file.getAbsolutePath();
        synchronized (cache) {
            zimage = cache.get(path);
            if (zimage == null) {
                try { 
                    zimage = new ZoomableImage(file);
                } catch (IOException ioe) {
                    String msg = "Could not retrieve zoomable image " + path;
                    LogService.logError(msg, logger, ioe);
                    zimage = null;
                }
            }
        }
        
        return zimage;
    }
    
    /**
     *  Returns the filename (or more accurately, the <tt>ZipEntry</tt> 
     *  of the tile at the speicifed layer, row, col triple.  
     */ 
    static String getTileFilename(int layer, int row, int col) {
        return layer + "-" + row + "-" + col + ".jpg";
    }
    
/* ************************************************************************* *
 *                        PRIVATE MEMBERS                                    *
 * ************************************************************************* */
    
    private File         imagefile = null;
    private ZipFile      zimage    = null;
    private TziInfo info      = null;
    
/* ************************************************************************* *
 *                        INSTANCE METHODS                                   *
 * ************************************************************************* */
    /** Loads a new tzi from the specified file. */
    public ZoomableImage(File file) throws IOException {
        this.imagefile = file;
        this.zimage = new ZipFile(this.imagefile, ZipFile.OPEN_READ);
        
        ZipEntry info = this.zimage.getEntry(INFO_FILE);
        InputStream is = this.zimage.getInputStream(info);
        Document doc = XMLUtil.getXmlDocument(is);
        this.info = new TziInfo(doc);
    }
    
    /** Releases the resources associated with this TZI. */
    public void finalize() {
        if (this.zimage != null) {
            try { this.zimage.close();
            } catch (IOException ioe ) {
                String msg = "Could not close zip file";
                LogService.logError(msg, logger, ioe);
            }
        }
    }
    
    /**
     * USE WITH CARE Returns an <tt>InputStream</tt> representing the 
     * specified tile. The caller is responsible for closing this resource.
     * FAILURE TO CLOSE THIS <tt>ImageStream</tt> WILL RESULT IN A RESOURCE 
     * LEAK. 
     * 
     * @param layer The layer of the image from which the tile should be 
     *      retrieved.
     * @param row The row of the tile to be retrieved.
     * @param col The column of the tile to be retrieved.
     * 
     * @return An <tt>InputStream</tt> containing the image data for the 
     *      specified tile.
     *       
     * @throws IOException If there are problems obtaining the 
     *      <tt>InputStream</tt>.
     */
    public InputStream getInputStream(int layer, int row, int col) 
        throws IOException {
        if (!tileExists(layer, row, col)) return null;
        
        String fname = getTileFilename(layer, row, col);
        ZipEntry entry = this.zimage.getEntry(fname);
        return this.zimage.getInputStream(entry);
    }
    
    /**
     * Returns the specified tile. 
     * 
     * @param layer The layer of the image from which the tile should be 
     *      retrieved.
     * @param row The row of the tile to be retrieved.
     * @param col The column of the tile to be retrieved.
     * 
     * @return A <tt>PlanarImage</tt> containing the specified tile.
     */
    public BufferedImage get(int layer, int row, int col) {
        BufferedImage image = null; 
        InputStream is    = null;
        
        if (!tileExists(layer, row, col)) return null;
        try {
            is = this.getInputStream(layer, row, col);
            if (is != null) {
                image = ImageIO.read(is);
                is.close();         // TODO validate that the stream is no longer needed.
            }
        } catch (IOException ioe) {
            LogService.logError("Could not load input stream for tile: " +
                    getTileFilename(layer, row, col), logger, ioe);
            
            if (is != null) { 
                try { is.close(); }
                catch (IOException ex) { /* nothing to do. */ }
            }
            
            is = null;
            image = null;
        }
        
        return image;
    }
    
    /** Returns descrptive information about this tzi. */
    public TziInfo getInfo() { return this.info; }
    
    /** Indicates whether the specified tile exists. */
    public boolean tileExists(int layer, int row, int col) {
        return ((row >= 0) && (row < this.info.getRows(layer)) &&
                (col >= 0) && (col < this.info.getCols(layer)));
                
    }
}
