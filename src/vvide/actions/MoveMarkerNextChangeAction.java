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
import javax.swing.ImageIcon;

import vvide.Application;
import vvide.MarkerManager;
import vvide.signal.AbstractSignal;
import vvide.signal.Marker;
import vvide.signal.VisibleSignal;

/**
 * Action to move the selected marker to the next signal change
 */
public class MoveMarkerNextChangeAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 8425540623417445603L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public MoveMarkerNextChangeAction() {
		super( "To next Change" );
		putValue( SHORT_DESCRIPTION,
				"Move the selected marker to the next signal change" );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
				"/img/actions/marker_next_change.png" ) ) );
		setEnabled( false );

		Application.markerManager.addPropertyChangeListener(
				MarkerManager.MARKER_SELECTED, new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						setEnabled( evt.getNewValue() != null );
					}
				} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		Marker marker = (Marker) Application.markerManager.getSelectedMarker();
		AbstractSignal signal = Application.signalManager.getSelectedSignal();
		if (marker == null) return;

		// jump to any change or to change in selected signal
		long nextChange = -1;
		if (signal != null) {
			nextChange = signal.getLastChangeTime(marker.getPosition());
		} else {
			for (VisibleSignal nextSignal : Application.signalManager.getVisibleSignals()) {
				if (nextChange != -1) {
					nextChange = Math.min(nextChange, nextSignal.getSignal()
							.getLastChangeTime(marker.getPosition()));
				} else {
					nextChange = nextSignal.getSignal().getLastChangeTime(marker.getPosition());
				}
			}
		}
		Application.markerManager.changeMarkerPosition( marker, nextChange );
	}
}
