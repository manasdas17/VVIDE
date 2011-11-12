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
package vvide.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import vvide.Application;
import vvide.parser.AbstractParser;
import vvide.parser.VCDParser;

/**
 * A Dialog for the VCD File parsing progress
 */
public class ParsingProgressDialog extends JDialog {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 4677286288714332010L;
	/**
	 * A Progress Bar
	 */
	private JProgressBar progress;
	/**
	 * A Label For the current operation
	 */
	private JLabel lblOperation;
	/**
	 * Current parser
	 */
	private VCDParser vcdParser;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Set the working parser
	 * 
	 * @param parser
	 *        a parser to set
	 */
	public void setParser( VCDParser parser ) {
		this.vcdParser = parser;
		// Adding listeners
		parser.addPropertyChangeListener( AbstractParser.CURRENT_OPERATION,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						SwingUtilities.invokeLater( new SetOperationText(
								(String) evt.getNewValue() ) );
					}
				} );
		parser.addPropertyChangeListener( AbstractParser.PROGRESS,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						SwingUtilities.invokeLater( new SetProgress(
								(Integer) evt.getNewValue() ) );
					}
				} );
		parser.addPropertyChangeListener( AbstractParser.FINISHED,
				new PropertyChangeListener() {

					@Override
					public void propertyChange( PropertyChangeEvent evt ) {
						vcdParser = null;
						dispose();
					}
				} );
	}

	/*
	 * ============================ Methods ==================================
	 */
	public ParsingProgressDialog() {

		// Make UI
		getContentPane().setLayout(
				new MigLayout( "", "[grow]", "[][30px:n][]" ) );

		lblOperation = new JLabel( "" );
		getContentPane().add( lblOperation, "cell 0 0" );

		progress = new JProgressBar();
		progress.setStringPainted( true );
		getContentPane().add( progress, "cell 0 1,growx,aligny center" );

		JButton btnCancel = new JButton( "Cancel" );
		getContentPane().add( btnCancel, "cell 0 2,alignx center" );

		setSize( 450, 125 );
		setLocationRelativeTo( Application.mainFrame );
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setTitle( Application.programName );
		
		btnCancel.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed( ActionEvent e ) {
				if (vcdParser != null) {
					vcdParser.stopParse();
					dispose();
				}
			}
		});
	}

	/*
	 * ======================= Internal Classes ==============================
	 */
	/**
	 * Class to show the operation text
	 */
	private class SetOperationText implements Runnable {

		/**
		 * Operation name
		 */
		private String operationName;

		/**
		 * Constructor
		 */
		public SetOperationText( String operationName ) {
			super();
			this.operationName = operationName;
		}

		@Override
		public void run() {
			progress.setValue( 0 );
			lblOperation.setText( operationName );
		}

	}

	/**
	 * Class to change the progress
	 */
	private class SetProgress implements Runnable {

		/**
		 * Progress
		 */
		private int progressValue;

		/**
		 * Constructor
		 */
		public SetProgress( int progressValue ) {
			super();
			this.progressValue = progressValue;
		}

		@Override
		public void run() {
			progress.setValue( progressValue );
		}

	}
}
