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
package vvide.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import vvide.Application;
import vvide.utils.CommonMethods;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.View;

/**
 * Abstract class for all possible views
 */
@SuppressWarnings( "serial" )
public abstract class AbstractView extends View {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * Name of the project property
	 */
	public static String PROPERTY_VISIBLE = "isVisible";
	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Property change support
	 */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	/**
	 * Instance of this Object. Need for internal classes
	 */
	protected AbstractView thisView = null;
	/**
	 * A PopupMenu
	 */
	protected JPopupMenu popupMenu;
	/**
	 * ID of the view
	 */
	protected int id;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Return true if the view is Static.
	 * 
	 * @return true if the view is static, false otherwise
	 */
	public abstract boolean isStatic();

	/**
	 * Return the ID of the View
	 * 
	 * @return ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * Set the new visibility state
	 * 
	 * @param isVisible
	 *        new visible state
	 */
	protected void setIsVisible( boolean isVisible ) {
		pcs.firePropertyChange( PROPERTY_VISIBLE, !isVisible, isVisible );
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param title
	 *        title of the View
	 * @param icon
	 *        Icon for the view
	 * @param id
	 *        id of the view
	 */
	public AbstractView( String title, Icon icon, final int id ) {
		super( title, icon, null );

		this.id = id;

		// Adding a window listener
		addListener( new DockingWindowAdapter() {

			@Override
			public void windowRestored( DockingWindow window ) {
				setIsVisible( true );
			}

			@Override
			public void windowClosed( DockingWindow window ) {
				setIsVisible( false );
			}

			@Override
			public void windowRemoved( DockingWindow removedFromWindow,
					DockingWindow removedWindow ) {
				setIsVisible( false );
			}

			@Override
			public void windowAdded( DockingWindow addedToWindow,
					DockingWindow addedWindow ) {
				setIsVisible( true );
			}
		} );
	}

	/**
	 * Init the view
	 */
	public void initView() {
		Application.mainFrame.getRootWindow().addListener(
				new DockingWindowAdapter() {

					@Override
					public void windowClosed( DockingWindow window ) {
						setIsVisible( isDisplayable() );
					}
				} );
	}

	/**
	 * Add a listener to a specified property
	 * 
	 * @param property
	 *        a name of the property
	 * @param listener
	 *        listener to add
	 */
	public void addPropertyChangeListener( String property,
			PropertyChangeListener listener ) {
		this.pcs.addPropertyChangeListener( property, listener );
	}

	/**
	 * Add a listener to all events
	 * 
	 * @param listener
	 *        listener to add
	 */
	public void addPropertyChangeListener( PropertyChangeListener listener ) {
		this.pcs.addPropertyChangeListener( listener );
	}

	/**
	 * Remove a listener from all events
	 * 
	 * @param listener
	 *        listener to remove
	 */
	public void removePropertyChangeListener( PropertyChangeListener listener ) {
		this.pcs.removePropertyChangeListener( listener );
	}

	/**
	 * Remove a listener from a specified events
	 * 
	 * @param property
	 *        a name of the property
	 * @param listener
	 *        listener to remove
	 */
	public void removePropertyChangeListener( String property,
			PropertyChangeListener listener ) {
		this.pcs.removePropertyChangeListener( property, listener );
	}

	/*
	 * ========================= Internal Classes==============================
	 */
	/**
	 * Listener to show PopupMenu
	 */
	protected class PopupListener extends MouseAdapter {

		/**
		 * A menu to show
		 */
		private final JPopupMenu menu;
		/**
		 * A component for menu
		 */
		private final JComponent parentComponent;

		/**
		 * Constructor
		 * 
		 * @param menu
		 *        popup menu
		 * @param parentComponent
		 *        component for menu
		 */
		public PopupListener( JPopupMenu menu, JComponent parentComponent ) {
			this.menu = menu;
			this.parentComponent = parentComponent;
		}

		public void mousePressed( MouseEvent e ) {
			CommonMethods.maybeShowPopup( e, menu, parentComponent );
		}

		public void mouseReleased( MouseEvent e ) {
			CommonMethods.maybeShowPopup( e, menu, parentComponent );
		}
	}
}
