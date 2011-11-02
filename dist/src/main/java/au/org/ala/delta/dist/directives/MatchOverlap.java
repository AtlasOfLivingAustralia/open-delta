package au.org.ala.delta.dist.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractNoArgDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.dist.DistContext;

public class MatchOverlap extends AbstractNoArgDirective {

	public MatchOverlap() {
		super("match", "overlap");
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		((DistContext)context).matchOverlap();
	}
}
