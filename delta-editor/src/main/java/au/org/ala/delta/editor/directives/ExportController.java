package au.org.ala.delta.editor.directives;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import org.apache.commons.io.FilenameUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ExportViewModel;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog;
import au.org.ala.delta.editor.directives.ui.ImportExportViewModel;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.directive.DirectiveInOutState;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.util.FileUtils;

/**
 * The ExportController manages the process of exporting a set of directives
 * files from the data set to the file system.
 */
public class ExportController {
	private DeltaEditor _editor;
	private EditorViewModel _model;
	private ImportExportViewModel _exportModel;
	private ImportExportDialog _exportDialog;
	private ResourceMap _resources;
	private ActionMap _actions;
	private String _exportFileEncoding;
	
	public ExportController(DeltaEditor context) {
		_editor = context;
		_model = context.getCurrentDataSet();
		_resources = _editor.getContext().getResourceMap();
		_actions = _editor.getContext().getActionMap(this);
		_exportFileEncoding = "Cp1252";
	}

	public void begin(PropertyChangeListener listener) {
		_exportModel = new ExportViewModel();
		_exportModel.populate(_model);

		_exportDialog = new ImportExportDialog(_editor.getMainFrame(), _exportModel, "ExportDialog");
		_exportDialog.setDirectorySelectionAction(_actions.get("changeExportDirectory"));
		
		_editor.show(_exportDialog);

		if (_exportDialog.proceed()) {
			List<DirectiveFileInfo> files = _exportModel.getSelectedFiles();
			File selectedDirectory = _exportModel.getCurrentDirectory();
			_model.setExportPath(selectedDirectory.getAbsolutePath());
			doExport(selectedDirectory, files, listener);
		}
	}
	
	public void begin() {
		begin(null);
	}

	
	/**
	 * Exports the supplied directives files into the specified directory.
	 * @param selectedDirectory the directory to export the files to.
	 * @param files the files to export.
	 */
	public void doExport(File selectedDirectory, List<DirectiveFileInfo> files, PropertyChangeListener listener) {

		// Do the export on a background thread.
		DoExportTask exportTask = new DoExportTask(selectedDirectory, files, false);
		if (listener != null) {
			exportTask.addPropertyChangeListener(listener);
		}
		exportTask.execute();
	}
	
	@Action
	public void changeExportDirectory() {
		JFileChooser directorySelector = new JFileChooser(_exportModel.getCurrentDirectory());
		String directoryChooserTitle = _resources.getString("ExportDialog.directoryChooserTitle");
		directorySelector.setDialogTitle(directoryChooserTitle);
		directorySelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directorySelector.setAcceptAllFileFilterUsed(false);
		
		int result = directorySelector.showOpenDialog(_exportDialog);
		if (result == JFileChooser.APPROVE_OPTION) {
			_exportModel.setCurrentDirectory(directorySelector.getSelectedFile());
			_exportDialog.updateUI();
		}
	}

	public void doSilentExport(File selectedDirectory,
			List<DirectiveFileInfo> files) {
		new DoExportTask(selectedDirectory, files, true).execute();
	}
	
	public void writeDirectivesFile(DirectiveFile file, DirectiveInOutState state) {
		try {
			List<DirectiveInstance> directives = file.getDirectives();

			for (int i = 0; i < directives.size(); i++) {
				writeDirective(directives.get(i), state);
				if (i != directives.size() - 1) {
					state.getPrinter().writeBlankLines(1, 0);
				}
			}
			state.getPrinter().printBufferLine();
			file.setLastModifiedTime(System.currentTimeMillis());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (state.getPrinter() != null) {
				state.getPrinter().close();
			}
		}
	}
	
	private File createExportFile(DirectiveFile file, String directoryPath) {
		String fileName = file.getShortFileName();
		FileUtils.backupAndDelete(fileName, directoryPath);
		
		FilenameUtils.concat(directoryPath, fileName);
		File directivesFile = new File(directoryPath + fileName);
		
		return directivesFile;
	}

	protected void writeDirective(DirectiveInstance directive,
			DirectiveInOutState state) {
		
		state.setCurrentDirective(directive);
		Directive directiveInfo = directive.getDirective();

		directiveInfo.getOutFunc().process(state);
	}

	public class DoExportTask extends ImportExportTask {
		
		public DoExportTask(File directory, List<DirectiveFileInfo> files, boolean silent) {
			super(_editor, _model, directory, files, "export", silent);
		}

		@Override
		protected Void doInBackground() throws Exception {
			
			publish(_status);

			DirectiveFilesInitialiser initialiser = new DirectiveFilesInitialiser(_editor, _model);
			initialiser.buildSpecialDirFiles(_files);
			
			DirectiveInOutState state = new StatusUpdatingState(_model, _status);
			for (DirectiveFileInfo file : _files) {
				DirectiveFile dirFile = file.getDirectiveFile();
				if (dirFile != null) {
					File output = createExportFile(dirFile, _directoryName);
					state.setPrintStream(new PrintStream(output, _exportFileEncoding));
					_status.setCurrentFile(file);
					writeDirectivesFile(dirFile, state);
				}

				publish(_status);
			}
			_status.finish();
			publish(_status);

			return null;
		}
	}
	
	/**
	 * Wraps the DirectiveInOutState to allow status updates during the 
	 * export operation.
	 */
	private class StatusUpdatingState extends DirectiveInOutState {
		
		private ImportExportStatus _status;
		public StatusUpdatingState(EditorViewModel model, ImportExportStatus status) {
			super(model);
			_status = status;
		}

		@Override
		public void setCurrentDirective(DirectiveInstance directive) {
			
			_status.setCurrentDirective(directive);
			super.setCurrentDirective(directive);
		}
	}
}
