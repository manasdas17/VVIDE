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
package vvide.ui.views.wave;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JScrollBar;

import vvide.Application;
import vvide.ui.views.WaveView;

/**
 * Mouse Adapter for Actions with the signals
 */
public class SignalPanelMouseAdapter extends MouseAdapter {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * WaveView
	 */
	private WaveView view;
	/**
	 * Signal Render
	 */
	private SignalRender render;
	/**
	 * Vertical scrollbar
	 */
	private final JScrollBar scrollVertical;
	/**
	 * Horizontal scrollbar
	 */
	private final JScrollBar scrollHorizontal;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param view
	 *        WaveView
	 * @param scrollHorizontal 
	 * @param scrollVertical 
	 */
	public SignalPanelMouseAdapter( WaveView view, SignalRender render, JScrollBar scrollVertical, JScrollBar scrollHorizontal ) {
		this.view = view;
		this.render = render;
		this.scrollVertical = scrollVertical;
		this.scrollHorizontal = scrollHorizontal;
	}

	@Override
	public void mouseClicked( MouseEvent e ) {
		render.proceedMouseClick( e );
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		Application.signalManager
				.setSelectedSignalIndex( view.getSignalIndexUnderCursor( e.getY() ) );
	}

	@Override
	public void mouseReleased( MouseEvent e ) {}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		if ( e.isAltDown() ) {
			scrollHorizontal.dispatchEvent( e );
		}
		else {
			scrollVertical.dispatchEvent( e );
		}
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		// No DND if no signal selected
		int selectedSignalIndex =
				Application.signalManager.getSelectedSignalIndex();
		if ( selectedSignalIndex == -1 ) return;

		int newIndex = view.getSignalIndexUnderCursor( e.getY() );
		Application.signalManager.setSignalPosition( selectedSignalIndex,
				newIndex, true );
	}

}
