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
package au.org.ala.delta.io;

import au.org.ala.delta.DeltaContext.OutputFormat;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.translation.PrintFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages CONFOR output files.
 */
public class OutputFileSelector extends OutputFileManager {
	
	enum ConforOutputFileType {
		PRINT_FILE("PRINT"), 
		INDEX_FILE("INDEX"), 
		KEY_OUTPUT_FILE("KEY"), 
		INTKEY_OUTPUT_FILE("INTKEY"), 
		DIST_OUTPUT_FILE("DIST");
		
		String _name;
		
		private ConforOutputFileType(String name) {
			_name = name;
		}
		
		public String getName() {
			return _name;
		}
	}
	
	public static final int DEFAULT_PRINT_WIDTH = 80;
	
	private int _characterForOutputFiles = 0;
	private Map<String, String> _itemOutputFiles = new HashMap<String, String>();
	private Set<Integer> _newFileItems = new HashSet<Integer>();
	private MutableDeltaDataSet _dataSet;
	private String _subjectForOutputFiles;
	private String _imageDirectory;
	private PrintStream _printStream;
	private PrintFile _printFile;
	private String _printFileName;
	private int _outputFileIndex;
	private PrintFile _indexFile;
	/** output when a new print file is created */
	private String _printFileHeaderText;
	/** Output at the end of the print file */
	private String _printFileFooterText;
	
	/** Number of characters on a line of text written to the print file */
	private int _printWidth;
	
	private List<OutputFile> _archivedFiles;
	private Map<ConforOutputFileType, OutputFile> _currentFiles;
	
	public OutputFileSelector(MutableDeltaDataSet dataSet) {
		init(dataSet);
	}
	
	public OutputFileSelector(MutableDeltaDataSet dataSet, PrintStream out, PrintStream err) {
		super(out, err);
		init(dataSet);
	}
	
	private void init(MutableDeltaDataSet dataSet) {
		_dataSet = dataSet;
		_printWidth = DEFAULT_PRINT_WIDTH;
		_outputWidth = DEFAULT_PRINT_WIDTH;
		_outputFileIndex = 1;
		_archivedFiles = new ArrayList<OutputFileManager.OutputFile>();
		_currentFiles = new HashMap<OutputFileSelector.ConforOutputFileType, OutputFileManager.OutputFile>();
		
	}

	/**
	 * Returns the filename of the file that the translated output from the 
	 * supplied item should be written to.
	 * This is used both during natural language translation to determine 
	 * the output file and by the intkey translation to write the file name
	 * for the item to the intkey items file.
	 * @param itemNumber the item to determine the output file for.
	 * @return the name of the file the translated output for the supplied
	 * item should go to.
	 */
	public String getItemOutputFile(int itemNumber) {
		
		String outputFile = "";
		// This basically says use the current output file if a new one is
		// not specified for this item.  (to mimic CONFOR behaviour).
		while (itemNumber >= 1 && StringUtils.isEmpty(outputFile)) {
			if (_characterForOutputFiles == 0) {
				String description = itemDescriptionFor(itemNumber);
				outputFile = _itemOutputFiles.get(description);
			}
			else {
				Attribute attribute = _dataSet.getAttribute(itemNumber, _characterForOutputFiles);
				if (attribute != null) {
					outputFile = outputFileFromAttribute(attribute);
				}
			}
			itemNumber--;
		}
		outputFile = prependOutputDirectory(outputFile);
		return addExtension(outputFile);
	}

	protected String outputFileFromAttribute(Attribute attribute) {
		String outputFile;
		AttributeFormatter formatter = new AttributeFormatter(false, true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, null);
		outputFile = formatter.formatAttribute(attribute);
		
		if (outputFile.contains(" ")) {
			outputFile = outputFile.substring(0, outputFile.indexOf(" "));
		}
		return outputFile;
	}
	
