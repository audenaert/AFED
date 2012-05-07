/* Created on       Oct 3, 2007
 * Last Modified on $Date: 2008-07-17 19:08:24 $
 * $Revision: 1.1 $
 * $Log: ZoomableInfo.java,v $
 * Revision 1.1  2008-07-17 19:08:24  neal
 * Reattached NADL Project to a CVS Repository. This time the HTML, JS, and other webcomponents are being backed up as well.
 *
 * Revision 1.1  2007-11-08 15:39:19  neal
 * Creating a general project to provide a consistent codebase for NADL. This is being expanded to include most of the components from the old CSDLCommon and CSDLWeb packages, as I reorganize the package structure and improve those components.
 *
 * 
 * Copyright TEES Center for the Study of Digital Libraries (CSDL),
 *           Neal Audenaert
 *
 * ALL RIGHTS RESERVED. PERMISSION TO USE THIS SOFTWARE MAY BE GRANTED 
 * TO INDIVIDUALS OR ORGANIZATIONS ON A CASE BY CASE BASIS. FOR MORE 
 * INFORMATION PLEASE CONTACT THE DIRECTOR OF THE CSDL. IN THE EVENT 
 * THAT SUCH PERMISSION IS GIVEN IT SHOULD BE UNDERSTOOD THAT THIS 
 * SOFTWARE IS PROVIDED ON AN AS IS BASIS. THIS CODE HAS BEEN DEVELOPED 
 * FOR USE WITHIN A PARTICULAR RESEARCH PROJECT AND NO CLAIM IS MADE AS 
 * TO IS CORRECTNESS, PERFORMANCE, OR SUITABILITY FOR ANY USE.
 */
package org.idch.tzivi;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.idch.util.LogService;
import org.idch.util.xml.XmlDocument;
import org.idch.util.xml.XmlElement;

/** 
 * Maintains information about a single zoomable image. This information can 
 * be retrieved as XML data and restored from its XML form. 
 */
public class TziInfo {
    private static final String logger = TziInfo.class.getName();
    
    // Symbolic constants for XML element and attribute names
    public static final String DOC_ELEMENT          = "zoomableImage";
    public static final String WIDTH_ATTR           = "w";
    public static final String HEIGHT_ATTR          = "h";
    public static final String NAME_ELEM            = "name";
    public static final String THUMB_ELEM           = "thumb";
    public static final String TWIDTH_ELEM          = "tileWidth";
    public static final String THEIGHT_ELEM         = "tileHeight";
    public static final String TILE_EXT_ELEM        = "tileExtension";
    public static final String THUMBNAIL_ELEM       = "thumbnail";
    public static final String THUMB_SRC_ATTR       = "src";
    public static final String LAYERS_ELEM          = "layers";
    public static final String MIN_LAYER_ATTR       = "min";
    public static final String MAX_LAYER_ATTR       = "max";
    public static final String DEFAULT_LAYER_ATTR   = "default";
    
    private Map<Integer, ZoomableLayer> layers = 
        new HashMap<Integer, ZoomableLayer>();
    
    private int layerid = 0;
    
    private String name   = null;
    private int    width   = 0;    /** The width of the full image. */
    private int    height  = 0;    /** The height of the full image. */
    private int    twidth  = 0;    /** The width of the image tiles. */
    private int    theight = 0;    /** The height of the image tiles. */
    
    private String tileExtension = null;  /** The filename extension (e.g., jpeg) used for the tile image files. */ 
    private String thumbUrl = null;  /** The URL (relative to this image) of the thumbnail. */
    private long   thumbWidth = 0;   /** The width of the thumbnail image. */ 
    private long   thumbHeight = 0;  /** The height of the thumbnail image. */

    private int    defaultlayer = 0; /** The default zoom level for this tzi. */
     
    /** Constructs a new, empty <tt>ZoomableInfo</tt> object. */
    public TziInfo() { }
    
    /** Restores a <tt>ZoomableInfo</tt> object from its serialized form. */
    public TziInfo(Document doc) {
        this.restore(doc);
    }
    
    /** Specifies the name of the tzi described by this object. */
    void setName(String name)      { this.name    = name; }
    
    /**
     *  Specifies the width of full resolution layer of the tzi described 
     *  by this object. */
    void setWidth(int width)       { this.width   = width; }
    
    /**
     *  Specifies the height of full resolution layer of the tzi described 
     *  by this object. */
    void setHeight(int height)     { this.height  = height; }
    
