package au.org.ala.delta.editor.directives;

import java.io.File;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.ConforDirectiveFileParser;
import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog;
import au.org.ala.delta.editor.directives.ui.ImportExportStatusDialog;
import au.org.ala.delta.editor.directives.ui.ImportExportViewModel;
import au.org.ala.delta.editor.model.EditorDataModel;

/**
 * The ImportController manages the process of importing a set of directives files
 * into the current Editor dataset.
 */
public class ImportController {

	private DeltaEditor _context;
	private EditorDataModel _model;
	private ImportExportViewModel _importModel;
	private ImportExportDialog _importDialog;
	private ResourceMap _resources;
	private ActionMap _actions;
	
	public ImportController(DeltaEditor context) {
		_context = context;
		_resources = _context.getContext().getResourceMap();
		_actions = _context.getContext().getActionMap(this);
		_model = context.getCurrentDataSet();
	}
	
	public void begin() {
		
		if ((_model.getNumberOfCharacters() > 0) || (_model.getMaximumNumberOfItems() > 0)) {
			JOptionPane.showMessageDialog(_context.getMainFrame(), "Imports are only currently supported for new data sets.");
			return;
		}
		_importModel = new ImportExportViewModel();
		_importModel.setCurrentDirectory(new File(_model.getDataSetPath()));
		_importDialog = new ImportExportDialog(_context.getMainFrame(), _importModel);
		_importDialog.setDirectorySelectionAction(_actions.get("changeImportDirectory"));
		_context.show(_importDialog);
		
		
		if (_importDialog.proceed()) {
			List<DirectiveFileInfo> files = _importModel.getSelectedFiles();
			File selectedDirectory = _importModel.getCurrentDirectory();
			
			doImport(selectedDirectory, files);
		}
	}
	
	@Action
	public void changeImportDirectory() {
		JFileChooser directorySelector = new JFileChooser(_importModel.getCurrentDirectory());
		String directoryChooserTitle = _resources.getString("ImportExportDialog.directoryChooserTitle");
		directorySelector.setDialogTitle(directoryChooserTitle);
		directorySelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directorySelector.setAcceptAllFileFilterUsed(false);
		
		int result = directorySelector.showOpenDialog(_importDialog);
		if (result == JFileChooser.APPROVE_OPTION) {
			_importModel.setCurrentDirectory(directorySelector.getSelectedFile());
			_importModel.populateExcludedFromCurrentDirectory();
			_importDialog.updateUI();
		}
	}
	
	public void doImport(File selectedDirectory, List<DirectiveFileInfo> files) {
		ImportExportStatusDialog statusDialog = new ImportExportStatusDialog(_context.getMainFrame());
		_context.show(statusDialog);
		
		// Do the import on a background thread.
		DoImportTask importTask = new DoImportTask(selectedDirectory, files);
		importTask.addTaskListener(new StatusUpdater(statusDialog));
		importTask.execute();
	}
	
	public void doSilentImport(File selectedDirectory, List<DirectiveFileInfo> files) {
		new DoImportTask(selectedDirectory, files).execute();
	}

	
	public class DoImportTask extends Task<Void, ImportExportStatus> implements DirectiveParserObserver {

		private String _directoryName;
		private List<DirectiveFileInfo> _files;
		
		public DoImportTask(File directory, List<DirectiveFileInfo> files) {
			super(_context);
			String directoryName = directory.getAbsolutePath();
			if (!directoryName.endsWith(File.separator)) {
				directoryName += File.separator;
			}
			_directoryName = directoryName;
			_files = files;
		}
		
		
		@Override
		protected Void doInBackground() throws Exception {
			ImportExportStatus status = new ImportExportStatus();
			publish(status);
						
			DeltaContext context = new DeltaContext(_model);
			ConforDirectiveFileParser parser = ConforDirectiveFileParser.createInstance();
			int fileNumber = 1;
			for (DirectiveFileInfo file : _files) {
				
				_model.addDirectiveFile(fileNumber++, file.getFileName(), 0);
				// First check if the existing dataset has a directives file with the same name
				// and same last modified date.  If so, skip it.
				status.setCurrentFile(file.getFileName());
				publish(status);
				// Looks like we skip the specs file if we have non zero items or chars.....
				try {
				
					File directiveFile = new File(_directoryName+file.getFileName());
					parser.parse(directiveFile, context);
				}
				catch (Exception e) {
					status.setTotalErrors(status.getTotalErrors()+1);
					e.printStackTrace();
				}
				status.setTotalLines(status.getTotalLines()+1);
				publish(status);
			}
			
			return null;
		}

		@Override
		protected void failed(Throwable cause) {
			cause.printStackTrace();
			super.failed(cause);
		}	
			
		@Override
		public void preProcess(String data) {}

		@Override
		public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {
			//storeDirective(directive);
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
