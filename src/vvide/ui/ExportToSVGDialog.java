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

import javax.swing.JDialog;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;

import vvide.Application;
import vvide.actions.ui.SelectFileAction;
import vvide.signal.Marker;
import vvide.signal.TimeMetric;
import vvide.signal.VisibleSignal;
import vvide.utils.CommonMethods;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.border.TitledBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Insets;

/**
 * Dialog for exporting the visible signals to the svg file
 */
public class ExportToSVGDialog extends JDialog {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -3087925124065211488L;
	/**
	 * Dialog result
	 */
	private DialogResult dialogResult;
	/**
	 * RadioButtonGroup for From panel
	 */
	private final ButtonGroup groupFrom = new ButtonGroup();
	/**
	 * TextBox with start position
	 */
	private JTextField tbxPositionFrom;
	/**
	 * Combobox with start marker
	 */
	private JComboBox cmbxMarkerFrom;
	/**
	 * RadioButton to select a Marker as "from position"
	 */
	private JRadioButton rdbtnMarkerFrom;
	/**
	 * RadioButton to select a textbox with position as "From position"
	 */
	private JRadioButton rdbtnPositionFrom;
	/**
	 * RadioButtonGroup for To panel
	 */
	private final ButtonGroup groupTo = new ButtonGroup();
	/**
	 * Combobox with stop marker
	 */
	private JComboBox cmbxMarkerTo;
	/**
	 * TextBox with stop position
	 */
	private JTextField tbxPositionTo;
	/**
	 * RadioButton to select a Marker as "to position"
	 */
	private JRadioButton rdbtnMarkerTo;
	/**
	 * RadioButton to select a textbox with position as "To position"
	 */
	private JRadioButton rdbtnPositionTo;
	/**
	 * JList with signals to be exported
	 */
	private JList listExportedSignals;
	/**
	 * JList with available visible signals
	 */
	private JList listAvailableSignals;
	/**
	 * Model for list with available signals
	 */
	private DefaultListModel availableSignalsModel = new DefaultListModel();
	/**
	 * Model for list with exported signals
	 */
	private DefaultListModel exportedSignalsModel = new DefaultListModel();
	/**
	 * Model for combobox with Marker from
	 */
	private DefaultComboBoxModel markerFromModel = new DefaultComboBoxModel(
		Application.markerManager.getMarkers() );
	/**
	 * Model for combobox with Marker to
	 */
	private DefaultComboBoxModel markerToModel = new DefaultComboBoxModel(
		Application.markerManager.getMarkers() );
	/**
	 * TextBox with file name to export
	 */
	private JTextField tbxFileName;

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

	/**
	 * Return the list of signal to export
	 * 
	 * @return array with signal to be export
	 */
	@SuppressWarnings( "unchecked" )
	public Vector<VisibleSignal> getExportedSignals() {
		Vector<VisibleSignal> signals = new Vector<VisibleSignal>();
		Enumeration<VisibleSignal> elements =
			(Enumeration<VisibleSignal>) exportedSignalsModel.elements();
		while ( elements.hasMoreElements() ) {
			signals.add( (VisibleSignal) elements.nextElement() );
		}
		return signals;
	}

	/**
	 * Getter for start position
	 * 
	 * @return the start position to export
	 */
	public long getStartPosition() {
		if ( rdbtnMarkerFrom.isSelected() ) { return ((Marker) cmbxMarkerFrom
			.getSelectedItem()).getPosition(); }
		return TimeMetric.fromString( tbxPositionFrom.getText().trim(),
			Application.signalManager.getScaleUnit() );
	}

	/**
	 * Getter for stop position
	 * 
	 * @return the stop position to export
	 */
	public long getStopPosition() {
		if ( rdbtnMarkerTo.isSelected() ) { return ((Marker) cmbxMarkerTo
			.getSelectedItem()).getPosition(); }
		return TimeMetric.fromString( tbxPositionTo.getText().trim(),
			Application.signalManager.getScaleUnit() );
	}

