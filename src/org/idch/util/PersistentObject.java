/**
 * 
 */
package org.idch.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Transient;

import org.hibernate.Session;
import org.idch.util.persist.RepositoryAccessException;

/**
 * @author Neal Audenaert
 */
public class PersistentObject<T> implements Attachable {
    
    protected final FieldManager fields = new FieldManager();
    
    private EntityManagerFactory emf;
//    private EntityTransaction tx;
//    private transient EntityManager em = null;
    
    protected PersistentObject() {
        
    }
    
    protected PersistentObject(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    protected void setEntityManagerFactory(EntityManagerFactory emf) {
//        if (this.isAttached()) {
//            throw new RepositoryAccessException(
//                    "Cannot update the EMF of an attached object.");
//        }
//        
        this.emf = emf;
    }
    
    @Transient
    protected FieldManager getFields() {
        return fields;
    }
    
//    @Transient
//    private boolean isUpdatable() {
//        return null != tx && tx.isActive();
//    }
    
    @Transient
    public boolean isAttached() {
        return false;
//        return (this.em != null) && this.em.isOpen();
    }
    
    /**
     * Makes this persistent object updatable if it is not already. Persistent objects may
     * be disconnected from their data store 
     * 
     * @throws RepositoryAccessException
     */
    protected void makeUpdatable() throws RepositoryAccessException {
//        if (isUpdatable()) return; // we already have an active transaction to use 
//        
//        if (!this.isAttached()) {
//            this.attach();
//        }
//        
//        this.tx = this.em.getTransaction();
//        if (!this.tx.isActive()) {
//            this.tx.begin();
//        } else {
//            // otherwise we're joining an existing transaction 
//            
//            // FIXME this will commit the other resources when this transaction is saved
//            //       which is probably not what we want
//        }
    }
    
    
    @SuppressWarnings("unused")
    private void joinTransaction(EntityTransaction tx) 
            throws RepositoryAccessException {
        
    }
    
    /**
     * 
     * @return
     * @throws RepositoryAccessException
     */
    public boolean save() throws RepositoryAccessException {
        // FIXME this breaks down when we try to use a single EM (and transaction) 
        //       across updates to multiple objects. We need to provide support for
        //       this scenario at some point.
        
        EntityManager em = emf.createEntityManager();
        Session sess = (Session)em.getDelegate();
        
        sess.getTransaction().begin();
        sess.saveOrUpdate(this);
        sess.flush();
        sess.getTransaction().commit();
        em.close();
        
        return true;
        
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
        
//        return success;
    }
    
    public Object save(Object obj) throws RepositoryAccessException {
        EntityManager em = emf.createEntityManager();
        Session sess = (Session)em.getDelegate();
        
        sess.getTransaction().begin();
        sess.saveOrUpdate(obj);
        sess.flush();
        sess.getTransaction().commit();
        em.close();
        
        return obj;
    }

    public void revert() throws RepositoryAccessException {
        
    }
    
    public boolean remove() {
        return false;
    }
    
    /**
     * Attaches this object to the persistent data store. Note that this will save any 
     * changes that may have been made to this object. Any subsequent changes that are made
     * prior to detaching the object will also be saved.
     * 
     * @see org.idch.util.Attachable#attach()
     */
    @Override
    public void attach() throws RepositoryAccessException {
//        this.attach(null);
    }
    
    @Override
    public void attach(EntityManager em) throws RepositoryAccessException {
//        // If an EM was supplied, make sure that we aren't attached to a different EM 
//        if ((null != em)  && this.isAttached()) {
//            if (this.em != em) {
//                throw new RepositoryAccessException("Cannot attach this object to the " +
//                        "provided EntityManager: already attached to a different " +
//                        "EntityManager.");
//            } 
//        }
//        
//        if (!this.isAttached()) {
//            // NOTE this ties us to Hibernate instead of JPA more generally
//            this.em = (em != null) ? em : this.emf.createEntityManager();
//          
//            Session session = (Session)this.em.getDelegate(); 
//            session.saveOrUpdate(this);
//        }
    }

    /* (non-Javadoc)
     * @see org.idch.afed.impl.jpa.Attachable#detach()
     */
    @Override
    public void detach() throws RepositoryAccessException {
//        this.em.close();
//        this.em = null;
    }

}
