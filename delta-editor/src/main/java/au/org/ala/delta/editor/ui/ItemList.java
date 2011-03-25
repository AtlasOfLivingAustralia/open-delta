package au.org.ala.delta.editor.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;


import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;

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
		private DeltaDataSet _dataSet;
		
		public ItemListModel(DeltaDataSet dataSet) {
			_dataSet = dataSet;
		}
		
		@Override
		public Object getElementAt(int index) {
			return _dataSet.getItem(index+1);
		}

		@Override
		public int getSize() {
			return _dataSet.getMaximumNumberOfItems();
		}
	}
	
	/**
	 * Works with an ItemListModel to render Items in an appropriate manner.
	 */
	class ItemRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1615023904839892809L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			
			Item item = (Item)value;
			
			super.getListCellRendererComponent(list, formatItem(item), index, isSelected, cellHasFocus);
			
			
			return this;
		}
		
		/**
		 * Formats an item description by including it's number and whether or not it is a variant item.
		 * @param item the item to format.
		 * @return a string representing the supplied Item.
		 */
		private String formatItem(Item item) {
			StringBuilder builder = new StringBuilder();
			builder.append(item.getItemNumber()).append(". ");
			if (item.isVariant()) {
				builder.append("(+) ");
			}
			builder.append(item.getDescription());
			return builder.toString();
		}
	}
	
	public ItemList() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public ItemList(DeltaDataSet dataSet) {
		this();
		setDataSet(dataSet);
	}
	
	/**
	 * Creates an ItemList backed by the supplied dataSet.
	 * @param dataSet the data set to act as the model for this List.
	 */
	public void setDataSet(DeltaDataSet dataSet) {
		setModel(new ItemListModel(dataSet));
		setCellRenderer(new ItemRenderer());
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
				Action action = getActionMap().get(SELECTION_ACTION_NAME);
	
				if (action != null) {
					ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
					action.actionPerformed(event);
				}
			}
		}
	}
	
}
