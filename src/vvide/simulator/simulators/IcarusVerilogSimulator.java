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
import java.util.Vector;
import java.util.regex.Pattern;

import vvide.Application;
import vvide.simulator.AbstractSimulator;

/**
 * Simulator Icarus Verilog
 */
public class IcarusVerilogSimulator extends AbstractSimulator {

	/*
	 * ======================= Getters / Setters =============================
	 */
	@Override
	public String getTopLevelModuleName( String fileName ) throws IOException {
		// return filename without extension
		return fileName.substring( 0, fileName.lastIndexOf( '.' ) );
	}

	@Override
	public Pattern getErrorMessagePattern() {
		return Pattern.compile( "^:(\\d+): (.+)$" );
	}

	@Override
	public String getDescription() {
		return "Icarus Verilog Simulator";
	}

	/*
	 * ============================ Methods ==================================
	 */
	@Override
	public List<String> getCheckSyntaxCommand( String fileName ) {
		// Build a command
		List<String> command = new ArrayList<String>( 6 );
		command.add( Application.settingsManager.getIcarusCompilerPath() );
		command.add( "-y" );
		command.add( Application.projectManager.getCurrentProject()
			.getProjectLocation() );
		command.add( "-t" );
		command.add( "null" );
		command.add( fileName );

		return command;
	}

	@Override
	public void analyzeFile( String fileName ) throws IOException {
		errorData = new Vector<String>();
		outputData = new Vector<String>();
	}

	@Override
	public void compile( String fileName, String unitName ) throws IOException {
		// Build a command
		List<String> command = new ArrayList<String>( 4 );
		command.add( Application.settingsManager.getIcarusCompilerPath() );
		command.add( "-y" );
		command.add( Application.projectManager.getCurrentProject()
			.getProjectLocation() );
		command.add( "-o" );
		command.add( unitName );
		command.add( fileName );

		executeCommand( command );
	}

	@Override
	public void simulate( String fileName, String unitName, String stopTime )
		throws IOException {
		// Build a command
		List<String> command = new ArrayList<String>( 4 );
		command.add( Application.settingsManager.getIcarusSimulatorPath() );
		command.add( unitName );

		simulateInThread( command );
	}
}
