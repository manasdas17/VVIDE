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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import vvide.Application;
import vvide.annotations.Export;
import vvide.annotations.Import;
import vvide.exceptions.FileExistsException;
import vvide.logger.Logger;
import vvide.simulator.AbstractSimulator;
import vvide.utils.IOMethods;

/**
 * A Project class
 */
public class Project {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * Name of the project property
	 */
	public static String PROJECT_NAME = "ProjectName";
	/**
	 * Property indicates that the file was added to the project
	 */
	public static String FILE_ADDED = "FileAdded";
	/**
	 * Property indicates that the file was removed from the project
	 */
	public static String FILE_REMOVED = "FileRemoved";
	/**
	 * Property for project modification status
	 */
	public static String PROJECT_MODIFIED = "ProjectModified";
	/**
	 * Property for project location
	 */
	public static String PROJECT_LOCATION = "ProjectLocation";
	/**
	 * Property for top level entity
	 */
	public static String TOP_LEVEL_ENTITY = "TopLevelEntity";
	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * File Extension for the project
	 */
	public static String PROJECT_FILE_EXTENSION = "project";
	/**
	 * Property change support
	 */
	private PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	/**
	 * Files in the project
	 */
	private Vector<AbstractFile> files;
	/**
	 * Name of the project
	 */
	private String projectName;
	/**
	 * Modify status of the project
	 */
	private boolean isModified = false;
	/**
	 * Location of the Project
	 */
	private String projectLocation;
	/**
	 * Simulator
	 */
	private AbstractSimulator currentSimulator;
	/**
	 * Last simulated file
	 */
	private List<AbstractSourceFile> lastSimulatedFiles =
		new ArrayList<AbstractSourceFile>();
	/**
	 * File with topLevel Entity
	 */
	private String topLevelEntityFileName = null;

	// Tag's names
	private static final String PROJECT_NAME_TAG = "ProjectName";
	private static final String PROJECT_FILES_TAG = "Files";
	private static final String TOP_LEVEL_ENTITY_FILE_NAME_TAG =
		"TopLevelEntity";
	private static final String PROJECT_SIMULATOR = "Simulator";

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for projectName
	 * 
	 * @param projectName
	 *        the projectName to set
	 */
	@Import( tagName = PROJECT_NAME_TAG )
	public void setProjectName( String projectName ) {
		if ( this.projectName == null || !this.projectName.equals( projectName ) ) {
			String oldName = this.projectName;
			this.projectName = projectName;
			setIsModified( true );
			pcs.firePropertyChange( PROJECT_NAME, oldName, projectName );
		}
	}

	/**
	 * Getter for projectName
	 * 
	 * @return the projectName
	 */
	@Export( type = "String", tagName = PROJECT_NAME_TAG )
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Setter for files
	 * 
	 * @param files
	 *        the files for the project
	 */
	@Import( tagName = PROJECT_FILES_TAG )
	public void setFiles( List<AbstractFile> files ) {
		try {
			addFile( files.toArray( new AbstractFile[] {} ) );
		}
		catch ( FileExistsException e ) {
			Logger.logError( this, e );
		}
	}

	/**
	 * Getter for files
	 * 
	 * @return the files
	 */
	@Export( type = "List", tagName = PROJECT_FILES_TAG )
	public List<AbstractFile> getFiles() {
		return Collections.unmodifiableList( files );
	}

	/**
	 * Setter for Modify status
	 * 
	 * @param newStatus
	 *        new status of the project
	 */
	public void setIsModified( boolean newStatus ) {
		if ( isModified != newStatus ) {
			boolean oldStatus = isModified;
			isModified = newStatus;
			pcs.firePropertyChange( PROJECT_MODIFIED, oldStatus, newStatus );
		}
	}

	/**
	 * Modify status of the project
	 * 
	 * @return true, if the project was modified, false otherwise
	 */
	public boolean isModified() {
		return isModified;
	}

	/**
	 * Setter for projectLocation
	 * 
	 * @param projectLocation
	 *        the projectLocation to set
	 */
	public void setProjectLocation( String projectLocation ) {
		// Adding a separator to the end of the path
		projectLocation = IOMethods.addSeparator( projectLocation );
		if ( this.projectLocation == null
			|| !this.projectLocation.equals( projectLocation ) ) {
			String oldLocation = this.projectLocation;
			this.projectLocation = projectLocation;
			setIsModified( true );
			pcs.firePropertyChange( PROJECT_LOCATION, oldLocation,
				projectLocation );
		}
	}

	/**
	 * Getter for projectLocation
	 * 
	 * @return the projectLocation
	 */
	public String getProjectLocation() {
		return projectLocation;
	}

	/**
	 * Return a folder for the temporary files
	 * 
	 * @return a String with a temporary folder
	 */
	public String getTemporaryFolder() {
		File folder = new File( getProjectLocation() + "tmp/" );
		if ( !folder.exists() ) {
			if ( !folder.mkdir() ) {
				Logger.logError( this.getClass().getName(),
					"Can't create a temporary folder" );
				return null;
			}
		}

		String path = null;
		try {
			path = IOMethods.addSeparator( folder.getCanonicalPath() );
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
			return null;
		}

		return path;
	}

	/**
	 * Return a file by it's name
	 * 
	 * @param fileName
	 *        name of the file
	 * @return AbstractFile or null if no file founded
	 */
	public AbstractFile getFile( String fileName ) {
		for ( AbstractFile file : files ) {
			if ( file.getFileName().equals( fileName ) ) return file;
		}
		return null;
	}