    /**
     *  Specifies the width of the tiles used in the tzi described  
     *  by this object. */
    void setTileWidth(int width)   { this.twidth  = width; }
    
    /**
     *  Specifies the height of the tiles used in the tzi described  
     *  by this object. */
    void setTileHeight(int height) { this.theight = height; }
    
    /**
     * Sets the filename extension that is used for the tile images in this
     * tzi.
     * 
     * @param ext The extension to use
     */
    void setExtension(String ext) {
        this.tileExtension = ext;
    }
    
    void defineThumbnail(String url, long width, long height) {
        this.thumbUrl = url;
        this.thumbWidth = width;
        this.thumbHeight = height;
    }
    
   
    /**
     *  Specifies the layer that should be used as the first layer to display
     *  when accessign the the tzi described by this object. */
    public void setDefaultLayer(int layer) { 
        if ((layer > 0) && (layer < this.layerid))
            this.defaultlayer = layer;
        // otherwise take no action.
    }
    
    /** Returns the name of the tzi described by this object. */
    public String getName()               { return this.name; }
    
    /**
     *  Returns the width of full resolution layer of the tzi described 
     *  by this object. */
    public int    getWidth()              { return this.width; }
    
    /**
     *  Returns the height of full resolution layer of the tzi described 
     *  by this object. */
    public int    getHeight()             { return this.height; }
    
    /**
     *  Returns the width of the tiles used in the tzi described  
     *  by this object. */
    public int    getTileWidth()          { return this.twidth; }
    
    /**
     *  Returns the height of the tiles used in the tzi described  
     *  by this object. */
    public int    getTileHeight()         { return this.theight; }
    
    /**
     *  Returns the layer that should be used as the first layer to display
     *  when accessign the the tzi described by this object. */
    public int    getDefaultLayer()       { return this.defaultlayer; }
    
    /** 
     * Creates a new layer and returns the id of that layer. This assumes that
     * layers will be added sequentially, from smallest to largest. 
     */
    int createLayer() {
        ZoomableLayer layer = new ZoomableLayer(this.layerid++);
        this.layers.put(layer.getId(), layer);
        return layer.getId();
    }
    
    /**
     * Helper function to obtain a layer or else throw a runtime exception.
     * This should be called only by methods for which the absense of the 
     * specified layer constitutes a programming error.
     */
    private ZoomableLayer getLayer(int layerId) {
        if (this.layers.containsKey(layerId)) {
            return this.layers.get(layerId);
        } else throw new NoSuchElementException("No such layer: " + layerId);
    }
    
    /** Specifies the width of the indicated layer. */
    void setWidth(int layerId, int width) {
        this.getLayer(layerId).setWidth(width);
    }
    
    /** Specifies the height of the indicated layer. */
    void setHeight(int layerId, int height) {
        this.getLayer(layerId).setHeight(height);
    }
    
    /** Specifies the number of rows in the indicated layer. */
    void setRows(int layerId, int rows) {
        this.getLayer(layerId).setRows(rows);
    }
    
    /** Specifies the number of columns in the indicated layer. */
    void setCols(int layerId, int cols) {
        this.getLayer(layerId).setCols(cols);
    }
    
    /** Specifies the ratio of this layer to the largest layer in the tzi. */ 
    void setRatio(int layerId, float ratio) {
        this.getLayer(layerId).setRatio(ratio);
    }
    
    /** Returns the width of the indicated layer. */
    public int getWidth(int layerId) { 
        return this.getLayer(layerId).getWidth();
    }
    
    /** Returns the height of the indicated layer. */
    public int getHeight(int layerId) {
        return this.getLayer(layerId).getHeight();
    }
    
    /** Returns the number of rows in the indicated layer. */
    public int getRows(int layerId) {
        return this.getLayer(layerId).getRows();
    }
    
    /** Returns the number of columns in the indicated layer. */
    public int getCols(int layerId) {
        return this.getLayer(layerId).getCols();
    }
    
    /** Returns the ratio of this layer to the largest layer in the tzi. */ 
    public float getRatio(int layerId) {
        return this.getLayer(layerId).getRatio();
    }
    
