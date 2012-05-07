/**
 * 
 */
package org.idch.afed.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.idch.images.ImageContext;
import org.idch.images.TileIterator;
import org.idch.images.stores.FSImageStore;

import junit.framework.TestCase;

/**
 * @author Neal Audenaert
 */
public class TileIterartorTests extends TestCase {
    private static final String TEST_IMAGE = "data/testdata/images/GA0209/0001a.jpg";
    private static final File OUTPUT_DIR = new File("data/testdata/temp/fsimagestore"); 
    private static final String CTX =  "tiles/";
    
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
//        Filenames.deleteDirectory(OUTPUT_DIR);
    }
    
    public void testTileCreation() throws IOException {
        BufferedImage image = ImageIO.read(new File(TEST_IMAGE));
        TileIterator tiles = new TileIterator(image, 256);
        while (tiles.hasNext()) {
            BufferedImage img = tiles.next();
            context.store(tiles.getY() + "," + tiles.getX() + ".jpg", img, "jpg");
        }
    }
    
    // TODO test basic properties like the number of tiles, sum of tile sizes, etc.
}
