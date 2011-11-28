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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext.OutputFormat;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.translation.PrintFile;

/**
 * Manages the files and file paths that are output by DELTA programs.
 */
public class OutputFileManager {

	public enum OutputFileType {LISTING_FILE, ERROR_FILE, OUTPUT_FILE};
	
	
	protected class BinaryOutputFile extends OutputFile {
		public BinaryOutputFile(String fileName) {
			super(fileName);
			_binary = true;
		}
	}
	
	protected class TextOutputFile extends OutputFile {
		PrintStream _stream;
		
		public TextOutputFile(String fileName) throws DirectiveException {
			super(fileName);
			_binary = false;
			_stream = createPrintStream(_fileName);
		}

		public TextOutputFile(PrintStream stream) {
			super(null);
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
			if (_stream != _defaultOut && _stream != _defaultErr) {
				IOUtils.closeQuietly(_stream);
			}
		}
	}
	
	protected abstract class OutputFile {
		String _fileName;
		
		boolean _binary;
		
		public OutputFile(String fileName) {
			_fileName = prependOutputDirectory(fileName);
			_binary = false;
		}
		
		public String getFileName() {
			return _fileName;
		}
		
		public boolean isBinary() {
			return _binary;
		}
		
		protected File toFile() {
			if (StringUtils.isEmpty(getFileName())) {
				return null;
			}
			String parentPath = _context.getFile().getParent();
			String fileName = FilenameUtils.concat(parentPath, getFileName());
			return new File(fileName);
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
	protected PrintStream _defaultOut;
	protected PrintStream _defaultErr;
	
	protected TextOutputFile[] _outputFiles;
	
	public OutputFileManager() {
		this(System.out, System.err);
	}
	
	public OutputFileManager(PrintStream out, PrintStream error) {
		super();
		_defaultErr = error;
		_defaultOut = out;
		_outputFiles = new TextOutputFile[3];
		_outputFiles[OutputFileType.ERROR_FILE.ordinal()] = new TextOutputFile(_defaultErr);

	}
	
	public void enableListing() {
		if (outputFile(OutputFileType.LISTING_FILE) == null) {
			_outputFiles[OutputFileType.LISTING_FILE.ordinal()] = new TextOutputFile(_defaultOut);
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
		String outputFileName = FilenameUtils.separatorsToSystem(fileName);
		if (!outputFileName.contains(File.separator) && (_outputDirectory != null)) {
			outputFileName = FilenameUtils.concat(_outputDirectory, fileName);
		}
		return outputFileName;
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
		_outputFiles[OutputFileType.ERROR_FILE.ordinal()] = new TextOutputFile(errorFile);
	}
	
	public void setListingFileName(String listingFile) throws Exception {
		close(outputFile(OutputFileType.LISTING_FILE));
		_outputFiles[OutputFileType.LISTING_FILE.ordinal()] = new TextOutputFile(listingFile);
	}
	
	private void close(TextOutputFile file) {
		if (file != null) {
			file.close();
		}
	}

	public void setOutputFileName(String outputFile) throws Exception {
		_outputFiles[OutputFileType.OUTPUT_FILE.ordinal()] = new TextOutputFile(outputFile);
		_outputFile = new PrintFile(_outputFiles[OutputFileType.OUTPUT_FILE.ordinal()].getPrintStream(), _outputWidth);
	}

	public PrintFile getOutputFile() {
		return _outputFile;
	}
	
	public void setOutputFile(PrintFile outputFile) {
		_outputFile = outputFile;
	}

	public void setOutputWidth(int value) {
		_outputWidth = value;
	}
	
	public int getOutputWidth() {
		return _outputWidth;
	}
	
	

	protected PrintStream createPrintStream(File file) throws DirectiveException {
		PrintStream printStream = null;
		try {
			FileUtils.forceMkdir(file.getParentFile());
			printStream = new PrintStream(file, outputFileEncoding());
		}
		catch (IOException e) {
			throw DirectiveError.asException(DirectiveError.Error.FILE_CANNOT_BE_OPENED, 0, file.getPath());
		}
		return printStream;
	}
	
	protected PrintStream createPrintStream(String fileName) throws DirectiveException {
		File file = createFile(fileName);
		return createPrintStream(file);
	}

	protected File createFile(String fileName) {
		File parent = _context.getFile().getParentFile();
		
		File file = new File(fileName);
		if (!file.isAbsolute()) {
			file = new File(FilenameUtils.concat(parent.getAbsolutePath(), fileName));
		}
		return file;
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
		OutputFile outputFile = outputFile(OutputFileType.OUTPUT_FILE);
		return outputFile != null ? outputFile.toFile() : null;
	}
	
	public List<File> getOutputFiles() {
		List<File> files = new ArrayList<File>();
		for (OutputFile file : _outputFiles) {
			if (file != null) {
				File outFile = file.toFile();
				if (outFile != null) {
					files.add(outFile);
				}
			}
		}
		return files;
	}

	
	/**
	 * Writes a line of output to both the list and error files.
	 * @param line the text to output.
	 */
	public void message(String line) {
		listMessage(line);
		errorMessage(line);
		if (_outputFiles[OutputFileType.ERROR_FILE.ordinal()].getPrintStream() != _defaultErr) {
			_defaultErr.println(line);
		}
	}
	
	public TextOutputFile outputFile(OutputFileType type) {
		return _outputFiles[type.ordinal()];
	}
}
