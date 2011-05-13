package au.org.ala.delta.editor.ui;

import javax.swing.Action;

public interface ReorderableList<T> {
	
	public T getSelected();
	
	public void setSelectedIndex(int index);
	
	public int getDropLocationIndex();
	
	public void setSelectionAction(Action action);
}
