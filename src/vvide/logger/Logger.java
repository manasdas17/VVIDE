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

package vvide.logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger Class Save log messages in "vvide.log" Format:
 * "%date%: %source(class name/etc.)% log a(an) %type(warning/error/etc.)%: %Message text%"
 */
public class Logger {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Stream to the log-file
	 */
	private static PrintWriter stream;
	/**
	 * DateFormatter to format the current date and time
	 */
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss" );
	/**
	 * Buffer for formatted log-entry
	 */
	private static StringBuffer stringBuffer = new StringBuffer( 100 );
	/**
	 * Line separator sequence
	 */
	private static String lineSeparator = System.getProperty( "line.separator" );

	/**
	 * Various type of log-messages
	 */
	public static enum MessageType {
		WARNING, ERROR, DEBUG_MESSAGE, NOTIFY
	}

	/*
	 * ============================= Methods =================================
	 */
	/**
	 * Save a log-entry. Open the file "vvide.log" in current directory and
	 * save the message at the end of the file
	 * 
	 * @param source
	 *        - text, that represent the place, where the message come
	 * @param type
	 *        - type of the message
	 * @param message
	 *        - text of the message
	 * @throws FileNotFoundException
	 */
	public static void log( String source, MessageType type, String message ) {
		if ( stream == null ) {
			try {
				stream =
						new PrintWriter( new FileOutputStream( "vvide.log",
								true ) );
			}
			catch ( FileNotFoundException e ) {
				e.printStackTrace();
				return;
			}
		}

		stringBuffer.append( dateFormatter.format( new Date() ) ).append( ": " )
				.append( source ).append( " log a(an) " ).append(
						type.toString() ).append( ": " ).append( message )
				.append( lineSeparator );
		stream.print( stringBuffer.toString() );
		stream.flush();

		System.err.println( stringBuffer.toString() );
	}

	/**
	 * Log an error
	 * 
	 * @param sender
	 *        object sender
	 * @param e
	 *        exception
	 */
	public static void logError( Object sender, Exception e ) {
		String source =
				(String) ((sender instanceof String) ? sender : sender
						.getClass().getName());
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		logError( source, e.getClass().getName() + " " + e.getMessage() + "\n" + sw.toString() );
	}

	/**
	 * Log an error
	 * 
	 * @param source
	 *        string with source of the error
	 * @param message
	 *        string with error message
	 */
	public static void logError( String source, String message ) {
		log( source, MessageType.ERROR, message );
	}
}