	/**
	 * Determines and adds an appropriate filename extension to an output file
	 * based on the type of translation being performed.  If the filename
	 * already includes a '.', no extension will be appended.
	 * @param outputFile the output file.
	 * @return the output file with an extension added.
	 */
	private String addExtension(String outputFile) {
		
		if (StringUtils.isEmpty(outputFile)) {
			return "";
		}
		StringBuilder output = new StringBuilder(outputFile);
		if (outputFile.indexOf('.') < 0) {
			switch (_outputFormat) {
			case RTF:
				output.append(".rtf");
				break;
			case HTML:
				output.append(".htm");
				break;
			default:
				output.append(".prt");
				break;
			}
		}
		return output.toString();
	}
	
	public void setSubjectForOutputFiles(String subjectForOutputFiles) {
		_subjectForOutputFiles = subjectForOutputFiles;
	}
	
	public String getSubjectForOutputFiles() {
		return _subjectForOutputFiles;
	}
	
	public void setItemOutputFile(String itemDescription, String fileName) {
		_itemOutputFiles.put(RTFUtils.stripFormatting(itemDescription), fileName);
	}
	
	public Iterator<String> itemOutputFileDescriptions() {
		return _itemOutputFiles.keySet().iterator();
	}
	
	private String itemDescriptionFor(int itemNumber) {
		Item item = _dataSet.getItem(itemNumber);
		String description = RTFUtils.stripFormatting(item.getDescription());
		return description;
	}
	
	protected String getFilePath(ConforOutputFileType fileType) throws DirectiveException {
		OutputFile file = _currentFiles.get(fileType);
		if (file == null) {
			throw DirectiveError.asException(DirectiveError.Error.MISSING_OUTPUT_FILE, 0, fileType.getName());
		}
		return makeAbsolute(file.getFileName());
	}
	
	protected void addAndArchive(ConforOutputFileType fileType, String fileName) {
		BinaryOutputFile file = new BinaryOutputFile(fileName);
		if (_currentFiles.containsKey(fileType)) {
			_archivedFiles.add(_currentFiles.get(fileType));
		}
		_currentFiles.put(fileType, file);
	}
	
	public String getIntkeyOutputFilePath() throws DirectiveException {
		return getFilePath(ConforOutputFileType.INTKEY_OUTPUT_FILE);
	}
	
	public void setIntkeyOutputFile(String intkeyOut) {
		addAndArchive(ConforOutputFileType.INTKEY_OUTPUT_FILE, intkeyOut);
	}
	
	public String getKeyOutputFilePath() throws DirectiveException {
		return getFilePath(ConforOutputFileType.KEY_OUTPUT_FILE);
	}
	
	public void setKeyOutputFile(String keyOut) {
		addAndArchive(ConforOutputFileType.KEY_OUTPUT_FILE, keyOut);
	}
	
	public void setDistOutputFile(String outputFile) {
		addAndArchive(ConforOutputFileType.DIST_OUTPUT_FILE, outputFile);
	}
	
	public String getDistOutputFilePath() throws DirectiveException {
		return getFilePath(ConforOutputFileType.DIST_OUTPUT_FILE);
	}
	
	public void setCharacterForOutputFiles(int character) {
		_characterForOutputFiles = character;
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		_outputFormat = outputFormat;
	}
	
	public void addNewFileAtItem(int itemNumber) {
		_newFileItems.add(itemNumber);
	}
	
	public boolean getNewFileAtItem(int itemNumber) {
		return _newFileItems.contains(itemNumber);
	}
	
	/**
	 * When typesetting marks are being used, some are required to be output
	 * when a new print file is created.  The string supplied to this method
	 * will be output to the print file whenever a new file is created.
	 * @param headerText
	 */
	public void setPrintFileHeader(String headerText) {
		_printFileHeaderText = headerText;
		if (_printFile != null) {
			_printFile.setNewFileHeader(headerText);
		}
	}
	
	/**
	 * When typesetting marks are being used, some are required to be output
	 * when before a file is closed.  The string supplied to this method
	 * will be outputn at the end of the print file.
	 * @param footer the file footer.
	 */
	public void setPrintFileFooter(String footer) {
		_printFileFooterText = footer;
		if (_printFile != null) {
			_printFile.setFileFooter(footer);
		}
	}
	
