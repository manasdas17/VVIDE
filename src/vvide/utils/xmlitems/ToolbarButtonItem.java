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

import java.awt.Insets;

import javax.swing.JButton;

import org.w3c.dom.Element;

import vvide.Application;

/**
 * An Item for JButton
 */
public class ToolbarButtonItem extends AbstractXMLItem {

	/*
	 * ============================ Methods ==================================
	 */
	@Override
	public Object parseXMLElement( Element element ) {
		String actionName =
				(String) xmlUtils.getItem( "String" ).parseXMLElement(
						xmlUtils.getElementNodeList( element.getChildNodes() )
								.firstElement() );
		JButton button = new JButton( Application.actionManager.getAction( actionName ) );
		button.setHideActionText( true );
		button.setFocusable(false);
		button.setBorderPainted( false );
		button.setMargin( new Insets( 0, 0, 0, 0 ) );
		
		return button;
	}
}
