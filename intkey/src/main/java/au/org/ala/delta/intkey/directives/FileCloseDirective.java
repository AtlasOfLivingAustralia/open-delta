package au.org.ala.delta.intkey.directives;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.directives.invocation.FileCloseDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileCloseDirective extends IntkeyDirective {

    public FileCloseDirective() {
        super("file", "close");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {

        File fileToClose = null;

        if (StringUtils.isEmpty(data)) {
            if (context.getOutputFiles().isEmpty()) {
                context.getUI().displayErrorMessage("There are no output files to be closed.");
                return null;
            }
            
            fileToClose = context.getDirectivePopulator().promptForOutputFile();
            if (fileToClose == null) {
                // User hit cancel when prompted to select output file to close
                return null;
            }
            
        } else {
            // If the supplied file path starts with one of the file system
            // roots, then it is absolute. Otherwise, assume that
            // it is relative to the directory in which the dataset is located.
            boolean fileAbsolute = false;
            for (File root : File.listRoots()) {
                if (data.toLowerCase().startsWith(root.getAbsolutePath().toLowerCase())) {
                    fileAbsolute = true;
                    break;
                }
            }

            if (fileAbsolute) {
                fileToClose = new File(data);
            } else {
                fileToClose = new File(context.getDatasetDirectory(), data);
            }
            
            if (!context.getOutputFiles().contains(fileToClose)) {
                context.getUI().displayErrorMessage("This file has not been opened as an OUTPUT file.");
                return null;
            }
        }

        return new FileCloseDirectiveInvocation(fileToClose);
    }

}
