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
import javax.swing.ImageIcon;

import vvide.Application;
import vvide.ProjectManager;
import vvide.logger.Logger;
import vvide.logger.Logger.MessageType;
import vvide.project.AbstractFile;
import vvide.ui.DialogResult;
import vvide.ui.ImportFileDialog;
import vvide.utils.IOMethods;

/**
 * Import an existed file to the project
 */
public class ImportFileAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -6740205282383756338L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public ImportFileAction() {
		super( "Import File" );
		putValue( SHORT_DESCRIPTION, "Import an existed file to the project" );
		putValue( LARGE_ICON_KEY,  new ImageIcon( getClass().getResource( "/img/actions/file_import.png" ) ));
		setEnabled( false );

		// Adding a listener to the current project
		Application.projectManager.addPropertyChangeListener(
				ProjectManager.CURRENT_PROJECT, new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent e ) {
						setEnabled( e.getNewValue() != null );
					}
				} );
	}

	@Override
	public void actionPerformed( ActionEvent arg0 ) {
		// show a dialog
		ImportFileDialog importFileDialog = new ImportFileDialog();
		importFileDialog.setVisible( true );

		// creating a file
		if ( importFileDialog.getResult() == DialogResult.OK ) {
			Class<? extends AbstractFile> fileClass =
					Application.projectManager
							.getClassForFileType( importFileDialog
									.getFileType() );
			if ( fileClass != null ) {
				try {
					AbstractFile newFile = fileClass.newInstance();
					newFile.setFileName( importFileDialog.getFileName() );
					String fileLocation;
					// Copy file into the project folder if it needed
					if ( importFileDialog.isCopyToProject() ) {
						File source = new File( importFileDialog.getFilePath() );
						fileLocation =
								Application.projectManager.getCurrentProject()
										.getProjectLocation();
						File destination =
								new File( fileLocation + newFile.getFileName() );
						IOMethods.copyFile( source, destination );
					}
					else {
						fileLocation = importFileDialog.getFileLocation();
					}
					newFile.setFileLocation( IOMethods.getRelativePath(
							Application.projectManager.getCurrentProject()
									.getProjectLocation(), fileLocation ) );
					Application.projectManager.getCurrentProject().addFile(
							newFile );
				}
				catch ( Exception ex ) {
					Logger.logError( this, ex );
				}
			}
			else {
				Logger.log( this.getClass().getCanonicalName(),
						MessageType.ERROR,
						"Can't get a class for specified file type: "
							+ importFileDialog.getFileType() );
			}
		}
	}
}
