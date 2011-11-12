/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This file based on the code of WaveForm Viewer project.
 * 
 * Copyright (C) 2010-2011 Department of Digital Technology
 * of the University of Kassel, Germany
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

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;

import vvide.Application;
import vvide.utils.CommonMethods;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A dialog to choose a font
 */
public class FontDialog extends JDialog {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -4105530377878409910L;
	/**
	 * Flag to make a font bold
	 */
	private JCheckBox chckbxBold;
	/**
	 * Flag to make a font italic
	 */
	private JCheckBox chckbxItalic;
	/**
	 * List to get a font size
	 */
	private JList listFontSize;
	/**
	 * list to get font family
	 */
	private JList listFontFamily;
	/**
	 * cancel button
	 */
	private JButton cancelButton;
	/**
	 * Dialog result
	 */
	private DialogResult dialogResult;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Set the font
	 */
	public void setFontToEdit( Font font ) {
		if ( font == null ) {
			listFontFamily.setSelectedIndex( -1 );
			listFontSize.setSelectedIndex( -1 );
			chckbxBold.setSelected( false );
			chckbxItalic.setSelected( false );
		}
		else {
			listFontFamily.setSelectedValue( font.getFamily(), true );
			listFontSize.setSelectedValue( String.valueOf( font.getSize() ), true );
			chckbxBold.setSelected( font.isBold() );
			chckbxItalic.setSelected( font.isItalic() );
		}
	}

	/**
	 * Get the font
	 */
	public Font getFontToEdit() {
		String family =
				(String) ((listFontFamily.getSelectedIndex() >= 0)
						? listFontFamily.getSelectedValue() : Font.DIALOG);
		int size =
				(listFontSize.getSelectedIndex() >= 0) ? Integer
						.valueOf( (String) listFontSize.getSelectedValue() )
						: 10;
		int style = Font.PLAIN;
		if ( chckbxBold.isSelected() ) style += Font.BOLD;
		if ( chckbxItalic.isSelected() ) style += Font.ITALIC;

		return new Font( family, style, size );
	}

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
	 * Create the dialog.
	 */
	@SuppressWarnings( "serial" )
	public FontDialog() {
		setResizable( false );
		setModal( true );
		setModalityType( ModalityType.APPLICATION_MODAL );
		setTitle( "Font" );
		getContentPane().setLayout(
				new MigLayout( "", "[grow][100px]", "[grow][grow][grow]" ) );

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder( new TitledBorder( new LineBorder( new Color( 184,
				207, 229 ) ), "Font family:", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color( 51, 51, 51 ) ) );
		getContentPane().add( scrollPane, "cell 0 0 1 2,grow" );

		listFontFamily =
				new JList( GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getAvailableFontFamilyNames() );
		listFontFamily.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		scrollPane.setViewportView( listFontFamily );

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder( new TitledBorder( null, "Size:",
				TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
		getContentPane().add( scrollPane_1, "cell 1 0,grow" );

		listFontSize = new JList();
		listFontSize.setModel( new AbstractListModel() {

			String[] values = new String[] { "4", "5", "6", "7", "8", "9",
					"10", "11", "12", "13", "14", "16", "18", "20", "22", "24",
					"28", "32" };

			public int getSize() {
				return values.length;
			}

			public Object getElementAt( int index ) {
				return values[index];
			}
		} );
		scrollPane_1.setViewportView( listFontSize );

		JPanel panel = new JPanel();
		panel.setBorder( new TitledBorder( null, "Style:",
				TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
		getContentPane().add( panel, "cell 1 1,grow" );
		panel.setLayout( new BoxLayout( panel, BoxLayout.PAGE_AXIS ) );

		chckbxBold = new JCheckBox( "Bold" );
		panel.add( chckbxBold );

		chckbxItalic = new JCheckBox( "Italic" );
		panel.add( chckbxItalic );

		JPanel panel_1 = new JPanel();
		getContentPane().add( panel_1, "cell 0 2 2 1,growx,aligny center" );
		panel_1.setLayout( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );

		JButton btnOk = new JButton( "Ok" );
		btnOk.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				dialogResult = DialogResult.OK;
				dispose();
			}
		} );
		panel_1.add( btnOk );

		cancelButton = new JButton( "Cancel" );
		cancelButton.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				dialogResult = DialogResult.CANCEL;
				dispose();
			}
		} );
		panel_1.add( cancelButton );
		pack();
		setLocationRelativeTo( Application.mainFrame );

		CommonMethods.addEscapeListener( this, cancelButton );
	}
}
