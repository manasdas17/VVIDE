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

package vvide.ui.views.wave;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.TransferHandler;

import vvide.Application;
import vvide.logger.Logger;
import vvide.signal.AbstractSignal;
import vvide.ui.views.WaveView;

/**
 * A Target Listener for the Drag-n-Drop method for the Signal Panel
 */
public class SignalPanelDropTargetListener implements DropTargetListener {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * A WaveView for this target
	 */
	WaveView waveView;

	/*
	 * ============================= Methods =================================
	 */
	/**
	 * Constructor
	 * 
	 * @param view
	 *        a WaveView for this target
	 */
	public SignalPanelDropTargetListener( WaveView view ) {
		this.waveView = view;
	}

	@Override
	public void dragEnter( DropTargetDragEvent dtde ) {
		// The Panel accept olny Signals
		if ( dtde.isDataFlavorSupported(DataFlavor.stringFlavor) ) {
			dtde.acceptDrag( TransferHandler.COPY );
		}
	}

	@Override
	public void dragExit( DropTargetEvent dte ) {

	}

	@Override
	public void dragOver( DropTargetDragEvent dtde ) {}

	@Override
	public void drop( DropTargetDropEvent dtde ) {
		try {
			// Signal codes
			String signalCodes = (String) dtde.getTransferable().getTransferData( DataFlavor.stringFlavor );
			// Signals
			Vector<AbstractSignal> signals = new Vector<AbstractSignal>();
			
			// getting the signals
			String[] codes = signalCodes.split( "\n" );
			for (String path : codes) {
				AbstractSignal signal = Application.signalManager.findSignal( path );
				if (signal != null) {
					signals.add( signal );
				}
			}

			// getting an insert point
			// if cursor is not above an any signal adding to the end of the
			// list. Otherwise the signal will be inserted at the specified
			// position
			int index =
					waveView.getSignalIndexUnderCursor( dtde.getLocation().y );
			if ( index == -1 ) {
				Application.signalManager.addToVisible( signals );
			}
			else {
				Application.signalManager.insertVisibleSignalsAt(index, signals);
			}
		}
		catch ( UnsupportedFlavorException e ) {
			dtde.rejectDrop();
		}
		catch ( IOException e ) {
			Logger.logError( this, e );
		}
	}

	@Override
	public void dropActionChanged( DropTargetDragEvent dtde ) {}

}
