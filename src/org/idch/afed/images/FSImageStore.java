/**
 * 
 */
package org.idch.afed.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.idch.util.Filenames;

/**
 * @author Neal Audenaert
 */
public class FSImageStore implements ImageStore {
    private static final Logger LOGGER = Logger.getLogger(ImageStore.class);
    // TODO create exceptions NotConnectedException, AlreadyConnectedException, ClosedException
    //      move synchronization details to abstract super class.

    public static final String DEFAULT_FORMAT = "jpeg";
    
    public static FSImageStore createImageStore(String path) throws IOException {
        FSImageStore store = new FSImageStore();
        store.setBaseDir(path);
        
        return store;
    }
    
    private File baseDir;   
    private String basePath;
    
    private boolean connected = false;
    private boolean allowOverwrite = false;
    
    private StateLock stateLock = new StateLock();
    
    //=========================================================================
    // CONSTRUCTORS & CONFIGURATION METHODS
    //=========================================================================
    

    /**
     * 
     */
    FSImageStore() {
        
    }
    
    public void setBaseDir(String path) throws IOException {
        synchronized (stateLock) {
            if (stateLock.isLocked()) {
                throw new IOException("Cannot update path: Already connected to image store.");
            }
            
            this.baseDir = new File(path);
            this.basePath = Filenames.getCanonicalPOSIXPath(this.baseDir);

            if (!this.baseDir.canWrite()) {
                throw new IOException("The supplied base directory is not writable: " + this.basePath);
            } else if (this.baseDir.exists() && !this.baseDir.isDirectory()) {
                throw new IOException("The supplied base directory is not a directory: " + this.basePath);
            }
        }
    }
    
    //=========================================================================
    // UTILITY METHODS
    //=========================================================================
    
    /** 
     * Checks to see if the supplied file is a sub-directory of this image 
     * store's base directory.
     *  
     * @param file  The file to check.
     * @return <tt>true</tt> if the supplied file is a sub directory of this 
     *      image store's base directory, <tt>false</tt> otherwise.
     */
    private boolean isSubdirectory(File file) {
        boolean result = false;;
        try {
            String path = Filenames.getCanonicalPOSIXPath(file);
            result = path.startsWith(this.basePath);
        } catch (IOException ioe) {
            LOGGER.error("Cannot find cannoical path ", ioe);
            result = false;
        }
        
        return result;
    }
    
    private void checkSubdirectory(File file) throws IOException {
        if (!isSubdirectory(file)) {
            String path = Filenames.getCanonicalPOSIXPath(file);
            throw new IOException("The supplied relative path points to a " +
                    "location that is not a sub-directory of this image " +
                    "store's root directory: " + path);
        }
    }
    
    private void createDirectories(File file) throws IOException {
        String dirname = Filenames.getDirectory(Filenames.getCanonicalPOSIXPath(file));
        File dir = new File(dirname);
        if (!dir.exists()) {
            LOGGER.debug("Creating directory: " + dirname);
            dir.mkdirs();
        }
    }
    
    private void overwriteIfNeeded(File file) throws IOException {
        if (file.exists()) {
            String path = Filenames.getCanonicalPOSIXPath(file);
            if (this.allowOverwrite && file.delete()) {
                LOGGER.info("Replacing existing image: " + path);
            } else {
                throw new IOException("A file already exists at this location ( " +
                        path + "). Refusing to overwrite existing file.");
            }
        }
    }
    
    //=========================================================================
    // ACCESSORS & MUTATORS
    //=========================================================================
    
    /** Allow the image store to overwrite existing resources. By default,
     *  overwriting existing resources is not allowed. */
    public void allowOverwrite() {
        allowOverwrite(true);
    }
    
    /** Indicate whether the image store can overwrite existing resources. By
     *  default, overwriting existing resources is not allowed.  */
    public void allowOverwrite(boolean flag) {
        allowOverwrite = flag;
    }
    
    //=========================================================================
    // DATA ACCESS METHOS
    //=========================================================================
    
    private void checkConnection() {
        if (!this.isConnected()) {
            throw new RuntimeException("Not Connected");
        }
    }
    
    /** 
     * @see org.idch.afed.images.ImageStore#store(java.lang.String, java.awt.image.BufferedImage)
     */
    @Override
    public void store(String relPath, BufferedImage image) throws IOException {
        store(relPath, image, "jpeg");
    }
    
    /** 
     * @see org.idch.afed.images.ImageStore#store(java.lang.String, java.awt.image.BufferedImage)
     */
    @Override
    public void store(String relPath, BufferedImage image, String format) throws IOException {
        checkConnection();
        
        File file = new File(baseDir, relPath);
        
        // sanity checks
        checkSubdirectory(file);
        overwriteIfNeeded(file);
        createDirectories(file);
        
        ImageIO.write(image, format, file);
    }
    
    /**
     * 
     * @param relPath
     * @return
     */
    @Override
    public boolean exists(String relPath) {
        checkConnection();
        
        File file = new File(baseDir, relPath);
        return isSubdirectory(file) && file.exists() && file.canRead();
    }
    
    /**
     * 
     */
    @Override
    public BufferedImage get(String relPath) throws IOException {
        checkConnection();
        
        File file = new File(baseDir, relPath);
        
        if (!exists(relPath)) {
            LOGGER.debug("The requested resource does not exist: " + 
                    Filenames.getCanonicalPOSIXPath(file));
            throw new IOException("The requested resource does not exist: " + relPath);
        }
        
        return ImageIO.read(file);
    }
    
    //=========================================================================
    // CONNECTION METHODS
    //=========================================================================
    
    @Override
    public void connect() throws IOException {
        synchronized (stateLock) {
            if (this.connected || this.stateLock.isLocked()) {
                throw new RuntimeException("Connection Failed: this image store is" +
                		"either already connected or else it's state has been locked.");
            }
            
            this.stateLock.lock();
            try {
                if (!this.baseDir.exists()) {
                    this.baseDir.mkdirs();
                }
                
                this.connected = true;
            } catch (Throwable t) {
                throw new IOException("Could not create parent directories."); 
            }
            
        }
    }
    
    @Override
    public boolean isConnected() {
        synchronized (stateLock) {
            return this.connected;
        }
    }
    
    @Override
    public void close() {
        synchronized (stateLock) {
            this.connected = false;
        }
    }
    
    @Override
    public boolean isClosed() {
        synchronized (stateLock) {
            return (stateLock.isLocked() && !this.connected);
        }
    }

    private class StateLock {
        boolean locked = false;
        
        public void lock() {
            locked = true;
        }
        
        public boolean isLocked() {
            return locked;
        }
    }
}
