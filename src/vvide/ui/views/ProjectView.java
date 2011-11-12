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

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import vvide.Application;
import vvide.ProjectManager;
import vvide.project.AbstractFile;
import vvide.project.AbstractSourceFile;
import vvide.project.Project;
import vvide.ui.AbstractView;
import vvide.ui.views.project.NeverLeafNode;
import vvide.ui.views.project.ProjectTreeCellRenderer;
import vvide.utils.CommonMethods;

/**
 * A panel to control current Project
 */
public class ProjectView extends AbstractView {

	/*
	 * =========================== Properties ================================
	 */
	/**
	 * Name of the project property
	 */
	public static String SELECTED_FILES = "SelectedFiles";
	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 187425854358939476L;
	/**
	 * A tree with the
	 */
	private JTree projectTree;
	/**
	 * Node for source files
	 */
	private DefaultMutableTreeNode sourceNode;
	/**
	 * Node for other files
	 */
	private DefaultMutableTreeNode otherNode;
	/**
	 * Tree model
	 */
	private DefaultTreeModel model;
	/**
	 * Listener for changing current Project
	 */
	private CurrentProjectListener currentProjectListener =
		new CurrentProjectListener();
	/**
	 * Listener for adding a file to the project
	 */
	private FileAddedListener fileAddedListener = new FileAddedListener();
	/**
	 * Listener for adding a file to the project
	 */
	private FileRemovedListener fileRemovedListener = new FileRemovedListener();
	/**
	 * Listener for changing of the selected file
	 */
	private ProjectTreeSelectionListener treeSelectionListener =
		new ProjectTreeSelectionListener();
	/**
	 * Listener for open the file
	 */
	private ProjectTreeMouseListener treeMouseListener =
		new ProjectTreeMouseListener();
	/**
	 * Keyboard listener for the tree
	 */
	private ProjectTreeKeyListener treeKeyListener =
		new ProjectTreeKeyListener();
	/**
	 * FileName Listener
	 */
	private FileNameListener fileNameListener = new FileNameListener();
	/**
	 * ProjectName Listener
	 */
	private ProjectNameListener projectNameListener = new ProjectNameListener();
	/**
	 * TopLevelEntityFileName Listener
	 */
	private TopLevelEntityFileName topEntityListener = new TopLevelEntityFileName();
	/**
	 * A panel with view content
	 */
	private JPanel content;
	/**
	 * Current project
	 */
	private Project currentProject = null;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Filter the files from the all selected items in the ProjectTree
	 * 
	 * @return a vector with selected files
	 */
	public Vector<AbstractFile> getSelectedFiles() {
		// Build the array of selected files
		if ( projectTree.getSelectionPaths() == null ) return null;
		Vector<AbstractFile> selectedFiles = new Vector<AbstractFile>();
		for ( TreePath path : projectTree.getSelectionPaths() ) {
			DefaultMutableTreeNode selectedNode =
				(DefaultMutableTreeNode) path.getLastPathComponent();
			if ( selectedNode.getUserObject() instanceof AbstractFile ) {
				selectedFiles.add( (AbstractFile) selectedNode.getUserObject() );
			}
		}
		return selectedFiles;
	}

	/**
	 * Return the first selected item.
	 * SourceFile and other file aren't included
	 * 
	 * @return the selected item
	 */
	public Object getSelectedItem() {
		Object selectedNode =
			projectTree.getSelectionPath().getLastPathComponent();
		if ( selectedNode == null || selectedNode == sourceNode
			|| selectedNode == otherNode ) return null;
		return ((DefaultMutableTreeNode) selectedNode).getUserObject();
	}

