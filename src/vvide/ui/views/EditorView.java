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
package vvide.ui.views;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;
import org.fife.ui.rtextarea.RTextScrollPane;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;

import vvide.Application;
import vvide.logger.Logger;
import vvide.project.AbstractFile;
import vvide.ui.AbstractView;

/**
 * A view for a source editor
 */
public class EditorView extends AbstractView {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = -1282024380933024851L;
	/**
	 * Code editor for the file
	 */
	private RSyntaxTextArea codeEditor;
	/**
	 * A file opened in the editor
	 */
	private AbstractFile editableFile;
	/**
	 * A Listener for the focus
	 */
	private EditorViewWindowListener windowListener =
			new EditorViewWindowListener();
	/**
	 * A Flag to show that the file is changed
	 */
	private boolean isChanged;
	/**
	 * Scrollpane
	 */
	private RTextScrollPane scrollPane;
	/**
	 * Spell Checker
	 */
	private SpellingParser spellCheckParser;
	/**
	 * Listener to the file properties
	 */
	private FileNameListener fileNameListener = new FileNameListener();

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for codeEditor
	 * 
	 * @return the codeEditor
	 */
	public RSyntaxTextArea getCodeEditor() {
		return codeEditor;
	}

	/**
	 * Getter for editableFile
	 * 
	 * @return the editableFile
	 */
	public AbstractFile getEditableFile() {
		return editableFile;
	}

	/**
	 * Setter for isChanged
	 * 
	 * @param isChanged
	 *        the isChanged to set
	 */
	public void setChanged( boolean changed ) {
		if ( isChanged != changed ) {
			// Change status
			this.isChanged = changed;
			updateTitle();
		}
	}

	/**
	 * Getter for isChanged
	 * 
	 * @return the isChanged
	 */
	public boolean isChanged() {
		return isChanged;
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param file
	 *        File to edit
	 * @param hashName
	 *        hash name of the view
	 * @throws IOException
	 *         if there is an error loading the file content
	 */
	public EditorView( AbstractFile file, int id ) throws IOException {

		super( file.getFileName(), null, id );
		thisView = this;

		// Make UI
		codeEditor = new RSyntaxTextArea();
		LanguageSupportFactory.get().register( codeEditor );
		codeEditor.setSyntaxEditingStyle( file.getContentType() );
		codeEditor.setFont( Application.settingsManager.getEditorFont() );
		codeEditor.setMarkOccurrences( true );
		codeEditor.setAntiAliasingEnabled( true );

		scrollPane = new RTextScrollPane( codeEditor, true );
		ErrorStrip es = new ErrorStrip( codeEditor );
		es.setFollowCaret( true );
		es.setLevelThreshold( ParserNotice.INFO );

		JPanel contentPanel = new JPanel( new BorderLayout() );
		contentPanel.add( scrollPane, BorderLayout.CENTER );
		contentPanel.add( es, BorderLayout.LINE_END );

		setComponent( contentPanel );

		// Save the file reference
		editableFile = file;

		// Load file source
		codeEditor.setText( file.getContent() );
		codeEditor.setCaretPosition( 0 );
		codeEditor.discardAllEdits();

		codeEditor.getDocument().addDocumentListener( new DocumentListener() {

			@Override
			public void removeUpdate( DocumentEvent e ) {
				setChanged( true );
			}

			@Override
			public void insertUpdate( DocumentEvent e ) {
				setChanged( true );
			}

			@Override
			public void changedUpdate( DocumentEvent e ) {
				setChanged( true );
			}
		} );

		editableFile.addPropertyChangeListener( AbstractFile.FILE_NAME,
				fileNameListener );

		// Add listeners
		addListener( windowListener );
		
		new Thread() {
			public void run() {
				spellCheckParser = createSpellingParser();
				if ( spellCheckParser != null ) {
					try {
						spellCheckParser.setUserDictionary( new File(
								Application.settingsManager
										.getUserDictionaryPath() ) );
					}
					catch ( IOException e ) {
						Logger.logError( this, e );
					}
					SwingUtilities.invokeLater( new Runnable() {

						public void run() {
							codeEditor.addParser( spellCheckParser );
						}
					} );
				}
			}
		}.start();	
	}

	/**
	 * Update the tab title
	 */
	private void updateTitle() {
		getViewProperties().setTitle(
				((isChanged) ? "*" : "") + editableFile.getFileName() );
	}

	/**
	 * Create an English spellchecker
	 * 
	 * @return SpellChecker
	 */
	private SpellingParser createSpellingParser() {
		try {
			return SpellingParser.createEnglishSpellingParser( new File(
					Application.settingsManager.getEnglishDictionaryPath() ),
					true );
		}
		catch ( IOException ioe ) {
			ioe.printStackTrace();
		}
		return null;
	}

	/*
	 * ======================= Internal Classes ==============================
	 */
	/**
	 * Window listener for a view<br>
	 * change some actions settings
	 */
	private class EditorViewWindowListener extends DockingWindowAdapter {

		@Override
		public void viewFocusChanged( View oldeView, View newView ) {
			// focus gained
			if ( newView == thisView ) {
				Application.projectManager.setCurrentFile( editableFile );
			}
		}

		@Override
		public void windowClosing( DockingWindow window )
				throws OperationAbortedException {
			if ( isChanged() ) {
				int result =
						JOptionPane
								.showConfirmDialog(
										Application.mainFrame,
										"The file "
											+ editableFile.getFileName()
											+ " is changed.\nSave changes before closing?",
										Application.programName,
										JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE );
				switch ( result ) {
				case JOptionPane.YES_OPTION:
					Application.actionManager.getAction( "SaveFileAction" )
							.actionPerformed( null );
					break;
				case JOptionPane.CANCEL_OPTION:
					throw new OperationAbortedException();
				}
			}
		}

		@Override
		public void windowClosed( DockingWindow window ) {
			super.windowClosed( window );
			Application.projectManager.setCurrentFile( null );
			editableFile.removePropertyChangeListener( AbstractFile.FILE_NAME,
					fileNameListener );
			Application.viewManager.removeEditorForFile(editableFile.getFileName());
		}
	}

	/**
	 * Listener to get a file name changing
	 */
	private class FileNameListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			updateTitle();
		}

	}
}
