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
import java.io.FilenameFilter;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import vvide.Application;
import vvide.ProjectManager;
import vvide.ViewManager;
import vvide.parser.AbstractParser;
import vvide.parser.VCDParser;
import vvide.project.AbstractSourceFile;
import vvide.project.Project;
import vvide.signal.AbstractSignal;
import vvide.signal.NumeralSystem;
import vvide.signal.VisibleSignal;
import vvide.ui.ParsingProgressDialog;
import vvide.ui.views.MarkerView;
import vvide.ui.views.SignalTreeView;
import vvide.ui.views.WaveView;
import vvide.ui.views.signaltree.SignalTreeModel;
import vvide.utils.CommonMethods;

/**
 * Start the parsing process and show the signals
 */
public class ShowVCDFileAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -903805370268655141L;
	/**
	 * Wave View
	 */
	private WaveView waveView = (WaveView) Application.viewManager
		.getView( ViewManager.WAVE_VIEW_ID );
	/**
	 * Backup
	 */
	private WaveViewBackupData backup = null;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public ShowVCDFileAction() {
		super( "Show simulated file" );
		putValue( SHORT_DESCRIPTION,
			"Parse a generated VCD File and show the waves" );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
			"/img/actions/show_vcd.png" ) ) );

		setEnabled( false );

		// Adding a listener to the current project
		Application.projectManager.addPropertyChangeListener(
			ProjectManager.CURRENT_PROJECT, new PropertyChangeListener() {

				@Override
				public void propertyChange( PropertyChangeEvent e ) {
					setEnabled( e.getNewValue() != null );
				}
			} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {

		// Getting the vcd file
		Project currentProject = Application.projectManager.getCurrentProject();
		
		File tmpDir = new File(currentProject.getTemporaryFolder());
		File vcdFile = null;
		long time = 0;
		for (File nextFile : tmpDir.listFiles( new VcdFilter())) {
			if (nextFile.lastModified() > time) {
				time = nextFile.lastModified();
				vcdFile = nextFile;
			}
		}

		// If the file not exists or not modified - do nothing
		if ( vcdFile == null) {
			CommonMethods.showErrorMessage( "No VCD File found" );
			return;
		}

		List<AbstractSourceFile> lastSimulations =
			currentProject.getLastSimulatedFile();
		if ( (lastSimulations.size() == 2 && lastSimulations.get( 0 ) == lastSimulations
			.get( 1 ))
			|| (lastSimulations.size() == 1 && Application.signalManager
				.getCountVisibleSignals() > 0) ) {

			// Store a view and signals data
			backup = new WaveViewBackupData();
			backup.position = waveView.getStartVisibleTime();
			backup.zoom = waveView.getZoom();
			backup.selectedSignalIndex =
				Application.signalManager.getSelectedSignalIndex();
			backup.visibleSignals =
				new Vector<ShowVCDFileAction.VisibleSignalBackupData>();
			for ( VisibleSignal signal : Application.signalManager
				.getVisibleSignals() ) {
				VisibleSignalBackupData data = new VisibleSignalBackupData();
				data.fullPath = signal.getSignal().getFullPath();
				data.system = signal.getNumeralSystem();
				backup.visibleSignals.add( data );
			}

			// Reset the signal manager
			Application.signalManager.removeAll();
		}
		else {
			// Reset the signal manager
			Application.signalManager.removeAll();
			Application.markerManager.removeAll();
			backup = null;
		}

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

					// Show the MarkerView
					MarkerView markerView =
						(MarkerView) Application.viewManager
							.getView( ViewManager.MARKER_VIEW_ID );
					markerView.loadMarkers();

					waveView.resetSettings();

					// restoring the backup
					if ( backup != null ) {
						waveView.setZoom( backup.zoom );
						waveView.scrollToPosition( backup.position );
						Vector<VisibleSignal> newVisibleSignals = new Vector<VisibleSignal>();
						for ( VisibleSignalBackupData data : backup.visibleSignals ) {
							AbstractSignal newSignal = Application.signalManager
									.findSignal( data.fullPath );
							if ( newSignal != null ) {
								VisibleSignal signal =
									new VisibleSignal( newSignal );
								signal.setNumeralSystem( data.system );
								newVisibleSignals.add( signal );
							}
						}
						if (newVisibleSignals.size() > 0) {
							Application.signalManager.addAllToVisible( newVisibleSignals );
						}
						if ( Application.signalManager.getCountVisibleSignals() <= backup.selectedSignalIndex ) {
							Application.signalManager
								.setSelectedSignalIndex( backup.selectedSignalIndex );
						}
						backup = null;
					}
				}
			} );

		// Start
		parser.startParse( vcdFile );
	}

	/*
	 * ======================== Internal Classes ==============================
	 */
	/**
	 * BackUp data for view
	 */
	private class WaveViewBackupData {

		/**
		 * Current zoom
		 */
		public long zoom;
		/**
		 * Current position
		 */
		public long position;
		/**
		 * Selected signal index
		 */
		public int selectedSignalIndex;
		/**
		 * Visible signals
		 */
		public Vector<VisibleSignalBackupData> visibleSignals;
	}

	/**
	 * BackUpData for Visible signal
	 */
	private class VisibleSignalBackupData {

		/**
		 * Full path to the signal
		 */
		public String fullPath;
		/**
		 * Numeral system
		 */
		public NumeralSystem system;
	}

	/**
	 * FileFilter for vcd files
	 */
	private class VcdFilter implements FilenameFilter {

		@Override
		public boolean accept( File dir, String name ) {
			return name.toLowerCase( Locale.ENGLISH ).endsWith( "vcd" );
		}

	}
}
