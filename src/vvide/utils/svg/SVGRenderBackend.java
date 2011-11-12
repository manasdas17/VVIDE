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
package vvide.utils.svg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import vvide.signal.SignalRenderBackend;
import vvide.signal.TextAlign;
import vvide.signal.VisibleSignal;

/**
 * Backend for output a svg file
 */
public class SVGRenderBackend implements SignalRenderBackend {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Document to export
	 */
	private Document document;
	/**
	 * Root element
	 */
	private Element root;
	/**
	 * Graphic for getting a text sizes
	 */
	private Graphics2D textGraphics = (Graphics2D) new BufferedImage( 500, 250,
		BufferedImage.TYPE_INT_ARGB ).getGraphics();
	/**
	 * Group for signal
	 */
	private Element signalGroup;
	/**
	 * Group for signal line
	 */
	private Element signalLineGroup;
	/**
	 * A group to draw a lines etc.
	 */
	private Element currentGroup;
	/**
	 * A group to add a text
	 */
	private Element currentTextGroup;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param document
	 *        a document to export
	 */
	public SVGRenderBackend( Document document ) {
		this.document = document;

		// Creating a root
		root = document.createElement( "svg" );
		root.setAttribute( "xmlns:svg", "http://www.w3.org/2000/svg" );
		root.setAttribute( "xmlns", "http://www.w3.org/2000/svg" );
		root.setAttribute( "id", "svg2" );
		root.setAttribute( "version", "1.1" );
		document.appendChild( root );
	}

	@Override
	public void beginDrawSignal() {
		// Creating a signal group
		signalGroup = document.createElement( "g" );
		signalLineGroup = document.createElement( "g" );
		root.appendChild( signalGroup );
		signalGroup.appendChild( signalLineGroup );

		// Change a current group
		currentGroup = signalLineGroup;
		currentTextGroup = signalGroup;
	}

	@Override
	public void endDrawSignal() {}

	@Override
	public void drawBackground() {
		// Creating a group for timeline
		currentGroup = document.createElement( "g" );
		currentTextGroup = currentGroup;
		root.appendChild( currentGroup );
	}

	@Override
	public void drawSelectedSignalBorder( int offsetY ) {}

	@Override
	public void drawSignalInfo( VisibleSignal signal, int offsetY,
		long markerPosition, int infoWidth, Font signalNameFont, Font bitNrFont ) {

		// Draw a signal name
		String signalName = signal.getSignal().getFullPath();
		switch ( signal.getNumeralSystem() ) {
		case BINARY:
			signalName += " [B]";
			break;
		case HEXADECIMAL:
			signalName += " [H]";
			break;
		case UNSIGNED_DECIMAL:
			signalName += " [D]";
			break;
		case SIGNED_DECIMAL:
			signalName += " [+D]";
			break;
		}
		drawText( signalName, 2, offsetY, infoWidth, TextAlign.TOP,
			TextAlign.LEFT, signalNameFont, Color.BLACK );

		if ( signal.getSignal().getBitNr() > 0 ) {
			drawText( "#" + signal.getSignal().getBitNr(), 2, offsetY + 20,
				infoWidth, TextAlign.TOP, TextAlign.LEFT, bitNrFont,
				Color.BLACK );
		}
		
		// Change the text group
		currentTextGroup = signalLineGroup;
	}

	@Override
	public void drawLine( long x1, long y1, long x2, long y2, Color color ) {
		Element line = document.createElement( "line" );
		line.setAttribute( "x1", String.valueOf( x1 ) );
		line.setAttribute( "y1", String.valueOf( y1 ) );
		line.setAttribute( "x2", String.valueOf( x2 ) );
		line.setAttribute( "y2", String.valueOf( y2 ) );
		line.setAttribute( "style", "stroke:" + convertColor( color )
			+ "; stroke-width: 1px" );
		currentGroup.appendChild( line );
	}

