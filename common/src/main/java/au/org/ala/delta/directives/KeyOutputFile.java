package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Handles the KEY OUTPUT FILE directive.
 */
public class KeyOutputFile extends AbstractTextDirective {

	public KeyOutputFile() {
		super("key", "output", "file");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.getOutputFileSelector().setKeyOutputFile(directiveArguments.getFirstArgumentText().trim());
	}

}
