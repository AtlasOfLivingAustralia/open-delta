package au.org.ala.delta.intkey.directives.invocation;

import java.io.File;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileCloseDirectiveInvocation implements IntkeyDirectiveInvocation {

    private File _file;
    
    public FileCloseDirectiveInvocation(File file) {
        _file = file;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        // File should already have been previous checked to ensure that it is
        // an open output file.
        context.closeOutputFile(_file);
        return true;
    }

}
