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

import java.io.File;
import java.io.IOException;
import java.util.List;

import vvide.Application;
import vvide.ViewManager;
import vvide.actions.StopSimulationAction;
import vvide.logger.Logger;
import vvide.ui.views.ConsoleView;

/**
 * A Separate thread for simulations
 */
public class SimulationThread extends Thread {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Command to execute
	 */
	private List<String> command = null;
	/**
	 * A simulator
	 */
	private AbstractSimulator simulator = null;
	/**
	 * Process with a simulator
	 */
	private Process process;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public SimulationThread( AbstractSimulator abstractSimulator,
			List<String> command ) {
		this.simulator = abstractSimulator;
		this.command = command;
	}

	@Override
	public void run() {
		if ( simulator == null || command == null ) return;

		// Creating a process builder
		ProcessBuilder builder = new ProcessBuilder( command );
		builder.directory( new File( Application.projectManager
				.getCurrentProject().getTemporaryFolder() ) );
		ConsoleView console =
				(ConsoleView) Application.viewManager
						.getView( ViewManager.CONSOLE_VIEW_ID );

		// Starting a process
		try {
			process = builder.start();

			ReaderThread outputReader =
					new ReaderThread( console.getErrorText(), process
							.getInputStream() );
			outputReader.start();
			ReaderThread errorReader =
					new ReaderThread( console.getErrorText(), process
							.getErrorStream() );
			errorReader.start();

			try {
				process.waitFor();
				simulator.fireSimulationFinished();
			}
			catch ( InterruptedException e ) {
				process.destroy();
				simulator.fireSimulationCanceled();			}
		}
		catch ( IOException e ) {
			Logger.logError( this, e );
		}

		StopSimulationAction action =
				(StopSimulationAction) Application.actionManager
						.getAction( "StopSimulationAction" );
		action.setCurrentSimulationThread( null );
		console.appendNormalText( "Stop simulating" );
	}
}
