package au.org.ala.delta.editor;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.ReorderableList;
import au.org.ala.delta.editor.ui.dnd.SimpleTransferHandler;
import au.org.ala.delta.editor.ui.util.MenuBuilder;
import au.org.ala.delta.editor.ui.util.PopupMenuListener;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;

/**
 * Handles actions performed on the Characters.
 */
public class CharacterController {

	private ReorderableList<Character> _view;
	private EditorViewModel _model;
	private ApplicationContext _context;
	private ActionMap _characterActions;
	private au.org.ala.delta.editor.ui.util.MessageDialogHelper _dialogHelper;
	
	/**
	 * Creates a new ItemController.
	 * @param view the view of the Items.
	 * @param model the model containing Item data.
	 */
	public CharacterController(ReorderableList<Character> view, EditorViewModel model) {
		_view = view;
		_model = model;
		_context = Application.getInstance().getContext();
		JComponent viewComponent = (JComponent)_view;
		
		viewComponent.setTransferHandler(new CharacterTransferHandler());
		_characterActions = _context.getActionMap(CharacterController.class, this);
		_view.setSelectionAction(_characterActions.get("editItem"));
		_dialogHelper = new au.org.ala.delta.editor.ui.util.MessageDialogHelper();
		
		new PopupBuilder();
	}
	
	/**
	 * Builds the Item-related popup menu based on the current selection.
	 * @return a JPopupMenu configured for the current selection.
	 */
	public JPopupMenu buildPopup() {
		JPopupMenu popup = new JPopupMenu();
		
		String[] characterPopupActions;
		if (_model.getMaximumNumberOfItems() == 0) {
			characterPopupActions = new String[] {"addCharacter", "-", "cancel"};
		}
		else {
		    characterPopupActions = new String[] {"editCharacter", "addCharacter", "insertCharacter", "deleteCharacter", "-", "cancel"};
		}
		MenuBuilder.buildMenu(popup, characterPopupActions, _characterActions);
		
		return popup;
	}
	
	/**
	 * Adds a new Item to the dataset after the last existing one.
	 */
	@Action
	public void addCharacter(ActionEvent e) {
		Character newItem = _model.addCharacter(CharacterType.UnorderedMultiState);
		
		editNewCharacter(newItem, e);
	}
	
	/**
	 * Deletes the currently selected Character.
	 */
	@Action
	public void deleteCharacter() {
		
		Character toDelete = _view.getSelected();
		
		if (toDelete == null) {
			return;
		}
		
		int itemNum = toDelete.getCharacterId();
		if (confirmDelete(toDelete)) {
			_model.deleteCharacter(toDelete);
		}
		updateSelection(itemNum);
	}
	
	/**
	 * Displays the Item editor for the currently selected Item.
	 */
	@Action
	public void editItem(ActionEvent e) {
		_context.getActionMap().get("viewTaxonEditor").actionPerformed(e);
		
	}
	
	@Action
	public void insertCharacter() {
		Character selectedItem = _view.getSelected();
		int characterNumber = 1;
		if (selectedItem != null) {
			characterNumber = selectedItem.getCharacterId();
		}
		Character newItem = _model.addCharacter(characterNumber, CharacterType.UnorderedMultiState);
		
		editNewCharacter(newItem, new ActionEvent(_view, -1, ""));
	}
	
	@Action
	public void cancel() {
		// Do nothing.
	}
	
	public void editNewCharacter(Character newItem, ActionEvent e) {
		int selectedItem = newItem.getCharacterId();
		_view.setSelectedIndex(selectedItem-1);
		
		editItem(e);
	}
	
	private void updateSelection(int itemNum) {
		if (itemNum > _model.getMaximumNumberOfItems()) {
			itemNum = _model.getMaximumNumberOfItems();
		}
		if (itemNum > 0) {
			_view.setSelectedIndex(itemNum-1);
		}
	}
	
	public void moveCharacter(Character character, int newIndex) {
		_model.moveCharacter(character, newIndex+1);
		_view.setSelectedIndex(newIndex);
	}
	
	public void copyCharacter(Character character, int copyLocation) {
		
		Character newItem = _model.addCharacter(copyLocation+1, character.getCharacterType());
		newItem.setDescription("Copy of " +character.getDescription());
	}
	
	private boolean confirmDelete(Character toDelete) {
		CharacterFormatter formatter = new CharacterFormatter(true, false, false, true);
		
		String itemDescription = formatter.formatCharacterDescription(toDelete);
		
		return _dialogHelper.confirmDeleteItem(itemDescription);
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
			return _view.getSelected();
		}
		
		@Override
		protected int getStartIndex() {
			int startIndex = 0;
			Character selected = _view.getSelected();
			if (selected != null) {
				startIndex = selected.getCharacterId()-1;
			}
			return startIndex;
		}
		
		@Override
		protected int getDropLocationIndex() {
			return _view.getDropLocationIndex();
		}

		@Override
		protected void move(Character item, int targetIndex) {
			CharacterController.this.moveCharacter(item, targetIndex);
		}

		@Override
		protected void copy(Character item, int targetIndex) {
			CharacterController.this.copyCharacter(item, targetIndex);
		}
	}
	
	class PopupBuilder extends PopupMenuListener {
		public PopupBuilder() {
			super(null, (JComponent)_view);
		}
		
		@Override
		protected JPopupMenu getPopup() {
			return buildPopup();
		}
		
	}
}
