/* Created on       May 28, 2010
 * Last Modified on $Date: $
 * $Revision: $
 * $Log: $
 *
 * Copyright Institute for Digital Christian Heritage,
 *           Neal Audenaert
 *
 * ALL RIGHTS RESERVED. 
 */
package org.idch.images;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over an image through a set of non-overlapping rectangular tiles.
 * 
 * @author Neal Audenaert
 */
public class TileIterator implements Iterator<BufferedImage> {
    
    /** The source image from which this tile is taken. */
    private final BufferedImage m_source;
    
    /** The width of this tile. */
    private int tileWidth;
    
    /** The height of this tile (in pixels). */
    private int tileHeight; 
    
    private final int width;  /** The width of the source image. */
    private final int height; /** The height of the source image. */
    private final int numCols;  /** Number of tiles horizontally. */
    private final int numRows;  /** Number of tiles vertically. */
    private final int numTiles;   /** Number of tiles in the image. */
    
    private final int imageType;
    
    private int ix = -1;           /** Index of the current tile. */
    
    /**
     * Creates a new <code>TileIterator</code> with square tiles.
     * 
     * @param source The image to be iterated over.
     * @param w The width and height of the tiles to use.
     */
    public TileIterator(BufferedImage source, int w) 
        throws IOException {
        this(source, w, w);
    }
    
    /**
     * Creates a new <code>TileIterator</code>.
     * 
     * @param source The image to be iterated over.
     * @param w The width of the tiles to use.
     * @param h The height of the tiles to use.
     */
    public TileIterator(BufferedImage source, int w, int h) 
        throws IOException {
        
        this.m_source = source;
        
        tileWidth  = w;
        tileHeight = h;

        width  = source.getWidth();
        height = source.getHeight();

        numCols = width / tileWidth + (width % tileWidth == 0 ? 0 : 1);
        numRows = height / tileHeight + (height % tileHeight == 0 ? 0 : 1);
        numTiles  = numCols * numRows;
        
        imageType = (source.getTransparency() == Transparency.OPAQUE) 
                        ? BufferedImage.TYPE_INT_RGB
                        : BufferedImage.TYPE_INT_ARGB;
    }
    
    /** 
     * Returns the source image this tile belongs to. 
     * 
     * @return the source image this tile belongs to.
     */
    public BufferedImage getSourceImage() { 
        return m_source;
    }
    
    /**
     * Returns the number of tiles in the horizonal direction.
     * 
     * @return the number of tiles in the horizonal direction
     */
    public int getNumXTiles() { 
        return numCols;
    }

    /**
     * Returns the number of tiles in the vertical direction.
     * 
     * @return the number of tiles in the vertical direction
     */
    public int getNumYTiles() { 
        return numRows;
    }
    
    /** 
     * Returns the x coordinate of the current tile relative to the 
     * source image.
     * 
     * @return the x coordinate of the current tile relative to the 
     *      source image.
     */
    public int getX() {
        return ix % numCols;
    }
    
    /** 
     * Returns the y coordinate of the current tile relative to the 
     * source image.
     * 
     * @return the y coordinate of the current tile relative to the 
     *      source image.
     */
    public int getY() {
        return ix / numCols;
    }
    
    /**
     * Returns the width of this tile in pixels. This adjusts for the fact that
     * tiles at the rightmost edge of the image may be more narrow that other 
     * tiles.
     * 
     * @return The width of this tile in pixels.
     */
    private int getWidth() {
        if (ix < 0) 
            throw new NoSuchElementException();
        
        int result = tileWidth;
        if ((getX() == numCols - 1) && (width % tileWidth != 0)) {
            // if this is the last column and the width isn't exactly divisible
            result = width % tileWidth;
        }
        
        return result;
    }
    
    /**
     * Returns the height of this tile in pixels. This adjusts for the fact that
     * tiles at the bottom edge of the image may be shorter that other tiles. 
     * 
     * @return The height of this tile in pixels.
     */
    private int getHeight() {
        if (ix < 0) 
            throw new NoSuchElementException();
        int result = tileHeight;
        if ((getY() == numRows - 1) && (height % tileHeight != 0)) {
            result = height % tileHeight;
        }
        
        return result;
    }
    
    /** @inheritDoc */
    public boolean hasNext() {
        return ix < (numTiles - 1);
    }

    /**
     * Returns the next tile in the underlying image. 
     */
    public BufferedImage next() {
        if (ix >= numTiles) 
            throw new NoSuchElementException();
        
        ix++;
        int c = ix % numCols;
        int r = ix / numCols;
        
        int w = getWidth();
        int h = getHeight();
        
        int x = c * tileWidth;
        int y = r * tileHeight;
        
        BufferedImage result = new BufferedImage(getWidth(), getHeight(), imageType);
        Graphics g = result.getGraphics();
        g.drawImage(m_source, 0, 0, w, h, x, y, (x + w), (y + h), null);
        g.dispose();
        
        return result;
    }
    
    /**
     * Unsupported operation.
     */
    public void remove() {
        throw new UnsupportedOperationException(
                "Cannot remove a tile from an image.");
    }
}