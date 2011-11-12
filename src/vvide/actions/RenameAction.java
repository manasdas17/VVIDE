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

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import vvide.Application;
import vvide.ProjectManager;
import vvide.ViewManager;
import vvide.project.AbstractFile;
import vvide.project.Project;
import vvide.ui.views.ProjectView;

/**
 * An Action to show file Properties
 */
public class RenameAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 3977419481563419047L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public RenameAction() {
		super( "Rename" );
		putValue( SHORT_DESCRIPTION, "Rename the selected item" );
		setEnabled( false );

		// Add CurrentFileListener
		Application.projectManager.addPropertyChangeListener(
				ProjectManager.CURRENT_PROJECT, new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						setEnabled( evt.getNewValue() != null );
					}
				} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		// getting the selected item
		if ( Application.viewManager.isViewOpened( ViewManager.PROJECT_VIEW_ID ) ) {
			ProjectView projectVew =
					(ProjectView) Application.viewManager
							.getView( ViewManager.PROJECT_VIEW_ID );
			Object selectedObject = projectVew.getSelectedItem();
			if ( selectedObject == null || selectedObject instanceof Project ) {
				renameProject();
			}
			else {
				renameFile( (AbstractFile) selectedObject );
			}

		}
		else {
			renameProject();
		}

	}

	/**
	 * Show properties of the selected file
	 * 
	 * @param selectedFile
	 *        file to show properties
	 */
	private void renameFile( AbstractFile selectedFile ) {

		String newName =
				JOptionPane.showInputDialog( Application.mainFrame,
						"Please give a new name", selectedFile.getFileName() );

		// Save the changes if needed
		if ( newName != null && !newName.equals( selectedFile.getFileName() ) ) {
			
			File oldFile = new File( selectedFile.getFullPath() );
			File newFile =
					new File( Application.projectManager.getCurrentProject()
							.getProjectLocation()
						+ selectedFile.getFilePath() + newName );
			if ( !oldFile.renameTo( newFile ) ) {
				JOptionPane.showMessageDialog( Application.mainFrame,
						"Can't rename the file", "Error",
						JOptionPane.ERROR_MESSAGE );
				return;
			}
			// Rename the TopEntity
			if (Application.projectManager.getCurrentProject().getTopLevelEntityFileName().equals( selectedFile.getFileName() )) {
				Application.projectManager.getCurrentProject().setTopLevelEntityFileName( newName );
			}
			selectedFile.setFileName( newName );
		}
	}

	/**
	 * Show the properties of the project
	 */
	private void renameProject() {
		Project project = Application.projectManager.getCurrentProject();
		String newName =
				JOptionPane.showInputDialog( Application.mainFrame,
						"Please give a new name", project.getProjectName() );

		// Save the changes if needed
		if ( newName != null && !newName.equals( project.getProjectName() ) ) {
			project.setProjectName( newName );
		}
	}
}
