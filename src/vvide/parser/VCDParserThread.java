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

package vvide.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.Thread;

import vvide.Application;
import vvide.logger.Logger;
import vvide.signal.SignalValueDump;
import vvide.signal.TimeMetric;
import vvide.signal.visitors.DumpSetterVisitor;

/**
 * Background parser for VCD-Files Open a file and parse it
 */
public class VCDParserThread extends Thread {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Store a parser object. Need to update progress value and finished flag
	 */
	private VCDParser parser;
	/**
	 * File to parse
	 */
	private File fileToParse;
	/**
	 * Show, that file can be parsed. It's mean that the file exists and opened.
	 * NOT that the file is a VCD-file
	 */
	private boolean canStart;
	/**
	 * Store a size of the file
	 */
	private long fileSize;
	/**
	 * TimeRatio
	 */
	private int timeRatio = Application.settingsManager.getTimeRatio();
	/**
	 * Time Divide to eliminate not used timemetrics
	 */
	private long timeDivide = 1;
	/**
	 * Delta for timeScale
	 */
	private int timeScaleDelta = 0;
	/**
	 * Structural lexer
	 */
	private VCDStructLexer structLexer;
	/**
	 * Value lexer
	 */
	private VCDValueLexer valueLexer;
	/**
	 * Flag to interrupt the process
	 */
	private boolean interrupted = false;

	/*
	 * ====================== Getters and setters ============================
	 */
	/**
	 * Return a current value of the canStart attribute
	 * 
	 * @return a boolean value of the CanStart attribute
	 */
	public boolean canStart() {
		return this.canStart;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param parser
	 *        - a VCD parser object
	 */
	public VCDParserThread( VCDParser parser, File file ) {
		this.parser = parser;
		this.canStart = true;
		this.fileToParse = file;
		fileSize = file.length();
	}

	/**
	 * Interrupt the thread. Close the input stream
	 */
	@Override
	public void interrupt() {
		interrupted = true;
		if (structLexer != null)
			structLexer.interrupt();
		else if (valueLexer != null) 
			valueLexer.interrupt();
		super.interrupt();
	}

	/**
	 * Start the parsing process
	 */
	@Override
	public void run() {

		// Getting amount of changes of each signal
		// Set the current operation "Analyze the file"
		parser.setCurrentOperation( "Analyze the file" );

		try {
			structLexer =
					new VCDStructLexer( new BufferedReader( new FileReader(
							fileToParse ) ) );
			structLexer.setFileSize( fileSize );
			structLexer.setParser( parser );
			structLexer.yylex();
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
			return;
		}
		if (interrupted) return;

		// Calculating the time divide
		long minInterval = structLexer.getMinInterval();
		while ( minInterval > 0 && (minInterval % 1000) == 0 ) {
			minInterval /= 1000;
			timeDivide *= 1000;
			timeScaleDelta += 3;
		}

		// Setup a signalManager
		Application.signalManager.setScale( structLexer.getTimeScaleValue() );
		Application.signalManager.setScaleUnit( TimeMetric
				.fromString( structLexer.getTimeScaleUnit() )
			+ timeScaleDelta );

		parser.setCurrentOperation( "Memory allocation" );
		// getting amount of bits needed for save time
		long signalLength =
				structLexer.getLastTimestamp() / timeDivide * timeRatio
					+ timeRatio;
		Application.signalManager.setSignalLength( signalLength );
		Application.signalManager.createDumpBuffer( structLexer.maxVarID );

		// allocation
		for ( int i = 1; i <= structLexer.maxVarID; ++i ) {
			Integer bitWidthObj = structLexer.bitWidthMap.get( i );
			if (bitWidthObj != null)
			{
				SignalValueDump dump =
						new SignalValueDump( structLexer.changesBuffer[i], bitWidthObj );
				Application.signalManager.setSignalDump( i, dump );
			}
		}
		
		// Setting the dump for all signals
		DumpSetterVisitor visitor = new DumpSetterVisitor();
		Application.signalManager.getMainScope().accept( visitor );

		parser.setCurrentOperation( "Value parse" );
		structLexer = null;
		if (interrupted) return;
		try {
			valueLexer =
					new VCDValueLexer( new BufferedReader( new FileReader(
							fileToParse ) ) );
			valueLexer.setFileSize( fileSize );
			valueLexer.setParser( parser );
			valueLexer.setTimeDivide(timeDivide);
			valueLexer.yylex();
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
			return;
		}
		valueLexer = null;

		if (!interrupted) parser.setFinished( true );
	}
}
