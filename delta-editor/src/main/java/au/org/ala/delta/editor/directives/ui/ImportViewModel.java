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
package au.org.ala.delta.editor.directives.ui;

import au.org.ala.delta.editor.directives.DirectiveFileInfo;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the data displayed in the ImportExportDialog during an import 
 * operation.
 */
public class ImportViewModel extends ImportExportViewModel {

    private boolean _includeItemsFile;

	@Override
	public void populate(EditorViewModel model) {

        _includeItemsFile = model.getMaximumNumberOfItems() <= 0;

		populateExcludedFiles(model);
		
		populateIncludedFiles(model);
		
		setDefaults();
	}

	private void populateIncludedFiles(EditorViewModel model) {
		
		_specsDisabled = model.getNumberOfCharacters() > 0 || model.getMaximumNumberOfItems() > 0;
		
		int directiveFileCount = model.getDirectiveFileCount();
		
		_includedDirectivesFiles = new ArrayList<DirectiveFileInfo>();

        for (int i = 1; i <= directiveFileCount; i++) {
			DirectiveFile dirFile = model.getDirectiveFile(i);

			DirectiveFileInfo info = new DirectiveFileInfo(
					dirFile.getShortFileName(), dirFile.getType());
			info.setDirectiveFile(dirFile);


			if ((_includeItemsFile || !dirFile.isItemsFile()) && isOnImportPath(info)) {

				_excludedDirectiveFiles.remove(info);
				if (dirFile.isSpecsFile()) {
					setSpecsFile(info);
				} else if (dirFile.isCharsFile()) {
					setCharactersFile(info);
                } else if (dirFile.isItemsFile()) {
                    setItemsFile(info);
				} else {
					_includedDirectivesFiles.add(info);
				}
				
			}

		}
	}
	
	/**
	 * Checks if a supplied file exists as an importable file.  
	 * @param file the file to check.
	 * @return true if a file with this name is found in the current drirectory.
	 */
	private boolean isOnImportPath(DirectiveFileInfo file) {
		return _excludedDirectiveFiles.contains(file);
	}
	
	private void populateExcludedFiles(EditorViewModel model) {
		
		if (_currentDirectory == null) {
			String exportPath = model.getExportPath();
			if (StringUtils.isEmpty(exportPath)) {
				exportPath = model.getDataSetPath();
				
			}
			setCurrentDirectory(new File(exportPath));
		}
		
		_excludedDirectiveFiles = new ArrayList<DirectiveFileInfo>();
		for (File file : _currentDirectory.listFiles()) {
			if (!file.isDirectory()) {
				DirectiveFileInfo fileInfo = new DirectiveFileInfo(file.getName());
				_excludedDirectiveFiles.add(fileInfo);
			}
		}
	}

	private void setDefaults() {
		if (_specsFile == null && containsFileNamed(_excludedDirectiveFiles, DEFAULT_SPECS_DIRECTIVE_FILE)) {
			_specsFile = new DirectiveFileInfo(DEFAULT_SPECS_DIRECTIVE_FILE, DirectiveType.CONFOR);
			_excludedDirectiveFiles.remove(DEFAULT_SPECS_DIRECTIVE_FILE);
		}
		if (_charactersFile == null && containsFileNamed(_excludedDirectiveFiles, DEFAULT_CHARS_DIRECTIVE_FILE)) {
			_charactersFile = new DirectiveFileInfo(DEFAULT_CHARS_DIRECTIVE_FILE, DirectiveType.CONFOR);
			_excludedDirectiveFiles.remove(DEFAULT_CHARS_DIRECTIVE_FILE);
		}
		if (_includeItemsFile && _itemsFile == null && containsFileNamed(_excludedDirectiveFiles, DEFAULT_ITEMS_DIRECTIVE_FILE)) {
			_itemsFile = new DirectiveFileInfo(DEFAULT_ITEMS_DIRECTIVE_FILE, DirectiveType.CONFOR);
			_excludedDirectiveFiles.remove(DEFAULT_ITEMS_DIRECTIVE_FILE);
		}
	}
	
	private boolean containsFileNamed(List<DirectiveFileInfo> files, String name) {
		for (DirectiveFileInfo file : files) {
			if (name.equals(file.getFileName())) {
				return true;
			}
		}
		return false;
	}
}
