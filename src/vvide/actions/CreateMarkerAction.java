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
import vvide.ProjectManager;

/**
 * Action to create a new Marker
 */
public class CreateMarkerAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 579805370268655141L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public CreateMarkerAction() {
		super( "New marker" );
		putValue( SHORT_DESCRIPTION, "Create a new marker at position 0" );

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
		if ( Application.projectManager.getCurrentProject() == null ) return;

		Application.markerManager.addMarker( 0 );
	}
}
