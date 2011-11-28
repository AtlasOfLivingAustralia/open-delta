/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.editor.directives;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.DeltaEditor;
import au.org.ala.delta.editor.directives.ui.ExportViewModel;
import au.org.ala.delta.editor.directives.ui.ImportExportDialog;
import au.org.ala.delta.editor.directives.ui.ImportExportViewModel;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.directive.DirectiveInOutState;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;

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

	public class DoExportTask extends ImportExportTask {
		
		public DoExportTask(File directory, List<DirectiveFileInfo> files, boolean silent) {
			super(_editor, _model, directory, files, "export", silent);
		}

		@Override
		protected Void doInBackground() throws Exception {
			
			publish(_status);

			DirectiveFilesInitialiser initialiser = new DirectiveFilesInitialiser(_editor, _model);
			initialiser.buildSpecialDirFiles(_files);
			DirectivesFileExporter exporter = new DirectivesFileExporter();
			
			DirectiveInOutState state = new StatusUpdatingState(_model, _status);
			for (DirectiveFileInfo file : _files) {
				DirectiveFile dirFile = file.getDirectiveFile();
				if (dirFile != null) {
					File output = exporter.createExportFile(dirFile, _directoryName);
					state.setPrintStream(new PrintStream(output, _exportFileEncoding));
					_status.setCurrentFile(file);
					exporter.writeDirectivesFile(dirFile, state);
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
