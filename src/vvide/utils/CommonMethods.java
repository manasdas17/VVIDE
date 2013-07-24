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
package vvide.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import vvide.Application;
import vvide.simulator.AbstractSimulator;
import vvide.ui.views.ConsoleView;

/**
 * A class with common methods
 */
public class CommonMethods {

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Ask user about saving a project and return a user selection
	 * 
	 * @return <code>JOptionPane.YES_OPTION</code> or
	 *         <code>JOptionPane.NO_OPTION</code> or
	 *         <code>JOptionPane.CANCEL_OPTION</code>
	 */
	public static int confirmSaveProject() {
		int res = JOptionPane.NO_OPTION;
		if ( Application.projectManager.getCurrentProject() != null
			&& Application.projectManager.getCurrentProject().isModified() ) {
			res =
				JOptionPane.showConfirmDialog( Application.mainFrame,
					"SaveChanges in the Project?", Application.programName,
					JOptionPane.YES_NO_CANCEL_OPTION );
			// Proceed the user action
			if ( res == JOptionPane.YES_OPTION )
				Application.actionManager.getAction( "SaveProjectAction" )
					.actionPerformed( null );
		}
		return res;
	}

	/**
	 * Append the console with a simulator output
	 * 
	 * @param simulator
	 *        a simulator to get the output
	 */
	public static void printInConsole( AbstractSimulator simulator ) {
		// Fill the console
		ConsoleView console =
			(ConsoleView) Application.viewManager
				.getView( ConsoleView.CONSOLE_VIEW_ID );
		if ( simulator.getOutputData() != null )
			console.appendNormalText( simulator.getOutputData() );
		if ( simulator.getErrorData() != null )
			console.appendErrorText( simulator.getErrorData() );
	}

	/**
	 * Return a specified JToolBar
	 * 
	 * @param toolbarName
	 *        name of the toolbar
	 * @return a ToolBar
	 */
	public static JToolBar getToolbar( String toolbarName ) {
		XMLUtils xmlUtils = new XMLUtils();
		return (JToolBar) xmlUtils.loadFromXMLStream( CommonMethods.class
			.getResourceAsStream( "/res/toolbars/" + toolbarName + ".xml" ) );
	}

	/**
	 * Return a specified JMenuBar
	 * 
	 * @param menubarName
	 *        name of the menubar
	 * @return a JMenuBar
	 */
	public static JMenuBar getMenubar( String menubarName ) {
		XMLUtils xmlUtils = new XMLUtils();
		return (JMenuBar) xmlUtils.loadFromXMLStream( CommonMethods.class
			.getResourceAsStream( "/res/menues/" + menubarName + ".xml" ) );
	}

	/**
	 * Return a specified JToolBar
	 * 
	 * @param toolbarName
	 *        name of the toolbar
	 * @return a ToolBar
	 */
	public static JPopupMenu getPopUpMenu( String menuName ) {
		XMLUtils xmlUtils = new XMLUtils();
		return (JPopupMenu) xmlUtils.loadFromXMLStream( CommonMethods.class
			.getResourceAsStream( "/res/menues/" + menuName + ".xml" ) );
	}

	/**
	 * Get a simulation time
	 * 
	 * @return return a simulation time
	 */
	public static String getSimulationTime() {
		String simulationTime = Application.mainFrame.getSimulationTime();
		if ( simulationTime == null || simulationTime.length() == 0 ) {
			simulationTime =
				JOptionPane.showInputDialog( Application.mainFrame,
					"Specify the simulation stop time:" );
		}
		return simulationTime;
	}

	/**
	 * Check if the popup menu must be show and show it
	 * 
	 * @param e
	 *        mouse event
	 * @param menu
	 *        a menu to show
	 * @param parent
	 *        a component for menu
	 */
	public static void maybeShowPopup( MouseEvent e, JPopupMenu menu,
		JComponent parent ) {
		if ( e.isPopupTrigger() ) {
			menu.show( parent, e.getX(), e.getY() );
		}
	}

	/**
	 * Convert an string ID of the signal to the int value
	 * 
	 * @param stringId
	 *        a string with signal id
	 * @return integer >= 0 if conversion was correct, -1 otherwise
	 */
	public static int getIDFormString( String stringId ) {
		stringId = stringId.trim();
		if ( stringId.length() == 0 ) return -1;
		int value = 0;
		for ( int i = 0; i < stringId.length(); i++ ) {
			int digit = (int) stringId.charAt( i ) - 32;
			value = value + digit * (int) Math.pow( 94, i );
		}
		return value;
	}

	/**
	 * Send a keys with robot
	 * 
	 * @param component
	 *        component to send event
	 * @param modifiers
	 *        e.g. Ctrl/Shift
	 * @param key
	 *        key code to send
	 */
	public static void sendKeys( JComponent component, int modifiers, int key ) {
		KeyEvent event =
			new KeyEvent( component, KeyEvent.KEY_PRESSED, 0, modifiers, key,
				KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD );
		component.requestFocusInWindow();
		component.dispatchEvent( event );
	}

	/**
	 * Return the current useble editor
	 * 
	 * @return
	 */
	public static RSyntaxTextArea getCurrentEditor() {
		return Application.viewManager.getEditorFor(
			Application.projectManager.getCurrentFile() ).getCodeEditor();
	}

	/**
	 * Add an Listener for an esc key and make a click on the specified button
	 * on Esc
	 * 
	 * @param frame
	 *        frame to add listener
	 * @param cancelButton
	 *        button to make a click
	 */
	public static void addEscapeListener( JDialog frame, JButton cancelButton ) {
		KeyStroke stroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 );
		frame.getRootPane().registerKeyboardAction(
			new EscapeListener( cancelButton ), stroke,
			JComponent.WHEN_IN_FOCUSED_WINDOW );
	}

	/**
	 * Show an error message
	 * 
	 * @param message
	 *        text to show
	 */
	public static void showErrorMessage( String message ) {
		JOptionPane.showMessageDialog( Application.mainFrame, message, "Error",
			JOptionPane.ERROR_MESSAGE );
	}

	/*
	 * ======================== Internal Classes ==============================
	 */
	private static class EscapeListener implements ActionListener {

		/**
		 * Button to make a click
		 */
		private final JButton cancelButton;

		/**
		 * Constructor
		 * 
		 * @param cancelButton
		 *        button to make a click
		 */
		public EscapeListener( JButton cancelButton ) {
			this.cancelButton = cancelButton;
		}

		@Override
		public void actionPerformed( ActionEvent e ) {
			cancelButton.doClick();
		}

	}
}
