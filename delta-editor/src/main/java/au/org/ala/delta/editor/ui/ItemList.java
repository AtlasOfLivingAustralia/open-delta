package au.org.ala.delta.editor.ui;

import javax.swing.AbstractListModel;
import javax.swing.ListSelectionModel;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;

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
	class ItemListModel extends AbstractListModel {

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
	}
	
	/**
	 * Represents an Item in a way suitable for display on the List.
	 */
	class ItemViewModel {

		private Item _model;
		private ItemFormatter _formatter;
		
		public ItemViewModel(Item item) {
			_model = item;
			_formatter = new ItemFormatter(true, false, false, true, true);
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
	}
	
	public void setDataSet(EditorViewModel dataSet) {
		setModel(new ItemListModel(dataSet));
		
	}
	
	
	
}
