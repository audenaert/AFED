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

import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileRepository;
import org.idch.afed.impl.jpa.JPAFacsimileRepository;
import org.idch.afed.impl.jpa.legacy.JPAFacsimileDelegate;
import org.idch.afed.legacy.BasicFacsimile;
import org.idch.images.ImageContext;
import org.idch.images.dz.FSTziCreator;
import org.idch.images.dz.TziConfig;
import org.idch.images.stores.FSImageStore;
import org.idch.ms.BasicDesignation;
import org.idch.util.PersistenceUtil;


/**
 * @author Neal Audenaert
 */
public class SimpleTestDriver {
    
    public static void apiTest() {
        try {
            FacsimileRepository repo = JPAFacsimileRepository.getInstance();

            int ct = 1;

            String name = "Test Facsimile " + ct++;
            String desc = "A simple facsimile object to test if this works";
            String date = "IV";

            BasicFacsimile f = new BasicFacsimile(name, desc, date);
            f.putDesignation(new BasicDesignation("GA", "P46"));
            f.save();
            System.out.println(f + " " + f.getDateOfOrigin());
            Set<BasicDesignation> desgns = f.getDesignations();
            for (BasicDesignation d : desgns) {
                System.out.println("    " + d);
            }

            f.setDateOfOrigin("XII");
            f.setName("Bob's your uncle");
            f.putDesignation(new BasicDesignation("GA", "02"));
            f.putDesignation(new BasicDesignation("shelf", "232d02"));
            f.save();
            System.out.println(f + " " + f.getDateOfOrigin());
            for (BasicDesignation d : desgns) {
                System.out.println("    " + d);
            }

            repo.list();
            
        } finally {
            PersistenceUtil.shutdown();
        }
    }
    
    public static void apiTest2() {
        try {
            FacsimileRepository repo = JPAFacsimileRepository.getInstance();

            int ct = 1;

            String name = "Test Facsimile " + ct++;
            String desc = "A simple facsimile object to test if this works";
            String date = "IV";
            
            Facsimile f = repo.create(name, desc, date);

//            BasicFacsimile f = new BasicFacsimile(name, desc, date);
//            f.putDesignation(new BasicDesignation("GA", "P46"));
//            f.save();
            System.out.println(f + " " + f.getDateOfOrigin());
//            Set<BasicDesignation> desgns = f.getDesignations();
//            for (BasicDesignation d : desgns) {
//                System.out.println("    " + d);
//            }
//
//            f.setDateOfOrigin("XII");
//            f.setName("Bob's your uncle");
//            f.putDesignation(new BasicDesignation("GA", "02"));
//            f.putDesignation(new BasicDesignation("shelf", "232d02"));
//            f.save();
//            System.out.println(f + " " + f.getDateOfOrigin());
//            for (BasicDesignation d : desgns) {
//                System.out.println("    " + d);
//            }

            repo.list();
            
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
            FacsimileRepository repo = JPAFacsimileRepository.getInstance();

            int ct = 1;

            String name = "Test Facsimile " + ct++;
            String desc = "A simple facsimile object to test if this works";
            String date = "IV";

            JPAFacsimileDelegate f = new JPAFacsimileDelegate(name, desc, date);
            BasicDesignation p46 = new BasicDesignation("GA", "P46");
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
            Set<BasicDesignation> desgns = f.getDesignations();
            for (BasicDesignation d : desgns) {
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
    
    private static void testImageImport() {
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
    }
    
    private static final String TEST_IMAGE = "data/testdata/images/GA0209/0001a.jpg";
    private static final File OUTPUT_DIR = new File("data/testdata/temp/fsimagestore"); 
    private static final String CTX =  "tiles/";

    public static void main(String [] args) {
        
        FacsimileRepository repo = JPAFacsimileRepository.getInstance();

        String name = "Test Facsimile";
        String desc = "A simple facsimile object to test if this works";
        String date = "IV";

        boolean error = false;
        Facsimile f = repo.create(name, desc, date);
        if (f == null) {
            System.err.println("Facsimile as null.");
            error = true;
        } else {
            if (!f.getName().equals(name)) {
                System.err.println("Incorrect name: " + f.getName());
                error = true;
            }
            
            if (!f.getDescription().equals(desc)) {
                System.err.println("Incorrect description: " + f.getDescription());
                error = true;
            }
            
            if (!f.getDateOfOrigin().equals(date)) {
                System.err.println("Incorrect date: " + f.getDateOfOrigin());
                error = true;
            }
        }
        
//        FacsimileMutator mutable = repo.
        
        System.out.println(error ? "Errors" : "OK");
        
//        apiTest();
//        for (String fmt : ImageIO.getReaderFormatNames()) {
//            System.out.println(fmt);
//            
//        }
        
//        FacsimileRepository repo = FacsimileRepository.getInstance();
//        repo.list();
        
        
        // NOTE http://www.coderanch.com/t/445956/open-source/add-tiff-writer-imageio-package
        //      On reading and writing TIFF files
        
        System.out.println("done.");
    }
}
