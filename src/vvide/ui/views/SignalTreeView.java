/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This file based on the code of WaveForm Viewer project.
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
package vvide.ui.views;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import vvide.Application;
import vvide.SignalManager;
import vvide.signal.AbstractSignal;
import vvide.signal.CompoundSignal;
import vvide.signal.Scope;
import vvide.ui.AbstractView;
import vvide.ui.views.signaltree.SignalTreeCellRenderer;
import vvide.ui.views.signaltree.SignalTreeTransferHandler;
import vvide.utils.CommonMethods;

/**
 * A View with signals tree
 */
public class SignalTreeView extends AbstractView {
	/*
	 * =========================== Properties ================================
	 */
	/**
	 * ID for a SignalTreeView
	 */
	public static int SIGNAL_TREE_VIEW_ID = 4;
	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 9207760289004567346L;
	/**
	 * Tree with the signals
	 */
	private JTree signalTree;
	/**
	 * Tree Mouse Listener
	 */
	private TreeMouseListener treeMouseListener = new TreeMouseListener();

	/*
	 * ======================= Getters / Setters =============================
	 */
	@Override
	public boolean isStatic() {
		return false;
	}

	/**
	 * Set the model for the tree
	 * 
	 * @param model
	 *        a model to set
	 */
	public void setModel( TreeModel model ) {
		signalTree.setModel( model );
	}

	/**
	 * Return the selected signals
	 * 
	 * @return the selected signals
	 */
	public Vector<AbstractSignal> getSelectedSignals() {
		// Vector with selected signals
		TreePath[] selectionPaths = signalTree.getSelectionPaths();
		Vector<AbstractSignal> selectedSignals = new Vector<AbstractSignal>();
		
		if (selectionPaths != null)
			for ( TreePath path : selectionPaths ) {
				if ( !(path.getLastPathComponent() instanceof Scope) )
					selectedSignals.add( (AbstractSignal) path
							.getLastPathComponent() );
			}
		
		return selectedSignals;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public SignalTreeView( int id ) {

		// Make UI
		super( "Signals", null, id );
		signalTree = new JTree();
		signalTree.setModel( null );
		signalTree.setRootVisible( false );
		setComponent( new JScrollPane( signalTree ) );
		signalTree.setCellRenderer( new SignalTreeCellRenderer() );

		// Prevent expand of ManyBit signals on doubleClick
		signalTree.setToggleClickCount( 0 );

		// Add listeners
		signalTree.addMouseListener( treeMouseListener );

		// Add DND
		signalTree.setDragEnabled( true );
		signalTree.setTransferHandler( new SignalTreeTransferHandler( this ) );
	}
	
	@Override
	public void initView() {
		super.initView();
		popupMenu = CommonMethods.getPopUpMenu( "signal_tree_view" );
		Application.signalManager.addPropertyChangeListener(
				SignalManager.SIGNALS_CLEARED, new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						signalTree.setModel( null );
					}
				} );
	}

	/*
	 * ========================= Internal Classes =============================
	 */
	/**
	 * Mouse listener
	 */
	private class TreeMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked( MouseEvent e ) {
			// check, if the selected item is a signal
			TreePath selection =
					signalTree.getPathForLocation( e.getX(), e.getY() );
			if ( (selection != null) && (e.getClickCount() > 1)
				&& !(selection.getLastPathComponent() instanceof Scope) ) {
				Application.signalManager.addToVisible( getSelectedSignals() );
			}
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			// if the item under cursor is a manybitsignal - don't expand it
			TreePath selection =
					signalTree.getPathForLocation( e.getX(), e.getY() );
			if ( (selection != null) && (e.getClickCount() > 1)
				&& (selection.getPathCount() > 1)
				&& !(selection.getLastPathComponent() instanceof CompoundSignal) ) {
				signalTree.expandPath( selection );
			}
			CommonMethods.maybeShowPopup( e, popupMenu, signalTree );
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			CommonMethods.maybeShowPopup( e, popupMenu, signalTree );
		}
	}
}
