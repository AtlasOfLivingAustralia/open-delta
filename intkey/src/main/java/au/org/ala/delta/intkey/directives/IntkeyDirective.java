package au.org.ala.delta.intkey.directives;

import java.text.ParseException;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public abstract class IntkeyDirective extends AbstractDirective<IntkeyContext> {
    
    public IntkeyDirective(String... controlWords) {
        super(controlWords);
    }
    
    protected DirectiveArguments _args;
    
    @Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public void parse(IntkeyContext context, String data) throws ParseException {
		
		_args = DirectiveArguments.textArgument(data);
	}

	@Override
	public void process(IntkeyContext context, DirectiveArguments directiveArguments) throws Exception {
		parseAndProcess(context, directiveArguments.getFirstArgumentText());
	}

	@Override
    public final void parseAndProcess(IntkeyContext context, String data) throws Exception {
        IntkeyDirectiveInvocation invoc = doProcess(context, data);
        
        if (invoc != null) {
            context.executeDirective(invoc);
        }
    }
    
    protected abstract IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception;

}
