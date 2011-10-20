package au.org.ala.delta.io;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext.OutputFormat;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.translation.PrintFile;

/**
 * Manages CONFOR output files.
 */
public class OutputFileSelector {
	
	public static final String OUTPUT_FILE_ENCODING = "utf-8";
	public static final String RTF_OUTPUT_FILE_ENCODING = "cp1252";
	public static final int DEFAULT_PRINT_WIDTH = 80;
	
	private int _characterForOutputFiles = 0;
	private Map<String, String> _itemOutputFiles = new HashMap<String, String>();
	private Set<Integer> _newFileItems = new HashSet<Integer>();
	private DeltaDataSet _dataSet;
	private String _subjectForOutputFiles;
	private String _intkeyOutputFile;
	private String _keyOutputFile;
	private String _distOutputFile;
	private String _outputDirectory;
	private String _imageDirectory;
	private String _printFileName;
	private PrintStream _printStream;
	private PrintFile _printFile;
	private ParsingContext _context;
	private OutputFormat _outputFormat;
	private int _outputFileIndex;
	private PrintFile _indexFile;
	private String _indexFileName;
	private String _outputFileName;
	private PrintFile _outputFile;
	
	/** output when a new print file is created */
	private String _printFileHeaderText;
	/** Output at the end of the print file */
	private String _printFileFooterText;
	
	/** Number of characters on a line of text written to the print file */
	private int _printWidth;
	
	public OutputFileSelector(DeltaDataSet dataSet) {
		_dataSet = dataSet;
		_printWidth = DEFAULT_PRINT_WIDTH;
		_outputFileIndex = 1;
	}

	public void setParsingContext(ParsingContext context) {
		_context = context;
	}
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
					AttributeFormatter formatter = new AttributeFormatter(false, true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, false, null);
					outputFile = formatter.formatAttribute(attribute);
				}
			}
			itemNumber--;
		}
		outputFile = prependOutputDirectory(outputFile);
		return addExtension(outputFile);
	}
	
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
	
	private String itemDescriptionFor(int itemNumber) {
		Item item = _dataSet.getItem(itemNumber);
		String description = RTFUtils.stripFormatting(item.getDescription());
		return description;
	}
	
	public String getIntkeyOutputFilePath() {
		return makeAbsolute(_intkeyOutputFile);
	}
	
	public void setIntkeyOutputFile(String intkeyOut) {
		_intkeyOutputFile = intkeyOut;
	}

	public String getKeyOutputFile() {
		return makeAbsolute(_keyOutputFile);
	}
	
	public void setKeyOutputFile(String keyOut) {
		_keyOutputFile = keyOut;
	}
	
	private String makeAbsolute(String fileName) {
		File file = new File(fileName);
		if (!file.isAbsolute()) {
			File workingDir = _context.getFile().getParentFile();
			file = new File(workingDir, fileName);
		}
		return file.getAbsolutePath();
	}

	public void setCharacterForOutputFiles(int character) {
		_characterForOutputFiles = character;
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		_outputFormat = outputFormat;
	}

	public String getKeyOutputFilePath() {
		return makeAbsolute(_keyOutputFile);
	}

	public void setDistOutputFile(String outputFile) {
		_distOutputFile = outputFile;
	}
	
	public String getDistOutputFilePath() {
		return makeAbsolute(_distOutputFile);
	}

	public void addNewFileAtItem(int itemNumber) {
		_newFileItems.add(itemNumber);
	}
	
	public boolean getNewFileAtItem(int itemNumber) {
		return _newFileItems.contains(itemNumber);
	}
	
	public void setOutputDirectory(String directory) throws Exception {
		_outputDirectory = FilenameUtils.separatorsToSystem(directory);
		if (_printFileName != null) {
			recreatePrintFile();
		}
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
		_printFileName = FilenameUtils.separatorsToSystem(filename);
		
		recreatePrintFile();
	}
	
	private void recreatePrintFile() throws Exception {
		
		closeExistingPrintStream();
		
		_printStream = createPrintStream(_printFileName);
		
		if (StringUtils.isNotEmpty(_printFileHeaderText)) {
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
	
	private PrintStream createPrintStream(String fileName) throws Exception {
		File parent = _context.getFile().getParentFile();
		
		String tmpFileName = prependOutputDirectory(fileName);
		File file = new File(tmpFileName);
		if (!file.isAbsolute()) {
			file = new File(FilenameUtils.concat(parent.getAbsolutePath(), tmpFileName));
		}
		FileUtils.forceMkdir(file.getParentFile());
		
		PrintStream printStream = new PrintStream(file, outputFileEncoding());
		
		return printStream;
	}
	
	private String prependOutputDirectory(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return "";
		}
		String outputFileName = fileName;
		if (!fileName.contains(File.separator) && (_outputDirectory != null)) {
			outputFileName = FilenameUtils.concat(_outputDirectory, fileName);
		}
		return outputFileName;
	}
	
	private void closeExistingPrintStream() {
		if (_printStream != null && _printStream != System.out && _printStream != System.err) {
			if (_printFile != null) {
				_printFile.closePrintStream();
			}
			else {
				IOUtils.closeQuietly(_printStream);
			}
		}
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
	
	private String outputFileEncoding() {
		if (_outputFormat == OutputFormat.RTF) {
			return RTF_OUTPUT_FILE_ENCODING;
		}
		else {
			return OUTPUT_FILE_ENCODING;
		}
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
		_indexFileName = FilenameUtils.separatorsToSystem(fileName);
		PrintStream indexStream = createPrintStream(_indexFileName);
		_indexFile = new PrintFile(indexStream, _printWidth);
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
	
	public File getPrintFileAsFile() {
		return fullPathOf(_printFileName);
	}
	
	public File getIndexFileAsFile() {
		return fullPathOf(_indexFileName);
	}
	
	private File fullPathOf(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		String parentPath = _context.getFile().getParent();
		fileName = FilenameUtils.concat(parentPath, prependOutputDirectory(fileName));
		return new File(fileName);
	}

	public void setOutputFileName(String outputFile) throws Exception {
		_outputFileName = FilenameUtils.separatorsToSystem(outputFile);
		PrintStream indexStream = createPrintStream(_outputFileName);
		_outputFile = new PrintFile(indexStream, _printWidth);
	}
	
	public PrintFile getOutputFile() {
		return _outputFile;
	}
}
