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
package vvide.project;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import vvide.annotations.Export;
import vvide.annotations.Import;
import vvide.logger.Logger;
import vvide.utils.IOMethods;

/**
 * An abstract file in the project
 */
public abstract class AbstractFile {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * Property for name of the file
	 */
	public static String FILE_NAME = "FileName";
	/**
	 * Property for file location
	 */
	public static String FILE_PATH = "FileLocation";

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Property change support
	 */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	/**
	 * Name of the File
	 */
	protected String fileName;
	/**
	 * Location of the file
	 */
	protected String fileLocation;
	/**
	 * Project
	 */
	protected Project project;

	// Tag's names
	protected static final String FILE_NAME_TAG = "FileName";
	protected static final String FILE_LOCATION_TAG = "FileLocation";

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for fileName
	 * 
	 * @param fileName
	 *        the fileName to set
	 */
	@Import( tagName = FILE_NAME_TAG )
	public void setFileName( String fileName ) {
		if ( this.fileName == null || !this.fileName.equals( fileName ) ) {
			String oldName = this.fileName;
			this.fileName = fileName;
			pcs.firePropertyChange( FILE_NAME, oldName, fileName );
		}
	}

	/**
	 * Getter for fileName
	 * 
	 * @return the fileName
	 */
	@Export( type = "String", tagName = FILE_NAME_TAG )
	public String getFileName() {
		return fileName;
	}

	/**
	 * Setter for fileLocation
	 * 
	 * @param fileLocation
	 *        the fileLocation to set
	 */
	@Import( tagName = FILE_LOCATION_TAG )
	public void setFileLocation( String fileLocation ) {
		// Adding a separator to the end of the path
		fileLocation = IOMethods.addSeparator( fileLocation );
		if ( this.fileLocation == null
			|| !this.fileLocation.equals( fileLocation ) ) {
			String oldLocation = this.fileLocation;
			this.fileLocation = fileLocation;
			pcs.firePropertyChange( FILE_PATH, oldLocation, fileLocation );
		}
	}

	/**
	 * Getter for filePath
	 * 
	 * @return the filePath
	 */
	@Export( type = "String", tagName = FILE_LOCATION_TAG )
	public String getFilePath() {
		return fileLocation;
	}

	/**
	 * Return a full name of the file (path+"/"+name)
	 */
	public String getFullPath() {
		String projectPath =
				(project != null) ? project.getProjectLocation() : "";
		return projectPath + fileLocation + fileName;
	}

	/**
	 * Indicate that the file is a source file
	 * 
	 * @return true, if the file is a source file
	 */
	public abstract boolean isSourceFile();

	/**
	 * Get a description of the type of the file
	 * 
	 * @return String with a description
	 */
	public abstract String getFileTypeDescription();

	/**
	 * Get a default file extension
	 * 
	 * @return String with the default file extension
	 */
	public abstract String getDefaultFileExtension();

	/**
	 * Return a content of file
	 * 
	 * @return String with a text
	 * @throws IOException
	 *         if the file not exists or not a file
	 */
	public String getContent() throws IOException {

		// Buffer for content
		StringBuffer content = new StringBuffer();

		File thisFile = new File( getFullPath() );
		if ( !thisFile.isFile() )
			throw new IOException( "The file " + getFullPath()
				+ " is not exists or not a file" );

		// Try to create an UTF8 Reader
		BufferedReader br;
		try {
			br =
					new BufferedReader( new InputStreamReader(
							new FileInputStream( thisFile ), "UTF-8" ) );
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
			br = new BufferedReader( new FileReader( thisFile ) );
		}

		// Load the content
		String line;
		while ( (line = br.readLine()) != null ) {
			content.append( line );
			content.append( "\n" );
		}
		br.close();

		return content.toString();
	}

	/**
	 * Setter for project
	 * 
	 * @param project
	 *        the project to set
	 */
	public void setProject( Project project ) {
		this.project = project;
	}

	/**
	 * Getter for project
	 * 
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Return the supporting of the multiline comment flag
	 * 
	 * @return true if the multiline comments are supported
	 */
	public abstract boolean isMultilineCommentsSupported();

	/**
	 * Return the start string for multiline comment
	 * 
	 * @return the start string for multiline comment
	 */
	public abstract String getMultilineCommentStartString();

	/**
	 * Return the end string for multiline comment
	 * 
	 * @return the end string for multiline comment
	 */
	public abstract String getMultilineCommentEndString();

	/**
	 * Return the string for singleline comment
	 * 
	 * @return the string for singleline comment
	 */
	public abstract String getCommentStartString();

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public AbstractFile() {}

	/**
	 * Constructor
	 * 
	 * @param name
	 *        name of the file
	 * @param path
	 *        file path
	 */
	public AbstractFile( String name, String path ) {
		setFileName( name );
		setFileLocation( path );
	}

	@Override
	public String toString() {
		return fileName;
	}

	/**
	 * Save content to the file
	 * 
	 * @param content
	 *        String with a file content
	 */
	public void saveFile( String content ) {
		File thisFile = new File( this.getFullPath() );
		if ( thisFile.exists() ) {
			try {
				IOMethods.backUpFile( thisFile );
				thisFile.delete();
			}
			catch ( Exception e ) {
				Logger.logError( this, e );
			}
		}

		try {
			BufferedWriter bw = new BufferedWriter( new FileWriter( thisFile ) );
			bw.write( content );
			bw.flush();
			bw.close();
		}
		catch ( IOException e ) {
			Logger.logError( this, e );
		}
	}

	/**
	 * Add a listener to a specified property
	 * 
	 * @param property
	 *        a name of the property
	 * @param listener
	 *        listener to add
	 */
	public void addPropertyChangeListener( String property,
			PropertyChangeListener listener ) {
		this.pcs.addPropertyChangeListener( property, listener );
	}

	/**
	 * Add a listener to all events
	 * 
	 * @param listener
	 *        listener to add
	 */
	public void addPropertyChangeListener( PropertyChangeListener listener ) {
		this.pcs.addPropertyChangeListener( listener );
	}

	/**
	 * Remove a listener from all events
	 * 
	 * @param listener
	 *        listener to remove
	 */
	public void removePropertyChangeListener( PropertyChangeListener listener ) {
		this.pcs.removePropertyChangeListener( listener );
	}

	/**
	 * Remove a listener from a specified events
	 * 
	 * @param property
	 *        a name of the property
	 * @param listener
	 *        listener to remove
	 */
	public void removePropertyChangeListener( String property,
			PropertyChangeListener listener ) {
		this.pcs.removePropertyChangeListener( property, listener );
	}

	/**
	 * Return string with contentType for editor (e.g. "text/plain")
	 * 
	 * @return String with content type
	 */
	public abstract String getContentType();
}
