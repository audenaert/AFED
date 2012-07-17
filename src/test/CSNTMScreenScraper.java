/**
 * 
 */
package test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.idch.util.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

/**
 * @author Neal Audenaert
 */
public class CSNTMScreenScraper {
    
    Document getPage(String uri) throws Exception {
        HttpClient client;
        HttpGet request;
        HttpResponse response;
        Document doc = null;
        InputStream baos = null;
        
        
        try {
            // FIXME we're probably leaking connections heres
            client = new DefaultHttpClient();
            request = new HttpGet(uri);
            response = client.execute(request);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String respBody = EntityUtils.toString(entity);

                // TODO Not handling character encoding 
                baos = new ByteArrayInputStream(respBody.getBytes()); 
                Tidy jtidy = new Tidy();
                doc = jtidy.parseDOM(baos, (OutputStream)null);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            throw new Exception();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            baos.close();
        }
        
        return doc;
    }
    
    public List<String> extractPageURIs(Document page) {
        
//        XMLUtil.format(page, "transform.xslt");
//        TransformerFactory tFactory = TransformerFactory.newInstance();
//        Transformer transformer = tFactory.newTransformer(new StreamSource("transform.xslt"));
//        
//        transformer.transform(page, new StreamResult(new FileOutputStream("output.out")));
//        System.out.println("************* The result is in output.out *************");
//          } catch (Throwable t) {
//            t.printStackTrace();
//          }
//        }
        return null;
    }
    public void scrape() {
        try {
            Document page = getPage("http://csntm.org/Manuscript/View/GA_0209");
        } catch (Exception e) {
            System.out.println("Failed. Dying.");
        }
        
        
    }
    
    public static final void main(String args[]) {
        long mb = 1024*1024;
        long gb = mb * 1024;
        //        CSNTMScreenScraper scraper = new CSNTMScreenScraper();
        //        scraper.scrape();
        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " + 
                Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " + 
                Runtime.getRuntime().freeMemory() / mb + " MB");

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " + 
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / mb) + " MB");

        /* Total memory currently in use by the JVM */
        System.out.println("Total memory (bytes): " + 
                Runtime.getRuntime().totalMemory() / mb + " MB");

        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();

        /* For each filesystem root, print some info */
        for (File root : roots) {
            System.out.println();
            System.out.println("File system root: " + root.getAbsolutePath());
            System.out.println("Total space (bytes): " + (root.getTotalSpace() / gb) + " GB");
            System.out.println("Free space (bytes): " + (root.getFreeSpace() / gb) + " GB");
            System.out.println("Usable space (bytes): " + (root.getUsableSpace() / gb) + " GB");
        }
    }

}
