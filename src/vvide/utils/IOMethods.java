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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

import vvide.Application;
import vvide.logger.Logger;
import vvide.project.AbstractSourceFile;

/**
 * Help methods for IO
 */
public class IOMethods {

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Copy one file to another
	 * 
	 * @param sourceFile
	 *        file to copy
	 * @param destFile
	 *        new file
	 * @throws IOException
	 */
	public static void copyFile( File sourceFile, File destFile )
		throws IOException {
		if ( !destFile.exists() ) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream( sourceFile ).getChannel();
			destination = new FileOutputStream( destFile ).getChannel();
			destination.transferFrom( source, 0, source.size() );
		}
		finally {
			if ( source != null ) {
				source.close();
			}
			if ( destination != null ) {
				destination.close();
			}
		}
	}

	/**
	 * Make a backup of the file
	 * 
	 * @param file
	 *        a file to backup
	 * @throws IOException
	 */
	public static void backUpFile( File file ) throws IOException {
		File backupFile = new File( file.getCanonicalPath() + ".backup" );
		// Remove old backup
		if ( backupFile.exists() ) {
			backupFile.delete();
		}
		copyFile( file, backupFile );
	}

	/**
	 * Check if the string ends with a separator char, if not add a separator
	 * 
	 * @param path
	 *        a string to correct
	 * @return string with a separator char at the end
	 */
	public static String addSeparator( String path ) {
		// Adding a separator to the end of the path
		if ( !path.endsWith( File.separator ) ) {
			path += File.separator;
		}
		return path;
	}

	/**
	 * Check if the string ends with a file extension, if not add a extension
	 * 
	 * @param fileName
	 *        a string with a file name
	 * @param defaultExtension
	 *        a default extension for the file type
	 * @return a corrected file name
	 */
	public static String addFileExtension( String fileName,
		String defaultExtension ) {
		// if the file name contains no "." adding an extension
		if ( fileName.indexOf( '.' ) == -1 ) {
			fileName += ('.' + defaultExtension);
		}
		return fileName;
	}

	/**
	 * Change absolute path to relative
	 * 
	 * @param projectPath
	 *        path to the project
	 * @param filePath
	 *        path to the file
	 * @return String with relative path from the project path to the file path
	 */
	public static String getRelativePath( String projectPath, String filePath ) {
		return new File( projectPath ).toURI().relativize(
			new File( filePath ).toURI() ).getPath();
	}

	/**
	 * Copy the file to the temporary folder for compiling, simulating etc
	 * 
	 * @param sourceFile
	 *        file to copy
	 */
	public static void copyFileToTempFolder( AbstractSourceFile sourceFile ) {

		// Getting tmp folder and file to copy
		String tmpFolder =
			Application.projectManager.getCurrentProject().getTemporaryFolder();
		File destination = new File( tmpFolder + sourceFile.getFileName() );
		File source = new File( sourceFile.getFullPath() );

		try {
			IOMethods.copyFile( source, destination );
		}
		catch ( IOException e ) {
			Logger.logError( "IOMethods.copyFileToTempFolder", e );
		}
	}

	/**
	 * Load a thext file to the string
	 * 
	 * @param fileName
	 *        name of the file to load
	 * @return String with a text
	 */
	public static String loadTextFile( String fileName ) {
		File file = new File( fileName );
		if ( !file.isFile() ) return null;

		StringBuilder buffer = new StringBuilder( (int) file.length() );
		try {
			BufferedReader reader = new BufferedReader( new FileReader( file ) );
			String line;
			while ( (line = reader.readLine()) != null )
				buffer.append( line ).append( "\n" );
			reader.close();
		}
		catch ( IOException e ) {
			Logger.logError( "IOMethods.loadTextFile()", e );
		}

		return buffer.toString();
	}

}