	/**
	 * Return the selected file name
	 * 
	 * @return name of the file
	 */
	public String getFileName() {
		return tbxFileName.getText();
	}

	/**
	 * Setter for the filename
	 * 
	 * @param name
	 *        the path to the file
	 */
	public void setFileName( String name ) {
		tbxFileName.setText( name );
	}

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Create the dialog.
	 */
	public ExportToSVGDialog() {

		// Fill the model
		for ( VisibleSignal signal : Application.signalManager
			.getVisibleSignals() ) {
			exportedSignalsModel.addElement( signal );
		}

		// Build UI
		setTitle( "Export" );
		setModalityType( ModalityType.APPLICATION_MODAL );
		setModal( true );
		getContentPane().setLayout( new BorderLayout( 0, 0 ) );

		JPanel buttonPanel = new JPanel();
		getContentPane().add( buttonPanel, BorderLayout.SOUTH );
		buttonPanel.setLayout( new MigLayout( "", "[grow,right]", "[25px]" ) );

		JButton btnOk = new JButton( "OK" );
		btnOk.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				if ( canBeExported() ) {
					dialogResult = DialogResult.OK;
					dispose();
				}
			}
		} );
		buttonPanel.add( btnOk, "cell 0 0" );

		JButton btnCancel = new JButton( "Cancel" );
		btnCancel.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				dialogResult = DialogResult.CANCEL;
				dispose();
			}
		} );
		buttonPanel.add( btnCancel, "cell 0 0" );

		JPanel contentPanel = new JPanel();
		getContentPane().add( contentPanel, BorderLayout.CENTER );
		contentPanel.setLayout( new MigLayout( "", "[grow][trailing][grow]",
			"[][][grow][grow]" ) );

		JPanel panel = new JPanel();
		contentPanel.add( panel, "cell 0 0 3 1,grow" );
		panel.setLayout( new MigLayout( "", "[][grow]", "[]" ) );

		JLabel lblFile = new JLabel( "File:" );
		panel.add( lblFile, "cell 0 0,alignx trailing" );

		tbxFileName = new JTextField();
		panel.add( tbxFileName, "flowx,cell 1 0,growx" );
		tbxFileName.setColumns( 10 );

		SelectFileAction action =
			new SelectFileAction( this, "Select a file...",
				JFileChooser.FILES_ONLY, new FileNameExtensionFilter(
					"SVG Files", "svg" ), true, 
					Application.projectManager.getCurrentProject().getProjectLocation() );
		try {
			action.setSetPathMethod( this, getClass().getMethod( "setFileName",
				String.class ) );
		}
		catch ( Exception e ) {}

		JButton btnSelectFile = new JButton( action );
		panel.add( btnSelectFile, "cell 1 0" );

		JPanel panelFrom = new JPanel();
		panelFrom.setBorder( new TitledBorder( null, "From:",
			TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
		contentPanel.add( panelFrom, "cell 0 1,grow" );
		panelFrom.setLayout( new MigLayout( "", "[][grow]", "[][]" ) );

		rdbtnMarkerFrom = new JRadioButton( "Marker" );
		rdbtnMarkerFrom.setSelected( true );
		groupFrom.add( rdbtnMarkerFrom );
		panelFrom.add( rdbtnMarkerFrom, "cell 0 0,alignx left,aligny top" );

		cmbxMarkerFrom = new JComboBox( markerFromModel );
		panelFrom.add( cmbxMarkerFrom, "cell 1 0,growx" );
		rdbtnMarkerFrom.addItemListener( new EnableBinder( rdbtnMarkerFrom,
			cmbxMarkerFrom ) );

		rdbtnPositionFrom = new JRadioButton( "Position" );
		groupFrom.add( rdbtnPositionFrom );
		panelFrom.add( rdbtnPositionFrom, "cell 0 1" );

		tbxPositionFrom = new JTextField();
		panelFrom.add( tbxPositionFrom, "cell 1 1,growx" );
		tbxPositionFrom.setColumns( 10 );
		rdbtnPositionFrom.addItemListener( new EnableBinder( rdbtnPositionFrom,
			tbxPositionFrom ) );

		JPanel panelTo = new JPanel();
		panelTo.setBorder( new TitledBorder( null, "To:", TitledBorder.LEADING,
			TitledBorder.TOP, null, null ) );
		contentPanel.add( panelTo, "cell 2 1,grow" );
		panelTo.setLayout( new MigLayout( "", "[][grow]", "[][]" ) );

		rdbtnMarkerTo = new JRadioButton( "Marker" );
		rdbtnMarkerTo.setSelected( true );
		groupTo.add( rdbtnMarkerTo );
		panelTo.add( rdbtnMarkerTo, "cell 0 0" );

		cmbxMarkerTo = new JComboBox( markerToModel );
		panelTo.add( cmbxMarkerTo, "cell 1 0,growx" );
		rdbtnMarkerTo.addItemListener( new EnableBinder( rdbtnMarkerTo,
			cmbxMarkerTo ) );

		rdbtnPositionTo = new JRadioButton( "Position" );
		groupTo.add( rdbtnPositionTo );
		panelTo.add( rdbtnPositionTo, "cell 0 1" );

		tbxPositionTo = new JTextField();
		panelTo.add( tbxPositionTo, "cell 1 1,growx" );
		tbxPositionTo.setColumns( 10 );
		rdbtnPositionTo.addItemListener( new EnableBinder( rdbtnPositionTo,
			tbxPositionTo ) );

		JPanel panelAvaiableSignals = new JPanel();
		panelAvaiableSignals.setBorder( new TitledBorder( new LineBorder(
			new Color( 184, 207, 229 ) ), "Available signals:",
			TitledBorder.LEADING, TitledBorder.TOP, null,
			new Color( 51, 51, 51 ) ) );
		contentPanel.add( panelAvaiableSignals, "cell 0 2 1 2,grow" );
		panelAvaiableSignals.setLayout( new BorderLayout( 0, 0 ) );

		JScrollPane scrollPane = new JScrollPane();
		panelAvaiableSignals.add( scrollPane, BorderLayout.CENTER );

		listAvailableSignals = new JList( availableSignalsModel );
		listAvailableSignals
			.setCellRenderer( new VisibleSignalListCellRenderer() );
		scrollPane.setViewportView( listAvailableSignals );

		JButton btnRemoveSignal = new JButton( "" );
		btnRemoveSignal.setMargin( new Insets( 2, 2, 2, 2 ) );
		btnRemoveSignal.setIcon( new ImageIcon( ExportToSVGDialog.class
			.getResource( "/img/left_arrow.png" ) ) );
		btnRemoveSignal.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				Object selected = listAvailableSignals.getSelectedValue();
				if ( selected != null ) {
					availableSignalsModel.removeElement( selected );
					exportedSignalsModel.addElement( selected );
				}
			}
		} );
		contentPanel.add( btnRemoveSignal,
			"cell 1 2,alignx center,aligny bottom" );

		JPanel panelExportedSignals = new JPanel();
		panelExportedSignals.setBorder( new TitledBorder( null,
			"Exported signals:", TitledBorder.LEADING, TitledBorder.TOP, null,
			null ) );
		contentPanel.add( panelExportedSignals, "cell 2 2 1 2,grow" );
		panelExportedSignals.setLayout( new BorderLayout( 0, 0 ) );

		JScrollPane scrollPane_1 = new JScrollPane();
		panelExportedSignals.add( scrollPane_1 );

		listExportedSignals = new JList( exportedSignalsModel );
		listExportedSignals
			.setCellRenderer( new VisibleSignalListCellRenderer() );
		scrollPane_1.setViewportView( listExportedSignals );

		JButton btnAddSignal = new JButton( "" );
		btnAddSignal.setMargin( new Insets( 2, 2, 2, 2 ) );
		btnAddSignal.setIcon( new ImageIcon( ExportToSVGDialog.class
			.getResource( "/img/right_arrow.png" ) ) );
		btnAddSignal.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				Object selected = listExportedSignals.getSelectedValue();
				if ( selected != null ) {
					exportedSignalsModel.removeElement( selected );
					availableSignalsModel.addElement( selected );
				}
			}
		} );
		contentPanel.add( btnAddSignal, "cell 1 3,aligny top" );

		pack();
		CommonMethods.addEscapeListener( this, btnCancel );
		setLocationRelativeTo( Application.mainFrame );
		setResizable( false );
	}

	/**
	 * Check, if all input values are correct. If not - show an error message
	 * 
	 * @return true, if the signals can be exported
	 */
	private boolean canBeExported() {
		// Check the from position
		StringBuilder errorText = new StringBuilder();
		boolean hasErrors = false;

		if ( rdbtnMarkerFrom.isSelected()
			&& cmbxMarkerFrom.getSelectedItem() == null ) {
			hasErrors = true;
			errorText.append( "No Marker selected in from-section" ).append(
				"\n" );
		}

		if ( rdbtnPositionFrom.isSelected()
			&& TimeMetric.fromString( tbxPositionFrom.getText(),
				Application.signalManager.getScaleUnit() ) < 0 ) {
			hasErrors = true;
			errorText.append( "Error parsing a start position" ).append( "\n" );
		}

		if ( rdbtnMarkerTo.isSelected()
			&& cmbxMarkerTo.getSelectedItem() == null ) {
			hasErrors = true;
			errorText.append( "No Marker selected in to-section" )
				.append( "\n" );
		}

		if ( rdbtnPositionTo.isSelected()
			&& TimeMetric.fromString( tbxPositionTo.getText(),
				Application.signalManager.getScaleUnit() ) < 0 ) {
			hasErrors = true;
			errorText.append( "Error parsing a stop position" ).append( "\n" );
		}
		
		if (listExportedSignals.getModel().getSize() == 0) {
			hasErrors = true;
			errorText.append( "No signals to export" ).append( "\n" );
		}
		
		if (tbxFileName.getText() == null || tbxFileName.getText().trim().length() == 0) {
			hasErrors = true;
			errorText.append( "Export file is not specified" ).append( "\n" );
		}
		
		if (hasErrors) {
			JOptionPane.showMessageDialog( this, errorText.toString().trim(), "Found errors", JOptionPane.ERROR_MESSAGE );
		}

		return !hasErrors;
	}

	/*
	 * ========================= Internal Classes =============================
	 */
	/**
	 * A Cell rederer for Visible signal in the Export To SVG Dialog
	 */
	@SuppressWarnings( "serial" )
	private class VisibleSignalListCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent( JList list,
			Object value, int index, boolean isSelected, boolean cellHasFocus ) {
			super.getListCellRendererComponent( list, value, index, isSelected,
				cellHasFocus );
			setText( ((VisibleSignal) value).getSignal().getFullPath() );
			return this;
		}
	}

	/**
	 * A listener to bind the selected property of the JRadioButton and the
	 * Enable Property of some other Component
	 */
	private class EnableBinder implements ItemListener {

		/**
		 * Source component
		 */
		private final JRadioButton source;
		/**
		 * Destination Component
		 */
		private final JComponent dest;

		/**
		 * Constructor
		 */
		public EnableBinder( JRadioButton source, JComponent dest ) {
			this.source = source;
			this.dest = dest;
			itemStateChanged( null );
		}

		@Override
		public void itemStateChanged( ItemEvent e ) {
			dest.setEnabled( source.isSelected() );
		}
	}
}
