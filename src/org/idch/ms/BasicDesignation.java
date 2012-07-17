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
public class BasicDesignation implements Designation {
    // TODO need to break out the real version of this and the JPA backed version.
    
	/** The identifier used for a manuscript in a given scheme. For example,
	 *  <tt>01</tt> for Codex Sinaiticus in Gregory Alland. */
	private final String id;
	
	/** A short identifier for the naming scheme. For example, <tt>GA</tt> for 
	 *  Gregory Alland. */
	private final String scheme;
	
	//======================================================================================
	// CONSTRUCTORS
	//======================================================================================
	
	/**
	 * Constructs a new designation with the specified scheme and identifier.
	 * 
	 * @param scheme
	 * @param id
	 */
	public BasicDesignation(String scheme, String id) {
		this.scheme = scheme;
		this.id = id;
	}
	
	public BasicDesignation(BasicDesignation d) {
	    this.scheme = d.getScheme();
	    this.id = d.getId();
	}
	
	//======================================================================================
    // ACCESSORS 
    //====================================================================================== 

	/** Returns a short identifier for the naming scheme of this identifier, for example, 
	 * <tt>GA</tt> for Gregory Alland numbers. */
	public String getScheme() {
		return scheme;
	}

	/** The identifier for this designation. */
	public String getId() {
		return id;
	}
	
	//======================================================================================
    // EQUALITY METHOS
    //====================================================================================== 
	
	public String toString() {
	    return this.scheme + " " + this.id;
	}
	
	public boolean equals(Object o) {
	    if (o == this) {
	        return true;
	    }
	    
	    if (null == o || !(o instanceof BasicDesignation)) {
	        return false;
	    }
	    
	    BasicDesignation d = (BasicDesignation)o;
	    
	    return d.toString().equals(this.toString());
	}
	
	public int hashCode() {
	    return this.toString().hashCode();
	}

}
