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
package vvide.utils.xmlitems.info;

import vvide.annotations.Import;


/**
 * A key -> Value class
 */
public class KeyValueInfo {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Key
	 */
	private Object key = null;
	/**
	 * Value
	 */
	private Object value = null;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for key
	 * 
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * Setter for key
	 * 
	 * @param key
	 *        the key to set
	 */
	@Import(tagName="Key")
	public void setKey( Object key ) {
		this.key = key;
	}

	/**
	 * Getter for value
	 * 
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Setter for value
	 * 
	 * @param value
	 *        the value to set
	 */
	@Import(tagName="Value")
	public void setvalue( Object value ) {
		this.value = value;
	}
}
