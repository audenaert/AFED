/**
 * 
 */
package org.idch.afed.impl.jpa;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.idch.afed.CollationDelegate;
import org.idch.afed.FacsimileDelegate;
import org.idch.afed.FacsimileRepository;
import org.idch.afed.Image;
import org.idch.util.Field;
import org.idch.util.PersistentObject;

/**
 * @author Neal Audenaert
 */
@Entity
@Table(name="COLLATIONS")
public class JPACollationDelegate  extends PersistentObject<JPAFacsimileDelegate> implements CollationDelegate {
    /** The repository to be used to update this collation and to create 
     *  new resources (images, features, etc.). */
    private transient JPAFacsimileRepository repo;
    
    private Long id = null;
    
    private List<Image> images;
    private Field<String> name = fields.create((String)null);
    private Field<String> description = fields.create((String)null);
    
    private JPAFacsimileDelegate facsimile;
    
    //=========================================================================
    // CONSTRUCTORS
    //=========================================================================
    
    /** No-arg constructor as required by JPA. */
    JPACollationDelegate() {
        
    }
    
    public JPACollationDelegate(FacsimileDelegate facsimile) {
        
    }
    
    public JPACollationDelegate(FacsimileDelegate facsimile, String name, String desc) {
        
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
        } else if (repo instanceof JPAFacsimileRepository) {
            this.repo = (JPAFacsimileRepository)repo;
            super.setEntityManagerFactory(this.repo.getEmf());
        } else {
            this.repo = null;
            // TODO throw exception
        }
    }
    
    @Id @GeneratedValue 
    Long getJPAId() {
        return this.id;
    }
    
    /** Called by the JPA framework to inject the id. */
    @SuppressWarnings("unused")
    private void setJPAId (Long id) {
        this.id = id;
    }

    @ManyToOne
    JPAFacsimileDelegate getFacsimile() {
        return facsimile;
    }
    
    void setFacsimile(JPAFacsimileDelegate facsimile) {
        this.facsimile = facsimile;
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.Collation#getName()
     */
    @Override
    public String getName() {
        return name.value();
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Collation#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name.set(name);
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Collation#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description.value();
    }

    /* (non-Javadoc)
     * @see org.idch.afed.Collation#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String desc) {
        this.description.set(desc);
    }

    //=========================================================================
    // LIST PASS THROUGH METHODS
    //=========================================================================

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    @Override
    public int size() {
        return images.size();
    }

    /* (non-Javadoc)
     * @see java.util.List#isEmpty()
     */
    @Override @Transient
    public boolean isEmpty() {
        return images.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.List#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return images.contains(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#iterator()
     */
    @Override
    public Iterator<Image> iterator() {
        return images.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray()
     */
    @Override
    public Object[] toArray() {
        return images.toArray();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray(T[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return images.toArray(a);
    }

    /* (non-Javadoc)
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public boolean add(Image e) {
        return images.add(e);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        return images.remove(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return images.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends Image> c) {
        return images.addAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(int index, Collection<? extends Image> c) {
        return images.addAll(index, c);
    }

    /* (non-Javadoc)
     * @see java.util.List#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return images.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return images.retainAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#clear()
     */
    @Override
    public void clear() {
        images.clear();

    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    @Override
    public Image get(int index) {
        return images.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public Image set(int index, Image element) {
        return images.set(index, element);
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, Image element) {
        images.add(index, element);

    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    @Override
    public Image remove(int index) {
        return images.remove(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object o) {
        return images.indexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(Object o) {
        return images.lastIndexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator()
     */
    @Override
    public ListIterator<Image> listIterator() {
        return images.listIterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator(int)
     */
    @Override
    public ListIterator<Image> listIterator(int index) {
        return images.listIterator(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#subList(int, int)
     */
    @Override
    public List<Image> subList(int fromIndex, int toIndex) {
        return images.subList(fromIndex, toIndex);
    }


}
