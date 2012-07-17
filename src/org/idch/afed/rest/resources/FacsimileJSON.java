/**
 * 
 */
package org.idch.afed.rest.resources;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileMutator;

/**
 * Wraps a <tt>Facsimile</tt> instance to provide a simple API for generating JSON 
 * based representations.
 *  
 * @author Neal Audenaert
 */
public class FacsimileJSON implements Facsimile {
    
    public static final String ID_PROP = "id"; 
    public static final String NAME_PROP = "name"; 
    public static final String DESCRIPTION_PROP = "description"; 
    public static final String DATE_OF_ORIGIN_PROP = "dateOfOrigin"; 
    public static final String DISPLAY_IMAGE_PROP = "displayImage";
    public static final Set<String> PROPERTIES = new HashSet<String>();
    
    static {
        PROPERTIES.add(ID_PROP);
        PROPERTIES.add(NAME_PROP);
        PROPERTIES.add(DESCRIPTION_PROP);
        PROPERTIES.add(DATE_OF_ORIGIN_PROP);
        PROPERTIES.add(DISPLAY_IMAGE_PROP);
    }
    
    // FIXME this has dependencies on JAX-RS that need to be removed.
    private static final ObjectMapper mapper = new ObjectMapper(); 
     
    /**
     * 
     * @param is
     * @return
     * @throws WebApplicationException
     */
    public static FacsimileJSON create(InputStream is) throws WebApplicationException {
        try {
            return mapper.readValue(is, FacsimileJSON.class);
        } catch (Exception e) {
            String msg = "Could not parse the supplied facsimile represetation: " + 
                            e.getLocalizedMessage();
            FacsimileResource.LOGGER.warn("Client Error: " + msg, e);
            
            ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
            builder.entity("\"" + msg + "\"");
            
            throw new WebApplicationException(e, builder.build());
        }
    }
    
    /**
     * Creates a map-based representation of this facsimile's data. 
     * 
     * @param is
     * @return
     */
    public static Map<String, ?> asMap(InputStream is) {
        try {
            return mapper.readValue(is, new TypeReference<Map<String,?>>() { });
        } catch (Exception e) {
            String msg = "Could not parse the supplied facsimile represetation: " + 
                            e.getLocalizedMessage();
            FacsimileResource.LOGGER.warn("Client Error: " + msg, e);
            
            ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
            builder.entity("\"" + msg + "\"");
            
            throw new WebApplicationException(e, builder.build());
        }
    }
    
    /**
     * Applies a Map-based representation of JSON data to a mutator. The Mutator's set 
     * methods are called only if the corresponding property is set in the supplied data 
     * map.
     * 
     * @param json The incoming JSON data.
     * @param mutator The mutator to update.
     * @return The mutator.
     */
    public static FacsimileMutator apply(Map<String, ?> json, FacsimileMutator mutator) {
         // TODO need a cleaner way to set fields by introspecting the POJO and matching 
        //      fields to JSON properties

        if (json.containsKey(NAME_PROP)) {
            mutator.setName((String)json.get(NAME_PROP));
        }

        if (json.containsKey(DESCRIPTION_PROP)) {
            mutator.setDescription((String)json.get(DESCRIPTION_PROP));
        }

        if (json.containsKey(DATE_OF_ORIGIN_PROP)) {
            mutator.setDateOfOrigin((String)json.get(DATE_OF_ORIGIN_PROP));
        }

        if (json.containsKey(DISPLAY_IMAGE_PROP)) {
             // mutator.setDisplayImage((String)json.get(DISPLAY_IMAGE_PROP));
        }

        return mutator;
    }
    
    /**
     * Returns a JSON based representation of a Facsimile as a string using the default 
     * character encoding.
     * 
     * @param facsimile The facsimile for which a JSON representation should be created.
     * @return The JSON representation of the supplied facsimile.
     */
    public static String asJson(Facsimile facsimile) throws WebApplicationException {
        return asJson(facsimile, FacsimileResource.DEFAULT_ENCODING);
    }
    
