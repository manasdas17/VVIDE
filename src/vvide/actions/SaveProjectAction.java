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
package vvide.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import vvide.Application;
import vvide.ProjectManager;
import vvide.logger.Logger;
import vvide.project.Project;
import vvide.utils.IOMethods;
import vvide.utils.XMLUtils;

/**
 * Action to save the project
 */
public class SaveProjectAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -8841393230578745611L;
	/**
	 * Project changed listener
	 */
	private ProjectChangedListener projectChangedListener = new ProjectChangedListener();

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public SaveProjectAction() {
		super( "Save Project" );
		setEnabled( false );
		putValue( SHORT_DESCRIPTION, "Save the opened project" );
		putValue( LARGE_ICON_KEY,  new ImageIcon( getClass().getResource( "/img/actions/project_save.png" ) ));
		
		// Adding a listener to the current project
		Application.projectManager.addPropertyChangeListener( ProjectManager.CURRENT_PROJECT, new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {
				// Apply a listener to the change
				Project project = (Project) e.getOldValue();
				if (project != null) {
					project.removePropertyChangeListener( Project.PROJECT_MODIFIED,  projectChangedListener);
				}
				project = (Project) e.getNewValue();
				if (project != null) {
					project.addPropertyChangeListener( Project.PROJECT_MODIFIED,  projectChangedListener);
				}
				setEnabled( project != null );
			}
		});
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		Project project = Application.projectManager.getCurrentProject();
		if (project == null) return;

		//check if the file exists
		String fileName = project.getProjectLocation() + project.getProjectName() + "." + Project.PROJECT_FILE_EXTENSION;
		File projFile = new File( fileName );
		if (projFile.exists()) {
			//making a backup
			try {
				IOMethods.backUpFile( projFile );
				projFile.delete();
				projFile.createNewFile();
			}
			catch ( IOException e1 ) {
				Logger.logError( this, e1 );
				return;
			}
		}
		
		//Saving a project
		new XMLUtils().saveToXMLFile( project, projFile );
		
		project.setIsModified( false );
	}
	
	/*
	 * ======================= Internal Classes ==============================
	 */
	/**
	 * Listener for project changed status
	 */
	private class ProjectChangedListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			// if a project changed - enable the action
			setEnabled( (Boolean) evt.getNewValue() );
		}
	}
}
