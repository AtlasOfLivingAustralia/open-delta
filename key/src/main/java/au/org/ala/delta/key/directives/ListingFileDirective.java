package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractTextDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.key.KeyContext;

public class ListingFileDirective extends AbstractTextDirective {

    public ListingFileDirective() {
        super("listing", "file");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        KeyContext keyContext = (KeyContext) context;

        String fileName = directiveArguments.getFirstArgumentText().trim();
        keyContext.getOutputFileManager().setKeyListingFileName(fileName);
    }

}
