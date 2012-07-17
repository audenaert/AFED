/**
 * 
 */
package org.idch.afed.impl.jpa;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;

import org.idch.util.PersistenceUtil;

/**
 * 
 * @author Neal Audenaert
 * @param <T>
 */
public class PagedCriteriaIterable<T> implements Iterable<T> {
    private static final int DEFAULT_PAGE_SIZE = 50;
    
    private final CriteriaQuery<JPAFacsimile> criteria;
    private final EntityManagerFactory emf;
    
    private Long ct = null; 
    
    private int pageSize = DEFAULT_PAGE_SIZE;
    
    PagedCriteriaIterable(CriteriaQuery<JPAFacsimile> criteria, EntityManagerFactory emf) {
        this.criteria = criteria;
        this.emf = emf;
    }
    
    /**
     * Return an estimate of the total number of items matched by this Query.
     */
     public synchronized long getTotalItems() {
        if (ct == null) {
            EntityManager em = null;
            try {
                em = emf.createEntityManager();
                ct = PersistenceUtil.findCountByCriteria(criteria, em);
            } finally { 
                em.close();
            }
        }

        return ct;
    }

    /** Set the number of items to return per page. */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    /** Get the number of items that will be returned per page; */
    public int getPageSize() {
        return this.pageSize;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return new PagedCriteriaIterator(criteria, emf, this.pageSize);
    }

    public Iterator<T> iterator(int page) {
        return null;
    }
    

    //========================================================================================
    //========================================================================================
    
    /**
     * 
     * @author Neal Audenaert
     * @param <T>
     */
    class PagedCriteriaIterator<T> implements Iterator<T> {
        private final CriteriaQuery<JPAFacsimile> criteria;
        private final EntityManagerFactory emf;
        
        boolean started = false;
        boolean atEnd = false;
        
        List<JPAFacsimile> results;
        Iterator<JPAFacsimile> resultsIterator;
        
        /** The number of results to return at a time. */
        private final int pageSize;
         
        /** The index of the next result page set to return. */
        private int nextPage = 0;
        
        /**
         * 
         * @param criteria
         * @param em
         * @param pageSize
         */
        PagedCriteriaIterator(CriteriaQuery<JPAFacsimile> criteria, 
                              EntityManagerFactory emf,
                              int pageSize) {
            this.criteria = criteria;
            this.emf = emf;
            this.pageSize = pageSize;
        }
        
        /**
         * Creates and executes a query based on this criteria to retrieve the next set 
         * of results. 
         */
        private void retrieveNextPage() {
            EntityManager em = null;
            try {
                em = this.emf.createEntityManager();
                results = em.createQuery(criteria).getResultList();
                resultsIterator = results.iterator();
            } finally { 
                em.close();
            }
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            if (resultsIterator == null) {
                started = true;
                
            }
            return (resultsIterator != null && resultsIterator.hasNext());
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public T next() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            // TODO Auto-generated method stub
            
        }
        
       
    }
}