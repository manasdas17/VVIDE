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

import javax.swing.AbstractAction;

import vvide.Application;
import vvide.SignalManager;
import vvide.ui.DialogResult;
import vvide.ui.ExportToSVGDialog;
import vvide.utils.svg.SVGExporter;

/**
 * An Action to export visible signals to an SVG File
 */
public class ExportToSVGAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -7225446382509386110L;
	/**
	 * Listener to enable/disable Action
	 */
	private EnableActionListener enabler = new EnableActionListener();

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public ExportToSVGAction() {
		super( "Export to SVG File" );
		putValue( SHORT_DESCRIPTION, "Export visible signals to the SVG file" );
		setEnabled( false );

		Application.signalManager.addPropertyChangeListener(
				SignalManager.VISIBLE_SIGNAL_ADDED, enabler );
		Application.signalManager.addPropertyChangeListener(
				SignalManager.VISIBLE_SIGNAL_REMOVED, enabler );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		// Show the export dialog
		ExportToSVGDialog dialog = new ExportToSVGDialog();
		dialog.setVisible( true );
		// If result not Ok returning
		if ( dialog.getDialogResult() != DialogResult.OK ) return;
		new SVGExporter( dialog.getExportedSignals(),
				dialog.getStartPosition(), dialog.getStopPosition() )
				.exportToFile( dialog.getFileName() );
	}

	/*
	 * ========================= Internal Classes =============================
	 */
	/**
	 * A listener to enable/Disable the action
	 */
	private class EnableActionListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			setEnabled( Application.signalManager.getCountVisibleSignals() > 0 );
		}
	}
}
