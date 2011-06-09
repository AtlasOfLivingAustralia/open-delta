package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;

public abstract class AbstractNoArgDirective extends AbstractDirective<DeltaContext>  {
	
	protected AbstractNoArgDirective(String... controlWords) {
		super(controlWords);
	}
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return null;
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_NONE;
	}
}
