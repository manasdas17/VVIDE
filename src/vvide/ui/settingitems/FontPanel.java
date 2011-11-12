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

package vvide.ui.settingitems;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import vvide.ui.DialogResult;
import vvide.ui.FontDialog;

/**
 * A panel to adjust a font setting
 */
public class FontPanel extends AbstractSettingPanel {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 5943960411523059087L;
	/**
	 * A label with sample text
	 */
	private JLabel lblSample;
	/**
	 * A selected font
	 */
	private Font font;

	/*
	 * ======================= Getters / Setters =============================
	 */
	@Override
	public Object getValue() {
		return font;
	}

	@Override
	public void setValue( Object value ) {
		font = (Font) value;
		lblSample.setFont( font );
	}

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Create the panel.
	 */
	public FontPanel() {
		super();

		lblSample = new JLabel( "Sample Text" );
		lblSample.setHorizontalAlignment( SwingConstants.RIGHT );
		lblSample.setPreferredSize( new Dimension( 150, 25 ) );
		lblSample.setMinimumSize( new Dimension( 150, 25 ) );
		lblSample.setMaximumSize( new Dimension( 150, 25 ) );
		add( lblSample, "cell 0 0" );

		JButton btnFont = new JButton( "..." );
		btnFont.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				FontDialog fontDialog = new FontDialog();
				fontDialog.setFontToEdit( font );
				fontDialog.setVisible( true );
				if ( fontDialog.getDialogResult() == DialogResult.OK )
					setValue( fontDialog.getFontToEdit() );
			}
		} );
		add( btnFont, "cell 0 0" );
	}
}