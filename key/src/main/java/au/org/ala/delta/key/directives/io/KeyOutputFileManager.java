package au.org.ala.delta.key.directives.io;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.io.OutputFileSelector;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.util.Utils;

public class KeyOutputFileManager extends OutputFileSelector {

    private static final String DEFAULT_TYPESETTING_FILE_EXTENSION = ".rtf";
    private static final String DEFAULT_OUTPUT_FILE_EXTENSION = ".prt";
    private static final int DEFAULT_OUTPUT_FILE_WIDTH = 80;

    private String _defaultBaseFileName;

    private String _keyListingFileName;
    private PrintFile _keyListingFile;
    private String _typesettingFileName;
    private PrintFile _typesettingFile;

    private File _typesettingFileOutputDirectory;

    public KeyOutputFileManager(MutableDeltaDataSet dataSet) {
        super(dataSet);
        _defaultBaseFileName = "key"; // Set a default here to ensure it is
                                      // never null.
        // default base file name should always be set in the constructor for
        // KeyContext
    }

    public void setDefaultBaseFileName(String defaultBaseFileName) {
        _defaultBaseFileName = defaultBaseFileName;
    }

    @Override
    public PrintFile getOutputFile() {
        PrintFile outputFile = super.getOutputFile();

        if (outputFile == null) {
            try {
                setOutputFileName(_defaultBaseFileName + DEFAULT_OUTPUT_FILE_EXTENSION);
            } catch (Exception ex) {
                throw new RuntimeException(String.format("Error creating default output file %s", _defaultBaseFileName + DEFAULT_OUTPUT_FILE_EXTENSION), ex);
            }
            outputFile = super.getOutputFile();
        }

        return outputFile;
    }

    public void setKeyListingFileName(String fileName) throws Exception {
        _keyListingFileName = fileName;
        PrintStream out = createPrintStream(_keyListingFileName);
        _keyListingFile = new PrintFile(out, getOutputWidth());
    }

    public PrintFile getKeyListingFile() {
        return _keyListingFile;
    }

    public void setTypesettingFileByName(String fileName) throws Exception {
        File outputDir = _typesettingFileOutputDirectory;
        if (outputDir == null) {
            outputDir = createFile(_outputDirectory);
        }

        _typesettingFileName = Utils.createFileFromPath(fileName, outputDir).getAbsolutePath();
        PrintStream out = createPrintStream(_typesettingFileName);
        // The file width has no effect on typeset data. Always use the default
        // file width.
        _typesettingFile = new PrintFile(out, 1000);
    }

    public PrintFile getTypesettingFile() {
        if (_typesettingFile == null) {
            _typesettingFileName = Utils.createFileFromPath(_defaultBaseFileName + DEFAULT_TYPESETTING_FILE_EXTENSION, _typesettingFileOutputDirectory).getAbsolutePath();
            try {
                PrintStream out = createPrintStream(_typesettingFileName);
                _typesettingFile = new PrintFile(out, 1000);
            } catch (Exception ex) {
                throw new RuntimeException(String.format("Error creating default typesetting file %s", _typesettingFileName), ex);
            }
        }
        return _typesettingFile;
    }

    public File getTypesettingFileOutputDirectory() {
        return _typesettingFileOutputDirectory;
    }

    public void setTypesettingFileOutputDirectory(File outputDir) {
        _typesettingFileOutputDirectory = outputDir;
    }

    @Override
    public List<File> getOutputFiles() {
        List<File> files = super.getOutputFiles();
        if (_keyListingFile != null) {
            files.add(createFile(_keyListingFileName));
        }

        if (_typesettingFile != null) {
            files.add(createFile(_typesettingFileName));
        }

        return files;
    }

    @Override
    public void message(String line) {
        if (_keyListingFile != null) {
            _keyListingFile.outputLine(line);
        }
        // Output message to stdout
        System.out.println(line);
    }

}
