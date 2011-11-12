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
import vvide.MarkerManager;
import vvide.ViewManager;
import vvide.signal.Marker;
import vvide.ui.views.ProjectView;
import vvide.ui.views.WaveView;

/**
 * Scroll the ViewPort to the Marker Position
 */
public class ScrollToMarkerAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -934518804093128388L;
	/**
	 * Instance of the WaveView
	 */
	private WaveView waveView = (WaveView) Application.viewManager
			.getView( ViewManager.WAVE_VIEW_ID );

	/*
	 * ============================ Methods ==================================
	 */
	public ScrollToMarkerAction() {
		super( "Scroll to Marker" );
		putValue( SHORT_DESCRIPTION,
				"Scroll the ViewPort to place the selected Marker in center" );
		setEnabled( false );

		// Listener for Enable/Disable Action
		waveView.addPropertyChangeListener( ProjectView.PROPERTY_VISIBLE,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						if ( (Boolean) evt.getNewValue() ) {
							setEnabled( Application.markerManager
									.getSelectedMarker() != null );
						}
						else {
							setEnabled( false );
						}
					}
				} );

		Application.markerManager.addPropertyChangeListener(
				MarkerManager.MARKER_SELECTED, new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						setEnabled( waveView != null
							&& evt.getNewValue() != null );
					}
				} );

	}

	@Override
	public void actionPerformed( ActionEvent e ) {

		Marker selectedMarker = Application.markerManager.getSelectedMarker();
		if ( !waveView.isDisplayable() || selectedMarker == null ) return;

		waveView.scrollToPosition( selectedMarker.getPosition() );
	}
}
