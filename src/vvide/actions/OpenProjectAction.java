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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import vvide.Application;
import vvide.actions.ui.SelectFileAction;
import vvide.logger.Logger;
import vvide.project.Project;
import vvide.utils.XMLUtils;

/**
 * An Action to open an Project.<br>
 * Check if the project is opened, close it and show dialog to open the project
 */
public class OpenProjectAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -7848113156160183719L;
	/**
	 * Project file path
	 */
	private String projectPath = null;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for project path
	 * 
	 * @param path
	 *        a path for the project file
	 */
	public void setProjectPath( String path ) {
		this.projectPath = path;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public OpenProjectAction() {
		super( "Open Project" );
		putValue( SHORT_DESCRIPTION, "Open the existed project" );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_O,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
			"/img/actions/project_open.png" ) ) );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		// getting a project file
		SelectFileAction action =
			new SelectFileAction( Application.mainFrame, "Open project",
				JFileChooser.FILES_ONLY, new FileNameExtensionFilter(
					"VVIDE project", Project.PROJECT_FILE_EXTENSION ) );
		try {
			action.setSetPathMethod( this, this.getClass().getMethod(
				"setProjectPath", String.class ) );
		}
		catch ( Exception e1 ) {
			Logger.logError( this, e1 );
		}
		action.actionPerformed( null );

		// if no file selected - return
		if ( projectPath == null ) return;

		// Loading a project
		File projectFile = new File( projectPath );
		Project project =
			(Project) new XMLUtils().loadFromXMLFile( projectFile );
		if ( project == null ) return;
		// Update project path
		String path = projectFile.getAbsolutePath();
		int sep = path.lastIndexOf( File.separator );
		project.setProjectLocation( path.substring( 0, sep ) );

		// Close the opened project and setting a new
		Application.actionManager.getAction( "CloseProjectAction" )
			.actionPerformed( null );
		project.setIsModified( false );
		Application.projectManager.setCurrentProject( project );
	}
}
