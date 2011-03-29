package au.org.ala.delta.editor.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;

/**
 * A specialised List for displaying DELTA Items.
 * 
 * The ItemList also supports an extended selection model whereby it can respond to double clicks
 * or the Enter key. To respond to this kind of selection event, register an Action using the 
 * setSelectionAction method.
 */
public class ItemList extends JList {

	private static final String SELECTION_ACTION_NAME = "selectionAction";
	private static final long serialVersionUID = -5233281885631132020L;

	/**
	 * A ListModel that uses a backing DeltaDataSet to obtain a list of Items to display in the list.
	 */
	class ItemListModel extends AbstractListModel {

		private static final long serialVersionUID = 3730613528594711922L;
		private EditorDataModel _dataSet;
		
		public ItemListModel(EditorDataModel dataSet) {
			_dataSet = dataSet;
			_dataSet.addDeltaDataSetObserver(new NewItemListener());
		}
		
		@Override
		public Object getElementAt(int index) {
			return new ItemViewModel(_dataSet.getItem(index+1));
		}

		@Override
		public int getSize() {
			return _dataSet.getMaximumNumberOfItems();
		}
		
		class NewItemListener extends AbstractDataSetObserver {

			@Override
			public void itemAdded(DeltaDataSetChangeEvent event) {
				fireIntervalAdded(ItemListModel.this, getSize()-1, getSize()-1);
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
			_formatter = new ItemFormatter();
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
	public ItemList(EditorDataModel dataSet) {
		this();
		setDataSet(dataSet);
	}
	
	public Item getSelectedItem() {
		Item selectedItem = null;
		ItemViewModel item = (ItemViewModel)getSelectedValue();
		if (item != null) {
			selectedItem = item.getItem();
		}
		return selectedItem;
	}
	
	
	public void setDataSet(EditorDataModel dataSet) {
		setModel(new ItemListModel(dataSet));
		
	}
	
	
	/**
	 * Registers the action to take when a selection (double click or Enter key) has been made on
	 * this list.
	 * @param action the action that will be invoked on selection.
	 */
	public void setSelectionAction(Action action) {
		addMouseListener(new DoubleClickToAction());
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECTION_ACTION_NAME);
		getActionMap().put(SELECTION_ACTION_NAME, action);
	}
	
	/**
	 * Detects double clicks and treats them as a different type of selection event.
	 */
	public class DoubleClickToAction extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				int index = locationToIndex(e.getPoint());
				setSelectedIndex(index);
				
				Action action = getActionMap().get(SELECTION_ACTION_NAME);
	
				if (action != null) {
					ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
					action.actionPerformed(event);
				}
			}
		}
	}
	
	
	
}
