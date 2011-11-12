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
package vvide.simulator.simulators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vvide.Application;
import vvide.logger.Logger;
import vvide.simulator.AbstractSimulator;
import vvide.utils.IOMethods;

/**
 * VHDL Simulator using ModelSim
 */
public class ModelSimSimulator extends AbstractSimulator {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Pattern for top Entity (VHDL)
	 */
	Pattern topEntityPattern = Pattern.compile( "entity(\\s+)(\\S+)(\\s+)is" );
	/**
	 * Pattern for top Entity (Verilog)
	 */
	Pattern topModulePattern = Pattern.compile( "module(\\s+)([^\\(]+)" );
	/**
	 * Pattern for error output
	 */
	private Pattern errorPattern = Pattern.compile( "^\\((\\d+)\\): (.+)$" );
	/**
	 * Show the work library is init
	 */
	private boolean isInit = false;

	/*
	 * ======================= Getters / Setters =============================
	 */
	@Override
	public String getTopLevelModuleName( String fileName ) throws IOException {
		String name = null;

		String content =
			Application.projectManager.getCurrentProject().getFile( fileName )
				.getContent();
		Matcher m = topEntityPattern.matcher( content );

		// Try vhdl
		if ( m.find() ) {
			name = m.group( 2 );
		}
		else {
			// Try verilog
			m = topModulePattern.matcher( content );
			if ( m.find() ) {
				name = m.group( 2 );
			}
		}

		return name.toLowerCase();
	}

	@Override
	public String getDescription() {
		return "ModelSim Simulator";
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Create work library for the simulator
	 * 
	 * @throws IOException
	 */
	private void initSimulator() throws IOException {
		// Create a library
		List<String> command = new ArrayList<String>( 2 );
		command.add( Application.settingsManager.getModelSimBinPath() + "vlib" );
		command.add( "work" );
		executeCommand( command );
		isInit = true;
	}

	@Override
	public Pattern getErrorMessagePattern() {
		return errorPattern;
	}

	@Override
	public List<String> getCheckSyntaxCommand( String fileName ) {
		return getCompileCommand( fileName );
	}

	/**
	 * Return a compile command
	 * 
	 * @param fileName
	 *        name of the file
	 * @return
	 */
	private List<String> getCompileCommand( String fileName ) {
		if ( !isInit ) try {
			initSimulator();
		}
		catch ( IOException e1 ) {
			return null;
		}
		// Build a command
		List<String> command = new ArrayList<String>( 3 );

		String content;
		try {
			content = IOMethods.loadTextFile( fileName );
			if ( content == null ) return null;

			Matcher m = topEntityPattern.matcher( content );

			// Try vhdl
			if ( m.find() ) {
				command.add( Application.settingsManager.getModelSimBinPath()
					+ "vcom" );
				command.add( "-93" );
			}
			else {
				// Try verilog
				m = topModulePattern.matcher( content );
				if ( m.find() ) {
					command.add( Application.settingsManager
						.getModelSimBinPath()
						+ "vlog" );
				}
			}
			command.add( fileName );
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}

		return command;
	}

	@Override
	public void analyzeFile( String fileName ) {
		errorData = new Vector<String>();
		outputData = new Vector<String>();
	}

	@Override
	public void compile( String fileName, String unitName ) throws IOException {
		if ( !isInit ) initSimulator();
		executeCommand( getCompileCommand( fileName ) );
	}

	@Override
	public void simulate( String fileName, String unitName, String stopTime ) throws IOException {
		if ( !isInit ) initSimulator();
		// Adjust the script file
		try {
			BufferedReader reader =
				new BufferedReader( new InputStreamReader( getClass()
					.getResourceAsStream( "/res/simulate.do" ) ) );
			BufferedWriter writer =
				new BufferedWriter( new FileWriter( Application.projectManager
					.getCurrentProject().getTemporaryFolder()
					+ "simulate.do" ) );
			String line;
			while ( (line = reader.readLine()) != null ) {
				line = line.replaceFirst( "%TOP_LEVEL_ENTITY%", unitName );
				line = line.replaceFirst( "%SIMULATION_TIME%", stopTime );
				writer.write( line );
				writer.write( "\n" );
			}
			reader.close();
			writer.flush();
			writer.close();
		}
		catch ( IOException e ) {
			Logger.logError( this, e );
			return;
		}

		// Build a command
		List<String> command = new ArrayList<String>( 4 );
		command.add( Application.settingsManager.getModelSimBinPath() + "vsim" );
		command.add( "-c" );
		command.add( "-do" );
		command.add( "simulate.do" );
		simulateInThread( command );
	}
}
