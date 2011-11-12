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

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A panel to Adjust a color Setting
 */
public class ColorPanel extends AbstractSettingPanel {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -4770059914611367592L;
	/**
	 * A Panel with color sample
	 */
	private JPanel pnlSample;
	/**
	 * A color
	 */
	private Color color;

	/*
	 * ======================= Getters / Setters =============================
	 */
	@Override
	public Object getValue() {
		return color;
	}

	@Override
	public void setValue( Object value ) {
		color = (Color) value;
		pnlSample.setBackground( color );
	}

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Create the panel.
	 */
	public ColorPanel() {
		super();

		pnlSample = new JPanel();
		pnlSample.setBorder( new LineBorder( new Color( 0, 0, 0 ) ) );
		pnlSample.setPreferredSize( new Dimension( 50, 25 ) );
		pnlSample.setMinimumSize( new Dimension( 50, 25 ) );
		pnlSample.setMaximumSize( new Dimension( 50, 25 ) );
		add( pnlSample, "cell 0 0" );

		JButton btnFont = new JButton( "..." );
		btnFont.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed( ActionEvent e ) {
				Color res =
						JColorChooser.showDialog( null, "Choose the color",
								color );
				if ( res != null ) setValue( res );
			}
		} );
		add( btnFont, "cell 0 0" );
	}
}