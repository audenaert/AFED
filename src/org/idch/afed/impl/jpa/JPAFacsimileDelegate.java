/**
 * 
 */
package org.idch.afed.impl.jpa;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.idch.afed.Collation;
import org.idch.afed.FacsimileDelegate;
import org.idch.afed.FacsimileRepository;
import org.idch.afed.Image;
import org.idch.meta.DublinCore;
import org.idch.ms.Designation;

import org.idch.util.Field;
import org.idch.util.PersistentObject;

/**
 * @author Neal Audenaert
 */
@Entity
@Table(name="FACSIMILES")
public class JPAFacsimileDelegate extends PersistentObject<JPAFacsimileDelegate> implements FacsimileDelegate {

    /** The repository to be used to update this facsimile and to create 
     *  new resources (images, collations, featires, etc.). */
    private transient JPAFacsimileRepository repo;
   
    private Long id = null;
    
    private Field<String> name = fields.create((String)null);
    private Field<String> description = fields.create((String)null);
    private Field<String> date = fields.create((String)null);
    
    private Set<Designation> designations = new HashSet<Designation>();
    private Set<JPACollationDelegate> collations = new HashSet<JPACollationDelegate>(); 
    
    private DublinCore dc = null;
    
    // TODO use a UUID to support unique comparisons between facsimiles
    // TODO Create a more robust date mechanism that supports fuzzy historical dates and
    //      associated supporting documentation.
    
    //==============================================================================
    // CONSTRUCTORS
    //==============================================================================
    
    public JPAFacsimileDelegate() {
        this(null);
    }
    
    public JPAFacsimileDelegate(String name, String description, String date) {
        this(null, name, description, date);
    }
    
    
    JPAFacsimileDelegate(JPAFacsimileRepository repo) {
        this.initRepository(repo);
    }
    
    JPAFacsimileDelegate(JPAFacsimileRepository repo, 
            String name, String description, String date) {
        
        this.initRepository(repo);
        
        this.name.set(name);
        this.description.set(description);
        this.date.set(date);
    }
    
    void initRepository(FacsimileRepository repo) {
        // FIXME do we need a repo, or just an EMF? 
        // FIXME How do we initialize this when retrieving objects from JPA
        if (this.repo != null) {
            if ((null != repo) && (this.repo != repo)) {
                // TODO REPORT ERROR
            }
            
            return;
        }
        
        if (null == repo) {
            repo = FacsimileRepository.getInstance();
        } 

        if (repo instanceof JPAFacsimileRepository) {
            this.repo = (JPAFacsimileRepository)repo;
            super.setEntityManagerFactory(this.repo.getEmf());
        } else {
            this.repo = null;
            // TODO throw exception
        }
    }

