/**
 * 
 */
package org.idch.afed;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;

import junit.framework.TestCase;

import org.idch.afed.impl.jpa.JPAFacsimileRepository;
import org.idch.util.PersistenceUtil;

/**
 * @author Neal Audenaert
 */
public class JPAFacsimileTests extends TestCase {
    private static final String REPO_UNIT_NAME = "org.idch.afed";   // FIXME: change to org.idch.afed.test
    
    EntityManagerFactory emf;
    FacsimileRepository repo;
    
    public void setUp() throws IOException {
        repo = JPAFacsimileRepository.getInstance(REPO_UNIT_NAME);
    }
    
    public void tearDown() {
        repo.dispose();
    }
    
    
    public void testCreateFacsimle() {
        FacsimileRepository repo = JPAFacsimileRepository.getInstance();

        String name = "Test Facsimile";
        String desc = "A simple facsimile object to test if this works";
        String date = "IV";

        Facsimile f = repo.create(name, desc, date);
        assertNotNull("Facsimile as null.", f);
        
        assertTrue("", f.getName().equals(name));
        assertTrue("", f.getDescription().equals(desc));
        assertTrue("", f.getDateOfOrigin().equals(date));
    }
    
    public static void main(String[] args) {
        JPAFacsimileTests test = new JPAFacsimileTests();
        try {
            test.setUp();
            test.testCreateFacsimle();
            test.tearDown();
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            t.printStackTrace();
        } finally {
            PersistenceUtil.shutdown();
        }
    }

}
