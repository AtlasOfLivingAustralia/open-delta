package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;

public abstract class AbstractInternalDirective extends AbstractDirective<DeltaContext> {
	protected DirectiveArguments _args;
	
	public AbstractInternalDirective(String... controlWords) {
		super(controlWords);
		_args = new DirectiveArguments();
	}
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_INTERNAL;
	}
}
