package au.org.ala.delta.key.directives.io;

import java.io.PrintStream;

import au.org.ala.delta.io.OutputFileSelector;
import au.org.ala.delta.io.OutputFileManager.OutputFileType;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.translation.PrintFile;

public class KeyOutputFileManager extends OutputFileSelector {

    private static final int DEFAULT_OUTPUT_FILE_WIDTH = 80;

    private String _typesettingFileName;
    private PrintFile _typesettingFile;

    public KeyOutputFileManager(MutableDeltaDataSet dataSet) {
        super(dataSet);
    }

    public void setTypesettingFileName(String fileName) throws Exception {
        _typesettingFileName = fileName;
        PrintStream out = createPrintStream(_typesettingFileName);
        _typesettingFile = new PrintFile(out, DEFAULT_OUTPUT_FILE_WIDTH);
    }

    public PrintFile getTypesettingFile() {
        return _typesettingFile;
    }

    @Override
    public void message(String line) {
        // Output message to the KEY listing file (the print file is used for
        // this purpose)
        getPrintFile().outputLine(line);

        // Output message to stdout
        System.out.println(line);
    }

}
