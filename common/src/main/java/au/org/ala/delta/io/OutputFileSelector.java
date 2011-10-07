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

/**
 * Manages CONFOR output files.
 */
public class OutputFileSelector {
	
	public static final String OUTPUT_FILE_ENCODING = "utf-8";
	
	private int _characterForOutputFiles = 0;
	private Map<String, String> _itemOutputFiles = new HashMap<String, String>();
	private Set<Integer> _newFileItems = new HashSet<Integer>();
	private DeltaDataSet _dataSet;
	private String _subjectForOutputFiles;
	private String _intkeyOutputFile;
	private String _keyOutputFile;
	private String _distOutputFile;
	private String _outputDirectory;
	private String _printFileName;
	private PrintStream _printStream;
	
	private ParsingContext _context;
	private OutputFormat _outputFormat;
	
	public OutputFileSelector(DeltaDataSet dataSet) {
		_dataSet = dataSet;
	}

	public void setParsingContext(ParsingContext context) {
		_context = context;
	}
	public String getItemOutputFile(int itemNumber) {
		if (_itemOutputFiles.isEmpty() && _characterForOutputFiles == 0) {
			throw new RuntimeException("One of ITEM OUTPUT FILES or CHARACTER FOR OUTPUT FILES must be specified.");
		}
		
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
		
		return addExtension(outputFile);
	}
	
	private String addExtension(String outputFile) {
		
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
		_outputFormat = outputFormat;	}

	public String getKeyOutputFilePath() {
		return makeAbsolute(_keyOutputFile);
	}

	public void setDistOutputFile(String outputFile) {
		_distOutputFile = outputFile;
	}
	
	public String getDistOutputFilePath() {
		return makeAbsolute(_distOutputFile);
	}

	public void addNewFileAtItem(Integer id) {
		_newFileItems.add(id);
	}
	
	public void setOutputDirectory(String directory) throws Exception {
		_outputDirectory = directory;
		if (_printFileName != null) {
			recreatePrintFile();
		}
	}
	
	public void setPrintFile(String filename) throws Exception {
		_printFileName = FilenameUtils.separatorsToSystem(filename);
		
		recreatePrintFile();
	}
	
	private void recreatePrintFile() throws Exception {
		
		closeExistingPrintStream();
		
		File parent = _context.getFile().getParentFile();
		File outputDir = null;
		if (_outputDirectory != null) {
			outputDir = new File(FilenameUtils.separatorsToSystem(_outputDirectory));
			
			if (!outputDir.isAbsolute()) {
				
				outputDir = new File(FilenameUtils.concat(parent.getAbsolutePath(), _outputDirectory));
			}
		}
		else {
			outputDir = parent;
		}
		
		File file = new File(outputDir, _printFileName);	
		FileUtils.forceMkdir(file.getParentFile());
		
		_printStream = new PrintStream(file, OUTPUT_FILE_ENCODING);
	}
	
	private void closeExistingPrintStream() {
		if (_printStream != null && _printStream != System.out && _printStream != System.err) {
			IOUtils.closeQuietly(_printStream);
		}
	}
	
	public void setPrintStream(PrintStream stream) {
		_printStream = stream;
	}
	
	public PrintStream getPrintStream() {
		return _printStream;
	}
}
