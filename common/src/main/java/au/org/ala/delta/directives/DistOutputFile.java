package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Handles the DIST OUTPUT FILE directive.
 */
public class DistOutputFile extends AbstractTextDirective {

	public DistOutputFile() {
		super("dist", "output", "file");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.getOutputFileSelector().setDistOutputFile(directiveArguments.getFirstArgumentText().trim());
	}

}
