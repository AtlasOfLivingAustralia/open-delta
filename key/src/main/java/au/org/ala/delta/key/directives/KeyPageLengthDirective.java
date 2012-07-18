package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractIntegerDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.validation.IntegerValidator;
import au.org.ala.delta.directives.validation.PositiveIntegerValidator;

public class KeyPageLengthDirective extends AbstractIntegerDirective {
    
    public KeyPageLengthDirective() {
        super("page", "length");
        
    }

    @Override
    protected void processInteger(DeltaContext context, int value) throws Exception {
        if (value < 0) {
            throw new IllegalArgumentException("Page length cannot be negative");
        }
        context.getOutputFileSelector().setOutputPageLength(value);
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTEGER;
    }

    @Override
    protected IntegerValidator createValidator(DeltaContext context) {
        return new PositiveIntegerValidator();
    }

}
