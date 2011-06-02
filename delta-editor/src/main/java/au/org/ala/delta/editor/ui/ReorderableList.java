package au.org.ala.delta.editor.ui;

import javax.swing.Action;
import javax.swing.TransferHandler.DropLocation;

public interface ReorderableList {
	
	public int getSelectedIndex();
	
	public void setSelectedIndex(int index);
	
	public int getDropLocationIndex(DropLocation dropLocation);
	
	public void setSelectionAction(Action action);
}
