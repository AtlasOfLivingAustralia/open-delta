package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JInternalFrame;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.DeltaView;
import au.org.ala.delta.editor.directives.DirectiveFileInfo;
import au.org.ala.delta.editor.directives.DirectiveImportHandlerAdapter;
import au.org.ala.delta.editor.directives.ExportController;
import au.org.ala.delta.editor.directives.ImportContext;
import au.org.ala.delta.editor.directives.ImportController;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.directive.DirectiveInOutState;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.ui.validator.ValidationListener;
import au.org.ala.delta.editor.ui.validator.ValidationResult;
import au.org.ala.delta.ui.codeeditor.CodeEditor;

/**
 * Provides a user interface that allows directive files to be edited.
 */
public class DirectiveFileEditor extends JInternalFrame implements ValidationListener, DeltaView {

	private static final long serialVersionUID = 9193388605723396077L;

	/** Contains the directive file are editing */
	private EditorViewModel _model;

	private ActionMap _actions;

	private CodeEditor directivesEditor;
	
	private String originalText;

	public DirectiveFileEditor(EditorViewModel model) {
		super();
		setName("ItemEditorDialog");
		_model = model;
		_actions = Application.getInstance().getContext().getActionMap(this);

		createUI();
		addEventHandlers();
		updateGUI();
	}
	
	private void addEventHandlers() {
		javax.swing.Action applyChanges = _actions.get("applyChanges");
		directivesEditor.addToolbarButton(applyChanges, "saveDirectiveFile");
		directivesEditor.getTextArea().getDocument().addDocumentListener( 
			new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					enableSave();
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					enableSave();
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					enableSave();
				}
			});
	}
	
	private void enableSave() {
		_actions.get("applyChanges").setEnabled(true);
	}

	private void createUI() {
		directivesEditor = new CodeEditor(getMimeType());
		directivesEditor.getTextArea().setEOLMarkersPainted(false);
		getContentPane().add(directivesEditor, BorderLayout.CENTER);
	}

	private String getMimeType() {
		DirectiveFile file = _model.getSelectedDirectiveFile();
		String mimeType;
		switch (file.getType()) {
		case CONFOR:
			mimeType = "text/confor";
			break;
		default:
			mimeType = "text/plain";
			break;
		}
		return mimeType;
	}

	private void updateGUI() {
		DirectiveFile file = _model.getSelectedDirectiveFile();
		ExportController ec = new ExportController((DeltaEditor) Application.getInstance());
		DirectiveInOutState state = new DirectiveInOutState(_model);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream p = new PrintStream(out);
		state.setPrintStream(p);
		ec.writeDirectivesFile(file, state);

		if (file.isCharsFile() || file.isItemsFile() || file.isSpecsFile()) {
			directivesEditor.getTextArea().setEditable(false);
		}
		originalText = new String(out.toByteArray());
		directivesEditor.setText(originalText);
	}

	@Override
	public String getViewTitle() {
		return _model.getSelectedDirectiveFile().getShortFileName();
	}

	@Override
	public void open() {

	}

	@Action
	public void applyChanges() {
		ImportController controller = new ImportController(
				(DeltaEditor) Application.getInstance(), _model);
	
		String text = directivesEditor.getTextArea().getText();
		DirectiveFile file = _model.getSelectedDirectiveFile();
		DirectiveFileInfo fileInfo = new DirectiveFileInfo(file);
		boolean success = controller.importDirectivesFile(fileInfo, new StringReader(text), new ImportErrorHandler());
		if (success) {
			updateGUI();
		}
	}
	
	@Override
	public boolean editsValid() {

		return true;
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

	}

	@Override
	public void validationFailed(ValidationResult results) {

	}
	
	private void highlightError(int charNumber) {
		directivesEditor.getTextArea().select(charNumber, charNumber+1);
	}
	
	class ImportErrorHandler extends DirectiveImportHandlerAdapter {

		@Override
		public void handleUnrecognizedDirective(ImportContext context, List<String> controlWords) {
			
		}

		@Override
		public void handleDirectiveProcessingException(ImportContext context, AbstractDirective<ImportContext> d,
				Exception ex) {
			handleException(ex);
		}
		
		private void handleException(Exception ex) {
			if (ex instanceof ParseException) {
				highlightError(((ParseException)ex).getErrorOffset());
			}
		}
		
	}

}