	/**
	 * Setter for currentSimulator
	 * 
	 * @param simulator
	 *        the simulator to set
	 */
	@Import( tagName = PROJECT_SIMULATOR )
	public void setSimulator( AbstractSimulator simulator ) {
		if (currentSimulator == null || currentSimulator.getClass() != simulator.getClass()) {
			this.currentSimulator = simulator;
			setIsModified( true );
		}
	}

	/**
	 * Getter for vhdlSimulator
	 * 
	 * @return the vhdlSimulator
	 */
	@Export( type = "ObjectFromClass", tagName = PROJECT_SIMULATOR )
	public AbstractSimulator getSimulator() {
		if ( currentSimulator == null ) {
			setSimulator( Application.settingsManager
				.getDefaultSimulator() );
		}
		return currentSimulator;
	}

	/**
	 * Add a new file to the list of simulated files
	 * 
	 * @param lastSimulatedFile
	 *        the file to add
	 */
	public void addLastSimulatedFile( AbstractSourceFile lastSimulatedFile ) {
		lastSimulatedFiles.add( lastSimulatedFile );
		if ( lastSimulatedFiles.size() > 2 ) {
			lastSimulatedFiles.remove( 0 );
		}
	}

	/**
	 * Getter for lastSimulatedFiles
	 * 
	 * @return the lastSimulatedFiles
	 */
	public List<AbstractSourceFile> getLastSimulatedFile() {
		return lastSimulatedFiles;
	}

	/**
	 * Setter for topLevelEntityFileName
	 * 
	 * @param fileName
	 *        the topLevelEntityFile to set
	 */
	@Import( tagName = TOP_LEVEL_ENTITY_FILE_NAME_TAG )
	public void setTopLevelEntityFileName( String fileName ) {
		if ( this.topLevelEntityFileName == null
			|| !topLevelEntityFileName.equals( fileName ) ) {
			String oldValue = topLevelEntityFileName;
			this.topLevelEntityFileName = fileName;
			pcs.firePropertyChange( TOP_LEVEL_ENTITY, oldValue, fileName );
			setIsModified( true );
		}
	}

	/**
	 * Getter for topLevelEntityFileName
	 * 
	 * @return the topLevelEntityFileName
	 */
	@Export( type = "String", tagName = TOP_LEVEL_ENTITY_FILE_NAME_TAG )
	public String getTopLevelEntityFileName() {
		return topLevelEntityFileName;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor. Create an empty project with the name "New Project"
	 */
	public Project() {
		this( "New Project" );
	}

	/**
	 * Constructor. Create an empty project
	 * 
	 * @param name
	 *        name of the project
	 */
	public Project( String name ) {
		projectName = name;
		pcs = new PropertyChangeSupport( this );
		files = new Vector<AbstractFile>();
	}

	/**
	 * Add file to the Project
	 * 
	 * @param file
	 *        file to add
	 * @throws FileExistsException
	 *         if the file is already exists in the project
	 */
	public void addFile( AbstractFile file ) throws FileExistsException {
		addFile( new AbstractFile[] { file } );
	}

	/**
	 * Add multiply file to the Project
	 * 
	 * @param filesToAdd
	 *        Array with files to add
	 * @throws FileExistsException
	 *         if the file is already exists in the project
	 */
	public void addFile( AbstractFile[] filesToAdd ) throws FileExistsException {
		boolean isAdded = false;

		// Adding the files
		for ( AbstractFile file : filesToAdd ) {
			if ( files.contains( file ) )
				throw new FileExistsException(
					"File already exists in the project" );
			files.add( file );
			file.setProject( this );
			isAdded = true;
		}

		// if files were added - notify the listeners
		if ( isAdded ) {
			setIsModified( true );
			pcs.firePropertyChange( FILE_ADDED, null, filesToAdd );
		}
	}

	/**
	 * Remove file to the Project
	 * 
	 * @param file
	 *        file to remove
	 */
	public void removeFile( AbstractFile file ) {
		removeFile( new AbstractFile[] { file } );
	}

	/**
	 * Remove multiply file from the Project
	 * 
	 * @param filesToRemove
	 *        Array with files to remove
	 */
	public void removeFile( AbstractFile[] filesToRemove ) {
		boolean isRemoved = false;

		// remove files
		for ( AbstractFile fileToRemove : filesToRemove ) {
			if ( !files.contains( fileToRemove ) ) continue;
			files.remove( fileToRemove );
			isRemoved = true;
		}

		// If some files was removed - notify the listeners
		if ( isRemoved ) {
			setIsModified( true );
			pcs.firePropertyChange( FILE_REMOVED, filesToRemove, null );
		}
	}

	/**
	 * Remove the temporary folders and it's content
	 */
	public void removeTemporaryFiles() {
		File tmpPath = new File( getTemporaryFolder() );
		if ( tmpPath.exists() ) {
			File[] files = tmpPath.listFiles();
			for ( File file : files ) {
				if ( !file.delete() ) {
					Logger.logError( "Project.RemoveTemporaryFiles()",
						"Can't delete a file: " + file.getName() );
				}
			}
			if ( !tmpPath.delete() ) {
				Logger.logError( "Project.RemoveTemporaryFiles()",
					"Can't delete a folder: " + tmpPath.getName() );
			}
		}
	}

	@Override
	public String toString() {
		return projectName;
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
