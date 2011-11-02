package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.io.OutputFileSelector;

/**
 * Processes the LIST HEADING directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*PRINT_HEADING_
 */
public class ListHeading extends AbstractNoArgDirective {

	public ListHeading() {
		super("list", "heading");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		String heading = context.getHeading(HeadingType.HEADING);
		
		OutputFileSelector outputFileManager = context.getOutputFileSelector();
		outputFileManager.listMessage("");
		outputFileManager.listMessage("");		
		outputFileManager.listMessage(heading);
		outputFileManager.listMessage("");
		outputFileManager.listMessage("");		
	}

}
