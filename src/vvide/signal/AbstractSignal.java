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

package vvide.signal;

import java.util.Vector;

import vvide.signal.visitors.AbstractVisitor;

/**
 * Abstract Signal Class. Simple abstract signal. Used to implement any signals
 */
public abstract class AbstractSignal {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 8760076510275095147L;
	/**
	 * Undefined state
	 */
	public static String SIGNAL_VALUE_X = "x";
	/**
	 * Highohm state
	 */
	public static String SIGNAL_VALUE_Z = "z";
	/**
	 * A parent for the signal
	 */
	protected AbstractSignal parent = null;
	/**
	 * A parent for the signal
	 */
	protected Vector<AbstractSignal> childrens = null;
	/**
	 * A name of the item
	 */
	protected String name;
	/**
	 * A number of a bit in many bit signal
	 */
	protected int bitNr;
	/**
	 * An amount of bits in the signal
	 */
	protected int bitWidth;
	/**
	 * An Integer represent an ID of a Signal
	 */
	protected int id;

	/*
	 * ======================== Getters / Setters ============================
	 */
	/**
	 * Set a new value of the bit number
	 * 
	 * @param value
	 *        new bit number
	 */
	public void setBitNr( int value ) {
		this.bitNr = value;
	}

	/**
	 * Return a bit number of this signal
	 */
	public int getBitNr() {
		return this.bitNr;
	}

	/**
	 * Set a new bit width of the signal
	 */
	public void setBitWidth( int value ) {
		this.bitWidth = value;
	}

	/**
	 * return a width of the signal
	 */
	public int getBitWidth() {
		return this.bitWidth;
	}

	/**
	 * Return an ID of this signal
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Set a new ID for the signal
	 */
	public void setId( int value ) {
		this.id = value;
	}

	/**
	 * Set a new name of the item
	 */
	public void setName( String value ) {
		this.name = value;
	}

	/**
	 * Return a name of the item
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set a new Scope for the item
	 */
	public void setParent( AbstractSignal parent ) {
		this.parent = parent;
	}

	/**
	 * Return a scope for the item
	 */
	public AbstractSignal getParent() {
		return parent;
	}

	/**
	 * Return a children of the item
	 */
	public Vector<AbstractSignal> getChildrens() {
		return childrens;
	}

	/**
	 * Return a full path to the signal
	 */
	public String getFullPath() {
		return (parent != null) ? parent.getFullPath() + "/" + name : name;
	}

	/**
	 * Return flag is compound signal
	 */
	public abstract boolean isCompound();

	/**
	 * Return a value of the Signal at the specified time
	 * 
	 * @param time
	 *        - time to get the Sinal's value
	 * @return A char with the value of the Signal
	 */
	public abstract String getValueAt( long time );

	/**
	 * Set the Signal's value at the specified time
	 * 
	 * @param value
	 *        - a value of the signal to set
	 * @param time
	 *        - time to set the value
	 * @return true, if the setting was correct, false otherwise
	 */
	public abstract boolean setValueAt( long time, String value );

	/**
	 * Return time, when the signal next time changed
	 * 
	 * @param time
	 *        - time of the last change
	 * @return a time of the next change
	 */
	public abstract long getNextChangeTime( long time );

	/**
	 * Return time, when the signal next time changed or the signal length
	 * 
	 * @param time
	 *        - time of the last change
	 * @return a time of the last change
	 */
	public abstract long getLastChangeTime( long time );

	/**
	 * Return time, when the signal previous time changed
	 * 
	 * @param time
	 *        - time of the previous change
	 * @return a time of the previous change
	 */
	public abstract long getPreviousChangeTime( long time );

	/**
	 * Return time, when the signal previous time changed
	 * 
	 * @param time
	 *        - time of the previous change
	 * @return a time of the previous change
	 */
	public abstract long getNearestPreviousChangeTime( long time );

	/**
	 * Return an max amount of changes of the signal between two times
	 * 
	 * @param time1
	 *        - start time
	 * @param time2
	 *        - stop time
	 * @return an amount of changes
	 */
	public abstract int getCountChanges( long time1, long time2 );

	/**
	 * Return an amount of changes of the signal between two times
	 * 
	 * @param time1
	 *        - start time
	 * @param time2
	 *        - stop time
	 * @return an amount of changes
	 */
	public abstract int getSumCountChanges( long time1, long time2 );

	/**
	 * Return a minimum interval in px without changes
	 */
	public abstract int getNoChangeWidth();

	/*
	 * ============================= Methods =================================
	 */
	/**
	 * Constructor
	 * 
	 * @param id
	 *        - an Inetger as ID of Signal
	 * @param name
	 *        - name of the Signal
	 * @param description
	 *        - description of the signal
	 * @param bitNr
	 *        - A number of a bit in many bit signal
	 * @param bitWidth
	 *        - An amount of bits in the signal
	 */
	public AbstractSignal( int id, String name, int bitNr, int bitWidth ) {
		this.id = id;
		this.name = name;
		this.bitNr = bitNr;
		this.bitWidth = bitWidth;
	}

	/**
	 * Add a new child
	 */
	public void addChild( AbstractSignal child ) {
		if ( child != null ) {
			this.childrens.add( child );
			child.setParent( this );
		}
	}
	
	/**
	 * Find a signal by name
	 * @param name name of the signal
	 */
	public AbstractSignal getChildByName( String name ) {
		for ( AbstractSignal signal : childrens ) {
			if ( signal.getName().equals( name ) ) return signal;
		}
		return null;
	}
	
	/**
	 * Accept the visitor
	 * 
	 * @param v
	 *        visitor to accept
	 */
	public abstract void accept( AbstractVisitor v );
}