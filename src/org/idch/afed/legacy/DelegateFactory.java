/**
 * 
 */
package org.idch.afed.legacy;

import org.idch.afed.legacy.ImageDelegate;
import org.idch.afed.impl.jpa.legacy.JPACollationDelegate;
import org.idch.afed.impl.jpa.legacy.JPAFacsimileDelegate;
import org.idch.afed.impl.jpa.legacy.JPAImageDelegate;

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

    public static CollationDelegate getCollationDelegate(BasicFacsimile f) {
        return new JPACollationDelegate(f.getDelegate());
    }
    
    public static CollationDelegate getCollationDelegate(BasicFacsimile f, String name, String desc) {
        return new JPACollationDelegate(f.getDelegate(), name, desc);
    }
    
    public static ImageDelegate getImageDelegate(BasicFacsimile f, String ctx) {
        return new JPAImageDelegate(f.getDelegate(), ctx);
    }
}
