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
package vvide;

import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import vvide.ui.MainFrame;

/**
 * Startup code for the program
 */
public class VVIDE {

	/**
	 * Start the program
	 * 
	 * @param args
	 *        command line arguments
	 */
	public static void main( String[] args ) {

		// Init the RTextArea
		new RSyntaxTextArea();

		// Store all managers
		Application.settingsManager = SettingsManager.loadSettings();
		Application.projectManager = new ProjectManager();
		Application.signalManager = new SignalManager();
		Application.markerManager = new MarkerManager();
		Application.viewManager = new ViewManager();
		Application.mainFrame = new MainFrame();
		Application.actionManager = new ActionManager();

		// Init
		Application.mainFrame.initFrame();
		Application.viewManager.initViews();

		// Start a main window controller
		SwingUtilities.invokeLater( new Runnable() {

			@Override
			public void run() {
				Application.mainFrame.setVisible( true );
				// Load Layout
				Application.viewManager.loadLayout();
			}
		} );
	}
}
