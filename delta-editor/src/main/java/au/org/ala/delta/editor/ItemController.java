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
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Handles actions performed on the ItemList.
 */
public class ItemController {

	private ReorderableList _view;
	private EditorViewModel _model;
	private ApplicationContext _context;
	private ActionMap _itemActions;
	private au.org.ala.delta.editor.ui.util.MessageDialogHelper _dialogHelper;
	
	/**
	 * Creates a new ItemController.
	 * @param view the view of the Items.
	 * @param model the model containing Item data.
	 */
	public ItemController(ReorderableList view, EditorViewModel model) {
		_view = view;
		_model = model;
		_context = Application.getInstance().getContext();
		JComponent viewComponent = _view.getListViewComponent();
		
		viewComponent.setTransferHandler(new ItemTransferHandler());
		_itemActions = _context.getActionMap(ItemController.class, this);
		_view.setSelectionAction(_itemActions.get("editItem"));
		_dialogHelper = new au.org.ala.delta.editor.ui.util.MessageDialogHelper();
		
		new PopupBuilder();
	}
	
	/**
	 * Builds the Item-related popup menu based on the current selection.
	 * @return a JPopupMenu configured for the current selection.
	 */
	public JPopupMenu buildPopup() {
		JPopupMenu popup = new JPopupMenu();
		
		String[] itemsPopupActions;
		if (_model.getMaximumNumberOfItems() == 0) {
			itemsPopupActions = new String[] {"addItem", "-", "cancel"};
		}
		else {
		    itemsPopupActions = new String[] {"editItem", "addItem", "insertItem", "deleteItem", "-", "cancel"};
		}
		MenuBuilder.buildMenu(popup, itemsPopupActions, _itemActions);
		
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
		
		Item toDelete = _model.getItem(_view.getSelectedIndex()+1);
		
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
		if (_model.getSelectedItem() != null) {
			_context.getActionMap().get("viewTaxonEditor").actionPerformed(e);
		}
	}
	
	@Action
	public void insertItem() {
		Item selectedItem = _model.getItem(_view.getSelectedIndex()+1);
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
	
	public void moveItem(Item item, int newIndex) {
		_model.moveItem(item, newIndex+1);
		_view.setSelectedIndex(newIndex);
	}
	
	public void copyItem(Item item, int copyLocation) {
		
		Item newItem = _model.addItem(copyLocation+1);
		newItem.setDescription("Copy of " +item.getDescription());
	}
	
	private boolean confirmDelete(Item toDelete) {
		ItemFormatter formatter = new ItemFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false, false);
		
		String itemDescription = formatter.formatItemDescription(toDelete);
		
		return _dialogHelper.confirmDeleteItem(itemDescription);
	}
	
	
	/**
	 * Handles drag and drop of Items in the ItemList.
	 */
	class ItemTransferHandler extends SimpleTransferHandler<Item> {
		
		private static final long serialVersionUID = 889705892088002277L;
		
		public ItemTransferHandler() {
			super(Item.class);
		}
		
		@Override
		protected Item getTransferObject() {
			return _model.getItem(_view.getSelectedIndex()+1);
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
		protected void move(Item item, int targetIndex) {
			ItemController.this.moveItem(item, targetIndex);
		}

		@Override
		protected void copy(Item item, int targetIndex) {
			ItemController.this.copyItem(item, targetIndex);
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
