/* Created on       Jul 6, 2010
 * Author: Neal Audenaert (neal@idch.org)
 * 
 * Last Modified on $Date: $
 * $Revision: $
 * $Log: $
 *
 * Copyright Institute for Digital Christian Heritage (IDCH) 
 *           All Rights Reserved.
 */
package org.idch.tzivi.legacy;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.idch.images.dz.FSTziCreator;
import org.idch.images.dz.TziConfig;
import org.idch.images.dz.TziConfig.ConfigurationException;
import org.idch.util.Filenames;
import org.idch.util.LogService;

/** 
 * Generates tiled, zoomable images (tzi). The <code>TziCreator</code> can be 
 * run in the context of a Java application or directly from the command-line.
 *  
 * @author Neal Audenaert
 */
public class TziGenerator {
    //=========================================================================
    // CONSTANTS
    //=========================================================================    
    private static final String LOGGER = TziGenerator.class.getName();
    
    private static final String HELP = "help";
    private static final String INTERACTIVE = "interactive";
    private static final String DEST = "dest";
    
    //=========================================================================
    // MEMBER VARIABLES
    //=========================================================================
    
    private File m_currentDirectory;
    private File m_outputDirectory;
    private TziConfig m_config;
    private List<File> m_images = new ArrayList<File>();
    
    //=========================================================================
    // STATIC METHODS
    //=========================================================================
    
