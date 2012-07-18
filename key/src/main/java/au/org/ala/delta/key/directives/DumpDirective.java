package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractIntegerDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.validation.IntegerValidator;

public class DumpDirective extends AbstractIntegerDirective {

    public DumpDirective() {
        super("dump");
    }

    @Override
    protected void processInteger(DeltaContext context, int character) throws Exception {
        // do nothing, this is a no-op directive, implemented only for backwards
        // compatibility
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTEGER;
    }

    @Override
    protected IntegerValidator createValidator(DeltaContext context) {
        return null;
    }

}
