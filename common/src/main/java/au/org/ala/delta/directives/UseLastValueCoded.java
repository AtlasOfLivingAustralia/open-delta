package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the USE LAST VALUE CODED directive used in the 
 * TRANSLATE INTO PAUP FORMAT operation.
 */
public class UseLastValueCoded extends AbstractNoArgDirective {

	public UseLastValueCoded() {
		super("use", "last", "value", "coded");
	}
	
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.treatVariableAsUnknown();
	}
	
	@Override
	public int getOrder() {
		return 4;
	}

}