	@Override
	public void drawText( String text, long x, long y, long maxWidth,
		TextAlign vAlign, TextAlign hAlign, Font font, Color color ) {

		font = adjustFont( font, text, maxWidth );
		LineMetrics lm =
			font.getLineMetrics( text, textGraphics.getFontRenderContext() );
		int width = textGraphics.getFontMetrics( font ).stringWidth( text );

		// Change the x position according to horizontal align
		switch ( hAlign ) {
		case RIGHT:
			x = (int) (x - width);
			break;
		case CENTER:
			x = x - (int) (width / 2);
			break;
		}

		// Change the x position according to horizontal align
		switch ( vAlign ) {
		case TOP:
			y = (int) (y + lm.getAscent());
			break;
		case CENTER:
			y = y + (int) (lm.getAscent() / 2);
			break;
		}

		Element textElement = document.createElement( "text" );
		textElement.setAttribute( "x", String.valueOf( x ) );
		textElement.setAttribute( "y", String.valueOf( y ) );
		textElement.setAttribute( "font-family", font
			.getFamily( Locale.ENGLISH ) );
		if ( font.isBold() ) textElement.setAttribute( "font-weight", "bold" );
		if ( font.isItalic() )
			textElement.setAttribute( "font-style", "italic" );
		textElement
			.setAttribute( "font-size", String.valueOf( font.getSize() ) );
		textElement.appendChild( document.createTextNode( text ) );
		currentTextGroup.appendChild( textElement );
	}

	/**
	 * Adjust a font size to pass in the maxWidth
	 * 
	 * @param font
	 *        font to use
	 * @param text
	 *        text to draw
	 * @param maxWidth
	 *        maximal width of the text
	 * @return new Font
	 */
	private Font adjustFont( Font font, String text, long maxWidth ) {
		long width = textGraphics.getFontMetrics( font ).stringWidth( text );
		long delta = 1;
		Font newFont = font;

		while ( width > maxWidth && delta < 5 ) {
			newFont =
				new Font( newFont.getFamily( Locale.ENGLISH ), newFont
					.getStyle(), newFont.getSize() - 1 );
			width = textGraphics.getFontMetrics( newFont ).stringWidth( text );
			delta++;
		}

		return newFont;
	}

	@Override
	public void drawPolyLine( long[] x, long[] y, Color color ) {
		Element polyLine = document.createElement( "polyline" );

		// build a points string
		StringBuffer pointsBuffer = new StringBuffer();
		for ( int i = 0; i < x.length; ++i ) {
			pointsBuffer.append( x[i] ).append( ',' ).append( y[i] ).append(
				' ' );
		}

		polyLine.setAttribute( "points", pointsBuffer.toString() );
		polyLine.setAttribute( "style", "fill:none; stroke:"
			+ convertColor( color ) + "; stroke-width: 1px" );
		currentGroup.appendChild( polyLine );
	}

	@Override
	public void fillRectangle( long x1, long y1, long x2, long y2, Color color ) {
		Element rect = document.createElement( "rect" );
		rect.setAttribute( "x", String.valueOf( x1 ) );
		rect.setAttribute( "y", String.valueOf( y1 ) );
		rect.setAttribute( "width", String.valueOf( x2 - x1 ) );
		rect.setAttribute( "height", String.valueOf( y2 - y1 ) );

		rect.setAttribute( "style", "fill:" + convertColor( color )
			+ "; stroke:" + color + "; stroke-width: 1px" );
		currentGroup.appendChild( rect );
	}

	@Override
	public void drawHexagon( long startX, long stopX, int offsetY, int lowOffset,
		int middleOffset, int highOffset, Color color ) {
		Element polygon = document.createElement( "polygon" );

		// build a points string
		StringBuffer pointsBuffer = new StringBuffer();
		pointsBuffer.append( startX ).append( ',' ).append(
			offsetY + middleOffset ).append( ' ' );
		pointsBuffer.append( startX + 3 ).append( ',' ).append(
			offsetY + highOffset ).append( ' ' );
		pointsBuffer.append( stopX - 3 ).append( ',' ).append(
			offsetY + highOffset ).append( ' ' );
		pointsBuffer.append( stopX ).append( ',' ).append(
			offsetY + middleOffset ).append( ' ' );
		pointsBuffer.append( stopX - 3 ).append( ',' ).append(
			offsetY + lowOffset ).append( ' ' );
		pointsBuffer.append( startX + 3 ).append( ',' ).append(
			offsetY + lowOffset );

		polygon.setAttribute( "points", pointsBuffer.toString() );
		polygon.setAttribute( "style", "fill:none; stroke:"
			+ convertColor( color ) + "; stroke-width: 1px" );
		currentGroup.appendChild( polygon );
	}

	/**
	 * Convert a color to string
	 * 
	 * @param color
	 *        a color
	 * @return string "rgb(R,G,B)"
	 */
	private String convertColor( Color color ) {
		return "rgb(" + color.getRed() + "," + color.getGreen() + ","
			+ color.getBlue() + ")";
	}
}
