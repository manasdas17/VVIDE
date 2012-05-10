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
package vvide.simulator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import vvide.Application;
import vvide.ViewManager;
import vvide.actions.StopSimulationAction;
import vvide.logger.Logger;
import vvide.ui.views.ConsoleView;

/**
 * An Abstract simulator
 */
public abstract class AbstractSimulator {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * Property to notify about the finishing the simulation
	 */
	public final static String SIMULATION_FINISHED = "SimulationFinshed";
	/**
	 * Property for top level entity
	 */
	public static String SIMULATION_CANCELED = "SimulationCanceled";

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Property change support
	 */
	private PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	/**
	 * A name of the generated vcd file
	 */
	public final static String VCDFILE_NAME = "simulated.vcd";
	/**
	 * A Vector with an output data of the command
	 */
	protected Vector<String> outputData = null;
	/**
	 * A Vector with an error data of the command
	 */
	protected Vector<String> errorData = null;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for outputData
	 * 
	 * @return the outputData
	 */
	public Vector<String> getOutputData() {
		return outputData;
	}

	/**
	 * Getter for errorData
	 * 
	 * @return the errorData
	 */
	public Vector<String> getErrorData() {
		return errorData;
	}

	/**
	 * Getter for error message Pattern
	 * 
	 * @return error message Pattern
	 */
	public abstract Pattern getErrorMessagePattern();


	/**
	 * Return the first founded top level module (or Entity in VHDL) in the file
	 * 
	 * @param fileName
	 *        a name of the file to get an entity
	 * @return String with a module name
	 * @throws IOException
	 */
	public abstract String getTopLevelModuleName( String fileName )
		throws IOException;
	
	/**
	 * Return the description of the simulator
	 * @return string with the description
	 */
	public abstract String getDescription(); 
	
	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Check the file syntax
	 * 
	 * @param fileName
	 *        a name of the file to syntax check
	 */
	public abstract List<String> getCheckSyntaxCommand( String fileName );

	/**
	 * Compile the file
	 * 
	 * @param fileName
	 *        a name of the file to analyze
	 */
	public abstract void analyzeFile( String fileName ) throws IOException;

	/**
	 * Compile the entity
	 * 
	 * @param unitName
	 *        a name of the unit to compile (entity for ghdl or file name for
	 *        icarus)
	 * @param topLevelModule
	 * @throws IOException 
	 */
	public abstract void compile( String fileName, String unitName ) throws IOException;

	/**
	 * Simulate the file
	 * 
	 * @param unitName
	 *        a name of the unit to compile (entity for ghdl or file name for
	 *        icarus
	 * @param stopTime
	 *        a simulation time
	 * @throws IOException 
	 */
	public abstract void simulate( String fileName, String unitName,
		String stopTime ) throws IOException;

	/**
	 * Execute a command and return the output as array of strings
	 * 
	 * @param command
	 *        list with a command and params
	 * @return array with output
	 * @throws IOException 
	 */
	protected void executeCommand( List<String> command ) throws IOException {
		executeCommand( command, Application.projectManager.getCurrentProject()
			.getTemporaryFolder() );
	}

	/**
	 * Execute a command and return the output as array of strings
	 * 
	 * @param command
	 *        list with a command and params
	 * @param workingDirectory
	 *        working directory to start the process
	 * 
	 * @return array with output
	 * @throws IOException 
	 */
	protected void executeCommand( List<String> command, String workingDirectory ) throws IOException {
		// Result string
		outputData = new Vector<String>();
		errorData = new Vector<String>();
		
		// Check the command
		if (command == null || command.size() == 0) return;

		// Creating a process builder
		ProcessBuilder builder = new ProcessBuilder( command );
		builder.directory( new File( workingDirectory ) );
		
		// Print command
		// Print the command
		if (Application.settingsManager.getVerboseConsoleOutput()) {
			StringBuilder commanString = new StringBuilder("Command: ");
			for (String commandPart : command)
				commanString.append(commandPart).append(' ');

			((ConsoleView) Application.viewManager
					.getView(ViewManager.CONSOLE_VIEW_ID))
					.appendNormalText(commanString.toString());
		}

		// Starting a process
		try {
			Process process = builder.start();

			// Reading the output
			BufferedReader br =
				new BufferedReader( new InputStreamReader( process
					.getInputStream() ) );
			String line;
			while ( (line = br.readLine()) != null ) {
				outputData.add( line );
			}
			br.close();

			// Reading the errors
			br =
				new BufferedReader( new InputStreamReader( process
					.getErrorStream() ) );
			while ( (line = br.readLine()) != null ) {
				errorData.add( line );
			}
			br.close();
		}
		catch ( IOException e ) {
			Logger.logError( this, e );
			throw e;
		}
	}

	/**
	 * Start a new Thread with the simulation
	 * 
	 * @param command
	 *        a command to execute
	 */
	protected void simulateInThread( List<String> command ) {
		SimulationThread simulationThread =
			new SimulationThread( this, command );

		StopSimulationAction action =
			(StopSimulationAction) Application.actionManager
				.getAction( "StopSimulationAction" );
		action.setCurrentSimulationThread( simulationThread );

		simulationThread.start();
	}
	
	@Override
	public String toString() {
		return getDescription();
	}

	/**
	 * Notify listeners that the simulation was finished
	 */
	void fireSimulationFinished() {
		pcs.firePropertyChange( SIMULATION_FINISHED, null, null );
	}

	/**
	 * Notify listeners that the simulation was canceled
	 */
	void fireSimulationCanceled() {
		pcs.firePropertyChange( SIMULATION_CANCELED, null, null );
	}

	/**
	 * Add a listener to a specified property
	 * 
	 * @param property
	 *        a name of the property
	 * @param listener
	 *        listener to add
	 */
	public void addPropertyChangeListener( String property,
		PropertyChangeListener listener ) {
		this.pcs.addPropertyChangeListener( property, listener );
	}

	/**
	 * Add a listener to all events
	 * 
	 * @param listener
	 *        listener to add
	 */
	public void addPropertyChangeListener( PropertyChangeListener listener ) {
		this.pcs.addPropertyChangeListener( listener );
	}

	/**
	 * Remove a listener from all events
	 * 
	 * @param listener
	 *        listener to remove
	 */
	public void removePropertyChangeListener( PropertyChangeListener listener ) {
		this.pcs.removePropertyChangeListener( listener );
	}

	/**
	 * Remove a listener from a specified events
	 * 
	 * @param property
	 *        a name of the property
	 * @param listener
	 *        listener to remove
	 */
	public void removePropertyChangeListener( String property,
		PropertyChangeListener listener ) {
		this.pcs.removePropertyChangeListener( property, listener );
	}
}
