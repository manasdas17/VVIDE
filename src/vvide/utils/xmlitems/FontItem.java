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

import java.awt.Font;
import java.util.HashMap;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * An XML Element for Font
 */
public class FontItem extends AbstractXMLItem {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Tag for Family name
	 */
	protected final String TAG_FAMILY_NAME = "FamilyName";
	/**
	 * Tag for style
	 */
	protected final String TAG_STYLE = "Style";
	/**
	 * Tag for size
	 */
	protected final String TAG_SIZE = "Size";

	/*
	 * ============================ Methods ==================================
	 */
	@Override
	public Element makeXMLElement( Document document, Element element,
			Object obj ) {
		Font font = (Font) obj;
		addChild( document, element, TAG_FAMILY_NAME, font
				.getFamily( Locale.ENGLISH ) );
		addChild( document, element, TAG_SIZE, font.getSize() );
		addChild( document, element, TAG_STYLE, font.getStyle() );
		return element;
	}

	@Override
	public Object parseXMLElement( Element element ) {
		HashMap<String, Object> paramsMap = getMapFromElement( element );

		return new Font( (String) paramsMap.get( TAG_FAMILY_NAME ),
				(Integer) paramsMap.get( TAG_STYLE ), (Integer) paramsMap
						.get( TAG_SIZE ) );
	}
}
