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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;

import vvide.Application;
import vvide.SignalManager;
import vvide.logger.Logger;
import vvide.signal.AbstractSignal;
import vvide.signal.Marker;
import vvide.signal.NumeralSystem;
import vvide.signal.TimeMetric;
import vvide.signal.VisibleSignal;
import vvide.ui.views.WaveView;

/**
 * SignalController Create a controller for displaying all visible signals
 * Functions:
 * <ol>
 * <li>show signals
 * <li>change the signal displaying order
 * </ol>
 */
public class SignalRender {

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
	 * Height of the area with signal names
	 */
	private int signalHeight;
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
	 * Font for displaying less visible time
	 */
	private Font startTimeFont;
	/**
	 * Color for the font for displaying less visible time
	 */
	private Color startTimeColor;
	/**
	 * Font for displaying name of the signal
	 */
	private Font signalNameFont;
	/**
	 * Font for displaying Bit number of the signal
	 */
	private Font bitNrFont;
	/**
	 * Font for displaying value of the signal
	 */
	private Font signalValueFont;
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
	 * Color for the lines at logic "0" and "1" levels
	 */
	private Color signalBoundColor;
	/**
	 * Image for selected binary displaying mode
	 */
	private BufferedImage binaryImageOn = null;
	/**
	 * Image for non-selected binary displaying mode
	 */
	private BufferedImage binaryImageOff = null;
	/**
	 * X offset of the binary displaying mode button
	 */
	private int binaryOffset;
	/**
	 * Image for selected unsigned decimal displaying mode
	 */
	private BufferedImage unsignedDecimalImageOn = null;
	/**
	 * Image for non-selected unsigned decimal displaying mode
	 */
	private BufferedImage unsignedDecimalImageOff = null;
	/**
	 * X offset of the unsigned decimal displaying mode button
	 */
	private int unsignedDecimalOffset;
	/**
	 * Image for selected signed decimal displaying mode
	 */
	private BufferedImage signedDecimalImageOn = null;
	/**
	 * Image for non-selected signed decimal displaying mode
	 */
	private BufferedImage signedDecimalImageOff = null;
	/**
	 * X offset of the signed decimal displaying mode button
	 */
	private int signedDecimalOffset;
	/**
	 * Image for selected hexadecimal displaying mode
	 */
	private BufferedImage hexadecimalImageOn = null;
	/**
	 * Image for non-selected hexadecimal displaying mode
	 */
	private BufferedImage hexadecimalImageOff = null;
	/**
	 * X offset of the hexadecimal displaying mode button
	 */
	private int hexadecimalOffset;
	/**
	 * Y offset of the displaying mode buttons
	 */
	private int yNumeralSystemOffset = 32;
	/**
	 * Image for binary displaying mode
	 */
	private BufferedImage imgB = null;
	/**
	 * Image for unsigned decimal displaying mode
	 */
	private BufferedImage imgD = null;
	/**
	 * Image for signed decimal displaying mode
	 */
	private BufferedImage imgDs = null;
	/**
	 * Image for hexadecimal displaying mode
	 */
	private BufferedImage imgH = null;
	/**
	 * Image for a delete signal button
	 */
	private BufferedImage deleteSignalImgae = null;
	/**
	 * Position of the delete signal button
	 */
	private Point deleteSignalPoint;
	/**
	 * Position of the text of signal value at selected marker
	 */
	private Point signalValueAtMarkerPoint;
	/**
	 * Width of the buttons with numeral systems
	 */
	private int numeralSystemImageSize;
	/**
	 * Color of the selected signal's border
	 */
	private Color selectedSignalBorderColor;
	/**
	 * Background of the selected signal
	 */
	private Color selectedSignalBackgroundColor;
	/**
	 * Width of the button delete
	 */
	private int deleteSignalImageSize;
	/* Help variables for draw a signal */
	boolean isValue0;
	boolean isValue1;
	boolean isValueZ;
	boolean isValueX;
	boolean isLastValue0;
	boolean isLastValue1;
	boolean isLastValueZ;
	boolean isLastValueX;

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Init the render
	 */
	public void init() {
		// Load images
		try {
			binaryImageOn =
					ImageIO.read( this.getClass().getResource(
							"/img/binary-on.png" ) );
			binaryImageOff =
					ImageIO.read( this.getClass().getResource(
							"/img/binary-off.png" ) );
			unsignedDecimalImageOn =
					ImageIO.read( this.getClass().getResource(
							"/img/unsigned-decimal-on.png" ) );
			unsignedDecimalImageOff =
					ImageIO.read( this.getClass().getResource(
							"/img/unsigned-decimal-off.png" ) );
			signedDecimalImageOn =
					ImageIO.read( this.getClass().getResource(
							"/img/signed-decimal-on.png" ) );
			signedDecimalImageOff =
					ImageIO.read( this.getClass().getResource(
							"/img/signed-decimal-off.png" ) );
			hexadecimalImageOn =
					ImageIO.read( this.getClass().getResource(
							"/img/hexadecimal-on.png" ) );
			hexadecimalImageOff =
					ImageIO.read( this.getClass().getResource(
							"/img/hexadecimal-off.png" ) );
			deleteSignalImgae =
					ImageIO.read( this.getClass().getResource(
							"/img/delete.png" ) );
			numeralSystemImageSize = binaryImageOn.getWidth();
			deleteSignalImageSize = deleteSignalImgae.getWidth();
		}
		catch ( IOException e ) {
			Logger.logError( this, e );
		}

		reloadSettings();
	}

