package au.org.ala.delta.directives;

import java.text.ParseException;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the KEY STATES directive.
 */
public class KeyStates extends AbstractDirective<DeltaContext> {

	
	private DirectiveArguments _args;
	
	public KeyStates() {
		super("key", "states");
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_KEYSTATE;
	}

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		throw new NotImplementedException();
		
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		throw new NotImplementedException();
		
	}
	
	
	
}
