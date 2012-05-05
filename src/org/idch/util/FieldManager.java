/**
 * 
 */
package org.idch.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FieldManager {
    private final List<Field<? extends Object>> fields = 
            new ArrayList<Field<? extends Object>>();
    
    public <T> Field<T> create(T value) {
        Field<T> f = new Field<T>(value);
        fields.add(f);
        
        return f;
    }
    
    public <T> SetField<T> create(Set<T> value) {
        return new SetField<T>(value);
    }
    
    public <T> ListField<T> create(List<T> value) {
        return new ListField<T>(value);
    }
    
    public boolean changed() {
        boolean changed = false;
        for (Field<? extends Object> f : fields) {
            if (f.changed()) {
                changed = true;
                break;
            }
        }
        
        return changed;
    }
    
    public void flush() {
        for (Field<? extends Object> f : fields) {
            f.flush();
        }
    }
    
    public void reset() {
        for (Field<? extends Object> f : fields) {
            f.reset();
        }
    }
}