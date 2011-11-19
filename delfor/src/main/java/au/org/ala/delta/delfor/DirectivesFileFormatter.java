package au.org.ala.delta.delfor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.delfor.format.FormattingAction;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.editor.directives.DirectiveFileInfo;
import au.org.ala.delta.editor.directives.DirectiveImportHandlerAdapter;
import au.org.ala.delta.editor.directives.DirectivesFileExporter;
import au.org.ala.delta.editor.directives.DirectivesFileImporter;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.directive.DirectiveInOutState;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.util.FileUtils;

public class DirectivesFileFormatter {

	private DelforContext _context;
	private EditorDataModel _model;
	
	public DirectivesFileFormatter(DelforContext context) {
		_context = context;
		_model = new EditorDataModel((AbstractObservableDataSet)context.getDataSet());
	}
	
	public void reformat() throws DirectiveException {
		List<File> toReformat = _context.getFilesToReformat();
		
		
		importAll(toReformat);
		
		runFormattingActions();
		
		exportAll(toReformat);
	}

	protected void exportAll(List<File> toReformat) throws DirectiveException {
		for (File file : toReformat) {
			
			DirectiveFile directiveFile = _model.getDirectiveFile(file.getName());
			
			try {
				DirectivesFileExporter exporter = new DirectivesFileExporter();
				DirectiveInOutState state = createState(directiveFile, file, _model);
				exporter.writeDirectivesFile(directiveFile, state);
			}
			catch (IOException e) {
				e.printStackTrace();
				throw DirectiveError.asException(DirectiveError.Error.ALL_CHARACTERS_EXCLUDED, 0);
			}
		}
	}

	protected void importAll(List<File> toReformat) throws DirectiveException {
		DirectivesFileImporter importer = new DelforDirectivesFileImporter(_model, _context);
		
		for (File file : toReformat) {
			DirectiveFileInfo directiveInfo = new DirectiveFileInfo(file.getName(), DirectiveType.CONFOR);
			List<DirectiveFileInfo> files = new ArrayList<DirectiveFileInfo>();
			
			files.add(directiveInfo);
		
			try {
			
				importer.importDirectivesFile(directiveInfo, file, new DirectiveImportHandlerAdapter());
			}
			catch (IOException e) {
				e.printStackTrace();
				throw DirectiveError.asException(DirectiveError.Error.ALL_CHARACTERS_EXCLUDED, 0);
			}
		}
	}
	
	private DirectiveInOutState createState(
			DirectiveFile directiveFile, File file, EditorViewModel model) throws IOException {
		String fileName = directiveFile.getShortFileName()+".new";
		
		File outputFile = new File(file.getParentFile(), fileName);
		FileUtils.backupAndDelete(fileName, outputFile.getParent());
		DirectiveInOutState state = new DirectiveInOutState(model);
		
		state.setPrintStream(new PrintStream(outputFile, _context.getFileEncoding().name()));
		state.getPrinter().setPrintWidth(_context.getOutputWidth());
		return state;
	}
	
	private void runFormattingActions() {
		for (FormattingAction action : _context.getFormattingActions()) {
			action.format(_context, (SlotFileDataSet)_context.getDataSet());
		}
	}
	
}
