/**
 * 
 */
package org.idch.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.idch.util.persist.RepositoryAccessException;

/**
 * @author Neal Audenaert
 */
public class PersistentFactory<T> {
    private static final Logger LOGGER = Logger.getLogger(PersistentFactory.class);
    
    EntityManagerFactory emf = null;
    
    PersistentFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public EntityManagerFactory getEMFactory() {
        return this.emf;
    }
    
    public EntityManager createEntityManager() {
        return this.emf.createEntityManager();
    }
    
    /** 
     */
    public T create(T object) 
            throws RepositoryAccessException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        tx.begin();
        try {
            em.persist(object);
            tx.commit();
        } catch (Throwable ex) {
            object = null;
            // FIXME need to provide a more detailed error message.
            LOGGER.error("Failed to create new facsimle.", ex);
            
            throw new RepositoryAccessException("", ex);
        } finally {
            if (tx.isActive())
                tx.rollback();
            
            em.close();
        }
        
        return object;
    }
    
    public void save(T object) throws RepositoryAccessException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        tx.begin();
        try {
            em.merge(object);
            tx.commit();
        } catch (Throwable ex) {
            object = null;
            // FIXME need to provide a more detailed error message.
            LOGGER.error("Failed to create new facsimle.", ex);
            
            throw new RepositoryAccessException("", ex);
        } finally {
            if (tx.isActive())
                tx.rollback();
            
            em.close();
        }
    }
    

}
