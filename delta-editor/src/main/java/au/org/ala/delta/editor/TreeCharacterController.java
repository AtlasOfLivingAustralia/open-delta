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
