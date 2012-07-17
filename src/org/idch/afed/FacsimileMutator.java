/**
 * 
 */
package org.idch.afed;

import java.net.URI;


/**
 * A mutator object that allows a single logical set of mutations to a facsimile object. In 
 * general, implementers should strive to ensure that the operations of a mutator are atomic
 * and that no changes are committed until the appropriate committing method is invoked 
 * (<tt>save</tt>, <tt>remove</tt> or <tt>revert</tt>). Implementations are not required to 
 * be thread safe as instances are intended to be confined to a single thread for the 
 * duration of their use.
 * 
 * <p>Clients are responsible for calling <tt>save</tt>, <tt>remove</tt> or <tt>revert</tt> 
 * exactly once before the mutable facsimile passes out of scope.
 * 
 * @author Neal Audenaert
 */
public interface FacsimileMutator {

    /**
     * Sets the name of this facsimile. The mutator must be saved for this change
     * to take affect.
     */
    public void setName(String name);

    /**
     * Sets the description of this facsimile. The mutator must be saved for this change
     * to take affect.
     */
    public void setDescription(String description);

    /**
     * Sets the date of origin of this facsimile. The mutator must be saved for this change
     * to take affect.
     */
    public void setDateOfOrigin(String dateOfOrigin);
    
    public boolean isClosed();

    /** 
     * Saves all changes made to this mutable facsimile. After invocation, this 
     * immutable cannot be re-used. This method finalizes all changes that have been 
     * made to the <tt>MutableFacsimile</tt> object and returns a reference to an 
     * immutable <tt>Facsimile</tt>. 
     * 
     * @return A reference to the saved <tt>Facsimile</tt>
     */
    public Facsimile save();

    /**
     * Removes this facsimile from the persistent store. After invocation, this 
     * immutable cannot be re-used. The results of this operation cannot be undone. 
     */
    public void remove();

    /**
     * Reverts changes made to this mutable facsimile, leaving the underlying 
     * persistence store unchanged. After invocation, immutable cannot be re-used. 
     * 
     * @return A reference to the unmodified <tt>Facsimile</tt> object. Note that if  
     *      other clients have modified this object, it may be in a different state than
     *      it was when this mutator was created.
     */
    public Facsimile revert();

    /**
     * Sets the URL of an image to use as the display image for this facsimile.
     * 
     * @param displayImage The URL of the image to be displayed for this facsimile.
     */
    void setDisplayImage(URI displayImage);

}
