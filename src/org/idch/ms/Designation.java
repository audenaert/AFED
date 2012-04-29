/**
 * 
 */
package org.idch.ms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a designated identifier for this manuscript according to some specified 
 * reference scheme (GA number, ver Soden, shelf number). A single manuscript may have 
 * multiple designations.
 * 
 * @author Neal Audenaert
 */
@Entity
@Table(name="MS_DESIGNATIONS")
public class Designation {
    /** The persistent id of this designation. */
    private Long pId;
    
	/** The identifier used for a manuscript in a given scheme. For example,
	 *  <tt>01</tt> for Codex Sinaiticus in Gregory Alland. */
	private String id;
	
	/** A short identifier for the naming scheme. For example, <tt>GA</tt> for 
	 *  Gregory Alland. */
	private String scheme;
	
	//======================================================================================
	// CONSTRUCTORS
	//======================================================================================
	
	/** Default constructor required for JPA. */
	protected Designation() {
	    
	}
	
	/**
	 * Constructs a new designation with the specified scheme and identifier.
	 * 
	 * @param scheme
	 * @param id
	 */
	public Designation(String scheme, String id) {
		this.scheme = scheme;
		this.id = id;
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
    // ACCESSORS & MUTATORS REQUIRED FOR PERSISTENCE 
    //======================================================================================
	
	@Id @Column(name="uid") @GeneratedValue
    Long getPersistentId() { return pId; }
	
	void setPersistentId(Long id) { this.pId = id; }
    
    void setScheme(String value) { this.scheme = value; }
    
    void setId(String id) { this.id = id; }
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
	    
	    if (null == o || this.getClass() != o.getClass()) {
	        return false;
	    }
	    
	    Designation d = (Designation)o;
	    
	    return d.toString().equals(this.toString());
	}
	
	public int hashCode() {
	    return this.toString().hashCode();
	}

}
