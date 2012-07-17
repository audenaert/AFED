/**
 * 
 */
package org.idch.afed.impl.jpa;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileMutator;
import org.idch.util.persist.RepositoryAccessException;

import com.sun.corba.se.spi.activation._ActivatorImplBase;

/**
 * @author Neal Audenaert
 */
@Entity
@Table(name="FACSIMILES_2")
class JPAFacsimile  {
    private static final Logger LOGGER = Logger.getLogger(JPAFacsimile.class); 
    
    private static void logAndThrow(String msg, JPAFacsimile f) 
            throws RepositoryAccessException {
        logAndThrow(msg, f, null);
    }
    
    private static void logAndThrow(String msg, JPAFacsimile f, Throwable t) 
            throws RepositoryAccessException {
        msg += ": " + JPAFacsimileRepository.obfuscator.encode(f.id);
        if (t != null) {
            LOGGER.error(msg, t);
            throw new RepositoryAccessException(msg, t);
        } else {
            LOGGER.error(msg);
            throw new RepositoryAccessException(msg);
        }
    }
    
    @Transient
    JPAFacsimileRepository repo;
    
    @Id @GeneratedValue 
    private Long id = null;
    
    private String name;
    private String description;
    private String dateOfOrigin;
    private String displayImage;
    
    
//    @Transient
//    private Set<BasicDesignation> designations = new HashSet<BasicDesignation>();
//    
//    @Transient
//    private Set<JPACollationDelegate> collations = new HashSet<JPACollationDelegate>(); 
//    
//    @Transient
//    private DublinCore dc = null;
    
    /** Default constructor required by JPA */
    protected JPAFacsimile() {
        
    }
    
    JPAFacsimile(JPAFacsimileRepository repo, String name, String desc, String date) {
        this.repo = repo;
        this.name = name;
        this.description = desc;
        this.dateOfOrigin = date;
        
    }
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    public String getDateOfOrigin() {
        return dateOfOrigin;
    }
    
    public URI getDisplayImage() {
        // TODO return default image
        URI defaultImage = null;
        try {
            return (this.displayImage != null) ? new URI(this.displayImage) : defaultImage;
        } catch (URISyntaxException use) {
            LOGGER.error("Invalid display image URL: " + this.displayImage);
            return defaultImage;
        }
    }
    
    Facsimile asFacsimile() {
        return new ImmutableFacsimile(this);
    }
    
    FacsimileMutator asMutableFacsimile(JPAFacsimileRepository repo) {
        return new JPAMutableFacsimile(repo);
    }
    
    /** 
     * Provides an Facsimile representation that maintains a reference to the underlying 
     * repository, but is not a JPA managed Entity. This object cannot be modified. Note that 
     * some fields may be lazy loaded.
     * 
     * @author Neal Audenaert
     */
    private static class ImmutableFacsimile implements Facsimile {
        
        private final String id;
        private final String name;
        private final String description;
        private final String dateOfOrigin;
        private final URI displayImage;
        
        ImmutableFacsimile(JPAFacsimile facs) {
            this.id = JPAFacsimileRepository.obfuscator.encode(facs.id);
            this.name = facs.name;
            this.description = facs.description;
            this.dateOfOrigin = facs.dateOfOrigin;
            this.displayImage = facs.getDisplayImage();
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getDescription() {
            return this.description;
        }

        @Override
        public String getDateOfOrigin() {
            return this.dateOfOrigin;
        }
        
        @Override
        public URI getDisplayImage() {
            return this.displayImage;
        }
    }
    
    /**
     * 
     * <p>This class is not thread safe and is intended to be confined to a single thread
     * for use in one time transactions.
     * 
     * @author Neal Audenaert
     */
    private class JPAMutableFacsimile implements FacsimileMutator {
        // TODO provide for lazy locking.
        
