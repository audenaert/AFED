/**
 * 
 */
package org.idch.afed.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.idch.images.ImageContext;
import org.idch.images.stores.FSImageStore;
import org.idch.util.Filenames;

import junit.framework.TestCase;

/**
 * @author Neal Audenaert
 */
public class ImageContextTests extends TestCase {
    private static final String TEST_IMAGE = "data/testdata/images/GA0209/0001a.jpg";
    private static final File OUTPUT_DIR = new File("data/testdata/temp/fsimagestore"); 
    private static final String CTX =  "GA0209/0001a";
    
    private FSImageStore store;
    private ImageContext context;
    
    public void setUp() throws IOException {
        if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdirs();
        }
        
        store = FSImageStore.getImageStore(OUTPUT_DIR.getPath());
        context = new ImageContext(store, CTX);
        store.connect();
    }
    
    public void tearDown() {
        store.close();
        Filenames.deleteDirectory(OUTPUT_DIR);
    }
    
    public void testStoreAndGetImage() throws IOException {
        BufferedImage image = ImageIO.read(new File(TEST_IMAGE));
        
        String fname = ";type=UV/original.jpg";
        context.store(fname, image);
        
        assertTrue(context.exists(fname));
        assertFalse(store.exists(fname));
        assertTrue(store.exists(CTX + fname));
        
        BufferedImage im2 = context.get(fname);
        assertNotNull(im2);
        
        im2 = store.get(CTX + fname);
        assertNotNull(im2);
    }
    
    public void testMultiLevelWrapping() throws IOException {
        BufferedImage image = ImageIO.read(new File(TEST_IMAGE));
        
        String local = ";type=UV/";
        ImageContext ctx = new ImageContext(context, local);
        
        String fname = "original.jpg";
        ctx.store(fname, image);
        
        assertTrue(ctx.exists(fname));
        assertFalse(store.exists(fname));
        assertTrue(store.exists(CTX + local + fname));
        
        BufferedImage im2 = ctx.get(fname);
        assertNotNull(im2);
        
        im2 = store.get(CTX + local + fname);
        assertNotNull(im2);
    }
    
   

}