	/**
	 * Load the setting from the settings manager
	 */
	public void reloadSettings() {
		// Load Settings
		timeLineHeight = Application.settingsManager.getTimeLineHeight();
		infoWidth = Application.settingsManager.getInfoWidth();
		binaryOffset = infoWidth - 53;
		unsignedDecimalOffset = infoWidth - 40;
		signedDecimalOffset = infoWidth - 27;
		hexadecimalOffset = infoWidth - 14;
		deleteSignalPoint = new Point( infoWidth - 12, 2 );
		signalHeight = Application.settingsManager.getSignalHeight();
		signalValueAtMarkerPoint =
				new Point( infoWidth - 2, signalHeight / 2 + 3 );
		timeFont = Application.settingsManager.getTimeFont();
		timeColor = Application.settingsManager.getTimeColor();
		timeLineColor = Application.settingsManager.getTimeLineColor();
		startTimeFont = Application.settingsManager.getStartTimeFont();
		startTimeColor = Application.settingsManager.getStartTimeColor();
		lowOffset = Application.settingsManager.getLowOffset();
		highOffset = Application.settingsManager.getHighOffset();
		middleOffset = (lowOffset + highOffset) / 2;
		signalBoundColor = Application.settingsManager.getSignalBoundColor();
		signalNameFont = Application.settingsManager.getSignalNameFont();
		bitNrFont = Application.settingsManager.getBitNrFont();
		signalValueFont = Application.settingsManager.getSignalValueFont();
		selectedSignalBackgroundColor = Application.settingsManager.getSelectedSignalBackgroundColor();
		selectedSignalBorderColor = Application.settingsManager.getSelectedSignalBorderColor();
	}

