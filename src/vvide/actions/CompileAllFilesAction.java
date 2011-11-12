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
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import vvide.Application;
import vvide.ProjectManager;
import vvide.project.AbstractFile;
import vvide.project.AbstractSourceFile;
import vvide.project.Project;
import vvide.simulator.AbstractSimulator;
import vvide.utils.ActionUtils;

/**
 * Compile all files
 */
public class CompileAllFilesAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -4879466859429488472L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public CompileAllFilesAction() {
		super( "Compile all files" );
		setEnabled( false );
		putValue( SHORT_DESCRIPTION, "Compile all source files in the project" );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
			"/img/actions/file_compile_all.png" ) ) );

		// Listener for Enable/Disable Action
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

		Project currentProject = Application.projectManager.getCurrentProject();
		if ( currentProject == null ) return;

		// Compiling all source files in the Proiject
		List<AbstractFile> files = currentProject.getFiles();
		AbstractSimulator simulator = currentProject.getSimulator();
		try {
			for ( AbstractFile file : files ) {
				if ( file.isSourceFile() ) {
					ActionUtils.compileFile( (AbstractSourceFile) file,
						simulator );
				}
			}
		}
		catch ( IOException e1 ) {}
	}
}
