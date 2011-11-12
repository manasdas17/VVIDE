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

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * XML Item for Float 
 */
public class FloatItem extends AbstractXMLItem {

	/*
	 * ============================ Methods ==================================
	 */
	@Override
	public Element makeXMLElement( Document document, Element element,
			Object obj ) {
		element.appendChild( document.createTextNode( obj.toString() ) );
		return element;
	}

	@Override
	public Object parseXMLElement( Element element ) {
		return Float.parseFloat(xmlUtils.getNodeValue( element ));
	}
}
