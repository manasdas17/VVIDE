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
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import vvide.Application;
import vvide.ProjectManager;
import vvide.ViewManager;
import vvide.project.AbstractFile;
import vvide.project.Project;
import vvide.ui.views.ProjectView;

/**
 * A Action to compile simulate and show VCD file
 */
public class CompileSimulateShowFileAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 2375452285605179077L;
	/**
	 * Listener to enable the action
	 */
	private EnableSetter enabler = new EnableSetter();
	/**
	 * Project View
	 */
	private ProjectView projectView = (ProjectView) Application.viewManager
		.getView( ProjectView.PROJECT_VIEW_ID );
	/**
	 * Listener for the simulation
	 */
	private SimulationListener simulationListener = new SimulationListener();
	/**
	 * Current Project
	 */
	private Project currentProject = null;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public CompileSimulateShowFileAction() {
		super( "Compile all -> Show File" );
		setEnabled( false );
		putValue( SHORT_DESCRIPTION,
			"Compile all files, simulate and show simulated file" );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
			"/img/actions/file_comp_sim_show.png" ) ) );

		// Listener for Enable/Disable Action
		// Add TopLevel Listener
		Application.projectManager.addPropertyChangeListener(
			ProjectManager.CURRENT_PROJECT, new PropertyChangeListener() {

				@Override
				public void propertyChange( PropertyChangeEvent evt ) {
					if ( evt.getNewValue() != null ) {
						currentProject =
							Application.projectManager.getCurrentProject();
						currentProject.addPropertyChangeListener(
							Project.TOP_LEVEL_ENTITY, enabler );
					}
					else {
						currentProject.removePropertyChangeListener(
							Project.TOP_LEVEL_ENTITY, enabler );
						currentProject = null;
					}
					changeEnableState();
				}
			} );
		// Current File
		Application.projectManager.addPropertyChangeListener(
			ProjectManager.CURRENT_FILE, enabler );
		// Selected file
		projectView.addPropertyChangeListener( ProjectView.PROPERTY_VISIBLE,
			new PropertyChangeListener() {

				@Override
				public void propertyChange( PropertyChangeEvent evt ) {
					if ( (Boolean) evt.getNewValue() ) {
						projectView.addPropertyChangeListener(
							ProjectView.SELECTED_FILES, enabler );
					}
					else {
						projectView.removePropertyChangeListener(
							ProjectView.SELECTED_FILES, enabler );
					}
				}
			} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		Application.actionManager.getAction( "CompileAllFilesAction" )
			.actionPerformed( null );
		SimulateFileAction action =
			(SimulateFileAction) Application.actionManager
				.getAction( "SimulateFileAction" );
		action.addPropertyChangeListener( simulationListener );
		action.actionPerformed( null );
	}

	/**
	 * Change the enable state of the action
	 */
	private void changeEnableState() {
		
		Vector<AbstractFile> selectedFiles = projectView.getSelectedFiles();
		
		// enable if project opened
		// and a Top level setted
		// or there is a file opened
		// or a source file selected
		boolean enable = currentProject != null
			&& ( 
				currentProject.getTopLevelEntityFileName() != null
				|| Application.projectManager.getCurrentFile() != null
				|| 
					(
						Application.viewManager.isViewOpened( ProjectView.PROJECT_VIEW_ID ) 
						&&  selectedFiles != null 
						&& selectedFiles.size() == 1 
					)
				);

		setEnabled( enable );
	}

	/*
	 * ========================= Internal Classes =============================
	 */
	/**
	 * Listener to enable/disable action
	 */
	private class EnableSetter implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			changeEnableState();
		}
	}

	/**
	 * Class for listen a finishing of the simulation
	 */
	private class SimulationListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			((SimulateFileAction) (evt.getSource()))
				.removePropertyChangeListener( this );
			if ( evt.getPropertyName().equals(
				SimulateFileAction.SIMULATION_FINISHED ) ) {
				Application.actionManager.getAction( "ShowVCDFileAction" )
					.actionPerformed( null );
			}
		}
	}
}
