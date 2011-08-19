package au.org.ala.delta.intkey.directives;

import java.io.File;

import au.org.ala.delta.intkey.directives.invocation.FileInputDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileInputDirective extends IntkeyDirective {

    public FileInputDirective() {
        super("file", "input");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        //TODO need to prompt for file when no data supplied
        
        File selectedFile = new File(data);
        if (!selectedFile.isAbsolute()) {
            selectedFile = new File(context.getDatasetDirectory(), data);
        }
        
        return new FileInputDirectiveInvocation(selectedFile);
    }

}
