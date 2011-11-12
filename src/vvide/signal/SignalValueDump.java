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

import java.nio.ByteBuffer;

import vvide.Application;

/**
 * A SignalValueDump Class. Contains information about changes of the signal
 */
public class SignalValueDump {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Controller for buffer
	 */
	ByteBuffer buffer;
	/**
	 * Count changes in the signal
	 */
	private final int countChanges;
	/**
	 * Bytes for signal value
	 */
	private final int recordLength;
	/**
	 * Bytes for signal value
	 */
	private final int valueLength;
	/**
	 * String buffer for value
	 */
	private StringBuffer sb = new StringBuffer( 64 );

	/*
	 * ============================= Methods =================================
	 */
	/**
	 * Constructor
	 * 
	 * @param countChanges
	 *        count of changes in the signal
	 */
	public SignalValueDump( int countChanges, int valueLength ) {
		this.countChanges = countChanges;
		this.valueLength = valueLength;
		this.recordLength = valueLength + 8;
		buffer = ByteBuffer.allocate( countChanges * recordLength );
	}

	/**
	 * Add a new record to the collection
	 * 
	 * @param time
	 *        - a timestamp
	 * @param value
	 *        - value of the signal at the specified time
	 */
	public boolean addRecord( long time, String c ) {
		// getting a time
		buffer.putLong( time );
		buffer.put( c.getBytes() );
		return true;
	}

	/**
	 * Return a signal value at the specified time
	 * 
	 * @param time
	 *        - time to get the value
	 * @return a SignalValue object with the value of the signal
	 */
	public String getValueAt( long time ) {
		if ( countChanges > 0 ) {
			Borders b = searchBorders( time );
			int pos = b.right * recordLength;
			long timeRight = buffer.getLong( pos );

			if ( timeRight <= time ) {
				pos += 8;
			}
			else {
				pos -= valueLength;
			}

			return getValueAtPosition( pos );
		}
		return "";
	}

	/**
	 * Return the signal value at the specified position
	 * 
	 * @param pos
	 *        position in buffer
	 * @return string with value
	 */
	private String getValueAtPosition( int pos ) {
		sb.setLength( 0 );
		for ( int i = 0; i < valueLength; ++i ) {
			sb.append( (char) buffer.get( pos++ ) );
		}
		String value = sb.toString();

		if ( value.indexOf( 'x' ) >= 0 )
			return AbstractSignal.SIGNAL_VALUE_X;
		else
			if ( value.indexOf( 'z' ) >= 0 ) return AbstractSignal.SIGNAL_VALUE_Z;
		return value;
	}

	/**
	 * Return time, when the signal next time changed
	 * 
	 * @param time
	 *        - time of the change
	 * @return a time of the next change or -1 if the time is the last time of
	 *         dumping
	 */
	public long getNextChangeTime( long time ) {
		if ( countChanges > 0 ) {
			Borders b = searchBorders( time );
			long timeLeft = buffer.getLong( b.left * recordLength );
			long timeRight = buffer.getLong( b.right * recordLength );
			if ( (time >= timeLeft) && (time < timeRight) ) { return timeRight; }
			if ( b.right < countChanges - 1 ) { return buffer
					.getLong( (b.right + 1) * recordLength ); }
		}
		return -1;
	}

	/**
	 * Return time, when the signal net time changed
	 * 
	 * @param time
	 *        - time of the change
	 * @return a time of the next change or SignalLength if the time is the last
	 *         time of dumping
	 */
	public long getLastChangeTime( long time ) {
		time = getNextChangeTime( time );
		return (time != -1) ? time : Application.signalManager
				.getSignalLength();
	}

	/**
	 * Return time, when the signal previous time changed
	 * 
	 * @param time
	 *        - time of the previous change
	 * @return a time of the previous change or -1 if signal has no changes. If
	 *         the signal has a change at <code>time</code> it will be ignored
	 */
	public long getPreviousChangeTime( long time ) {
		if ( countChanges > 0 ) {
			Borders b = searchBorders( time );
			long timeLeft = buffer.getLong( b.left * recordLength );
			long timeRight = buffer.getLong( b.right * recordLength );
			if ( (time > timeLeft) && (time <= timeRight) ) { return timeLeft; }
			if ( time > timeRight ) { return timeRight; }
			if ( time <= timeLeft ) {
				if ( b.left > 1 ) {
					return buffer.getLong( (b.left - 1) * recordLength );
				}
				else {
					return 0;
				}
			}
		}
		return -1;
	}

