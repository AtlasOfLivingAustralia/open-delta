package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractIntegerDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;

public class KeyPageLengthDirective extends AbstractIntegerDirective {
    
    public KeyPageLengthDirective() {
        super("page", "length");
        
    }

    @Override
    protected void processInteger(DeltaContext context, int value) throws Exception {
        context.getOutputFileSelector().setOutputPageLength(value);
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTEGER;
    }

}