	/**
	 * Find a node for the specified file
	 * 
	 * @param searchNode
	 *        a node to search
	 * @param file
	 *        a file to search
	 * @return DefaultMutableTreeNode for the file or null if no node found
	 */
	private DefaultMutableTreeNode getNodeForFile(
		DefaultMutableTreeNode searchNode, AbstractFile file ) {
		for ( int i = 0; i < searchNode.getChildCount(); ++i ) {
			DefaultMutableTreeNode node =
				(DefaultMutableTreeNode) searchNode.getChildAt( i );
			if ( node.getUserObject() == file ) return node;
		}
		return null;
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public ProjectView( int id ) {

		super( "Project View", null, id );
		thisView = this;

		projectTree = new JTree();
		projectTree.setModel( new DefaultTreeModel( null ) );

		content = new JPanel( new BorderLayout() );
		content.add( new JScrollPane( projectTree ), BorderLayout.CENTER );

		setComponent( content );

		// Add listeners to the project
		Application.projectManager.addPropertyChangeListener(
			ProjectManager.CURRENT_PROJECT, currentProjectListener );

		// Add Tree listeners
		projectTree.addTreeSelectionListener( treeSelectionListener );
		projectTree.addMouseListener( treeMouseListener );
		projectTree.addKeyListener( treeKeyListener );
	}

	@Override
	public void initView() {
		super.initView();
		popupMenu = CommonMethods.getPopUpMenu( "project_view" );
		content.add( CommonMethods.getToolbar( "project_view" ),
			BorderLayout.NORTH );
	}

	/**
	 * Add files to the project tree
	 * 
	 * @param files
	 *        files to add
	 */
	public void addFilesToProjectTree( AbstractFile[] files ) {
		for ( AbstractFile file : files ) {
			file.addPropertyChangeListener( AbstractFile.FILE_NAME,
				fileNameListener );
			DefaultMutableTreeNode node = new DefaultMutableTreeNode( file );
			if ( file.isSourceFile() ) {
				model.insertNodeInto( node, sourceNode, sourceNode
					.getChildCount() );
			}
			else {
				model.insertNodeInto( node, otherNode, otherNode
					.getChildCount() );
			}
		}
	}

	/**
	 * Remove listeners from the project
	 * 
	 * @param project
	 *        a project to remove the listeners
	 */
	private void removeProjectListeners( Project project ) {
		project.removePropertyChangeListener( Project.PROJECT_NAME,
			projectNameListener );
		project.removePropertyChangeListener( Project.FILE_ADDED,
			fileAddedListener );
		project.removePropertyChangeListener( Project.FILE_REMOVED,
			fileRemovedListener );
		project.removePropertyChangeListener( Project.TOP_LEVEL_ENTITY,
			topEntityListener );
	}

	/**
	 * Add listeners from the project
	 * 
	 * @param project
	 *        a project to add the listeners
	 */
	private void addProjectListeners( Project project ) {
		project.addPropertyChangeListener( Project.PROJECT_NAME,
			projectNameListener );
		project.addPropertyChangeListener( Project.FILE_ADDED,
			fileAddedListener );
		project.addPropertyChangeListener( Project.FILE_REMOVED,
			fileRemovedListener );
		project.addPropertyChangeListener( Project.TOP_LEVEL_ENTITY,
			topEntityListener );
	}

	/*
	 * ======================= Internal Classes ==============================
	 */
	/**
	 * A listener for current project changing
	 */
	private class CurrentProjectListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			// removing listener from project
			if ( evt.getOldValue() != null ) {
				removeProjectListeners( (Project) evt.getOldValue() );
			}
			// removing listener from project
			if ( evt.getNewValue() != null ) {
				currentProject = (Project) evt.getNewValue();
				projectTree.setCellRenderer( new ProjectTreeCellRenderer(
					currentProject ) );
				DefaultMutableTreeNode rootNode =
					new DefaultMutableTreeNode( currentProject.getProjectName() );
				rootNode.setUserObject( currentProject );
				sourceNode = new NeverLeafNode( "Source Files" );
				rootNode.add( sourceNode );
				otherNode = new NeverLeafNode( "Other Files" );
				rootNode.add( otherNode );
				model = new DefaultTreeModel( rootNode );
				projectTree.setModel( model );
				addProjectListeners( currentProject );
				addFilesToProjectTree( currentProject.getFiles().toArray(
					new AbstractFile[] {} ) );
			}
			else {
				projectTree.setCellRenderer( new DefaultTreeCellRenderer() );
				projectTree.setModel( new DefaultTreeModel( null ) );
			}
		}
	}

	/**
	 * A listener for adding new files to the project
	 */
	private class FileAddedListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			// Adding files to the model
			AbstractFile[] files = (AbstractFile[]) evt.getNewValue();
			addFilesToProjectTree( files );
		}

	}

	/**
	 * A listener for removing new files to the project
	 */
	private class FileRemovedListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			// Adding files to the model
			AbstractFile[] files = (AbstractFile[]) evt.getOldValue();
			for ( AbstractFile file : files ) {
				DefaultMutableTreeNode fileNode;
				if ( file.isSourceFile() ) {
					fileNode = getNodeForFile( sourceNode, file );
				}
				else {
					fileNode = getNodeForFile( otherNode, file );
				}
				if ( fileNode != null ) {
					file.removePropertyChangeListener( AbstractFile.FILE_NAME,
						fileNameListener );
					model.removeNodeFromParent( fileNode );
				}
			}
		}
	}

	/**
	 * Listener for changing of the selected file in the Project tree
	 */
	private class ProjectTreeSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged( TreeSelectionEvent e ) {
			Vector<AbstractFile> selectedFiles = getSelectedFiles();
			if ( selectedFiles != null && selectedFiles.size() == 0 )
				selectedFiles = null;
			pcs.firePropertyChange( SELECTED_FILES, null, selectedFiles );
		}
	}

	/**
	 * Mouse listener for a tree<br>
	 * Open a File to edit on DoubleClick
	 */
	private class ProjectTreeMouseListener extends MouseAdapter {

		@Override
		public void mousePressed( MouseEvent e ) {
			if ( projectTree.getSelectionCount() <= 1 ) {
				projectTree.setSelectionPath( projectTree.getPathForLocation( e
					.getX(), e.getY() ) );
			}
			CommonMethods.maybeShowPopup( e, popupMenu, projectTree );
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			CommonMethods.maybeShowPopup( e, popupMenu, projectTree );
		}

		@Override
		public void mouseClicked( MouseEvent e ) {
			// Check the doubleclick and that the selected Item is a file
			DefaultMutableTreeNode selectedNode =
				(projectTree.getSelectionPath() == null) ? null
					: (DefaultMutableTreeNode) projectTree.getSelectionPath()
						.getLastPathComponent();
			if ( e.getClickCount() > 1 && selectedNode != null
				&& selectedNode.getUserObject() instanceof AbstractFile ) {
				Application.viewManager
					.openEditorView( (AbstractFile) selectedNode
						.getUserObject() );
			}
		}
	}

	/**
	 * Key Listener for a tree<br>
	 * Enter for a openFile<br>
	 * Del for delete the file<br>
	 * F2 to rename
	 */
	private class ProjectTreeKeyListener extends KeyAdapter {

		@Override
		public void keyPressed( KeyEvent e ) {
			if ( e.getKeyCode() == KeyEvent.VK_F2 ) {
				Application.actionManager.getAction( "RenameAction" )
					.actionPerformed( null );
				return;
			}
			// getting selected files
			Vector<AbstractFile> selectedFiles = getSelectedFiles();

			if ( selectedFiles != null && selectedFiles.size() > 0 ) {
				// Switching the keys
				switch ( e.getKeyCode() ) {

				case KeyEvent.VK_ENTER:
					Application.viewManager.openEditorView( selectedFiles
						.toArray( new AbstractFile[] {} ) );
					break;

				case KeyEvent.VK_DELETE:
					Application.actionManager.getAction( "RemoveFileAction" )
						.actionPerformed( null );
				}
			}
		}

	}

	/**
	 * Listener to get a file name changing
	 */
	private class FileNameListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			DefaultMutableTreeNode fileNode = null;
			AbstractFile file = (AbstractFile) evt.getSource();
			if ( file.isSourceFile() ) {
				fileNode = getNodeForFile( sourceNode, file );
			}
			else {
				fileNode = getNodeForFile( otherNode, file );
			}
			if ( fileNode != null ) {
				model.reload( fileNode );
			}
		}

	}


	/**
	 * Listener to get a file name changing
	 */
	private class TopLevelEntityFileName implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			String oldName = (String) evt.getOldValue();
			String newName = (String) evt.getNewValue();
			
			if (oldName != null) {
				try {
					AbstractSourceFile file = (AbstractSourceFile) currentProject.getFile( oldName );
					DefaultMutableTreeNode node = getNodeForFile( sourceNode, file );
					model.reload( node );
				} catch (Exception e) {
					// for the case, if the top level is not a source file. Then we have a Cast exception
				}
			}
			
			if (newName != null) {
				try {
					AbstractSourceFile file = (AbstractSourceFile) currentProject.getFile( newName );
					DefaultMutableTreeNode node = getNodeForFile( sourceNode, file );
					model.reload( node );
				} catch (Exception e) {
					// for the case, if the top level is not a source file. Then we have a Cast exception
				}
			}
		}

	}
	
	/**
	 * Listener to get a project name changing
	 */
	private class ProjectNameListener implements PropertyChangeListener {

		@Override
		public void propertyChange( PropertyChangeEvent evt ) {
			model.reload( (TreeNode) model.getRoot() );
		}

	}
}
