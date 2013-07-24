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
package vvide.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import net.infonode.docking.RootWindow;
import net.infonode.docking.theme.BlueHighlightDockingTheme;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.util.DockingUtil;

import vvide.Application;
import vvide.utils.CommonMethods;

/**
 * A Class for the main frame of the studio
 */
public class MainFrame extends JFrame {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 7803477487599575869L;
	/**
	 * Root Docking View
	 */
	private RootWindow rootWindow;
	/**
	 * Simulation Time TextBox
	 */
	private JTextField tbxSimulationTime = null;
	/**
	 * Panel With content
	 */
	private JPanel contentPanel;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for rootWindow
	 * 
	 * @return the rootWindow
	 */
	public RootWindow getRootWindow() {
		return rootWindow;
	}

	/**
	 * Getter for simulation time
	 * 
	 * @return String with a simulation time
	 */
	public String getSimulationTime() {
		return (tbxSimulationTime == null) ? null : tbxSimulationTime.getText()
				.trim().replaceAll( " ", "" );
	}

	/**
	 * Setter for simulation time
	 * 
	 * @param time
	 *        String with a simulation time
	 */
	public void setSimulationTime( String time ) {
		if ( tbxSimulationTime != null ) tbxSimulationTime.setText( time );
	}

	/*
	 * ============================ Methods ==================================
	 */
	public MainFrame() {
		rootWindow =
				DockingUtil.createRootWindow( Application.viewManager
						.getViewMap(), false );
		rootWindow.getRootWindowProperties().setRecursiveTabsEnabled( false );

		DockingWindowsTheme theme = new BlueHighlightDockingTheme();
		// Apply theme
		rootWindow.getRootWindowProperties().addSuperObject(
				theme.getRootWindowProperties() );

		contentPanel = new JPanel( new BorderLayout() );
		contentPanel.add( rootWindow, BorderLayout.CENTER );

		getContentPane().add( contentPanel );

		// set a size of the frame
		setSize( 800, 600 );
		setLocationRelativeTo( null );
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setTitle( Application.programName );

		// Add closing listener
		addWindowListener( new WindowAdapter() {

			@Override
			public void windowClosing( WindowEvent e ) {
				Application.actionManager.getAction( "ExitAction" )
						.actionPerformed( null );
			}

		} );
	}

	/**
	 * Init the frame
	 */
	public void initFrame(String mainMenu, String mainToolbar) {
		// Build the main menu
		JMenuBar menuBar = CommonMethods.getMenubar( mainMenu );
		setJMenuBar( menuBar );

		// Get a toolbar
		if (mainToolbar == null) return;
		
		JToolBar toolbar = CommonMethods.getToolbar( mainToolbar );
		// Getting a textBox
		for ( Component comp : toolbar.getComponents() ) {
			if ( comp instanceof JTextField ) {
				tbxSimulationTime = (JTextField) comp;
				break;
			}
		}

		contentPanel.add( toolbar, BorderLayout.NORTH );
	}
}
