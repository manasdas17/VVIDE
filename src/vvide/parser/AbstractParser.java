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

package vvide.parser;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Abstract Parser Class. Simple abstract parser. Used to implement any parsers
 */
public abstract class AbstractParser {

	/*
	 * ======================== Propertie's Names ============================
	 */
	/**
	 * Property name for the "Finished" property
	 */
	public final static String FINISHED = "Finished";
	/**
	 * Property for the current Operation
	 */
	public final static String CURRENT_OPERATION = "CurrentOperation";
	/**
	 * Property name for the "Progress" property
	 */
	public final static String PROGRESS = "Progress";

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Store all property-change-listeners
	 */
	protected final PropertyChangeSupport pcs =
			new PropertyChangeSupport( this );
	/**
	 * Show if the parsing of the file is finished
	 */
	protected boolean finished = false;
	/**
	 * Store a current progress of parsing (bytes proceed / total bytes)
	 */
	protected int progress = 0;
	/**
	 * Name of the current operation
	 */
	private String currentOperation = "";

	/*
	 * ====================== Getters and setters ============================
	 */
	/**
	 * Set a new value for the property "Finished". Notify listeners about the
	 * change of the value
	 */
	/* package */void setFinished( boolean value ) {
		if ( this.finished != value ) {
			boolean oldValue = this.finished;
			this.finished = value;
			pcs.firePropertyChange( FINISHED, oldValue, value );
		}
	}

	/**
	 * Return a current value of the "Finished" Property
	 * 
	 * @return a boolean value of the "Finished" property
	 */
	public boolean isFinished() {
		return this.finished;
	}

	/**
	 * Set the new value for the "Progress" property. Notify listeners about the
	 * changes
	 * 
	 * @param value
	 *        a new value for the property "Progress"
	 */
	/* package */void setProgress( int value ) {
		if ( this.progress != value ) {
			int oldValue = this.progress;
			this.progress = value;
			pcs.firePropertyChange( PROGRESS, oldValue, value );
		}
	}

	/**
	 * Return a current value of the "Progress" Property
	 * 
	 * @return an integer value of the "Progress" property
	 */
	public int getProgress() {
		return this.progress;
	}

	/**
	 * Setter for currentOperation
	 * 
	 * @param currentOperation
	 *        the currentOperation to set
	 */
	public void setCurrentOperation( String currentOperation ) {
		if ( !this.currentOperation.equals( currentOperation ) ) {
			String oldValue = this.currentOperation;
			this.currentOperation = currentOperation;
			pcs.firePropertyChange( CURRENT_OPERATION, oldValue,
					currentOperation );
		}
	}

	/**
	 * Getter for currentOperation
	 * 
	 * @return the currentOperation
	 */
	public String getCurrentOperation() {
		return currentOperation;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Start the parser
	 * 
	 * @param file
	 *        file to parse
	 * @return true, if the parser start correctly, false otherwise
	 */
	public abstract boolean startParse( File file );

	/**
	 * Stop the parser
	 * 
	 * @return true, if the parser stopped correctly, false otherwise
	 */
	public abstract boolean stopParse();

	/**
	 * Add a listener for all properties
	 * 
	 * @param listener
	 *        a listener for all properties
	 */
	public void addPropertyChangeListener( PropertyChangeListener listener ) {
		pcs.addPropertyChangeListener( listener );
	}

	/**
	 * Remove a listener for all properties
	 * 
	 * @param listener
	 *        a listener to be removed
	 */
	public void removePropertyChangeListener( PropertyChangeListener listener ) {
		pcs.removePropertyChangeListener( listener );
	}

	/**
	 * Add a listener for the specified property
	 * 
	 * @param property
	 *        a string with a property's name
	 * @param listener
	 *        a listener to be added
	 */
	public void addPropertyChangeListener( String property,
			PropertyChangeListener listener ) {
		pcs.addPropertyChangeListener( property, listener );
	}

	/**
	 * Remove a listener for the specified property
	 * 
	 * @param property
	 *        a string with a property's name
	 * @param listener
	 *        a listener to be removed
	 */
	public void removePropertyChangeListener( String property,
			PropertyChangeListener listener ) {
		pcs.removePropertyChangeListener( property, listener );
	}
}
