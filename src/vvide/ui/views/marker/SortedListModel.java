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
package vvide.ui.views.marker;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

/**
 * A sorted list model
 */
public class SortedListModel extends AbstractListModel {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 2001241776146316810L;
	/**
	 * Container for maodel
	 */
	SortedSet<Object> model;

	/*
	 * ======================= Getters / Setters =============================
	 */
	public int getSize() {
		// Return the model size
		return model.size();
	}

	public Object getElementAt( int index ) {
		// Return the appropriate element
		return model.toArray()[index];
	}

	// Other methods
	public void add( Object element ) {
		if ( model.add( element ) ) {
			fireContentsChanged( this, 0, getSize() );
		}
	}

	public void addAll( Object elements[] ) {
		Collection<Object> c = Arrays.asList( elements );
		model.addAll( c );
		fireContentsChanged( this, 0, getSize() );
	}

	public void clear() {
		model.clear();
		fireContentsChanged( this, 0, getSize() );
	}

	public boolean contains( Object element ) {
		return model.contains( element );
	}

	public Object firstElement() {
		// Return the appropriate element
		return model.first();
	}

	public Iterator<Object> iterator() {
		return model.iterator();
	}

	public Object lastElement() {
		// Return the appropriate element
		return model.last();
	}

	public boolean removeElement( Object element ) {
		boolean removed = model.remove( element );
		if ( removed ) {
			fireContentsChanged( this, 0, getSize() + 1 );
		}
		return removed;
	}

	public void updateElement( Object element ) {
		if ( contains( element ) ) {
			fireContentsChanged( this, 0, getSize() );
		}
	}

	/*
	 * ============================ Methods ==================================
	 */
	public SortedListModel() {
		// Create a TreeSet
		// Store it in SortedSet variable
		model = new TreeSet<Object>();
	}
}
