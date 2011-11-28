/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.editor;

import java.awt.event.ActionEvent;

import javax.swing.JPopupMenu;

import org.jdesktop.application.Action;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.CharacterTree;
import au.org.ala.delta.editor.ui.ReorderableList;
import au.org.ala.delta.editor.ui.util.MenuBuilder;

public class TreeCharacterController extends CharacterController {
	
	private CharacterTree _tree;

	public TreeCharacterController(ReorderableList view, EditorViewModel model) {
		super(view, model);
		if (view instanceof CharacterTree) {
			_tree = (CharacterTree) view;
		}
	}
	
	/**
	 * Builds the Item-related popup menu based on the current selection.
	 * @return a JPopupMenu configured for the current selection.
	 */
	@Override
	public JPopupMenu buildPopup() {
		JPopupMenu popup = new JPopupMenu();
		
		String[] characterPopupActions;
		if (getModel().getMaximumNumberOfItems() == 0) {
			characterPopupActions = new String[] {"addCharacter", "-", "cancel"};
		}
		else {
		    characterPopupActions = new String[] {"editCharacter", "addCharacter", "insertCharacter", "deleteCharacter", "-", "expandAll", "collapseAll", "-", "cancel"};
		}
		MenuBuilder.buildMenu(popup, characterPopupActions, getCharacterActions());
		
		return popup;
	}
	
	@Action
	public void expandAll(ActionEvent e) {
		if (_tree != null) {
			_tree.expandAll();
		}
	}
	
	@Action
	public void collapseAll(ActionEvent e) {
		if (_tree != null) {
			_tree.collapseAll();
		}
	}

}
