package au.org.ala.delta.intkey.directives.invocation;

import java.io.File;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileInputDirectiveInvocation implements IntkeyDirectiveInvocation {

    private File _inputFile;
    
    public FileInputDirectiveInvocation(File inputFile) {
        _inputFile = inputFile;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.processInputFile(_inputFile);
        return true;
    }
    
    
}
