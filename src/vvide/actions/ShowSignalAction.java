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
import java.util.Vector;

import javax.swing.AbstractAction;

import vvide.Application;
import vvide.ViewManager;
import vvide.signal.AbstractSignal;
import vvide.ui.views.ProjectView;
import vvide.ui.views.SignalTreeView;

/**
 * Add the signal to the list of visible signals
 */
public class ShowSignalAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 7340438992255779251L;
	/**
	 * Instance of the signal Tree view
	 */
	private SignalTreeView view = (SignalTreeView) Application.viewManager
			.getView( ViewManager.SIGNAL_TREE_VIEW_ID );
	/**
	 * Listener for selected signals
	 */
	private SelectedSignalsListener selectedSignalsListener =
			new SelectedSignalsListener();

	/*
	 * ============================ Methods ==================================
	 */
	public ShowSignalAction() {
		super( "Show Signal" );
		putValue( SHORT_DESCRIPTION,
				"Add selected Signal to the list of visible signals" );

		setEnabled( false );

		// Listener for Enable/Disable Action
		view.addPropertyChangeListener( ProjectView.PROPERTY_VISIBLE,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						setEnabled( (Boolean) evt.getNewValue() );
						if ( (Boolean) evt.getNewValue() ) {

							view.addPropertyChangeListener(
									ProjectView.SELECTED_FILES,
									selectedSignalsListener );
						}
						else {
							view.removePropertyChangeListener(
									ProjectView.SELECTED_FILES,
									selectedSignalsListener );
						}
					}
				} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {

		// A signal to add
		Vector<AbstractSignal> signals =
				(Vector<AbstractSignal>) ((SignalTreeView) Application.viewManager
						.getView( ViewManager.SIGNAL_TREE_VIEW_ID ))
						.getSelectedSignals();

		if ( (signals != null) && (signals.size() > 0) ) {
			Application.signalManager.addToVisible( signals );
		}

	}

	/*
	 * ======================= Internal Classes ===============================
	 */
	/**
	 * A listener for selected signals
	 */
	private class SelectedSignalsListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			setEnabled( view.isDisplayable()
				&& view.getSelectedSignals() != null
				&& view.getSelectedSignals().size() > 0 );
		}
	}
}
