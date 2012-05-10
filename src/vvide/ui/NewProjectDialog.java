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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

import vvide.Application;
import vvide.actions.ui.SelectFileAction;
import vvide.logger.Logger;
import vvide.simulator.AbstractSimulator;
import vvide.utils.XMLUtils;

import java.awt.Dimension;
import javax.swing.JCheckBox;

/**
 * A Dialog to add new project
 */
public class NewProjectDialog extends JDialog {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 8826546137906916262L;
	/**
	 * Panel with the content
	 */
	private final JPanel contentPanel = new JPanel();
	/**
	 * Textbox with name of the project
	 */
	private JTextField tbxProjectName;
	/**
	 * Textbox with the location
	 */
	private JTextField tbxProjectLocation;
	/**
	 * Result of the dialog
	 */
	private DialogResult result = null;
	/**
	 * Flag to create a folder for project
	 */
	private JCheckBox chbxCreateFolder;
	/**
	 * Combobox with simulators
	 */
	private JComboBox cmbxSimulator;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for project name
	 * 
	 * @param projectName
	 *        a new value for the project name
	 */
	public void setProjectName( String projectName ) {
		this.tbxProjectName.setText( projectName );
	}

	/**
	 * Getter for projectName
	 * 
	 * @return the text it the tbxProjectName
	 */
	public String getProjectName() {
		return tbxProjectName.getText();
	}

	/**
	 * Setter for file location
	 * 
	 * @param projectLocation
	 *        the new value for the project location
	 */
	public void setProjectLocation( String projectLocation ) {
		this.tbxProjectLocation.setText( projectLocation );
	}

	/**
	 * Getter for project location
	 * 
	 * @return the text in the tbxProjectLocation
	 */
	public String getProjectLocation() {
		return tbxProjectLocation.getText();
	}

	/**
	 * Getter for result
	 * 
	 * @return the result
	 */
	public DialogResult getResult() {
		return result;
	}

	/**
	 * Show that the folder for project must be created
	 * 
	 * @return true if the user has select the checkbox
	 */
	public boolean isCreateFolder() {
		return chbxCreateFolder.isSelected();
	}

	/**
	 * Getter for simulator
	 * 
	 * @return the selected simulator
	 */
	public AbstractSimulator getSimulator() {
		return (AbstractSimulator) cmbxSimulator.getSelectedItem();
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Create the dialog.
	 */
	@SuppressWarnings( "unchecked" )
	public NewProjectDialog() {
		// Common settings
		setModalityType( ModalityType.APPLICATION_MODAL );
		setTitle( "Add new project" );
		setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		setBounds( 0, 0, 500, 190 );
		setMinimumSize( new Dimension( 500, 0 ) );
		setLocationRelativeTo( Application.mainFrame );

		// components
		getContentPane().setLayout( new BorderLayout() );
		contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
		getContentPane().add( contentPanel, BorderLayout.CENTER );
		contentPanel
			.setLayout( new MigLayout( "", "[][grow,fill]", "[][][][]" ) );

		// Name of the project
		JLabel lblFileName = new JLabel( "Project Name:" );
		contentPanel.add( lblFileName, "cell 0 0,alignx left" );
		tbxProjectName = new JTextField();
		contentPanel.add( tbxProjectName, "cell 1 0,growx,aligny center" );
		tbxProjectName.setColumns( 10 );
		chbxCreateFolder = new JCheckBox( "Create folder for project" );
		contentPanel.add( chbxCreateFolder, "cell 1 1" );

		JLabel lblSimulator = new JLabel( "Simulator:" );
		contentPanel.add( lblSimulator, "cell 0 2,alignx left" );

		// Load the available simulator
		List<AbstractSimulator> listSomulators =
			(List<AbstractSimulator>) new XMLUtils()
				.loadFromXMLStream( getClass().getResourceAsStream(
					"/res/simulators.xml" ) );
		cmbxSimulator =
			new JComboBox( new DefaultComboBoxModel( listSomulators.toArray() ) );
		contentPanel.add( cmbxSimulator, "cell 1 2,growx" );

		// Project location
		JLabel lblFileLocation = new JLabel( "Location:" );
		contentPanel.add( lblFileLocation, "cell 0 3,alignx left" );
		SelectFileAction action = new SelectFileAction( this, "Select a folder", JFileChooser.DIRECTORIES_ONLY, null, null );
		Method setMethod;
		try {
			setMethod =
				this.getClass().getMethod( "setProjectLocation", String.class );
			action.setSetPathMethod( this, setMethod );
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
		tbxProjectLocation = new JTextField();
		contentPanel.add( tbxProjectLocation,
			"flowx,cell 1 3,growx,aligny center" );
		tbxProjectLocation.setColumns( 10 );
		JButton btnShowOpenFolderDialog = new JButton( action );
		contentPanel.add( btnShowOpenFolderDialog,
			"cell 1 3,alignx center,aligny center" );

		// Ok Cancel buttons
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
		getContentPane().add( buttonPane, BorderLayout.SOUTH );
		JButton okButton = new JButton( "OK" );
		okButton.setActionCommand( "OK" );
		buttonPane.add( okButton );
		getRootPane().setDefaultButton( okButton );
		okButton.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				if ( checkInput() ) {
					result = DialogResult.OK;
					dispose();
				}
			}
		} );
		JButton cancelButton = new JButton( "Cancel" );
		cancelButton.setActionCommand( "Cancel" );
		buttonPane.add( cancelButton );
		cancelButton.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				result = DialogResult.CANCEL;
				dispose();
			}
		} );
		pack();
		setResizable( false );
	}

	/**
	 * Check the correct input
	 * 
	 * @return true, if the location exists and the name of the project is not
	 *         empty
	 */
	private boolean checkInput() {
		File location = new File( getProjectLocation() );
		boolean isCorrect = true;

		// show error messages
		String errorMessage = "";
		if ( getProjectName() == null || getProjectName().trim().length() == 0 ) {
			errorMessage = "Name of the project can't be empty.";
			isCorrect = false;
		}
		if ( !location.isDirectory() ) {
			if ( errorMessage.length() != 0 ) {
				errorMessage += "\n";
			}
			errorMessage += "Path not exists.";
			isCorrect = false;
		}
		if ( !isCorrect ) {
			JOptionPane.showMessageDialog( this, errorMessage,
				Application.programName, JOptionPane.ERROR_MESSAGE );
		}

		return isCorrect;
	}

}
