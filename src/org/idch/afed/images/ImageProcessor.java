/**
 * 
 */
package org.idch.afed.images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.idch.afed.images.ImageFormatter.ImageProcessorException;

/**
 * 
 * @author Neal Audenaert
 */
public class ImageProcessor {
    // TODO read and write from a JSON serialization format
    //      we need a way to lookup the formatters used to create an image.
    //      need to be able to query an image by named type
    //      need a way to get a URI to resolve images
    //      collect timing statistics
    
    public static ImageProcessor getImageProcessor(String path) throws IOException {
        FSImageStore store = FSImageStore.getImageStore("data/testdata/temp");
        store.connect();
        
        ImageProcessor processor = new ImageProcessor(store);
        processor.add(new ScaledImageFormatter("thumb", 125, 125));
        processor.add(new ScaledImageFormatter("small", 250, 250));
        processor.add(new ScaledImageFormatter("medium", 500, 500));
        processor.add(new ScaledImageFormatter("large", 800, 800));
        processor.add(new ScaledImageFormatter("huge", 1600, 1600));
        
        return processor;
    }
    
    private ImageStore store = null;
    private final List<ImageFormatter> formatters = new ArrayList<ImageFormatter>();
    
    ImageProcessor(ImageStore store) {
        this.store = store;
    }
    
    public void process(BufferedImage image, String path) {
        ImageContext ctx = new ImageContext(this.store, path);
        for (ImageFormatter formatter : formatters) {
            try {
                formatter.process(image, ctx);
            } catch (ImageProcessorException ipe) {
                // TODO log me
            }
        }
    }
    
    public void add(ImageFormatter f) {
        // validate unique names
        formatters.add(f);
    }

}
