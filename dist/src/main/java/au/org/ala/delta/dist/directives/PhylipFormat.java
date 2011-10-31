package au.org.ala.delta.dist.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractNoArgDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class PhylipFormat extends AbstractNoArgDirective {

	public PhylipFormat() {
		super("phylip", "format");
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		
	}
}
