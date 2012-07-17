/**
 * 
 */
package org.idch.afed.rest.resources;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileMutator;
import org.idch.afed.FacsimileRepository;
import org.idch.util.persist.RepositoryAccessException;

/**
 * @author Neal Audenaert
 */
@Path("/facsimiles")
public class FacsimileResource {
    final static Logger LOGGER = Logger.getLogger(FacsimileResource.class); 
    
    public static final String DEFAULT_ENCODING = "UTF-8";

    private final FacsimileRepository repo;
    
    /**
     * @param repo
     */
    public FacsimileResource(FacsimileRepository repo) {
        this.repo = repo;
    }

    
    @GET
    @Produces("application/json")
    public String listAsJSON() throws Exception  {
        FacsimileLister lister = new FacsimileLister();
        // TODO set query parameters as needed
        
        return lister.asJSON();
    }
    
    @POST
    @Consumes("application/json")
    public String createFromJSON(InputStream is) throws Exception  {
        FacsimileJSON f = FacsimileJSON.create(is);
        
        Facsimile facsimile = this.repo.create(f.getName(), f.getDescription(), f.getDateOfOrigin());
        return FacsimileJSON.asJson(facsimile);
    }
    
    @GET 
    @Path("{id}")
    @Produces("application/json")
    public String getAsJSON(@PathParam("id") String id) throws Exception {
        Facsimile facsimile = this.repo.get(id);
        if (facsimile == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        return FacsimileJSON.asJson(facsimile);
    }
    
    @PUT 
    @Path("{id}")
    @Produces("application/json")
    public String updateFromJSON(@PathParam("id") String id,
                                 InputStream is) throws Exception {
        Map<String, ?> data = FacsimileJSON.asMap(is);
        
        String fId = (String)data.get(FacsimileJSON.ID_PROP);
        if ((fId != null) && !fId.equals(id)) {
            ResponseBuilder resp = Response.status(Response.Status.BAD_REQUEST);
            resp.entity("Could not update Facsimile. The id of the supplied representation (" + 
                    fId + " does not match the id of this facsimile " + id);
            throw new WebApplicationException(resp.build());
        }
        
        FacsimileMutator mutator = this.repo.getFacsimileMutator(id);
        FacsimileJSON.apply(data, mutator);
        
        try {
            return  FacsimileJSON.asJson(mutator.save());
        }  catch (RepositoryAccessException rae) {
            ResponseBuilder resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            resp.entity("Could not update Facsimile: " + id);
            throw new WebApplicationException(rae, resp.build());
        }
    }
    
    @DELETE 
    @Path("{id}")
    public Response remove(@PathParam("id") String id)  throws Exception {
        ResponseBuilder resp;
        try {
            FacsimileMutator mutator = this.repo.getFacsimileMutator(id);
            if (mutator != null) {
                mutator.remove();
            }
            resp = Response.noContent();
        } catch (Exception ex) {
            LOGGER.error("Could not delete facsimile: " + id, ex);
            
            // TODO need better error messaging.
            resp = Response.serverError();
            resp.entity(new StringEntity("Failed to delete facsimile (" + id + "): " + ex.getMessage()));
        }
        
        return resp.build();
    }
    
    //====================================================================================
    // INNER CLASSES
    //====================================================================================
    
    /**
     * 
     * @author Neal Audenaert
     */
    final class FacsimileLister {
        // GaurdedBy this
        private Iterable<Facsimile> facsimiles = null; 
        
        private String encoding = "UTF-8";
        
        FacsimileLister() {
            
        }
        
        public synchronized Iterable<Facsimile> list() {
            if (facsimiles == null) { 
                facsimiles = repo.list();
            }
            
            return facsimiles;
        }
        
        public String asJSON() throws WebApplicationException {
            Iterable<Facsimile> facsimiles = this.list();
            
            List<SimpleFacsimileJSON> results = new ArrayList<SimpleFacsimileJSON>();
            for (Facsimile f : facsimiles) {
                results.add(new SimpleFacsimileJSON(f));
            }
            
            ObjectMapper mapper = new ObjectMapper();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                mapper.writeValue(baos, results);
                return baos.toString(encoding);
            } catch (UnsupportedEncodingException e) {
                String msg = "The requested character encoding (" + this.encoding + ") is not available";
                LOGGER.warn("Client Error: " + msg, e);
                
                ResponseBuilder response = Response.status(Response.Status.NOT_ACCEPTABLE);
                response.entity("\"" + msg + "\"");
                throw new WebApplicationException(e, response.build());
            } catch (Exception ex) {
                // exceptions thrown for json generation and mapping or IO errors
                // These shouldn't happen and represent an internal error if they do.

                // TODO generate informative error message.
                LOGGER.error("", ex);
                ResponseBuilder response = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
                response.entity("\"Internal Error: Could not write data to response.\"");
                throw new WebApplicationException(ex, response.build());
            }
        }
    }
    
    private class FacsimileListHeader {
        private static final String RESOURCE_NAME = "List Facsimiles";
        
        
        
//        { "header" : { 
//            "name": "List Facsimiles", 
//            "requestUri" : "http://example.com/facsimiles",
//            "apiVersion": 0.1, 
//            "paging": {
//                "numReturned": 25, 
//                "totalMatched": 1348, 
//                "offset": 0,
//                "limit": 25, 
//                "self": "http://example.com/facsimiles.json?offset=0&limit=25", 
//                "last": "http://example.com/facsimiles.json?offset=1325&limit=25", 
//                "next": "http://example.com/facsimiles.json?offset=25&limit=25"
//            }, 
//            "params": {
//                "detail": false,
//                "before": null,
//                "after": null,
//                "q": null
//            }
//         },
        
    }
  
    @SuppressWarnings("unused")
    private class SimpleFacsimileJSON {
        private final Facsimile facsimile;
        
        SimpleFacsimileJSON(Facsimile f) {
            this.facsimile = f;
        }
        
        public String getId() {
            return this.facsimile.getId();
        }
        
        public String getName() {
            return facsimile.getName();
        }
        
        public String getDescription() {
            return facsimile.getDescription();
        }
        
        public String getDateOfOrigin() {
            return facsimile.getDateOfOrigin();
        }
    }

}
