package au.org.ala.delta.editor;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.ReorderableList;
import au.org.ala.delta.editor.ui.dnd.SimpleTransferHandler;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;

/**
 * Handles actions performed on the Characters.
 */
public class StateController {

	private ReorderableList _view;
	private EditorViewModel _model;
	private ApplicationContext _context;
	private ActionMap _stateActions;
	private au.org.ala.delta.editor.ui.util.MessageDialogHelper _dialogHelper;
	
	/**
	 * Creates a new ItemController.
	 * @param view the view of the Items.
	 * @param model the model containing Item data.
	 */
	public StateController(ReorderableList view, EditorViewModel model) {
		_view = view;
		setModel(model);
		_context = Application.getInstance().getContext();
		JComponent viewComponent = (JComponent)_view;
		
		viewComponent.setTransferHandler(new StateTransferHandler());
		_stateActions = _context.getActionMap(StateController.class, this);
		_view.setSelectionAction(_stateActions.get("editCharacter"));
		_dialogHelper = new au.org.ala.delta.editor.ui.util.MessageDialogHelper();
	}
	
	public void setModel(EditorViewModel model) {
		_model = model;
	}
	
	/**
	 * Adds a new Item to the dataset after the last existing one.
	 */
	@Action
	public void addState(ActionEvent e) {
		
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
	
	@Action
	public void insertState() {
		
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
		getSelectedCharacter().setUncodedImplicitState(_view.getSelectedIndex()+1);
	}
	
	private boolean confirmDelete(int toDelete) {
		CharacterFormatter formatter = new CharacterFormatter(true, false, false, true);
		
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
