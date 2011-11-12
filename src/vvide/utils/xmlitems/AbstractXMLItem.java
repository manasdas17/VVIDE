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
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import vvide.logger.Logger;
import vvide.utils.XMLUtils;

/**
 * Abstract XML Item
 */
public abstract class AbstractXMLItem {

	/*
	 * ========================== Attributes =================================
	 */
	/**
	 * Attribute Name for type of the item
	 */
	public final static String typeAttributeName = "type";

	/**
	 * An instance of util class
	 */
	protected XMLUtils xmlUtils;

	/*
	 * ====================== Getters / Setters ==============================
	 */
	/**
	 * Setter for xmlUtils
	 * 
	 * @param xmlUtils
	 *        the xmlUtils to set
	 */
	public void setXmlUtils( XMLUtils xmlUtils ) {
		this.xmlUtils = xmlUtils;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Create an XML Item for a specified Object and save to the specified
	 * Element
	 * 
	 * @param document
	 *        a XML document in which to be save
	 * @param element
	 *        an XML Element in which to be save
	 * @param obj
	 *        an object to save
	 * @return a created XML Element
	 */
	public Element makeXMLElement( Document document, Element element,
			Object obj ) {
		try {
			element.appendChild( xmlUtils.getElementFor( obj, document ) );
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
		return element;
	}

	/**
	 * Return an Object from the xml
	 * 
	 * @param element
	 *        a XML Element to parse
	 * @return an Object parsed from XML Eleemnt
	 */
	public abstract Object parseXMLElement( Element element );

	/**
	 * Parse an xml Element to a hashmap
	 * 
	 * @param element
	 *        element to parse
	 * @return HashMap with parsed items
	 */
	protected HashMap<String, Object> getMapFromElement( Element element ) {
		HashMap<String, Object> parsedItems = new HashMap<String, Object>();

		// Parse the elements
		Vector<Element> elements =
				xmlUtils.getElementNodeList( element.getChildNodes() );
		for ( Element nextElement : elements ) {
			parsedItems.put( nextElement.getTagName(), xmlUtils.getItem(
					nextElement.getAttribute( typeAttributeName ) )
					.parseXMLElement( nextElement ) );
		}
		return parsedItems;
	}

	/**
	 * Add a child element to the specified element
	 * 
	 * @param document
	 *        XML Document
	 * @param parent
	 *        parent element
	 * @param tagName
	 *        tag name for child element
	 * @param obj
	 *        Object to create a child element
	 */
	protected void addChild( Document document, Element parent, String tagName,
			Object obj ) {
		Element element = document.createElement( tagName );
		element.setAttribute( typeAttributeName, obj.getClass().getSimpleName() );
		xmlUtils.getItem( obj.getClass().getSimpleName() ).makeXMLElement(
				document, element, obj );
		parent.appendChild( element );
	}
}
