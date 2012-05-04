/**
 * 
 */
package org.idch.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Neal Audenaert
 */
public class ListField<T> extends Field<List<T>> implements List<T> {
    
    /** Indicates whether the current value is the same object as the original 
     *  value. Since collection objects are modified in-place, the <tt>Field</tt> 
     *  must create a clone of the original that can be modified separately. 
     *  For performance reasons (memory and the running time of clone operation), 
     *  this clone is created on demand only when the client first attempts to 
     *  modify the List. Many applications will only need read access to their 
     *  fields (once initialized from the persistence layer) so this avoids
     *  un-necessary duplication of memory. 
     *  
     *  NOTE that the clone operation creates a shallow clone of List. Any direct
     *  modifications of the objects within the list will be reflected in both 
     *  the original and current representation and will not be considered
     *  when checking for the clean/dirty state or updated in response to the 
     *  revert/flush commands.
     */
    private boolean isCurrentValueLinked = true;

    /**
     * @param value
     */
    ListField(List<T> value) {
        super(value);
    }
    

    @Override
    public void set(List<T> value) {
        this.isCurrentValueLinked = this.original() == value;
        super.set(value);
    }
    
    @Override
    public List<T> value() {
        return this;
    }
    
    /**
     * Unlinks the original and current objects by setting the underlying current
     * set value to reference a cloned version of the original object.  
     */
    private void unlink() {
        if (this.isCurrentValueLinked) {
            // clone the original set
            List<T> clonedList = new ArrayList<T>(this.original());
            this.set(clonedList);
        }
    }

    //=========================================================================
    // QUERY METHODS (NO UNLINKING REQUIRED)
    //=========================================================================

    /**
     * @see java.util.List#get(int)
     */
    @Override
    public T get(int index) {
        return super.value().get(index);
    }
    
    /**
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object o) {
        return super.value().indexOf(o);
    }


    /**
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(Object o) {
        return super.value().lastIndexOf(o);
    }

    
    /**
     * Returns the size of this list. 
     * @see java.util.List#size()
     */
    @Override
    public int size() {
        return super.value().size();
    }


    /**
     * Indicates if this list is empty.
     * @see java.util.List#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return super.value().isEmpty();
    }


    /** 
     * Indicates whether this list contains the supplied object.
     * @see java.util.List#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return super.value().isEmpty();
    }

    /**
     * @see java.util.List#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return super.value().containsAll(c);
    }
    
    /**
     * Returns the contents of this list as an array.
     * @see java.util.List#toArray()
     */
    @Override
    public Object[] toArray() {
        return super.value().toArray();
    }

    /** 
     * Fills the supplied array with the contents of this list. 
     * @see java.util.List#toArray(T[])
     */
    @Override
    public <E> E[] toArray(E[] a) {
        return super.value().toArray(a);
    }

    //=========================================================================
    // MUTABLE METHODS (NEED TO UNLINK)
    //=========================================================================

    /**
     * Sets the element at a specific index in the list.
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public T set(int index, T element) {
        unlink();
        return super.value().set(index, element);
    }

    /**
     * Adds an element to the end of the list.
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public boolean add(T e) {
        unlink();
        return super.value().add(e);
    }
    
    /**
     * Inserts and element at a specific index.
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, T element) {
        unlink();
        super.value().add(index, element);
    }
    
    /** 
     * Adds all elements from a collection to the end of the list.
     * @see java.util.List#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        unlink();
        return super.value().addAll(c);
    }
    
    /**
     * Inserts all elements from a collection at the specified place in the list.
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        unlink();
        return super.value().addAll(index, c);
    }

    /**
     * @see java.util.List#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        unlink();
        return super.value().retainAll(c);
    }

    /**
     * @see java.util.List#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        unlink();
        return super.value().remove(o);
    }
    
    /**
     * @see java.util.List#remove(int)
     */
    @Override
    public T remove(int index) {
        unlink();
        return super.value().remove(index);
    }

    /**
     * @see java.util.List#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        unlink();
        return super.value().removeAll(c);
    }

    /**
     * @see java.util.List#clear()
     */
    @Override
    public void clear() {
        unlink();
        super.value().clear();
        
    }

    /** @see java.util.List#iterator() */
    @Override public Iterator<T> iterator() {
        return new ListFieldIterator<T>(this, super.value().listIterator());
    }

    /** @see java.util.List#listIterator() */
    @Override public ListIterator<T> listIterator() {
        return new ListFieldIterator<T>(this, super.value().listIterator());
    }


    /** @see java.util.List#listIterator(int)  */
    @Override public ListIterator<T> listIterator(int index) {
        return new ListFieldIterator<T>(this, super.value().listIterator(index));
    }


    /** @see java.util.List#subList(int, int) */
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        // NOTE Taking the easy way out. We could provide our own sub-list 
        //      implementation to avoid some unnecessary cloning if this 
        //      proves to be a bottle neck.
        unlink();
        return super.value().subList(fromIndex, toIndex);
    }
    
    //=========================================================================
    // ListIterator Wrapper
    //=========================================================================

    /**
     * Provides a thin wrapper around a <tt>ListIterator</tt> that supports
     * unlinking the underlying ListField for mutable iterator methods.
     * 
     * @author Neal Audenaert
     * @param <T>
     */
    private static class ListFieldIterator<T> implements ListIterator<T> {

        private final ListField<T> f;
        private final ListIterator<T> it;

        private ListFieldIterator(ListField<T> f, ListIterator<T> it) {
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

        /** @see java.util.ListIterator#hasPrevious() */
        @Override public boolean hasPrevious() {
            return it.hasPrevious();
        }

        /** @see java.util.ListIterator#previous() */
        @Override public T previous() {
            return it.previous();
        }

        /** @see java.util.ListIterator#nextIndex() */
        @Override public int nextIndex() {
            return it.nextIndex();
        }

        /** @see java.util.ListIterator#previousIndex() */
        @Override public int previousIndex() {
            return it.previousIndex();
        }

        /** @see java.util.ListIterator#remove() */
        @Override public void remove() {
            this.f.unlink();
            it.remove();
        }

        /** @see java.util.ListIterator#set(java.lang.Object) */
        @Override public void set(T e) {
            this.f.unlink();
            it.set(e);
        }

        /** @see java.util.ListIterator#add(java.lang.Object) */
        @Override public void add(T e) {
            this.f.unlink();
            it.add(e);
        }
    }
}
