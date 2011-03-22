package au.org.ala.delta.editor.directives;

import java.io.File;
import java.util.List;

import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ImportExportStatusDialog;
import au.org.ala.delta.editor.ui.ImportExportDialog;
import au.org.ala.delta.editor.ui.ImportExportDialog.DirectiveFile;

/**
 * The ImportController manages the process of importing a set of directives files
 * into the current Editor dataset.
 */
public class ImportController {

	private DeltaEditor _context;
	
	
	public ImportController(DeltaEditor context) {
		_context = context;
	}
	
	public void begin() {
		
		ImportExportDialog dialog = new ImportExportDialog();
		
		_context.show(dialog);
		
		if (dialog.proceed()) {
		
			List<DirectiveFile> files = dialog.getSelectedFiles();
			File selectedDirectory = dialog.getSelectedDirectory();
			
			doImport(new ImportExportStatus());
		}
		
	}
	
	public void doImport(ImportExportStatus status) {
		ImportExportStatusDialog statusDialog = new ImportExportStatusDialog();
		
		statusDialog.update(status);
		
		_context.show(statusDialog);
	}
	
}
