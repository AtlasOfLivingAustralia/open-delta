package au.org.ala.delta.dist.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractNoArgDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class Log extends AbstractNoArgDirective {

	public Log() {
		super("log");
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		
	}
}
