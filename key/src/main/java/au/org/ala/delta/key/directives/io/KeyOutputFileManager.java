package au.org.ala.delta.key.directives.io;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import au.org.ala.delta.io.OutputFileSelector;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.translation.PrintFile;

public class KeyOutputFileManager extends OutputFileSelector {

    private static final int DEFAULT_OUTPUT_FILE_WIDTH = 80;

    private String _keyListingFileName;
    private PrintFile _keyListingFile;
    private String _typesettingFileName;
    private PrintFile _typesettingFile;

    public KeyOutputFileManager(MutableDeltaDataSet dataSet) {
        super(dataSet);
    }

    public void setKeyListingFileName(String fileName) throws Exception {
        _keyListingFileName = fileName;
        PrintStream out = createPrintStream(_keyListingFileName);
        _keyListingFile = new PrintFile(out, getPrintWidth());
    }

    public PrintFile getKeyListingFile() {
        return _keyListingFile;
    }

    public void setTypesettingFileName(String fileName) throws Exception {
        _typesettingFileName = fileName;
        PrintStream out = createPrintStream(_typesettingFileName);
        // The file width has no effect on typeset data. Always use the default
        // file width.
        _typesettingFile = new PrintFile(out, DEFAULT_OUTPUT_FILE_WIDTH);
    }

    public PrintFile getTypesettingFile() {
        return _typesettingFile;
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
