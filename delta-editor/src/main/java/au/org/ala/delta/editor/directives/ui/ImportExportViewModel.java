package au.org.ala.delta.editor.directives.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.directives.DirectiveFileInfo;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;

/**
 * Manages the data displayed in the ImportExportDialog.
 */
public class ImportExportViewModel {

	public static final String DEFAULT_ITEMS_DIRECTIVE_FILE = "items";
	public static final String DEFAULT_CHARS_DIRECTIVE_FILE = "chars";
	public static final String DEFAULT_SPECS_DIRECTIVE_FILE = "specs";

	private DirectiveType _selectedDirectiveType = DirectiveType.CONFOR;
	private File _currentDirectory;
	private String _specsFile;
	private String _charactersFile;
	private String _itemsFile;
	private List<DirectiveFileInfo> _includedDirectivesFiles;
	private List<String> _excludedDirectiveFiles;

	public ImportExportViewModel() {
		_specsFile = DEFAULT_SPECS_DIRECTIVE_FILE;
		_includedDirectivesFiles = new ArrayList<DirectiveFileInfo>();
		_excludedDirectiveFiles = new ArrayList<String>();
	}

	public void include(String file) {
		DirectiveFileInfo directiveFile = new DirectiveFileInfo((String) file,
				_selectedDirectiveType);

		_includedDirectivesFiles.add(directiveFile);
		_excludedDirectiveFiles.remove(file);
	}

	public void exclude(DirectiveFileInfo file) {
		_excludedDirectiveFiles.add(file.getFileName());
		_includedDirectivesFiles.remove(file);
	}

	public void moveToSpecs(String file) {
		if (StringUtils.isNotEmpty(_specsFile)) {
			_excludedDirectiveFiles.add(_specsFile);
		}
		_excludedDirectiveFiles.remove(file);
		_specsFile = file;

	}

	public void moveToChars(String file) {
		if (StringUtils.isNotEmpty(_charactersFile)) {
			_excludedDirectiveFiles.add(_charactersFile);
		}
		_excludedDirectiveFiles.remove(file);
		_charactersFile = file;
	}

	public void moveToItems(String file) {
		if (StringUtils.isNotEmpty(_itemsFile)) {
			_excludedDirectiveFiles.add(_itemsFile);
		}
		_excludedDirectiveFiles.remove(file);
		_itemsFile = file;
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
		_includedDirectivesFiles = new ArrayList<DirectiveFileInfo>();
		_excludedDirectiveFiles = new ArrayList<String>();

		for (File file : _currentDirectory.listFiles()) {
			_excludedDirectiveFiles.add(file.getName());

		}
		if (_excludedDirectiveFiles.contains(DEFAULT_SPECS_DIRECTIVE_FILE)) {
			_specsFile = DEFAULT_SPECS_DIRECTIVE_FILE;
			_excludedDirectiveFiles.remove(DEFAULT_SPECS_DIRECTIVE_FILE);
		}
		if (_excludedDirectiveFiles.contains(DEFAULT_CHARS_DIRECTIVE_FILE)) {
			_charactersFile = DEFAULT_CHARS_DIRECTIVE_FILE;
			_excludedDirectiveFiles.remove(DEFAULT_CHARS_DIRECTIVE_FILE);
		}
		if (_excludedDirectiveFiles.contains(DEFAULT_ITEMS_DIRECTIVE_FILE)) {
			_itemsFile = DEFAULT_ITEMS_DIRECTIVE_FILE;
			_excludedDirectiveFiles.remove(DEFAULT_ITEMS_DIRECTIVE_FILE);
		}

	}

	public List<DirectiveFileInfo> getSelectedFiles() {

		List<DirectiveFileInfo> files = new ArrayList<DirectiveFileInfo>();
		addIfNotEmpty(_specsFile, files);
		addIfNotEmpty(_charactersFile, files);
		addIfNotEmpty(_itemsFile, files);

		files.addAll(_includedDirectivesFiles);

		return files;
	}

	private void addIfNotEmpty(String fileName, List<DirectiveFileInfo> files) {
		if (StringUtils.isNotEmpty(fileName)) {
			DirectiveFileInfo specsFile = new DirectiveFileInfo(fileName,
					DirectiveType.CONFOR);
			files.add(specsFile);
		}
	}

	public boolean isImportable() {
		return (_currentDirectory != null) && 
		  (!_includedDirectivesFiles.isEmpty() || 
		   StringUtils.isNotEmpty(_charactersFile) || 
		   StringUtils.isNotEmpty(_itemsFile));
	}

	public String getSpecsFile() {
		return _specsFile;
	}

	public void setSpecsFile(String specsFile) {
		this._specsFile = specsFile;
	}

	public String getCharactersFile() {
		return _charactersFile;
	}

	public void setCharactersFile(String charactersFile) {
		this._charactersFile = charactersFile;
	}

	public String getItemsFile() {
		return _itemsFile;
	}

	public void setItemsFile(String itemsFile) {
		this._itemsFile = itemsFile;
	}

	public List<DirectiveFileInfo> getIncludedDirectivesFiles() {
		return _includedDirectivesFiles;
	}

	public void setIncludedDirectivesFiles(
			List<DirectiveFileInfo> includedDirectivesFiles) {
		this._includedDirectivesFiles = includedDirectivesFiles;
	}

	public List<String> getExcludedDirectiveFiles() {
		return _excludedDirectiveFiles;
	}

	public void setExcludedDirectiveFiles(List<String> excludedDirectiveFiles) {
		this._excludedDirectiveFiles = excludedDirectiveFiles;
	}
}
