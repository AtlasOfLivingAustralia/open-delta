package au.org.ala.delta.editor.directives;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog;
import au.org.ala.delta.editor.directives.ui.ImportExportStatusDialog;
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
public class ImportController implements DirectiveImportHandler  {

	private DeltaEditor _editor;
	private EditorViewModel _model;
	private ImportExportViewModel _importModel;
	private ImportExportDialog _importDialog;
	private ResourceMap _resources;
	private ActionMap _actions;
	private ImportContext _context;
	private DirectiveImportHandler _handler;
	
	public ImportController(DeltaEditor editor, EditorViewModel model, DirectiveImportHandler handler) {
		_editor = editor;
		_resources = _editor.getContext().getResourceMap();
		_actions = _editor.getContext().getActionMap(this);
		_model = model;
		_context = new ImportContext(_model);
		_handler = handler;
	}
	
	public ImportController(DeltaEditor editor, EditorViewModel model) {
		this(editor, model, null);
	}
	
	public void begin() {
		
		if ((_model.getNumberOfCharacters() > 0) || (_model.getMaximumNumberOfItems() > 0)) {
			JOptionPane.showMessageDialog(_editor.getMainFrame(), "Imports are only currently supported for new data sets.");
			return;
		}
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
		ImportExportStatusDialog statusDialog = new ImportExportStatusDialog(_editor.getMainFrame());
		_editor.show(statusDialog);
		
		// Do the import on a background thread.
		DoImportTask importTask = new DoImportTask(selectedDirectory, files);
		importTask.addTaskListener(new StatusUpdater(statusDialog));
		importTask.execute();
	}
	
	public void doSilentImport(File selectedDirectory, List<DirectiveFileInfo> files) {
		new DoImportTask(selectedDirectory, files).execute();
	}

	public boolean importDirectivesFile(DirectiveFileInfo fileInfo, Reader directivesReader, ImportExportStatus status) {
		
		String name = fileInfo.getName();
		
		DirectiveFile existing =  _model.getDirectiveFile(name);
		DirectiveFile directiveFile = _model.addDirectiveFile(_model.getDirectiveFileCount()+1, name, fileInfo.getType());
		directiveFile.setLastModifiedTime(System.currentTimeMillis());
		DirectiveFileImporter importer = new DirectiveFileImporter(this, directivesOfType(directiveFile.getType()));
		
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
			status.setTotalErrors(status.getTotalErrors()+1);
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
	
	@Override
	public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
		if (_handler != null) {
			_handler.preProcess(directive, data);
		}
	}

	@Override
	public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {
		if (_handler != null) {
			_handler.postProcess(directive);
		}
	}

	@Override
	public void handleUnrecognizedDirective(ImportContext context, List<String> controlWords) { 
		System.out.println(controlWords);
		
		if (_handler != null) {
			_handler.handleUnrecognizedDirective(context, controlWords);
		}
	}

	@Override
	public void handleDirectiveProcessingException(
			ImportContext context, AbstractDirective<ImportContext> d, Exception ex) {
		ex.printStackTrace();
		if (_handler != null) {
			_handler.handleDirectiveProcessingException(context, d, ex);
		}
	}
	
	
	public class DoImportTask extends Task<Void, ImportExportStatus> implements DirectiveImportHandler {

		private String _directoryName;
		private List<DirectiveFileInfo> _files;
		private ImportExportStatus _status = new ImportExportStatus();
		
		public DoImportTask(File directory, List<DirectiveFileInfo> files) {
			super(_editor);
			String directoryName = directory.getAbsolutePath();
			if (!directoryName.endsWith(File.separator)) {
				directoryName += File.separator;
			}
			_directoryName = directoryName;
			_files = files;
		}
		
		
		@Override
		protected Void doInBackground() throws Exception {
			
			publish(_status);
						
			for (DirectiveFileInfo file : _files) {
				
				File toParse = new File(_directoryName+file.getFileName());
				FileInputStream fileIn = new FileInputStream(toParse);
				InputStreamReader reader = new InputStreamReader(fileIn, _context.getFileEncoding());
				
				importDirectivesFile(file, reader, _status);
				
				// First check if the existing dataset has a directives file with the same name
				// and same last modified date.  If so, skip it.
				_status.setCurrentFile(file.getFileName());
				publish(_status);
				
				_status.setTotalLines(_status.getTotalLines()+1);
				publish(_status);
			}
			
			return null;
		}

		@Override
		protected void failed(Throwable cause) {
			cause.printStackTrace();
			super.failed(cause);
		}


		@Override
		public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
			_status.setCurrentDirective((String)directive.getName());
			publish(_status);
		}


		@Override
		public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {
			
		}


		@Override
		public void handleUnrecognizedDirective(ImportContext context, List<String> controlWords) {
			_status.setTotalErrors(_status.getTotalErrors()+1);
			publish(_status);
		}


		@Override
		public void handleDirectiveProcessingException(ImportContext context, AbstractDirective<ImportContext> d,
				Exception ex) {
			_status.setTotalErrors(_status.getTotalErrors()+1);
			
			publish(_status);
		}
	}
	
	/**
	 * Listens for import progress and updates the Status Dialog.
	 */
	private class StatusUpdater extends TaskListener.Adapter<Void, ImportExportStatus> {

		private ImportExportStatusDialog _statusDialog;
		
		public StatusUpdater(ImportExportStatusDialog statusDialog) {
			_statusDialog = statusDialog;
		}
		@Override
		public void process(TaskEvent<List<ImportExportStatus>> event) {
			
			_statusDialog.update(event.getValue().get(0));
		}
		
	}
	
	
}
