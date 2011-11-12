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
package vvide.parser;

import vvide.Application;
import vvide.utils.CommonMethods;

/**
 * Abstract class for Structure parser
 */
public abstract class AbstractValueLexer {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * A parser
	 */
	protected AbstractParser parser;
	/**
	 * size of the parsed file
	 */
	protected long fileSize;
	/**
	 * count of added changes
	 */
	protected int countChangesAdded = 0;
	/**
	 * Current timestamp
	 */
	protected long timestamp;
	/**
	 * Value of the signal
	 */
	protected String value;
	/**
	 * A divide for time
	 */
	protected long timeDivide;
	/**
	 * A time ratio
	 */
	protected int timeRatio = Application.settingsManager.getTimeRatio();
	/**
	 * A flag to cancel the work
	 */
	protected boolean interrupted = false;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for parser
	 * 
	 * @param parser
	 *        a parser to set
	 */
	public void setParser( AbstractParser parser ) {
		this.parser = parser;
	}

	/**
	 * Setter for fileSize
	 * 
	 * @param fileSize
	 *        the fileSize to set
	 */
	public void setFileSize( long fileSize ) {
		this.fileSize = fileSize;
	}

	/**
	 * Setter for timeDivide
	 * 
	 * @param timeDivide
	 *        the timeDivide to set
	 */
	public void setTimeDivide( long timeDivide ) {
		this.timeDivide = timeDivide;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Add a new change of the signal
	 */
	protected void addVarChange( String varID ) {
		int ID = CommonMethods.getIDFormString( varID );
		Application.signalManager.getDump( ID ).addRecord(
				timestamp / timeDivide * timeRatio, value );
	}
	
	/**
	 * Cancel the parsing
	 */
	public synchronized void interrupt() {
		this.interrupted = true;
	}
}
