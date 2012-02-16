package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractIntegerDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;

public class KeyPrintWidthDirective extends AbstractIntegerDirective {
    
    public KeyPrintWidthDirective() {
        super("print", "width");
        
    }

    @Override
    protected void processInteger(DeltaContext context, int value) throws Exception {
        // KEY only uses output files, the "print file" is not used. Set the width for all output files.
        context.getOutputFileSelector().setOutputWidth(value);
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTEGER;
    }

}
