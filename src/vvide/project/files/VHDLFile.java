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
package vvide.project.files;

import vvide.project.AbstractSourceFile;

/**
 * A VHDL File
 */
public class VHDLFile extends AbstractSourceFile {

	/*
	 * ======================= Getters / Setters =============================
	 */
	@Override
	public String getFileTypeDescription() {
		return "VHDL File";
	}

	@Override
	public String getDefaultFileExtension() {
		return "vhd";
	}

	@Override
	public String getContentType() {
		return "text/vhdl";
	}
	
	@Override
	public boolean isMultilineCommentsSupported() {
		return false;
	}

	@Override
	public String getMultilineCommentStartString() {
		return null;
	}

	@Override
	public String getMultilineCommentEndString() {
		return null;
	}

	@Override
	public String getCommentStartString() {
		return "-- ";
	}
	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public VHDLFile() {}

	/**
	 * Constructor
	 * 
	 * @param name
	 *        name of the file
	 * @param path
	 *        path of the file
	 */
	public VHDLFile( String name, String path ) {
		super( name, path );
	}
}
