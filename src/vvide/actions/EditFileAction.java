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
import javax.swing.ImageIcon;

import vvide.Application;
import vvide.ViewManager;
import vvide.project.AbstractFile;
import vvide.ui.views.ProjectView;

/**
 * Show Editor for the file
 */
public class EditFileAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -4879466859429488472L;
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
	public EditFileAction() {
		super( "Edit file" );
		setEnabled( false );
		putValue( SHORT_DESCRIPTION, "Open editor for the selected files" );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
				"/img/actions/file_edit.png" ) ) );

		// Listener for Enable/Disable Action
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
		// getting list of the selected files
		if ( !projectView.isDisplayable() ) return;
		Application.viewManager.openEditorView( projectView.getSelectedFiles()
				.toArray( new AbstractFile[] {} ) );
	}

	/*
	 * ========================= Internal Classes =============================
	 */
	/**
	 * Listener to enable/disable action
	 */
	private class EnableSetter implements PropertyChangeListener {

		@Override
		@SuppressWarnings( "unchecked" )
		public void propertyChange( PropertyChangeEvent evt ) {
			Vector<AbstractFile> files =
					(Vector<AbstractFile>) evt.getNewValue();
			setEnabled( files != null && files.size() > 0 );
		}
	}
}
