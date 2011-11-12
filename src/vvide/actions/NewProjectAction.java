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
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import vvide.Application;
import vvide.logger.Logger;
import vvide.logger.Logger.MessageType;
import vvide.project.Project;
import vvide.ui.DialogResult;
import vvide.ui.NewProjectDialog;
import vvide.utils.CommonMethods;
import vvide.utils.IOMethods;

/**
 * Create a new project
 */
public class NewProjectAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -1074694422916663695L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public NewProjectAction() {
		super( "New Project" );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_N,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() + KeyEvent.SHIFT_DOWN_MASK ) );

		putValue( SHORT_DESCRIPTION, "Create a new project" );
		putValue( LARGE_ICON_KEY,  new ImageIcon( getClass().getResource( "/img/actions/project_new.png" ) ));

	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		if ( CommonMethods.confirmSaveProject() == JOptionPane.CANCEL_OPTION )
			return;
		// show a dialog
		NewProjectDialog newProjectDialog = new NewProjectDialog();
		newProjectDialog.setVisible( true );

		// Create a new Project
		if ( newProjectDialog.getResult() == DialogResult.OK ) {
			
			Project newProject =
				new Project( newProjectDialog.getProjectName() );
			String location = newProjectDialog.getProjectLocation();

			// Make a folder for project
			if (newProjectDialog.isCreateFolder()) {
				
				location = IOMethods.addSeparator( location ) + newProject.getProjectName();
				File projectFolder = new File( location );
				if (!projectFolder.mkdir()) {
					Logger.log(this.getClass().getName(), MessageType.ERROR, "Error creating a folder for project.");
					return;
				}
			}
			newProject.setProjectLocation( location );
			newProject.setSimulator( newProjectDialog.getSimulator() );

			Application.projectManager.setCurrentProject( newProject );
			//Saving a project
			Application.actionManager.getAction( "SaveProjectAction" ).actionPerformed( null );
		}
	}
}
