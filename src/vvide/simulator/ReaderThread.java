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
package vvide.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.text.Style;

import vvide.Application;
import vvide.ViewManager;
import vvide.logger.Logger;
import vvide.ui.views.ConsoleView;

/**
 * Thread to read a stream and print it into the console
 */
public class ReaderThread extends Thread {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Console
	 */
	ConsoleView console = null;
	/**
	 * Stream to read
	 */
	InputStream stream = null;
	/**
	 * Style for text
	 */
	Style style = null;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param stream
	 *        an input stream to read
	 */
	public ReaderThread( Style style, InputStream stream ) {
		this.style = style;
		this.stream = stream;
		this.console =
				(ConsoleView) Application.viewManager
						.getView( ViewManager.CONSOLE_VIEW_ID );
	}

	@Override
	public void run() {
		BufferedReader reader =
				new BufferedReader( new InputStreamReader( stream ) );
		while ( !interrupted() ) {
			try {
				String line = reader.readLine();
				// stop on eof
				if ( line == null ) break;
				console.appendText( line, style );
			}
			catch ( IOException e ) {
				Logger.logError( this, e );
			}
		}
		try {
			reader.close();
		}
		catch ( IOException e ) {
			Logger.logError( this, e );
		}
	}

}
