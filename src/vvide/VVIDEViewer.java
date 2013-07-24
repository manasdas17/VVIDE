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

import vvide.ui.MainFrame;
import vvide.ui.views.ConsoleView;
import vvide.ui.views.MarkerView;
import vvide.ui.views.SignalTreeView;
import vvide.ui.views.WaveView;

/**
 * Startup code for the program
 */
public class VVIDEViewer {

	/**
	 * Start the program
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {

		// Store all managers
		Application.layoutFileName = "layout_viewer.xml";
		Application.programName = "VVIDE Viewer";
		Application.uiXmlFile = "setting_dialog_viewer.xml";
		Application.settingsManager = SettingsManager.loadSettings();
		Application.signalManager = new SignalManager();
		Application.markerManager = new MarkerManager();
		Application.viewManager = new ViewManager();

		Application.viewManager.addView(ConsoleView.CONSOLE_VIEW_ID,
				new ConsoleView(ConsoleView.CONSOLE_VIEW_ID));
		Application.viewManager.addView(WaveView.WAVE_VIEW_ID, new WaveView(
				WaveView.WAVE_VIEW_ID));
		Application.viewManager.addView(MarkerView.MARKER_VIEW_ID,
				new MarkerView(MarkerView.MARKER_VIEW_ID));
		Application.viewManager.addView(SignalTreeView.SIGNAL_TREE_VIEW_ID,
				new SignalTreeView(SignalTreeView.SIGNAL_TREE_VIEW_ID));

		Application.mainFrame = new MainFrame();
		Application.actionManager = new ActionManager("action_map_viewer.xml");

		// Init
		Application.mainFrame.initFrame("main_menu_viewer", null);
		Application.viewManager.initViews();

		// Start a main window controller
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Application.mainFrame.setVisible(true);
				// Load Layout
				Application.viewManager.loadLayout();
			}
		});
	}
}