	/**
	 * Return time, when the signal previous time changed
	 * 
	 * @param time
	 *        - time of the previous change
	 * @return a time of the previous change or -1 if signal has no changes. If
	 *         the signal has a change at <code>time</code> it will be returned
	 */
	public long getNearestPreviousChangeTime( long time ) {
		if ( countChanges > 0 ) {
			Borders b = searchBorders( time );
			long timeLeft = buffer.getLong( b.left * recordLength );
			long timeRight = buffer.getLong( b.right * recordLength );
			if ( (time >= timeLeft) && (time < timeRight) ) {
				return timeLeft;
			}
			else {
				return time;
			}
		}
		return -1;
	}

	/**
	 * Return count changes between time1 and time2. Changes on time1 and time2
	 * are included
	 * 
	 * @param time1
	 *        first time
	 * @param time2
	 *        second time
	 * @return count if changes
	 */
	public int getCountChanges( long time1, long time2 ) {
		if ( countChanges > 0 ) {
			int index2 = 0;
			int index1 = 0;

			// exclude change on time == 0
			time1 = (time1 == 0) ? 1 : time1;

			// Get borders for time 2
			Borders b = searchBorders( time2 );
			long timeLeft = buffer.getLong( b.left * recordLength );
			long timeRight = buffer.getLong( b.right * recordLength );
			if ( time2 >= timeRight ) {
				index2 = b.right;
			}
			else
				if ( timeLeft == time2 ) {
					index2 = b.left + 1;
				}
				else {
					index2 = b.left;
				}

			// Get borders for time 1
			b = searchBorders( time1 );
			timeLeft = buffer.getLong( b.left * recordLength );
			timeRight = buffer.getLong( b.right * recordLength );
			if ( time1 >= timeRight ) {
				index1 = b.right;
			}
			else
				if ( timeLeft == time1 ) {
					index1 = b.left - 1;
				}
				else {
					index1 = b.left;
				}

			return index2 - index1;
		}
		return -1;
	}

	public int getSumCountChanges( long time1, long time2 ) {
		if ( countChanges > 0 ) {
			int countChanges = 0;
			int pos = 0;
			String value;
			String lastValue;

			// exclude change on time == 0
			time1 = (time1 == 0) ? 1 : time1;

			// Get borders for time1
			Borders b1 = searchBorders( time1 );
			Borders b2 = searchBorders( time2 );

			if ( b1.left == b2.left && b1.right == b2.right ) {
				if ( buffer.getLong( b1.right * recordLength ) < time1 )
					return 0;
				if ( buffer.getLong( b1.left * recordLength ) == time1 )
					countChanges++;
				if ( buffer.getLong( b1.right * recordLength ) <= time2 )
					countChanges++;
				return countChanges;
			}

			if ( buffer.getLong( b1.right * recordLength ) <= time1 ) {
				pos = b1.right * recordLength;
			}
			else
				if ( buffer.getLong( b1.left * recordLength ) == time1 ) {
					pos = b1.left * recordLength;
				}
				else {
					pos = b1.right * recordLength;
				}

			lastValue = getValueAtPosition( pos - valueLength );
			while ( pos < buffer.capacity() && buffer.getLong( pos ) <= time2 ) {
				value = getValueAtPosition( pos + 8 );
				if ( value.length() != lastValue.length() ) {
					countChanges += valueLength;
				}
				else {
					for ( int i = 0; i < valueLength; ++i ) {
						if ( lastValue.charAt( i ) != value.charAt( i ) )
							countChanges++;
					}
				}
				pos += recordLength;
				lastValue = value;
			}
			return countChanges;
		}
		return -1;
	}

	/**
	 * Binary search the element in the dump to get a Value of the signal at
	 * selected
	 * time
	 * 
	 * @param time
	 *        - time to be found
	 * @return Borders-Object with indexes
	 */
	Borders searchBorders( long time ) {

		Borders b = new Borders();

		b.left = 0;
		b.right = countChanges - 1;
		b.center = (b.left + b.right) / 2;

		while ( b.right - b.left > 1 ) {
			if ( buffer.getLong( b.center * recordLength ) > time ) {
				b.right = b.center;
			}
			else {
				b.left = b.center;
			}
			b.center = (b.left + b.right) / 2;
		}
		return b;
	}

	/*
	 * ======================== Internal Classes ==============================
	 */
	/**
	 * Help class to store "left right center" borders of searching of the item
	 * in the collection
	 */
	class Borders {

		public int left;
		public int right;
		public int center;
	}
}
