package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.validation.IntegerValidator;
import au.org.ala.delta.directives.validation.ItemNumberValidator;

/**
 * Implements the STOP AT ITEM directive.  Terminates translations after a supplied number of Items have been processed.
 */
public class StopAfterItem extends AbstractIntegerDirective {

    public StopAfterItem() {
        super("stop", "after", "item");
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_ITEM;
    }

    protected void processInteger(DeltaContext context, int itemNumber) throws Exception {
        context.stopAfterItem(itemNumber);
    }

    protected IntegerValidator createValidator(DeltaContext context) {
        return new ItemNumberValidator(context);
    }

    public int getOrder() {
        return 4;
    }

}
