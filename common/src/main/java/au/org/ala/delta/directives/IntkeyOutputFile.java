package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Handles the INTKEY OUTPUT FILE directive.
 */
public class IntkeyOutputFile extends AbstractTextDirective {

	public IntkeyOutputFile() {
		super("intkey", "output", "file");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.getOutputFileSelector().setIntkeyOutputFile(directiveArguments.getFirstArgumentText());
	}

}
