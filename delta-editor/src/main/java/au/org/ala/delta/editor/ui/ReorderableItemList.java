package au.org.ala.delta.editor.ui;

import javax.swing.Action;

import au.org.ala.delta.model.Item;

public interface ReorderableItemList {
	
	public Item getSelectedItem();
	
	public void setSelectedItem(int itemNumber);
	
	public int getDropLocationIndex();
	
	public void setSelectionAction(Action action);
}
