package au.org.ala.delta.editor.ui;

import javax.swing.Action;

public interface ReorderableList {
	
	public int getSelectedIndex();
	
	public void setSelectedIndex(int index);
	
	public int getDropLocationIndex();
	
	public void setSelectionAction(Action action);
}
