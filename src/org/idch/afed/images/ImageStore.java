/**
 * 
 */
package org.idch.afed.images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Neal Audenaert
 */
public interface ImageStore {
    
    // Supported image formats (USING JAI)
    public static final String TIF  = "tif";
    public static final String TIFF = "tiff";
    public static final String BMP  = "bmp";
    public static final String FPX  = "fpx";
    public static final String GIF  = "gif";
    public static final String JPEG = "jpeg";
    public static final String JPG  = "jpg";
    public static final String PNG  = "png";
    public static final String PNM  = "pnm";
    
    public static final String supportedFormats[] =
        { TIF, TIFF, JPEG, JPG, GIF, BMP, FPX, PNG, PNM };
    
    public static final Map<String, String> fileStoreOpts =
        new HashMap<String, String>();
    
   
         
//    static {
//        fileStoreOpts.put(TIF,  "TIFF");
//        fileStoreOpts.put(TIFF, "TIFF");
//        fileStoreOpts.put(JPEG, "JPEG");
//        fileStoreOpts.put(JPG,  "JPEG");
//        fileStoreOpts.put(GIF,  "GIF");
//        fileStoreOpts.put(BMP,  "BMP");
//        fileStoreOpts.put(FPX,  "FPX");
//        fileStoreOpts.put(PNG,  "PNG");
//        fileStoreOpts.put(PNM,  "PNM");
//    }
    
    public void store(String relPath, BufferedImage image) throws IOException;
    
    public void store(String relPath, BufferedImage image, String format) throws IOException;
    
    public boolean canWrite(String format);
    
    public boolean canRead(String format);
    
    public boolean exists(String relPath);
    
    public BufferedImage get(String relPath) throws IOException;
        
    public void connect() throws IOException;
    
    public boolean isConnected();
    
    public void close();
    
    public boolean isClosed();
}
