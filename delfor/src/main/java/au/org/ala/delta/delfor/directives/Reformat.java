package au.org.ala.delta.delfor.directives;

import java.io.File;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.delfor.DirectivesFileFormatter;
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
		String fileName = directiveArguments.getFirstArgumentText().trim();
		
		DirectivesFileFormatter formatter = new DirectivesFileFormatter((DelforContext)context);
		
		formatter.reformat(toFile(context, fileName));
	}

	private File toFile(DeltaContext context, String fileName) {
		File directory = context.getCurrentParsingContext().getFile().getParentFile();
		
		return new File(directory, fileName);
	}
	
	@Override
	public int getOrder() {
		return 5;
	}
	
}
