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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import net.infonode.util.Direction;

import vvide.Application;
import vvide.ui.AbstractView;
import vvide.ui.TransientTabWindow;

/**
 * Action to show a specified view
 */
@SuppressWarnings( "serial" )
public abstract class AbstractShowViewAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * ID of the view to open/close
	 */
	protected int viewId;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param name
	 *        title for the menu
	 * @param viewId
	 *        ID of the view to control
	 */
	public AbstractShowViewAction( String name, int viewId ) {
		super( name );

		this.viewId = viewId;

		// Adding a listener to the view
		Application.viewManager.getView( viewId ).addPropertyChangeListener(
				AbstractView.PROPERTY_VISIBLE, new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						putValue( SELECTED_KEY, evt.getNewValue() );
					}
				} );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {

		// Get a needed view
		AbstractView view =
				((AbstractView) Application.viewManager.getView( viewId ));

		// If it open - close it
		if ( view.isDisplayable() ) {
			view.close();
		}
		else {
			// Restoring a view
			view.restore();
			if ( !view.isDisplayable() ) {
				Application.mainFrame.getRootWindow().getWindow().split(
						new TransientTabWindow( view ), Direction.LEFT, 0.15f );
			}
		}
	}
}
