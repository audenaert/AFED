/**
 * 
 */
package org.idch.afed.impl.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileMutator;
import org.idch.afed.FacsimileRepository;
import org.idch.afed.rest.resources.FacsimileJSON;
import org.idch.util.persist.RepositoryAccessException;

/**
 * @author Neal Audenaert
 */
public class RESTFacsimileRepository implements FacsimileRepository {
    private static final Logger LOGGER = Logger.getLogger(RESTFacsimileRepository.class);
    
    private String encoding = "UTF-8";
    private final HttpClient client = new DefaultHttpClient();      // ThreadSafe
    private boolean disposed = false;
    
    private final String apiEndpoint;

    public RESTFacsimileRepository(String apiEndpoint /*, Credentials credentials */) {
        this.apiEndpoint = apiEndpoint;
    }
    
    protected void finalize() throws Throwable {
        if (!this.isDisposed()) {
            LOGGER.error("RESTFacsimileRepository was not properly shutdown. Please call 'dispose()' on all repositories before the pass out of scope.");
            this.dispose();
        }
    }
    
    @Override
    public void dispose() {
        synchronized (client) {
            ClientConnectionManager cm = client.getConnectionManager();
            cm.shutdown();
            disposed = true;
        }
    }

    @Override
    public boolean isDisposed() {
        synchronized(client) {
            return disposed;
        }
    }

    /**
     * Checks to see if a response was successful. If not, it throws a new 
     * <tt>RepositoryAccessException</tt> with the supplied error message and the reason
     * phrase from the response status. 
     *   
     * @param response The response to check. 
     * @param errmsg The error message prefix.
     * @throws RepositoryAccessException If the response indicates that the request 
     *      was not successful. 
     */
    private void checkSuccessfulResponse(HttpResponse response, String errmsg) 
            throws RepositoryAccessException {
        // TODO we may need to allow redirects to pass this as well.
        // TODO we may need to pass along the entity body, if supplied
        // TODO may need to create an extension of the base exceptio class
        
        StatusLine status = response.getStatusLine();
        Family family = Response.Status.fromStatusCode(status.getStatusCode()).getFamily();
        if (family != Family.SUCCESSFUL) {
            throw new RepositoryAccessException(errmsg + status.getReasonPhrase());
        }
    }
    
