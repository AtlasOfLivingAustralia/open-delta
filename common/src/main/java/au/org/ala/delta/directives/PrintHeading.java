package au.org.ala.delta.directives;

import org.apache.commons.lang.StringUtils;

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
		au.org.ala.delta.translation.PrintFile printFile = context.getOutputFileSelector().getPrintFile();
		
		if (StringUtils.isNotBlank(heading)) {
			printFile.writeBlankLines(2, 0);
			printFile.outputLine(heading);
			printFile.writeBlankLines(2, 0);
		}
	}

}
