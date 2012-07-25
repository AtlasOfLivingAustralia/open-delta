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
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Handles actions performed on Character states.
 */
public class StateController {

	private ReorderableList _view;
	private EditorViewModel _model;
	private au.org.ala.delta.editor.ui.util.MessageDialogHelper _dialogHelper;
	
	/**
	 * Creates a new StateController.
	 * @param view the view of the Character states.
	 * @param model the model containing Character and state data.
	 */
	public StateController(ReorderableList view, EditorViewModel model) {
		_view = view;
		setModel(model);
		JComponent viewComponent = _view.getListViewComponent();
		
		viewComponent.setTransferHandler(new StateTransferHandler());
		_dialogHelper = new au.org.ala.delta.editor.ui.util.MessageDialogHelper();
	}
	
	public void setModel(EditorViewModel model) {
		_model = model;
	}
	
	/**
	 * Adds a new state to the currently selected Character at the position of the current selection.
	 */
	@Action
	public void addState(ActionEvent e) {
		MultiStateCharacter character = getSelectedCharacter();
		int selectedIndex = _view.getSelectedIndex();
		character.addState(selectedIndex + 1);	// addState expects a state number
		
		// We want to change the selected index to something other than the previously selected index to force a refresh
		// If we don't do this then the state editor will not know that the actual selection has changed
		if (selectedIndex > 0) { 
			updateSelection(1);		// If the selected state is not the first state, selected the first state
		} else {
			// Otherwise select the second state
			if (getSelectedCharacter().getNumberOfStates() > 1) {
				updateSelection(2);
			}
		}
		
		// Then finally reselected the new selected state.
		updateSelection(selectedIndex + 1);
	}
	
	/**
	 * Deletes the currently selected state.
	 */
	@Action
	public void deleteState() {
		
		MultiStateCharacter character = getSelectedCharacter();
		
		int stateNumToDelete = _view.getSelectedIndex()+1;
		
		if (stateNumToDelete <= 0) {
			return;
		}
		
		if (confirmDelete(stateNumToDelete)) {
			_model.deleteState(character, stateNumToDelete);
		}
		updateSelection(stateNumToDelete);
	}
	
	private void updateSelection(int stateNum) {
		_view.setSelectedIndex(stateNum-1);	
	}
	
	public void moveState(int stateNum, int newIndex) {
		getSelectedCharacter().moveState(stateNum, newIndex+1);
		_view.setSelectedIndex(newIndex);
	}
	
	/**
	 * Toggles the "implicit" property of the current state.  Only one state may be implicit
	 * in a multistate character.
	 */
	@Action
	public void toggleStateImplicit() {
		int state = _view.getSelectedIndex()+1;
		MultiStateCharacter character = getSelectedCharacter();
		if (character.getUncodedImplicitState() == state) {
			character.clearUncodedImplicitState();
		}
		else {
			character.setUncodedImplicitState(_view.getSelectedIndex()+1);
		}
	}
	
	private boolean confirmDelete(int toDelete) {
		CharacterFormatter formatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false);
		
		String itemDescription = formatter.formatState(getSelectedCharacter(), toDelete);
		
		return _dialogHelper.confirmDeleteState(itemDescription);
	}
	
	private MultiStateCharacter getSelectedCharacter() {
		return (MultiStateCharacter)_model.getSelectedCharacter();
	}
	
	/**
	 * Handles drag and drop of States.
	 */
	class StateTransferHandler extends SimpleTransferHandler<Integer> {
		
		private static final long serialVersionUID = 889705892088002277L;
		
		public StateTransferHandler() {
			super(Integer.class);
		}
		
		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		@Override
		protected Integer getTransferObject() {
			return _view.getSelectedIndex()+1;
		}
		
		@Override
		protected int getStartIndex() {
			return _view.getSelectedIndex();
		}
		
		@Override
		protected int getDropLocationIndex(DropLocation dropLocation) {
			return _view.getDropLocationIndex(dropLocation);
		}

		@Override
		protected void move(Integer stateNum, int targetIndex) {
			StateController.this.moveState(stateNum, targetIndex);
		}

		@Override
		protected void copy(Integer stateNum, int targetIndex) {
			throw new UnsupportedOperationException();
		}
	}
}
