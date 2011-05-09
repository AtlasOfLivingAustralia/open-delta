package au.org.ala.delta.editor;

import au.org.ala.delta.editor.ui.EditorDataModel;
import au.org.ala.delta.editor.ui.ItemEditor;
import au.org.ala.delta.editor.ui.MatrixViewer;
import au.org.ala.delta.editor.ui.TreeViewer;


/**
 * The DeltaViewFactory is responsible for creating DeltaViews.
 * Right now it doesn't do much - the intent is to eventually turn this into an 
 * abstract factory to remove the current dependency on JInternalFrames.
 * This is so an alternative view implementation (e.g. tabs) will be easier to write.
 */
public class DeltaViewFactory {

	
	public DeltaView createTreeView(EditorDataModel model) {
		return new TreeViewer(model);
	}
	
	public DeltaView createGridView(EditorDataModel model) {
		return new MatrixViewer(model);
	}
	
	public DeltaView createItemEditView(EditorDataModel model) {
		return new ItemEditor(model);
	}
	
}
