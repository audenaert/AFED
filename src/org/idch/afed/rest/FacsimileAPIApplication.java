/**
 * 
 */
package org.idch.afed.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.idch.afed.FacsimileRepository;
import org.idch.afed.impl.jpa.JPAFacsimileRepository;
import org.idch.afed.rest.resources.FacsimileResource;

/**
 * @author Neal Audenaert
 */
public class FacsimileAPIApplication extends Application {
    
    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();
    
    private FacsimileRepository repo;
    
    public FacsimileAPIApplication() {
        initRepositories();
        
        singletons.add(new FacsimileResource(repo));
        // add other singleton resources
    }
    
    private void initRepositories() {
        // HACK: Should lookup from config file. Need to make the back end JPA 
        //       context configurable.
        repo = JPAFacsimileRepository.getInstance(/* context name */);
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        return empty;
    }
    
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
