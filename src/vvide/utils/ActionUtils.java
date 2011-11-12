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
package vvide.utils;

import java.io.IOException;

import vvide.Application;
import vvide.ViewManager;
import vvide.logger.Logger;
import vvide.project.AbstractSourceFile;
import vvide.simulator.AbstractSimulator;
import vvide.ui.views.ConsoleView;

/**
 * Common methods used in Actions
 */
public class ActionUtils {

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Compile the file
	 * 
	 * @param sourceFile
	 *        file to compile
	 * @throws IOException 
	 */
	public static void compileFile( AbstractSourceFile sourceFile,
			AbstractSimulator simulator ) throws IOException {
		// copy file to the temporary folder
		IOMethods.copyFileToTempFolder( sourceFile );

		// Getting a Top-Level module in this file
		try {
			String topLevelModule =
					simulator.getTopLevelModuleName( sourceFile.getFileName() );
			if ( topLevelModule == null ) {
				Logger.logError( "ActionUtils.compileFile",
						"No Top-Level Module found!" );
				return;
			}
			// Notify start
			((ConsoleView) Application.viewManager
					.getView( ViewManager.CONSOLE_VIEW_ID ))
					.appendNormalText( "Start compiling the file "
						+ sourceFile.getFileName() );

			simulator.analyzeFile( sourceFile.getFileName() );
			simulator.compile( sourceFile.getFileName(), topLevelModule );

			CommonMethods.printInConsole( simulator );

			((ConsoleView) Application.viewManager
					.getView( ViewManager.CONSOLE_VIEW_ID ))
					.appendNormalText( "Stop compiling the file "
						+ sourceFile.getFileName() );
		}
		catch ( IOException e ) {
			Logger.logError( "ActionUtils.compileFile", e );
			CommonMethods.showErrorMessage( e.getMessage() );
			CommonMethods.printInConsole( simulator );
			throw e;
		}

	}
}
