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

package vvide.ui.views.signaltree;

import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import vvide.signal.AbstractSignal;
import vvide.signal.CompoundSignal;
import vvide.signal.Scope;
import vvide.signal.VectorSignal;

/**
 * Tree model for signal tree
 */
public class SignalTreeModel implements TreeModel {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Model Listeners
	 */
	private Vector<TreeModelListener> treeModelListeners =
			new Vector<TreeModelListener>();
	/**
	 * Root
	 */
	private Scope rootScope;

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Constructor
	 */
	public SignalTreeModel( Scope root ) {
		this.rootScope = root;
	}

	@Override
	public void addTreeModelListener( TreeModelListener listener ) {
		treeModelListeners.add( listener );
	}

	@Override
	public Object getChild( Object parent, int index ) {
		if ( parent instanceof Scope ) {
			// Scope element
			Scope scope = (Scope) parent;
			int countScopes = scope.getScopes().size();
			return (countScopes > index) ? scope.getScopes().get( index )
					: scope.getSignals().get( index - countScopes );
		}
		else {
			// ManyBitSignal
			CompoundSignal signal = (CompoundSignal) parent;
			return signal.getSignal( index );
		}
	}

	@Override
	public int getChildCount( Object parent ) {
		if ( parent instanceof Scope ) {
			// Scope element
			Scope scope = (Scope) parent;
			return scope.getScopes().size() + scope.getSignals().size();
		}
		else {
			// ManyBitSignal
			CompoundSignal signal = (CompoundSignal) parent;
			return signal.getChildrens().size();
		}
	}

	@Override
	public int getIndexOfChild( Object parent, Object child ) {
		if ( parent instanceof Scope ) {
			Scope scope = (Scope) parent;
			if ( child instanceof AbstractSignal ) {
				return scope.getSignals().indexOf( child )
					+ scope.getScopes().size();
			}
			else {
				return scope.getScopes().indexOf( child );
			}
		}
		else {
			// ManyBitSignal
			CompoundSignal signal = (CompoundSignal) parent;
			return signal.getChildrens().indexOf( child );
		}
	}

	@Override
	public Object getRoot() {
		return rootScope;
	}

	@Override
	public boolean isLeaf( Object node ) {
		return (node instanceof VectorSignal);
	}

	@Override
	public void removeTreeModelListener( TreeModelListener listener ) {
		treeModelListeners.remove( listener );
	}

	@Override
	public void valueForPathChanged( TreePath arg0, Object arg1 ) {}

}
