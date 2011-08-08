package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the PERCENTAGE ERROR directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*PERCENTAGE_ERROR_
 */
public class PercentageError extends AbstractCharacterListDirective<DeltaContext, Double> {

	public PercentageError() {
		super("percentage", "error");
	}
	
	@Override
	protected void addArgument(DirectiveArguments args, int charIndex, String value) {
		args.addNumericArgument(charIndex, value);
	}

	@Override
	protected Double interpretRHS(DeltaContext context, String rhs) {
		return Double.parseDouble(rhs);
	}

	@Override
	protected void processCharacter(DeltaContext context, int charIndex, Double error) {
		context.setPercentageError(charIndex, error);
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARREALLIST;
	}

}
