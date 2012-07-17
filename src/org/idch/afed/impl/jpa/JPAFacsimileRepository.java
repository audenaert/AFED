/**
 * 
 */
package org.idch.afed.impl.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileMutator;
import org.idch.afed.FacsimileRepository;
import org.idch.util.IdObfuscator;
import org.idch.util.PersistenceUtil;
import org.idch.util.persist.RepositoryAccessException;

/**
 * @author Neal Audenaert
 */
public class JPAFacsimileRepository implements FacsimileRepository {
    // The obfuscator is designed to turn numeric identifiers into string-based identifiers.
    // Future retrieval of values based on their ID requires that the alphabet, block size, 
    // and min length of the obfuscator not be changed after ids are publicized. 
    private static final String ALPHABET = "mn6j2c4rv8bpygw95z7hsdaetxuk3fq";
    private static final int BLOCK_SIZE = 24;
    private static final int MIN_LENGTH = 5;
    static final IdObfuscator obfuscator = new IdObfuscator(ALPHABET, BLOCK_SIZE, MIN_LENGTH);
    
//    private static final Logger LOGGER = Logger.getLogger(JPAFacsimileRepository.class);
    
    static final String REPO_UNIT_NAME = "org.idch.afed";
    
    private static final Map<String, JPAFacsimileRepository> repos = 
            new HashMap<String, JPAFacsimileRepository>();
    
    /** 
     * Returns a <tt>FacsimileRepository</tt> using the default persistence context.
     */
    public synchronized static FacsimileRepository getInstance() {
        return getInstance(REPO_UNIT_NAME);
    }
    
    /**
     * Returns a <tt>FacsimileRepository</tt> using the named persistence context.
     * @param repo
     * @return
     */
    public static FacsimileRepository getInstance(String repo) {
        synchronized(repos) {
            JPAFacsimileRepository repository = repos.get(repo);
            if (repository == null || repository.isDisposed()) {
                EntityManagerFactory emf = PersistenceUtil.getEMFactory(REPO_UNIT_NAME);
                
                repository = new JPAFacsimileRepository(emf);
                repos.put(repo, repository);
            }
            
            return repository;
        }
    } 

    //========================================================================================
    // MEMBER VARIABLES
    //========================================================================================
    
    final EntityManagerFactory emf;
    
    private JPAFacsimileRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void dispose() {
//        this.emf.close();
    }
    
    @Override
    public boolean isDisposed() {
        return !this.emf.isOpen();
    }
    
    //========================================================================================
    // MEMBER METHODS
    //========================================================================================
    
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
        JPAFacsimile f = new JPAFacsimile(this, name, desc, date);
        PersistenceUtil.save(emf, f);
        
        return f.asFacsimile();
    }
    
    private JPAFacsimile getJPAFacsimile(Long id) {
        JPAFacsimile f = null;
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            f = em.find(JPAFacsimile.class, id);
        } catch (Exception ex) {
            // TODO this shouldn't have happened. Log error and press on.
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        
        return f;
        
    }
    
    /**
     * 
     */
    @Override
    public Facsimile get(String id) {
        
        long fId = obfuscator.decode(id);
        JPAFacsimile f = getJPAFacsimile(fId);
        return (f != null) ? f.asFacsimile() : null;
    }
    
    /**
     * 
     */
    @Override
    public FacsimileMutator getFacsimileMutator(String id) {
        long fId = obfuscator.decode(id);
        JPAFacsimile f = getJPAFacsimile(fId);
        return (f != null) ? f.asMutableFacsimile(this) : null;
    }
    
    /** 
     * Returns all facsimiles in the repository as an iterable. 
     * 
     * @see org.idch.afed.FacsimileRepository#list()
     */
    @Override
    public Iterable<Facsimile> list() {
        // TODO Implement paged listings
        CriteriaBuilder builder = emf.getCriteriaBuilder();
        CriteriaQuery<JPAFacsimile> criteria = builder.createQuery(JPAFacsimile.class);
        criteria.select(criteria.from(JPAFacsimile.class));

        EntityManager em = null;
        List<Facsimile> immutable = null;
        try {
            em = this.emf.createEntityManager();
            List<JPAFacsimile> results = em.createQuery(criteria).getResultList();
            immutable = new ArrayList<Facsimile>(results.size());
            for (JPAFacsimile result : results) {
                immutable.add(result.asFacsimile());
            }
            
            return Collections.unmodifiableList(immutable);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
