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

import java.awt.Point;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.FloatingWindow;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.TabWindow;
import net.infonode.docking.model.TabWindowItem;

/**
 * A tab window that can be transient
 */
public class TransientTabWindow extends TabWindow {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -6198008287629639334L;

	/**
	 * Show that the window don't need to save it content
	 */
	private boolean isTransient = false;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Setter for isTransient
	 * 
	 * @param isTransient
	 *        the isTransient to set
	 */
	public void setTransient( boolean isTransient ) {
		this.isTransient = isTransient;
		if (isTransient) {
			getTabWindowProperties().getCloseButtonProperties().setVisible( false );
			getTabWindowProperties().getUndockButtonProperties().setVisible( false );
		}
	}

	/**
	 * Getter for isTransient
	 * 
	 * @return the isTransient
	 */
	public boolean isTransient() {
		return isTransient;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @see TabWindow
	 */
	public TransientTabWindow() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @see TabWindow
	 */
	public TransientTabWindow( DockingWindow window ) {
		super( window );
	}

	/**
	 * Constructor
	 * 
	 * @see TabWindow
	 */
	public TransientTabWindow( DockingWindow[] windows ) {
		super( windows );
	}

	/**
	 * Constructor
	 * 
	 * @see TabWindow
	 */
	public TransientTabWindow( DockingWindow[] windows, TabWindowItem windowItem ) {
		super( windows, windowItem );
	}

	@Override
	protected void optimizeWindowLayout() {
		// Do nothing to prevent changing in editor view
		if ( !isTransient ) super.optimizeWindowLayout();
	}

	@Override
	public FloatingWindow undockWithAbort( Point location )
		throws OperationAbortedException {
		if ( isTransient ) {
			throw new OperationAbortedException();
		}
		else return super.undockWithAbort( location );
	}

	@Override
	public void close() {
		if ( !isTransient ) super.close();
	}
}