    /** 
     * Returns an <tt>XmlDocument</tt> containing the information about the 
     * tzi represented by this object. 
     */
    public XmlDocument toXml() {
        XmlDocument doc = new XmlDocument(DOC_ELEMENT);
        XmlElement root = doc.getRoot();
        
        root.setAttribute(WIDTH_ATTR, this.width  + "");
        root.setAttribute(HEIGHT_ATTR, this.height + "");
        
        root.createElement(NAME_ELEM).createTextNode(this.name);
        root.createElement(TWIDTH_ELEM).createTextNode(this.twidth + "");
        root.createElement(THEIGHT_ELEM).createTextNode(this.theight + "");
        
        root.createElement(TILE_EXT_ELEM).createTextNode(this.tileExtension);

        if (this.thumbUrl != null) {
            XmlElement thumb = root.createElement(THUMBNAIL_ELEM);
            thumb.setAttribute(THUMB_SRC_ATTR, this.thumbUrl);
            thumb.setAttribute(WIDTH_ATTR, this.thumbWidth +"");
            thumb.setAttribute(HEIGHT_ATTR, this.thumbHeight +"");
        }

        XmlElement layers = root.createElement("layers");
        layers.setAttribute(MIN_LAYER_ATTR, 0 + "");
        layers.setAttribute(MAX_LAYER_ATTR, (this.layerid - 1) + "");
        layers.setAttribute(DEFAULT_LAYER_ATTR, this.defaultlayer + "");
        
        for (int i = 0; i < this.layerid; i++) {
            ZoomableLayer layer = this.layers.get(i);
            assert layer != null;
            if (layer == null) throw new RuntimeException("Internal Error.");
            
            layer.toXml(layers);
            
        }
        
        return doc;
    }
    
    /** Returns a JSON formatted representation of this tzi. */
    public String toJSON() {
        String json = "{\n" +
            "  \"width\" : "   + this.width + ",\n" + 
            "  \"height\" : "  + this.height + ",\n" + 
            "  \"name\" : \""  + this.name + "\",\n" + 
            "  \"tw\" : "      + this.twidth + ",\n" + 
            "  \"th\" : "      + this.theight + ",\n" +
            "  \"ext\" : \""     + this.tileExtension + "\",\n";
        
        if (thumbUrl != null) {
            json += "  \"thumb\" : { \n" +  
                    "    \"url\" : \""     + this.thumbUrl + "\",\n" + 
                    "    \"w\" : "       + this.thumbWidth + ",\n" + 
                    "    \"h\" : "       + this.thumbHeight + "\n" +
                    "  },\n";
        }
        
        json +=  "  \"layers\" : [\n";
        
        for (int i = 0; i < this.layerid; i++) {
            ZoomableLayer layer = this.layers.get(i);
            if (i > 0) json += ", \n";
            json += "    " + layer.toJSON();
            
        }
        json += "\n  ]\n}";
        
        return json;
    }
    
    /**
     * Used to restore the state of this object from its serialized XML form. 
     */ 
    private void restore(Document doc) {
        Element root        = doc.getDocumentElement();
        Element name    = null;
        Element twidth  = null;
        Element theight = null;
        Element layers  = null;
        
        NodeList items = root.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) continue;
            
