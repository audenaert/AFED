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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.idch.afed.Collation;
import org.idch.afed.FacsimileDelegate;
import org.idch.afed.FacsimileRepository;
import org.idch.meta.DublinCore;
import org.idch.ms.Designation;

import org.idch.util.Attachable;
import org.idch.util.Field;
import org.idch.util.FieldManager;
import org.idch.util.PersistentObject;

/**
 * @author Neal Audenaert
 */
@Entity
@Table(name="FACSIMILES")
public class JPAFacsimileDelegate extends PersistentObject implements FacsimileDelegate, Attachable {

    /** The repository to be used to update this facsimile and to create 
     *  new resources (images, collations, annotations, etc.). */
    private transient JPAFacsimileRepository repo;
   
    private final FieldManager fields = new FieldManager();
    private Long id = null;
    // TODO use a UUID to support unique comparisons between facsimiles
    
    private Field<String> name = fields.create((String)null);
    private Field<String> description = fields.create((String)null);
    private Field<String> date = fields.create((String)null);
    
    // TODO create specialized field representation
    private Field<HashSet<Designation>> designations =
        fields.create(new HashSet<Designation>());
    
    private DublinCore dc = null;
    
    private Set<JPACollationDelegate> collations = new HashSet<JPACollationDelegate>();
    
//    @SuppressWarnings("unused")
//    private Map<String, Designation> designations = new HashMap<String, Designation>();
    
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
            if (repo instanceof JPAFacsimileRepository) {
                this.repo = (JPAFacsimileRepository)repo;
        }
            
        
    }

    //=========================================================================
    // FACSIMILE DESIGNATION METHOS 
    //=========================================================================
    
    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#listDesignations()
     */
    @Override 
    public HashSet<Designation> getDesignations() {
        return this.designations.value();
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#getDesignation(java.lang.String)
     */
    @Override
    public Designation getDesignation(String scheme) {
        // FIXME this is really inefficient. Requires full DB load.
        Designation result = null;
        HashSet<Designation> desgns = this.designations.value();
        for (Designation d : desgns) {
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

    /**
     * Returns the modifiable set of designations for this delegate. The field object used
     * to manage previous and updated states of a field requires that a new value be 
     * supplied. For mutable objects, this means that we need to create a new object and 
     * update the field with that object. Otherwise, we would simply be updating the 
     * internal state of the original value, not supplying a new value. This method will
     * examine the field object and, if it is unmodified, clone the existing collection
     * and and update the field. This is a time consuming process, so to minimize the 
     * number of times a potentially very large object must be cloned this will only 
     * replace the underlying collection if required. 
     * 
     * <p>This assumes that the returned object will be used and touches the field in 
     * order to update the timestamp.
     * 
     * @return A form of the modfiable object suitable for updates.
     */
    @Transient
    private HashSet<Designation> getModifiableDesignations() {
        this.makeUpdatable();
        
        HashSet<Designation> designations = null;
        if (!this.designations.changed()) {
            // Create a new map (if needed) to hold the modified values.
            designations = new HashSet<Designation>(this.designations.value());
            this.designations.set(designations);
        } else {
            designations = this.designations.value();
        }
        
        this.designations.touch();
        return designations;
    }
    
    /** (non-Javadoc)
     * @see org.idch.afed.Facsimile#setDesignation(org.idch.ms.Designation)
     */
    @Override
    public Designation putDesignation(Designation d) {
        Designation existing = this.getDesignation(d.getScheme());
        if ((null != existing) && existing.equals(d)) {
            return null;        // no action is needed
        }
        
        HashSet<Designation> designations = getModifiableDesignations();
        if (existing != null) {
            // evict the existing designation for this scheme
            designations.remove(existing);
        }

        designations.add(d);
        return existing;
    }
    
    @Override
    public Designation removeDesignation(String scheme) {
        Designation existing = this.getDesignation(scheme);
        if (null == existing) {
            return null;
        }
        
        HashSet<Designation> designations = getModifiableDesignations();
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
    void setDesignations(HashSet<Designation> designations) {
        this.designations.set(designations);
        this.designations.flush();
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
        this.makeUpdatable();
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
        this.makeUpdatable();
        this.description.set(desc);
    }
    
    /**
     * Sets the date that this documetn is thought to have been created. 
     * 
     * @see org.idch.afed.Facsimile#setDateOfOrigin(java.lang.String)
     */
    @Override
    public void setDateOfOrigin(String date) {
        this.makeUpdatable();
        this.date.set(date);
    }

   
    
    // TODO Create a more robust date mechanism that supports fuzzy historical dates and
    //      associated supporting documentation.
    
    //==============================================================================
    // COLLATION METHODS
    //==============================================================================
    

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
        
        for (Collation c : this.collations) {
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
    @Transient
    public Collation getCollation(String collationName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#createCollation(java.lang.String)
     */
    public Collation createCollation(String collationName) {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.Facsimile#addImage(java.lang.String, java.io.InputStream, java.lang.String)
     */
    @Override
    public void addImage(String subContext, InputStream is, String type) {
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
    
    public boolean remove() {
        return false;
    }
}
