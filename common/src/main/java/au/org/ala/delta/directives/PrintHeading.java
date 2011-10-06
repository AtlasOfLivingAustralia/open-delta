package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the PRINT HEADING directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*PRINT_HEADING_
 */
public class PrintHeading extends AbstractNoArgDirective {

	public PrintHeading() {
		super("print", "heading");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		String heading = context.getHeading(HeadingType.HEADING);
		String eol = System.getProperty("line.separator");
		context.print(eol+eol+heading+eol+eol);
	}

}
