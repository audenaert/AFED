/**
 * 
 */
package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileRepository;
import org.idch.afed.images.FSImageStore;
import org.idch.afed.images.ImageContext;
import org.idch.afed.impl.jpa.JPAFacsimileDelegate;
import org.idch.ms.Designation;
import org.idch.tzivi.FSTziCreator;
import org.idch.tzivi.TziConfig;
import org.idch.util.PersistenceUtil;
import org.idch.util.persist.RepositoryAccessException;


/**
 * @author Neal Audenaert
 */
public class SimpleTestDriver {
    
    public static void apiTest() {
        try {
            FacsimileRepository repo = FacsimileRepository.getInstance();

            int ct = 1;

            String name = "Test Facsimile " + ct++;
            String desc = "A simple facsimile object to test if this works";
            String date = "IV";

            Facsimile f = new Facsimile(name, desc, date);
            f.putDesignation(new Designation("GA", "P46"));
            f.save();
            System.out.println(f + " " + f.getDateOfOrigin());
            Set<Designation> desgns = f.getDesignations();
            for (Designation d : desgns) {
                System.out.println("    " + d);
            }

            f.setDateOfOrigin("XII");
            f.setName("Bob's your uncle");
            f.putDesignation(new Designation("GA", "02"));
            f.putDesignation(new Designation("shelf", "232d02"));
            f.save();
            System.out.println(f + " " + f.getDateOfOrigin());
            for (Designation d : desgns) {
                System.out.println("    " + d);
            }

        } finally {
            PersistenceUtil.shutdown();
        }
    }
    
//    public boolean save() throws RepositoryAccessException {
        // FIXME this breaks down when we try to use a single EM (and transaction) 
        //       across updates to multiple objects. We need to provide support for
        //       this scenario at some point.
        
       
//      this.em.persist((T)this);
//      Session session = (Session)this.em.getDelegate(); 
//      session.saveOrUpdate(this);
//        boolean success = false;
//        if (!this.isUpdatable()) {
//            this.makeUpdatable();
//        }
//        
//        try {
//            this.tx.commit();
//            this.getFields().flush();
//            success = true;
//        } catch (Throwable t) {
//            if (tx.isActive()) {
//                try { tx.rollback(); } 
//                catch (Throwable err) { }
//            }
//
//            // TODO handle and repackage exception
//
//        } finally {
//            tx = null;
//        }
//        
//        return success;
//    }

    public static void directTest() {
        try {
            FacsimileRepository repo = FacsimileRepository.getInstance();

            int ct = 1;

            String name = "Test Facsimile " + ct++;
            String desc = "A simple facsimile object to test if this works";
            String date = "IV";

            JPAFacsimileDelegate f = new JPAFacsimileDelegate(name, desc, date);
            Designation p46 = new Designation("GA", "P46");
            f.putDesignation(p46);
            
            EntityManagerFactory emf = PersistenceUtil.getEMFactory("org.idch.afed");
            EntityManager em = emf.createEntityManager();
            
            em.getTransaction().begin();
            em.persist(p46);
            em.persist(f);
            em.flush();
            em.getTransaction().commit();
            
            
//            f.save();
            System.out.println(f + " " + f.getDateOfOrigin());
            Set<Designation> desgns = f.getDesignations();
            for (Designation d : desgns) {
                System.out.println("    " + d);
            }
//
//            f.setDateOfOrigin("XII");
//            f.setName("Bob's your uncle");
//            f.putDesignation(new Designation("GA", "02"));
//            f.putDesignation(new Designation("shelf", "232d02"));
//            f.save();
//            System.out.println(f + " " + f.getDateOfOrigin());
//            for (Designation d : desgns) {
//                System.out.println("    " + d);
//            }

        } finally {
            PersistenceUtil.shutdown();
        }
    }
    
    private static final String TEST_IMAGE = "data/testdata/images/GA0209/0001a.jpg";
    private static final File OUTPUT_DIR = new File("data/testdata/temp/fsimagestore"); 
    private static final String CTX =  "tiles/";

    public static void main(String [] args) {
//        apiTest();
//        for (String fmt : ImageIO.getReaderFormatNames()) {
//            System.out.println(fmt);
//            
//        }
        try {
            File f = new File("I:\\DonneImages\\1633\\001-2.tif");
            if (f.exists() && f.canRead()) {
                System.out.println("Readable File Exists.");
            }
            
            FSImageStore store = FSImageStore.getImageStore(OUTPUT_DIR.getPath());
            ImageContext context = new ImageContext(store, CTX);
            store.connect();
            
            TziConfig config = new TziConfig();
            long start = System.currentTimeMillis();
            BufferedImage image = ImageIO.read(f);
            
            FSTziCreator creator = new FSTziCreator(image, config, context);
            creator.create();
            long end = System.currentTimeMillis();
            
            System.out.println("Elapsed time: " + (end - start));
            image.flush();
            store.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // NOTE http://www.coderanch.com/t/445956/open-source/add-tiff-writer-imageio-package
        //      On reading and writing TIFF files
        
        System.out.println("done.");
    }
}
