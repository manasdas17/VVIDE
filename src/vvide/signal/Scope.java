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
 * Scope class Contains a signals
 */
public class Scope extends AbstractSignal {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -7537612624327132563L;
	/**
	 * Scope Type
	 */
	private String type;

	/*
	 * ======================== Getters / Setters ============================
	 */
	/**
	 * Return a signals in this scope
	 */
	public Vector<AbstractSignal> getSignals() {
		Vector<AbstractSignal> signals = new Vector<AbstractSignal>();
		for ( AbstractSignal sig : childrens ) {
			if ( !(sig instanceof Scope) ) {
				signals.add( sig );
			}
		}
		return signals;
	}

	/**
	 * Return a scopes in this scope
	 */
	public Vector<Scope> getScopes() {
		Vector<Scope> signals = new Vector<Scope>();
		for ( AbstractSignal sig : childrens ) {
			if ( (sig instanceof Scope) ) {
				signals.add( (Scope) sig );
			}
		}
		return signals;
	}

	/**
	 * Setter for type
	 * 
	 * @param type
	 *        the type to set
	 */
	public void setType( String type ) {
		this.type = type;
	}

	/**
	 * Getter for type
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	@Override
	public boolean isCompound() {
		return false;
	}

	@Override
	public String getValueAt( long time ) {
		return null;
	}

	@Override
	public boolean setValueAt( long time, String value ) {
		return false;
	}

	@Override
	public long getNextChangeTime( long time ) {
		return -1;
	}

	@Override
	public long getLastChangeTime( long time ) {
		return -1;
	}

	@Override
	public long getPreviousChangeTime( long time ) {
		return -1;
	}

	@Override
	public long getNearestPreviousChangeTime( long time ) {
		return -1;
	}

	@Override
	public int getCountChanges( long time1, long time2 ) {
		return -1;
	}

	@Override
	public int getSumCountChanges( long time1, long time2 ) {
		return -1;
	}

	@Override
	public int getNoChangeWidth() {
		return -1;
	}

	/*
	 * ============================= Methods =================================
	 */
	/**
	 * Constructor Create an empty Scope without the name
	 */
	public Scope() {
		this( null );
	}

	/**
	 * Constructor Create an empty scope with the name
	 * 
	 * @param name
	 *        name of the scope
	 */
	public Scope( String name ) {
		super( -1, name, -1, -1 );
		this.setName( name );
		childrens = new Vector<AbstractSignal>();
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
			for ( AbstractSignal signal : childrens ) {
				signal.accept( v );
			}
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
	}
}