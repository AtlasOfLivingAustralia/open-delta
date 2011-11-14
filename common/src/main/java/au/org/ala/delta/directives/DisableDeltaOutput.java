package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class DisableDeltaOutput extends AbstractNoArgDirective {

	public DisableDeltaOutput() {
		super("disable", "delta", "output");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.disableDeltaOutput();
	}

	@Override
	public int getOrder() {
		return 4;
	}
}
