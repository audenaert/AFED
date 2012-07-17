/**
 * 
 */
package test;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.idch.afed.Facsimile;
import org.idch.afed.FacsimileMutator;
import org.idch.afed.FacsimileRepository;
import org.idch.afed.impl.rest.RESTFacsimileRepository;
import org.idch.afed.rest.resources.FacsimileJSON;

/**
 * @author Neal Audenaert
 */
public class Test {

    private static String json =  "{ " +
                                  "  \"name\" : \"New Facsimile\"," +
                                  "  \"description\" : \"A New Facsimile\"," + 
                                  "  \"dateOfOrigin\" : \"IV\"" +
                                  "}";
    
    public static void evaluate(Facsimile f, String name, String desc, String date) {
        if (!f.getName().equals(name))
            System.err.println("Bad name value");
        if (!f.getDescription().equals(desc))
            System.err.println("Bad description value");
        if (!f.getDateOfOrigin().equals(date))
            System.err.println("Bad date value");
    }
    
    public static void main(String [] args) {
        
//        HttpClient client;
//        HttpPost request;
//        HttpResponse response;
//        Document doc = null;
//        
//        String uri = "http://localhost:8080/AFED/facsimiles";
//        try {
//            org.apache.commons.logging.LogFactory fact;
//            // FIXME we're probably leaking connections heres
//            client = new DefaultHttpClient();
//            request = new HttpPost(uri);
//            
//            StringEntity entity = new StringEntity(json, "UTF-8");
//            entity.setContentType("application/json");
//            request.setEntity(entity);
//            response = client.execute(request);
//
//            System.out.println(response.getStatusLine().toString());
//        } catch (ClientProtocolException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } finally {
//        }
        
        String name = "Another Facsimile";
        String desc = "This is a facsimile created via the RESTful API";
        String date = "XII";
        FacsimileRepository repo = new RESTFacsimileRepository("http://localhost:8080/AFED");
        Facsimile f = repo.create(name, desc, date);
        
        String id = f.getId();
        System.out.println("Created Facsimile: " + id);
        evaluate(f, name, desc, date);
        
        String name2 = "Fixed Name";
        String desc2 = "Another description because I didn't like the first";
        String date2 = "VII";
        FacsimileMutator mutator = repo.getFacsimileMutator(id);
        mutator.setName(name2);
        mutator.setDescription(desc2);
        mutator.setDateOfOrigin(date2);
        mutator.remove();
//        
//        f = mutator.save();
//        evaluate(f, name2, desc2, date2);
//        
//        f = repo.get(id);
//        evaluate(f, name2, desc2, date2);
        
        
//        
//        FacsimileJSON f = new FacsimileJSON();
//        f.setName("Another Facsimile");
//        f.setDescription("This is a facsimile created via the RESTful API");
//        f.setDateOfOrigin("XII");
//        
//        String json = null;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try {
//            mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
//            mapper.writeValue(baos, f);
//            json = baos.toString("UTF-8");
//        } catch (Exception ex) {
//            
//        }
//        
//        System.out.println(json);
        
        
    }
    
    static final ObjectMapper mapper = new ObjectMapper(); 
    static final Logger LOGGER = Logger.getLogger(Test.class);
    static final String DEFAULT_ENCODING = "UTF-8";
    


    
    
}
