package au.org.ala.delta.delfor.directives;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.directives.AbstractRangeListDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.validation.DirectiveException;

/**
 * Processes the NEW CHARACTER ORDER directive.
 */
public class NewCharacterOrder extends AbstractRangeListDirective<DelforContext> {

	@Override
	protected void processNumber(DelforContext context, int number) throws DirectiveException {
		

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
