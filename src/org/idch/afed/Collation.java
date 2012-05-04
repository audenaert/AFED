/**
 * 
 */
package org.idch.afed;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.idch.afed.Image;

/**
 * @author Neal Audenaert
 */
public class Collation {
    
    private final CollationDelegate delegate;
    
    //=========================================================================
    // CONSTRUCTORS
    //=========================================================================
    
    public Collation(Facsimile f) {
        this.delegate = DelegateFactory.getCollationDelegate(f);
    }
    
    public Collation(Facsimile f, String name, String description) {
        this.delegate = DelegateFactory.getCollationDelegate(f, name, description);
    }
    
    public Collation(CollationDelegate delegate) {
        this.delegate = delegate;
    }
    
    CollationDelegate getDelegate() { 
        return this.delegate;
    }
    
    //=========================================================================
    // ACCESSORS AND MUTATORS
    //=========================================================================
    
    /** Returns the display name of this collation.  */
    public String getName() {
        return delegate.getName();
    }

    /** Sets the display name of this collation. */
    public void setName(String name) {
        delegate.setName(name);
    }

    /** Returns a description of this collation. */
    public String getDescription() {
        return delegate.getDescription();
    }

    /** Sets a description for this collation. */
    public void setDescription(String desc) {
        delegate.setDescription(desc);
    }
    
    //=========================================================================
    // LIST METHOS (ACCESSORS)
    //=========================================================================
    
    /** @see java.util.List#get(int) */
    public Image get(int index) {
        return this.delegate.get(index);
    }
    
    /** @see java.util.List#size() */
    public int size() {
        return this.delegate.size();
    }

    /** @see java.util.List#indexOf(java.lang.Object) */
    public int indexOf(Object o) {
        return this.delegate.indexOf(o);
    }

    /** @see java.util.List#lastIndexOf(java.lang.Object) */
    public int lastIndexOf(Object o) {
        return this.delegate.lastIndexOf(o);
    }
    
    /** @see java.util.List#contains(java.lang.Object) */
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }
    
    /** @see java.util.List#containsAll(java.util.Collection) */
    public boolean containsAll(Collection<?> c) {
        return this.delegate.containsAll(c);
    }

    /** @see java.util.List#isEmpty() */
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    //=========================================================================
    // LIST METHOS (MUTATORS)
    //=========================================================================
    
    /** @see java.util.List#set(int, java.lang.Object) */
    public Image set(int index, Image element) {
        return this.delegate.set(index, element);
    }

    /** @see java.util.List#add(java.lang.Object) */
    public boolean add(Image e) {
        return this.delegate.add(e);
    }
    
    /** @see java.util.List#add(int, java.lang.Object) */
    public void add(int index, Image element) {
        this.delegate.add(index, element);
    }
    
    /** @see java.util.List#addAll(java.util.Collection) */
    public boolean addAll(Collection<? extends Image> c) {
        return this.delegate.addAll(c);
    }

    /** @see java.util.List#addAll(int, java.util.Collection) */
    public boolean addAll(int index, Collection<? extends Image> c) {
        return this.delegate.addAll(index, c);
    }

    /** @see java.util.List#remove(java.lang.Object) */
    public boolean remove(Object o) {
        return this.delegate.remove(o);
    }
    
    /** @see java.util.List#remove(int) */
    public Image remove(int index) {
        return this.delegate.remove(index);
    }
    
    /** @see java.util.List#retainAll(java.util.Collection) */
    public boolean retainAll(Collection<?> c) {
        return this.delegate.retainAll(c);
    }
    
    /** @see java.util.List#removeAll(java.util.Collection) */
    public boolean removeAll(Collection<?> c) {
        return this.delegate.removeAll(c);
    }

    /** @see java.util.List#clear() */
    public void clear() {
        this.delegate.clear();
    }

    /** @see java.util.List#subList(int, int) */
    public List<Image> subList(int fromIndex, int toIndex) {
        return this.delegate.subList(fromIndex, toIndex);
    }

    /** @see java.util.List#toArray() */
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    /** @see java.util.List#toArray(T[]) */
    public <T> T[] toArray(T[] a) {
        return this.delegate.toArray(a);
    }
    
    /** @see java.util.List#iterator() */
    public Iterator<Image> iterator() {
        return this.delegate.iterator();
    }

    /** @see java.util.List#listIterator() */
    public ListIterator<Image> listIterator() {
        return this.delegate.listIterator();
    }

    /** @see java.util.List#listIterator(int) */
    public ListIterator<Image> listIterator(int index) {
        return this.delegate.listIterator(index);
    }
}
