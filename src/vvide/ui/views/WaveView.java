/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This file based on the code of WaveForm Viewer project.
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
package vvide.ui.views;

import java.awt.BorderLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import vvide.Application;
import vvide.ui.AbstractView;
import vvide.ui.views.wave.MarkerRender;
import vvide.ui.views.wave.ScrollAlignment;
import vvide.ui.views.wave.ScrollRegion;
import vvide.ui.views.wave.SignalImagePanel;
import vvide.ui.views.wave.SignalPanelDropTargetListener;
import vvide.ui.views.wave.SignalPanelMouseAdapter;
import vvide.ui.views.wave.SignalRender;
import vvide.ui.views.wave.TimeLineMouseAdapter;
import vvide.utils.CommonMethods;

/**
 * A view for rendered signals
 */
public class WaveView extends AbstractView {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * Name of the project property
	 */
	public static String ZOOM_CHANGED = "ZoomChanged";
	/**
	 * ID for a WaveView
	 */
	public static int WAVE_VIEW_ID = 2;

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -6641537184282393616L;
	/**
	 * Panel with signal graphics
	 */
	private SignalImagePanel signalPanel;
	/**
	 * Horizontal Scroller
	 */
	private JScrollBar scrollHorizontal;
	/**
	 * Vertikal Scroller
	 */
	private JScrollBar scrollVertical;
	/**
	 * A Signal Render
	 */
	private SignalRender signalRender;
	/**
	 * A Marker Render
	 */
	private MarkerRender markerRender;
	/**
	 * A Model for a horizontal ScrollBar
	 */
	private ScrollRegion horizontalRegion;
	/**
	 * Amount of signal that can be displayed
	 */
	private int countVisibleSignals;
	/**
	 * Amount of pixel that can be used to draw a signal
	 */
	private int signalWidth;
	/**
	 * Array with allowed zoom factors
	 */
	private long[] zoomFactors = { 1, 2, 5 };
	/**
	 * Current zoom factor index
	 */
	private int zoomIndex = 0;
	/**
	 * Factor for multiply of zoom factor zoom = zommFactor * multiplyFactor
	 */
	private long zoomMultiplyFactor = 10;
	/**
	 * Maximal allowed Multiply factor (minimal is 1)
	 */
	private long maxZoomMultiplyFactor = 100000000000000000L;
	/**
	 * Minimal zoom
	 */
	private long minimalZoom;
	/**
	 * Instance of this class for internal classes
	 */
	private WaveView instance;
	/**
	 * Current MouseAdapter
	 */
	private MouseAdapter currentMouseAdapter;
	/**
	 * SignalManager Listener
	 */
	private SignalManagerListener signalManagerListener =
			new SignalManagerListener();
	/**
	 * MarkerManager Listener
	 */
	private MarkerManagerListener markerManagerListener =
			new MarkerManagerListener();

	/*
	 * ============================== Settings ===============================
	 */
	/**
	 * Multiply factor for a time
	 */
	private int timeRatio;
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
	 * Panel with view content
	 */
	private JPanel content;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Return a time, which is a start time for a signal displaying
	 * 
	 * @return a time
	 */
	public long getStartVisibleTime() {
		return horizontalRegion.begin;
	}

	/**
	 * Return a time, which is a end time for a signal displaying
	 * 
	 * @return a time
	 */
	public long getEndVisibleTime() {
		return horizontalRegion.end;
	}

	/**
	 * Return an index of the first visible signal
	 * 
	 * @return an index of the signal
	 */
	public int getStartVisibleSignalIndex() {
		return scrollVertical.getValue();
	}

	/**
	 * Return a maximal amount of signals that can be displayed at screen
	 * 
	 * @return a maximal amount of signals
	 */
	public int getMaxVisibleSignals() {
		return countVisibleSignals;
	}

