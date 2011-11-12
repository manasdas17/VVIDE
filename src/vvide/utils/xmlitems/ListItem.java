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

import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import vvide.logger.Logger;

/**
 * An XML Item for java.util.List
 */
public class ListItem extends AbstractXMLItem {

	/*
	 * ============================ Methods ==================================
	 */
	@Override
	public Element makeXMLElement( Document document, Element element,
			Object obj ) {
		// getting a list
		List<?> list = (List<?>) obj;
		// save each element of the list
		for (Object item : list) {
			try {
				element.appendChild( xmlUtils.getElementFor( item, document ) );
			}
			catch ( Exception e ) {
				Logger.logError( this, e );
			}
		}
		return element;
	}

	@Override
	public Object parseXMLElement( Element element ) {
		//Getting subelements
		Vector<Element> elementNodeList = xmlUtils.getElementNodeList( element.getChildNodes() );
		Vector<Object> list = new Vector<Object>(elementNodeList.size());
		
		//fill the vector
		for (Element nextElement : elementNodeList) {
			list.add( xmlUtils.getItem( nextElement.getAttribute( typeAttributeName ) ).parseXMLElement( nextElement ) );
		}
		
		return (List<?>)list;
	}
}
