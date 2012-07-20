package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Implements the PRINT ALL CHARACTERS directive.
 * @link http://delta-intkey.com/www/uguide.htm#_*PRINT_ALL_CHARACTERS
 */
public class PrintAllCharacters extends AbstractNoArgDirective {

    public PrintAllCharacters() {
        super("print", "all", "characters");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        context.printAllCharacters();
    }

    @Override
    public int getOrder() {
        return 4;
    }
}
