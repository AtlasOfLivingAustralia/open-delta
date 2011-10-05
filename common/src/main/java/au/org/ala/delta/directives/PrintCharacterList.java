package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.PrintActionType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the PRINT CHARACTER LIST directive.
 */
public class PrintCharacterList extends AbstractNoArgDirective {

	public PrintCharacterList() {
		super("print", "character", "list");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.addPrintAction(PrintActionType.PRINT_CHARACTER_LIST);
	}
}
