package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgs;
import au.org.ala.delta.directives.args.TextListArg;

public abstract class AbstractTextListDirective extends AbstractDirective<DeltaContext> {
	
	protected TextListArg _args;
	
	protected AbstractTextListDirective(String... controlWords) {
		super(controlWords);
	}
	
	@Override
	public DirectiveArgs getDirectiveArgs() {

		return _args;
	}
}
