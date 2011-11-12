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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;

import org.fife.rsta.ac.OutputCollector;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

import vvide.Application;
import vvide.logger.Logger;
import vvide.simulator.AbstractSimulator;

/**
 * Syntax Checker for VHDL language
 */
public class SyntaxChecker extends AbstractParser {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Parse result
	 */
	private DefaultParseResult result;
	/**
	 * The maximum amount of time to wait for VHDL to finish compiling a
	 * source file.
	 */
	private static final int MAX_COMPILE_MILLIS = 10000;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public SyntaxChecker() {
		result = new DefaultParseResult( this );
	}

	@Override
	public ParseResult parse( RSyntaxDocument doc, String style ) {
		result.clearNotices();
		

		int lineCount = doc.getDefaultRootElement().getElementCount();
		result.setParsedLines( 0, lineCount - 1 );

		long start = System.currentTimeMillis();

		try {

			// make a tmp copy
			String workFolder = Application.projectManager.getCurrentProject()
			.getTemporaryFolder(); 
			AbstractSimulator simulator = Application.projectManager.getCurrentProject().getSimulator();
			File tempFile =
				new File( workFolder + "sytaxcheck.tmp" );
			BufferedOutputStream out =
				new BufferedOutputStream( new FileOutputStream( tempFile ) );
			try {
				new DefaultEditorKit().write( out, doc, 0, doc.getLength() );
			}
			catch ( BadLocationException ble ) {
				ble.printStackTrace();
				throw new IOException( ble.getMessage() );
			}
			out.close();

			// Getting a command to check syntax and starting a process
			List<String> command =
				simulator.getCheckSyntaxCommand( tempFile.getAbsolutePath() );
			ProcessBuilder builder = new ProcessBuilder( command );
			builder.directory( new File( workFolder ) );
			builder.redirectErrorStream( true );
			
			Process p = builder.start();

			Element root = doc.getDefaultRootElement();

			// Read all from stdout >> null
			OutputCollector stdout =
				new OutputCollector( p.getErrorStream(), true );
			Thread t = new Thread( stdout );
			t.start();

			// Parse an error messages
			ErrorParser stderr =
				new ErrorParser( p.getInputStream(), this, result, root,
					simulator.getErrorMessagePattern(), tempFile.getAbsolutePath() );
			Thread t2 = new Thread( stderr );
			t2.start();
			try {
				t2.join( MAX_COMPILE_MILLIS );
				t.join( MAX_COMPILE_MILLIS );
				if ( t.isAlive() ) {
					t.interrupt();
				}
				else {
					p.waitFor();
				}
			}
			catch ( InterruptedException ie ) {
				ie.printStackTrace();
			}

			long time = System.currentTimeMillis() - start;
			result.setParseTime( time );
		}
		catch ( IOException ioe ) {
			result.setError( ioe );
			Logger.logError( this, ioe );
		}

		return result;
	}
}
