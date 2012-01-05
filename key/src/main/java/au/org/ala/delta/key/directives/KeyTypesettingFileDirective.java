package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractTextDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.key.KeyContext;

public class KeyTypesettingFileDirective extends AbstractTextDirective {

    public KeyTypesettingFileDirective() {
        super("key", "typesetting", "file");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        KeyContext keyContext = (KeyContext) context;

        String fileName = directiveArguments.getFirstArgumentText().trim();
        // Use the print file to output the typeset bracketed key.
        keyContext.getOutputFileManager().setPrintFileName(fileName);
        keyContext.setTypesettingFileSet(true);
    }

}
