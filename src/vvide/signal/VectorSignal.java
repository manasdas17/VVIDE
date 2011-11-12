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

import java.lang.reflect.Method;

import vvide.logger.Logger;
import vvide.signal.visitors.AbstractVisitor;

/**
 * Abstract class for Vector signals
 */
public class VectorSignal extends AbstractSignal {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Collection of the Signalvalue's changes
	 */
	private SignalValueDump signalValueDump;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for signalValueDump
	 */
	public SignalValueDump getSignalDump() {
		return this.signalValueDump;
	}

	/**
	 * Setter for a signalValueDump
	 */
	public void setSignalDump( SignalValueDump value ) {
		this.signalValueDump = value;
	}

	@Override
	public boolean isCompound() {
		return false;
	}

	@Override
	public boolean setValueAt( long time, String value ) {
		return signalValueDump.addRecord( time, value );
	}

	@Override
	public String getValueAt( long time ) {
		return signalValueDump.getValueAt( time );
	}

	@Override
	public long getNextChangeTime( long time ) {
		return signalValueDump.getNextChangeTime( time );
	}

	@Override
	public int getCountChanges( long time1, long time2 ) {
		return signalValueDump.getCountChanges( time1, time2 );
	}

	@Override
	public int getSumCountChanges( long time1, long time2 ) {
		return signalValueDump.getSumCountChanges( time1, time2 );
	}

	@Override
	public long getPreviousChangeTime( long time ) {
		return signalValueDump.getPreviousChangeTime( time );
	}

	@Override
	public long getLastChangeTime( long time ) {
		return signalValueDump.getLastChangeTime( time );
	}

	@Override
	public long getNearestPreviousChangeTime( long time ) {
		return signalValueDump.getNearestPreviousChangeTime( time );
	}
	
	@Override
	public int getNoChangeWidth() {
		return 5;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param id
	 *        id of the signal
	 * @param name
	 *        reference of the signal
	 * @param bitNr
	 *        number of the bit
	 * @param bitWidth
	 *        bitWidth of the signal
	 */
	public VectorSignal( int id, String name, int bitNr, int bitWidth ) {
		super( id, name, bitNr, bitWidth );
	}

	/**
	 * Accept the visitor
	 * 
	 * @param v
	 *        visitor to accept
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public void accept( AbstractVisitor v ) {
		// Getting a current class name
		try {
			Class visitorClass = v.getClass();
			Method visitorMethod =
					visitorClass.getMethod( "visit", new Class[] { this
							.getClass() } );
			visitorMethod.invoke( v, new Object[] { this } );
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
	}

	@Override
	public AbstractSignal getChildByName( String name ) {
		return null;
	}
}
