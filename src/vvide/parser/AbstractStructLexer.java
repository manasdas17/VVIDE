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

import java.util.HashMap;

import vvide.Application;
import vvide.ViewManager;
import vvide.signal.AbstractSignal;
import vvide.signal.CompoundSignal;
import vvide.signal.Scope;
import vvide.signal.SignalFactory;
import vvide.ui.views.ConsoleView;
import vvide.utils.CommonMethods;

/**
 * Abstract class for Structure parser
 */
public abstract class AbstractStructLexer {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * An array with amount of changes each signal
	 */
	protected int[] changesBuffer = null;
	/**
	 * An array with BitWidth of each signal
	 */
	protected HashMap<Integer, Integer> bitWidthMap =
			new HashMap<Integer, Integer>();
	/**
	 * A parser
	 */
	protected AbstractParser parser;
	/**
	 * size of the parsed file
	 */
	protected long fileSize;
	/**
	 * Current var Info
	 */
	protected VarInfo var = null;
	/**
	 * Minimal interval between changes
	 */
	protected long minInterval = Long.MAX_VALUE;
	/**
	 * Store a current signal time
	 */
	protected long lastTimestamp = Long.MIN_VALUE + 100;
	/**
	 * Store a timescale value
	 */
	protected int timeScaleValue;
	/**
	 * Srore a timescale unit
	 */
	protected String timeScaleUnit;
	/**
	 * String buffer for comments
	 */
	protected StringBuffer commentBuffer = new StringBuffer();
	/**
	 * Max value for signal id
	 */
	protected int maxVarID = 0;
	/**
	 * Signal Factory
	 */
	protected SignalFactory signalFactory = new SignalFactory();
	/**
	 * Current scope
	 */
	protected Scope currentScope = Application.signalManager.getMainScope();
	/**
	 * New ID for ManyBitSignal
	 */
	protected int compoundSignalID = 1000000;
	/**
	 * count of added changes
	 */
	protected int countChangesAdded = 0;
	/**
	 * Console
	 */
	protected ConsoleView console;
	/**
	 * A flag to cancel the work
	 */
	protected boolean interrupted = false;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for changesMap
	 * 
	 * @return the changesMap
	 */
	public int[] getChangesMap() {
		return changesBuffer;
	}

	/**
	 * Setter for timeScaleValue
	 * 
	 * @param timeScaleValue
	 *        the timeScaleValue to set
	 */
	protected void setTimeScaleValue( int timeScaleValue ) {
		this.timeScaleValue = timeScaleValue;
	}

	/**
	 * Getter for timeScaleValue
	 * 
	 * @return the timeScaleValue
	 */
	public int getTimeScaleValue() {
		return timeScaleValue;
	}

	/**
	 * Setter for timeScaleUnit
	 * 
	 * @param timeScaleUnit
	 *        the timeScaleUnit to set
	 */
	protected void setTimeScaleUnit( String timeScaleUnit ) {
		this.timeScaleUnit = timeScaleUnit;
	}

	/**
	 * Getter for timeScaleUnit
	 * 
	 * @return the timeScaleUnit
	 */
	public String getTimeScaleUnit() {
		return timeScaleUnit;
	}

	/**
	 * Getter for minInterval
	 * 
	 * @return the minInterval
	 */
	public long getMinInterval() {
		return minInterval;
	}

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
	 * Setter for currentScope
	 * 
	 * @param currentScope
	 *        the currentScope to set
	 */
	public void setCurrentScope( Scope currentScope ) {
		this.currentScope = currentScope;
	}

	/**
	 * Getter for currentScope
	 * 
	 * @return the currentScope
	 */
	public Scope getCurrentScope() {
		return currentScope;
	}

	/**
	 * Setter for lastTimestamp
	 * 
	 * @param lastTimestamp
	 *        the lastTimestamp to set
	 */
	public void setLastTimestamp( long lastTimestamp ) {
		this.lastTimestamp = lastTimestamp;
	}

	/**
	 * Getter for lastTimestamp
	 * 
	 * @return the lastTimestamp
	 */
	public long getLastTimestamp() {
		return lastTimestamp;
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

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public AbstractStructLexer() {
		console =
				(ConsoleView) Application.viewManager
						.getView( ViewManager.CONSOLE_VIEW_ID );
	}

	/**
	 * Create a new empty Scope Info and set it as current
	 */
	protected void createNewScope() {
		Scope scope = new Scope();
		scope.setParent( currentScope );
		currentScope.addChild( scope );
		currentScope = scope;
	}

	/**
	 * Remove info about vars
	 */
	protected void beginVar() {
		var = new VarInfo();
	}

	/**
	 * Remove info about vars
	 */
	protected void endVar() {
		if ( var != null ) {
			int id = var.getId();
			currentScope.addChild( signalFactory.createVectorSignal( id, var
					.getReference(), var.getBitWidth() ) );
			maxVarID = Math.max( maxVarID, id );
			bitWidthMap.put( id, var.getBitWidth() );
		}
	}

	/**
	 * Append a var info to compound var info
	 */
	protected void appendToCompoundVar() {
		CompoundSignal compound = findCompound( var.getReference() );
		if ( compound == null ) {
			compound =
					signalFactory.createCompoundSignal( compoundSignalID++, var
							.getReference(), var.getBitNr() );
			currentScope.addChild( compound );
		}
		int id = var.getId();
		compound.addChild( signalFactory.createScalarSignal( id, var
				.getReference(), var.getBitNr() ) );
		maxVarID = Math.max( maxVarID, id );
		// if needed increment the vector size
		bitWidthMap.put( id, 1 );
		var = null;
	}

	private CompoundSignal findCompound( String reference ) {
		for ( AbstractSignal signal : currentScope.getSignals() ) {
			if ( signal.isCompound() && signal.getName().equals( reference ) )
				return (CompoundSignal) signal;
		}
		return null;
	}

	/**
	 * Create a changesMap
	 */
	protected void createChangesMap() {
		changesBuffer = new int[maxVarID + 1];
	}

	/**
	 * Add a new change of the signal
	 */
	protected void addVarChange( String varID ) {
		int ID = CommonMethods.getIDFormString( varID );
		changesBuffer[ID]++;
	}

	/**
	 * Print a founded comment
	 */
	protected void printComment() {
		if ( console != null )
			console.appendNormalText( commentBuffer.toString() );
		commentBuffer.setLength( 0 );
	}

	/**
	 * Cancel the parsing
	 */
	public synchronized void interrupt() {
		this.interrupted = true;
	}
}
