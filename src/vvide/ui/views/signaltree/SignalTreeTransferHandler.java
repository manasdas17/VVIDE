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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */

package vvide.ui.views.signaltree;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import vvide.signal.AbstractSignal;
import vvide.ui.views.SignalTreeView;

/**
 * A Transfer Handler for a signal tree. Allow to copy signals from the signal
 * tree by Drag-n-Drop
 */
public class SignalTreeTransferHandler extends TransferHandler {
	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version for the serialize
	 */
	private static final long	serialVersionUID	= -5928853634441510619L;
	/**
	 * Signal Tree View
	 */
	private final SignalTreeView signalTreeView;

	/*
	 * ======================== Getters / Setters ============================
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}

	/*
	 * ============================= Methods =================================
	 */
	
	public SignalTreeTransferHandler(SignalTreeView view) {
		this.signalTreeView = view;
	}
	
	@Override
	public Transferable createTransferable(JComponent c) {
		Vector<AbstractSignal> selectedSignals = signalTreeView.getSelectedSignals();
		StringBuffer sb = new StringBuffer();
		for (AbstractSignal signal : selectedSignals) sb.append( signal.getFullPath() ).append( "\n" );
		return new StringSelection( sb.toString() );
	}
}
