package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;

/**
 * Processes the OUTPUT FORMAT HTML directive.
 */
public class OutputFormatHtml extends AbstractDirective<DeltaContext>{

	/**
	 * Updates the context to indicate this directive has been processed.
	 */
	@Override
	public void process(DeltaContext context, String data) throws Exception {
		
		context.setOutputHtml(true);
	}

	
}
