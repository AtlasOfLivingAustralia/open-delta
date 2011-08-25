package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;

import javax.swing.ActionMap;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.DeltaView;
import au.org.ala.delta.editor.directives.ExportController;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.ui.validator.ValidationListener;
import au.org.ala.delta.editor.ui.validator.ValidationResult;

/**
 * Provides a user interface that allows directive files to be edited.
 */
public class DirectiveFileEditor extends JInternalFrame implements ValidationListener, DeltaView {
	
	private static final long serialVersionUID = 9193388605723396077L;

	/** Contains the directive file are editing */
	private EditorViewModel _model;
	
	/** Allows directive files to be opened read only */
	private boolean _readOnly;
	
	private ResourceMap _resources;
	
	private ActionMap _actions;
	
	public DirectiveFileEditor(EditorViewModel model) {	
		super();
		setName("ItemEditorDialog");
		_model = model;
		_resources = Application.getInstance().getContext().getResourceMap(DirectiveFileEditor.class);
		_actions = Application.getInstance().getContext().getActionMap(this);
		
		createUI();
		updateGUI();
	}
	
	
	private void createUI() {
		getContentPane().add(new JTextArea(), BorderLayout.CENTER);
	}


	private void updateGUI() {
		DirectiveFile file = _model.getSelectedDirectiveFile();
		ExportController ec = new ExportController((DeltaEditor)Application.getInstance());
		
	}

	@Override
	public String getViewTitle() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void open() {
		
	}



	@Override
	public boolean editsValid() {
		
		return false;
	}



	@Override
	public ReorderableList getCharacterListView() {
		return null;
	}

	@Override
	public ReorderableList getItemListView() {
		return null;
	}



	@Override
	public void validationSuceeded(ValidationResult results) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void validationFailed(ValidationResult results) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
