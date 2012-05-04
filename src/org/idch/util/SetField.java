/**
 * 
 */
package org.idch.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Neal Audenaert
 */
public class SetField<T> extends Field<Set<T>> implements Set<T> {
    
    /** Indicates whether the current value is the same object as the original 
     *  value. Since collection objects are modified in-place, the <tt>Field</tt> 
     *  must create a clone of the original that can be modified separately. 
     *  For performance reasons (memory and the running time of clone operation), 
     *  this clone is created on demand only when the client first attempts to 
     *  modify the Set. Many applications will only need read access to their 
     *  fields (once initialized from the persistence layer) so this avoids
     *  un-necessary duplication of memory. 
     *  
     *  NOTE that the clone operation creates a shallow clone of Set. Any direct
     *  modifications of the objects within the set will be reflected in both 
     *  the original and current representation and will not be considered
     *  when checking for the clean/dirty state or updated in response to the 
     *  revert/flush commands.
     */
    private boolean isCurrentValueLinked = true;

    /**
     * @param value
     */
    SetField(Set<T> value) {
        super(value);
    }
    

    public void set(Set<T> value) {
        this.isCurrentValueLinked = this.original() == value;
        super.set(value);
    }
    
    public Set<T> value() {
        return this;
    }
    
    /**
     * Unlinks the original and current objects by setting the underlying current
     * set value to reference a cloned version of the original object.  
     */
    private void unlink() {
        if (this.isCurrentValueLinked) {
            // clone the original set
            Set<T> clonedSet = new HashSet<T>(this.original());
            this.set(clonedSet);
        }
    }

    //=========================================================================
    // MUTABLE METHODS (NEED TO UNLINK)
    //=========================================================================

    /**
     * Returns the size of this set. 
     * @see java.util.Set#size()
     */
    @Override
    public int size() {
        return super.value().size();
    }


    /**
     * Indicates if this set is empty.
     * @see java.util.Set#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return super.value().isEmpty();
    }


    /** 
     * Indicates whether this set contains the supplied object.
     * @see java.util.Set#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return super.value().isEmpty();
    }

    /**
     * @see java.util.Set#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return super.value().containsAll(c);
    }
    
    /**
     * Returns the contents of this set as an array.
     * @see java.util.Set#toArray()
     */
    @Override
    public Object[] toArray() {
        return super.value().toArray();
    }

    /** 
     * Fills the supplied array with the contents of this set. 
     * @see java.util.Set#toArray(T[])
     */
    @Override
    public <E> E[] toArray(E[] a) {
        return super.value().toArray(a);
    }
    

    //=========================================================================
    // MUTABLE METHODS (NEED TO UNLINK)
    //=========================================================================


    /**
     * @see java.util.Set#add(java.lang.Object)
     */
    @Override
    public boolean add(T e) {
        unlink();
        
        return super.value().add(e);
    }

    /** 
     * @see java.util.Set#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        unlink();
        
        return super.value().addAll(c);
    }
    
    /**
     * @see java.util.Set#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        unlink();
        
        return super.value().remove(o);
    }
    
    /**
     * @see java.util.Set#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        unlink();
        
        return super.value().removeAll(c);
    }

    /**
     * @see java.util.Set#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        unlink();
        
        return super.value().retainAll(c);
    }

    /**
     * @see java.util.Set#clear()
     */
    @Override
    public void clear() {
        unlink();
        
        super.value().clear();
        
    }

    /**
     * @see java.util.Set#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return new SetFieldIterator<T>(this, super.value().iterator());
    }
    
    //=========================================================================
    // Iterator Wrapper
    //=========================================================================

    /**
     * Provides a thin wrapper around a <tt>Iterator</tt> that supports
     * unlinking the underlying ListField for mutable iterator methods.
     * 
     * @author Neal Audenaert
     * @param <T>
     */
    private static class SetFieldIterator<T> implements Iterator<T> {

        private final SetField<T> f;
        private final Iterator<T> it;

        private SetFieldIterator(SetField<T> f, Iterator<T> it) {
            this.f = f;
            this.it = it;
        }
        
        /** @see java.util.ListIterator#hasNext() */
        @Override public boolean hasNext() {
            return it.hasNext();
        }

        /** @see java.util.ListIterator#next() */
        @Override public T next() {
            return it.next();
        }

        /** @see java.util.ListIterator#remove() */
        @Override public void remove() {
            this.f.unlink();
            it.remove();
        }

    }
}
