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
package vvide.ui.views;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import vvide.logger.Logger;
import vvide.ui.AbstractView;

/**
 * A view to display output of simulators
 */
public class ConsoleView extends AbstractView {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * ID for a ConsoleView
	 */
	public static int CONSOLE_VIEW_ID = 1;
	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -1149529400067404561L;
	/**
	 * A TextArea to display a text
	 */
	private StyledDocument consoleContent;
	/**
	 * A Style for normal output
	 */
	private Style normalText;
	/**
	 * A Style for error output
	 */
	private Style errorText;
	/**
	 * A text area
	 */
	private JTextPane textArea;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for normalText
	 * 
	 * @return the normalText
	 */
	public Style getNormalText() {
		return normalText;
	}

	/**
	 * Getter for errorText
	 * 
	 * @return the errorText
	 */
	public Style getErrorText() {
		return errorText;
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public ConsoleView( int id) {
		super( "Console", null, id );

		textArea = new JTextPane();
		textArea.setEditable( false );
		JScrollPane sp = new JScrollPane( textArea );
		setComponent( sp );

		// Setting up the console
		consoleContent = (StyledDocument) textArea.getDocument();
		normalText = consoleContent.addStyle( "Normal", null );
		errorText = consoleContent.addStyle( "Error", null );
		StyleConstants.setForeground( errorText, Color.RED );
	}
	
	/**
	 * Clear the console content
	 */
	public void clearContent() {
		try {
			consoleContent.remove( 0, consoleContent.getLength() );
		}
		catch ( BadLocationException e ) {
			Logger.logError( this, e );
		}
	}

	/**
	 * Append a normal string
	 * 
	 * @param text
	 *        a String with a text to append
	 */
	public void appendNormalText( String text ) {
		appendText( text, normalText );
	}

	/**
	 * Append a normal string
	 * 
	 * @param text
	 *        a Vector with Strings to append
	 */
	public void appendNormalText( Vector<String> text ) {
		for ( String line : text ) {
			appendText( line, normalText );
		}
	}

	/**
	 * Append an error string
	 * 
	 * @param text
	 *        a String with a text to append
	 */
	public void appendErrorText( String text ) {
		appendText( text, errorText );
	}

	/**
	 * Append an error string
	 * 
	 * @param text
	 *        a Vector with Strings to append
	 */
	public void appendErrorText( Vector<String> text ) {
		for ( String line : text ) {
			appendText( line, errorText );
		}
	}

	/**
	 * Append a text to the console
	 * 
	 * @param text
	 *        a text to append
	 * @param style
	 *        a style for the text
	 */
	public synchronized void appendText( String text, Style style ) {
		try {
			// adding "\n"
			text = text.trim() + "\n";
			if ( style == null ) style = normalText;
			consoleContent.insertString( consoleContent.getLength(), text,
					style );
			textArea.setCaretPosition( consoleContent.getLength() );
		}
		catch ( BadLocationException e ) {
			Logger.logError( this, e );
		}
	}
}
