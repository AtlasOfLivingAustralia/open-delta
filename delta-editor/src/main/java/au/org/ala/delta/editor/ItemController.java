package au.org.ala.delta.editor;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

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
		
		buildPopup();
	}
	
	public void buildPopup() {
		JPopupMenu popup = new JPopupMenu();
		ActionMap itemActions = _context.getActionMap(ItemController.class, this);
		String[] itemsPopupActions = new String[] {"editItem", "addItem", "insertItem", "deleteItem", "-"};
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
		Item newItem = _model.addItem(selectedItem+1);
		
		editNewItem(newItem, e);
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
	
	private boolean confirmDelete(Item toDelete) {
		int result = MessageDialogHelper.showConfirmDialog(_view, "CONFIRM", 
				"Please confirm that you really wish to delete this taxon:\n" + toDelete.getDescription(), 50);
		return result == JOptionPane.OK_OPTION;
	}
	
}
