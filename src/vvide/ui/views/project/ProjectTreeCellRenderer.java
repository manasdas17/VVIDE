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

package vvide.ui.views.project;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import vvide.project.AbstractSourceFile;
import vvide.project.Project;

/**
 * Render for a Project Tree
 */
public class ProjectTreeCellRenderer extends DefaultTreeCellRenderer {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Version UID
	 */
	private static final long serialVersionUID = 7777810539500025294L;
	/**
	 * Project
	 */
	private Project project;

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Constructor
	 * 
	 * @param project
	 *        project to display the tree
	 */
	public ProjectTreeCellRenderer( Project project ) {
		this.project = project;
	}

	@Override
	public Component getTreeCellRendererComponent( JTree tree, Object value,
		boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus ) {

		super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf,
			row, hasFocus );
		if ( project != null) {
			try {
				if (((DefaultMutableTreeNode) value).getUserObject() instanceof AbstractSourceFile) {
					AbstractSourceFile file =  (AbstractSourceFile) ((DefaultMutableTreeNode) value).getUserObject();
					if (file.getFileName().equals(
						project.getTopLevelEntityFileName() ) ) {
						this.setText( "<html><b>" + getText() + "</b></html>" );
					}
				}
			} catch (Exception e) {
				// for the error case, that value is not a tree node
			}
		}
		return this;
	}

}
