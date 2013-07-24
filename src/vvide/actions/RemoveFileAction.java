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
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import vvide.Application;
import vvide.ViewManager;
import vvide.logger.Logger;
import vvide.project.AbstractFile;
import vvide.ui.views.ProjectView;

/**
 * Remove file from the project
 */
public class RemoveFileAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 3116271285195239394L;
	/**
	 * Instance of the WaveView
	 */
	private ProjectView projectView = (ProjectView) Application.viewManager
			.getView( ProjectView.PROJECT_VIEW_ID );
	/**
	 * Listener for selecting files
	 */
	private SelectedFilesListener selectedFileListener =
			new SelectedFilesListener();

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public RemoveFileAction() {
		super( "Remove File" );
		setEnabled( false );
		putValue( SHORT_DESCRIPTION, "Remove a file from project" );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
				"/img/actions/file_delete.png" ) ) );

		projectView.addPropertyChangeListener( ProjectView.PROPERTY_VISIBLE,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						if ( (Boolean) evt.getNewValue() ) {
							projectView.addPropertyChangeListener(
									ProjectView.SELECTED_FILES,
									selectedFileListener );
						}
						else {
							projectView.removePropertyChangeListener(
									ProjectView.SELECTED_FILES,
									selectedFileListener );
						}
					}
				} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {

		if ( !projectView.isDisplayable()) return;

		// Getting a file list to remove
		Vector<AbstractFile> filesToRemove = projectView.getSelectedFiles();

		// Ask about physically removing of the file
		JCheckBox isToDelete = new JCheckBox( "Remove file from disk?" );

		// Build the list of file names
		String fileNames = "";
		for ( int i = 0; (i < 5 && i < filesToRemove.size()); ++i ) {
			fileNames += "\n" + filesToRemove.get( i ).getFileName();
		}

		Object[] params =
				{ "Are you sure to remove this file(s)?" + fileNames,
						isToDelete };
		int result =
				JOptionPane.showConfirmDialog( Application.mainFrame, params,
						Application.programName, JOptionPane.YES_NO_OPTION );
		// Remove a file
		if ( result == JOptionPane.YES_OPTION ) {
			Application.projectManager.getCurrentProject().removeFile(
					filesToRemove.toArray( new AbstractFile[] {} ) );
			// if needed - delete from the disk
			if ( isToDelete.isSelected() ) {
				for ( AbstractFile fileToRemove : filesToRemove ) {
					File file = new File( fileToRemove.getFullPath() );
					try {
						file.delete();
					}
					catch ( Exception e1 ) {
						Logger.logError( this, e1 );
					}
				}
			}
		}
	}

	/*
	 * ======================= Internal Classes ===============================
	 */
	/**
	 * A listener for selected files
	 */
	private class SelectedFilesListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			setEnabled( projectView.isDisplayable()
				&& projectView.getSelectedFiles() != null
				&& projectView.getSelectedFiles().size() > 0 );
		}
	}
}
