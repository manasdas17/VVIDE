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

/**
 * Remove a selected signal from the list of the visible signals
 */
public class RemoveSignalAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 3999900092324805563L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public RemoveSignalAction() {
		super( "Remove signal" );
		putValue( SHORT_DESCRIPTION, "Removes the selected signal" );

		setEnabled( false );

		// Adding a listener to the current selected marker
		Application.signalManager.addPropertyChangeListener(
				SignalManager.SELECTED_SIGNAL_INDEX,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent e ) {
						setEnabled( (Integer) e.getNewValue() >= 0 );
					}
				} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {

		int selectedIndex = Application.signalManager.getSelectedSignalIndex();
		if ( selectedIndex < 0 ) return;

		Application.signalManager.removeFromVisible( selectedIndex );

	}
}
