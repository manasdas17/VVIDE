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
import vvide.signal.AbstractSignal;
import vvide.signal.Marker;
import vvide.signal.VisibleSignal;
import vvide.ui.views.WaveView;

/**
 * Mouse Adapter for Actions with the timeline
 */
public class TimeLineMouseAdapter extends MouseAdapter {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * WaveView
	 */
	private WaveView view;
	/**
	 * Horizontal scrollbar
	 */
	private final JScrollBar scrollHorizontal;
	/**
	 * Last position of the moved marker
	 */
	private int lastDraggedMarkersPosition = 0;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param view
	 *        WaveView
	 * @param render
	 *        Marker render
	 * @param scrollHorizontal
	 */
	public TimeLineMouseAdapter( WaveView view, JScrollBar scrollHorizontal ) {
		this.view = view;
		this.scrollHorizontal = scrollHorizontal;
	}

	@Override
	public void mouseClicked( MouseEvent e ) {
		int x = e.getX() - Application.settingsManager.getInfoWidth() - 1;
		if ( x < 0 ) return;

		// Make a new marker
		if ( e.getClickCount() > 1 ) {
			long position = view.getTimeFromCoord( e.getX(), true );
			if ( position > Application.signalManager.getSignalLength() + 5
				* view.getZoom() ) return;

			position = Application.markerManager.correctPosition( position );
			Application.markerManager.addMarker( position );
		}
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		int x = e.getX() - Application.settingsManager.getInfoWidth() - 1;
		if ( x < 0 ) return;

		Application.markerManager.setSelectedMarker( Application.markerManager
				.getMarkerUnderCursor( e.getX() ) );
		lastDraggedMarkersPosition = e.getX();
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
		lastDraggedMarkersPosition = 0;
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		scrollHorizontal.dispatchEvent( e );
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		Marker selectedMarker = Application.markerManager.getSelectedMarker();
		if ( selectedMarker == null ) return;

		long newPosition = view.getTimeFromCoord( e.getX(), true );
		
		// Jump to next/previous Change
		if ( e.isControlDown() ) {
			// Check direction of the movement
			boolean isNext = ((e.getX() - lastDraggedMarkersPosition) > 0);
			long position = view.getTimeFromCoord( e.getX() );
			AbstractSignal signal =
					Application.signalManager.getSelectedSignal();
			long changeTime = -1;
			// find a next signal change
			if ( signal != null ) {
				changeTime =
						(isNext) ? signal.getNextChangeTime( position )
								: signal.getPreviousChangeTime( position );
			}
			else {
				for ( VisibleSignal nextSignal : Application.signalManager
						.getVisibleSignals() ) {
					long nextChangeTime =
							(isNext) ? nextSignal.getSignal()
									.getNextChangeTime( position ) : nextSignal
									.getSignal().getPreviousChangeTime(
											position );
					if ( nextChangeTime == -1 ) {
						nextChangeTime = Application.signalManager.getSignalLength();
					}
					if ( changeTime == -1 ) {
						changeTime = nextChangeTime;
					}
					else {
						changeTime =
								(isNext) ? Math
										.min( changeTime, nextChangeTime )
										: Math.max( changeTime, nextChangeTime );
					}
				}
			}
			if ( changeTime != -1 ) {
				long changePosition =
						Math.abs( e.getX() - view.getCoordFromTime( changeTime ) );
				if ( changePosition <= 3 ) {
					newPosition = changeTime;
				}
			}
		}
		Application.markerManager.changeMarkerPosition(selectedMarker, newPosition);
		lastDraggedMarkersPosition = e.getX();
	}

}
