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
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import vvide.Application;
import vvide.ProjectManager;
import vvide.ViewManager;
import vvide.logger.Logger;
import vvide.project.AbstractFile;
import vvide.project.AbstractSourceFile;
import vvide.project.Project;
import vvide.simulator.AbstractSimulator;
import vvide.ui.views.ConsoleView;
import vvide.ui.views.ProjectView;
import vvide.utils.CommonMethods;

/**
 * Simulate the specified file
 */
public class SimulateFileAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 7712162474540953523L;
	/**
	 * Property for top level entity
	 */
	public static String SIMULATION_FINISHED = "SimulationFinished";
	/**
	 * Property for top level entity
	 */
	public static String SIMULATION_CANCELED = "SimulationCanceled";
	/**
	 * Simulation time
	 */
	private String simulationTime = null;
	/**
	 * Listener for the simulation
	 */
	private SimulationListener simulationListener = new SimulationListener();

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for simulationTime
	 * 
	 * @param simulationTime
	 *        the simulationTime to set
	 */
	public void setSimulationTime( String simulationTime ) {
		this.simulationTime = simulationTime;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public SimulateFileAction() {
		super( "Simulate File" );
		setEnabled( false );
		putValue( SHORT_DESCRIPTION, "Simulate the file" );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
			"/img/actions/file_simulate.png" ) ) );

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
	public void actionPerformed( ActionEvent evt ) {

		if ( Application.projectManager.getCurrentProject() == null ) return;

		Project currentProject = Application.projectManager.getCurrentProject();
		AbstractFile file = null;

		// topEntity - current File - selected file
		if ( currentProject.getTopLevelEntityFileName() != null ) {
			file =
				currentProject.getFile( currentProject
					.getTopLevelEntityFileName() );
		}
		else
			if ( Application.projectManager.getCurrentFile() != null ) {
				file = Application.projectManager.getCurrentFile();
			}
			else {
				ProjectView view =
					(ProjectView) Application.viewManager
						.getView( ProjectView.PROJECT_VIEW_ID );
				Vector<AbstractFile> selectedFiles = view.getSelectedFiles();
				if ( selectedFiles != null ) {
					file = selectedFiles.get( 0 );
				}
			}

		// Do nothing id the file not specified
		if ( file == null || !file.isSourceFile() ) return;

		AbstractSourceFile sourceFile = (AbstractSourceFile) file;
		AbstractSimulator simulator = currentProject.getSimulator();

		// Getting a Top-Level module in this file
		try {
			String topLevelModule =
				simulator.getTopLevelModuleName( sourceFile.getFileName() );
			if ( topLevelModule == null ) {
				Logger.logError( this.getClass().getName(),
					"No Top-Level Module found!" );
				return;
			}
			simulator.compile( sourceFile.getFileName(), topLevelModule );

			// Check the simulation time
			if ( simulationTime == null ) {
				// Getting a simulation time
				simulationTime = CommonMethods.getSimulationTime();
				// if canseled - do nothing
				if ( simulationTime == null ) { return; }
			}
			// Notify start
			((ConsoleView) Application.viewManager
				.getView( ConsoleView.CONSOLE_VIEW_ID ))
				.appendNormalText( "Start simulating" );
			simulator.addPropertyChangeListener( simulationListener );
			simulator.simulate( sourceFile.getFileName(), topLevelModule,
				simulationTime );

			Application.projectManager.getCurrentProject()
				.addLastSimulatedFile( sourceFile );

			simulationTime = null;
		}
		catch ( IOException e ) {
			Logger.logError( this, e );
			CommonMethods.showErrorMessage(e.getMessage());
		}

		// CommonMethods.printInConsole( simulator );
	}

	/*
	 * ======================== Internal Classes ==============================
	 */
	/**
	 * Class for listen a finishing of the simulation
	 */
	private class SimulationListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			((AbstractSimulator) (evt.getSource()))
				.removePropertyChangeListener( this );
			if ( evt.getPropertyName().equals(
				AbstractSimulator.SIMULATION_FINISHED ) ) {
				changeSupport.firePropertyChange( SIMULATION_FINISHED, null, null );
			}
			else {
				changeSupport.firePropertyChange( SIMULATION_CANCELED, null, null );
			}
		}
	}
}
