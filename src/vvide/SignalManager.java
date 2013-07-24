/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This file based on the code of WaveForm Viewer project.
 * 
 * Copyright (C) 2010-2011 Department of Digital Technology
 * of the University of Kassel, Germany
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

package vvide;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;

import vvide.signal.AbstractSignal;
import vvide.signal.NumeralSystem;
import vvide.signal.Scope;
import vvide.signal.SignalValueDump;
import vvide.signal.VisibleSignal;

/**
 * SignalManager Class Controls the operations with loaded signals
 */
public class SignalManager {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * Property name for adding a visible signal
	 */
	public static String VISIBLE_SIGNAL_ADDED = "VisibleSignalAdded";
	/**
	 * Property name for removing a visible signal
	 */
	public static String VISIBLE_SIGNAL_REMOVED = "VisibleSignalRemoved";
	/**
	 * Property name for moving a visible signal
	 */
	public static String VISIBLE_SIGNAL_MOVED = "VisibleSignalMoved";
	/**
	 * Property name for selected signal index
	 */
	public static String SELECTED_SIGNAL_INDEX = "SelectedSignalIndex";
	/**
	 * Property name for removing all signals
	 */
	public static String SIGNALS_CLEARED = "SignalsCleared";
	/**
	 * Changing the numeralSystem for signal
	 */
	public static String NUMERAL_SYSTEM_CHANGED = "NumeralSystemChanged";

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Property change support
	 */
	private PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	/**
	 * A Signal Scale. This is a minimal time between two changes of the Signal.
	 */
	private int scale;
	/**
	 * A metric for the scale. (s ms ys ns ps fs)
	 */
	private int scaleUnit;
	/**
	 * Length of the simulation
	 */
	private long signalLength;
	/**
	 * A main Scope
	 */
	private Scope mainScope = new Scope( "" );
	/**
	 * Visible signals
	 */
	private Vector<VisibleSignal> visibleSignals = new Vector<VisibleSignal>();
	/**
	 * A collection of SignalDumps
	 */
	private SignalValueDump[] signalDumps;
	/**
	 * Index of the selected signal
	 */
	private int selectedSignalIndex = -1;

	/*
	 * ====================== Getters and setters ============================
	 */
	/**
	 * Set a new value of a scale unit (us, ps, ns etc)
	 * 
	 * @param scaleUnit
	 *        a string with a new metric
	 */
	public void setScaleUnit( int scaleUnit ) {
		this.scaleUnit = scaleUnit;
	}

	/**
	 * Return a current scale unit
	 * 
	 * @return string with a current metric
	 */
	public int getScaleUnit() {
		return scaleUnit;
	}

	/**
	 * Set a new scale
	 * 
	 * @param scale
	 *        an integer with a surrent scale
	 */
	public void setScale( int scale ) {
		this.scale = scale;
	}

	/**
	 * Return a current scale
	 * 
	 * @return an integer with a current scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Set a new value for a simulation length. This is the maximal signal
	 * Length, that can be displayed
	 * 
	 * @param value
	 *        an Integer with a new value
	 */
	public void setSignalLength( long value ) {
		this.signalLength = value;
	}

	/**
	 * Return a current simulation length
	 * 
	 * @return an integer with a current simulation length
	 */
	public long getSignalLength() {
		return this.signalLength;
	}

	/**
	 * Return a main Scope object
	 * 
	 * @return a Scope object
	 */
	public Scope getMainScope() {
		return mainScope;
	}

	/**
	 * Return an index of the selected signal
	 * 
	 * @return index of the selected signal
	 */
	public int getSelectedSignalIndex() {
		return selectedSignalIndex;
	}

	/**
	 * Setter for an index of the selected signal
	 * 
	 * @param index
	 *        index of the selected signal
	 */
	public void setSelectedSignalIndex( int index ) {
		// Adjust the index value
		if ( index >= visibleSignals.size() )
			index = visibleSignals.size() - 1;
		if ( index < -1 ) index = -1;

		if ( selectedSignalIndex != index ) {
			int oldValue = selectedSignalIndex;
			selectedSignalIndex = index;
			pcs.firePropertyChange( SELECTED_SIGNAL_INDEX, oldValue, index );
		}
	}

