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
import java.util.Arrays;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;

import vvide.Application;
import vvide.actions.ui.SelectFileAction;
import vvide.logger.Logger;

import java.awt.Dimension;

/**
 * A Dialog to add new files to the project
 */
public class NewFileDialog extends JDialog {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 8826537995606916262L;
	/**
	 * Panel with the content
	 */
	private final JPanel contentPanel = new JPanel();
	/**
	 * Textbox with name of the file
	 */
	private JTextField tbxFileName;
	/**
	 * Textbox with the location
	 */
	private JTextField tbxFileLocation;
	/**
	 * Combobox with a type of the file
	 */
	private JComboBox cmbxFileType;
	/**
	 * Result of the dialog
	 */
	private DialogResult result = null;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for file name
	 * 
	 * @param fileName
	 *        a new value for the file name
	 */
	public void setFileName( String fileName ) {
		this.tbxFileName.setText( fileName );
	}

	/**
	 * Getter for fileName
	 * 
	 * @return the text it the tbxFileName
	 */
	public String getFileName() {
		return tbxFileName.getText();
	}

	/**
	 * Setter for file location
	 * 
	 * @param fileLocation
	 *        the new value for the file location
	 */
	public void setFileLocation( String fileLocation ) {
		this.tbxFileLocation.setText( fileLocation );
	}

	/**
	 * Getter for file location
	 * 
	 * @return the text in the tbxFileLocation
	 */
	public String getFileLocation() {
		return tbxFileLocation.getText();
	}
	
	/**
	 * Setter for file type
	 * 
	 * @param fileType
	 *        the type of the file
	 */
	public void setFileType( String fileType ) {
		ComboBoxModel model = cmbxFileType.getModel(); 
		for (int i = 0; i < model.getSize(); ++i) {
			if (model.getElementAt( i ).equals( fileType )) {
				cmbxFileType.setSelectedIndex( i );
				break;
			}
		}
	}

	/**
	 * Getter for file type
	 * 
	 * @return the text in the cmbxFileType
	 */
	public String getFileType() {
		return (String) cmbxFileType.getSelectedItem();
	}
	/**
	 * Getter for result
	 * 
	 * @return the result
	 */
	public DialogResult getResult() {
		return result;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Create the dialog.
	 */
	public NewFileDialog() {
		// Common settings
		setModalityType( ModalityType.APPLICATION_MODAL );
		setTitle( "Add new file" );
		setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		setBounds( 0, 0, 500, 150 );
		setMinimumSize( new Dimension( 500, 0 ) );
		setLocationRelativeTo( Application.mainFrame );

		// components
		getContentPane().setLayout( new BorderLayout() );
		contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
		getContentPane().add( contentPanel, BorderLayout.CENTER );
		contentPanel.setLayout( new MigLayout( "", "[][grow,fill]", "[][][]" ) );

		// Name of the file
		{
			JLabel lblFileName = new JLabel( "File Name:" );
			contentPanel.add( lblFileName, "cell 0 0,alignx trailing" );
		}
		{
			tbxFileName = new JTextField();
			contentPanel.add( tbxFileName, "cell 1 0,growx,aligny center" );
			tbxFileName.setColumns( 10 );
		}

		// File location
		{
			JLabel lblFileLocation = new JLabel( "Location:" );
			contentPanel.add( lblFileLocation, "cell 0 1,alignx trailing" );
		}
		{
			tbxFileLocation = new JTextField();
			contentPanel.add( tbxFileLocation,
					"flowx,cell 1 1,growx,aligny center" );
			tbxFileLocation.setColumns( 10 );
		}
		{
			SelectFileAction action = new SelectFileAction( this, "Select a folder", 
					JFileChooser.DIRECTORIES_ONLY, null, Application.projectManager.getCurrentProject().getProjectLocation() );
			Method setMethod;
			try {
				setMethod =
						this.getClass().getMethod( "setFileLocation",
								String.class );
				action.setSetPathMethod( this, setMethod );
			}
			catch ( Exception e ) {
				Logger.logError( this, e );
			}
			JButton btnShowOpenFolderDialog = new JButton( action );
			contentPanel.add( btnShowOpenFolderDialog,
					"cell 1 1,alignx center,aligny center" );
		}

		// Type of the file
		{
			JLabel lblType = new JLabel( "Type:" );
			contentPanel.add( lblType, "cell 0 2,alignx trailing" );
		}
		{
			String[] fileTypes =
					(String[]) Application.projectManager.getSupportedFiles().toArray(new String[] {});
			Arrays.sort( fileTypes );
			DefaultComboBoxModel model = new DefaultComboBoxModel(fileTypes);
			cmbxFileType = new JComboBox( model );
			contentPanel.add( cmbxFileType, "cell 1 2,growx,aligny center" );
		}

		// Ok Cancel buttons
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
			getContentPane().add( buttonPane, BorderLayout.SOUTH );
			{
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
			}
			{
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
			}
		}
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
		File location = new File( getFileLocation() );
		boolean isCorrect = true;

		// show error messages
		String errorMessage = "";
		if ( getFileName() == null || getFileName().trim().length() == 0 ) {
			errorMessage = "Name of the file can't be empty.";
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
