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
package vvide;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import vvide.project.AbstractFile;
import vvide.project.Project;
import vvide.utils.XMLUtils;

/**
 * A manager of the projects.
 */
public class ProjectManager {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * Property name for current project
	 */
	public static String CURRENT_PROJECT = "CurrentProject";
	/**
	 * Property name for current file
	 */
	public static String CURRENT_FILE = "CurrentFile";

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Property change support
	 */
	private PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	/**
	 * Current opened project
	 */
	private Project currentProject = null;
	/**
	 * Current edited file
	 */
	private AbstractFile currentFile = null;
	/**
	 * Supported file types
	 */
	private HashMap<String, Class<? extends AbstractFile>> supportedFiles;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for currentProject
	 * 
	 * @param currentProject
	 *        the currentProject to set
	 */
	public void setCurrentProject( Project project ) {
		if ( currentProject != project ) {
			Project oldProject = currentProject;
			currentProject = project;
			pcs.firePropertyChange( CURRENT_PROJECT, oldProject, project );
		}
	}

	/**
	 * Getter for currentProject
	 * 
	 * @return the currentProject
	 */
	public Project getCurrentProject() {
		return currentProject;
	}

	/**
	 * Setter for currentFile
	 * 
	 * @param currentFile
	 *        the currentFile to set
	 */
	public void setCurrentFile( AbstractFile file ) {
		if ( currentFile != file ) {
			AbstractFile oldCurrentFile = currentFile;
			currentFile = file;
			pcs.firePropertyChange( CURRENT_FILE, oldCurrentFile, file );
		}
	}

	/**
	 * Getter for currentFile
	 * 
	 * @return the currentFile
	 */
	public AbstractFile getCurrentFile() {
		return currentFile;
	}

	/**
	 * Getter for supportedFiles
	 * 
	 * @return the supportedFiles
	 */
	public Set<String> getSupportedFiles() {
		return supportedFiles.keySet();
	}

	/**
	 * Getter for class of the supported file
	 * 
	 * @param fileType
	 *        text with a type of the file
	 * @return class of the specified file
	 */
	public Class<? extends AbstractFile> getClassForFileType( String fileType ) {
		return supportedFiles.get( fileType );
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	@SuppressWarnings( "unchecked" )
	public ProjectManager() {

		XMLUtils utils = new XMLUtils();

		currentProject = null;
		supportedFiles =
				(HashMap<String, Class<? extends AbstractFile>>) utils
						.loadFromXMLStream( getClass().getResourceAsStream(
								"/res/supported_files.xml" ) );
	}

	/**
	 * Close the current project
	 */
	public void closeProject() {
		if ( currentProject != null ) {
			// TODO Uncomment
			// currentProject.removeTemporaryFiles();
		}
		setCurrentProject( null );
	}

	/**
	 * Load project from file
	 * 
	 * @param projectFileName
	 *        A string with full path file name
	 * @throws FileNotFoundException
	 *         is the file is not exits or if it is a Directory
	 */
	public void loadProject( String projectFileName )
			throws FileNotFoundException {
		File projectFile = new File( projectFileName );
		loadProject( projectFile );
	}

	/**
	 * Load project from file
	 * 
	 * @param projectFile
	 *        A File with project
	 * @throws FileNotFoundException
	 *         is the file is not exits or if it is a Directory
	 */
	public void loadProject( File projectFile ) throws FileNotFoundException {
		if ( !projectFile.isFile() )
			throw new FileNotFoundException( projectFile.getAbsolutePath()
				+ " is not Found" );
		loadProject( new FileInputStream( projectFile ) );
	}

	/**
	 * Load project from file
	 * 
	 * @param projectFileStream
	 *        An input stream to file
	 */
	public void loadProject( InputStream projectFileStream ) {
		Project newProject =
				new Project( "Project " + System.currentTimeMillis() );
		setCurrentProject( newProject );
	}

	/**
	 * Try to guess the file type by it's name
	 * @param filePath 
	 *        path to the file
	 * @return
	 *        File type string or null 
	 */
	public String getFileType(String filePath) {
		for (String fileType : supportedFiles.keySet())
		{
			try {
				AbstractFile clazzInstance = getClassForFileType(fileType).newInstance();
				if (filePath.endsWith(clazzInstance.getDefaultFileExtension()))
				{
					return fileType;
				}
			} catch (Exception e) {}
		}
		return null;
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
}
