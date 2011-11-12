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

package vvide;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import vvide.signal.Marker;
import vvide.ui.views.WaveView;

/**
 * MarkerManager Class Controls the operations with markers
 */
public class MarkerManager {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * Property name for adding a marker
	 */
	public static String MARKER_ADDED = "MarkerAdded";
	/**
	 * Property name for removing a marker
	 */
	public static String MARKER_REMOVED = "MarkerRemoved";
	/**
	 * Property name for a selecting Marker
	 */
	public static String MARKER_SELECTED = "MarkerSelected";
	/**
	 * Property name for removing all markers
	 */
	public static String MARKERS_CLEARED = "MarkersCleared";
	/**
	 * Property name for changing in markers
	 */
	public static String MARKER_CHANGED = "MarkersChanged";

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Marker counter
	 */
	private int markerCounter = 1;
	/**
	 * Property change support
	 */
	private PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	/**
	 * A vector with a markers
	 */
	private Vector<Marker> markers = new Vector<Marker>();
	/**
	 * Selected Marker
	 */
	private Marker selectedMarker = null;

	/*
	 * ====================== Getters and setters ============================
	 */
	/**
	 * Get a Vector with markers
	 */
	public Vector<Marker> getMarkers() {
		return markers;
	}

	/**
	 * Getter for a selected Marker
	 * 
	 * @return the selected Marker
	 */
	public Marker getSelectedMarker() {
		return selectedMarker;
	}

	/**
	 * Setter for a selected Marker
	 * 
	 * @return the selected Marker
	 */
	public void setSelectedMarker( Marker marker ) {
		if ( selectedMarker != marker ) {
			Marker old = selectedMarker;
			selectedMarker = marker;
			pcs.firePropertyChange( MARKER_SELECTED, old, selectedMarker );
		}
	}

	/**
	 * Setter for a selected Marker
	 * 
	 * @return the selected Marker
	 */
	public void setSelectedMarker( int index ) {
		if ( markers.indexOf( selectedMarker ) != index ) {
			Marker old = selectedMarker;
			selectedMarker = markers.get( index );
			pcs.firePropertyChange( MARKER_SELECTED, old, selectedMarker );
		}
	}

	/**
	 * Return a marker under the mouse cursor
	 * 
	 * @param x
	 *        the X coordinate in the signal panel axis
	 */
	public Marker getMarkerUnderCursor( int x ) {

		if (!Application.viewManager.isViewOpened( ViewManager.WAVE_VIEW_ID )) return null;
		
		WaveView view = (WaveView) Application.viewManager.getView( ViewManager.WAVE_VIEW_ID );
		long position = view.getTimeFromCoord( x );
		// Select a marker a time position +-5 zooms
		long minPosition = correctPosition( position - Application.settingsManager.getMarkerSelectionSensitivityArea() * view.getZoom() );
		long maxPosition = correctPosition( position + Application.settingsManager.getMarkerSelectionSensitivityArea() * view.getZoom() );

		// Search for a marker
		for ( Marker marker : Application.markerManager.getMarkers() ) {
			if ( (marker.getPosition() >= minPosition)
				&& (marker.getPosition() <= maxPosition) ) { return marker; }
		}
		return null;
	}

	/**
	 * Setter for markerCounter
	 * 
	 * @param markerCounter
	 *        the markerCounter to set
	 */
	public void setMarkerCounter( int markerCounter ) {
		this.markerCounter = markerCounter;
	}

	/**
	 * Getter for markerCounter
	 * 
	 * @return the markerCounter
	 */
	public int getMarkerCounter() {
		return markerCounter;
	}

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Add a new Marker
	 * 
	 * @param marker
	 *        A new marker to be added
	 */
	public void addMarker( Marker marker ) {
		this.markers.add( marker );
		pcs.firePropertyChange( MARKER_ADDED, null, marker );
	}

	/**
	 * Add a new marker at the specified position
	 * 
	 * @param position
	 *        position of the marker
	 */
	public void addMarker( long position ) {
		addMarker( new Marker( "Marker " + markerCounter++, position ) );
	}

	/**
	 * Remove a marker from collection
	 * 
	 * @param marker
	 *        A marker to be removed
	 */
	public void removeMarker( Marker marker ) {
		int index = markers.indexOf( marker );
		if ( index == -1 ) return;
		markers.remove( marker );
		pcs.firePropertyChange( MARKER_REMOVED, marker, null );
		if (getSelectedMarker() == marker) setSelectedMarker( null );
	}

	/**
	 * Remove all markers from the collection
	 */
	public void removeAll() {
		this.markers.clear();
		pcs.firePropertyChange( MARKERS_CLEARED, null, null );
	}

	/**
	 * Check that position is more that 0 or less that signal length
	 */
	public long correctPosition( long position ) {
		if ( position < 0 ) {
			position = 0;
		}
		if ( position > Application.signalManager.getSignalLength() ) {
			position = Application.signalManager.getSignalLength();
		}
		return position;
	}

	/**
	 * Change the position of the marker
	 * 
	 * @param marker
	 *        marker to change
	 * @param position
	 *        position to set
	 */
	public void changeMarkerPosition( Marker marker, long position ) {
		if ( marker.getPosition() != correctPosition( position ) ) {
			marker.setPosition( correctPosition( position ) );
			pcs.firePropertyChange( MARKER_CHANGED, null, marker );
		}
	}

	/**
	 * Change the Marker settings
	 * 
	 * @param marker
	 *        marker to change
	 * @param name
	 *        new Name of the marker
	 * @param position
	 *        new position
	 */
	public void changeMarker( Marker marker, String name, long position ) {
		if ( marker != null ) {
			marker.setName( name );
			marker.setPosition( correctPosition( position ) );
			pcs.firePropertyChange( MARKER_CHANGED, null, marker );
		}
	}

	/**
	 * Add a listener to a specified property
	 * 
	 * @param property
	 *        a name of the property
	 * @param listener
	 *        listener to add
	 */
	public void addPropertyChangeListener( String property,
			PropertyChangeListener listener ) {
		this.pcs.addPropertyChangeListener( property, listener );
	}

	/**
	 * Add a listener to all events
	 * 
	 * @param listener
	 *        listener to add
	 */
	public void addPropertyChangeListener( PropertyChangeListener listener ) {
		this.pcs.addPropertyChangeListener( listener );
	}

	/**
	 * Remove a listener from all events
	 * 
	 * @param listener
	 *        listener to remove
	 */
	public void removePropertyChangeListener( PropertyChangeListener listener ) {
		this.pcs.removePropertyChangeListener( listener );
	}

	/**
	 * Remove a listener from a specified events
	 * 
	 * @param property
	 *        a name of the property
	 * @param listener
	 *        listener to remove
	 */
	public void removePropertyChangeListener( String property,
			PropertyChangeListener listener ) {
		this.pcs.removePropertyChangeListener( property, listener );
	}
}