/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This file based on the code of WaveForm Viewer project.
 * 
 * Copyright (C) 2010-2011 Department of Digital Technology
 * of the University of Kassel, Germany
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

package vvide.ui.views.wave;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.imageio.ImageIO;

import vvide.Application;
import vvide.logger.Logger;
import vvide.signal.AbstractSignal;
import vvide.signal.Marker;
import vvide.signal.TimeMetric;
import vvide.ui.views.WaveView;

/**
 * A Controller for displaying and manage the markers
 * Functions:
 * <ol>
 * <li>create and show markers
 * </ol>
 */
public class MarkerRender {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Image for a marker
	 */
	private BufferedImage markerImage = null;
	/**
	 * Image for a selected marker
	 */
	private BufferedImage selectedMarkerImage = null;

	/*
	 * ============================== Settings ===============================
	 */
	/**
	 * The height of the timeline area
	 */
	private int timeLineHeight;
	/**
	 * Width of the area with signal names
	 */
	private int infoWidth;
	/**
	 * A font, used for drawing a distance between the markers
	 */
	private Font markerDistanceFont;
	/**
	 * A color of the font, used for drawing a distance between the markers
	 */
	private Color distanceTextColor;
	/**
	 * Color of marker line for the selected marker
	 */
	private Color selectedMarkerColor;
	/**
	 * Color of marker line for the non-selected marker
	 */
	private Color nonSelectedMarkerColor;

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Init the render
	 */
	public void init() {

		// Load settings
		reloadSettings();
	}

	/**
	 * Load settings from the setting manager
	 */
	public void reloadSettings() {
		timeLineHeight = Application.settingsManager.getTimeLineHeight();
		infoWidth = Application.settingsManager.getInfoWidth();
		markerDistanceFont =
				Application.settingsManager.getMarkerDistanceFont();
		selectedMarkerColor =
				Application.settingsManager.getSelectedMarkerColor();
		nonSelectedMarkerColor =
				Application.settingsManager.getNonSelectedMarkerColor();
		distanceTextColor = Application.settingsManager.getDistanceTextColor();
		
		// Load Images
		try {
			markerImage =
					ImageIO.read( this.getClass().getResource(
							"/img/marker.png" ) );
			replaceColor(markerImage, nonSelectedMarkerColor);
			selectedMarkerImage =
					ImageIO.read( this.getClass().getResource(
							"/img/marker.png" ) );
			replaceColor(selectedMarkerImage, selectedMarkerColor);
		}
		catch ( IOException e ) {
			Logger.log( this.getClass().getCanonicalName(),
					Logger.MessageType.ERROR, "Can't load Marker Images" );
		}
	}

	/**
	 * Replace the gray color of the marker with the specified color
	 * @param markerImage image with a marker
	 * @param color a color to replace with
	 */
	private void replaceColor( BufferedImage markerImage,
			Color color ) {
		for (int y = 0; y < markerImage.getHeight(); ++y) {
			for (int x = 0; x < markerImage.getWidth(); ++x) {
				if (markerImage.getRGB( x, y ) == -8355712) {
					markerImage.setRGB( x, y, color.getRGB() );
				}
			}
		}
	}

	/**
	 * Draw a markers
	 * 
	 * @param g
	 *        a graphics2D to draw
	 * @param imageWidth
	 *        width of the graphics
	 * @param imageHeight
	 *        height of the graphics
	 * @param view
	 *        a WaveView
	 */
	public void drawMarkers( Graphics2D g, int imageWidth, int imageHeight,
			WaveView view ) {

		if ( g == null ) return;

		// Clear the graphic
		g.setBackground( new Color( 0, 0, 0, 0 ) );
		g.clearRect( 0, 0, imageWidth, imageHeight );

		// Sorting all markers by the time
		@SuppressWarnings( "unchecked" )
		Vector<Marker> markers =
				(Vector<Marker>) Application.markerManager.getMarkers().clone();

		if ( markers.size() == 0 ) return;

		Collections.sort( markers, new Comparator<Marker>() {

			@Override
			public int compare( Marker m1, Marker m2 ) {
				long dist = m1.getPosition() - m2.getPosition();
				int res = 0;
				if ( dist > 0 ) {
					res = 1;
				}
				else
					if ( dist < 0 ) {
						res = -1;
					}
				return res;
			}
		} );

		// Getting selected signal
		AbstractSignal selectedSignal =
				Application.signalManager.getSelectedSignal();

		// Draw markers, lines and times between the markers
		int lastVisibleIndex = -1;

		for ( int i = 0; i < markers.size(); i++ ) {
			// find visible
			if ( (markers.get( i ).getPosition() >= view.getStartVisibleTime())
				&& (markers.get( i ).getPosition() <= view.getEndVisibleTime()) ) {
				lastVisibleIndex = i;

				// Draw a marker and distance betwen the markers
				drawMarker( g, imageHeight, markers, i, view );
				drawMarkerDistance( g, imageWidth, markers, i, view,
						selectedSignal );
			}
		}

		// Draw lines to the markers that are not visible
		if ( lastVisibleIndex != markers.size() - 1 ) {
			drawMarkerDistance( g, imageWidth, markers, lastVisibleIndex + 1,
					view, selectedSignal );
		}

		// Draw a distance between two markers that are invisible, but visible
		// region is between those markers
		for ( int i = 0; i < markers.size(); i++ ) {
			// check, that (i+1) index is in the array
			if ( i <= markers.size() - 2 ) {
				if ( (markers.get( i ).getPosition() < view
						.getStartVisibleTime())
					&& (markers.get( i + 1 ).getPosition() > view
							.getEndVisibleTime()) ) {
					drawMarkerDistance( g, imageWidth, markers, i + 1, view,
							selectedSignal );
					break;
				}
			}
		}
	}

