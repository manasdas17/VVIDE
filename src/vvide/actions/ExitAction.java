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

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import vvide.Application;
import vvide.ui.DialogResult;

/**
 * Close the program
 */
public class ExitAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -5517834302247523133L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public ExitAction() {
		super( "Exit" );
		putValue( SHORT_DESCRIPTION, "Exit from the program" );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		if ( JOptionPane.showConfirmDialog( Application.mainFrame, "Exit "
			+ Application.programName + "?", Application.programName,
				JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION ) {

			// Close the project
			if ( Application.projectManager.getCurrentProject() != null ) {
				CloseProjectAction action =
						(CloseProjectAction) Application.actionManager
								.getAction( "CloseProjectAction" );
				action.actionPerformed( null );
				if ( action.getDialogResult() == DialogResult.CANCEL ) return;
			}

			// Saving the layout and settings
			Application.viewManager.saveLayout();
			Application.settingsManager.saveSettings();

			// Exit
			System.exit( 0 );
		}
	}
}
