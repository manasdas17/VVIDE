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
import java.util.Vector;

import vvide.Application;
import vvide.ui.views.WaveView;

/**
 * Render class for signals
 */
public class SignalRender {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Backend for rendering
	 */
	private SignalRenderBackend backend;
	/**
	 * The height of the timeline area
	 */
	private int timeLineHeight;
	/**
	 * Width of the area with signal names
	 */
	private int infoWidth;
	/**
	 * Height of the area with signal names
	 */
	private int signalHeight;
	/**
	 * The position of the logic "0" level
	 */
	private int lowOffset;
	/**
	 * The position of the logic "1" level
	 */
	private int highOffset;
	/**
	 * The middle between logic "0" and "1"
	 */
	private int middleOffset;
	/**
	 * Font for displaying time on the timeline
	 */
	private Font timeFont;
	/**
	 * Color for the font for displaying time on the timeline
	 */
	private Color timeColor;
	/**
	 * Color for the lines on the timeline
	 */
	private Color timeLineColor;
	/**
	 * Font for displaying value of the signal
	 */
	private Font signalValueFont;
	/**
	 * Font for displaying less visible time
	 */
	private Font startTimeFont;
	/**
	 * Color for the font for displaying less visible time
	 */
	private Color startTimeColor;
	/**
	 * A minimal width between two changes
	 */
	private int smallestChangeWidth = 5;
	/**
	 * A delta for the searching the are without changes
	 */
	private long searchTimeDelta;
	/**
	 * Font for displaying name of the signal
	 */
	private Font signalNameFont;
	/**
	 * Font for displaying Bit number of the signal
	 */
	private Font bitNrFont;	
	/**
	 * Zoom
	 */
	private long zoom;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for backend
	 * 
	 * @param backend
	 *        the backend to set
	 */
	public void setBackend( SignalRenderBackend backend ) {
		this.backend = backend;
	}

