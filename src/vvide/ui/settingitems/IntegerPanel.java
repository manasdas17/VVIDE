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

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * A panel to adjust an iteger setting
 */
public class IntegerPanel extends AbstractSettingPanel {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 3855561620595143933L;
	/**
	 * A spinner for the value
	 */
	private JSpinner spinner;

	/*
	 * ======================= Getters / Setters =============================
	 */
	@Override
	public Object getValue() {
		return spinner.getValue();
	}

	@Override
	public void setValue( Object value ) {
		spinner.setValue( value );
	}

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Create the panel.
	 */
	public IntegerPanel() {
		super();

		spinner = new JSpinner();
		spinner.setPreferredSize( new Dimension( 200, 20 ) );
		spinner.setMinimumSize( new Dimension( 200, 20 ) );
		spinner.setModel( new SpinnerNumberModel( new Integer( 0 ),
				new Integer( 0 ), null, new Integer( 1 ) ) );
		add( spinner, "cell 0 0" );
	}
}