package au.org.ala.delta.key.directives;

import java.io.File;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractTextDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.key.KeyContext;
import au.org.ala.delta.util.Utils;

public class KeyOutputDirectoryDirective extends AbstractTextDirective {

    public KeyOutputDirectoryDirective() {
        super("output", "directory");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        KeyContext keyContext = (KeyContext) context;

        String directoryName = directiveArguments.getFirstArgumentText().trim();
        File datasetDirectory = keyContext.getDataDirectory();
        File directory = Utils.createFileFromPath(directoryName, datasetDirectory);

        if (!directory.exists()) {
            boolean directoryCreated = directory.mkdir();
            if (!directoryCreated) {
                throw DirectiveError.asException(DirectiveError.Error.DIRECTORY_DOES_NOT_EXIST_CANNOT_CREATE, directoryName.length() + 1);
            }
        } else {
            if (!directory.isDirectory()) {
                throw DirectiveError.asException(DirectiveError.Error.DIRECTORY_DOES_NOT_EXIST_CANNOT_CREATE, directoryName.length() + 1);
            }
        }
        
        keyContext.getOutputFileManager().setTypesettingFileOutputDirectory(directory);
    }

}
