package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the TREAT VARIABLE AS UNKNOWN directive used in the 
 * TRANSLATE INTO PAUP FORMAT operation.
 */
public class TreatVariableAsUnknown extends AbstractNoArgDirective {

	public TreatVariableAsUnknown() {
		super("treat", "variable", "as", "unknown");
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
