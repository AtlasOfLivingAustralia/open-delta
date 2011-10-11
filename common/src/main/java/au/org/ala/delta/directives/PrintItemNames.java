package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.PrintActionType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the PRINT ITEM NAMES directive.
 */
public class PrintItemNames extends AbstractNoArgDirective {

	public PrintItemNames() {
		super("print", "item", "names");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.addPrintAction(PrintActionType.PRINT_ITEM_NAMES);
	}
}
