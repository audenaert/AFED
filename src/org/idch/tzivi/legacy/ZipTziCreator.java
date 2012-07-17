/* Created on       Oct 2, 2007
 * Last Modified on $Date: 2008-07-17 19:08:24 $
 * $Revision: 1.1 $
 * $Log: TziviCreator.java,v $
 * Revision 1.1  2008-07-17 19:08:24  neal
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

import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import org.idch.util.LogService;
import org.idch.images.ImageUtils;
import org.idch.images.dz.TziConfig;
import org.idch.images.dz.TziInfo;

/**
 * <p>Manages the process of creating new tiled, zoomable image (tzi) files.
 * This class also provides a <tt>main</tt> method that supports commandline 
 * based interaction to allow the creation of a tzi from a single image or 
 * from all images in a directory. In general, this class is intended to be 
 * accessed only within this package. Other applications that need to create 
 * tzi files should use the <tt>create</tt> method of the <tt>ZoomableImage</tt>
 * class.</p>
 * 
 * TODO allow creation of layers that are greater than 100%
 * TODO allow specification of tile format (e.g, JPEG, TIFF, etc)
 * 
 * @author Neal Audenaert
 */
public class ZipTziCreator {
    static final String logger = ZipTziCreator.class.getName();
    
    private TziConfig      config = null;
    private PlanarImage      image  = null;
    private Interpolation    interp = new InterpolationBicubic(8);
    private ZipOutputStream  out    = null;
    private TziInfo     info   = null;
    
    /**
     * Constructs a new instance of this class that will create a new tzi 
     * from the provided image using the supplied configuration paramaters.
     *  
     * @param image  The source image to be tiled.
     * @param dest   The file to which the tzi should be written.
     * @param config Configuration parameters.
     */
    ZipTziCreator(PlanarImage image, File dest, TziConfig config) 
        throws FileNotFoundException {
        this.config = config;
        this.image  = image;
        
        try {
            // Create the zip file that we will write data to. 
            FileOutputStream fos = new FileOutputStream(dest);
            this.out = new ZipOutputStream(fos);
            
            // turn off compression since we're writing mostly JPEG image data.
            this.out.setLevel(Deflater.NO_COMPRESSION);
        } catch (FileNotFoundException ex) {
            String msg = "Could no open the tzi file to be created: " + 
                    dest.getAbsolutePath();
            LogService.logError(msg, logger, ex);
            this.out = null;
            throw ex;
        }
    }
    
    /** Creates the tiled zoomable image. */
    void process() throws IOException {
        assert this.out != null : "Not initialized";
        if (this.out == null) {
            String msg = "Could not process image. Output Stream not " +
                    "available. This is most likely due to an error when " +
                    "constructing this TziviCreator object.";
            LogService.logError(msg, logger);
        }
        
        int w = this.image.getWidth();
        int h = this.image.getHeight();
        
        // initialze the zoomable info
        this.info   = new TziInfo();
        this.info.setName(config.name);
        this.info.setWidth(w);
        this.info.setHeight(h);
        this.info.setTileWidth(config.tWidth);
        this.info.setTileHeight(config.tHeight);
        
        // first, calculate the scale of the smallest image
        float scale  = 1.0F; 
        if (config.minScale != 0) {
            // if minimum scale is specified use that
            scale = config.minScale;
        } else {
            assert (config.minHeight > 0) || (config.minWidth > 0) : 
                "Min height, min width, or scale must be specified.";
            
            // find scale factor
            if (((w > h) && (config.minWidth != 0)) || (config.minHeight == 0)) 
                scale = (float)config.minWidth / w;
            else       
                scale = (float)config.minHeight / h;
        }
        
        int layerid = 0;
        while (scale < 1.0) {
            layerid = info.createLayer();
            info.setRatio(layerid, scale);
            processLayer(scale, layerid);
            scale += scale * config.stepSize;
        }
        
        // process the final layer
        layerid = info.createLayer();
        info.setRatio(layerid, 1.0F);
        processLayer(1.0F, layerid);
        
        ZipEntry entry = new ZipEntry("info.xml");
        out.putNextEntry(entry);
        out.write(this.info.toXml().toString().getBytes("UTF-8"));
        out.close();
    }
    
    /** Processes a single image layer. */
    private void processLayer(float scale, int layerid) throws IOException {
        assert scale >  0 : "Scale maynot be less than or equal to 0";
         
        PlanarImage tmp =
            ImageUtils.scaleImage(image, scale, interp);
        TiledImage layer = new TiledImage(tmp, config.tWidth, config.tHeight);
        int cols = layer.getNumXTiles();
        int rows = layer.getNumYTiles();
        
        // store the information about this layer
        info.setWidth(layerid, tmp.getWidth());
        info.setHeight(layerid, tmp.getHeight());
        info.setCols(layerid, cols);
        info.setRows(layerid, rows);
        
        // process tiles
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                String tileId = ZoomableImage.getTileFilename(layerid, r, c); 
                
                // encode the tile (r, c) 
                Raster raster = layer.getTile(c, r);  // TODO store directly from raster
                TiledImage img = layer.getSubImage(
                            raster.getMinX(),  raster.getMinY(), 
                            raster.getWidth(), raster.getHeight());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                JAI.create("encode", img, stream, ImageUtils.JPEG, null);
                
                // write the image data to the stream
                ZipEntry entry = new ZipEntry(tileId);
                out.putNextEntry(entry);
                out.write(stream.toByteArray());
              
                // clean up everything - XXX resource leak on exceptions  
                out.closeEntry();
                stream.close();
                stream = null;
            }
        }
    }
}