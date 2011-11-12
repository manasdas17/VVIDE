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
package vvide.utils;

import java.awt.event.ActionEvent;

/**
 * An ActionEvent with parameters
 */
public class ParametricActionEvent extends ActionEvent {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -9093394881546672860L;
	/**
	 * Parameter
	 */
	private Object param;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for parameter
	 * 
	 * @param param
	 *        the parameter to set
	 */
	public void setParam( Object param ) {
		this.param = param;
	}

	/**
	 * Getter for parameter
	 * 
	 * @return the parameter
	 */
	public Object getParam() {
		return param;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param source
	 * @param id
	 * @param command
	 * @param param
	 */
	public ParametricActionEvent( Object source, int id, String command,
			Object param ) {
		super( source, id, command );
		this.param = param;
	}

	/**
	 * Constructor
	 * 
	 * @param source
	 * @param id
	 * @param command
	 * @param modifiers
	 * @param param
	 */
	public ParametricActionEvent( Object source, int id, String command,
			int modifiers, Object param ) {
		super( source, id, command, modifiers );
		this.param = param;
	}

	/**
	 * Constructor
	 * 
	 * @param source
	 * @param id
	 * @param command
	 * @param when
	 * @param modifiers
	 * @param param
	 */
	public ParametricActionEvent( Object source, int id, String command,
			long when, int modifiers, Object param ) {
		super( source, id, command, when, modifiers );
		this.param = param;
	}
}
