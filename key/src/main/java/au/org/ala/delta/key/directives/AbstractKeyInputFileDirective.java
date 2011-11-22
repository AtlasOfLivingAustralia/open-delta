package au.org.ala.delta.key.directives;

import java.io.File;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractTextDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveError.Error;
import au.org.ala.delta.key.KeyContext;
import au.org.ala.delta.util.Utils;

public abstract class AbstractKeyInputFileDirective extends AbstractTextDirective {

    protected AbstractKeyInputFileDirective(String... controlWords) {
        super(controlWords);
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        KeyContext keyContext = (KeyContext) context;

        String fileName = directiveArguments.getFirstArgumentText().trim();
        File datasetDirectory = ((KeyContext) context).getDataDirectory();
        File inputFile = Utils.createFileFromPath(fileName, datasetDirectory);

        if (inputFile.exists()) {
            parseFile(inputFile, keyContext);
        } else {
            throw DirectiveError.asException(DirectiveError.Error.FILE_DOES_NOT_EXIST, fileName.length() + 1);
        }
    }

    abstract void parseFile(File file, KeyContext context) throws Exception;

}
