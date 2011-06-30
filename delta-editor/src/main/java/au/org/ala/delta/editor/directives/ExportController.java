package au.org.ala.delta.editor.directives;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import javax.swing.JOptionPane;

import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog;
import au.org.ala.delta.editor.directives.ui.ImportExportStatusDialog;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.VODirFileDesc;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;

public class ExportController {
	private DeltaEditor _context;
	private EditorDataModel _model;
	
	public ExportController(DeltaEditor context) {
		_context = context;
		_model = context.getCurrentDataSet();
	}
	
	public void begin() {
		
		if ((_model.getNumberOfCharacters() > 0) || (_model.getMaximumNumberOfItems() > 0)) {
			JOptionPane.showMessageDialog(_context.getMainFrame(), "Imports are only currently supported for new data sets.");
			return;
		}
		ImportExportDialog dialog = new ImportExportDialog(_context.getMainFrame());
		_context.show(dialog);
		
		if (dialog.proceed()) {
			List<DirectiveFileInfo> files = dialog.getSelectedFiles();
			File selectedDirectory = dialog.getSelectedDirectory();
			
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
	
	File temp; 
	PrintStream out;
	private void writeDirectivesFile(DirectiveFile file, String directoryPath) {
		try {
		String fileName = file.getFileName();
		temp = new File(directoryPath+fileName);
		out = new PrintStream(temp);
		
		List<Dir> directives = file.getDirectives();
		for (Dir directive : directives) {
			writeDirective(directive);
		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	private void writeDirective(Dir directive) {
		
		StringBuilder textBuffer = new StringBuilder();
		
	    textBuffer.append('*');
	    int dirType = directive.getDirType();
	    if ((dirType & VODirFileDesc.DIRARG_COMMENT_FLAG) > 0) {
	    	  textBuffer.append("COMMENT ");
	          dirType &= ~VODirFileDesc.DIRARG_COMMENT_FLAG;
	    }
	    
	    Directive directiveInfo = ConforDirType.ConforDirArray[dirType];
	    textBuffer.append(directiveInfo.joinNameComponents());
	    
	    OutputTextBuffer(textBuffer.toString(), 0, 0, false);
	    
	    textBuffer = new StringBuilder();
	    	     
	    directiveInfo.getOutFunc().process(null);
	}
	
	private void OutputTextBuffer(String buffer, int startIndex, int indent, boolean terminate) {
		// TODO consider adapting the common "Printer" so it's useful here.
		out.println(buffer);
	}
	
}
