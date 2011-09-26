package au.org.ala.delta.intkey.directives.invocation;

import java.io.File;
import java.io.IOException;

import au.org.ala.delta.Logger;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileOutputDirectiveInvocation extends IntkeyDirectiveInvocation {

    private File _file;

    public void setFile(File file) {
        this._file = file;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        try {
            context.newOutputFile(_file);
            return true;
        } catch (IOException ex) {
            Logger.error(ex);
            context.getUI().displayErrorMessage(String.format("Error opening output file %s", _file.getAbsolutePath()));
            return false;
        }
    }
}
