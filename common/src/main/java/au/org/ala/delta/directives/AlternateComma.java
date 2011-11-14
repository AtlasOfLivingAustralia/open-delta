package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;

/**
 * Implements the ALTERNATE COMMA directive.
 */
public class AlternateComma extends AbstractRangeListDirective<DeltaContext> {

	public AlternateComma() {
		super("alternate", "comma");
	}
	
	@Override
	protected void processNumber(DeltaContext context, int number) {
		context.useAlternateCommaForCharacter(number);
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}

	@Override
	public int getOrder() {
		return 4;
	}
	
}
