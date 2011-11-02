package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the DATA LISTING directive.
 */
public class DataListing extends AbstractNoArgDirective {

	
	public DataListing() {
		super("data", "listing");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments data) throws Exception {
		context.getOutputFileSelector().enableListing();
	}

}
