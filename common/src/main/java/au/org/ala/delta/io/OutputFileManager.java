package au.org.ala.delta.io;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext.OutputFormat;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.util.Utils;

/**
 * Manages the files and file paths that are output by DELTA programs.
 */
public class OutputFileManager {

	class OutputFile {
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
	
	public static final String OUTPUT_FILE_ENCODING = "utf-8";
	public static final String RTF_OUTPUT_FILE_ENCODING = "cp1252";
	private static final int LISTING_FILE = 0;
	private static final int ERROR_FILE = 1;
	private static final int OUTPUT_FILE = 2;
	
	protected OutputFormat _outputFormat;
	private String _outputDirectory;
	protected String _outputFileName;
	private PrintFile _outputFile;
	protected ParsingContext _context;
	/** Number of characters on a line of text written to the output file */
	protected int _outputWidth;
	
	private int _ListFilenameSize = 15;

	private OutputFile[] _outputFiles;
	
	public OutputFileManager() {
		super();
		_outputFiles = new OutputFile[3];
		_outputFiles[ERROR_FILE] = new OutputFile(System.err);
	}
	
	public void enableListing() {
		if (_outputFiles[LISTING_FILE] == null) {
			_outputFiles[LISTING_FILE] = new OutputFile(System.out);
		}
	}
	
	public void disableListing() {
		if (_outputFiles[LISTING_FILE] != null) {
			_outputFiles[LISTING_FILE].close();
			_outputFiles[LISTING_FILE] = null;
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
		close(_outputFiles[ERROR_FILE]);
		_outputFiles[ERROR_FILE] = new OutputFile(errorFile);
	}
	
	public void setListingFileName(String listingFile) throws Exception {
		close(_outputFiles[LISTING_FILE]);
		_outputFiles[LISTING_FILE] = new OutputFile(listingFile);
	}
	
	private void close(OutputFile file) {
		if (file != null) {
			file.close();
		}
	}

	public void setOutputFileName(String outputFile) throws Exception {
		_outputFiles[OUTPUT_FILE] = new OutputFile(outputFile);
		_outputFile = new PrintFile(_outputFiles[OUTPUT_FILE].getPrintStream(), _outputWidth);
	}

	public PrintFile getOutputFile() {
		return _outputFile;
	}

	public void setOutputWidth(int value) {
		_outputWidth = value;
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

		if (_outputFiles[LISTING_FILE] != null) {
			String prefix = "";
			if (_outputFiles[LISTING_FILE].getPrintStream() == System.out) {
				prefix = "LIST:";
			}

			String filename = Utils.truncate(String.format("%s,%d", _context.getFile().getAbsolutePath(), _context.getCurrentLine()), _ListFilenameSize);
			
			_outputFiles[LISTING_FILE].outputMessage("%s%s %s", prefix, filename, line);
		}
	}

	public void ErrorMessage(String format, Object... args) {
		_outputFiles[ERROR_FILE].outputMessage(format, args);
	}

}