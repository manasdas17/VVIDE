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

import java.lang.reflect.Method;
import java.util.Vector;

import vvide.logger.Logger;
import vvide.signal.visitors.AbstractVisitor;

/**
 * ManyBit Signal Class. A Container for many One-Bit signals
 */
public class CompoundSignal extends AbstractSignal {

	/*
	 * =========================== Attributes ================================
	 */
	@Override
	public boolean isCompound() {
		return true;
	}

	@Override
	public String getValueAt( long time ) {

		char[] value = new char[childrens.size()];
		for ( AbstractSignal signal : childrens ) {
			VectorSignal vSignal = (VectorSignal)signal;
			
			String sigValue = vSignal.getValueAt( time );
			if ( sigValue.equals( AbstractSignal.SIGNAL_VALUE_X )
				|| sigValue.equals( AbstractSignal.SIGNAL_VALUE_Z ) )
				return sigValue;
			value[bitWidth - vSignal.getBitNr() - 1] = sigValue.charAt( 0 );
		}

		return String.valueOf( value );
	}

	@Override
	public boolean setValueAt( long time, String value ) {
		return false;
	}

	@Override
	public long getNextChangeTime( long time ) {
		long nextTime = -1;
		for ( AbstractSignal signal : childrens ) {
			long tmpTime = ((VectorSignal)signal).getNextChangeTime( time );
			if ( tmpTime != -1 ) {
				if ( nextTime != -1 ) {
					nextTime = Math.min( tmpTime, nextTime );
				}
				else {
					nextTime = tmpTime;
				}
			}
		}
		return nextTime;
	}

	@Override
	public int getCountChanges( long time1, long time2 ) {
		int count = ((VectorSignal)childrens.get( 0 )).getCountChanges( time1, time2 );
		for ( AbstractSignal signal : childrens ) {
			count = Math.max( ((VectorSignal)signal).getCountChanges( time1, time2 ), count );
		}
		return count;
	}

	@Override
	public int getSumCountChanges( long time1, long time2 ) {
		int count = 0;
		for ( AbstractSignal signal : childrens ) {
			count += ((VectorSignal)signal).getCountChanges( time1, time2 );
		}
		return count;
	}

	/**
	 * Return a OneBitsignal with specified BitNr.
	 * 
	 * @param bitNr
	 *        a bitNr of the signal
	 * @return OneBitSignal Object or null if no signal found
	 */
	public AbstractSignal getSignal( int bitNr ) {
		for ( AbstractSignal signal : childrens ) {
			if ( ((VectorSignal)signal).getBitNr() == bitNr ) { return signal; }
		}
		return null;
	}

	@Override
	public int getNoChangeWidth() {
		return 6;
	}

	@Override
	public long getPreviousChangeTime( long time ) {
		long lastTime = -1;
		for ( AbstractSignal signal : childrens ) {
			long tmpTime = signal.getPreviousChangeTime( time );
			if ( tmpTime != -1 ) {
				if ( lastTime != -1 ) {
					lastTime = Math.max( tmpTime, lastTime );
				}
				else {
					lastTime = tmpTime;
				}
			}
		}
		return lastTime;
	}

	@Override
	public long getLastChangeTime( long time ) {
		long nextTime = -1;
		for ( AbstractSignal signal : childrens ) {
			long tmpTime = signal.getLastChangeTime( time );
			if ( tmpTime != -1 ) {
				if ( nextTime != -1 ) {
					nextTime = Math.min( tmpTime, nextTime );
				}
				else {
					nextTime = tmpTime;
				}
			}
		}
		return nextTime;
	}

	@Override
	public long getNearestPreviousChangeTime( long time ) {
		long nextTime = -1;
		for ( AbstractSignal signal : childrens ) {
			long tmpTime = signal.getNearestPreviousChangeTime( time );
			if ( tmpTime != -1 ) {
				if ( nextTime != -1 ) {
					nextTime = Math.max( tmpTime, nextTime );
				}
				else {
					nextTime = tmpTime;
				}
			}
		}
		return nextTime;
	}
	/*
	 * ============================= Methods =================================
	 */
	/**
	 * Constructor
	 * 
	 * @see AbstractSignal#AbstractSignal(int, String, String, int, int,
	 *      SignalSetting)
	 */
	public CompoundSignal( int id, String name, int bitNr, int bitWidth ) {
		super( id, name, bitNr, bitWidth );
		childrens = new Vector<AbstractSignal>();
	}

	/**
	 * Add a new signal to the signals collection
	 * 
	 * @param signal
	 *        a signal to add
	 * @return true if the signal was added
	 */
	@Override
	public void addChild( AbstractSignal signal ) {
		if ( signal == null ) return;
		bitWidth = Math.max( bitWidth, (signal.bitNr + 1) );
		childrens.add( signal );
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
			
			// Navigate to children
			for (AbstractSignal signal : childrens) {
				signal.accept( v );
			}
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
	}
}
