package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

public abstract class AbstractTextListDirective extends AbstractDirective<DeltaContext> {
	
	protected DirectiveArguments _args;
	
	protected AbstractTextListDirective(String... controlWords) {
		super(controlWords);
	}
	
	@Override
	public DirectiveArguments getDirectiveArgs() {

		return _args;
	}
}