	/**
	 * Return the count of visible signals
	 * 
	 * @return the count of visible signals
	 */
	public int getCountVisibleSignals() {
		return visibleSignals.size();
	}

	/**
	 * Return the selected Signal
	 * 
	 * @return The AbstractSignal that is selected
	 */
	public AbstractSignal getSelectedSignal() {
		return (selectedSignalIndex == -1 || selectedSignalIndex >= visibleSignals
			.size()) ? null : visibleSignals.get( selectedSignalIndex )
			.getSignal();
	}

	/**
	 * Return the specified value Dump
	 * 
	 * @return a ValueDump
	 */
	public SignalValueDump getDump( int id ) {
		return signalDumps[id];
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Clear the Signal's Collection
	 */
	public void removeAll() {
		this.mainScope = new Scope( "" );
		this.visibleSignals.clear();
		this.setSignalLength( 0 );
		this.setScale( 1 );
		this.setScaleUnit( 0 );
		this.signalDumps = null;
		pcs.firePropertyChange( SIGNALS_CLEARED, null, null );
	}

	/**
	 * Find a signal by the specified path
	 * 
	 * @param path
	 *        - string with the path to the signal. Separator "/"
	 * @return a Signal with specified name, if it found or null
	 */
	public AbstractSignal findSignal( String path ) {
		Scope scope = mainScope;
		String[] scopeNames = path.split( "/" );
		for ( int i = 1; i < scopeNames.length - 1; ++i ) {
			// find a scope
			Scope childScope = null;
			for ( Scope next : scope.getScopes() ) {
				if ( next.getName().equalsIgnoreCase( scopeNames[i] ) ) {
					childScope = next;
					break;
				}
			}
			if ( childScope == null ) { return null; }
			scope = childScope;
		}
		return (scope == null) ? null : scope
			.getChildByName( scopeNames[scopeNames.length - 1] );
	}

	/**
	 * Add a signal to the visible signals
	 * 
	 * @param signal
	 *        a signal to add
	 */
	public void addToVisible( AbstractSignal signal ) {
		Vector<AbstractSignal> signals = new Vector<AbstractSignal>( 1 );
		signals.add( signal );
		addToVisible( signals );
	}

	/**
	 * Add a signal to the visible signals
	 * 
	 * @param signal
	 *        a visible signal to add
	 */
	public void addAllToVisible( Vector<VisibleSignal> signals ) {
		Vector<AbstractSignal> abstractSignals = new Vector<AbstractSignal>();
		for (VisibleSignal signal : signals) {
			visibleSignals.add( signal );
			abstractSignals.add( signal.getSignal() );
		}
		if ( signals != null && signals.size() > 0 )
			pcs.firePropertyChange( VISIBLE_SIGNAL_ADDED, null, signals );
	}

	/**
	 * Add a signal to the visible signals
	 * 
	 * @param signals
	 *        a signals to add
	 */
	public void addToVisible( Vector<AbstractSignal> signals ) {
		for ( AbstractSignal signal : signals ) {
			visibleSignals.add( new VisibleSignal( signal ) );
		}
		if ( signals != null && signals.size() > 0 )
			pcs.firePropertyChange( VISIBLE_SIGNAL_ADDED, null, signals );
	}

	/**
	 * Remove a signal from visible
	 * 
	 * @param signal
	 *        a signal to remove
	 */
	public void removeFromVisible( VisibleSignal signal ) {
		Vector<VisibleSignal> signals = new Vector<VisibleSignal>( 1 );
		signals.add( signal );
		removeFromVisible( signals );
	}

	/**
	 * Remove a signal from visible
	 * 
	 * @param signal
	 *        a signal to remove
	 */
	public void removeFromVisible( int signalIndex ) {
		if ( signalIndex < 0 || signalIndex >= visibleSignals.size() ) return;
		Vector<VisibleSignal> signals = new Vector<VisibleSignal>( 1 );
		signals.add( visibleSignals.get( signalIndex ) );
		removeFromVisible( signals );
	}

	/**
	 * Remove a signal from visible
	 * 
	 * @param signals
	 *        a signals to remove
	 */
	public void removeFromVisible( Vector<VisibleSignal> signals ) {
		for ( VisibleSignal signal : signals ) {
			visibleSignals.remove( signal );
		}
		if ( signals != null && signals.size() > 0 )
			pcs.firePropertyChange( VISIBLE_SIGNAL_REMOVED, signals, null );
	}

	/**
	 * Return all visible Signals
	 */
	public Vector<VisibleSignal> getVisibleSignals() {
		return visibleSignals;
	}

	/**
	 * insert the signals to the list of the visible signals
	 * 
	 * @param index
	 *        position to insert
	 * @param signals
	 *        Vector with signals
	 */
	public void insertVisibleSignalsAt( int index,
		Vector<AbstractSignal> signals ) {
		// Check the position
		if ( index < 0 ) index = 0;
		if ( index >= visibleSignals.size() )
			index = visibleSignals.size() - 1;

		if ( signals != null ) {
			for ( AbstractSignal signal : signals ) {
				visibleSignals.insertElementAt( new VisibleSignal( signal ),
					index++ );
			}
			if ( signals.size() > 0 )
				pcs.firePropertyChange( VISIBLE_SIGNAL_ADDED, null, signals );
		}
	}

	/**
	 * Change a numeraSystem for a specified visible signal index
	 * 
	 * @param selectedIndex
	 *        index of the visible signal
	 * @param system
	 *        numeral System
	 */
	public void changeNummeralSystem( int selectedIndex, NumeralSystem system ) {
		if ( selectedIndex < 0 || selectedIndex >= visibleSignals.size() )
			return;
		NumeralSystem oldValue =
			visibleSignals.get( selectedIndex ).getNumeralSystem();
		if ( oldValue != system ) {
			visibleSignals.get( selectedIndex ).setNumeralSystem( system );
			pcs.firePropertyChange( NUMERAL_SYSTEM_CHANGED, null,
				visibleSignals.get( selectedIndex ) );
		}
	}

	/**
	 * Set the new position foe the visible signal
	 * 
	 * @param from
	 *        Signal index to move
	 * @param to
	 *        new position of the signal
	 * @param changeSelectexIndex
	 *        flag to change the selecteSignalIndex
	 */
	public void setSignalPosition( int from, int to, boolean changeSelectexIndex ) {
		// Check the input range
		if ( from < 0 || from >= visibleSignals.size() ) return;
		// Adjust the newPosition
		if ( to < 0 ) to = 0;
		if ( to >= visibleSignals.size() ) to = visibleSignals.size() - 1;
		if ( from == to ) return;
		VisibleSignal tmp = visibleSignals.get( from );
		visibleSignals.set( from, visibleSignals.get( to ) );
		visibleSignals.set( to, tmp );
		if ( changeSelectexIndex ) {
			setSelectedSignalIndex( to );
		}
		pcs.firePropertyChange( VISIBLE_SIGNAL_MOVED, selectedSignalIndex + 1,
			selectedSignalIndex );
	}

	/**
	 * Create a buffer for signal dumps
	 * 
	 * @param countDumps
	 *        amout of dumps in buffer
	 */
	public void createDumpBuffer( int countDumps ) {
		signalDumps = new SignalValueDump[countDumps + 1];
	}

	/**
	 * Set a signal dump
	 * 
	 * @param varID
	 *        is of the var
	 * @param dump
	 *        dump to set
	 */
	public void setSignalDump( int varID, SignalValueDump dump ) {
		signalDumps[varID] = dump;
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
