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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import vvide.Application;
import vvide.ViewManager;
import vvide.parser.AbstractParser;
import vvide.parser.VCDParser;
import vvide.ui.ParsingProgressDialog;
import vvide.ui.views.SignalTreeView;
import vvide.ui.views.WaveView;
import vvide.ui.views.signaltree.SignalTreeModel;

/**
 * An action to open any VCD File
 */
public class OpenVCDFileAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 3270915484611798384L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public OpenVCDFileAction() {
		super( "OpenVCD File" );
		putValue( SHORT_DESCRIPTION, "Open an existed VCD File" );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {

		// Make dialog
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle( "Select a file" );
		fileChooser.setFileFilter( new FileFilter() {

			@Override
			public String getDescription() {
				return "Value Change Dump File (*.vcd)";
			}

			@Override
			public boolean accept( File f ) {
				return (f.isDirectory() || (f.isFile() && f.getName().toLowerCase().endsWith(
					".vcd" )));
			}
		} );

		// Show Dialog
		int result = fileChooser.showOpenDialog( Application.mainFrame );
		if ( result == JFileChooser.APPROVE_OPTION ) {
			// Reset the signal manager
			Application.signalManager.removeAll();
			Application.markerManager.removeAll();

			// Creating a parser
			VCDParser parser = new VCDParser();

			// Show the process window
			ParsingProgressDialog ppd = new ParsingProgressDialog();
			ppd.setParser( parser );
			ppd.setVisible( true );

			// Adding listener for the end of parsing
			parser.addPropertyChangeListener( AbstractParser.FINISHED,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						// Show the signal tree view
						SignalTreeView signalView =
							(SignalTreeView) Application.viewManager
								.getView( ViewManager.SIGNAL_TREE_VIEW_ID );
						signalView.setModel( new SignalTreeModel(
							Application.signalManager.getMainScope() ) );

						((WaveView) (Application.viewManager
							.getView( ViewManager.WAVE_VIEW_ID )))
							.resetSettings();

					}
				} );

			// Start
			parser.startParse( fileChooser.getSelectedFile() );

		}
	}
}
