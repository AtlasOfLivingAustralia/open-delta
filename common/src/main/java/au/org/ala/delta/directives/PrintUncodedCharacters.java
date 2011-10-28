package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.PrintActionType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the PRINT UNCODED CHARACTERS directive.
 */
public class PrintUncodedCharacters extends AbstractNoArgDirective {

	public PrintUncodedCharacters() {
		super("print", "uncoded", "characters");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.addPrintAction(PrintActionType.PRINT_UNCODED_CHARACTERS);
	}
}
