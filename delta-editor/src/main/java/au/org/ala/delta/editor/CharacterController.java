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

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.ReorderableList;
import au.org.ala.delta.editor.ui.dnd.SimpleTransferHandler;
import au.org.ala.delta.editor.ui.util.MenuBuilder;
import au.org.ala.delta.editor.ui.util.PopupMenuListener;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Handles actions performed on the Characters.
 */
public class CharacterController {

	private ReorderableList _view;
	private EditorViewModel _model;
	private ApplicationContext _context;
	private ActionMap _characterActions;
	private au.org.ala.delta.editor.ui.util.MessageDialogHelper _dialogHelper;
	
	/**
	 * Creates a new CharacterController.
	 * @param view the view of the Characters.
	 * @param model the model containing Character data.
	 */
	public CharacterController(ReorderableList view, EditorViewModel model) {
		_view = view;
		_model = model;
		_context = Application.getInstance().getContext();
		_view.getListViewComponent().setTransferHandler(new CharacterTransferHandler());
		_characterActions = _context.getActionMap(CharacterController.class, this);
		_view.setSelectionAction(_characterActions.get("editCharacter"));
		_dialogHelper = new au.org.ala.delta.editor.ui.util.MessageDialogHelper();
		
		new PopupBuilder();
	}
	
	protected EditorViewModel getModel() {
		return _model;
	}
	
	protected ActionMap getCharacterActions() {
		return _characterActions;
	}
	
	/**
	 * Builds the Character related popup menu based on the current selection.
	 * @return a JPopupMenu configured for the current selection.
	 */
	public JPopupMenu buildPopup() {
		JPopupMenu popup = new JPopupMenu();
		
		String[] characterPopupActions;
		if (_model.getNumberOfCharacters() == 0) {
			characterPopupActions = new String[] {"addCharacter", "-", "cancel"};
		} else {
		    characterPopupActions = new String[] {"editCharacter", "addCharacter", "insertCharacter", "deleteCharacter", "-", "cancel"};
		}
		MenuBuilder.buildMenu(popup, characterPopupActions, _characterActions);
		
		return popup;
	}
	
	/**
	 * Adds a new Character to the dataset after the last existing one.
	 */
	@Action
	public void addCharacter(ActionEvent e) {
		
		Character newCharacter = addCharacter();
		editNewCharacter(newCharacter, e);
	}
	
	/**
	 * Adds a new Character to the data set.
	 * @return the new Character.
	 */
	public Character addCharacter() {
		return _model.addCharacter(CharacterType.Unknown);
	}
	
	/**
	 * Deletes the currently selected Character.
	 */
	@Action
	public void deleteCharacter() {
		
		Character toDelete = _model.getCharacter(_view.getSelectedIndex()+1);
		
		if (toDelete == null) {
			return;
		}
		
		int charNum = toDelete.getCharacterId();
		if (confirmDelete(toDelete)) {
			_model.deleteCharacter(toDelete);
		}
		updateSelection(charNum);
	}
	
	/**
	 * Displays the Character editor for the currently selected Character.
	 */
	@Action
	public void editCharacter(ActionEvent e) {
		_context.getActionMap().get("viewCharacterEditor").actionPerformed(e);
		
	}
	
	@Action
	public void insertCharacter() {
		Character selectedCharacter = _model.getCharacter(_view.getSelectedIndex()+1);
		int characterNumber = 1;
		if (selectedCharacter != null) {
			characterNumber = selectedCharacter.getCharacterId();
		}
		Character newCharacter = _model.addCharacter(characterNumber, CharacterType.Unknown);
		
		editNewCharacter(newCharacter, new ActionEvent(_view, -1, ""));
	}
	
	@Action
	public void cancel() {
		// Do nothing.
	}
	
	public void editNewCharacter(Character newCharacter, ActionEvent e) {
		int selectedCharacter = newCharacter.getCharacterId();
		_view.setSelectedIndex(selectedCharacter-1);
		
		editCharacter(e);
	}
	
	private void updateSelection(int charNum) {
		if (charNum > _model.getNumberOfCharacters()) {
			charNum = _model.getNumberOfCharacters();
		}
		if (charNum > 0) {
			_view.setSelectedIndex(charNum-1);
		}
	}
	
	public void moveCharacter(Character character, int newIndex) {
		_model.moveCharacter(character, newIndex+1);
		_view.setSelectedIndex(newIndex);
	}
	
	public void copyCharacter(Character character, int copyLocation) {
		
		Character newCharacter = _model.addCharacter(copyLocation+1, character.getCharacterType());
		newCharacter.setDescription("Copy of " +character.getDescription());
	}
	
	private boolean confirmDelete(Character toDelete) {
		CharacterFormatter formatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false);
		
		String characterDescription = formatter.formatCharacterDescription(toDelete);
		
		return _dialogHelper.confirmDeleteCharacter(characterDescription);
	}
	
	/**
	 * Changes the type of the currently selected character.
	 * @param type the type to change to.
	 * @return true if the type was changed, false otherwise.
	 */
	public boolean changeCharacterType(CharacterType type) {
		
		Character _selectedCharacter = _model.getSelectedCharacter();
		
		CharacterType existingType = _selectedCharacter.getCharacterType();
		if (type.equals(existingType)) {
			return false;
		}
		
		if (_model.canChangeCharacterType(_selectedCharacter, type)) {			
			_model.changeCharacterType(_selectedCharacter, type);
		}
		else {
			return false;
		}
		return true;
	}
	
	
	/**
	 * Handles drag and drop of Characters.
	 */
	class CharacterTransferHandler extends SimpleTransferHandler<Character> {
		
		private static final long serialVersionUID = 889705892088002277L;
		
		public CharacterTransferHandler() {
			super(Character.class);
		}
		
		@Override
		protected Character getTransferObject() {
			return _model.getCharacter(_view.getSelectedIndex()+1);
		}
		
		@Override
		protected int getStartIndex() {
			int startIndex = 0;
			Character selected = _model.getCharacter(_view.getSelectedIndex()+1);
			if (selected != null) {
				startIndex = selected.getCharacterId()-1;
			}
			return startIndex;
		}
		
		@Override
		protected int getDropLocationIndex(DropLocation dropLocation) {
			return _view.getDropLocationIndex(dropLocation);
		}

		@Override
		protected void move(Character character, int targetIndex) {
			CharacterController.this.moveCharacter(character, targetIndex);
		}

		@Override
		protected void copy(Character character, int targetIndex) {
			CharacterController.this.copyCharacter(character, targetIndex);
		}
	}
	
	class PopupBuilder extends PopupMenuListener {
		public PopupBuilder() {
			super(null, _view.getListViewComponent());
		}
		
		@Override
		protected JPopupMenu getPopup() {
			return buildPopup();
		}
		
	}
}
