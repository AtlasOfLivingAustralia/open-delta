package au.org.ala.delta.delfor.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractTextDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the REFORMAT directive.
 */
public class Reformat extends AbstractTextDirective {

	public Reformat() {
		super("reformat");
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		String fileName = directiveArguments.getFirstArgumentText();
		
		
	}

	@Override
	public int getOrder() {
		return 5;
	}
	
}
