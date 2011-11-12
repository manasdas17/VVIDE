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

import vvide.utils.CommonMethods;

/**
 * Abstract class for var info
 */
public class VarInfo {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Type of the var
	 */
	private String type = null;
	/**
	 * Bit Width
	 */
	private int bitWidth = -1;
	/**
	 * ID if the var
	 */
	private int id = -1;
	/**
	 * Reference name
	 */
	private String reference = null;
	/**
	 * Bit Nr in compound signal
	 */
	private int bitNr = -1;
	/**
	 * MSB in vector
	 */
	private int msb = -1;
	/**
	 * LSB in vector
	 */
	private int lsb = -1;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for type
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
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
	 * Setter for bitWidth
	 * 
	 * @param bitWidth
	 *        the bitWidth to set
	 */
	public void setBitWidth( int bitWidth ) {
		this.bitWidth = bitWidth;
	}

	/**
	 * Getter for bitWidth
	 * 
	 * @return the bitWidth
	 */
	public int getBitWidth() {
		return bitWidth;
	}

	/**
	 * Getter for id
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter for id
	 * 
	 * @param id
	 *        the id to set
	 */
	public void setId( int id ) {
		this.id = id;
	}

	/**
	 * Setter for id
	 * 
	 * @param id
	 *        the id to set
	 */
	public void setId( String id ) {
		this.id = CommonMethods.getIDFormString( id );
	}
	
	/**
	 * Getter for reference
	 * 
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * Setter for reference
	 * 
	 * @param reference
	 *        the reference to set
	 */
	public void setReference( String reference ) {
		this.reference = reference;
	}

	/**
	 * Getter for bitNr
	 * 
	 * @return the bitNr
	 */
	public int getBitNr() {
		return bitNr;
	}

	/**
	 * Setter for bitNr
	 * 
	 * @param bitNr
	 *        the bitNr to set
	 */
	public void setBitNr( int bitNr ) {
		this.bitNr = bitNr;
	}

	/**
	 * Getter for msb
	 * 
	 * @return the msb
	 */
	public int getMsb() {
		return msb;
	}

	/**
	 * Setter for msb
	 * 
	 * @param msb
	 *        the msb to set
	 */
	public void setMsb( int msb ) {
		this.msb = msb;
	}

	/**
	 * Getter for lsb
	 * 
	 * @return the lsb
	 */
	public int getLsb() {
		return lsb;
	}

	/**
	 * Setter for lsb
	 * 
	 * @param lsb
	 *        the lsb to set
	 */
	public void setLsb( int lsb ) {
		this.lsb = lsb;
	}
}
