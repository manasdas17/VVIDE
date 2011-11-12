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

/**
 * Marker Class Contains Information about the marker
 */
public class Marker implements Comparable<Marker> {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Name of the Marker
	 */
	private String name;
	/**
	 * Description of the Marker
	 */
	private String description;
	/**
	 * Position of the marker
	 */
	private long position;

	/*
	 * ====================== Getters and setters ============================
	 */
	/**
	 * Return a name of the marker
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set a new name for the marker
	 */
	public void setName( String name ) {
		this.name = name;
	}

	/**
	 * Return a description of the marker
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set a new description for the file
	 */
	public void setDescription( String description ) {
		this.description = description;
	}

	/**
	 * Return a position of the marker
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * Set a new position for the marker
	 */
	public void setPosition( long position ) {
		this.position = position;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Create a marker with a name "New Marker" at position 0
	 */
	public Marker() {
		this( "Marker", "", 0 );
	}

	/**
	 * Create a Marker with a specified name at the position 0
	 * 
	 * @param name
	 *        String with a name of the marker
	 */
	public Marker( String name ) {
		this( name, "", 0 );
	}

	/**
	 * Create a Marker with a specified position
	 * 
	 * @param name
	 *        String with a name of the marker
	 */
	public Marker( long position ) {
		this( "Marker", "", position );
	}

	/**
	 * Create a Marker with a specified name at the specified position
	 * 
	 * @param name
	 *        String with a name of the marker
	 * @param position
	 *        position of the Marker
	 */
	public Marker( String name, long position ) {
		this( name, "", position );
	}

	/**
	 * Create a Marker with a specified name an description at the specified
	 * position
	 * 
	 * @param name
	 *        String with a name of the marker
	 * @param description
	 *        String with the description of the Marker
	 * @param position
	 *        position of the Marker
	 */
	public Marker( String name, String description, long position ) {
		this.name = name;
		this.description = description;
		this.position = position;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo( Marker o ) {
		if (o == null) return 1;
		if (position == o.position) return this.getName().compareTo( o.getName() );
		return (position > o.position) ? 1 : -1;
	}
}
