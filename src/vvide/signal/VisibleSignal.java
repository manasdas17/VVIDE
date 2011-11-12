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

import vvide.Application;

/**
 * A signal with Numeral system info
 */
public class VisibleSignal {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Numeral system
	 */
	private NumeralSystem numeralSystem = NumeralSystem.HEXADECIMAL;
	/**
	 * Signal
	 */
	private AbstractSignal signal = null;

	/*
	 * ======================== Getters / Setters ============================
	 */
	/**
	 * Return a numeral system for this signal
	 */
	public NumeralSystem getNumeralSystem() {
		return numeralSystem;
	}

	/**
	 * Set a new numeral system for this signal
	 */
	public void setNumeralSystem( NumeralSystem system ) {
		if ( system != null ) {
			this.numeralSystem = system;
		}
	}

	/**
	 * Return a signal
	 */
	public AbstractSignal getSignal() {
		return signal;
	}

	/*
	 * ============================= Methods =================================
	 */
	/**
	 * Constructor
	 */
	public VisibleSignal( AbstractSignal signal ) {
		this.signal = signal;
		this.numeralSystem =
				Application.settingsManager.getDefaultNumeralSystem();
	}

	/**
	 * Return a value of the Signal at the specified time
	 * 
	 * @param time
	 *        - time to get the Sinal's value
	 * @return A char with the value of the Signal
	 */
	public String getFormattedValueAt( long time ) {
		String strValue = signal.getValueAt( time );
		if ( strValue.length() == 1) return strValue;
		if (numeralSystem == NumeralSystem.BINARY) return strValue;
		
		long value = 0;
		
		for (int i = 0; i < strValue.length(); ++i) {
			value <<= 1;
			value += strValue.charAt( i ) - 48;
		}
		
		switch ( numeralSystem ) {
		case UNSIGNED_DECIMAL:
			return Long.toString( value );
		case SIGNED_DECIMAL:
			if ( signal.getBitWidth() == 1 ) {
				return Long.toString( value );
			}
			else {
				long max = (long) Math.pow( 2, signal.getBitWidth() - 1 );
				if ( value >= max ) {
					value = -max + (value ^ (1 << (signal.getBitWidth() - 1)));
				}
				return Long.toString( value );
			}
		case HEXADECIMAL:
			return Long.toHexString( value ).toUpperCase();
		}
		return "No Value";
	}
}
