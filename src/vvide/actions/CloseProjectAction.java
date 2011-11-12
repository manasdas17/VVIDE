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
import javax.swing.JOptionPane;

import vvide.Application;
import vvide.ProjectManager;
import vvide.ViewManager;
import vvide.ui.DialogResult;
import vvide.ui.views.ConsoleView;
import vvide.utils.CommonMethods;

/**
 * Close the opened project
 */
public class CloseProjectAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -5097442443577734726L;
	/**
	 * Dialog result
	 */
	private DialogResult dialogResult;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for dialogResult
	 * 
	 * @return the dialogResult
	 */
	public DialogResult getDialogResult() {
		return dialogResult;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public CloseProjectAction() {
		super( "Close Project" );
		setEnabled( false );
		putValue( SHORT_DESCRIPTION, "Close the opened project" );

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

		dialogResult = DialogResult.NO;

		// Asking about closing the project
		if ( CommonMethods.confirmSaveProject() == JOptionPane.CANCEL_OPTION ) {
			dialogResult = DialogResult.CANCEL;
			return;
		}

		// Closing all Editors
		if ( Application.viewManager.closeEditors() == JOptionPane.CANCEL_OPTION ) {
			dialogResult = DialogResult.CANCEL;
			return;
		}

		// Clear the console
		((ConsoleView) Application.viewManager
				.getView( ViewManager.CONSOLE_VIEW_ID )).clearContent();

		Application.signalManager.removeAll();
		Application.markerManager.removeAll();

		Application.projectManager.closeProject();
		System.gc();
	}
}
