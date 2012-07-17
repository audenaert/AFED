/**
 * 
 */
package org.idch.ms;

/**
 * Represents a designated identifier for this manuscript according to some specified 
 * reference scheme (GA number, ver Soden, shelf number). A single manuscript may have 
 * multiple designations.
 * 
 * @author Neal Audenaert
 */
public interface Designation {

    /** Returns a short identifier for the naming scheme of this identifier, for example, 
     * <tt>GA</tt> for Gregory Alland numbers. */
    public String getScheme();

    /** The identifier for this designation. */
    public String getId();
    
}