	/**
	 * Add a visible signals to the panel
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
	public void drawSignals( Graphics2D g, int imageWidth, int imageHeight,
			WaveView view ) {

		// Calc, if we need to show extra signals
		Boolean showExtraRecord =
				((imageHeight % signalHeight > 0) && (Application.signalManager
						.getVisibleSignals().size() > view
						.getMaxVisibleSignals()));
		int extraRecords = (showExtraRecord) ? 1 : 0;
		// Calc stop index of the signal to be shown
		int startIndex = view.getStartVisibleSignalIndex();
		int countVisibleSignals = view.getMaxVisibleSignals();
		Vector<VisibleSignal> visibleSignals =
				Application.signalManager.getVisibleSignals();
		int stopIndex =
				(startIndex + countVisibleSignals + extraRecords > visibleSignals
						.size()) ? visibleSignals.size() : startIndex
					+ countVisibleSignals + extraRecords;

		// Draw a background
		drawBackground( g, imageWidth, imageHeight, view );

		// Drawing the signals
		int signalStartY = timeLineHeight;
		Marker selectedMarker =
				(Marker) Application.markerManager.getSelectedMarker();
		long markerPosition =
				(selectedMarker == null) ? -1 : selectedMarker.getPosition();
		for ( int signalNr = 0; signalNr <= countVisibleSignals; signalNr++ ) {

			// Draw a rectangle if the signal is selected
			if ( Application.signalManager.getSelectedSignalIndex() == startIndex
				+ signalNr ) {
				g.setColor( selectedSignalBorderColor );
				g.drawRect( 0, signalStartY, imageWidth - 1, signalHeight );
				g.setColor( selectedSignalBackgroundColor );
				g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.75f ) );
				g.fillRect( 1, signalStartY + 1, imageWidth - 2,
						signalHeight - 1 );
				g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER ) );
			}

			// draw a signal info and signal graph
			if ( startIndex + signalNr < stopIndex )
				drawSignal( g, imageWidth, imageHeight, visibleSignals
						.get( startIndex + signalNr ), signalStartY,
						markerPosition, view );

			signalStartY += signalHeight;
		}
	}

	/**
	 * Draw a background, timelines etc
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
	private void drawBackground( Graphics2D g, int imageWidth, int imageHeight,
			WaveView view ) {
		if ( g == null ) return;

		// Clear background
		g.setColor( Color.black );
		g.fillRect( 0, 0, imageWidth, imageHeight );

		// Draw an InfoArea, TimeLine
		// Top gradient
		GradientPaint grad =
				new GradientPaint( 0, 0, new Color( 94, 94, 94 ), 0,
						timeLineHeight, Color.black );
		g.setPaint( grad );
		g.fillRect( 0, 0, imageWidth, timeLineHeight );
		g.setPaint( null );

		// Info Background
		g.setColor( new Color( 150, 150, 150 ) );
		g.fillRect( 0, timeLineHeight, infoWidth, imageHeight - timeLineHeight );
		g.setColor( new Color( 70, 70, 70 ) );
		g.drawLine( infoWidth, 0, infoWidth, imageHeight );

		// Timeline
		// Start at Starttime, lines every 10*zoom
		long mult = 10 * view.getZoom();
		long time = view.getStartVisibleTime() / mult * mult;
		if ( view.getStartVisibleTime() % mult > 0 ) {
			time += mult;
		}
		int h = 0;
		int n = 0;
		TextLayout timeLayout;

		while ( time <= view.getEndVisibleTime() ) {

			int x = (int)view.getCoordFromTime( time );

			if ( time % (100 * view.getZoom()) == 0 ) {
				h = 8;
			}
			else
				if ( time % (50 * view.getZoom()) == 0 ) {
					h = 5;
				}
				else {
					h = 3;
				}
			g.setColor( timeColor );
			g.drawLine( x, 0, x, h );

			// Vertical grid
			if ( time % (50 * view.getZoom()) == 0 ) {
				g.setColor( timeLineColor );
				g.drawLine( x, timeLineHeight, x, imageHeight );

			}

			// time
			if ( time % (100 * view.getZoom()) == 0 ) {
				// Draw a time value
				timeLayout =
						new TextLayout( TimeMetric.toString( time,
								Application.signalManager.getScaleUnit() ),
								timeFont, g.getFontRenderContext() );
				if ( (n == 0) || (timeLayout.getVisibleAdvance() < 90) ) {
					g.setColor( timeColor );
					timeLayout.draw( g, x - timeLayout.getVisibleAdvance() / 2,
							h + 8 );
				}
				n = (n + 1) % 2;
			}
			time += 10 * view.getZoom();
		}

		// Draw a start time value
		TextLayout startTimeLayout =
				new TextLayout( TimeMetric.toString(
						view.getStartVisibleTime(), Application.signalManager
								.getScaleUnit() ), startTimeFont, g
						.getFontRenderContext() );
		g.setColor( startTimeColor );
		startTimeLayout.draw( g, infoWidth - 5
			- startTimeLayout.getVisibleAdvance(), timeLineHeight - 3 );

		// Drawing the horizontal lines
		int signalStartY = timeLineHeight;
		for ( int signalNr = 0; signalNr <= view.getMaxVisibleSignals(); signalNr++ ) {

			g.setColor( new Color( 120, 120, 120 ) );
			g.drawLine( 0, signalStartY + signalHeight - 1, infoWidth - 1,
					signalStartY + signalHeight - 1 );
			g.setColor( signalBoundColor );
			g.drawLine( infoWidth + 1, signalStartY + lowOffset, imageWidth,
					signalStartY + lowOffset );
			g.drawLine( infoWidth + 1, signalStartY + highOffset, imageWidth,
					signalStartY + highOffset );

			signalStartY += signalHeight;
		}
	}

	/**
	 * Draw a signal
	 * 
	 * @param g
	 *        a graphics2D to draw
	 * @param imageWidth
	 *        width of the graphics
	 * @param imageHeight
	 *        height of the graphics
	 * @param signal
	 *        signal to draw
	 * @param signalStartY
	 *        startPosition of the signal
	 * @param view
	 *        a WaveFormView
	 */
	private void drawSignal( Graphics2D g, int imageWidth, int imageHeight,
			VisibleSignal visibleSignal, int signalStartY, long markerPosition,
			WaveView view ) {
		{
			// Draw a signal name
			AbstractSignal signal = visibleSignal.getSignal();
			TextLayout signalNameLayout =
					new TextLayout( signal.getFullPath(), signalNameFont, g
							.getFontRenderContext() );
			g.setColor( new Color( 220, 220, 220 ) );
			signalNameLayout.draw( g, 5, signalStartY + 15 );

			// draw a bitNr if is a manybit signal
			if ( signal.getBitNr() != -1 ) {
				TextLayout bitNrLayout =
						new TextLayout( "bit #: " + (signal.getBitNr() + 1),
								bitNrFont, g.getFontRenderContext() );
				g.setColor( new Color( 190, 190, 190 ) );
				bitNrLayout.draw( g, 5, signalStartY + 30 );
			}

			// Draw a signal value at selected marker
			if ( markerPosition != -1 ) {
				TextLayout signalValueAtMarkerLayout =
						new TextLayout( visibleSignal
								.getFormattedValueAt( markerPosition ),
								signalValueFont, g.getFontRenderContext() );
				g.setColor( Color.white );
				g.setFont( signalValueFont );
				signalValueAtMarkerLayout.draw( g, signalValueAtMarkerPoint.x
					- (int) signalValueAtMarkerLayout.getBounds().getWidth(),
						signalStartY + signalValueAtMarkerPoint.y );
			}

			// Draw images for numeral system
			int yNumeralSystemPosition = signalStartY + yNumeralSystemOffset;
			switch ( visibleSignal.getNumeralSystem() ) {
			case BINARY:
				imgB = binaryImageOn;
				imgD = unsignedDecimalImageOff;
				imgDs = signedDecimalImageOff;
				imgH = hexadecimalImageOff;
				break;
			case UNSIGNED_DECIMAL:
				imgB = binaryImageOff;
				imgD = unsignedDecimalImageOn;
				imgDs = signedDecimalImageOff;
				imgH = hexadecimalImageOff;
				break;
			case SIGNED_DECIMAL:
				imgB = binaryImageOff;
				imgD = unsignedDecimalImageOff;
				imgDs = signedDecimalImageOn;
				imgH = hexadecimalImageOff;
				break;
			case HEXADECIMAL:
				imgB = binaryImageOff;
				imgD = unsignedDecimalImageOff;
				imgDs = signedDecimalImageOff;
				imgH = hexadecimalImageOn;
				break;
			}
			g.drawImage( imgB, null, binaryOffset, yNumeralSystemPosition );
			g.drawImage( imgD, null, unsignedDecimalOffset,
					yNumeralSystemPosition );
			g.drawImage( imgDs, null, signedDecimalOffset,
					yNumeralSystemPosition );
			g.drawImage( imgH, null, hexadecimalOffset, yNumeralSystemPosition );

			// draw delete button
			g.drawImage( deleteSignalImgae, null, deleteSignalPoint.x,
					signalStartY + deleteSignalPoint.y );

			// draw a signal
			g.setClip( infoWidth + 1, signalStartY, imageWidth - infoWidth - 1,
					signalHeight );

			// get a last change time
			long currentTime =
					signal.getPreviousChangeTime( view.getStartVisibleTime() );
			if ( currentTime < view.getStartVisibleTime() - 10 * view.getZoom() ) {
				currentTime = view.getStartVisibleTime() - 10 * view.getZoom();
			}
			long nextChangeTime = 0;
			String lastValue = signal.getValueAt( currentTime );
			String value;
			boolean stop = false;
			long lastChangeTime =
					signal.getLastChangeTime( view.getEndVisibleTime() );

			while ( (currentTime <= lastChangeTime && (!stop)) ) {
				nextChangeTime = signal.getNextChangeTime( currentTime );

				// Correct a time of the next change
				stop = (nextChangeTime == -1) || (nextChangeTime > view.getEndVisibleTime()); 
				if (nextChangeTime == -1)  {
					nextChangeTime = view.getEndVisibleTime() + 10 * view.getZoom();
				}
				// Calc a width of change
				long pxDelta =
						view.getCoordFromTime( nextChangeTime )
							- view.getCoordFromTime( currentTime );
				if ( pxDelta >= signal.getNoChangeWidth() ) {

					value = signal.getValueAt( currentTime );

					// Draw a signal line
					// Correct Values of X
					long startX = view.getCoordFromTime( currentTime );
					startX = (startX < infoWidth) ? infoWidth - 10 : startX;
					long stopX = view.getCoordFromTime( nextChangeTime );
					if ( stopX > imageWidth + 10 ) {
						stopX = imageWidth + 10;
					}

					isLastValue0 = lastValue.equals( "0" );
					isLastValue1 = lastValue.equals( "1" );
					isLastValueZ =
							lastValue.equals( AbstractSignal.SIGNAL_VALUE_Z );
					isLastValueX =
							lastValue.equals( AbstractSignal.SIGNAL_VALUE_X );
					isValue0 = value.equals( "0" );
					isValue1 = value.equals( "1" );
					isValueZ = value.equals( AbstractSignal.SIGNAL_VALUE_Z );
					isValueX = value.equals( AbstractSignal.SIGNAL_VALUE_X );

					drawSignalLine( g, (int)startX, (int)stopX, lastValue, value,
							signalStartY, signal, visibleSignal
									.getFormattedValueAt( currentTime ) );

					// Changing current pixel
					currentTime = nextChangeTime;
					lastValue = value;
				}
				else {
					// Have a more changes on some px
					int countChanges = 10;
					long startTime = currentTime;
					long stopTime = currentTime;
					while ( (countChanges != 0)
						&& (stopTime <= view.getEndVisibleTime()) ) {
						stopTime =
								startTime
									+ (long) ((signal.getNoChangeWidth() - 1) * view
											.getZoom());
						stopTime =
								signal.getNearestPreviousChangeTime( stopTime );
						if ( stopTime == startTime ) {
							stopTime = signal.getNextChangeTime( stopTime );
						}
						if ( stopTime > Application.signalManager
								.getSignalLength() ) {
							break;
						}
						if ( stopTime == -1 ) {
							stopTime = view.getEndVisibleTime();
							break;
						}
						countChanges =
								signal.getCountChanges( startTime, stopTime );
						startTime = stopTime;
					}

					// Draw a many changes
					g.setColor( Color.GREEN );
					g.fillRect( (int)view.getCoordFromTime( currentTime ),
							signalStartY + highOffset, (int)(view
									.getCoordFromTime( stopTime )
								- view.getCoordFromTime( currentTime )),
							lowOffset - highOffset );
					// Changing current pixel
					currentTime = stopTime;
					lastValue = signal.getValueAt( stopTime );
				}
			}

			// Remove Clip
			g.setClip( 0, 0, imageWidth, imageHeight );
		}
	}

