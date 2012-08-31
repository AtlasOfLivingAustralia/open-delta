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
package au.org.ala.delta.delfor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import au.org.ala.delta.delfor.format.FormattingAction;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.editor.directives.DirectiveFileInfo;
import au.org.ala.delta.editor.directives.DirectiveImportHandlerAdapter;
import au.org.ala.delta.editor.directives.DirectivesFileExporter;
import au.org.ala.delta.editor.directives.DirectivesFileImporter;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.directive.DirectiveInOutState;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.util.FileUtils;
import au.org.ala.delta.util.Pair;

/**
 * The DirectivesFileFormatter class does the work of reformatting the
 * directives files.  It achieves this by importing all of the specified
 * directives files into the dataset, running the specified reformatting
 * actions (such as reordering characters/states etc) then exporting the
 * directives files.
 */
public class DirectivesFileFormatter {

	private DelforContext _context;
	private EditorDataModel _model;
	
	public DirectivesFileFormatter(DelforContext context) {
		_context = context;
		_model = new EditorDataModel((AbstractObservableDataSet)context.getDataSet());
	}
	
	public void reformat() throws DirectiveException {
		List<Pair<File, String>> toReformat = _context.getFilesToReformat();
		
		importAll(toReformat);
		
		runFormattingActions();
		
		exportAll(toReformat);
	}

	protected void exportAll(List<Pair<File, String>> toReformat) throws DirectiveException {
		for (Pair<File, String> fileInfo : toReformat) {
			
			File file = fileInfo.getFirst();
			DirectiveFile directiveFile = _model.getDirectiveFile(file.getName());
			
			try {
				DirectivesFileExporter exporter = new DirectivesFileExporter();
				DirectiveInOutState state = createState(directiveFile, file, fileInfo.getSecond(), _model);
				exporter.writeDirectivesFile(directiveFile, state);
			}
			catch (IOException e) {
				e.printStackTrace();
				throw DirectiveError.asException(DirectiveError.Error.ALL_CHARACTERS_EXCLUDED, 0);
			}
		}
	}

	protected void importAll(List<Pair<File, String>> toReformat) throws DirectiveException {
		DirectivesFileImporter importer = new DelforDirectivesFileImporter(_model, _context);
		DirectivesFileClassifier classifier = new DirectivesFileClassifier(_context);
		for (Pair<File, String> fileInfo : toReformat) {
			
			File file = fileInfo.getFirst();
			try {
				DirectiveType type = classifier.classify(file);
				DirectiveFileInfo directiveInfo = new DirectiveFileInfo(file.getName(), type);
			
				importer.importDirectivesFile(directiveInfo, file, new DirectiveImportHandlerAdapter());
			}
			catch (IOException e) {
				e.printStackTrace();
				throw DirectiveError.asException(DirectiveError.Error.ALL_CHARACTERS_EXCLUDED, 0);
			}
		}
	}
	
	private DirectiveInOutState createState(
			DirectiveFile directiveFile, File file, String outputFileName, EditorViewModel model) throws IOException {
		
		File outputFile = new File(file.getParentFile(), outputFileName);
		FileUtils.backupAndDelete(outputFileName, outputFile.getParent());
		DirectiveInOutState state = new DirectiveInOutState(model);
		if (_context.getNewLineForAttributes()) {
			state.setNewLineAfterAttributes(true);
		}
		
		state.setPrintStream(new PrintStream(outputFile, _context.getFileEncoding().name()));
		state.getPrinter().setPrintWidth(_context.getOutputWidth());
		return state;
	}
	
	private void runFormattingActions() {
		for (FormattingAction action : _context.getFormattingActions()) {
			action.format(_context, (SlotFileDataSet)_context.getDataSet());
		}
	}
	
}
