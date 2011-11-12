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
package vvide.exceptions;

/**
 * Exception, throwed if the XMLItemFactory find no XMLItem 
 */
public class XMLItemNotFoundException extends Exception {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -6023193192573496852L;

	/*
	 * ============================ Methods ==================================
	 *//**
	 * Constructor
	 */
	public XMLItemNotFoundException() {}

	/**
	 * Constructor
	 * 
	 * @param message
	 */
	public XMLItemNotFoundException( String message ) {
		super( message );
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 */
	public XMLItemNotFoundException( Throwable cause ) {
		super( cause );
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 * @param cause
	 */
	public XMLItemNotFoundException( String message, Throwable cause ) {
		super( message, cause );
	}
}
