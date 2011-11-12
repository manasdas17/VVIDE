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
import vvide.SignalManager;

/**
 * Action to move selected signal down
 */
public class MoveSignalDownAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -1250876409340241718L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public MoveSignalDownAction() {
		super( "Move signal down" );
		putValue( SHORT_DESCRIPTION, "Move the selected signal down" );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
				"/img/actions/signal_move_down.png" ) ) );
		setEnabled( false );

		Application.signalManager.addPropertyChangeListener(
				SignalManager.SELECTED_SIGNAL_INDEX,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						setEnabled( (Integer) evt.getNewValue() < Application.signalManager
								.getCountVisibleSignals() - 1 );
					}
				} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		int pos = Application.signalManager.getSelectedSignalIndex();
		if ( pos < Application.signalManager.getCountVisibleSignals() - 1 ) {
			Application.signalManager.setSignalPosition( pos, pos + 1, true );
		}
	}
}
