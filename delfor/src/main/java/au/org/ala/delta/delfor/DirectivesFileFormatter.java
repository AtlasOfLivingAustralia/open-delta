package au.org.ala.delta.delfor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.editor.directives.DirectiveFileInfo;
import au.org.ala.delta.editor.directives.DirectiveImportHandlerAdapter;
import au.org.ala.delta.editor.directives.DirectivesFileExporter;
import au.org.ala.delta.editor.directives.DirectivesFileImporter;
import au.org.ala.delta.editor.directives.ImportContext;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.directive.DirectiveInOutState;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.util.FileUtils;

public class DirectivesFileFormatter {

	private DelforContext _context;
	private DeltaDataSetRepository _dataSetRepository;
	
	public DirectivesFileFormatter(DelforContext context) {
		_context = context;
		_dataSetRepository = new SlotFileRepository();
	}
	
	public void reformat(File file) throws DirectiveException {
		AbstractObservableDataSet dataSet = (AbstractObservableDataSet) _dataSetRepository.newDataSet();

		
		EditorDataModel model = new EditorDataModel(dataSet);
		ImportContext context = new ImportContext(model);
		DirectivesFileImporter importer = new DirectivesFileImporter(model, context);
		
		
		List<DirectiveFileInfo> files = new ArrayList<DirectiveFileInfo>();
		DirectiveFileInfo directiveInfo = new DirectiveFileInfo(file.getName(), DirectiveType.CONFOR);
		files.add(directiveInfo);
		
		try {
			importer.importDirectivesFile(directiveInfo, file, new DirectiveImportHandlerAdapter());
			
			// do stuff based on the directives.
			
			DirectiveFile directiveFile = model.getDirectiveFile(file.getName());
			
			DirectivesFileExporter exporter = new DirectivesFileExporter();
			DirectiveInOutState state = createState(directiveFile, file, model);
			exporter.writeDirectivesFile(directiveFile, state);
		}
		catch (IOException e) {
			throw DirectiveError.asException(DirectiveError.Error.ALL_CHARACTERS_EXCLUDED, 0);
		}
		
	}
	
	private DirectiveInOutState createState(
			DirectiveFile directiveFile, File file, EditorViewModel model) throws IOException {
		String fileName = directiveFile.getShortFileName()+".new";
		
		File outputFile = new File(file.getParentFile(), fileName);
		FileUtils.backupAndDelete(fileName, outputFile.getParent());
		DirectiveInOutState state = new DirectiveInOutState(model);
		
		state.setPrintStream(new PrintStream(outputFile, _context.getFileEncoding().name()));
		
		return state;
	}
	
}
