package au.org.ala.delta.directives;

import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

public abstract class AbstractTextListDirective<T> extends AbstractDirective<DeltaContext> {
	
	protected DirectiveArguments _args;
	
	protected AbstractTextListDirective(String... controlWords) {
		super(controlWords);
	}
	

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		_args = new DirectiveArguments();
		
	}
	
	
	@Override
	public DirectiveArguments getDirectiveArgs() {

		return _args;
	}
}
