package au.org.ala.delta.editor.directives;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog;
import au.org.ala.delta.editor.directives.ui.ImportExportStatusDialog;
import au.org.ala.delta.editor.directives.ui.ImportExportViewModel;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.directive.DirectiveInOutState;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.translation.Printer;

/**
 * The ExportController manages the process of exporting a set of directives files
 * from the data set to the file system.
 */
public class ExportController {
	private DeltaEditor _context;
	private EditorDataModel _model;
	
	public ExportController(DeltaEditor context) {
		_context = context;
		_model = context.getCurrentDataSet();
	}
	
	public void begin() {
		ImportExportViewModel model = new ImportExportViewModel();
		ImportExportDialog dialog = new ImportExportDialog(_context.getMainFrame(), model);
		_context.show(dialog);
		
		if (dialog.proceed()) {
			List<DirectiveFileInfo> files = model.getSelectedFiles();
			File selectedDirectory = model.getCurrentDirectory();
			
			doExport(selectedDirectory, files);
		}
	}
	
	public void doExport(File selectedDirectory, List<DirectiveFileInfo> files) {
		ImportExportStatusDialog statusDialog = new ImportExportStatusDialog(_context.getMainFrame());
		_context.show(statusDialog);
		
		// Do the import on a background thread.
		DoExportTask importTask = new DoExportTask(selectedDirectory, files);
		importTask.addTaskListener(new StatusUpdater(statusDialog));
		importTask.execute();
	}
	
	public void doSilentExport(File selectedDirectory, List<DirectiveFileInfo> files) {
		new DoExportTask(selectedDirectory, files).execute();
	}
	
	
	public class DoExportTask extends Task<Void, ImportExportStatus> implements DirectiveParserObserver {

		private String _directoryName;
		private List<DirectiveFileInfo> _files;
		
		public DoExportTask(File directory, List<DirectiveFileInfo> files) {
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
						
			for (DirectiveFileInfo file : _files) {				
				DirectiveFile dirFile = file.getDirectiveFile();
				if (dirFile != null) {
					writeDirectivesFile(dirFile, _directoryName);
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
	
	private void writeDirectivesFile(DirectiveFile file, String directoryPath) {
		DirectiveInOutState state = new DirectiveInOutState();
		try {
			state = createExportState(file, directoryPath);
			List<DirectiveInstance> directives = file.getDirectives();
			
			for (int i=0; i<directives.size(); i++) {
				writeDirective(directives.get(i), state);
				if (i != directives.size()-1) {
					state.getPrinter().writeBlankLines(1, 0);
				}
			}
			state.getPrinter().printBufferLine();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (state.getPrinter() != null) {
				state.getPrinter().close();
			}
		}
	}

	private DirectiveInOutState createExportState(DirectiveFile file,
			String directoryPath) throws FileNotFoundException,
			UnsupportedEncodingException {
		DirectiveInOutState state;
		String fileName = file.getShortFileName();
		FilenameUtils.concat(directoryPath, fileName);
		File temp = new File(directoryPath+fileName);
		PrintStream out = new PrintStream(temp, "utf-8");
		Printer printer = new Printer(out, 80);
		printer.setIndentOnLineWrap(true);
		printer.setSoftWrap(true);
		printer.setIndent(2);
		state = new DirectiveInOutState();
		state.setPrinter(printer);
		state.setDataSet(_model);
		return state;
	}
	
	protected void writeDirective(DirectiveInstance directive, DirectiveInOutState state) {
		
		state.setCurrentDirective(directive);
	    Directive directiveInfo = directive.getDirective();
	   
	    directiveInfo.getOutFunc().process(state);
	    
	}
	
}
