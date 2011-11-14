package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.PrintActionType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the PRINT ITEM NAMES directive.
 */
public class PrintItemDescriptions extends AbstractNoArgDirective {

	public PrintItemDescriptions() {
		super("print", "item", "descriptions");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.addPrintAction(PrintActionType.PRINT_ITEM_DESCRIPTIONS);
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}
