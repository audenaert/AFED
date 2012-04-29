/**
 * 
 */
package org.idch.afed.impl.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileRepository;
import org.idch.util.PersistenceUtil;
import org.idch.util.persist.RepositoryAccessException;

/**
 * @author Neal Audenaert
 */
public class JPAFacsimileRepository extends FacsimileRepository {
//    private static final Logger LOGGER = Logger.getLogger(JPAFacsimileRepository.class);
    
    static final String REPO_UNIT_NAME = "org.idch.afed";
    
    private static FacsimileRepository repository = new JPAFacsimileRepository();
    
    public static FacsimileRepository getInstance() {
        return repository;
    }

    EntityManagerFactory emf;
    
    private JPAFacsimileRepository() {
        emf = PersistenceUtil.getEMFactory(REPO_UNIT_NAME);
    }

    EntityManagerFactory getEmf() {
        return this.emf;
    }
    
    /** 
     * Creates a new <tt>Facsimile</tt>
     * 
     * @param name The display name of the facsimile to create.
     * @param desc A description of the facsimile to create.
     * @param date The date when the facsimile is thought to have been created.
     * @throws RepositoryAccessException If there are problems accessing the underlying
     *      persistent data store.
     *      
     * @see org.idch.afed.FacsimileRepository#createFacsimile(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Facsimile create(String name, String desc, String date) 
            throws RepositoryAccessException {
        JPAFacsimileDelegate delegate = new JPAFacsimileDelegate(this, name, desc, date);
        Facsimile f = new Facsimile(delegate);
        f.save();
        
        return f;
    }
    
    public void save(Facsimile f) throws RepositoryAccessException {
        // factory.save(f);
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.FacsimileRepository#listFacsimiles()
     */
    @Override
    public Map<String, String> list() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.FacsimileRepository#getFacsimile(java.lang.String)
     */
    @Override
    public Facsimile get(String scheme) {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.idch.afed.FacsimileRepository#listFacsimiles(java.lang.String)
     */
    @Override
    public List<String> list(String scheme) {
        // TODO Auto-generated method stub
        return null;
    }


}
