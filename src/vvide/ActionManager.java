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
package vvide;

import java.util.HashMap;

import javax.swing.AbstractAction;

import vvide.logger.Logger;
import vvide.utils.XMLUtils;

/**
 * A Manager for all action.<br>
 * Store all actions in the single exemplar.
 */
public class ActionManager {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * A map of all actions
	 */
	private HashMap<String, AbstractAction> actionsMap;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Return an instance of the action
	 * 
	 * @return a BaseAction Object or null if no action found
	 */
	public AbstractAction getAction( String actionName ) {
		AbstractAction action = actionsMap.get( actionName );
		if ( action == null ) {
			Logger.logError( "ActionManager.getAction()", "Action nof found: "
				+ actionName );
		}
		return action;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param managers
	 */
	@SuppressWarnings( "unchecked" )
	public ActionManager() {

		// Load all actions
		XMLUtils utils = new XMLUtils();
		actionsMap =
				(HashMap<String, AbstractAction>) utils
						.loadFromXMLStream( getClass().getResourceAsStream(
								"/res/action_map.xml" ) );
	}
}