    public static void main(String args[]) {
        Options options = getOptions();
        
        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmdLine = 
                parser.parse(options, args, true /* stopAtNonOption */);
            
            if (cmdLine.hasOption(HELP)) {
                printHelp(options);
                System.exit(0);
            }
            
            boolean verbose = false;
            boolean recurse = false;
            String dest = ".";
            if (cmdLine.hasOption("verbose")) 
                verbose = true;
            if (cmdLine.hasOption("recurse")) 
                recurse = true;
            if (cmdLine.hasOption(DEST)) 
                dest = cmdLine.getOptionValue(DEST);
            
            // get a tzi config instance 
            Map<String, String> params = getConfigParameters(cmdLine);
            TziConfig config = new TziConfig(params);
            if (verbose) System.out.println(config);
            
            String err = config.testConfig();
            if (err != null) {
                System.err.println("Invalid configuration:");
                System.err.println(err);
                System.exit(1);
            } 
            
            // Make a creator
            TziGenerator generator = new TziGenerator(config);
            generator.setOutputDir(dest);
            generator.queueFiles(cmdLine.getArgs(), recurse);
            generator.process();
            
        } catch (ParseException e) {
            System.err.println(e);
            printHelp(options);
            System.exit(1);
        } catch (TziConfig.ConfigurationException e) {
            System.err.println(e);
            printHelp(options);
            System.exit(2);
        } catch (IOException e) {
            System.err.println(e);
            printHelp(options);
            System.exit(2);
        }
    }
    
    /** 
     * Configure and return command line options.
     */
    private static Options getOptions() {
        Options opts = new Options();
        opts.addOption("h", HELP, false, "Print this message.");
        opts.addOption("i", INTERACTIVE, false, 
                "Enter configuration details interactively. This feature has not been implemented");
        opts.addOption("d", DEST, true, 
                "The root directory under which the tiled images should be saved.");
        opts.addOption("v", "verbose", false, "Print verbose output.");
        opts.addOption("R", "recurse", false, "Process directories recursively.");

        TziConfig that = new TziConfig(); // get default config
        
        // Image name
        opts.addOption("n", TziConfig.NAME, true, 
                "The name of this image. Defaults to '" + that.name + "'.");
        
        // Layers definition
        opts.addOption("x", TziConfig.MIN_WIDTH,  true, 
                "The maximum width of the smallest layer to create. The " +
                "default value is " + that.minWidth + ".");
        opts.addOption("y", TziConfig.MIN_HEIGHT, true, 
                "The maximum height of the smallest layer to create. The " +
                "default value is " + that.minHeight + ".");
        opts.addOption(null, TziConfig.SCALE,      true,  
                "The scale (as a percentage of the original image size) of the " +
                "smallest layer in the created tzi. If specfied, this value " +
                "value will be used instead of the defined width or height.");
        opts.addOption("ss", TziConfig.STEPSIZE,   true, 
                "The percentage (as a fraction) increase in size for each " +
                "layer. For example, the default value of " + that.stepSize + 
                " means that each layer will be " + (that.stepSize * 100) + 
                "% bigger than the previous layer.");
        
        // tile size
        opts.addOption("tw", TziConfig.TILE_WIDTH,  true, 
                "The width (in pixels) of the tiles to be created for the image.");
        opts.addOption("th", TziConfig.TILE_HEIGHT, true, 
                "The height (in pixels) of the tiles to be created for the image.");
        
        // thumbnail generation
        opts.addOption(null, TziConfig.NO_THUMB, false,
                "Supresses thumbnail generation. If this is not supplied, a" +
                "thumbnail image will be generated.");
        opts.addOption(null, TziConfig.THUMB_WIDTH,  true, 
                "The maximum width (in pixels) of the thumbnail to generate. " +
                "The default value is " + that.thumbWidth + ".");
        opts.addOption(null, TziConfig.THUMB_HEIGHT, true, 
                "The maximum height (in pixels) of the thumbnail to generate. " +
                "The default value is " + that.thumbHeight + ".");
        
        // tile format 
        opts.addOption("fmt", TziConfig.FORMAT, true, 
                "The format of the tiles to generate. Should be one of the " +
                "following: JPEG, TIFF, PNG. The default value is " + 
                that.format + ".");
        
        return opts;
    }
    
    /**
     * Generates a <code>Map</code> of the parameters specified on the command
     * line suitable for building a <code>TziConfig</code> instance.
     * 
     * @param cl The parsed command line.
     * @return The <code>Map</code> of configuration parameters.
     */
    private static Map<String, String> getConfigParameters(CommandLine cl) {
        Map<String, String> params = new HashMap<String, String>();
        
        // name parameter
        if (cl.hasOption(TziConfig.NAME)) params.put(TziConfig.NAME, cl.getOptionValue(TziConfig.NAME));
        
        // tile size parameters
        if (cl.hasOption(TziConfig.TILE_WIDTH))   params.put(TziConfig.TILE_WIDTH, cl.getOptionValue(TziConfig.TILE_WIDTH));
        if (cl.hasOption(TziConfig.TILE_HEIGHT))  params.put(TziConfig.TILE_HEIGHT, cl.getOptionValue(TziConfig.TILE_HEIGHT));
        
        // first layer parameters
        if (cl.hasOption(TziConfig.SCALE))        params.put(TziConfig.SCALE, cl.getOptionValue(TziConfig.SCALE));
        if (cl.hasOption(TziConfig.MIN_WIDTH))    params.put(TziConfig.MIN_WIDTH, cl.getOptionValue(TziConfig.MIN_WIDTH));
        if (cl.hasOption(TziConfig.MIN_HEIGHT))   params.put(TziConfig.MIN_HEIGHT, cl.getOptionValue(TziConfig.MIN_HEIGHT));
        if (cl.hasOption(TziConfig.STEPSIZE))     params.put(TziConfig.STEPSIZE, cl.getOptionValue(TziConfig.STEPSIZE));
        
        // thumbnail parameters
        if (cl.hasOption(TziConfig.NO_THUMB))     params.put(TziConfig.NO_THUMB, "true");
        else                                      params.remove(TziConfig.NO_THUMB);
        
        if (cl.hasOption(TziConfig.THUMB_WIDTH))  params.put(TziConfig.THUMB_WIDTH,  cl.getOptionValue(TziConfig.THUMB_WIDTH));
        if (cl.hasOption(TziConfig.THUMB_HEIGHT)) params.put(TziConfig.THUMB_HEIGHT, cl.getOptionValue(TziConfig.THUMB_HEIGHT));
        
        // tile format
        if (cl.hasOption(TziConfig.FORMAT))       params.put(TziConfig.FORMAT,  cl.getOptionValue(TziConfig.FORMAT));
        
        return params;
    }
    
    /**
    * Prints command-line usage help.
    */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("create-tzi [OPTIONS] [<file-or-dir> ...]", options);
    }
    
    //=========================================================================
    // CONSTRUCTORS
    //=========================================================================
    
    public TziGenerator(TziConfig config) {
        m_currentDirectory = new File(".");
        m_outputDirectory = new File(".");
        m_config = config;
    }
    
    //=========================================================================
    // INSTANCE METHODS
    //=========================================================================
    
    /**
     * Sets the directory to which the files will be written.
     */
    public final void setOutputDir(String dest) 
        throws IOException {
        
        File output = new File(dest).getCanonicalFile();
        if (!output.exists()) {
            // create this directory if it doesen't exist.
            LogService.logInfo("Creating output directory: " + output, LOGGER);
            if (!output.mkdirs()) {
                throw new IOException("Could not create destination directory");
            }
        }
        
        m_outputDirectory = output;
    }
    
    /** 
     * 
     * @param dir
     */
    public final void setDocumentRoot(File dir) {
        // TODO IMPLEMENT
        //      We'll use this as the root directory for source files and 
        //      create the sub-tree in the output file based on source file's
        //      relationship with this dir.
    }
    
    /** 
     * Enqueues the files to be processed. 
     * 
     * @param files The relative or absolute paths to the files to be 
     *      processed. 
     * @throws IOException If the file cannot be read or is not a file or 
     *      directory.
     */
    public final void queueFiles(String[] files, boolean recurse) 
        throws IOException {
        
        for (String name : files) {
            File file = new File(name).getCanonicalFile();
            if (!file.canRead()) {
                LogService.logWarn("Skipping file (" + file + "). Do not " +
                        "have permission to read " + file.getAbsolutePath(), 
                        LOGGER);
                continue;
            }
            
            if (file.isFile()) {
                queueFile(file);
            } else if (file.isDirectory()) {
                queueDirectory(file, recurse, null /* filter */);
            } else {
                throw new IOException("Path \"" + file + "\" is not a valid " +
                        "file or directory.");
            }
        }
    }
    
    /**
     * Adds all image files from a directory into 
     * 
     * @param dir The directory whose image files should be enqueued.
     * @param recurse If <code>true</code>, enqueue the directory recursively.
     * @param filter A custom filter to selectively exclude files from being
     *      included. If <code>null</code> no filtering will be performed.
     *  
     * @throws IOException If the files cannot be included in the queue. 
     */
    public final void queueDirectory(
            File dir, boolean recurse, FilenameFilter filter)
        throws IOException {
        
        LogService.logInfo("Asked to process directory " + dir, LOGGER);
        File[] files = dir.listFiles(filter);
        for (File file : files) {
            file = file.getCanonicalFile();
            if (!file.canRead()) {
                LogService.logWarn("Skipping file (" + file + "). " +
                        "Cannot read file.", LOGGER);
                continue;
            }
            
            if (file.isDirectory()) {
                if (recurse)
                    queueDirectory(file, recurse, filter);
            } else if (file.isFile()) {
                queueFile(file);
            } else {
                String msg = "Path \"" + file + "\" is not a valid directory";
                throw new IOException(msg);
            }
        }
    }

    /**
     * Enqueues an image file for later processing. Call <code>process</code>
     * to generate Tzi files from the enqueued files.
     * 
     * @param file The image to enqueue.
     */
    public final void queueFile(File file) {
        // TODO eventually we should create a class that will look 
        //      for metadata pertaining to this file.
        // TODO should create a PDF extractor that will import PDF files.
        
        assert file.isFile() : "Not a file: " + file;
        assert file.canRead() : "Cannot read file: " + file;
        
        String ext = Filenames.getExtension(file.getName()).toLowerCase();
        if (ext.equalsIgnoreCase("properties")) { // Java "properties" file with metadata
            return;
        } else if (ext.equalsIgnoreCase("conf")) { // configuration file
            return;
        } if (!ImageUtils.fileStoreOpts.containsKey(ext)) {
            LogService.logInfo("Skipping: " + file + ". Unknown file type (" + ext + "). ", LOGGER);
            return;
        }
        
        LogService.logInfo("Queueing: " + file, LOGGER);
        m_images.add(file);
    }
    
    /** 
     * Processes all queued images.
     */
    public final void process() throws IOException {
        String baseDirPath, pathToImage, relativePath;
        if (m_images.isEmpty()) {
            LogService.logInfo("No images to process", LOGGER);
            return;
        }
        
        RenderedImage image = null;
        FSTziCreator creator = null;
        baseDirPath = Filenames.getCanonicalPOSIXPath(m_currentDirectory);
        for (File imagefile : m_images) {
            
            // determine the destination directory
            pathToImage  = Filenames.getCanonicalPOSIXPath(imagefile);
            pathToImage  = 
                Filenames.getDirectory(pathToImage) + "/" + 
                Filenames.getBasename(pathToImage);
            relativePath = Filenames.getRelativePath(baseDirPath, pathToImage);
            File tziDir = new File(m_outputDirectory, relativePath);
            if (tziDir.exists()) {
                LogService.logWarn("Skipping: " + imagefile +". Destination " +
                		"directory (" + tziDir + ") already exists.", LOGGER);
                continue;
            }
            
            // load the image file
            image = ImageUtils.loadImage(imagefile);
            if (image == null) {
                LogService.logError(
                        "Could not read image file: " + imagefile, LOGGER);
                continue;
            }
            
            LogService.logInfo("Processing: " + imagefile + "\n\t" +
            		"Destination: " + tziDir, LOGGER);
            try {
                creator = new FSTziCreator(image, m_config, tziDir, false);
                creator.create();
            } catch (IOException ioe) {
                LogService.logError("Could not create tiled image for \"" +
                		image + "\".", LOGGER, ioe);
                // keep processing files...
            }
        }
    }
}