	/**
	 * A Help Method to start a DrawMethod for a specified signal type
	 * 
	 * @param g
	 *        a graphics2D to draw
	 * @param xSrart
	 *        start position to draw
	 * @param xEnd
	 *        end position to draw
	 * @param lastValue
	 *        last signal value to draw the signal change line
	 * @param value
	 *        start value of the signal
	 * @param deltaY
	 *        offset of this signal
	 * @param signal
	 *        a signal to draw
	 * @param formattedValue
	 *        a value of the signal
	 * @see SignalRender#drawManyBitSignalLine(int, int, long, long, int)
	 * @see SignalRender#drawOneBitSignalLine(int, int, long, long, int)
	 */
	private void drawSignalLine( Graphics2D g, int xSrart, int xEnd,
			String lastValue, String value, int deltaY, AbstractSignal signal,
			String formattedValue ) {

		if ( signal.getBitWidth() == 1 ) {
			drawOneBitSignalLine( g, xSrart, xEnd, lastValue, value, deltaY );
		}
		else {
			drawManyBitSignalLine( g, xSrart, xEnd, lastValue, value, deltaY,
					formattedValue );
		}
	}

	/**
	 * Draw a One Bit signal
	 * 
	 * @param g
	 *        a graphics2D to draw
	 * @param xSrart
	 *        start position
	 * @param xEnd
	 *        end position
	 * @param value
	 *        value of signal
	 */
	private void drawOneBitSignalLine( Graphics2D g, int xSrart, int xEnd,
			String lastValue, String value, int deltaY ) {

		// Colors for draw a vertical line
		if ( isLastValueX || isValueX ) {
			g.setColor( Color.RED );
		}
		else
			if ( isLastValueZ || isValueZ ) {
				g.setColor( Color.BLUE );
			}
			else {
				g.setColor( Color.GREEN );
			}

		// Help Variables
		int lowY = deltaY + lowOffset;
		int middleY = deltaY + middleOffset;
		int highY = deltaY + highOffset;

		// Draw change from 1 to 0 or from 0 to 1
		if ( (isLastValue0 && isValue1) || (isLastValue1 && isValue0) ) {
			g.drawLine( xSrart, lowY, xSrart, highY );
		}
		// Draw change from 1 to (Z/X) or from (X/Z) to 1
		else
			if ( (isLastValue1 && (isValueZ || isValueX))
				|| (isValue1 && (isLastValueZ || isLastValueX)) ) {
				g.drawLine( xSrart, highY, xSrart, middleY );
			}
			// Draw change from 0 to (Z/X) or from (X/Z) to 0
			else
				if ( (isLastValue0 && (isValueZ || isValueX))
					|| (isValue0 && (isLastValueZ || isLastValueX)) ) {
					g.drawLine( xSrart, lowY, xSrart, middleY );
				}

		if ( isValue1 ) {
			g.setColor( Color.GREEN );
			g.drawLine( xSrart, highY, xEnd, highY );
		}
		else
			if ( isValue0 ) {
				g.setColor( Color.GREEN );
				g.drawLine( xSrart, lowY, xEnd, lowY );
			}
			else
				if ( isValueX ) {
					g.setColor( Color.RED );
					g.drawLine( xSrart, middleY, xEnd, middleY );
				}
				else {
					g.setColor( Color.BLUE );
					g.drawLine( xSrart, middleY, xEnd, middleY );
				}
	}

