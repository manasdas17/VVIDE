/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This file based on the code of WaveForm Viewer project.
 * 
 * Copyright (C) 2010-2011 Department of Digital Technology
 * of the University of Kassel, Germany
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

package vvide.ui;

import javax.swing.JDialog;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import javax.swing.JButton;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTabbedPane;

import org.w3c.dom.Element;

import vvide.Application;
import vvide.SettingsManager;
import vvide.logger.Logger;
import vvide.ui.settingitems.AbstractSettingPanel;
import vvide.utils.CommonMethods;
import vvide.utils.XMLUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Dialog with settings
 */
public class SettingsDialog extends JDialog {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -8671166607358455481L;
	/**
	 * Package with items
	 */
	private static final String ITEM_PACKAGE =
			"vvide.ui.settingitems.";
	/**
	 * Dialog result
	 */
	private DialogResult dialogResult;
	/**
	 * Tab pane with settings
	 */
	private JTabbedPane tabPane;
	/**
	 * Map with setting panels
	 */
	private HashMap<String, AbstractSettingPanel> settingPanels;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for dialogResult
	 * 
	 * @return the dialogResult
	 */
	public DialogResult getDialogResult() {
		return dialogResult;
	}

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Create the dialog.
	 */
	public SettingsDialog() {

		settingPanels = new HashMap<String, AbstractSettingPanel>();

		// Build UI
		setTitle( "Settings" );
		setModalityType( ModalityType.APPLICATION_MODAL );
		setModal( true );
		getContentPane().setLayout( new BorderLayout( 0, 0 ) );

		JPanel panel = new JPanel();
		getContentPane().add( panel, BorderLayout.SOUTH );
		panel.setLayout( new MigLayout( "", "[][grow][right][right]", "[25px]" ) );

		JButton btnLoadDefaults = new JButton( "Load Defaults" );
		btnLoadDefaults.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				loadSettings( new SettingsManager() );
			}
		} );
		panel.add( btnLoadDefaults, "cell 0 0" );

		JButton btnOk = new JButton( "OK" );
		btnOk.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				dialogResult = DialogResult.OK;
				dispose();
			}
		} );
		panel.add( btnOk, "cell 2 0" );

		JButton btnCancel = new JButton( "Cancel" );
		btnCancel.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				dialogResult = DialogResult.CANCEL;
				dispose();
			}
		} );
		panel.add( btnCancel, "cell 3 0" );

		CommonMethods.addEscapeListener( this, btnCancel );

		tabPane = new JTabbedPane( JTabbedPane.TOP );
		getContentPane().add( tabPane, BorderLayout.CENTER );

		// Load settings
		loadSettings( Application.settingsManager );

		pack();
		setLocationRelativeTo( Application.mainFrame );
		setResizable( false );
	}

	/**
	 * Load the settings from the setting Manager
	 * 
	 * @param settingsManager
	 *        a manager to load settings from
	 */
	protected void loadSettings( SettingsManager settingsManager ) {

		// remove tabs
		tabPane.removeAll();
		settingPanels.clear();

		XMLUtils xmlutils = new XMLUtils();
		Element root =
				xmlutils.parseFromXMLStream( getClass().getResourceAsStream(
						"/res/setting_dialog.xml" ) );

		// getting tabs
		int countTabs = Integer.valueOf( root.getAttribute( "items" ) );

		JPanel[] tabPanels = new JPanel[countTabs];
		String[] tabNames = new String[countTabs];

		Vector<Element> tabElements =
				xmlutils.getElementNodeList( root.getChildNodes() );

		for ( Element nextTab : tabElements ) {
			int tabIndex = Integer.valueOf( nextTab.getAttribute( "order" ) );
			tabNames[tabIndex] = nextTab.getAttribute( "title" );

			// Fill the tab
			JPanel tabPanel =
					new JPanel( new MigLayout( "", "[][grow,right]", "" ) );

			// Load the items
			Vector<Element> itemElements =
					xmlutils.getElementNodeList( nextTab.getChildNodes() );
			for ( Element nextItem : itemElements ) {
				String className =
						ITEM_PACKAGE + nextItem.getAttribute( "type" );
				try {
					AbstractSettingPanel itemPanel =
							(AbstractSettingPanel) Class.forName( className )
									.newInstance();
					JLabel desc =
							new JLabel( nextItem.getAttribute( "description" ) );
					itemPanel.setValue( settingsManager.getSetting( nextItem
							.getTagName() ) );
					int index =
							Integer.valueOf( nextItem.getAttribute( "order" ) );
					tabPanel.add( desc, "cell 0 " + index );
					tabPanel.add( itemPanel, "cell 1 " + index );
					settingPanels.put( nextItem.getTagName(), itemPanel );
				}
				catch ( Exception e ) {
					Logger.logError( this, e );
				}
			}

			tabPanels[tabIndex] = tabPanel;
		}

		// Adding tabs to the tabPane
		for ( int i = 0; i < countTabs; ++i ) {
			tabPane.addTab( tabNames[i], tabPanels[i] );
		}
	}

	/**
	 * Apply changes
	 */
	public void applySettings() {
		Iterator<String> iter = settingPanels.keySet().iterator();
		while ( iter.hasNext() ) {
			String settingName = iter.next();
			Application.settingsManager.setSetting( settingName, settingPanels
					.get( settingName ).getValue() );
		}
	}
}
