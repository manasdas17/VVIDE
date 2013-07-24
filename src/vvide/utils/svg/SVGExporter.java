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
package vvide.utils.svg;

import java.io.File;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import vvide.Application;
import vvide.logger.Logger;
import vvide.signal.SignalRender;
import vvide.signal.VisibleSignal;
import vvide.ui.views.WaveView;
import vvide.utils.XMLUtils;

/**
 * An Exporter the signals to the SVG File
 */
public class SVGExporter {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Signals to export
	 */
	private final Vector<VisibleSignal> exportedSignals;
	/**
	 * Start position
	 */
	private final long startPosition;
	/**
	 * Stop position
	 */
	private final long stopPosition;
	/**
	 * XML Document Factory
	 */
	private DocumentBuilderFactory builderFactory;
	/**
	 * XML Document Builder
	 */
	private DocumentBuilder docBuilder;
	/**
	 * XML Document
	 */
	private Document document;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param vector
	 *        array with signals to export
	 * @param startPosition
	 *        start position to export
	 * @param stopPosition
	 *        stop position
	 */
	public SVGExporter( Vector<VisibleSignal> vector, long startPosition,
		long stopPosition ) {
		this.exportedSignals = vector;
		this.startPosition = startPosition;
		this.stopPosition = stopPosition;
		try {
			builderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = builderFactory.newDocumentBuilder();
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
	}

	/**
	 * Export the signals to the specified file
	 * 
	 * @param fileName
	 *        name of the file to export in
	 */
	public void exportToFile( String fileName ) {
		try {
			// Making an XML Element
			document = docBuilder.newDocument();

			// Fill the xml tree
			SignalRender render = new SignalRender();
			render.reloadSettings( (WaveView) Application.viewManager
				.getView( WaveView.WAVE_VIEW_ID ) );
			SVGRenderBackend backend = new SVGRenderBackend( document );
			render.setBackend( backend );

			// Adding signals
			render.drawSignals( exportedSignals, startPosition, stopPosition );

			// Write to the file
			new XMLUtils().transformAndSave( document, new File( fileName ) );
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
	}

}