    /**
     * Returns a JSON based representation of a Facsimile as a string.
     * 
     * @param facsimile The facsimile for which a JSON representation should be created.
     * @param encoding The character encoding for the returned string.
     * @return The JSON representation of the supplied facsimile.
     */
    public static String asJson(Facsimile facsimile, String encoding) throws WebApplicationException {
        FacsimileJSON json = new FacsimileJSON(facsimile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            mapper.writeValue(baos, json);
            return baos.toString(encoding);
        } catch (UnsupportedEncodingException e) {
            String msg = "The requested character encoding (" + encoding + ") is not available";
            FacsimileResource.LOGGER.warn("Client Error: " + msg, e);
            
            ResponseBuilder response = Response.status(Response.Status.NOT_ACCEPTABLE);
            response.entity("\"" + msg + "\"");
            throw new WebApplicationException(e, response.build());
        } catch (Exception ex) {
            // exceptions thrown for json generation and mapping or IO errors
            // These shouldn't happen and represent an internal error if they do.

            // TODO generate informative error message.
            FacsimileResource.LOGGER.error("", ex);
            ResponseBuilder response = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity("\"Internal Error: Could not write data to response.\"");
            throw new WebApplicationException(ex, response.build());
        }
    }
    
    /**
     * Returns a JSON based representation of the supplied map as a string using the 
     * default character encoding. The keys of this map should conform to the properties 
     * that are defined for the Facsimile JSON format. Any miscellaneous properties will 
     * be disregarded. 
     *  
     * @param data The data for which a JSON representation should be created.
     * @return The JSON representation of the supplied facsimile.
     */
    public static String asJson(Map<String, Object> data) {
        return asJson(data, FacsimileResource.DEFAULT_ENCODING);
    }
    
    /**
     * Returns a JSON based representation of the supplied map as a string. The keys of this 
     * map should conform to the properties that are defined for the Facsimile JSON format. 
     * Any miscellaneous properties will be disregarded.
     *  
     * @param data The data for which a JSON representation should be created.
     * @param encoding The character encoding for the returned string.
     * @return The JSON representation of the supplied facsimile.
     */
    public static String asJson(Map<String, Object> data, String encoding) {
        
        // filter by properties that are defined for facsimiles 
        HashMap<String, Object> jsonData = new HashMap<String, Object>();
        for (String key : data.keySet()) {
            if (PROPERTIES.contains(key))
                jsonData.put(key, data.get(key));
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            mapper.writeValue(baos, jsonData);
            return baos.toString(encoding);
        } catch (UnsupportedEncodingException e) {
            String msg = "The requested character encoding (" + encoding + ") is not available";
            FacsimileResource.LOGGER.warn("Client Error: " + msg, e);
            
            ResponseBuilder response = Response.status(Response.Status.NOT_ACCEPTABLE);
            response.entity("\"" + msg + "\"");
            throw new WebApplicationException(e, response.build());
        } catch (Exception ex) {
            // exceptions thrown for json generation and mapping or IO errors
            // These shouldn't happen and represent an internal error if they do.

            // TODO generate informative error message.
            FacsimileResource.LOGGER.error("", ex);
            ResponseBuilder response = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity("\"Internal Error: Could not write data to response.\"");
            throw new WebApplicationException(ex, response.build());
        }
    }
    
    private String id = null;
    private String name = null;
    private String description = null;
    private String dateOfOrigin = null;
    private String dispalyImage = null;
    
    public FacsimileJSON() {
        
    }
    
    public FacsimileJSON(Facsimile f) {
        this.name = f.getName();
        this.description = f.getDescription();
        this.dateOfOrigin = f.getDateOfOrigin();
        this.id = f.getId();
        // this.displayImage = f.getDisplayImage();
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateOfOrigin() {
        return dateOfOrigin;
    }

    public void setDateOfOrigin(String dateOfOrigin) {
        this.dateOfOrigin = dateOfOrigin;
    }

    @Override
    public URI getDisplayImage() {
        try {
            return (!StringUtils.isBlank(dispalyImage)) ? new URI(dispalyImage) : null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public void setDisplayImage(String dispalyImage) {
        // TODO validate that this is a legitimate URI
        this.dispalyImage = dispalyImage;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.dateOfOrigin + "): " + this.description; 
    }

}