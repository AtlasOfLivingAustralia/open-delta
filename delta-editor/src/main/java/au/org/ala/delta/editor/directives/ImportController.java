package au.org.ala.delta.editor.directives;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog;
import au.org.ala.delta.editor.directives.ui.ImportExportViewModel;
import au.org.ala.delta.editor.directives.ui.ImportViewModel;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DistDirType;
import au.org.ala.delta.editor.slotfile.directive.IntkeyDirType;
import au.org.ala.delta.editor.slotfile.directive.KeyDirType;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;

/**
 * The ImportController manages the process of importing a set of directives files
 * into the current Editor dataset.
 */
public class ImportController  {

	private DeltaEditor _editor;
	private EditorViewModel _model;
	private ImportExportViewModel _importModel;
	private ImportExportDialog _importDialog;
	private ResourceMap _resources;
	private ActionMap _actions;
	private ImportContext _context;
	
	public ImportController(DeltaEditor editor, EditorViewModel model, DirectiveImportHandler handler) {
		_editor = editor;
		_resources = _editor.getContext().getResourceMap();
		_actions = _editor.getContext().getActionMap(this);
		_model = model;
		_context = new ImportContext(_model);
	}
	
	public ImportController(DeltaEditor editor, EditorViewModel model) {
		this(editor, model, null);
	}
	
	public void begin() {
		
		_importModel = new ImportViewModel();
		File dataSetPath = new File(_model.getDataSetPath());
		if (dataSetPath.exists()) {
			_importModel.populate(_model);
		}
		
		_importDialog = new ImportExportDialog(_editor.getMainFrame(), _importModel, "ImportDialog");
		_importDialog.setDirectorySelectionAction(_actions.get("changeImportDirectory"));
		_editor.show(_importDialog);
		
		if (_importDialog.proceed()) {
			List<DirectiveFileInfo> files = _importModel.getSelectedFiles();
			File selectedDirectory = _importModel.getCurrentDirectory();
			
			doImport(selectedDirectory, files);
		}
	}
	
	@Action
	public void changeImportDirectory() {
		JFileChooser directorySelector = new JFileChooser(_importModel.getCurrentDirectory());
		String directoryChooserTitle = _resources.getString("ImportDialog.directoryChooserTitle");
		directorySelector.setDialogTitle(directoryChooserTitle);
		directorySelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directorySelector.setAcceptAllFileFilterUsed(false);
		
		int result = directorySelector.showOpenDialog(_importDialog);
		if (result == JFileChooser.APPROVE_OPTION) {
			_importModel.setCurrentDirectory(directorySelector.getSelectedFile());
			_importModel.populate(_model);
			_importDialog.updateUI();
		}
	}
	
	public void doImport(File selectedDirectory, List<DirectiveFileInfo> files) {
		
		// Do the import on a background thread.
		DoImportTask importTask = new DoImportTask(selectedDirectory, files, false);
		
		importTask.execute();
	}
	
	public void doSilentImport(File selectedDirectory, List<DirectiveFileInfo> files) {
		new DoImportTask(selectedDirectory, files, true).execute();
	}

	/**
	 * Parses the supplied stream into the directives file identified by
	 * the supplied DirectiveFileInfo.  If a directive exists with the same
	 * name, the contents of that file will be replaced, otherwise a new
	 * directives file will be added.
	 * If the import fails the directives file will not be updated.
	 * @param fileInfo identifies the directives file to import into.
	 * @param directivesReader a reader containing the raw directives data.
	 * @param status
	 * @return true if the import succeeded, false otherwise.
	 */
	public boolean importDirectivesFile(DirectiveFileInfo fileInfo, Reader directivesReader, DirectiveImportHandler handler) {
		
		String name = fileInfo.getName();
		
		DirectiveFile existing =  _model.getDirectiveFile(name);
		DirectiveFile directiveFile = _model.addDirectiveFile(_model.getDirectiveFileCount()+1, name, fileInfo.getType());
		directiveFile.setLastModifiedTime(System.currentTimeMillis());
		DirectiveFileImporter importer = new DirectiveFileImporter(handler, directivesOfType(directiveFile.getType()));
		
		_context.setDirectiveFile(directiveFile);
		
		// Looks like we skip the specs file if we have non zero items or chars.....
		try {
			importer.parse(directivesReader, _context);
			
			if (importer.success()) {
				if (existing != null) {
					copyToExistingFile(existing, directiveFile);
					_model.deleteDirectiveFile(directiveFile);
				}
			}
			else {
				_model.deleteDirectiveFile(directiveFile);
			}
		}
		catch (Exception e) {
			_model.deleteDirectiveFile(directiveFile);
			e.printStackTrace();
		}
		return importer.success();
	}

	private void copyToExistingFile(DirectiveFile existing, DirectiveFile directiveFile) {
		existing.setDirectives(directiveFile.getDirectives());
		existing.setLastModifiedTime(directiveFile.getLastModifiedTime());
		existing.setFlags(directiveFile.getFlags());
	}
	
	private Directive[] directivesOfType(DirectiveType type) {
		Directive[] directives = null;
		switch(type) {
		case CONFOR:
			directives = ConforDirType.ConforDirArray;
			break;
		case INTKEY:
			directives = IntkeyDirType.IntkeyDirArray;
			break;
		case KEY:
			directives = KeyDirType.KeyDirArray;
			break;
		case DIST:
			directives = DistDirType.DistDirArray;
			break;
		}
		return directives;
	}
	
	public class DoImportTask extends ImportExportTask {
		
		public DoImportTask(File directory, List<DirectiveFileInfo> files, boolean silent) {
			super(_editor, _model, directory, files, "import", silent);
		}
		
		
		@Override
		protected Void doInBackground() throws Exception {
			
			publish(_status);
						
			for (DirectiveFileInfo file : _files) {
				if (isCancelled()) {
					break;
				}
				File toParse = new File(_directoryName+file.getFileName());
				FileInputStream fileIn = new FileInputStream(toParse);
				InputStreamReader reader = new InputStreamReader(fileIn, _context.getFileEncoding());
				
				// First check if the existing dataset has a directives file with the same name
				// and same last modified date.  If so, skip it.
				_status.setCurrentFile(file);
				publish(_status);
				importDirectivesFile(file, reader, this);
				
				publish(_status);
			}
			_status.finish();
			publish(_status);
			
			return null;
		}
	}
	
}
