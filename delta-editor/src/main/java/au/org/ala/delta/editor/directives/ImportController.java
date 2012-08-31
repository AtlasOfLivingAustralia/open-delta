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

import java.io.File;
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
		File currentDirectory = _importModel.getCurrentDirectory();
		if (currentDirectory == null) {
			currentDirectory = new File(System.getProperty("user.dir"));
		}
		JFileChooser directorySelector = new JFileChooser(currentDirectory.getAbsolutePath());
		directorySelector.setSelectedFile(currentDirectory);
		
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
	
	public class DoImportTask extends ImportExportTask {
		
		public DoImportTask(File directory, List<DirectiveFileInfo> files, boolean silent) {
			super(_editor, _model, directory, files, "import", silent);
		}
		
		
		@Override
		protected Void doInBackground() throws Exception {
			
			publish(_status);
			DirectivesFileImporter importer = new DirectivesFileImporter(_model, _context);
			for (DirectiveFileInfo file : _files) {
				if (isCancelled()) {
					break;
				}
				File toParse = new File(_directoryName+file.getFileName());
				
				// First check if the existing dataset has a directives file with the same name
				// and same last modified date.  If so, skip it.
				_status.setCurrentFile(file);
				publish(_status);
				
				importer.importDirectivesFile(file, toParse, this);
				
				publish(_status);
			}
			_status.finish();
			publish(_status);
			
			return null;
		}
	}
	
}
