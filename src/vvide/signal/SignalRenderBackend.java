/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package vvide.signal;

import java.awt.Color;
import java.awt.Font;

/**
 * Interface for Signal Render backend
 */
public interface SignalRenderBackend {

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Draw the background
	 */
	void drawBackground();

	/**
	 * Draw a border around the selected signal
	 * 
	 * @param offsetY
	 *        offset of the signal
	 */
	void drawSelectedSignalBorder( int offsetY );

	/**
	 * Draw Info about the signal
	 * 
	 * @param signal
	 *        visible signal
	 * @param offsetY
	 *        offset of the signal
	 * @param markerPosition
	 *        position of the selected marker
	 * @param infoWidth
	 *        width of the info area
	 * @param bitNrFont
	 *        Font to draw a bit number
	 * @param signalNameFont
	 *        font for signal name
	 */
	void drawSignalInfo( VisibleSignal signal, int offsetY,
		long markerPosition, int infoWidth, Font signalNameFont, Font bitNrFont );

	/**
	 * Notification, that new signal will be drawed
	 */
	void beginDrawSignal();

	/**
	 * Notification, that new signal is completely drawed
	 */
	void endDrawSignal();

	/**
	 * Draw a single line
	 * 
	 * @param startX
	 *        start x coordinate
	 * @param startY
	 *        start Y coordinate
	 * @param stopX
	 *        stop x coordinate
	 * @param startY2
	 *        stop y coordinate
	 * @param color
	 *        color of the line
	 */
	void drawLine( long startX, long startY, long stopX, long startY2, Color color );

	/**
	 * Draw a text
	 * 
	 * @param text
	 *        text to draw
	 * @param x
	 *        X position
	 * @param y
	 *        Y position
	 * @param maxWidth
	 *        maximum width of the text
	 * @param vAlign
	 *        vertical align
	 * @param hAlign
	 *        horizontal align
	 * @param font
	 *        font to use
	 * @param color
	 *        color of the text
	 */
	void drawText( String text, long x, long y, long maxWidth, TextAlign vAlign,
		TextAlign hAlign, Font font, Color color );

	/**
	 * Draw a poly Line
	 * 
	 * @param x
	 *        array with x coordinates
	 * @param y
	 *        array with y coordinates
	 * @param color
	 *        color of the line
	 */
	void drawPolyLine( long[] x, long[] y, Color color );

	/**
	 * Draw a filled rectangle
	 * 
	 * @param x1
	 *        x position of the left top corner
	 * @param y1
	 *        y position of the left top corner
	 * @param x2
	 *        x position of the right bottom corner
	 * @param y2
	 *        y position of the right bottom corner
	 * @param color
	 *        fill color
	 */
	void fillRectangle( long x1, long y1, long x2, long y2, Color color );

	/**
	 * Draw a hexagon
	 * 
	 * @param startX
	 *        left X coordinate
	 * @param stopX
	 *        right X coordinate
	 * @param offsetY
	 *        signal offset X
	 * @param lowOffset
	 *        value of the low offset
	 * @param middleOffset
	 *        value of the middle offset
	 * @param highOffset
	 *        value of the high offset
	 * @param color
	 *        color of the lines
	 */
	void drawHexagon( long startX, long stopX, int offsetY, int lowOffset,
		int middleOffset, int highOffset, Color color );
}
