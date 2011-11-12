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

import org.fife.ui.rtextarea.RecordableTextAction;

import vvide.Application;
import vvide.ProjectManager;
import vvide.utils.CommonMethods;

/**
 * An Action to Import Actions form RTextArea
 */
@SuppressWarnings( "serial" )
public abstract class AbstractTextAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Editor
	 */
	private RecordableTextAction action = null;
	/**
	 * Listener to enable disable action
	 */
	private EnableListener enableListener = new EnableListener();

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public AbstractTextAction( RecordableTextAction textAction,
			String actionName ) {
		super(actionName);
		this.action = textAction;
		putValue( ACCELERATOR_KEY, action.getAccelerator() );
		putValue( SHORT_DESCRIPTION, action.getDescription() );
		setEnabled( false );

		Application.projectManager.addPropertyChangeListener(
				ProjectManager.CURRENT_FILE, new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						if ( evt.getNewValue() == null ) {
							setEnabled( false );
							action.removePropertyChangeListener( enableListener );
							return;
						}
						setEnabled( action.isEnabled() );
						action.addPropertyChangeListener( enableListener );
					}
				} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {

		if ( action == null || !action.isEnabled() ) return;
		action.actionPerformedImpl( null, CommonMethods.getCurrentEditor() );
	}

	/*
	 * ======================= Internal Classes ===============================
	 */
	/**
	 * A listener for selected signals
	 */
	private class EnableListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			if ( evt.getPropertyName().equals( "enabled" ) ) {
				setEnabled( (Boolean) evt.getNewValue() );
			}
		}
	}
}
