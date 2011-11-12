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
package vvide.ui.views.editor.vhdl;

import javax.swing.ListCellRenderer;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import vvide.ui.views.editor.IconCompletionCellRenderer;
import vvide.ui.views.editor.SyntaxChecker;

/**
 * Language support for VHDL
 */
public class VHDLLanguageSupport extends AbstractLanguageSupport {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Code completing provider
	 */
	private VHDLCompletionProvider provider = null;
	/**
	 * Syntax Checker
	 */
	private SyntaxChecker syntaxChecker = null;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for provider
	 * 
	 * @return the provider
	 */
	public VHDLCompletionProvider getProvider() {
		if ( provider == null ) {
			provider = new VHDLCompletionProvider();
		}
		return provider;
	}
	
	/**
	 * Getter for syntax Checker
	 * @return the syntax checker
	 */
	public SyntaxChecker getSyntaxChecker() {
		if ( syntaxChecker == null ) {
			syntaxChecker = new SyntaxChecker();
		}
		return syntaxChecker;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public VHDLLanguageSupport() {
		setParameterAssistanceEnabled( true );
		setShowDescWindow( true );
	}

	@Override
	protected ListCellRenderer createDefaultCompletionCellRenderer() {
		return new IconCompletionCellRenderer();
	}

	@Override
	public void install( RSyntaxTextArea textArea ) {
		AutoCompletion ac = createAutoCompletion( getProvider() );
		ac.install( textArea );
		installImpl( textArea, ac );
		
		SyntaxChecker syntaxChecker = getSyntaxChecker();
		
		textArea.addParser(syntaxChecker);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, syntaxChecker);
		
		textArea.setToolTipSupplier( provider );
	}

	@Override
	public void uninstall( RSyntaxTextArea textArea ) {
		uninstallImpl( textArea );
		
		Object syntaxChecker = textArea.getClientProperty(PROPERTY_LANGUAGE_PARSER);
		if (syntaxChecker instanceof SyntaxChecker) {
			textArea.removeParser((SyntaxChecker) syntaxChecker);
		}
	}
}
