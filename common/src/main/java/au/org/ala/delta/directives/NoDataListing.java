package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the NO DATA LISTING directive.
 */
public class NoDataListing extends AbstractNoArgDirective {

	
	public NoDataListing() {
		super("no", "data", "listing");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments data) throws Exception {
		context.getOutputFileSelector().disableListing();
	}

}
