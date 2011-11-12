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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import javax.swing.JLabel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

import vvide.Application;
import vvide.simulator.AbstractSimulator;
import vvide.utils.XMLUtils;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComboBox;

/**
 * A Dialog to edit a project properties
 */
/**
 * @author Pavel Fischer
 *
 */
public class ProjectPropertiesDialog extends JDialog {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -7826065675445956257L;
	/**
	 * Textbox with name of the project
	 */
	private JTextField tbxProjectName;
	/**
	 * Result of the dialog
	 */
	private DialogResult result = null;
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
	 * Setter for simulator
	 * 
	 * @param simulator
	 *        a simulator to set
	 */
	public void setSimulator( AbstractSimulator simulator ) {
		for (int i = 0; i<cmbxSimulator.getItemCount(); i++) {
			if (cmbxSimulator.getItemAt( i ).getClass() == simulator.getClass()) {
				cmbxSimulator.setSelectedIndex( i );
				break;
			}
		}
	}

	/**
	 * Getter for simulator
	 * @return the selected simulator
	 */
	public AbstractSimulator getSimulator() {
		return (AbstractSimulator) cmbxSimulator.getSelectedItem();
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
	@SuppressWarnings( "unchecked" )
	public ProjectPropertiesDialog() {
		// Common settings
		setModalityType( ModalityType.APPLICATION_MODAL );
		setTitle( "Properties" );
		setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		setBounds( 0, 0, 500, 190 );
		setMinimumSize( new Dimension( 500, 0 ) );
		setLocationRelativeTo( Application.mainFrame );

		JPanel contentPanel = new JPanel();
		getContentPane().add( contentPanel, BorderLayout.NORTH );
		contentPanel.setLayout( new MigLayout( "", "[][grow]", "[][]" ) );

		JLabel lblProjectName = new JLabel( "Project Name:" );
		contentPanel.add( lblProjectName, "cell 0 0,alignx left" );

		tbxProjectName = new JTextField();
		contentPanel.add( tbxProjectName, "cell 1 0,growx" );

		JLabel lblSimulator = new JLabel( "Simulator:" );
		contentPanel.add( lblSimulator, "cell 0 1,alignx left" );

		// Load the available simulator
		List<AbstractSimulator> listSomulators =
			(List<AbstractSimulator>) new XMLUtils()
				.loadFromXMLStream( getClass().getResourceAsStream(
					"/res/simulators.xml" ) );
		cmbxSimulator =
			new JComboBox( new DefaultComboBoxModel( listSomulators.toArray() ) );
		contentPanel.add( cmbxSimulator, "cell 1 1,growx" );

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
				result = DialogResult.OK;
				dispose();
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
}
