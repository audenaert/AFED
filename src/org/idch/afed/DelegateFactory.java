/**
 * 
 */
package org.idch.afed;

import org.idch.afed.impl.jpa.JPACollationDelegate;
import org.idch.afed.impl.jpa.JPAFacsimileDelegate;
import org.idch.afed.impl.jpa.JPAImageDelegate;

/**
 * @author Neal Audenaert
 */
class DelegateFactory {
    
    public static FacsimileDelegate getFacsimileDelegate() {
        // TODO automagically look these up from config data
        return new JPAFacsimileDelegate();
    }
    
    public static FacsimileDelegate getFacsimileDelegate(
            String name, String desc, String date) {
        return new JPAFacsimileDelegate(name, desc, date);
    }

    public static CollationDelegate getCollationDelegate(Facsimile f) {
        return new JPACollationDelegate(f.getDelegate());
    }
    
    public static CollationDelegate getCollationDelegate(Facsimile f, String name, String desc) {
        return new JPACollationDelegate(f.getDelegate(), name, desc);
    }
    
    public static ImageDelegate getImageDelegate(Facsimile f, String ctx) {
        return new JPAImageDelegate(f.getDelegate(), ctx);
    }
}