	/**
	 * Draw a Many Bit signal
	 * 
	 * @param g
	 *        Graphic, where to draw
	 * @param xSrart
	 *        start position
	 * @param xEnd
	 *        end position
	 * @param value
	 *        value of signal
	 */
	private void drawManyBitSignalLine( Graphics2D g, int xSrart, int xEnd,
			String lastValue, String value, int deltaY, String formattedValue ) {

		// Colors for draw a vertical line
		if ( isLastValueX || isValueX ) {
			g.setColor( Color.RED );
		}
		else
			if ( isLastValueZ || isValueZ ) {
				g.setColor( Color.BLUE );
			}
			else {
				g.setColor( Color.GREEN );
			}

		// Help Variables
		int lowY = deltaY + lowOffset;
		int middleY = deltaY + middleOffset;
		int highY = deltaY + highOffset;

		if ( !(isValueX || isValueZ) ) {
			g.setColor( Color.GREEN );
			// /-\
			g.drawLine( xSrart, middleY, xSrart + 3, highY );
			g.drawLine( xSrart + 3, highY, xEnd - 3, highY );
			g.drawLine( xEnd - 3, highY, xEnd, middleY );
			// \_/
			g.drawLine( xSrart, middleY, xSrart + 3, lowY );
			g.drawLine( xSrart + 3, lowY, xEnd - 3, lowY );
			g.drawLine( xEnd - 3, lowY, xEnd, middleY );
			// Draw a text Value
			if ( formattedValue.length() > ((xEnd - xSrart) / (signalValueFont
					.getSize() * 0.75)) ) {
				formattedValue = "x";
			}
			TextLayout valueLayout =
					new TextLayout( formattedValue, signalValueFont, g
							.getFontRenderContext() );
			g.setColor( Color.WHITE );
			valueLayout.draw( g, xSrart
				+ (xEnd - xSrart - valueLayout.getVisibleAdvance()) / 2 + 1,
					middleY + 3 );
		}
		else
			if ( isValueX )
				g.drawLine( xSrart, middleY, xEnd, middleY );
			else g.drawLine( xSrart, middleY, xEnd, middleY );
	}