	/**
	 * Draw a single marker
	 * 
	 * @param g
	 *        a graphics2D to draw
	 * @param imageHeight
	 *        height of the graphics
	 * @param markers
	 *        a vector with all markers
	 * @param index
	 *        index of the marker to draw
	 * @param view
	 *        a WaveView
	 */
	private void drawMarker( Graphics2D g, int imageHeight,
			Vector<Marker> markers, int index, WaveView view ) {
		BufferedImage img = null;

		if ( markers.get( index ) == Application.markerManager
				.getSelectedMarker() ) {
			g.setColor( selectedMarkerColor );
			img = selectedMarkerImage;
		}
		else {
			g.setColor( nonSelectedMarkerColor );
			img = markerImage;
		}
		int x = (int)view.getCoordFromTime( markers.get( index ).getPosition() );

		g.drawImage( img, x - 3, timeLineHeight - 16, null );

		// Draw a lines
		g.drawLine( x, timeLineHeight + 4, x, imageHeight );
	}

	/**
	 * Draw a line with distance between the selected marker and the previous
	 * marker
	 * 
	 * @param g
	 *        a graphics2D to draw
	 * @param imageWidth
	 *        width of the graphics
	 * @param markers
	 *        a vector with all markers
	 * @param index
	 *        index of the marker to draw
	 * @param view
	 *        a WaveView
	 * @param selectedSignal
	 */
	private void drawMarkerDistance( Graphics2D g, int imageWidth,
			Vector<Marker> markers, int index, WaveView view,
			AbstractSignal selectedSignal ) {

		if ( index == 0 ) return;

		// Draw a distance between markers
		long x0 = view.getCoordFromTime( markers.get( index ).getPosition() );
		long x1 = view.getCoordFromTime( markers.get( index - 1 ).getPosition() );

		// Correct the left end of the line
		if ( x1 < infoWidth ) {
			x1 = infoWidth + 1;
		}
		// Correct the right end of the line
		if ( x0 > imageWidth ) {
			x0 = imageWidth;
		}

		g.setColor( distanceTextColor );
		g.drawLine( (int)x0, timeLineHeight - 15, (int)x1, timeLineHeight - 15 );

		// get a max width for a text
		long maxWidth = x0 - x1 - 2;

		// Calculate width of the text
		long distance =
				markers.get( index ).getPosition()
					- markers.get( index - 1 ).getPosition();

		String distanceText =
				""
					+ TimeMetric.toString( distance, Application.signalManager
							.getScaleUnit() );
		// Getting count changes
		if ( selectedSignal != null ) {
			distanceText +=
					"("
						+ selectedSignal.getSumCountChanges( markers.get(
								index - 1 ).getPosition(), markers.get( index )
								.getPosition() ) + ")";
		}

		TextLayout markerDistanceLayout =
				new TextLayout( distanceText, markerDistanceFont, g
						.getFontRenderContext() );

		if ( markerDistanceLayout.getVisibleAdvance() <= maxWidth ) {
			markerDistanceLayout.draw( g,
					(int) (x1 + (maxWidth - markerDistanceLayout
							.getVisibleAdvance()) / 2), timeLineHeight - 18 );
		}
	}
}