	/**
	 * Getter for backend
	 * 
	 * @return the backend
	 */
	public SignalRenderBackend getBackend() {
		return backend;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Load the setting from the settings manager
	 */
	public void reloadSettings( WaveView view ) {
		// Load Settings
		timeLineHeight = Application.settingsManager.getTimeLineHeight();
		infoWidth = Application.settingsManager.getInfoWidth();
		signalHeight = Application.settingsManager.getSignalHeight();
		lowOffset = Application.settingsManager.getLowOffset();
		highOffset = Application.settingsManager.getHighOffset();
		middleOffset = (lowOffset + highOffset) / 2;
		signalValueFont = Application.settingsManager.getSignalValueFont();
		timeFont = Application.settingsManager.getTimeFont();
		timeColor = Application.settingsManager.getTimeColor();
		timeLineColor = Application.settingsManager.getTimeLineColor();
		startTimeFont = Application.settingsManager.getStartTimeFont();
		startTimeColor = Application.settingsManager.getStartTimeColor();
		signalNameFont = Application.settingsManager.getSignalNameFont();
		bitNrFont = Application.settingsManager.getBitNrFont();
		zoom = view.getZoom();
		searchTimeDelta = (smallestChangeWidth - 1) * zoom;
	}

	/**
	 * Draw the signals
	 * 
	 * @param visibleSignals
	 *        vector with signals to render
	 * @param startTime
	 *        start time for render
	 * @param stopTime
	 *        stop time for render
	 */
	public void drawSignals( Vector<VisibleSignal> visibleSignals,
		long startTime, long stopTime ) {

		if ( backend == null ) return;

		// Draw the background
		backend.drawBackground();

		// Draw a timeline
		drawTimeline( startTime, stopTime, visibleSignals.size() * signalHeight );

		// Drawing the signals
		int offsetY = timeLineHeight;
		Marker selectedMarker =
			(Marker) Application.markerManager.getSelectedMarker();
		long markerPosition =
			(selectedMarker == null) ? -1 : selectedMarker.getPosition();
		AbstractSignal selectedSignal =
			Application.signalManager.getSelectedSignal();
		for ( int i = 0; i < visibleSignals.size(); i++ ) {

			VisibleSignal visibleSignal = visibleSignals.get( i );

			// Notify the backend, that a new signal will be drawed
			backend.beginDrawSignal();

			// Draw a rectangle if the signal is selected
			if ( visibleSignal.getSignal() == selectedSignal ) {
				backend.drawSelectedSignalBorder( offsetY );
			}

			// Draw a info about the signal
			backend.drawSignalInfo( visibleSignal, offsetY, markerPosition, infoWidth, signalNameFont, bitNrFont );

			// Draw signal
			if ( visibleSignals.get( i ).getSignal().getBitWidth() == 1 ) {
				drawOneBitSignal( visibleSignal, offsetY, startTime, stopTime );
			}
			else {
				drawManyBitSignal( visibleSignal, offsetY, startTime, stopTime );
			}

			// Notify the backend, that a signal is completely drawed
			backend.endDrawSignal();

			offsetY += signalHeight;
		}

	}

	/**
	 * Draw a timeline
	 * 
	 * @param startTime
	 * @param stopTime
	 * @param height
	 *        height of the vertical lines
	 */
	private void drawTimeline( long startTime, long stopTime, int height ) {
		// Draw interval
		long mult = 10 * zoom;
		// Getting a first drawing time
		long time = startTime / mult * mult;
		if ( startTime % mult > 0 ) {
			time += mult;
		}
		// Height of the stroke
		int h = 0;
		// Odd / Even time value
		int n = 0;
		// Vertical alignment for the time Value
		TextAlign vAlign;

		while ( time <= stopTime ) {

			long x = getXFromTime( startTime, time, zoom );

			if ( time % (100 * zoom) == 0 ) {
				h = 8;
			}
			else
				if ( time % (50 * zoom) == 0 ) {
					h = 5;
				}
				else {
					h = 3;
				}
			backend.drawLine( x, 0, x, h, timeColor );

			// Vertical grid
			if ( time % (50 * zoom) == 0 ) {
				backend.drawLine( x, timeLineHeight, x,
					timeLineHeight + height, timeLineColor );

			}

			// time
			if ( time % (100 * zoom) == 0 ) {
				// Draw a time value
				if ( n == 0 ) {
					vAlign = TextAlign.TOP;
				}
				else {
					vAlign = TextAlign.BOTTOM;
				}
				backend.drawText( TimeMetric.toString( time,
					Application.signalManager.getScaleUnit() ), x,
					timeLineHeight - 25, 90, vAlign, TextAlign.CENTER,
					timeFont, timeColor );
				n = (n + 1) % 2;
			}
			time += mult;
		}

		// Draw a start time value
		backend.drawText( TimeMetric.toString( startTime,
			Application.signalManager.getScaleUnit() ), infoWidth - 5,
			timeLineHeight - 3, infoWidth, TextAlign.CENTER, TextAlign.RIGHT,
			startTimeFont, startTimeColor );
	}

	/**
	 * Drawing a OneBitSignal
	 * 
	 * @param visibleSignal
	 *        a signal to draw
	 * @param offsetY
	 *        offset of the signal
	 * @param startTime
	 *        start time to draw
	 * @param stopTime
	 *        stop time to draw
	 */
	private void drawOneBitSignal( VisibleSignal visibleSignal, int offsetY,
		long startTime, long stopTime ) {

		AbstractSignal signal = visibleSignal.getSignal();

		long currentTime = startTime;
		long nextTime;
		String value = signal.getValueAt( startTime );
		String nextValue;
		long startX = infoWidth;
		long stopX;
		long startY;
		long stopY;

		while ( currentTime < stopTime ) {
			// Getting signal values and increment the current position
			nextTime = signal.getLastChangeTime( currentTime );
			if (nextTime > stopTime) nextTime = stopTime;
			nextValue = signal.getValueAt( nextTime );

			// Value line (horizontal)
			startY = offsetY + getOffsetForValue( value );
			stopX = startX + ((nextTime - currentTime) / zoom);
			long pxDelta = stopX - startX;
			if ( pxDelta > smallestChangeWidth ) {
				backend.drawLine( startX, startY, stopX, startY,
					getColorForValue( value ) );

				// Change line (vertical)
				if ( !value.equals( nextValue )
					|| !(value.charAt( 0 ) > '1' && nextValue.charAt( 0 ) > '1') ) {
					stopY = offsetY + getOffsetForValue( nextValue );
					backend.drawLine( stopX, startY, stopX, stopY,
						getColorForValue( value, nextValue ) );
				}
			} else {
				nextTime =
					drawManyChanges( startX, startTime, currentTime, stopTime,
						signal, offsetY );
				stopX = getXFromTime( startTime, nextTime, zoom );
				nextValue = signal.getValueAt( nextTime );
			}

			// Move to the next position
			value = nextValue;
			currentTime = nextTime;
			startX = stopX;
		}

	}

	/**
	 * Return an y offset of the line
	 * 
	 * @param value
	 *        a value to get offset
	 * @return offset
	 */
	private int getOffsetForValue( String value ) {
		if ( value.equals( "0" ) ) {
			return lowOffset;
		}
		else
			if ( value.equals( "1" ) ) {
				return highOffset;
			}
			else {
				return middleOffset;
			}
	}

	/**
	 * Draw a ManyBit signal
	 * 
	 * @param visibleSignal
	 *        signal to draw
	 * @param offsetY
	 *        startPosition of the signal
	 * @param markerPosition
	 *        position of the selected marker
	 * @param startTime
	 *        start time for drawing
	 * @param stopTime
	 *        stop time for drawing
	 */
	private void drawManyBitSignal( VisibleSignal visibleSignal, int offsetY,
		long startTime, long stopTime ) {

		AbstractSignal signal = visibleSignal.getSignal();

		long currentTime = startTime;
		long nextTime = signal.getNextChangeTime( startTime );
		long firstChangeTime = signal.getNearestPreviousChangeTime( startTime );

		long startX = infoWidth;
		long stopX =
			(nextTime == -1) ? getXFromTime( startTime, stopTime, zoom )
				: getXFromTime( startTime, nextTime, zoom );

		String value = visibleSignal.getFormattedValueAt( startTime );
		Color color = getColorForValue( value );
		long maxWidth = stopX - startX - 4;

		// Special cases
		// case 1: ==
		if ( firstChangeTime != startTime && nextTime == -1 ) {
			backend.drawLine( startX, offsetY + highOffset, stopX, offsetY
				+ highOffset, color );
			backend.drawLine( startX, offsetY + lowOffset, stopX, offsetY
				+ lowOffset, color );
			backend.drawText( value, (startX + stopX) / 2, offsetY
				+ middleOffset, maxWidth, TextAlign.CENTER, TextAlign.CENTER,
				signalValueFont, Color.WHITE );
			return;
		}
		// case 2: =>
		if ( firstChangeTime != startTime ) {
			long[] x;
			long[] y;
			if ( stopX - startX > 3 ) {
				x = new long[] { startX, stopX - 3, stopX, stopX - 3, startX };
				y =
					new long[] { offsetY + highOffset, offsetY + highOffset,
						offsetY + middleOffset, offsetY + lowOffset,
						offsetY + lowOffset };
			}
			else {
				x = new long[] { startX, stopX, startX };
				y =
					new long[] { offsetY + highOffset, offsetY + middleOffset,
						offsetY + lowOffset };
			}
			backend.drawPolyLine( x, y, color );
			backend.drawText( value, (startX + stopX) / 2, offsetY
				+ middleOffset, maxWidth, TextAlign.CENTER, TextAlign.CENTER,
				signalValueFont, Color.WHITE );
			currentTime = nextTime;
		}

		// Drawing signal
		while ( currentTime < stopTime ) {
			nextTime = signal.getNextChangeTime( currentTime );
			value = visibleSignal.getFormattedValueAt( currentTime );
			color = getColorForValue( value );

			// Correct a time of the next change
			if ( nextTime == -1 || nextTime > stopTime ) {
				stopX = getXFromTime( startTime, stopTime, zoom );
				drawLastChange( startX, stopX, offsetY, maxWidth, value, color );
				break;
			}

			// Calc a width of change
			stopX = getXFromTime( startTime, nextTime, zoom );
			maxWidth = stopX - startX - 4;
			long pxDelta = stopX - startX;
			if ( pxDelta > smallestChangeWidth ) {
				backend.drawHexagon( startX, stopX, offsetY, lowOffset,
					middleOffset, highOffset, color );
				backend.drawText( value, (stopX + startX) / 2, offsetY
					+ middleOffset, maxWidth, TextAlign.CENTER,
					TextAlign.CENTER, signalValueFont, Color.white );
			}
			else {
				nextTime =
					drawManyChanges( startX, startTime, currentTime, stopTime,
						signal, offsetY );
				stopX = getXFromTime( startTime, nextTime, zoom );
			}

			// Move to the next position
			currentTime = nextTime;
			startX = stopX;
		}

	}

	/**
	 * Draw the last change of the manyBitSignal
	 * 
	 * @param startX
	 *        start X value
	 * @param stopX
	 *        stop X value
	 * @param offsetY
	 *        y offset of the signal
	 * @param maxWidth
	 *        maximal width of the text
	 * @param value
	 *        color of the signal
	 * @param color
	 *        color of the lines
	 */
	private void drawLastChange( long startX, long stopX, int offsetY,
		long maxWidth, String value, Color color ) {
		long[] x;
		long[] y;
		if ( stopX - startX > 3 ) {
			x = new long[] { stopX, startX + 3, startX, startX + 3, stopX };
			y =
				new long[] { offsetY + highOffset, offsetY + highOffset,
					offsetY + middleOffset, offsetY + lowOffset,
					offsetY + lowOffset };
		}
		else {
			x = new long[] { stopX, startX, stopX };
			y =
				new long[] { offsetY + highOffset, offsetY + middleOffset,
					offsetY + lowOffset };
		}
		backend.drawPolyLine( x, y, color );
		backend.drawText( value, (startX + stopX) / 2, offsetY + middleOffset,
			maxWidth, TextAlign.CENTER, TextAlign.CENTER,
			signalValueFont, Color.white );
	}

	/**
	 * Draw a rectangle that show that signal has to many changes here
	 * 
	 * @param startX
	 * @param startTime
	 * @param currentTime
	 * @param stopTime
	 * @param signal
	 * @param offsetY
	 * @return the new value for current time
	 */
	private long drawManyChanges( long startX, long startTime, long currentTime,
		long stopTime, AbstractSignal signal, int offsetY ) {
		// Draw a rectangle
		int countChanges = 10;
		long startSearchTime = currentTime;
		long stopSearchTime = currentTime;
		while ( (countChanges != 0) && (stopSearchTime <= stopTime) ) {
			stopSearchTime =
				signal.getNearestPreviousChangeTime( startSearchTime
					+ searchTimeDelta );
			if ( stopSearchTime == startSearchTime ) {
				stopSearchTime = signal.getNextChangeTime( stopSearchTime );
			}
			if ( stopSearchTime == -1 ) {
				stopSearchTime = stopTime;
				break;
			}
			countChanges =
				signal.getCountChanges( startSearchTime, stopSearchTime );
			startSearchTime = stopSearchTime;
		}

		// Draw a many changes
		backend
			.fillRectangle( startX, offsetY + highOffset, getXFromTime(
				startTime, stopSearchTime, zoom ), offsetY + lowOffset,
				Color.GREEN );

		return stopSearchTime;
	}

	/**
	 * Convert the time to the coordinates
	 * 
	 * @param startTime
	 *        time at X = infoWidth
	 * @param time
	 *        time for getting a position
	 * @param zoom
	 *        current zoom
	 * @return x coordinate
	 */
	private long getXFromTime( long startTime, long time, long zoom ) {
		return (infoWidth + (time - startTime) / zoom);
	}

	/**
	 * Return a color for line
	 * 
	 * @param value
	 *        a value of the signal to determine a color
	 * @return color
	 */
	private Color getColorForValue( String value ) {
		if ( value.equals( AbstractSignal.SIGNAL_VALUE_Z ) ) {
			return Color.BLUE;
		}
		else
			if ( value.equals( AbstractSignal.SIGNAL_VALUE_X ) ) {
				return Color.RED;
			}
			else {
				return Color.GREEN;
			}
	}

	/**
	 * Return a color for line
	 * 
	 * @param value
	 *        a value of the signal to determine a color
	 * @param nextValue
	 *        a next Value of the signal to determine a color
	 * @return String Color
	 */
	private Color getColorForValue( String value, String nextValue ) {
		if ( value.equals( AbstractSignal.SIGNAL_VALUE_Z )
			|| nextValue.equals( AbstractSignal.SIGNAL_VALUE_Z ) ) {
			return Color.BLUE;
		}
		else
			if ( value.equals( AbstractSignal.SIGNAL_VALUE_X )
				|| nextValue.equals( AbstractSignal.SIGNAL_VALUE_X ) ) {
				return Color.RED;
			}
			else {
				return Color.GREEN;
			}
	}
}
