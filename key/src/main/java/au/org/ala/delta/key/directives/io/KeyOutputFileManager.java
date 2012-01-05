package au.org.ala.delta.key.directives.io;

import java.io.PrintStream;

import au.org.ala.delta.io.OutputFileSelector;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.translation.PrintFile;

public class KeyOutputFileManager extends OutputFileSelector {

    private static final int DEFAULT_OUTPUT_FILE_WIDTH = 80;

    private String _keyListingFileName;
    private PrintFile _keyListingFile;

    public KeyOutputFileManager(MutableDeltaDataSet dataSet) {
        super(dataSet);
    }

    public void setKeyListingFileName(String fileName) throws Exception {
        _keyListingFileName = fileName;
        PrintStream out = createPrintStream(_keyListingFileName);
        _keyListingFile = new PrintFile(out, DEFAULT_OUTPUT_FILE_WIDTH);
    }

    public PrintFile getKeyListingFile() {
        return _keyListingFile;
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
