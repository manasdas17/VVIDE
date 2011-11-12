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
package vvide.utils.xmlitems;

import java.util.HashMap;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import vvide.logger.Logger;

/**
 * An XML Item for SplitWindow serialization
 */
public class SplitWindowItem extends AbstractXMLItem {

	/*
	 * =========================== Attributes ================================
	 */
	// Tag Names
	private final String DIVIDER_LOCATION = "DividerLocation";
	private final String IS_HORIZONTAL = "isHorizontal";
	private final String FIRST_WINDOW = "FirstWindow";
	private final String SECOND_WINDOW = "SecondWindow";

	/*
	 * ============================ Methods ==================================
	 */
	@Override
	public Element makeXMLElement( Document document, Element element,
			Object obj ) {
		try {
			SplitWindow splitWindow = (SplitWindow) obj;

			// Splitting type
			Element isHorizontal = document.createElement( IS_HORIZONTAL );
			isHorizontal.setAttribute( typeAttributeName, "Boolean" );
			xmlUtils.getItem( "Boolean" ).makeXMLElement( document,
					isHorizontal, splitWindow.isHorizontal() );
			element.appendChild( isHorizontal );

			// Divider Location
			Element dividerLocation = document.createElement( DIVIDER_LOCATION );
			dividerLocation.setAttribute( typeAttributeName, "Float" );
			xmlUtils.getItem( "Float" ).makeXMLElement( document,
					dividerLocation, splitWindow.getDividerLocation() );
			element.appendChild( dividerLocation );

			// First Window
			Element firstWindow = document.createElement( FIRST_WINDOW );
			String firstWindowType =
					splitWindow.getChildWindow( 0 ).getClass().getSimpleName();
			firstWindow.setAttribute( typeAttributeName, firstWindowType );
			xmlUtils.getItem( firstWindowType ).makeXMLElement( document,
					firstWindow, splitWindow.getChildWindow( 0 ) );
			element.appendChild( firstWindow );

			// Second Window
			Element secondWindow = document.createElement( SECOND_WINDOW );
			String secondWindowType =
					splitWindow.getChildWindow( 1 ).getClass().getSimpleName();
			secondWindow.setAttribute( typeAttributeName, secondWindowType );
			xmlUtils.getItem( secondWindowType ).makeXMLElement( document,
					secondWindow, splitWindow.getChildWindow( 1 ) );
			element.appendChild( secondWindow );

		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
		return element;
	}

	@Override
	public Object parseXMLElement( Element element ) {

		// Map for items
		HashMap<String, Object> parsedItems = getMapFromElement( element );

		return new SplitWindow( (Boolean) parsedItems.get( IS_HORIZONTAL ),
				(Float) parsedItems.get( DIVIDER_LOCATION ),
				(DockingWindow) parsedItems.get( FIRST_WINDOW ),
				(DockingWindow) parsedItems.get( SECOND_WINDOW ) );
	}
}