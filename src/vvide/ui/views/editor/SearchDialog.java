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
package vvide.ui.views.editor;

import javax.swing.JDialog;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.ButtonGroup;

import vvide.Application;
import vvide.actions.AbstractSearchAction;
import vvide.actions.SearchReplaceAction;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A dialog to make search and replace action in the textEditor
 */
public class SearchDialog extends JDialog {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 543821682199270481L;
	/**
	 * Text box with text to search
	 */
	private JTextField tbxFind;
	/**
	 * TextBox with text to replace
	 */
	private JTextField tbxReplaceWith;
	/**
	 * Direction forward
	 */
	private JRadioButton rdbtnForward;
	/**
	 * Direction backward
	 */
	private JRadioButton rdbtnBackward;
	/**
	 * Option caseSensetive
	 */
	private JCheckBox chckbxCaseSensitive;
	/**
	 * Option WholeWord
	 */
	private JCheckBox chckbxWholeWord;
	/**
	 * Option use Regular Expressions
	 */
	private JCheckBox chckbxRegularExpression;
	/**
	 * Button find
	 */
	private JButton btnFind;
	/**
	 * Button replace
	 */
	private JButton btnReplace;
	/**
	 * Button replace all
	 */
	private JButton btnReplaceAll;
	/**
	 * Button to close dialog
	 */
	private JButton btnCancel;
	/**
	 * Group for directions
	 */
	private final ButtonGroup directionGroup = new ButtonGroup();
	/**
	 * Actions
	 */
	private AbstractSearchAction[] actions = new AbstractSearchAction[] {
			(AbstractSearchAction) Application.actionManager
					.getAction( "FindTextAction" ),
			(AbstractSearchAction) Application.actionManager
					.getAction( "ReplaceTextAction" ),
			(AbstractSearchAction) Application.actionManager
					.getAction( "ReplaceAllTextAction" ), };
	private JLabel lblStatus;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Set the text to search
	 * 
	 * @param selectedText
	 *        text to search
	 */
	public void setSearchText( String selectedText ) {
		tbxFind.setText( selectedText );
	}

	/**
	 * Return the search text
	 * 
	 * @return text to search
	 */
	public String getSearchText() {
		return tbxFind.getText();
	}

	/**
	 * Return the replace text
	 * 
	 * @return text to replace
	 */
	public String getReplaceText() {
		return tbxReplaceWith.getText();
	}

	/**
	 * Return the ForwardDirection Flag
	 * 
	 * @return true, if the direction is forward
	 */
	public boolean isForward() {
		return rdbtnForward.isSelected();
	}

	/**
	 * Return the BackwardDirection Flag
	 * 
	 * @return true, if the direction is backward
	 */
	public boolean isBackward() {
		return rdbtnBackward.isSelected();
	}

	/**
	 * Return the Case sensitive Option flag
	 * 
	 * @return true if "Case sensitive" is selected
	 */
	public boolean isCaseSesitive() {
		return chckbxCaseSensitive.isSelected();
	}

	/**
	 * Return the Whole word Option flag
	 * 
	 * @return true if "Whole word" is selected
	 */
	public boolean isWholeWord() {
		return chckbxWholeWord.isSelected();
	}

	/**
	 * Return the Regular expression Option flag
	 * 
	 * @return true if "Regular expression" is selected
	 */
	public boolean isRegularExpression() {
		return chckbxRegularExpression.isSelected();
	}

	/**
	 * Set the status text
	 * 
	 * @param text
	 *        text to set
	 */
	public void setStatusText( String text ) {
		lblStatus.setText( text );
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public SearchDialog() {
		setTitle( "Find/Replace" );
		setResizable( false );
		getContentPane().setLayout(
				new MigLayout("", "[grow,fill][70.00,grow,fill][grow,fill][grow,fill]", "[][][][][]") );

		JLabel lblFind = new JLabel( "Find:" );
		getContentPane().add( lblFind, "cell 0 0,alignx left" );

		tbxFind = new JTextField();
		getContentPane().add( tbxFind, "cell 1 0 3 1,growx" );
		tbxFind.setColumns( 10 );

		JLabel lblReplaceWith = new JLabel( "Replace with:" );
		getContentPane().add( lblReplaceWith, "cell 0 1,alignx left" );

		tbxReplaceWith = new JTextField();
		getContentPane().add( tbxReplaceWith, "cell 1 1 3 1,growx" );
		tbxReplaceWith.setColumns( 10 );

		JPanel panel = new JPanel();
		panel.setBorder( new TitledBorder( null, "Direction:",
				TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
		getContentPane().add( panel, "cell 0 2 2 1,grow" );
		panel.setLayout( new MigLayout( "", "[]", "[][]" ) );

		rdbtnForward = new JRadioButton( "Forward" );
		rdbtnForward.setSelected( true );
		directionGroup.add( rdbtnForward );
		panel.add( rdbtnForward, "cell 0 0,alignx left,aligny center" );

		rdbtnBackward = new JRadioButton( "Backward" );
		directionGroup.add( rdbtnBackward );
		panel.add( rdbtnBackward, "cell 0 1,alignx left,aligny center" );

		JPanel panel_1 = new JPanel();
		panel_1.setBorder( new TitledBorder( null, "Options:",
				TitledBorder.LEADING, TitledBorder.TOP, null, null ) );
		getContentPane().add( panel_1,
				"cell 2 2 2 1,grow" );
		panel_1.setLayout( new MigLayout( "", "[]", "[][]" ) );

		chckbxCaseSensitive = new JCheckBox( "Case sensitive" );
		panel_1.add( chckbxCaseSensitive, "cell 0 0,alignx left" );

		chckbxWholeWord = new JCheckBox( "Whole word" );
		panel_1.add( chckbxWholeWord, "flowy,cell 0 1,alignx left" );

		chckbxRegularExpression = new JCheckBox( "Regular expression" );
		panel_1.add( chckbxRegularExpression, "cell 0 1,alignx left" );

		btnFind = new JButton( actions[0] );
		getContentPane().add( btnFind, "cell 0 3,growx" );

		btnReplace = new JButton( actions[1] );
		getContentPane().add( btnReplace, "cell 1 3 2 1,growx" );

		btnReplaceAll = new JButton( actions[2] );
		getContentPane().add( btnReplaceAll, "cell 3 3,growx" );

		btnCancel = new JButton( "Cancel" );
		btnCancel.addActionListener( new ActionListener() {

			public void actionPerformed( ActionEvent e ) {
				dispose();
			}
		} );

		lblStatus = new JLabel( "" );
		getContentPane().add( lblStatus, "cell 0 4 3 1,grow" );
		getContentPane().add( btnCancel, "cell 3 4,growx" );

		pack();
		setLocationRelativeTo( Application.mainFrame );
		setAlwaysOnTop( true );
	}

	@Override
	public void addNotify() {
		super.addNotify();

		for ( AbstractSearchAction action : actions ) {
			action.setDialog( this );
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		SearchReplaceAction sraction =
				(SearchReplaceAction) Application.actionManager
						.getAction( "SearchReplaceAction" );
		sraction.closeDialog();
		for ( AbstractSearchAction action : actions ) {
			action.setDialog( null );
		}
	}

}
