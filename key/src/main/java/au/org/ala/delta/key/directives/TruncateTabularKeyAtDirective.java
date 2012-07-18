package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractIntegerDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.validation.IntegerValidator;
import au.org.ala.delta.directives.validation.PositiveIntegerValidator;
import au.org.ala.delta.key.KeyContext;

public class TruncateTabularKeyAtDirective extends AbstractIntegerDirective {
    
    public TruncateTabularKeyAtDirective() {
        super("truncate", "tabular", "key", "at");
        
    }

    @Override
    protected void processInteger(DeltaContext context, int truncateTabularKeyAtColumnNumber) throws Exception {
        KeyContext keyContext = (KeyContext) context;
        keyContext.setTruncateTabularKeyAtColumnNumber(truncateTabularKeyAtColumnNumber);
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
