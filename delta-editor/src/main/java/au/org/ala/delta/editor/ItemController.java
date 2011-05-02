package au.org.ala.delta.editor;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import au.org.ala.delta.editor.ui.EditorDataModel;
import au.org.ala.delta.editor.ui.ReorderableItemList;
import au.org.ala.delta.editor.ui.dnd.ItemTransferHandler;
import au.org.ala.delta.editor.ui.util.MenuBuilder;
import au.org.ala.delta.editor.ui.util.PopupMenuListener;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.ui.MessageDialogHelper;

/**
 * Handles actions performed on the ItemList.
 */
public class ItemController {

	private ReorderableItemList _view;
	private EditorDataModel _model;
	private ApplicationContext _context;
	
	public ItemController(ReorderableItemList view, EditorDataModel model) {
		_view = view;
		_model = model;
		_context = Application.getInstance().getContext();
		
		JComponent viewComponent = (JComponent)_view;
		
		viewComponent.setTransferHandler(new ListItemTransferHandler());
		
		new PopupBuilder();
	}
	
	public JPopupMenu buildPopup() {
		JPopupMenu popup = new JPopupMenu();
		ActionMap itemActions = _context.getActionMap(ItemController.class, this);
		
		String[] itemsPopupActions;
		if (_model.getMaximumNumberOfItems() == 0) {
			itemsPopupActions = new String[] {"addItem", "-", "cancel"};
		}
		else {
		    itemsPopupActions = new String[] {"editItem", "addItem", "insertItem", "deleteItem", "-", "cancel"};
		}
		MenuBuilder.buildMenu(popup, itemsPopupActions, itemActions);
		
		return popup;
	}
	
	/**
	 * Adds a new Item to the dataset after the last existing one.
	 */
	@Action
	public void addItem(ActionEvent e) {
		Item newItem = _model.addItem();
		
		editNewItem(newItem, e);
	}
	
	/**
	 * Deletes the currently selected Item.
	 */
	@Action
	public void deleteItem() {
		
		Item toDelete = _view.getSelectedItem();
		
		if (toDelete == null) {
			return;
		}
		
		int itemNum = toDelete.getItemNumber();
		if (confirmDelete(toDelete)) {
			_model.deleteItem(toDelete);
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
	public void insertItem() {
		Item selectedItem = _view.getSelectedItem();
		int itemNumber = 1;
		if (selectedItem != null) {
			itemNumber = selectedItem.getItemNumber();
		}
		Item newItem = _model.addItem(itemNumber);
		
		editNewItem(newItem, new ActionEvent(_view, -1, ""));
	}
	
	@Action
	public void cancel() {
		// Do nothing.
	}
	
	public void editNewItem(Item newItem, ActionEvent e) {
		int selectedItem = newItem.getItemNumber();
		_view.setSelectedItem(selectedItem);
		
		editItem(e);
		
		if (StringUtils.isEmpty(newItem.getDescription())) {
			_model.deleteItem(newItem);
			updateSelection(selectedItem);
		}
	}
	
	private void updateSelection(int itemNum) {
		if (itemNum > _model.getMaximumNumberOfItems()) {
			itemNum = _model.getMaximumNumberOfItems();
		}
		_view.setSelectedItem(itemNum);
	}
	
	public void moveItem(Item item, int newIndex) {
		_model.moveItem(item, newIndex+1);
		_view.setSelectedItem(newIndex+1);
	}
	
	public void copyItem(Item item, int copyLocation) {
		
		Item newItem = _model.addItem(copyLocation+1);
		newItem.setDescription("Copy of " +item.getDescription());
	}
	
	private boolean confirmDelete(Item toDelete) {
		JComponent viewComponent = (JComponent)_view;
		int result = MessageDialogHelper.showConfirmDialog(viewComponent, "CONFIRM", 
				"Please confirm that you really wish to delete this taxon:\n" + toDelete.getDescription(), 50);
		return result == JOptionPane.OK_OPTION;
	}
	
	/**
	 * Handles drag and drop of Items in the ItemList.
	 */
	class ListItemTransferHandler extends ItemTransferHandler {
		
		private static final long serialVersionUID = 889705892088002277L;
		
		@Override
		protected Item getItem() {
			return _view.getSelectedItem();
		}
		
		@Override
		protected int getStartIndex() {
			int startIndex = 0;
			Item selected = _view.getSelectedItem();
			if (selected != null) {
				startIndex = selected.getItemNumber()-1;
			}
			return startIndex;
		}
		
		@Override
		protected int getDropLocationIndex() {
			return _view.getDropLocationIndex();
		}

		@Override
		protected void moveItem(Item item, int targetIndex) {
			ItemController.this.moveItem(item, targetIndex);
		}

		@Override
		protected void copyItem(Item item, int targetIndex) {
			ItemController.this.copyItem(item, targetIndex);
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
