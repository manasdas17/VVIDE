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

import org.fife.ui.rtextarea.RTextAreaEditorKit;

/**
 * An Action to complete the word
 */
public class WordCompletionAction extends AbstractTextAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 1924529111758911674L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public WordCompletionAction() {
		super( new RTextAreaEditorKit.DumbCompleteWordAction(), "Word Completion" );
	}
}