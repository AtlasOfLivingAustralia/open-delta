package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the OUTPUT FORMAT HTML directive.
 */
public class OutputFormatHtml extends AbstractNoArgDirective {

	public OutputFormatHtml() {
		super("output", "format", "html");
	}
	/**
	 * Updates the context to indicate this directive has been processed.
	 */
	@Override
	public void process(DeltaContext context, DirectiveArguments data) throws Exception {
		
		context.setOutputHtml(true);
	}

	
}
