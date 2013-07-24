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
package vvide.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import vvide.Application;
import vvide.ViewManager;
import vvide.ui.DialogResult;
import vvide.ui.SettingsDialog;
import vvide.ui.views.WaveView;

/**
 * Action to show settings dialog
 */
public class ShowSettingsAction extends AbstractAction {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 1343924925353275132L;

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public ShowSettingsAction() {
		super( "Settings" );
		putValue( SHORT_DESCRIPTION, "Open a settings Dialog" );
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		SettingsDialog dialog = new SettingsDialog(Application.uiXmlFile);
		dialog.setVisible( true );

		if ( dialog.getDialogResult() == DialogResult.OK ) {
			dialog.applySettings();
			if ( Application.viewManager.isViewOpened( WaveView.WAVE_VIEW_ID ) ) {
				WaveView waveView =
						((WaveView) Application.viewManager
								.getView( WaveView.WAVE_VIEW_ID ));
				waveView.reloadSettings();
				waveView.renderContent();
			}
		}
	}
}
