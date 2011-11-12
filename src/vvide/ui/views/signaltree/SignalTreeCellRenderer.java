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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */

package vvide.ui.views.signaltree;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import vvide.signal.AbstractSignal;
import vvide.signal.Scope;

/**
 * Render for a SignalTreeCell
 */
public class SignalTreeCellRenderer extends DefaultTreeCellRenderer {

	/*
	 * =========================== Attributes ================================
	 */
	private static final long	serialVersionUID	= 3769401031066911530L;
	/**
	 * Icon for OneBitSignal
	 */
	private ImageIcon			oneBitSignalIcon;
	/**
	 * Icon for ManyBitSignal
	 */
	private ImageIcon			manyBitSignalIcon;

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Constructor
	 */
	public SignalTreeCellRenderer() {
		super();
		oneBitSignalIcon = new ImageIcon(this.getClass().getResource("/img/one-bit-signal.png"));
		manyBitSignalIcon = new ImageIcon(this.getClass().getResource("/img/many-bit-signal.png"));
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		String text = "";
		if (value instanceof Scope) {
			text = ((Scope) value).getName();
		} else {
			AbstractSignal signal = (AbstractSignal) value;
			text = signal.getName();
			setIcon(oneBitSignalIcon);
			// Add bitwidth information for manybit signal
			if (signal.getBitWidth() > 1) {
				text += " [" + signal.getBitWidth() + " Bit]";
				setIcon(manyBitSignalIcon);
			}
			// add bitNr information for a single signals in manybit signal
			if (signal.getBitNr() != -1) {
				text += " [Bit #" + (signal.getBitNr() + 1) + "]";
			}
		}
		setText(text);
		return this;
	}

}
