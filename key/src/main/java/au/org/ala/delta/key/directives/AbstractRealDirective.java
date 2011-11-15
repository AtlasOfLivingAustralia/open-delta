package au.org.ala.delta.key.directives;

import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveError.Error;
import au.org.ala.delta.key.KeyContext;

public abstract class AbstractRealDirective extends AbstractDirective<KeyContext> {

    private double _value = 0;

    protected AbstractRealDirective(String... controlWords) {
        super(controlWords);
    }

    @Override
    public DirectiveArguments getDirectiveArgs() {

        DirectiveArguments args = new DirectiveArguments();
        args.addDirectiveArgument(_value);
        return args;
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_REAL;
    }

    @Override
    public void parse(KeyContext context, String data) throws ParseException {
        try {
            _value = Double.parseDouble(data.trim());
        } catch (Exception ex) {
            throw DirectiveError.asException(Error.INVALID_REAL_NUMBER, context.getCurrentParsingContext().getCurrentOffset());
        }
    }
    
    @Override
    public void process(KeyContext context, DirectiveArguments directiveArguments) throws Exception {
        processReal(context, _value);
        
    }

    protected abstract void processReal(KeyContext context, double value) throws Exception;

}
