package au.org.ala.delta.editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import au.org.ala.delta.editor.ui.EditorDataModel;
import au.org.ala.delta.editor.ui.ItemList;
import au.org.ala.delta.editor.ui.util.MenuBuilder;
import au.org.ala.delta.editor.ui.util.PopupMenuListener;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.ui.MessageDialogHelper;

/**
 * Handles actions performed on the ItemList.
 */
public class ItemController {

	private ItemList _view;
	private EditorDataModel _model;
	private ApplicationContext _context;
	
	public ItemController(ItemList view, EditorDataModel model) {
		_view = view;
		_model = model;
		_context = Application.getInstance().getContext();
		
		_view.setDragEnabled(true);
		_view.setDropMode(DropMode.INSERT);
		_view.setTransferHandler(new ItemTransferHandler());
		
		buildPopup();
	}
	
	public void buildPopup() {
		JPopupMenu popup = new JPopupMenu();
		ActionMap itemActions = _context.getActionMap(ItemController.class, this);
		String[] itemsPopupActions = new String[] {"editItem", "addItem", "insertItem", "deleteItem", "-", "cancel"};
		MenuBuilder.buildMenu(popup, itemsPopupActions, itemActions);
		
		
		new PopupMenuListener(popup, _view);
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
		int selectedIndex = _view.getSelectedIndex();
		Item toDelete = _view.getSelectedItem();
		if (toDelete == null) {
			return;
		}
		if (confirmDelete(toDelete)) {
			_model.deleteItem(toDelete);
		}
		if (selectedIndex >= _model.getMaximumNumberOfItems()) {
			selectedIndex = _model.getMaximumNumberOfItems()-1;
		}
		_view.setSelectedIndex(selectedIndex);
	}
	
	/**
	 * Displays the Item editor for the currently selected Item.
	 */
	@Action
	public void editItem(ActionEvent e) {
		_context.getActionMap().get("viewTaxonEditor").actionPerformed(e);
		
	}
	
	@Action
	public void insertItem(ActionEvent e) {
		int selectedItem = _view.getSelectedIndex()+1;
		Item newItem = _model.addItem(selectedItem);
		
		editNewItem(newItem, e);
	}
	
	@Action
	public void cancel() {
		// Do nothing.
	}
	
	public void editNewItem(Item newItem, ActionEvent e) {
		int selectedIndex = newItem.getItemNumber()-1;
		_view.setSelectedIndex(selectedIndex);
		_view.ensureIndexIsVisible(selectedIndex);
		editItem(e);
		
		if (StringUtils.isEmpty(newItem.getDescription())) {
			_model.deleteItem(newItem);
			if (selectedIndex >= _model.getMaximumNumberOfItems()) {
				selectedIndex = _model.getMaximumNumberOfItems()-1;
			}
			_view.setSelectedIndex(selectedIndex);
		}
	}
	
	public void moveItem(Item item, int newIndex) {
		_model.moveItem(item, newIndex+1);
		
		_view.setSelectedIndex(newIndex);
		_view.ensureIndexIsVisible(newIndex);
		
	}
	
	private boolean confirmDelete(Item toDelete) {
		int result = MessageDialogHelper.showConfirmDialog(_view, "CONFIRM", 
				"Please confirm that you really wish to delete this taxon:\n" + toDelete.getDescription(), 50);
		return result == JOptionPane.OK_OPTION;
	}
	
	private static DataFlavor _itemFlavor = new DataFlavor(Item.class, "Item");
	
	/**
	 * Handles drag and drop of Items in the ItemList.
	 */
	class ItemTransferHandler extends TransferHandler {
		
		private static final long serialVersionUID = 889705892088002277L;
		private int sourceIndex;
		
		public boolean canImport(TransferHandler.TransferSupport info) {
			
			return info.isDataFlavorSupported(_itemFlavor);
		}
		
		protected Transferable createTransferable(JComponent c) {
			ItemList list = (ItemList)c;
			Item item = list.getSelectedItem();
			sourceIndex = list.getSelectedIndex();
			
			return new ItemTransferrable(item);
			
		}
		
		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY_OR_MOVE;
		}
		
		public boolean importData(TransferHandler.TransferSupport info) {
			ItemList list = (ItemList)info.getComponent();
			
			Transferable transferrable = info.getTransferable();
			
			Item item = null;
			try {
				item = (Item)transferrable.getTransferData(_itemFlavor);
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
			int targetIndex = list.getDropLocation().getIndex();
			if (targetIndex > sourceIndex) {
				targetIndex--;
			}
			
			if (info.getUserDropAction() == DnDConstants.ACTION_MOVE) {
				moveItem(item, targetIndex);
			}
			else if (info.getUserDropAction() == DnDConstants.ACTION_COPY) {
				System.out.println("Copying item "+item.getItemNumber() + " from "+sourceIndex+ " to "+targetIndex);
				
			}
			return true;
		}
	}

	/**
	 * Transfers an Item.
	 */
	class ItemTransferrable implements Transferable {

		private Item _item;
		
		public ItemTransferrable(Item item) {
			_item = item;
		}
		
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {_itemFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			
			return _itemFlavor.equals(flavor);
		}

		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return _item;
		}
		
	}
	
}
