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
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the data displayed in the ImportExportDialog.
 */
public abstract class ImportExportViewModel {

	public static final String DEFAULT_ITEMS_DIRECTIVE_FILE = "items";
	public static final String DEFAULT_CHARS_DIRECTIVE_FILE = "chars";
	public static final String DEFAULT_SPECS_DIRECTIVE_FILE = "specs";

	protected DirectiveType _selectedDirectiveType = DirectiveType.CONFOR;
	protected File _currentDirectory;
	protected DirectiveFileInfo _specsFile;
	protected DirectiveFileInfo _charactersFile;
	protected DirectiveFileInfo _itemsFile;
	protected List<DirectiveFileInfo> _includedDirectivesFiles;
	protected List<DirectiveFileInfo> _excludedDirectiveFiles;
	protected boolean _specsDisabled;

	public ImportExportViewModel() {
		_includedDirectivesFiles = new ArrayList<DirectiveFileInfo>();
		_excludedDirectiveFiles = new ArrayList<DirectiveFileInfo>();
		_specsDisabled = false;
	}

	public void include(DirectiveFileInfo file) {

		file.setType(_selectedDirectiveType);
		_includedDirectivesFiles.add(file);
		_excludedDirectiveFiles.remove(file);
        file.setItemsFile(false);
        file.setCharsFile(false);
        file.setSpecsFile(false);
	}

	public void exclude(DirectiveFileInfo file) {
		_excludedDirectiveFiles.add(file);
		_includedDirectivesFiles.remove(file);
        file.setItemsFile(false);
        file.setCharsFile(false);
        file.setSpecsFile(false);
	}

	public void moveToSpecs(DirectiveFileInfo file) {

        if (file == null || StringUtils.isEmpty(file.getFileName())) {
            if (_specsFile != null) {
                _excludedDirectiveFiles.add(_specsFile);
            }
            _specsFile = null;
            return;
        }

        if (_specsFile != null) {
			_excludedDirectiveFiles.add(_specsFile);
		}
		_excludedDirectiveFiles.remove(file);
		_specsFile = file;
		file.setType(DirectiveType.CONFOR);
        file.setSpecsFile(true);

	}
	
	public boolean isSpecsDisabled() {
		return _specsDisabled;
	}
	
	public void moveToSpecs(String fileName) {
		moveToSpecs(new DirectiveFileInfo(fileName, DirectiveType.CONFOR));
	}

	public void moveToChars(DirectiveFileInfo file) {

        if (file == null || StringUtils.isEmpty(file.getFileName())) {
            if (_charactersFile != null) {
                _excludedDirectiveFiles.add(_charactersFile);
            }
            _charactersFile = null;
            return;
        }

        if (_charactersFile != null) {
			_excludedDirectiveFiles.add(_charactersFile);
		}
		_excludedDirectiveFiles.remove(file);
		_charactersFile = file;
		file.setType(DirectiveType.CONFOR);
        file.setCharsFile(true);
	}
	
	public void moveToChars(String fileName) {
		moveToChars(new DirectiveFileInfo(fileName, DirectiveType.CONFOR));
	}

	public void moveToItems(DirectiveFileInfo file) {

        if (file == null || StringUtils.isEmpty(file.getFileName())) {
            if (_itemsFile != null) {
                _excludedDirectiveFiles.add(_itemsFile);
            }
            _itemsFile = null;
            return;
        }
		if (_itemsFile != null) {
			_excludedDirectiveFiles.add(_itemsFile);
		}
		_excludedDirectiveFiles.remove(file);
		_itemsFile = file;
		file.setType(DirectiveType.CONFOR);
        file.setItemsFile(true);
	}

	public void moveToItems(String fileName) {
		moveToItems(new DirectiveFileInfo(fileName, DirectiveType.CONFOR));
	}
	
	public DirectiveType getSelectedDirectiveType() {
		return _selectedDirectiveType;
	}

	public void setSelectedDirectiveType(DirectiveType selectedDirectiveType) {
		this._selectedDirectiveType = selectedDirectiveType;
	}

	public File getCurrentDirectory() {
		return _currentDirectory;
	}

	public void setCurrentDirectory(File currentDirectory) {
		this._currentDirectory = currentDirectory;
	}

	public List<DirectiveFileInfo> getSelectedFiles() {

		List<DirectiveFileInfo> files = new ArrayList<DirectiveFileInfo>();
		if (!_specsDisabled) {
			addIfNotEmpty(_specsFile, files);
			addIfNotEmpty(_charactersFile, files);
		}
        addIfNotEmpty(_itemsFile, files);
		files.addAll(_includedDirectivesFiles);

		return files;
	}

	private void addIfNotEmpty(DirectiveFileInfo fileInfo, List<DirectiveFileInfo> files) {
		if (fileInfo != null) {
			files.add(fileInfo);
		}
	}

	public boolean isImportable() {
		return (_currentDirectory != null) && 
		  (!_includedDirectivesFiles.isEmpty() || 
		  _charactersFile != null || 
		  _itemsFile != null);
	}

	public DirectiveFileInfo getSpecsFile() {
		return _specsFile;
	}

	public void setSpecsFile(DirectiveFileInfo specsFile) {
		this._specsFile = specsFile;
	}

	public DirectiveFileInfo getCharactersFile() {
		return _charactersFile;
	}

	public void setCharactersFile(DirectiveFileInfo charactersFile) {
		this._charactersFile = charactersFile;
	}

	public DirectiveFileInfo getItemsFile() {
		return _itemsFile;
	}

	public void setItemsFile(DirectiveFileInfo itemsFile) {
		this._itemsFile = itemsFile;
        itemsFile.setItemsFile(true);
	}

	public List<DirectiveFileInfo> getIncludedDirectivesFiles() {
		return _includedDirectivesFiles;
	}

	public void setIncludedDirectivesFiles(
			List<DirectiveFileInfo> includedDirectivesFiles) {
		this._includedDirectivesFiles = includedDirectivesFiles;
	}

	public List<DirectiveFileInfo> getExcludedDirectiveFiles() {
		return _excludedDirectiveFiles;
	}

	public void setExcludedDirectiveFiles(List<DirectiveFileInfo> excludedDirectiveFiles) {
		this._excludedDirectiveFiles = excludedDirectiveFiles;
	}
	
	public abstract void populate(EditorViewModel model);
}
