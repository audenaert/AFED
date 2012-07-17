/**
 * 
 */
package org.idch.afed.impl.rest;

import java.net.URI;

import org.idch.afed.Facsimile;
import org.idch.afed.rest.resources.FacsimileJSON;

/**
 * @author Neal Audenaert
 */
class RESTFacsimile implements Facsimile {
//    http://localhost:8080/AFED
    RESTFacsimileRepository repo;
    
    private final String id;
    private final String name;
    private final String description;
    private final String dateOfOrigin;
    private final URI displayImage;
    
    RESTFacsimile(RESTFacsimileRepository repo, FacsimileJSON facs) {
        // TODO FacsimileJSON inherit from Facsimile 
        this.repo = repo;
        
        this.id = facs.getId();
        this.name = facs.getName();
        this.description = facs.getDescription();
        this.dateOfOrigin = facs.getDateOfOrigin();
        this.displayImage = facs.getDisplayImage();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getDateOfOrigin() {
        return this.dateOfOrigin;
    }

    @Override
    public URI getDisplayImage() {
        return displayImage;
    }
}
