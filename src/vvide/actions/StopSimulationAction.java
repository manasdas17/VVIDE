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

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import vvide.Application;
import vvide.logger.Logger;
import vvide.ui.views.ConsoleView;

/**
 * Action to stop the simulation
 */
public class StopSimulationAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -6726986542314845853L;
	/**
	 * Current simulation thread
	 */
	private Thread currentSimulationThread = null;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for currentSimulationThread
	 *
	 * @param currentSimulationThread
	 *        the currentSimulationThread to set
	 */
	public void setCurrentSimulationThread( Thread currentSimulationThread ) {
		// stop the previous simulation
		if ( currentSimulationThread == null ) {
			this.currentSimulationThread = null;
			setEnabled( false );
		}
		else
			if ( this.currentSimulationThread != currentSimulationThread ) {
				this.actionPerformed( null );
				this.currentSimulationThread = currentSimulationThread;
				setEnabled( true );
			}
	}

	/**
	 * Getter for currentSimulationThread
	 *
	 * @return the currentSimulationThread
	 */
	public Thread getCurrentSimulationThread() {
		return currentSimulationThread;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public StopSimulationAction() {
		super( "Stop Simulation" );
		setEnabled( false );
		putValue( SHORT_DESCRIPTION, "Stop the current simulation" );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
				"/img/actions/file_simulate_stop.png" ) ) );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		if ( currentSimulationThread != null ) {
			currentSimulationThread.interrupt();
			try {
				currentSimulationThread.join( 5000 );
				if ( currentSimulationThread != null
					&& currentSimulationThread.isAlive() ) {
					JOptionPane.showMessageDialog( Application.mainFrame,
							"Error stopping previous simulation",
							Application.programName, JOptionPane.ERROR_MESSAGE );
				}
				// notify in the console
				((ConsoleView) Application.viewManager
						.getView( ConsoleView.CONSOLE_VIEW_ID ))
						.appendNormalText( "Simulating stopped" );
				currentSimulationThread = null;
				setEnabled( false );
			}
			catch ( InterruptedException ex ) {
				Logger.logError( this, ex );
			}
		}
	}
}
