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
package vvide.ui.views.editor;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.EmptyIcon;
import org.fife.ui.autocomplete.FunctionCompletion;

/**
 * CellRender with function icons
 */
public class IconCompletionCellRenderer extends CompletionCellRenderer {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -4251218745642322100L;
	/**
	 * Icon for function
	 */
	private Icon functionIcon;
	/**
	 * Icon for all other types
	 */
	private Icon emptyIcon;

	/*
	 * ======================= Getters / Setters =============================
	 */

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public IconCompletionCellRenderer() {
		functionIcon =
				new ImageIcon( getClass().getResource( "/img/function.png" ) );
		emptyIcon = new EmptyIcon( 16 );
	}

	@Override
	protected void prepareForFunctionCompletion( JList list,
			FunctionCompletion fc, int index, boolean selected, boolean hasFocus ) {
		super.prepareForFunctionCompletion( list, fc, index, selected, hasFocus );
		setIcon( functionIcon );
	}

	@Override
	protected void prepareForOtherCompletion( JList list, Completion c,
			int index, boolean selected, boolean hasFocus ) {
		super.prepareForOtherCompletion( list, c, index, selected, hasFocus );
		setIcon( emptyIcon );
	}

}
