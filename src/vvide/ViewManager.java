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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.swing.JOptionPane;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.ViewMap;
import vvide.logger.Logger;
import vvide.project.AbstractFile;
import vvide.ui.AbstractView;
import vvide.ui.TransientTabWindow;
import vvide.ui.views.EditorView;
import vvide.ui.views.ProjectView;
import vvide.utils.IOMethods;
import vvide.utils.XMLUtils;

/**
 * A class for manage various vies such as Project, editor etc.
 */
public class ViewManager {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Prefix for editorView
	 */
	private final String EDITOR_PREFIX = "Editor-";
	/**
	 * A viewMap with all views
	 */
	private ViewMap viewMap;
	/**
	 * Current opened views
	 */
	private HashMap<String, EditorView> openedEditors = new HashMap<String, EditorView>();
	/**
	 * An Editor Window
	 */
	private TransientTabWindow editorWindow;
	/**
	 * Start ID for Editors Views
	 */
	private int nextEditorID = 100;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Getter for viewMap
	 * 
	 * @return the viewMap
	 */
	public ViewMap getViewMap() {
		return viewMap;
	}

	/**
	 * Return an Editor for the specified file or null if no editor founded
	 * 
	 * @param fileToSave
	 *            an Abstract file
	 * @return EditorView for the specified file or null
	 */
	public EditorView getEditorFor(AbstractFile file) {
		return (EditorView) openedEditors.get(EDITOR_PREFIX
				+ file.getFileName());
	}

	/**
	 * Return an specified View
	 * 
	 * @param viewID
	 *            ID of the view
	 * @return a view
	 */
	public AbstractView getView(int viewID) {
		return (AbstractView) viewMap.getView(viewID);
	}

	/**
	 * Add a new View
	 * 
	 * @param viewID
	 *            ID of the view
	 * @param view
	 *            view itself
	 */
	public void addView(int viewID, AbstractView view) {
		viewMap.addView(viewID, view);
	}

	/**
	 * Check if the view is opened
	 * 
	 * @param viewID
	 *            name of the view
	 * @return true, if the view is opened or false if the view not found or not
	 *         opened
	 */
	public boolean isViewOpened(int viewID) {
		return viewMap.getView(viewID).isDisplayable();
	}

	/**
	 * Getter for editorWindow
	 * 
	 * @return the editorWindow
	 */
	public TransientTabWindow getEditorWindow() {
		return editorWindow;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Constructor
	 */
	public ViewManager() {
		viewMap = new ViewMap();
	}

	/**
	 * Init views
	 */
	public void initViews() {
		for (int i = 0; i < viewMap.getViewCount(); ++i) {
			AbstractView view = (AbstractView) viewMap.getView(i);
			if (view != null) view.initView();
		}
	}

	/**
	 * Load a layout from the file
	 */
	public void loadLayout() {
		// Checking the user layout file
		File layoutFile = new File(Application.layoutFileName);
		InputStream layoutStream = null;
		if (!layoutFile.exists()) {
			layoutStream = this.getClass().getResourceAsStream(
					"/res/" + Application.layoutFileName);
		} else {
			try {
				layoutStream = new FileInputStream(layoutFile);
			} catch (FileNotFoundException e) {
			}
		}

		// load a layout
		DockingWindow window = (DockingWindow) new XMLUtils()
				.loadFromXMLStream(layoutStream);

		// Filtering an Editor Window
		editorWindow = findEditorWindow(window);

		Application.mainFrame.getRootWindow().setWindow(window);

		// Focus in Project View
		View projectView = viewMap.getView(ProjectView.PROJECT_VIEW_ID);
		if (projectView == null) return;
		TabWindow parentWindow = (TabWindow) projectView.getWindowParent();
		if (parentWindow != null) {
			int index = parentWindow.getChildWindowIndex(projectView);
			parentWindow.setSelectedTab(index);
			parentWindow.getSelectedWindow().requestFocusInWindow();
		}
	}

	/**
	 * Find a TransientTabWindow that is a Transient and return it
	 * 
	 * @param window
	 *            a start window
	 * @return a founded window
	 */
	private TransientTabWindow findEditorWindow(DockingWindow window) {
		// If it a needed window - return it
		if ((window instanceof TransientTabWindow)
				&& ((TransientTabWindow) window).isTransient())
			return (TransientTabWindow) window;

		// skip simple tab windows
		if (window == null || window instanceof TabWindow)
			return null;

		// continue search in Splitted window
		SplitWindow w = (SplitWindow) window;
		TransientTabWindow foundedWindow = findEditorWindow(w.getChildWindow(0));
			if (foundedWindow != null)
				return foundedWindow;

		return findEditorWindow(w.getChildWindow(1));
	}

	/**
	 * Load a layout from the file
	 */
	public void saveLayout() {

		// Backup the file
		File layoutFile = new File(Application.layoutFileName);
		if (layoutFile.exists()) {
			try {
				IOMethods.backUpFile(layoutFile);
			} catch (IOException e) {
				Logger.logError(this, e);
			}
		}

		// Saving the layout
		layoutFile.delete();
		XMLUtils xmlUtils = new XMLUtils();
		xmlUtils.saveToXMLFile(Application.mainFrame.getRootWindow()
				.getWindow(), layoutFile, false);
	}

	/**
	 * Open a new or focused the existed EditorView for the file
	 * 
	 * @param fileToOpen
	 *            a file to edit
	 */
	public void openEditorView(AbstractFile fileToOpen) {
		openEditorView(new AbstractFile[] { fileToOpen });
	}

	/**
	 * Open a new or focused the existed EditorView for the file
	 * 
	 * @param files
	 *            an array with files to edit
	 */
	public void openEditorView(AbstractFile[] files) {
		// checking for null
		if (files == null)
			return;

		for (AbstractFile file : files) {
			// Check if the view Exists
			EditorView existed = getEditorFor(file);
			if (existed != null) {
				int index = editorWindow.getChildWindowIndex(existed);
				editorWindow.setSelectedTab(index);
				editorWindow.getSelectedWindow().requestFocusInWindow();
				continue;
			}

			try {

				// Add closing listener
				String hashName = EDITOR_PREFIX + file.getFileName();
				EditorView newEditorView = new EditorView(file, nextEditorID++);

				editorWindow.addTab(newEditorView);
				openedEditors.put(hashName, newEditorView);
				editorWindow.setSelectedTab(editorWindow
						.getChildWindowIndex(newEditorView));
				editorWindow.getSelectedWindow().requestFocusInWindow();
			} catch (IOException e) {
				Logger.logError(this, e);
				JOptionPane.showMessageDialog(Application.mainFrame,
						e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE,
						null);
			}

		}
	}

	/**
	 * Close all opened editors
	 * 
	 * @return JOptionPane.CANCEL_OPTION if the user cancel the closing
	 */
	public int closeEditors() {
		String[] keys = openedEditors.keySet().toArray(new String[] {});
		for (String key : keys) {
			if (key.startsWith(EDITOR_PREFIX)) {
				EditorView view = (EditorView) openedEditors.get(key);
				try {
					view.closeWithAbort();
				} catch (OperationAbortedException e) {
					return JOptionPane.CANCEL_OPTION;
				}
			}
		}
		return 0;
	}

	/**
	 * Remove the editor from cache
	 * 
	 * @param fileName
	 *            name of the file opened in the editor
	 */
	public void removeEditorForFile(String fileName) {
		openedEditors.remove(EDITOR_PREFIX + fileName);
	}
}
