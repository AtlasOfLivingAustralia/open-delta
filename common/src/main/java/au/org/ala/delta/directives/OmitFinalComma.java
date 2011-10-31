package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;

/**
 * Processes the OMIT FINAL COMMA directive.
 */
public class OmitFinalComma extends AbstractRangeListDirective<DeltaContext> {

	
	public OmitFinalComma() {
		super("omit", "final", "comma");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}
	
	@Override
	protected void processNumber(DeltaContext context, int number) {
		context.omitFinalCommaForCharacter(number);
	}
}
