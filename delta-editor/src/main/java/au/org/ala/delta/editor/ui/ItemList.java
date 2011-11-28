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
package au.org.ala.delta.editor.ui;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;

import org.jdesktop.application.Application;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.SearchDirection;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.ui.GenericSearchController;
import au.org.ala.delta.ui.GenericSearchPredicate;
import au.org.ala.delta.ui.SearchDialog;
import au.org.ala.delta.ui.SearchOptions;
import au.org.ala.delta.util.Predicate;
import au.org.ala.delta.util.SearchableModel;

/**
 * A specialized List for displaying DELTA Items.
 * 
 * The ItemList also supports an extended selection model whereby it can respond to double clicks
 * or the Enter key. To respond to this kind of selection event, register an Action using the 
 * setSelectionAction method.
 */
public class ItemList extends SelectionList {

	private static final long serialVersionUID = -5233281885631132020L;

	/**
	 * A ListModel that uses a backing DeltaDataSet to obtain a list of Items to display in the list.
	 */
	class ItemListModel extends AbstractListModel implements SearchableModel<Item>{

		private static final long serialVersionUID = 3730613528594711922L;
		private EditorViewModel _dataSet;
		
		public ItemListModel(EditorViewModel dataSet) {
			_dataSet = dataSet;
			_dataSet.addDeltaDataSetObserver(new ItemListener());
		}
		
		@Override
		public Object getElementAt(int index) {
			return new ItemViewModel(_dataSet.getItem(index+1));
		}

		@Override
		public int getSize() {
			
			return _dataSet.getMaximumNumberOfItems();
		}
		
		class ItemListener extends AbstractDataSetObserver {

			@Override
			public void itemAdded(DeltaDataSetChangeEvent event) {
				int newItem = event.getItem().getItemNumber();
				fireIntervalAdded(ItemListModel.this, newItem-1, newItem-1);
			}
			
			@Override
			public void itemDeleted(DeltaDataSetChangeEvent event) {
				int selection = getSelectedIndex();
				int deletedItem = event.getItem().getItemNumber();
				fireIntervalRemoved(ItemListModel.this, deletedItem-1, deletedItem-1);
				if (selection == deletedItem -1) {
					selection = Math.min(selection, getSize()-1);
					setSelectedIndex(Math.max(selection, 0));
				}
			}
			
			@Override
			public void itemMoved(DeltaDataSetChangeEvent event) {
				int newIndex = event.getItem().getItemNumber();
				int oldIndex = (Integer)event.getExtraInformation();
				
				fireContentsChanged(ItemListModel.this, Math.min(oldIndex, newIndex)-1, Math.max(oldIndex, newIndex)-1);
			}
		}

		@Override
		public Item first(Predicate<Item> predicate, int startIndex, SearchDirection direction) {
			return _dataSet.firstItem(predicate, startIndex, direction);
		}

		@Override
		public int size() {
			return this.getSize();
		}
	}
	
	/**
	 * Represents an Item in a way suitable for display on the List.
	 */
	class ItemViewModel {

		private Item _model;
		private ItemFormatter _formatter;
		
		public ItemViewModel(Item item) {
			_model = item;
			_formatter = new ItemFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, true, false);
		}

		@Override
		public String toString() {
			return _formatter.formatItemDescription(_model);
		}

		public Item getItem() {
			return _model;
		}
	}
	
	public ItemList() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	/**
	 * Creates an ItemList backed by the supplied dataSet.
	 * @param dataSet the data set to act as the model for this List.
	 */
	public ItemList(EditorViewModel dataSet) {
		this();
		setDataSet(dataSet);
		
		ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
		
		javax.swing.Action find = actionMap.get("find");
		if (find != null) {
			getActionMap().put("find", find);
		}

		javax.swing.Action findNext = actionMap.get("findNext");
		if (findNext != null) {
			getActionMap().put("findNext", findNext);
		}

	}
	
	public void setDataSet(EditorViewModel dataSet) {
		setModel(new ItemListModel(dataSet));
		
	}
	
	private SearchDialog _search;

	@org.jdesktop.application.Action
	public void find() {
		if (_search == null) {
			_search = new SearchDialog(new ItemSearchController());
		}
		_search.setVisible(true);
	}
	
	@org.jdesktop.application.Action
	public void findNext() {
		if (_search == null) {
			find();
			return;
		}
		
		_search.findNext();
	}
	
	class ItemSearchController extends GenericSearchController<Item> {

		public ItemSearchController() {
			super("findItem.title");
		}

		@Override
		public JComponent getOwningComponent() {
			return ItemList.this;
		}

		@Override
		protected void selectItem(Item item) {
			int index = item.getItemNumber() - 1;
			setSelectedIndex(index);
			ensureIndexIsVisible(index);			
		}

		@Override
		protected void clearSelection() {
			ItemList.this.clearSelection();
			
		}

		@Override
		protected SearchableModel<Item> getSearchableModel() {
			return (ItemListModel) getModel();
		}

		@Override
		protected int getSelectedIndex() {
			return ItemList.this.getSelectedIndex();
		}

		@Override
		protected int getIndexOf(Item object) {
			return object.getItemNumber() - 1;
		}

		@Override
		protected GenericSearchPredicate<Item> createPredicate(SearchOptions options) {
			return new ItemSearchPredicate(options);
		}
		
	}
	
	class ItemSearchPredicate extends GenericSearchPredicate<Item> {

		protected ItemSearchPredicate(SearchOptions options) {
			super(options);
		}

		@Override
		public boolean test(Item item) {
			String desc = item.getDescription();
			if (!getOptions().isCaseSensitive()) {
				return desc.toLowerCase().contains(getTerm());
			}

			return desc.contains(getTerm());
		}
		
	}
	
}
