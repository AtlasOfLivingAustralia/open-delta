package au.org.ala.delta.editor.directives;

import java.io.File;
import java.util.List;

import org.jdesktop.application.Task;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.DirectiveFileParser;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog.DirectiveFile;
import au.org.ala.delta.editor.directives.ui.ImportExportStatusDialog;

/**
 * The ImportController manages the process of importing a set of directives files
 * into the current Editor dataset.
 */
public class ImportController {

	private DeltaEditor _context;
	private ImportExportStatusDialog _statusDialog;
	
	
	public ImportController(DeltaEditor context) {
		_context = context;
	}
	
	public void begin() {
		
		ImportExportDialog dialog = new ImportExportDialog();
		_context.show(dialog);
		
		if (dialog.proceed()) {
			List<DirectiveFile> files = dialog.getSelectedFiles();
			File selectedDirectory = dialog.getSelectedDirectory();
			
			doImport(selectedDirectory, files);
		}
	}
	
	public void doImport(File selectedDirectory, List<DirectiveFile> files) {
		_statusDialog = new ImportExportStatusDialog();
		_context.show(_statusDialog);
		
		// Do the import on a background thread.
		new DoImportTask(selectedDirectory, files).execute();
	}
	
	public class DoImportTask extends Task<Void, ImportExportStatus> {

		private String _directoryName;
		private List<DirectiveFile> _files;
		
		public DoImportTask(File directory, List<DirectiveFile> files) {
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
						
			for (DirectiveFile file : _files) {
				
				// First check if the existing dataset has a directives file with the same name
				// and same last modified date.  If so, skip it.
				
				// Looks like we skip the specs file if we have non zero items or chars.....
				
				
				DeltaContext context = new DeltaContext(_context.getCurrentDataSet());
				
				DirectiveFileParser parser = new DirectiveFileParser();
				
				File directiveFile = new File(_directoryName+file._fileName);
				parser.parse(directiveFile, context);
				
			}
			
			return null;
		}

		@Override
		protected void process(List<ImportExportStatus> values) {
			_statusDialog.update(values.get(0));
		}
		
	}
}