            String strName = item.getNodeName();
            if (strName.equals(NAME_ELEM))         name = (Element)item;
            else if (strName.equals(TWIDTH_ELEM))  twidth = (Element)item;
            else if (strName.equals(THEIGHT_ELEM)) theight = (Element)item;
            else if (strName.equals(LAYERS_ELEM))  layers = (Element)item;
        }
        
        this.name = getTextContents(name).trim();
        String w  = root.getAttribute(WIDTH_ATTR);
        String h  = root.getAttribute(HEIGHT_ATTR);
        String tw = getTextContents(twidth).trim();
        String th = getTextContents(theight).trim();

        // restore layers
        // String min   = layers.getAttribute(MIN_LAYER_ATTR); // ignored
        String max   = layers.getAttribute(MAX_LAYER_ATTR);
        String start = layers.getAttribute(DEFAULT_LAYER_ATTR);
        NodeList layernodes = layers.getChildNodes();
        for (int i = 0; i < layernodes.getLength(); i++) {
            Node node = layernodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            if (!node.getNodeName().equals(ZoomableLayer.ELEMENT_NAME)) continue;
            
            ZoomableLayer layer = new ZoomableLayer((Element)node);
            this.layers.put(layer.getId(), layer);
        }
        
        try {
            this.width   = Integer.parseInt(w);
            this.height  = Integer.parseInt(h);
            this.twidth  = Integer.parseInt(tw);
            this.theight = Integer.parseInt(th);
            
            // we always start at layer 0.
            this.layerid = Integer.parseInt(max) + 1;
            this.defaultlayer = Integer.parseInt(start);
        } catch (NumberFormatException nfe) {
            String msg = "Invalid TZI Info XML: One of the following " +
                    "parameters is not a number: " +
                    "w: " + w + ", h: " + h + ", tw: " + tw + ", th: " + th + 
                    ", max: " + max + ", default: " + start;
            LogService.logError(msg, logger, nfe);
        }
    }
    
    /** Helper function used by restore to obtain the textual contents of an
     *  XML element. */
    private String getTextContents(Element el) {
        String result = "";
        
        el.normalize();
        NodeList items = el.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            if (item.getNodeType() != Node.TEXT_NODE) continue;
            result += item.getTextContent();
        }
        
        return result;
    }
    
    /** Represents a single layer within a TZI. */
    private static class ZoomableLayer {
        
        // Symbolic constants for the serialized XML form of the layer. */
        private static final String ELEMENT_NAME = "layer";
        private static final String ID_ATTR      = "n";
        private static final String WIDTH_ATTR   = "w";
        private static final String HEIGHT_ATTR  = "h";
        private static final String ROWS_ATTR    = "rows";
        private static final String COLS_ATTR    = "cols";
        private static final String RATIO_ATTR   = "ratio";
        
        private int   id     = 0;  /** The layer's id in the image.         */
        private int   width  = 0;  /** The width of the layer.              */
        private int   height = 0;  /** The height of the layer.             */
        private int   rows   = 0;  /** The number of rows in the layer.     */
        private int   cols   = 0;  /** The number of columns in the layer.  */
        private float ratio  = 0;  /** The ratio of the size of this layer   
                               to the size of the original image.   */
        /** Construct a layer with the specified id. */
        private ZoomableLayer(int id) { this.id = id; }
        
        
        private void  setWidth(int width)   { this.width  = width; }
        private void  setHeight(int height) { this.height = height; }
        private void  setRows(int rows)     { this.rows   = rows; }
        private void  setCols(int cols)     { this.cols   = cols; }
        private void  setRatio(float ratio) { this.ratio  = ratio; }
        
        private int   getId()               { return this.id; }
        private int   getWidth()            { return this.width; }
        private int   getHeight()           { return this.height; }
        private int   getRows()             { return this.rows; }
        private int   getCols()             { return this.cols; }
        private float getRatio()            { return this.ratio; }
    
        /** 
         * Add the contents of this layer's information as a child of the 
         * provided <tt>XmlElement</tt>
         */
        public void toXml(XmlElement element) {
            XmlElement layer = element.createElement(ELEMENT_NAME);
            layer.setAttribute(ID_ATTR,     id + "");
            layer.setAttribute(WIDTH_ATTR,  width + "");
            layer.setAttribute(HEIGHT_ATTR, height + "");
            layer.setAttribute(ROWS_ATTR,   rows + "");
            layer.setAttribute(COLS_ATTR,   cols+ "");
            layer.setAttribute(RATIO_ATTR,  ratio + "");
        }
        
        public String toJSON() {
            String json = "{" +
                "\"w\" : "     + this.width + "," +
                "\"h\" : "     + this.height + "," +
                "\"nRows\" : " + this.rows + "," +
                "\"nCols\" : " + this.cols + "," +
                "\"ratio\" : " + this.ratio + "" +
                "}";
            
            return json;
        }
        
        /** Restore this layer from an XML element. */
        private ZoomableLayer(Element el) {
            String n     = el.getAttribute(ID_ATTR);
            String w     = el.getAttribute(WIDTH_ATTR);
            String h     = el.getAttribute(HEIGHT_ATTR);
            String r     = el.getAttribute(ROWS_ATTR);
            String c     = el.getAttribute(COLS_ATTR);
            String ratio = el.getAttribute(RATIO_ATTR);
            
            try {
                this.id     = Integer.parseInt(n);
                this.width  = Integer.parseInt(w);
                this.height = Integer.parseInt(h);
                this.rows   = Integer.parseInt(r);
                this.cols   = Integer.parseInt(c);
                this.ratio  = Float.parseFloat(ratio);
            } catch (NumberFormatException nfe) {
                // TODO handle error
            }
        }
        
    }
    
    

}
