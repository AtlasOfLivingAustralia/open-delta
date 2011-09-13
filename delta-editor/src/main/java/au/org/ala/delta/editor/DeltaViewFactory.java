package au.org.ala.delta.editor;

import javax.swing.JInternalFrame;

import au.org.ala.delta.editor.model.DeltaViewModel;
import au.org.ala.delta.editor.ui.ActionSetsDialog;
import au.org.ala.delta.editor.ui.CharacterEditor;
import au.org.ala.delta.editor.ui.DirectiveFileEditor;
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
	
	public DeltaView createItemEditView(DeltaViewModel model, JInternalFrame owner) {
		return new ItemEditor(model, owner);
	}

	public DeltaView createCharacterEditView(DeltaViewModel model, JInternalFrame owner) {
		return new CharacterEditor(model, owner);
	}
	
	public DeltaView createImageEditorView(DeltaViewModel model) {
		return new ImageEditor(model);
	}

	public DeltaView createDirectivesEditorView(DeltaViewModel model) {
		return new DirectiveFileEditor(model);
	}

	public DeltaView createActionSetsView(DeltaViewModel model) {
		return new ActionSetsDialog(model);
	}
	
}
