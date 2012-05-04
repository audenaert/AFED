/**
 * 
 */
package test;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileRepository;
import org.idch.afed.impl.jpa.JPAFacsimileDelegate;
import org.idch.ms.Designation;
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
    
    public static void main(String [] args) {
        apiTest();
        
        System.out.println("done.");
    }
}
