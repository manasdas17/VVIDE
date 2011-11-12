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
package vvide.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import vvide.Application;
import vvide.ViewManager;
import vvide.ui.views.ProjectView;
import vvide.ui.views.WaveView;

/**
 * Action set minimal zoom (Show all signal)
 */
public class MinimalZoomAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -8144383734183373209L;
	/**
	 * Instance of the WaveView
	 */
	private WaveView waveView = (WaveView) Application.viewManager
			.getView( ViewManager.WAVE_VIEW_ID );

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public MinimalZoomAction() {
		super( "Show all" );
		putValue( SHORT_DESCRIPTION, "Show complete signal wave" );
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_DIVIDE,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
		putValue( LARGE_ICON_KEY, new ImageIcon( getClass().getResource(
				"/img/actions/zoom_all.png" ) ) );

		setEnabled( false );

		// Listener for Enable/Disable Action
		waveView.addPropertyChangeListener( ProjectView.PROPERTY_VISIBLE,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						setEnabled( (Boolean) evt.getNewValue() );
					}
				} );

	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		if ( waveView.isDisplayable() )
			waveView.setZoom( waveView.getMinimalZoom() );
	}
}
