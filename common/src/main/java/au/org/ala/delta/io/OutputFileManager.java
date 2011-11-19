package au.org.ala.delta.io;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext.OutputFormat;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.translation.PrintFile;

/**
 * Manages the files and file paths that are output by DELTA programs.
 */
public class OutputFileManager {

	public enum OutputFileType {LISTING_FILE, ERROR_FILE, OUTPUT_FILE, PRINT_FILE};
	
	protected class OutputFile {
		String _fileName;
		PrintStream _stream;
		
		public OutputFile(String fileName) throws Exception {
			_fileName = FilenameUtils.separatorsToSystem(fileName);
			_stream = createPrintStream(_fileName);
		}
		
		public OutputFile(PrintStream stream) {
			_fileName = null;
			_stream = stream;
		}
		
		public PrintStream getPrintStream() {
			return _stream;
		}
		
		public String getFileName() {
			return _fileName;
		}
		
		public void outputMessage(String format, Object... args) {
			if (_stream != null) {
				if (args == null || args.length == 0) {
					_stream.println(format);
				} else {
					String message = String.format(format, args);
					_stream.println(message);
				}
			}
		}
		
		public void close() {
			if (_stream != System.out && _stream != System.err) {
				IOUtils.closeQuietly(_stream);
			}
		}
	}
	
	public static final String OUTPUT_FILE_ENCODING = "cp1252";
	public static final String RTF_OUTPUT_FILE_ENCODING = "cp1252";
	
	protected OutputFormat _outputFormat;
	private String _outputDirectory;
	private PrintFile _outputFile;
	protected ParsingContext _context;
	/** Number of characters on a line of text written to the output file */
	protected int _outputWidth;
	
	
	protected OutputFile[] _outputFiles;
	
	public OutputFileManager() {
		super();
		_outputFiles = new OutputFile[3];
		_outputFiles[OutputFileType.ERROR_FILE.ordinal()] = new OutputFile(System.err);
	}
	
	public void enableListing() {
		if (outputFile(OutputFileType.LISTING_FILE) == null) {
			_outputFiles[OutputFileType.LISTING_FILE.ordinal()] = new OutputFile(System.out);
		}
	}
	
	public void disableListing() {
		if (outputFile(OutputFileType.LISTING_FILE) != null) {
			outputFile(OutputFileType.LISTING_FILE).close();
			_outputFiles[OutputFileType.LISTING_FILE.ordinal()] = null;
		}
	}

	/**
	 * Tracks the current ParsingContext to allow output files to be
	 * specified relative to the input files.
	 * @param context the current delta parsing context.
	 */
	public void setParsingContext(ParsingContext context) {
		_context = context;
	}

	public String makeAbsolute(String fileName) {
		File file = new File(fileName);
		if (!file.isAbsolute()) {
			File workingDir = _context.getFile().getParentFile();
			file = new File(workingDir, fileName);
		}
		return file.getAbsolutePath();
	}

	public void setOutputDirectory(String directory) throws Exception {
		_outputDirectory = FilenameUtils.separatorsToSystem(directory);
	}

	protected String prependOutputDirectory(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return "";
		}
		String outputFileName = fileName;
		if (!fileName.contains(File.separator) && (_outputDirectory != null)) {
			outputFileName = FilenameUtils.concat(_outputDirectory, fileName);
		}
		return outputFileName;
	}

	public void setOutputFile(PrintFile outputFile) {
		_outputFile = outputFile;
	}

	private String outputFileEncoding() {
		if (_outputFormat == OutputFormat.RTF) {
			return RTF_OUTPUT_FILE_ENCODING;
		}
		else {
			return OUTPUT_FILE_ENCODING;
		}
	}
	
	public void setErrorFileName(String errorFile) throws Exception {
		close(_outputFiles[OutputFileType.ERROR_FILE.ordinal()]);
		_outputFiles[OutputFileType.ERROR_FILE.ordinal()] = new OutputFile(errorFile);
	}
	
	public void setListingFileName(String listingFile) throws Exception {
		close(outputFile(OutputFileType.LISTING_FILE));
		_outputFiles[OutputFileType.LISTING_FILE.ordinal()] = new OutputFile(listingFile);
	}
	
	private void close(OutputFile file) {
		if (file != null) {
			file.close();
		}
	}

	public void setOutputFileName(String outputFile) throws Exception {
		_outputFiles[OutputFileType.OUTPUT_FILE.ordinal()] = new OutputFile(outputFile);
		_outputFile = new PrintFile(_outputFiles[OutputFileType.OUTPUT_FILE.ordinal()].getPrintStream(), _outputWidth);
	}

	public PrintFile getOutputFile() {
		return _outputFile;
	}

	public void setOutputWidth(int value) {
		_outputWidth = value;
	}
	
	public int getOutputWidth() {
		return _outputWidth;
	}
	
	protected File fullPathOf(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		String parentPath = _context.getFile().getParent();
		fileName = FilenameUtils.concat(parentPath, prependOutputDirectory(fileName));
		return new File(fileName);
	}

	protected PrintStream createPrintStream(String fileName) throws Exception {
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
	
	public void listMessage(String line) {

		if (outputFile(OutputFileType.LISTING_FILE) != null) {
			String prefix = "";
			if (outputFile(OutputFileType.LISTING_FILE).getPrintStream() == System.out) {
				prefix = "LIST:";
			}

			outputFile(OutputFileType.LISTING_FILE).outputMessage("%s%s", prefix, line);
		}
	}
	
	public void listMessage(String line, Object... args) {
		listMessage(String.format(line, args));
	}

	public void errorMessage(String format, Object... args) {
		_outputFiles[OutputFileType.ERROR_FILE.ordinal()].outputMessage(format, args);
	}
	
	
	public File getOutputFileAsFile() {
		return fullPathOf(outputFile(OutputFileType.OUTPUT_FILE)._fileName);
	}
	
	public List<String> getOutputFileNames() {
		List<String> names = new ArrayList<String>();
		for (OutputFile file : _outputFiles) {
			if (file != null) {
				names.add(file.getFileName());
			}
		}
		return names;
	}

	
	/**
	 * Writes a line of output to both the list and error files.
	 * @param line the text to output.
	 */
	public void message(String line) {
		listMessage(line);
		errorMessage(line);
	}
	
	public OutputFile outputFile(OutputFileType type) {
		return _outputFiles[type.ordinal()];
	}
}