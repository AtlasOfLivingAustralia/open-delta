package au.org.ala.delta.editor;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;

import au.org.ala.delta.editor.ui.EditorDataModel;
import au.org.ala.delta.editor.ui.ItemList;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.ui.MessageDialogHelper;

/**
 * Handles actions performed on the ItemList.
 */
public class ItemController {

	private ItemList _view;
	private EditorDataModel _model;
	
	public ItemController(ItemList view, EditorDataModel model) {
		_view = view;
		_model = model;
	}
	
	@Action
	public void addItem() {
		Item newItem = _model.addItem();
		displayItemEditor(newItem.getItemNumber());
		
		if (StringUtils.isEmpty(newItem.getDescription())) {
			//_model.deleteItem(newItem);
		}
		
	}
	
	@Action
	public void deleteItem() {
		Item toDelete = getSelectedItem();
		if (confirmDelete(toDelete)) {
			_model.deleteItem(toDelete);
		}
	}
	
	@Action
	public void editItem() {
		
	}
	
	@Action
	public void insertItem() {
		int selectedItem = _view.getSelectedIndex()+1;
		//Item newItem = _model.insertItem(selectedItem+1);
	}
	
	
	private void displayItemEditor(int itemNumber) {
		
	}
	
	public Item getSelectedItem() {
		int itemNumber = _view.getSelectedIndex()+1;
		return _model.getItem(itemNumber);
	}
	
	private boolean confirmDelete(Item toDelete) {
		int result = MessageDialogHelper.showConfirmDialog(_view, "CONFIRM", 
				"Please confirm that you really wish to delete this taxon:\n" + toDelete.getDescription(), 50);
		return result == JOptionPane.OK_OPTION;
	}
	
}