    //=========================================================================
    // FACSIMILE DESIGNATION METHOS 
    //=========================================================================
    
    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#listDesignations()
     */
    @ManyToMany @Override
    public Set<Designation> getDesignations() {
//        boolean autoAttached = false;
//        if (this.isAttached()) { 
//            this.attach();
//            autoAttached = true;
//        }
//        
//        Set<Designation> desgns = this.designations.value();
//        
//        if (autoAttached) {
//            this.detach();
//        }
            
        return this.designations;
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#getDesignation(java.lang.String)
     */
    @Override
    public Designation getDesignation(String scheme) {
        // FIXME this is really inefficient. Requires full DB load.
        Designation result = null;
        for (Designation d : this.designations) {
            if (d.getScheme().equals(scheme)) {
                result = d;
                break;
            }
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#hasDesignation(java.lang.String)
     */
    @Override
    public boolean hasDesignation(String scheme) {
        return this.getDesignation(scheme) != null;
    }

   
    
    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#putDesignation(org.idch.ms.Designation)
     */
    @Override
    public Designation putDesignation(Designation d) {
        Designation existing = this.getDesignation(d.getScheme());
        if ((null != existing) && existing.equals(d)) {
            return null;        // no action is needed
        }
        
        if (existing != null) {
            // evict the existing designation for this scheme
            this.designations.remove(existing);
        }

        save(d);
        designations.add(d);
        return existing;
    }
    
    @Override
    public Designation removeDesignation(String scheme) {
        Designation existing = this.getDesignation(scheme);
        if (null == existing) {
            return null;
        }
        
        if (designations.remove(existing)) {
            return existing;
        } else {
            // ?? This shouldn't happen under normal circumstances
            return null;
        }
    }

   
    
    /**
     * Called by the persistence layer to set the designations for this facsimile. This
     * will overwrite any local changes and mark the Field object as un-modified.
     * 
     * @param designations
     */
    void setDesignations(Set<Designation> designations) {
        this.designations = designations;
    }
    
    //==============================================================================
    // ACCESSORS AND NON PUBLIC MUTATORS
    //==============================================================================
    
    @Id @GeneratedValue 
    Long getJPAId() {
        return this.id;
    }
    
    /** Called by the JPA framework to inject the id. */
    @SuppressWarnings("unused")
    private void setJPAId (Long id) {
        this.id = id;
    }
    
    /** 
     * Returns the name for this document.
     * 
     * @see org.idch.afed.Facsimile#getName()
     */
    @Override
    public String getName() {
        return name.value();
    }

    /** 
     * Returns a description of this document.
     * 
     * @see org.idch.afed.Facsimile#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description.value();
    }

    /** 
     * Returns the date this document is thought to have been created.
     * 
     * @see org.idch.afed.Facsimile#getDateOfOrigin()
     */
    @Override
    public String getDateOfOrigin() {
        return this.date.value();
    }
    
    @Override @Transient
    public DublinCore getDCMetadata() {
        return this.dc;
    }
    
    /** Called by the JPA framework to inject the DublinCore metadata. */
    @SuppressWarnings("unused") 
    private void setDCMetadata(DublinCore dc) {
        this.dc = dc;
    }
    
    //=========================================================================
    // MUTATORS
    //=========================================================================
    
    /** 
     * Sets the name for this document.
     * 
     * @see org.idch.afed.Facsimile#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Sets a description for this document. In general, this should be plain 
     * text or lightly formated HTML.
     * 
     * @see org.idch.afed.Facsimile#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String desc) {
        this.description.set(desc);
    }
    
    /**
     * Sets the date that this documetn is thought to have been created. 
     * 
     * @see org.idch.afed.Facsimile#setDateOfOrigin(java.lang.String)
     */
    @Override
    public void setDateOfOrigin(String date) {
        this.date.set(date);
    }

    //==============================================================================
    // COLLATION METHODS
    //==============================================================================
    
    /** Expose the collations set to the JPA persistence layer. */
    @OneToMany
    Set<JPACollationDelegate> getCollations() {
        return this.collations;
    }
    
    /** Used by the JPA persistence layer to set the collations for this facsimile. */
    void setCollations(Set<JPACollationDelegate> collations) {
        this.collations = collations;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#listCollations()
     */
    @Override
    public Set<String> listCollations() {
        // NOTE: We are dynamically generating the set of collation names every time someone
        //       tries to access them. This is time consuming, however, collation looks will 
        //       be relatively rare and the number of collations relatively small, so the 
        //       penalty should be minimal. We can improve the performance later if needed. 
        
        Set<String> result = new HashSet<String>();
        
        for (JPACollationDelegate c : this.collations) {
            result.add(c.getName());
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#hasCollation(java.lang.String)
     */
    @Override
    public boolean hasCollation(String collationName) {
        return this.listCollations().contains(collationName);
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#getCollation(java.lang.String)
     */
    @Override
    public Collation getCollation(String collationName) {
        Collation result = null;
        for (JPACollationDelegate c : this.collations) {
            if (c.getName().equals(collationName)) {
                result = new Collation(c);
                break;
            }
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#createCollation(java.lang.String)
     */
    public Collation createCollation(String collationName) {
        JPACollationDelegate delegate = new JPACollationDelegate(this, collationName, "");
        
        Collation c = new Collation(delegate);
        this.collations.add(delegate);
        
        return c;
    }
    
    //==============================================================================
    // IMAGE METHODS
    //==============================================================================
    
    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#addImage(java.lang.String, java.io.InputStream, java.lang.String)
     */
    @Override
    public Image addImage(String subContext, InputStream is, String type) {
        
//        JPAImageDelegate image = new JPAImageDelegate();
        return null;
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#listImages()
     */
    @Override
    public List<String> listImages() {
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
