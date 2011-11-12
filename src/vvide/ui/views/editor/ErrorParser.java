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

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Element;

import org.fife.rsta.ac.OutputCollector;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;

/**
 * Parser of error messages
 */
public class ErrorParser extends OutputCollector {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Syntax Checker
	 */
	private SyntaxChecker syntaxChecker;
	/**
	 * Result of the parsing
	 */
	private final DefaultParseResult result;
	/**
	 * Root element f the document
	 */
	private final Element root;
	/**
	 * Error parsing pattern
	 */
	private Pattern  errorPattern;
	/**
	 * Name and path to the file
	 */
	private final String fileName;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public ErrorParser( InputStream in, SyntaxChecker syntaxChecker, DefaultParseResult result, Element root, Pattern errorPattern, String fileName ) {
		super( in );
		this.syntaxChecker = syntaxChecker;
		this.result = result;
		this.root = root;
		this.errorPattern = errorPattern;
		this.fileName = fileName;
	}
	
	protected void handleLineRead(String line) throws IOException {

		if (!line.contains( fileName )) return;
		line = line.substring( line.indexOf( fileName ) + fileName.length() );
		Matcher m = errorPattern.matcher(line);

		if (m.find()) {
			int lineNumber = Integer.parseInt(m.group(1)) - 1;
			String errorMessage = m.group(2);
			errorMessage = errorMessage.replaceAll( "<", "&lt;" );
			errorMessage = errorMessage.replaceAll( ">", "&gt;" );
			Element elem = root.getElement(lineNumber);
			int start = elem.getStartOffset();
			int end = elem.getEndOffset();

			DefaultParserNotice pn = new DefaultParserNotice(
					syntaxChecker, errorMessage, lineNumber, start, end-start);

			result.addNotice(pn);

		}
	}
}
