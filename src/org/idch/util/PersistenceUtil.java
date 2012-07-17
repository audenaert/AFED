package org.idch.util;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

/**
 * Bootstrapping class for obtaining references to <tt>EntityManagerFactories</tt> and for 
 * making sure they are all shut down when the application ends. In a J2EE environment, 
 * this functionality is provided by the application server, but in a J2EE environment, the 
 * application is responsible for obtaining its own factory instances. This is an expensive 
 * process and should be done only once for each persistence context.    
 *    
 * @author Neal Audenaert
 */
public class PersistenceUtil {
    
    private static final Map<String, EntityManagerFactory> factories = 
    	new HashMap<String, EntityManagerFactory>();

    /**
     * Obtains an <tt>EntityManagerFactory</tt> for a persistence used defined in the
     * <tt>persistence.xml</tt> file.
     *  
     * @param persistenceUnit The persistence unit to retrieve
     * @return The desired <tt>EntityManagerFactory</tt>
     */
    public static EntityManagerFactory getEMFactory(String persistenceUnit) {
    	EntityManagerFactory emf = null;
    	synchronized (factories) {
    		emf = factories.get(persistenceUnit);
    		if (emf == null) {
    			emf = Persistence.createEntityManagerFactory(persistenceUnit);
    			
    			if (emf != null)
    				factories.put(persistenceUnit, emf);
    		}
    	}
    	
    	return emf;
    }
    
    /**
     * Closes all factories that were loaded by an application.
     */
    public static void shutdown() {
        // closes caches and connection pools
        synchronized (factories) {
        	for (String pUnit : factories.keySet()) {
        		try {
        			factories.get(pUnit).close();
        		} catch (Throwable err) {
        			// TODO Use logging system
        			System.err.println("Could not close EM factory (" + pUnit + "): " + 
        					err.getLocalizedMessage());
        			err.printStackTrace();
        		}
        	}
        }
    }
    
    /**
     * Saves a new Entity or updates the state of an existing (modified) entity. 
     * 
     * @param emf The <tt>EntityManagerFactory</tt> to be used to save this object.
     * @param obj The object to be saved.
     * @return The saved object. Note that this may be a reference to a different object
     *      than was initially passed in to be saved. References to the original object 
     *      should be used and references to the provided object should be discarded.
     */
    public static <T> T save(EntityManagerFactory emf, T obj) {
        EntityManager em = emf.createEntityManager();
        Session sess = (Session)em.getDelegate();       // HACK: dependency on Hibernate instead of JPA 
        
        sess.getTransaction().begin();
        sess.saveOrUpdate(obj);
        sess.flush();
        sess.getTransaction().commit();
        em.close();
        
        return obj;
    }
    
    /**
     * Counts the number of results of a search. Note that this works only for queries 
     * that do not have a join clause.
     * 
     * @param criteria The criteria for the query.
     * @return The number of results of the query.
     */
    public static <T> Long findCountByCriteria(CriteriaQuery<T> criteria, EntityManager em) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<T> entityRoot = countCriteria.from(criteria.getResultType());
        countCriteria.select(builder.count(entityRoot));
        countCriteria.where(criteria.getRestriction());

        return em.createQuery(countCriteria).getSingleResult();
    }
}

