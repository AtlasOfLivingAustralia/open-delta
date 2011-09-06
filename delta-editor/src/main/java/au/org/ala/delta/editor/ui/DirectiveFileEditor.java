package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.DirectiveFileInfo;
import au.org.ala.delta.editor.directives.DirectiveImportHandlerAdapter;
import au.org.ala.delta.editor.directives.ExportController;
import au.org.ala.delta.editor.directives.ImportContext;
import au.org.ala.delta.editor.directives.ImportController;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.directive.DirectiveInOutState;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.ui.util.MessageDialogHelper;
import au.org.ala.delta.editor.ui.validator.TextComponentValidator;
import au.org.ala.delta.editor.ui.validator.ValidationResult;
import au.org.ala.delta.editor.ui.validator.Validator;
import au.org.ala.delta.ui.codeeditor.CodeEditor;
import au.org.ala.delta.ui.codeeditor.CodeTextArea;

/**
 * Provides a user interface that allows directive files to be edited.
 */
public class DirectiveFileEditor extends AbstractDeltaView {

	private static final long serialVersionUID = 9193388605723396077L;

	/** Contains the directive file are editing */
	private EditorViewModel _model;

	private ActionMap _actions;

	private CodeEditor directivesEditor;
	
	private String originalText;
	
	private MessageDialogHelper _messageHelper;

	public DirectiveFileEditor(EditorViewModel model) {
		super();
		setName("ItemEditorDialog");
		_model = model;
		_actions = Application.getInstance().getContext().getActionMap(this);
		_messageHelper = new MessageDialogHelper();
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
	private void disableSave() {
		_actions.get("applyChanges").setEnabled(false);
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
		disableSave();
	}

	@Override
	public String getViewTitle() {
		return _model.getSelectedDirectiveFile().getShortFileName();
	}

	@Override
	public boolean canClose() {
		if (originalText.equals(getText())) {
			return true;
		}
		int result = _messageHelper.promtForSaveBeforeClosing();
		if (result == JOptionPane.CANCEL_OPTION) {
			return false;
		}
		else if (result == JOptionPane.OK_OPTION) {
			applyChanges();
		}
		return true;
	}

	@Action
	public void applyChanges() {
		new CodeEditorValidator(new ImportErrorHandler()).verify(directivesEditor.getTextArea());
	}
	
	@Override
	public boolean editsValid() {
		return true;
	}
	
	private String getText() {
		return directivesEditor.getTextArea().getText();
	}
	
	class ImportErrorHandler extends DirectiveImportHandlerAdapter implements Validator {

		private ValidationResult _result = ValidationResult.success();
		/**
		 * Validate the supplied object and return an instance of ValidationResult.
		 * @param toValidate the object to validate.
		 * @return results of the validation.
		 */
		public ValidationResult validate(Object toValidate) {
			ImportController controller = new ImportController(
					(DeltaEditor) Application.getInstance(), _model);
		
			String text = getText();
			DirectiveFile file = _model.getSelectedDirectiveFile();
			DirectiveFileInfo fileInfo = new DirectiveFileInfo(file);
			boolean success = controller.importDirectivesFile(fileInfo, new StringReader(text), this);
			if (success) {
				updateGUI();
			}
			return _result;
		}
			
		@Override
		public void handleUnrecognizedDirective(ImportContext context, List<String> controlWords) {
			String directive = StringUtils.join(controlWords.toArray(), ' ');
			int location = directiveIndex(directive);
			if (location >= 0) {
				_result = ValidationResult.error("UNRECOGNISED_DIRECTIVE", location);
			}
			else {
				_result = ValidationResult.error("UNRECOGNISED_DIRECTIVE");
			}
			
			_result.setMessageArgs(directive);
		}

		@Override
		public void handleDirectiveProcessingException(ImportContext context, AbstractDirective<ImportContext> d,
				Exception ex) {
			if (ex instanceof ParseException) {
				String directive = d.getName();
				int location = directiveIndex(directive);
				ParseException pe = (ParseException)ex;
				location += directive.length();
				String text = getText();
				while (Character.isWhitespace(text.charAt(location))) {
					location ++;
				}
				
				location += pe.getErrorOffset();
				_result = ValidationResult.error("DIRECTIVE_PARSE_ERROR", location);
				_result.setMessageArgs(ex.getMessage());
			}
		}
		
		private int directiveIndex(String directive) {
			String text = getText();
			return text.indexOf(directive);
		}
		
	}
	
	class CodeEditorValidator extends TextComponentValidator {
		public CodeEditorValidator(Validator validator) {
			super(validator);
		}
		
		@Override
		public boolean verify(JComponent component) {
			return validate(component);
		}
		
		public Object getValueToValidate(JComponent component) {
			return getText();
		}
		
		protected void updateTextStyles(JComponent component, ValidationResult validationResult) {
			CodeTextArea textArea = (CodeTextArea)component;
			if (!validationResult.isValid()) {
				int pos = validationResult.getInvalidCharacterPosition();
				if (pos >= 0) {
					String text = getText();
					int nextSpace = text.indexOf(' ', pos);
					if (nextSpace < 0) {
						nextSpace = text.length() - 1;
					}
					int nextNewline = text.indexOf('\n', pos);
					if (nextNewline < 0) {
						nextNewline = text.length() - 1;
					}
					
					textArea.select(Math.max(pos, 0), Math.min(nextSpace, nextNewline));
				}
			}
		}
	}

}
