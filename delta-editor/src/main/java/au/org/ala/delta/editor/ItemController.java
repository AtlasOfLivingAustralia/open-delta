package au.org.ala.delta.editor;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;

import au.org.ala.delta.editor.ui.EditorDataModel;
import au.org.ala.delta.editor.ui.ItemList;
import au.org.ala.delta.model.Item;

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
		int itemNumber = _view.getSelectedIndex();
		_model.deleteItem(itemNumber);
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
	
}