	/**
	 * Set a new position for a vertical scroller
	 */
	public void setStartVisibleSignalIndex( int index ) {
		scrollVertical.setValue( index );
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	/**
	 * Getter for signal panel
	 * 
	 * @return a panel with signal graphics
	 */
	public SignalImagePanel getSignalPanel() {
		return signalPanel;
	}

	/**
	 * Convert a position to a screen Coordinates
	 * 
	 * @param time
	 *        position of the marker
	 * @return X coordinate of the position
	 */
	public long getCoordFromTime( long time ) {
		return infoWidth + ((time - horizontalRegion.begin) / getZoom())
			+ 1;
	}

	/**
	 * Convert a screen Coordinates to position
	 * 
	 * @param X
	 *        coordinate of the position
	 * @return position of the marker
	 */
	public long getTimeFromCoord( int x ) {
		return ((x - infoWidth - 1) * getZoom() + horizontalRegion.begin);
	}

	/**
	 * Convert a screen Coordinates to position
	 * 
	 * @param X
	 *        coordinate of the position
	 * @param isLimited
	 *        limit the value to the timeratio
	 * @return position of the marker
	 */
	public long getTimeFromCoord( int x, boolean isLimited ) {
		return (isLimited) ? getTimeFromCoord( x ) / timeRatio * timeRatio
				: getTimeFromCoord( x );
	}

	/**
	 * Return a current zoom value. Formula: zoom = zoomMultiplyFactor *
	 * zoomFactor;
	 * 
	 * @return a long value with current zoom value
	 */
	public long getZoom() {
		return zoomMultiplyFactor * zoomFactors[zoomIndex];
	}

	/**
	 * Change minimal zoom
	 */
	public void setMinimalZoom( long zoom ) {

		// Normalize the zoom
		long factor = 1;
		long zoomCopy = zoom;
		while ( zoom >= 10 ) {
			zoom /= 10;
			factor *= 10;
		}
		int i = 0;
		for ( i = 0; i < zoomFactors.length - 1; i++ ) {
			if ( zoom <= zoomFactors[i] ) break;
		}
		if ( zoomFactors[i] * factor < zoomCopy ) {
			i++;
			if ( i >= zoomFactors.length ) {
				i = 0;
				factor *= 10;
			}
		}

		minimalZoom = zoomFactors[i] * factor;
		if ( getZoom() > minimalZoom ) {
			setZoom( minimalZoom );
		}
	}

	/**
	 * Return minimal zoom
	 */
	public long getMinimalZoom() {
		return minimalZoom;
	}

	/**
	 * Return maximal zoom
	 */
	public long getMaximalZoom() {
		return 1L;
	}

	/**
	 * Return the default Zoom
	 */
	public long getDefaultZoom() {
		return 10L;
	}

	/**
	 * Set a new current zoom
	 * 
	 * @param zoom
	 *        a new zoom
	 */
	public void setZoom( long zoom ) {

		long oldZoom = getZoom();

		// Normalize the zoom
		zoomMultiplyFactor = 1;
		while ( zoom >= 10 ) {
			zoom /= 10;
			zoomMultiplyFactor *= 10;
		}
		zoomIndex = 0;
		for ( zoomIndex = 0; zoomIndex < zoomFactors.length; zoomIndex++ ) {
			if ( zoom <= zoomFactors[zoomIndex] ) break;
		}
		if ( getZoom() < zoom ) {
			zoomOut();
		}

		if ( oldZoom != getZoom() ) {
			pcs.firePropertyChange( ZOOM_CHANGED, oldZoom, getZoom() );
			renderContent();
		}
	}

	/**
	 * Return a signal, that is displayed under the mouse cursor
	 * 
	 * @param y
	 *        Y-coordinate of mouse
	 * @return an AbstractSignal or null
	 */
	public int getSignalIndexUnderCursor( int y ) {
		int startIndex = getStartVisibleSignalIndex();
		int signalIndex = (y - timeLineHeight) / signalHeight;
		if ( startIndex + signalIndex < Application.signalManager
				.getCountVisibleSignals() ) { return startIndex + signalIndex; }
		return -1;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public WaveView( int id ) {
		super( "Wave", null, id );

		instance = this;

		// Make UI
		signalPanel = new SignalImagePanel();
		scrollVertical = new JScrollBar();

		scrollHorizontal = new JScrollBar();
		scrollHorizontal.setOrientation( JScrollBar.HORIZONTAL );

		content = new JPanel( new BorderLayout() );
		content.add( scrollHorizontal, BorderLayout.SOUTH );
		content.add( scrollVertical, BorderLayout.EAST );
		content.add( signalPanel, BorderLayout.CENTER );

		setComponent( content );

		// Make Renders
		signalRender = new SignalRender();
		markerRender = new MarkerRender();

		// Add DnD
		signalPanel.setDropTarget( new DropTarget( signalPanel,
				new SignalPanelDropTargetListener( this ) ) );

		// Reload settings
		reloadSettings();
	}

	/**
	 * Load Settings from the setting manager
	 */
	public void reloadSettings() {
		timeRatio = Application.settingsManager.getTimeRatio();
		timeLineHeight = Application.settingsManager.getTimeLineHeight();
		infoWidth = Application.settingsManager.getInfoWidth();
		signalHeight = Application.settingsManager.getSignalHeight();

		signalRender.reloadSettings();
		markerRender.reloadSettings();
	}

	@Override
	public void initView() {
		super.initView();

		content.add( CommonMethods.getToolbar( "wave_view" ),
				BorderLayout.NORTH );

		// Create a new Horizontal ScrollBar Modell
		horizontalRegion =
				new ScrollRegion( Application.signalManager.getSignalLength() );

		// Reset settings for a ScrollBars
		scrollHorizontal.setValue( 0 );
		scrollHorizontal.setVisibleAmount( 0 );
		scrollHorizontal.setMaximum( horizontalRegion.scrollMax );
		scrollHorizontal.setVisible( true );

		scrollVertical.setValue( 0 );
		scrollVertical.setMaximum( 0 );
		scrollVertical.setVisibleAmount( 0 );
		scrollVertical.setVisible( true );

		signalRender.init();
		markerRender.init();

		// Calculate a new value for a minimal zoom factor
		resetSettings();

		// Adding listeners
		// Panel resize
		signalPanel.addComponentListener( new ComponentAdapter() {

			@Override
			public void componentResized( ComponentEvent e ) {
				signalPanel.updateImages();
				changeMinimalZoom();
				renderContent();
			}

			@Override
			public void componentShown( ComponentEvent e ) {
				signalPanel.updateImages();
				changeMinimalZoom();
				renderContent();
			}
		} );

		// Mouse events on panel
		signalPanel.addMouseListener( new MouseAdapter() {

			@Override
			public void mousePressed( MouseEvent e ) {
				signalPanel.requestFocus();
				currentMouseAdapter.mousePressed( e );
			}

			@Override
			public void mouseClicked( MouseEvent e ) {
				currentMouseAdapter.mouseClicked( e );
			}

			@Override
			public void mouseReleased( MouseEvent e ) {
				currentMouseAdapter.mouseReleased( e );
			}
		} );

		signalPanel.addMouseMotionListener( new MouseMotionListener() {

			@Override
			public void mouseMoved( MouseEvent e ) {
				// Changing the current mouse adapter
				if ( e.getY() <= timeLineHeight ) {
					if ( (currentMouseAdapter == null)
						|| (currentMouseAdapter instanceof SignalPanelMouseAdapter) ) {
						currentMouseAdapter =
								new TimeLineMouseAdapter( instance,
										scrollHorizontal );
					}
				}
				else {
					if ( (currentMouseAdapter == null)
						|| (currentMouseAdapter instanceof TimeLineMouseAdapter) ) {
						currentMouseAdapter =
								new SignalPanelMouseAdapter( instance,
										signalRender, scrollVertical,
										scrollHorizontal );
					}
				}
				currentMouseAdapter.mouseMoved( e );
			}

			@Override
			public void mouseDragged( MouseEvent e ) {
				currentMouseAdapter.mouseDragged( e );
			}
		} );

		signalPanel.addMouseWheelListener( new MouseWheelListener() {

			@Override
			public void mouseWheelMoved( MouseWheelEvent e ) {
				currentMouseAdapter.mouseWheelMoved( e );
			}
		} );

		// Key events on panel
		signalPanel.addKeyListener( new KeyAdapter() {

			@Override
			public void keyPressed( KeyEvent e ) {

				// Selected Signal
				int selectedSignalIndex =
						Application.signalManager.getSelectedSignalIndex();

				if ( selectedSignalIndex == -1 ) return;

				switch ( e.getKeyCode() ) {
				// if "delete" - remove current signal, move selected to the
				// next
				// signal
				case KeyEvent.VK_DELETE:
					Application.signalManager
							.removeFromVisible( selectedSignalIndex );
					Application.signalManager
							.setSelectedSignalIndex( selectedSignalIndex );
					break;
				case KeyEvent.VK_UP:
					if ( e.isControlDown() ) {
						Application.signalManager.setSignalPosition(
								selectedSignalIndex, selectedSignalIndex - 1,
								true );
					}
					else {
						Application.signalManager
								.setSelectedSignalIndex( selectedSignalIndex - 1 );
					}
					break;
				case KeyEvent.VK_DOWN:
					if ( e.isControlDown() ) {
						Application.signalManager.setSignalPosition(
								selectedSignalIndex, selectedSignalIndex + 1,
								true );
					}
					else {
						Application.signalManager
								.setSelectedSignalIndex( selectedSignalIndex + 1 );
					}
					break;
				}
			}
		} );

		// Horizontal Scroll
		scrollHorizontal.addAdjustmentListener( new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged( AdjustmentEvent e ) {
				if ( scrollHorizontal.getValue() == horizontalRegion.value )
					return;

				if ( scrollHorizontal.getValue() != horizontalRegion.value ) {
					// single click
					int delta =
							scrollHorizontal.getValue()
								- horizontalRegion.value;
					if ( (Math.abs( delta ) == scrollHorizontal
							.getUnitIncrement())
						|| (Math.abs( delta ) == scrollHorizontal
								.getBlockIncrement()) ) {
						horizontalRegion.scroll( delta, getZoom() * 10 );
					}
					else {
						horizontalRegion.moveTo( scrollHorizontal.getValue() );
					}
					renderContent();
				}
			}
		} );
		scrollHorizontal.addMouseWheelListener( new MouseWheelListener() {

			@Override
			public void mouseWheelMoved( MouseWheelEvent e ) {
				scrollHorizontal.setValue( scrollHorizontal.getValue()
					+ e.getWheelRotation() );
			}
		} );

		// Vertical Scroll
		scrollVertical.addAdjustmentListener( new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged( AdjustmentEvent e ) {
				signalRender.drawSignals( signalPanel.getSignalGraphics(),
						signalPanel.getWidth(), signalPanel.getHeight(),
						instance );
				signalPanel.repaint();
			}
		} );
		scrollVertical.addMouseWheelListener( new MouseWheelListener() {

			@Override
			public void mouseWheelMoved( MouseWheelEvent e ) {
				scrollVertical.setValue( scrollVertical.getValue()
					+ e.getWheelRotation() );
			}
		} );

		Application.signalManager
				.addPropertyChangeListener( signalManagerListener );
		Application.markerManager
				.addPropertyChangeListener( markerManagerListener );
		renderContent();

	}