    /**
     * Instantiates a Facsimile from the results of an HTTP request. This calls 
     * @see #checkSuccessfulResponse(HttpResponse, String) to ensure that the supplied
     * response was successful. 
     *   
     * @param response The response which should contain an encoded representation of a 
     *      facsimile. 
     * @param errmsg The error message prefix to use if the request was not successful.
     * @return The <tt>Facsimile</tt> returned by the supplied response.
     * @throws IllegalStateException
     * @throws IOException
     * @throws RepositoryAccessException If the response indicates that the request 
     *      was not successful. 
     */
    private Facsimile instantiate(HttpResponse response, String errmsg) 
            throws RepositoryAccessException, IllegalStateException, IOException {
        checkSuccessfulResponse(response, errmsg);

        // TODO support representations other than JSON.
        InputStream is = null;
        try {
            HttpEntity respEntity = response.getEntity();
            is = respEntity.getContent();
            return new RESTFacsimile(this, FacsimileJSON.create(is));
        } finally {
            if (is != null) {
                try {
                    is.close();         // shuts down the underlying connection
                } catch (Exception ex) {
                    // LOG EXCEPTION
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.idch.afed.FacsimileRepository#get(long)
     */
    @Override
    public Facsimile get(String id) {
        String errmsg = "Could not retrieve facsimile (id='" + id + "'): ";
        String uri = this.apiEndpoint + "/facsimiles/" + id;
        
        HttpGet request = new HttpGet(uri);
        try {
            HttpResponse response = client.execute(request);
            return instantiate(response, errmsg);
        } catch (Exception e) {
            if (e instanceof RepositoryAccessException) {
                throw (RepositoryAccessException)e;
            }
            
            throw new RepositoryAccessException(errmsg + e.getMessage(), e);
        } 
    }

    /* (non-Javadoc)
     * @see org.idch.afed.FacsimileRepository#getFacsimileMutator(long)
     */
    @Override
    public FacsimileMutator getFacsimileMutator(String id) {
        return new RESTFacsimileMutator(id);
    }

    /* (non-Javadoc)
     * @see org.idch.afed.FacsimileRepository#create(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Facsimile create(String name, String description, String date)
            throws RepositoryAccessException {
        String errmsg = "Could not create new facsimile (name='" + name + "'): ";
        String uri = this.apiEndpoint + "/facsimiles";
        
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(FacsimileJSON.NAME_PROP, name);
        data.put(FacsimileJSON.DESCRIPTION_PROP, description);
        data.put(FacsimileJSON.DATE_OF_ORIGIN_PROP, date);
        
        HttpPost request = new HttpPost(uri);
        try {
            StringEntity entity = new StringEntity(FacsimileJSON.asJson(data), encoding);
            entity.setContentType("application/json");
            request.setEntity(entity);
            
            HttpResponse response = client.execute(request);
            return instantiate(response, errmsg);
        } catch (Exception e) {
            if (e instanceof RepositoryAccessException) {
                throw (RepositoryAccessException)e;
            }
            
            throw new RepositoryAccessException(errmsg + e.getMessage(), e);
        } 
    }

    /* (non-Javadoc)
     * @see org.idch.afed.FacsimileRepository#list()
     */
    @Override
    public Iterable<Facsimile> list() {
        // TODO Auto-generated method stub
        return null;
    }
    
    //=======================================================================================
    // INNER CLASSES
    //=======================================================================================
    
    /**
     * 
     * @author Neal Audenaert
     */
    private class RESTFacsimileMutator implements FacsimileMutator {
        
        private final String id;
        private final Map<String, Object> data = 
                Collections.synchronizedMap(new HashMap<String, Object>());
        private boolean closed = false;

        /**
         * 
         */
        public RESTFacsimileMutator(String id) {
            this.id = id;
        }
        
        @Override
        public void setName(String name) {
            checkClosed();
            data.put(FacsimileJSON.NAME_PROP, name);
        }

        @Override
        public void setDescription(String description) {
            checkClosed();
            data.put(FacsimileJSON.DESCRIPTION_PROP, description);
        }

        @Override
        public void setDateOfOrigin(String dateOfOrigin) {
            checkClosed();
            data.put(FacsimileJSON.DATE_OF_ORIGIN_PROP, dateOfOrigin);
        }

        @Override
        public void setDisplayImage(URI displayImage) {
            checkClosed();
            data.put(FacsimileJSON.DISPLAY_IMAGE_PROP, displayImage.toString());
        }
        
        private String toJson() {
            return FacsimileJSON.asJson(data);
        }
        
        private String getURI() {
            return RESTFacsimileRepository.this.apiEndpoint + "/facsimiles/" + id;
        }
        
        @Override
        public synchronized boolean isClosed() {
            return closed;
        }
        
        public synchronized void checkClosed() {
            if (isClosed()) {
                throw new RepositoryAccessException("The mutator has been closed (saved, removed, or reverted).");
            }
        }
        
        public synchronized void checkAndClose() {
            checkClosed();
            closed = true;
        }
        
        
        /* (non-Javadoc)
         * @see org.idch.afed.FacsimileMutator#save()
         */
        @Override
        public Facsimile save() throws RepositoryAccessException {
            checkAndClose();
            String errmsg = "Could not save changes to facsimile (id='" + id + "'): ";
            try {
                HttpPut request = new HttpPut(this.getURI());
                StringEntity entity = new StringEntity(this.toJson(), encoding);
                entity.setContentType("application/json");
                request.setEntity(entity);
                
                HttpResponse response = client.execute(request);
                return instantiate(response, errmsg);
            } catch (Exception e) {
                if (e instanceof RepositoryAccessException) {
                    throw (RepositoryAccessException)e;
                }
                
                throw new RepositoryAccessException(errmsg + e.getMessage(), e);
            } 
        }

        /* (non-Javadoc)
         * @see org.idch.afed.FacsimileMutator#remove()
         */
        @Override
        public void remove() throws RepositoryAccessException {
            checkAndClose();
            String errmsg = "Could not delete facsimile (id='" + id + "'): ";
            
            closed = true;
            HttpDelete request = new HttpDelete(this.getURI());
            try {
                HttpResponse response = client.execute(request);
                checkSuccessfulResponse(response, errmsg);
            } catch (Exception e) {
                if (e instanceof RepositoryAccessException) {
                    throw (RepositoryAccessException)e;
                }
                
                throw new RepositoryAccessException(errmsg + e.getMessage(), e);
            } 
        }

        /* (non-Javadoc)
         * @see org.idch.afed.FacsimileMutator#revert()
         */
        @Override
        public Facsimile revert() throws RepositoryAccessException {
            checkAndClose();
            data.clear();
            closed = true;
            
            return RESTFacsimileRepository.this.get(id);
        }
        
    }

}
