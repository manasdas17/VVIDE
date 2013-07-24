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
import vvide.project.AbstractFile;
import vvide.project.AbstractSourceFile;
import vvide.project.Project;
import vvide.ui.views.ProjectView;

/**
 * Action to set a file as TopVelel Entity
 */
public class SetAsTopLevelEntityAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -9074508488920002634L;
	/**
	 * Listener to enable the action
	 */
	private EnableSetter enabler = new EnableSetter();
	/**
	 * Project View
	 */
	private ProjectView projectView = (ProjectView) Application.viewManager
		.getView( ProjectView.PROJECT_VIEW_ID );

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public SetAsTopLevelEntityAction() {
		super( "Set as Top Level" );
		putValue( SHORT_DESCRIPTION,
			"Mark the selected source file as file with top level entity" );
		setEnabled( false );

		// Adding a listener to enable the action
		projectView.addPropertyChangeListener( ProjectView.PROPERTY_VISIBLE,
			new PropertyChangeListener() {

				@Override
				public void propertyChange( PropertyChangeEvent evt ) {
					if ( (Boolean) evt.getNewValue() ) {
						projectView.addPropertyChangeListener(
							ProjectView.SELECTED_FILES, enabler );
					}
					else {
						projectView.removePropertyChangeListener(
							ProjectView.SELECTED_FILES, enabler );
					}
				}
			} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		Project currentProject = Application.projectManager.getCurrentProject();
		if ( currentProject == null ) return;

		currentProject.setTopLevelEntityFileName( projectView
			.getSelectedFiles().get( 0 ).getFileName() );
	}

	/*
	 * ========================= Internal Classes =============================
	 */
	/**
	 * Listener to enable/disable action
	 */
	private class EnableSetter implements PropertyChangeListener {

		@SuppressWarnings( "unchecked" )
		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			Vector<AbstractFile> files =
				(Vector<AbstractFile>) evt.getNewValue();
			setEnabled( files != null && files.size() == 1
				&& files.get( 0 ) instanceof AbstractSourceFile );
		}
	}
}
