package au.org.ala.delta.intkey.directives.invocation;

import java.io.File;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class NewDatasetDirectiveInvocation extends IntkeyDirectiveInvocation {

    private File _file;

    public void setFile(File file) {
        this._file = file;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.newDataSetFile(_file);
        return true;
    }

}
