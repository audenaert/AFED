/**
 * 
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import javax.imageio.ImageIO;

import org.idch.afed.legacy.BasicFacsimile;
import org.idch.ms.BasicDesignation;
import org.idch.util.Filenames;

/**
 * @author Neal Audenaert
 */
public class ImageImporter {
    public static enum ImageTypes {
        UV, RakingLight, NormalLight
    }
    
    public static void main(String[] args) {
        File ms = new File("data/facsimiles/nt/GA 0209");
        
        String name = "GA 0209";
        String desc = "<p>Majuscule Apostolos and Paul manuscript on parchment; " +
        		      "palimpsest, 8 leaves, 2 columns, 29–32 lines per column.</p>" +
        		      "<table><tbody>" +
        		      "  <tr><td>Date:</td><td>7th Century</td></tr>" +
        		      "  <tr><td>Location:</td><td>University of Michigan, Ann Arbor</td></tr>" +
        		      "  <tr><td>Shelf Number:</td><td>MS 8</td></tr>" +
        		      "</tbody></table>";
        
        String date = "VII";
        BasicFacsimile f = new BasicFacsimile(name, desc, date);
        f.putDesignation(new BasicDesignation("GA", "0209"));
        f.save();
        
        
        File[] images = ms.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                String ext = Filenames.getExtension(name);
                for (String s : ImageIO.getReaderFileSuffixes()) {
                    if (s.equalsIgnoreCase(ext)) {
                        return true;
                    }
                }
                return false;
            }
            
        });
        
        for (File image : images) {
            String imgName = Filenames.getBasename(image.getName());
            imgName = imgName.replace("GA_0209", "");
            try {
                f.addImage(imgName, new FileInputStream(image), ImageTypes.UV.name());
            } catch (FileNotFoundException fnfe) {
                
            }
        }
    }

}
