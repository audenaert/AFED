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

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.idch.afed.images.ImageContext;
import org.idch.afed.images.ImageStore;
import org.idch.afed.images.TileIterator;
import org.idch.tzivi.legacy.TziGenerator;
import org.idch.util.Scalr;
import org.idch.util.Scalr.Method;

/**
 * Manages the process of creating new tiled, zoomable image (tzi) files.
 * This class also provides a <tt>main</tt> method that supports commandline 
 * based interaction to allow the creation of a tzi from a single image or 
 * from all images in a directory. In general, this class is intended to be 
 * accessed only within this package. Other applications that need to create 
 * tzi files should use the <tt>create</tt> method of the <tt>ZoomableImage</tt>
 * class.
 * 
 * @author Neal Audenaert
 */
public class FSTziCreator {
    static final String LOGGER = TziGenerator.class.getName();
    
    private static final String THUMB_FILENAME = "thumb";
    
    private final TziConfig cfg;
    private final TziInfo m_info = new TziInfo();
    
    private final BufferedImage m_image;
    
    private final ImageStore store;
    
    /**
     * Constructs a new instance of this class that will create a new tzi 
     * from the provided image using the supplied configuration paramaters.
     *  
     * @param image  The source image to be tiled.
     * @param config Configuration parameters.
     * @param destDir The directory to which the tzi files should be written.
     * @param overwrite Whether existing data should be overwritten. If set to 
     *      true, any data in the supplied destination directory will be 
     *      deleted. 
     */
    public FSTziCreator(BufferedImage image, TziConfig config, ImageStore store) { 
        this.cfg = config;
        this.m_image = image;
        this.store = store;
    }
    
    /** Creates the tiled zoomable image. */
    public void create() throws IOException {
        // initialze the zoomable info
        this.m_info.setName(cfg.name);
        this.m_info.setWidth(m_image.getWidth());
        this.m_info.setHeight(m_image.getHeight());
        this.m_info.setTileWidth(cfg.tWidth);
        this.m_info.setTileHeight(cfg.tHeight);
        
        this.m_info.setExtension(cfg.format);
        
        float scale = initScale();
        int layerid = 0;
        while (scale < 1.0) {
            layerid = m_info.createLayer();
            m_info.setRatio(layerid, scale);
            processLayer(scale, layerid);
            scale += scale * cfg.stepSize;
        }
        
        // process the final, 100% scale layer
        layerid = m_info.createLayer();
        m_info.setRatio(layerid, 1.0F);
        processLayer(1.0F, layerid);
        
        writeThumbnail();
        writeMetadata();
    }
    
    /** Processes a single image layer. */
    private void processLayer(float scale, int layerId) throws IOException {
        assert scale >  0 : "Scale may not be less than or equal to 0";
         
        int w = (int) Math.round(m_image.getWidth() * scale);
        int h = (int) Math.round(m_image.getHeight() * scale);
        BufferedImage scaled = Scalr.resize(m_image, Method.QUALITY, w, h);
        TileIterator tiles = new TileIterator(scaled, cfg.tWidth, cfg.tHeight);
        
        // store the information about this layer
        m_info.setWidth(layerId, scaled.getWidth());
        m_info.setHeight(layerId, scaled.getHeight());
        m_info.setCols(layerId, tiles.getNumXTiles());
        m_info.setRows(layerId, tiles.getNumYTiles());
        
        // process tiles
        ImageContext ctx = new ImageContext(store, layerId + "/");
        while (tiles.hasNext()) {
            BufferedImage im = tiles.next();
            String fname = tiles.getX() + "," + tiles.getY() + "." + cfg.format;
            ctx.store(fname, im);
        }
    }
    
    private void writeMetadata() throws FileNotFoundException {
        // write the tzivi info to an xml file
//        File info = new File(m_basedir, "info.xml");
//        PrintStream infoStream = new PrintStream(new FileOutputStream(info));
//        infoStream.print(m_info.toXml().toString());
//        
//        // wirte the tzivi info in json format
//        File json = new File(m_basedir, "info.json");
//        PrintStream jsonStream = new PrintStream(new FileOutputStream(json));
//        jsonStream.print(m_info.toJSON());
    }
    
    private void writeThumbnail() throws IOException {
        if (!cfg.generateThumb) 
            return;
        
        int w = cfg.thumbWidth;
        int h = cfg.thumbHeight;
        
        BufferedImage thumb = Scalr.resize(m_image, Method.QUALITY, w, h);
        String fname = THUMB_FILENAME + "." + cfg.format;
        this.store.store(fname, thumb, cfg.format);

        // update the metadata information
        String thumbUrl = THUMB_FILENAME + "." + cfg.format;
        m_info.defineThumbnail(thumbUrl, thumb.getWidth(), thumb.getHeight());
    }
    
    /** Calculate the scale of the smallest layer. */
    private float initScale() {
        float scale  = 1.0F; 
        if (cfg.minScale > 0) {
            scale = cfg.minScale;
        } else {
            int w = m_image.getWidth();
            int h = m_image.getHeight();
            assert (cfg.minHeight > 0) || (cfg.minWidth > 0) : 
                "Min height, min width, or scale must be specified.";

            // find scale factor
            if (((w > h) && (cfg.minWidth != 0)) || (cfg.minHeight == 0)) 
                scale = (float)cfg.minWidth / w;
            else       
                scale = (float)cfg.minHeight / h;
        }
        
        return scale;
    }
}