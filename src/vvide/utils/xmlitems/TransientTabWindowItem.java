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

import java.util.Vector;

import net.infonode.docking.TabWindow;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import vvide.Application;
import vvide.logger.Logger;
import vvide.ui.AbstractView;
import vvide.ui.TransientTabWindow;

/**
 * An XML Item for TabWindow
 */
public class TransientTabWindowItem extends AbstractXMLItem {

	/*
	 * =========================== Attributes ================================
	 */
	// Tag Names
	public final String IS_TRANSIENT = "isTransient";
	public final String VIEW_ID = "WiewID";

	/*
	 * ============================ Methods ==================================
	 */
	@Override
	public Element makeXMLElement( Document document, Element element,
			Object obj ) {
		try {
			TransientTabWindow tabWindow = (TransientTabWindow) obj;

			makeElement( document, element, tabWindow, tabWindow.isTransient() );
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
		return element;
	}

	/**
	 * Make an Element
	 */
	protected void makeElement( Document document, Element element,
			TabWindow tabWindow, Boolean isTransient ) {
		// Transient flag
		Element transientFlag = document.createElement( IS_TRANSIENT );
		transientFlag.setAttribute( typeAttributeName, "Boolean" );
		xmlUtils.getItem( "Boolean" ).makeXMLElement( document, transientFlag,
				isTransient );
		element.appendChild( transientFlag );

		// Save the content
		for ( int i = 0; i < tabWindow.getChildWindowCount(); ++i ) {
			AbstractView view = (AbstractView) tabWindow.getChildWindow( i );
			Element viewElement = document.createElement( VIEW_ID );
			viewElement.setAttribute( typeAttributeName, "Integer" );
			element.appendChild( xmlUtils.getItem( "Integer" ).makeXMLElement(
					document, viewElement, view.getID() ) );
		}
	}

	@Override
	public Object parseXMLElement( Element element ) {
		TransientTabWindow window = new TransientTabWindow();

		// Getting the childs
		Vector<Element> childElements =
				xmlUtils.getElementNodeList( element.getChildNodes() );
		for ( Element nextElement : childElements ) {
			// Filter the transient flag
			if ( nextElement.getTagName().equals( IS_TRANSIENT ) ) {
				window.setTransient( (Boolean) xmlUtils.getItem( "Boolean" )
						.parseXMLElement( nextElement ) );
			}
			else {
				window.addTab( Application.viewManager
						.getView( (Integer) xmlUtils.getItem( "Integer" )
								.parseXMLElement( nextElement ) ) );
			}
		}

		return window;
	}
}
