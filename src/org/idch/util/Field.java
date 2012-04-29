/**
 * 
 */
package org.idch.util;

import java.util.Date;

public class Field<T> {
    private T originalValue;
    private T currentValue;
    private Date lastChange = new Date();
    
    Field(T value) {
        this.originalValue = value;
        this.currentValue = value;
    }
    
    /** Returns the current value of this field. */
    public T value() {
        return currentValue;
    }
    
    /** Sets the current value of this field. */
    public void set(T value) {
        this.currentValue = value;
        this.touch();
    }
    
    /** 
     * Updates the original value of this object to have the current value. In 
     * other words this flushes the modified state to the original state.  
     */
    public void flush() {
        this.originalValue = this.currentValue;
        this.touch();
    }
    
    /** Resets the current value of this field to the original value. */
    public void reset() {
        this.currentValue = this.originalValue;
        this.touch();
    }
    
    /** Marks the time the field field was last modified. */
    public void touch() {
        this.lastChange = new Date();
    }
    
    public Date getLastChanged() {
        return this.lastChange;
    }
    
    /**
     * Indicates whether this field's value has been modified.
     * @return
     */
    public boolean changed() {
        boolean changed = true;
        if (this.originalValue == null) {
            changed = this.currentValue != null;
        } else {
            changed = !this.originalValue.equals(this.currentValue);
        }
        
        return changed;
    }
}