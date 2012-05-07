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
public class FSImageStoreTests extends TestCase {
    private String testImage = "data/testdata/images/GA0209/0001a.jpg";
    
    private File outputDir = new File("data/testdata/temp/fsimagestore"); 
    
    private FSImageStore store;
    
    public void setUp() throws IOException {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        store = FSImageStore.getImageStore(outputDir.getPath());
        store.connect();
    }
    
    public void tearDown() {
        store.close();
        Filenames.deleteDirectory(outputDir);
    }
    
    public void testStoreAndGetImage() throws IOException {
        BufferedImage image = ImageIO.read(new File(testImage));
        
        String fname = "GA0209/0001a;type=UV/original.jpg";
        store.store(fname, image);
        
        assertTrue(store.exists(fname));
        
        BufferedImage im2 = store.get(fname);
        assertNotNull(im2);
    }
    
    public void testDuplicateStorage() throws IOException {
        // TODO this should validate that the images really are or aren't being overwritten. 
        BufferedImage image = ImageIO.read(new File(testImage));
        
        String fname = "GA0209/0001a;type=UV/original.jpg";
        store.store(fname, image);
        
        try {
            store.store(fname, image);
            assertTrue("Expected an exception when storing a duplicate image.", false);
        } catch (IOException ex) {
            // This is what we expected.
        }
        
        // can we allow overwrites? 
        try {
            store.allowOverwrite();
            store.store(fname, image);
        } catch (IOException ex) {
            assertTrue("Expected to be able to overwrite duplicate image.", false);
        }
        
        // can we reset to prevent overwrites?
        try {
            store.allowOverwrite(false);
            store.store(fname, image);
            assertTrue("Expected an exception when storing a duplicate image.", false);
        } catch (IOException ex) {
            // This is what we expected.
        }
    }

}
