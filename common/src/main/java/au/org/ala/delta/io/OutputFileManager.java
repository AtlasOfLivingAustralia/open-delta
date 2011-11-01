package au.org.ala.delta.io;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext.OutputFormat;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.util.Utils;

/**
 * Manages the files and file paths that are output by DELTA programs.
 */
public class OutputFileManager {

	public static final String OUTPUT_FILE_ENCODING = "utf-8";
	public static final String RTF_OUTPUT_FILE_ENCODING = "cp1252";
	
	protected OutputFormat _outputFormat;
	private String _outputDirectory;
	protected String _outputFileName;
	private PrintFile _outputFile;
	protected ParsingContext _context;
	/** Number of characters on a line of text written to the output file */
	protected int _outputWidth;
	
	private PrintStream _listingStream;
	private PrintStream _errorStream;
	private int _ListFilenameSize = 15;

	public OutputFileManager() {
		super();
		_listingStream = System.out;
		_errorStream = System.err;
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

	public void setOutputFileName(String outputFile) throws Exception {
		_outputFileName = FilenameUtils.separatorsToSystem(outputFile);
		PrintStream indexStream = createPrintStream(_outputFileName);
		_outputFile = new PrintFile(indexStream, _outputWidth);
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

	public void setErrorStream(PrintStream stream) {
		_errorStream = stream;
	}

	public void setListingStream(PrintStream stream) {
		_listingStream = stream;
	}
	
	public void listMessage(String line) {

		if (_listingStream != null) {
			String prefix = "";
			if (_listingStream == System.out) {
				prefix = "LIST:";
			}

			String filename = Utils.truncate(String.format("%s,%d", _context.getFile().getAbsolutePath(), _context.getCurrentLine()), _ListFilenameSize);
			OutputMessage(_listingStream, "%s%s %s", prefix, filename, line);
		}
	}

	public void ErrorMessage(String format, Object... args) {
		OutputMessage(_errorStream, format, args);
	}
	
	private void OutputMessage(PrintStream stream, String format, Object... args) {
		if (stream != null) {
			if (args == null || args.length == 0) {
				stream.println(format);
			} else {
				String message = String.format(format, args);
				stream.println(message);
			}
		}
	}

}