package au.org.ala.delta.delfor.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.directives.AbstractTextDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the OUTPUT FILE directive.
 */
public class OutputFile extends AbstractTextDirective {

	public OutputFile() {
		super("output", "file");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		((DelforContext)context).setNextOutputFile(directiveArguments.getFirstArgumentText().trim());
	}

}