	/**
	 * Reload The settings
	 */
	public void resetSettings() {
		setZoom( getDefaultZoom() );
		calcSettings();
		changeMinimalZoom();
		signalPanel.updateImages();
		renderContent();
		pcs.firePropertyChange( ZOOM_CHANGED, 1, getZoom() );
	}

	/**
	 * Calculate the displaying settings
	 */
	public void calcSettings() {
		// Calc the amount of visible signals
		countVisibleSignals =
				(signalPanel.getHeight() - timeLineHeight) / signalHeight;

		// Calc a width of image
		signalWidth = signalPanel.getWidth() - infoWidth;

		// Update a ScrollBars
		horizontalRegion.max = Application.signalManager.getSignalLength();
		horizontalRegion.changeVisibleRegion( (long) (signalWidth * getZoom()) );
		scrollHorizontal.setValue( horizontalRegion.value );
		scrollHorizontal.setVisibleAmount( horizontalRegion.visibleRange );

		scrollVertical.setMaximum( Application.signalManager
				.getCountVisibleSignals() );
		scrollVertical.setVisibleAmount( countVisibleSignals );
	}

	/**
	 * Render signals and Markers and repaint the panel
	 */
	public void renderContent() {
		calcSettings();
		signalRender.drawSignals( signalPanel.getSignalGraphics(), signalPanel
				.getWidth(), signalPanel.getHeight(), instance );
		markerRender.drawMarkers( signalPanel.getOverlayGraphics(), signalPanel
				.getWidth(), signalPanel.getHeight(), instance );
		signalPanel.repaint();
	}

