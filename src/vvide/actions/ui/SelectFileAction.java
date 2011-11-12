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
package vvide.actions.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import vvide.logger.Logger;

/**
 * Show a select folder dialog and execute a specified method to set a selected
 * folder or file.
 */
public class SelectFileAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -3576574927937412133L;
	/**
	 * A method to set the value
	 */
	private Method setPathMethod = null;
	/**
	 * Selected path
	 */
	private String selectedPath = null;
	/**
	 * Object to incoking the setPath method
	 */
	private Object invokeObject;
	/**
	 * Owner of the dialog
	 */
	private Component owner;
	/**
	 * Dialog to select a file
	 */
	private JFileChooser fileChooserDialog;
	/**
	 * Flag to set that will be used a save dialog
	 */
	private final boolean isSaveDialog;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for setPathMethod
	 * 
	 * @param object
	 *        an Object to invoked
	 * @param setPathMethod
	 *        the setPathMethod to set
	 */
	public void setSetPathMethod( Object object, Method setPathMethod ) {
		this.invokeObject = object;
		this.setPathMethod = setPathMethod;
	}

	/**
	 * Getter for selectedPath
	 * 
	 * @return the selectedPath
	 */
	public String getSelectedPath() {
		return selectedPath;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param fileFilter
	 *        A file filter
	 * @param fileSelectionMode
	 *        file selection mode for the JFileChooser
	 * @param title
	 *        dialog title
	 * @param owner
	 *        owner of the dialog
	 */
	public SelectFileAction( Component owner, String title,
		int fileSelectionMode, FileFilter fileFilter ) {
		this( owner, title, fileSelectionMode, fileFilter, false );
	}

	/**
	 * Constructor
	 * 
	 * @param fileFilter
	 *        A file filter
	 * @param fileSelectionMode
	 *        file selection mode for the JFileChooser
	 * @param title
	 *        dialog title
	 * @param owner
	 *        owner of the dialog
	 * @param isSaveDialog
	 *        flat to use a save dialog
	 */
	public SelectFileAction( Component owner, String title,
		int fileSelectionMode, FileFilter fileFilter, boolean isSaveDialog ) {
		super( "..." );
		this.isSaveDialog = isSaveDialog;
		fileChooserDialog = new JFileChooser();
		fileChooserDialog.setDialogTitle( title );
		fileChooserDialog.setFileSelectionMode( fileSelectionMode );
		if ( fileFilter != null )
			fileChooserDialog.setFileFilter( fileFilter );
		this.owner = owner;
	}

	@Override
	public void actionPerformed( ActionEvent arg0 ) {
		selectedPath = null;
		if ( isSaveDialog ) {
			fileChooserDialog.showSaveDialog( owner );
		}
		else {
			fileChooserDialog.showOpenDialog( owner );
		}

		// anpassen selected path
		if ( fileChooserDialog.getSelectedFile() != null ) {
			selectedPath =
				fileChooserDialog.getSelectedFile().getAbsolutePath();
		}
		
		if ( setPathMethod != null && invokeObject != null ) {
			try {
				setPathMethod.invoke( invokeObject, selectedPath );
			}
			catch ( Exception e ) {
				Logger.logError( this, e );
			}
		}
	}
}
