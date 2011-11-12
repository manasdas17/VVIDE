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
import vvide.project.AbstractFile;
import vvide.project.AbstractSourceFile;
import vvide.ui.views.EditorView;
import vvide.utils.IOMethods;

/**
 * Action to save current edited file
 */
public class SaveFileAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 7863186264161626511L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public SaveFileAction() {
		super("Save File");
		setEnabled( false );
		putValue( SHORT_DESCRIPTION, "Save the file" );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_S,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
		putValue( LARGE_ICON_KEY,  new ImageIcon( getClass().getResource( "/img/actions/file_save.png" ) ));
		
		// Add CurrentFileListener
		Application.projectManager.addPropertyChangeListener( ProjectManager.CURRENT_FILE, new PropertyChangeListener() {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {
				setEnabled( e.getNewValue() != null );
			}
		});	}
	
	@Override
	public void actionPerformed( ActionEvent e ) {
		
		// Do nothing if no file to save
		AbstractFile fileToSave = Application.projectManager.getCurrentFile(); 
		if ( fileToSave == null) return;
		
		// Do nothing if there is no editor for file
		EditorView editor = Application.viewManager.getEditorFor(fileToSave);
		if (editor == null) return;
		
		fileToSave.saveFile( editor.getCodeEditor().getText() );
		
		// copy file to the temporary folder
		if (fileToSave.isSourceFile()) {
			IOMethods.copyFileToTempFolder( (AbstractSourceFile) fileToSave );
		}
		
		// Notify that the file not more changed
		editor.setChanged( false );
	}
}
