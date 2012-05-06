/**
 * 
 */
package org.idch.afed.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.idch.util.Filenames;

import junit.framework.TestCase;

/**
 * @author Neal Audenaert
 */
public class ScaledImageFormatterTests extends TestCase {
    private static final String TEST_IMAGE = "data/testdata/images/GA0209/0001a.jpg";
    private static final File OUTPUT_DIR = new File("data/testdata/temp/fsimagestore"); 
    private static final String CTX =  "GA0209/0001a";
    
    private FSImageStore store;
    private ImageContext context;
    
    public void setUp() throws IOException {
        if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdirs();
        }
        
        store = FSImageStore.createImageStore(OUTPUT_DIR.getPath());
        context = new ImageContext(store, CTX);
        store.connect();
    }
    
    public void tearDown() {
        store.close();
//        Filenames.deleteDirectory(OUTPUT_DIR);
    }
    
    public void testScaleImage() throws IOException {
        ImageFormatter formatter = new ScaledImageFormatter("test", 150, 150);
        
        BufferedImage image = ImageIO.read(new File(TEST_IMAGE));
        
        String fname = ";type=UV/";
        ImageContext ctx = new ImageContext(context, fname);
        
        formatter.process(image, ctx);
        
        BufferedImage im2 = ctx.get("test.jpg");
        assertNotNull(im2);
        
        
//        processor.add(new ScaledImageFormatter("small", 250, 250));
//        processor.add(new ScaledImageFormatter("medium", 500, 500));
//        processor.add(new ScaledImageFormatter("large", 800, 800));
    }
}
