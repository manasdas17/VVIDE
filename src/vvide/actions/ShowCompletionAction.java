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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import vvide.Application;
import vvide.ProjectManager;
import vvide.utils.CommonMethods;

/**
 * An Action to show possible completitions form RTextArea
 */
public class ShowCompletionAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 1922335111758911674L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public ShowCompletionAction() {
		super( "Content Assist" );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_SPACE,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
		setEnabled( false );

		Application.projectManager.addPropertyChangeListener(
				ProjectManager.CURRENT_FILE, new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						setEnabled( evt.getNewValue() != null );
					}
				} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {

		if ( !isEnabled() ) return;
		RSyntaxTextArea codeEditor = CommonMethods.getCurrentEditor();
		codeEditor.getActionMap().get( "AutoComplete" ).actionPerformed( null );
	}
}
