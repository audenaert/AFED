/**
 * 
 */
package test;

import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileMutator;
import org.idch.afed.FacsimileRepository;
import org.idch.afed.impl.jpa.JPAFacsimileRepository;


/**
 * @author Neal Audenaert
 */
public class FacsimileTestDriver {
    
    public static final String NAME = "Test Facsimile";
    public static final String DESC = "A simple facsimile object to test if this works";
    public static final String DATE = "IV";
    FacsimileRepository repo;
    
    FacsimileTestDriver() {
        repo = JPAFacsimileRepository.getInstance();
    }
    
    public Facsimile createStandardFacsimile() {

        return repo.create(NAME, DESC, DATE);
    }
    
    public boolean testStandardFacsimile(Facsimile f, String name, String desc, String date) {
        boolean error = false;
        if (f == null) {
            System.err.println("Facsimile as null.");
            error = true;
        } else {
            if (!f.getName().equals(name)) {
                System.err.println("Incorrect name: " + f.getName() + " (expected '" + name + "').");
                error = true;
            }
            
            if (!f.getDescription().equals(desc)) {
                System.err.println("Incorrect description: " + f.getDescription()  + " (expected '" + desc + "').");
                error = true;
            }
            
            if (!f.getDateOfOrigin().equals(date)) {
                System.err.println("Incorrect date: " + f.getDateOfOrigin() + " (expected '" + date + "').");
                error = true;
            }
        }
        
        System.out.println(error ? "Errors" : "OK");
        return error;
    }

    public void testCreation() {
        Facsimile f = this.createStandardFacsimile();
        this.testStandardFacsimile(f, NAME, DESC, DATE);
        
    }
    
    public void testMutation() {
        String name = "Another name";
        String desc = "Another desc";
        String date = "unknown";
        
        Facsimile f = this.createStandardFacsimile();
        FacsimileMutator mutator = repo.getFacsimileMutator(f.getId());
        
        mutator.setName(name);
        mutator.setDescription(desc);
        mutator.setDateOfOrigin(date);
        
        // first, check that we haven't changed the DB yet
        Facsimile f2 = repo.get(f.getId());
        this.testStandardFacsimile(f2, NAME, DESC, DATE);
        
        Facsimile f3 = mutator.save();
        this.testStandardFacsimile(f3, name, desc, date);
        
        // make sure these changes really persisted
        Facsimile f4 = repo.get(f.getId());
        this.testStandardFacsimile(f4, name, desc, date);
        
        // TODO throw proper errors on reuse of mutator
        // TODO test deletion
        // TODO test rollbacks
        
    }
    
    public void testDeletion() {
        
    }
    
    public static void main(String [] args) {
        
       FacsimileTestDriver tester = new FacsimileTestDriver();
       tester.testCreation();
       tester.testMutation();
       
        System.out.println("done.");
    }
}
