package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Implements the ALLOW DUPLICATE VALUES directive.
 * @link http://delta-intkey.com/www/uguide.htm#_*ACCEPT_DUPLICATE_VALUES
 */
public class AcceptDuplicateValues extends AbstractNoArgDirective {

    public AcceptDuplicateValues() {
        super("accept", "duplicate", "values");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments data) throws Exception {
        context.acceptDuplicateValues();
    }

    @Override
    public int getOrder() {
        return 4;
    }

}
