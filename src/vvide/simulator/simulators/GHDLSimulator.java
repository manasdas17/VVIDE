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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vvide.Application;
import vvide.simulator.AbstractSimulator;

/**
 * A VHDL Simulator used GHDL
 */
public class GHDLSimulator extends AbstractSimulator {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Pattern for top Entity
	 */
	private Pattern topEntityPattern = Pattern
		.compile( "entity(\\s+)(\\S+)(\\s+)is" );
	/**
	 * Pattern for error output
	 */
	private Pattern errorPattern = Pattern.compile( "^:(\\d+):[\\d]*: (.+)$" );

	@Override
	public String getDescription() {
		return "GHDL Simulator";
	}

	/*
	 * ======================= Getters / Setters =============================
	 */
	@Override
	public String getTopLevelModuleName( String fileName ) throws IOException {
		String name = null;

		// find the first "entity (\w+) is" text
		String content =
			Application.projectManager.getCurrentProject().getFile( fileName )
				.getContent();
		Matcher m = topEntityPattern.matcher( content );

		if ( m.find() ) {
			name = m.group( 2 );
		}

		return (name != null) ? name.toLowerCase() : "";
	}

	@Override
	public Pattern getErrorMessagePattern() {
		return errorPattern;
	}

	/*
	 * ============================ Methods ==================================
	 */
	@Override
	public List<String> getCheckSyntaxCommand( String fileName ) {
		// Build a command
		List<String> command = new ArrayList<String>( 3 );
		command.add( Application.settingsManager.getGHDLPath() );
		command.add( "-s" );
		command.add( fileName );
		return command;
	}

	@Override
	public void analyzeFile( String fileName ) throws IOException {

		// File analyze
		List<String> command = new ArrayList<String>( 4 );
		command.add( Application.settingsManager.getGHDLPath() );
		command.add( "-a" );
		command.add( "-g" );
		command.add( fileName );

		executeCommand( command );
	}

	@Override
	public void compile( String fileName, String unitName ) throws IOException {
		// Compile
		List<String> command = new ArrayList<String>( 4 );
		command.add( Application.settingsManager.getGHDLPath() );
		command.add( "-e" );
		command.add( unitName );

		executeCommand( command );
	}

	@Override
	public void simulate( String fileName, String unitName, String stopTime )
		throws IOException {
		// Simulate
		List<String> command = new ArrayList<String>( 3 );
		// getting current OS
		if ( (System.getProperty( "os.name" )).contains( "Windows" ) ) {
			command.add( Application.settingsManager.getGHDLPath() );
			command.add( "--elab-run" );
			command.add( "-g" );
			command.add( unitName );
			command.add( "--vcd=" + VCDFILE_NAME );
			command.add( "--stop-time=" + stopTime );

		}
		else {
			command.add( "./" + unitName );
			command.add( "--vcd=" + VCDFILE_NAME );
			command.add( "--stop-time=" + stopTime );
		}

		simulateInThread( command );
	}
}