        private final EntityManagerFactory emf;
        private final EntityManager em;
        private final EntityTransaction tx;
        private final JPAFacsimile facsimile;
        
        JPAMutableFacsimile(JPAFacsimileRepository repo) {
            emf = repo.emf;
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            
            facsimile = em.merge(JPAFacsimile.this);
        }
        
        /**
         * If the mutable facsimile has not already been closed, this takes care of that
         * and logs a warning message.
         */
        protected void finalize() {
            if (!this.isClosed()) {
                LOGGER.warn("Mutable facsimile was not closed. Clients that obtain a mutable facsimile must call 'save', 'remove' or 'revert' prior to the object passing out of scope.");
                this.close();
            }
        }
        
        @Override
        public void setName(String name) {
            facsimile.name = name;
        }

        @Override
        public void setDescription(String desc) {
            facsimile.description = desc;
        }

        @Override
        public void setDateOfOrigin(String dateOfOrigin) {
            facsimile.dateOfOrigin = dateOfOrigin;
        }
        

        @Override
        public void setDisplayImage(URI displayImage) {
            facsimile.displayImage = displayImage.toString();
        }
        
        private void close() {
            try {
                if (this.em.isOpen())
                    em.close();
            } catch (Exception ex) {
                LOGGER.warn("Could not close entity manager", ex);
            }
        }
        
        public boolean isClosed() {
            return !em.isOpen();
        }
        
        private void checkIsClosed() {
            if (this.isClosed()) {
                logAndThrow("Changes to this mutable facsimile have already been committed", 
                        this.facsimile);
            }
        }
        
        /** 
         * Saves all changes made to this mutable facsimile. After invocation, this 
         * immutable cannot be re-used. This method finalizes all changes that have been 
         * made to the <tt>MutableFacsimile</tt> object and returns a reference to an 
         * immutable <tt>Facsimile</tt>. 
         * 
         * @return A reference to the saved <tt>Facsimile</tt>
         */
        @Override
        public Facsimile save() throws RepositoryAccessException {
            checkIsClosed();
            try {
                em.flush();
                tx.commit();
                em.close();

                return facsimile.asFacsimile();
            } catch (Exception e) {
                logAndThrow("Could not save facsimile: ", this.facsimile, e);
                return null;
            } finally {
                this.close();
            }
        }
        
        /**
         * Removes this facsimile from the persistent store. After invocation, this 
         * immutable cannot be re-used. The results of this operation cannot be undone. 
         */
        @Override
        public void remove() {
            checkIsClosed();
            // TODO Allow the use of a status code rather than full object removal 
            //      so that we can track changes
            try {
                em.remove(this.facsimile);
                em.close();
                tx.commit();
            } catch (Exception e) {
                logAndThrow("Could not remove facsimile: ", this.facsimile, e);
            } finally {
                this.close();
            }
        }
        
        /**
         * Reverts changes made to this mutable facsimile, leaving the underlying 
         * persistence store unchanged. After invocation, immutable cannot be re-used. 
         * 
         * @return A reference to the unmodified <tt>Facsimile</tt> object. Note that if  
         *      other clients have modified this object, it may be in a different state than
         *      it was when this mutator was created.
         */
        @Override
        public Facsimile revert() {
            checkIsClosed();
            JPAFacsimile result = null;
            try {
                tx.rollback();
                
            } catch (Exception ex) {
                logAndThrow("Could not revert facsimile: ", this.facsimile, ex);
            } finally {
                String errmsg = "Could not retrieve unmodified facsimile: ";
                if (!this.isClosed()) {
                    try {
                        result = em.find(JPAFacsimile.class, this.facsimile.id);
                    } catch (Exception ex) {
                        logAndThrow(errmsg, this.facsimile, ex);
                    } finally {
                        this.close();
                    }
                } else { 
                    logAndThrow(errmsg, this.facsimile);
                }
            }
            
            return (result != null) ? result.asFacsimile() : null;
        }
    }

}