	public void setPrintWidth(int printWidth) {
		_printWidth = printWidth;
		if (_printFile != null) {
			_printFile.setPrintWidth(printWidth);
		}
	}
	
	public void setPrintFileName(String filename) throws Exception {
		_printFileName = filename;
		recreatePrintFile();
	}
	
	void recreatePrintFile() throws Exception {
		
		closeExistingPrintStream();
		
		TextOutputFile printFile = (TextOutputFile)_currentFiles.get(ConforOutputFileType.PRINT_FILE);
		if (printFile != null) {
			_archivedFiles.add(printFile);
		}
		
		printFile = new TextOutputFile(_printFileName);
		_currentFiles.put(ConforOutputFileType.PRINT_FILE, printFile);
		
		_printStream = printFile.getPrintStream();
		
		if (StringUtils.isNotBlank(_printFileHeaderText)) {
			_printStream.println(_printFileHeaderText);
		}
		if (_printFile == null) {
			_printFile = new PrintFile(_printStream, _printWidth);
		}
		else {
			_printFile.setPrintStream(_printStream);
		}
		_printFile.setNewFileHeader(_printFileHeaderText);
		_printFile.setFileFooter(_printFileFooterText);
		
	}
	
	public void setPrintStream(PrintStream stream) {
		_printStream = stream;
		if (_printFile == null) {
			_printFile = new PrintFile(stream, _printWidth);
		}
		else {
			_printFile.setPrintStream(stream);
		}
	}
	
	public PrintFile getPrintFile() {
		return _printFile;
	}

    /**
     * Currently used by the IndexWriter if unable to determine a file name for an Item (e.g. if there is no
     * directives that specifiy item file names).
     * @return the nane of the current print file, including the output directory, if specified.
     */
    public String getPrintFileName() {
        return prependOutputDirectory(_printFileName);
    }
	
	public boolean createNewFileIfRequired(Item item) {
		
		boolean newFile = false;
		int itemNum = item.getItemNumber();
		String fileName = getItemOutputFile(itemNum);
		if (StringUtils.isEmpty(fileName)) {
			if (_newFileItems.contains(itemNum)) {
				fileName = _printFileName + _outputFileIndex;
				_outputFileIndex++;
			}
		}
		if (StringUtils.isNotEmpty(fileName)) {
			newFile = true;
			try {
				setPrintFileName(fileName);
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Unable to update print file!");
			}
		}
		return newFile;
	}

	public void setIndexOutputFile(String fileName) throws Exception {
		TextOutputFile indexFile = new TextOutputFile(fileName);
		_indexFile = new PrintFile(indexFile.getPrintStream(), _printWidth);
		_currentFiles.put(ConforOutputFileType.INDEX_FILE, indexFile);
	}
	
	public PrintFile getIndexFile() {
		return _indexFile;
	}

	public void setImageDirectory(String directoryName) {
		_imageDirectory = FilenameUtils.separatorsToSystem(directoryName);
	}
	
	public String getImageDirectory() {
		if (_imageDirectory == null) {
			_imageDirectory = "";
		}
		return _imageDirectory;
	}
	
	protected void closeExistingPrintStream() {
		if (_printStream != null && _printStream != _defaultOut && _printStream != _defaultErr) {
			if (_printFile != null) {
				_printFile.closePrintStream();
			}
			else {
				IOUtils.closeQuietly(_printStream);
			}
		}
	}
	
	@Override
	public void setOutputDirectory(String directory) throws Exception {
		super.setOutputDirectory(directory);
		if (_printFileName != null) {
			recreatePrintFile();
		}
	}

	@Override
	public List<File> getOutputFiles() {
		List<File> files = new ArrayList<File>(super.getOutputFiles());
		
		for (OutputFile file : _archivedFiles) {
			files.add(file.toFile());
		}
		
		for (OutputFile file : _currentFiles.values()) {
			files.add(file.toFile());
		}
		
		return files;
	}

	
	
}
