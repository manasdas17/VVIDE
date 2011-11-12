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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import vvide.Application;
import vvide.ProjectManager;
import vvide.logger.Logger;
import vvide.logger.Logger.MessageType;
import vvide.project.AbstractFile;
import vvide.ui.DialogResult;
import vvide.ui.NewFileDialog;
import vvide.utils.IOMethods;

/**
 * Add a new File to the project
 */
public class NewFileAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 6969734931474340965L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public NewFileAction() {
		super( "New File" );
		setEnabled( false );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_N,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
		putValue( SHORT_DESCRIPTION, "Create a new file" );
		putValue( LARGE_ICON_KEY,  new ImageIcon( getClass().getResource( "/img/actions/file_new.png" ) ));

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
	public void actionPerformed( ActionEvent e ) {
		// show a dialog
		NewFileDialog newFileDialog = new NewFileDialog();
		newFileDialog.setFileLocation( Application.projectManager
				.getCurrentProject().getProjectLocation() );
		newFileDialog.setVisible( true );

		// creating a file
		if ( newFileDialog.getResult() == DialogResult.OK ) {
			Class<? extends AbstractFile> fileClass =
					Application.projectManager
							.getClassForFileType( newFileDialog.getFileType() );
			if ( fileClass != null ) {
				try {
					AbstractFile newFile = fileClass.newInstance();
					// Correcting a file name
					String fileName = newFileDialog.getFileName();
					fileName =
							IOMethods.addFileExtension( fileName, newFile
									.getDefaultFileExtension() );
					newFile.setFileName( fileName );
					newFile.setFileLocation( IOMethods.getRelativePath(
							Application.projectManager.getCurrentProject()
									.getProjectLocation(), newFileDialog
									.getFileLocation() ) );
					Application.projectManager.getCurrentProject().addFile(
							newFile );
					newFile.saveFile( "" );
				}
				catch ( Exception ex ) {
					Logger.logError( this, ex );
				}
			}
			else {
				Logger.log( this.getClass().getCanonicalName(),
						MessageType.ERROR,
						"Can't get a class for specified file type: "
							+ newFileDialog.getFileType() );
			}
		}
	}
}