	/**
	 * Calculate a set a new minimal zoom
	 */
	private void changeMinimalZoom() {
		if ( Application.signalManager.getSignalLength() == 0 ) {
			setMinimalZoom( 1 );
		}
		else {
			double minZoom =
					(double) Application.signalManager.getSignalLength()
						/ (double) signalWidth;
			long zoom = (long) Math.floor( minZoom );
			if ( minZoom > zoom ) {
				zoom++;
			}
			setMinimalZoom( zoom );
		}
	}

	/**
	 * Increase a zoom
	 */
	public void zoomIn() {
		// zoom only if possible
		if ( (zoomIndex == 0) && (zoomMultiplyFactor == 1) ) return;

		long oldZoom = getZoom();

		zoomIndex--;
		if ( zoomIndex < 0 ) {
			zoomIndex = zoomFactors.length - 1;
			zoomMultiplyFactor /= 10;
		}

		pcs.firePropertyChange( ZOOM_CHANGED, oldZoom, getZoom() );
		renderContent();
	}

	/**
	 * Decrease a zoom
	 */
	public void zoomOut() {
		// zoom only if possible
		if ( (getZoom() == minimalZoom)
			|| ((zoomIndex == zoomFactors.length - 1) && (zoomMultiplyFactor == maxZoomMultiplyFactor)) )
			return;

		long oldZoom = getZoom();

		zoomIndex++;
		if ( zoomIndex >= zoomFactors.length ) {
			zoomIndex = 0;
			zoomMultiplyFactor *= 10;
		}

		pcs.firePropertyChange( ZOOM_CHANGED, oldZoom, getZoom() );
		renderContent();
	}

	/**
	 * Scroll the signal view to the specified position
	 * 
	 * @param position
	 *        a new position
	 */
	public void scrollToPosition( long position ) {
		horizontalRegion.moveTo( position, ScrollAlignment.CENTER );
		scrollHorizontal.setValue( horizontalRegion.value );
		renderContent();
	}

	/*
	 * ========================= Internal Classes =============================
	 */
	/**
	 * Listener for SignalManager
	 */
	private class SignalManagerListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			renderContent();
		}

	}

	/**
	 * Listener for MarkerManager
	 */
	private class MarkerManagerListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			renderContent();
		}

	}
}
