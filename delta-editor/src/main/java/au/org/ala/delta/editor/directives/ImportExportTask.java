package au.org.ala.delta.editor.directives;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ImportExportStatusDialog;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.ui.RichTextDialog;

/**
 * The ImportExportTask is a helper class for the import and export 
 * operations.  It handles the common functions of updating progress and
 * producing a progress report.
 */
public abstract class ImportExportTask extends Task<Void, ImportExportStatus> implements DirectiveImportHandler {
	
	protected String _directoryName;
	protected List<DirectiveFileInfo> _files;
	protected ImportExportStatus _status;
	
	public ImportExportTask(
			DeltaEditor editor, EditorViewModel model, 
			File directory, List<DirectiveFileInfo> files, String resourcePrefix)  {
		this(editor, model, directory, files, resourcePrefix, false);
	}
	
	public ImportExportTask(
			DeltaEditor editor, EditorViewModel model, 
			File directory, List<DirectiveFileInfo> files, String resourcePrefix, boolean silent) {
		super(editor);
		String directoryName = directory.getAbsolutePath();
		if (!directoryName.endsWith(File.separator)) {
			directoryName += File.separator;
		}
		_status = new ImportExportStatus(editor.getContext().getResourceMap(), resourcePrefix+"Report");
		_status.setHeading(model.getName());
		_status.setImportDirectory(directoryName);
		_directoryName = directoryName;
		_files = files;
		if (!silent) {
			addTaskListener(new StatusUpdater(editor, resourcePrefix));
		}
	}
	
	@Override
	protected void failed(Throwable cause) {
		cause.printStackTrace();
		super.failed(cause);
	}
	
	@Override
	public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
		_status.setCurrentDirective(directive, data);
		publish(_status);
	}

	@Override
	public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) { }

	@Override
	public void handleUnrecognizedDirective(ImportContext context, List<String> controlWords) {
		error("unrecognised directive " +controlWords);
	}

	@Override
	public void handleDirectiveProcessingException(AbstractDeltaContext context,
			AbstractDirective<? extends AbstractDeltaContext> directive, Exception ex) {
	}

	@Override
	public void handleDirectiveProcessingException(ImportContext context, AbstractDirective<ImportContext> d,
			Exception ex) {
		error(ex.getMessage());
	}
	
	@Override
	public void finishedProcessing() {}
	
	private void error(String message) {
		_status.error(message);
		publish(_status);
		
		if (_status.getPauseOnError()) {
			_status.pause();
			
			if (_status.isCancelled()) {
				cancel(false);
			}
		}
	}
	
	/**
	 * Listens for import progress and updates the Status Dialog.
	 */
	public static class StatusUpdater extends TaskListener.Adapter<Void, ImportExportStatus> {

		private ImportExportStatusDialog _statusDialog;
		private RichTextDialog _dialog;
		public StatusUpdater(DeltaEditor editor, String resourcePrefix) {
			_dialog = new RichTextDialog(editor.getMainFrame(), "");
			_statusDialog = new ImportExportStatusDialog(editor.getMainFrame(), resourcePrefix);
			editor.show(_statusDialog);
			editor.show(_dialog);
		}
		@Override
		public void process(TaskEvent<List<ImportExportStatus>> event) {
			ImportExportStatus status = event.getValue().get(0); 
			_statusDialog.update(status);
			String log = status.getImportLog();
			if (StringUtils.isNotEmpty(log)) {
				try {
				_dialog.setText(log);
				}
				catch (Exception e) {
					System.out.println(log);
					e.printStackTrace();
				}
			}
		}
	}
}