	/**
	 * Make an Action with a buttons
	 * 
	 * @param e
	 *        mouseEvent
	 */
	public void proceedMouseClick( MouseEvent e ) {

		SignalManager manager = Application.signalManager;
		int selectedIndex = manager.getSelectedSignalIndex();

		if ( e.getX() <= infoWidth && selectedIndex >= 0 ) {
			int y = e.getY() % signalHeight;
			int x = e.getX();
			int endY = yNumeralSystemOffset + numeralSystemImageSize;

			// Check "Binary"
			if ( x >= binaryOffset
				&& x <= binaryOffset + numeralSystemImageSize
				&& y >= yNumeralSystemOffset && y <= endY ) {
				manager.changeNummeralSystem( selectedIndex,
						NumeralSystem.BINARY );
			}
			// Check "Unsigned Decimal"
			if ( x >= unsignedDecimalOffset
				&& x <= unsignedDecimalOffset + numeralSystemImageSize
				&& y >= yNumeralSystemOffset && y <= endY ) {
				manager.changeNummeralSystem( selectedIndex,
						NumeralSystem.UNSIGNED_DECIMAL );
			}
			// Check "Signed Decimal"
			if ( x >= signedDecimalOffset
				&& x <= signedDecimalOffset + numeralSystemImageSize
				&& y >= yNumeralSystemOffset && y <= endY ) {
				manager.changeNummeralSystem( selectedIndex,
						NumeralSystem.SIGNED_DECIMAL );
			}
			// Check "Hexadecimal"
			if ( x >= hexadecimalOffset
				&& x <= hexadecimalOffset + numeralSystemImageSize
				&& y >= yNumeralSystemOffset && y <= endY ) {
				manager.changeNummeralSystem( selectedIndex,
						NumeralSystem.HEXADECIMAL );
			}
			// Check "Delete Signal"
			if ( x >= deleteSignalPoint.x
				&& x <= deleteSignalPoint.x + deleteSignalImageSize
				&& y >= deleteSignalPoint.y
				&& y <= deleteSignalPoint.y + deleteSignalImageSize ) {
				manager.removeFromVisible( selectedIndex );
			}
		}
	}
}