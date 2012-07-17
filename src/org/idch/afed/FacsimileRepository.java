/**
 * 
 */
package org.idch.afed;

import org.idch.util.persist.RepositoryAccessException;

/**
 * @author Neal Audenaert
 */
public interface FacsimileRepository {
    
    public void dispose();
    
    public boolean isDisposed();
    
    /**
     * Returns an immutable instance of the identified facsimile. 
     * 
     * @param id The id of the facsimile to return.
     * @return The identified facsimile or null if no such facsimile exists.
     */
    public Facsimile get(String id);
    
    /**
     * Returns a mutator that can be used to edit the identified facsimile.
     * 
     * @param id The id of the facsimile to be edited.
     * @return A mutator for the identified facsimile.
     */
    public FacsimileMutator getFacsimileMutator(String id);
    
    /** 
     * Creates a new <tt>Facsimile</tt>
     * 
     * @param name The display name of the facsimile to create.
     * @param desc A description of the facsimile to create.
     * @param date The date when the facsimile is thought to have been created.
     * @throws RepositoryAccessException If there are problems accessing the underlying
     *      persistent data store.
     */
    public Facsimile create(String name, String description, String date) 
            throws RepositoryAccessException;
    
    
    /**
     * TODO should return a FacsimileSet, an iterable that walks over all facsimiles
     *      in the document. This will allow for a variety of lazy fetching strategies.
     * @return
     */
    public Iterable<Facsimile> list();
    
//    /**
//     * 
//     * @param scheme
//     * @return
//     */
//    public List<String> list(String scheme);
//    
//    public void list(Designation d);
//    
//    public Facsimile get(String scheme);

    // TODO need a generic way of searching
    
    // TODO need a generic way of listening for changes
    //      FacsimileCreatedListener
    //      FacsimileChangedListener
    //      DataChangedListener

}
