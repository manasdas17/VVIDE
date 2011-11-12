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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchEngine;

import vvide.utils.CommonMethods;

/**
 * Search for the specified text
 */
public class ReplaceAllTextAction extends AbstractSearchAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -1602867417667439531L;

	/*
	 * ============================ Methods ==================================
	 */
	public ReplaceAllTextAction() {
		super( "Replace All" );
		putValue( SHORT_DESCRIPTION,
				"Replace all instances of founded text with specified text" );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		if ( dialog == null || dialog.getSearchText().length() == 0 ) return;
		RSyntaxTextArea codeEditor = CommonMethods.getCurrentEditor();
		int count =
				SearchEngine.replaceAll( codeEditor, dialog.getSearchText(),
						dialog.getReplaceText(), dialog.isCaseSesitive(),
						dialog.isWholeWord(), dialog.isRegularExpression() );
		dialog.setStatusText( "Replaced " + count + "coincidence(s)" );
	}
}
