package au.org.ala.delta.editor;

import au.org.ala.delta.editor.model.DeltaViewModel;
import au.org.ala.delta.editor.ui.CharacterEditor;
import au.org.ala.delta.editor.ui.ItemEditor;
import au.org.ala.delta.editor.ui.MatrixViewer;
import au.org.ala.delta.editor.ui.TreeViewer;
import au.org.ala.delta.editor.ui.image.ImageEditor;


/**
 * The DeltaViewFactory is responsible for creating DeltaViews.
 * Right now it doesn't do much - the intent is to eventually turn this into an 
 * abstract factory to remove the current dependency on JInternalFrames.
 * This is so an alternative view implementation (e.g. tabs) will be easier to write.
 */
public class DeltaViewFactory {

	
	public DeltaView createTreeView(DeltaViewModel model) {
		return new TreeViewer(model);
	}
	
	public DeltaView createGridView(DeltaViewModel model) {
		return new MatrixViewer(model);
	}
	
	public DeltaView createItemEditView(DeltaViewModel model) {
		return new ItemEditor(model);
	}

	public DeltaView createCharacterEditView(DeltaViewModel model) {
		return new CharacterEditor(model);
	}
	
	public DeltaView createImageEditorView(DeltaViewModel model) {
		return new ImageEditor(model);
	}
	
}
