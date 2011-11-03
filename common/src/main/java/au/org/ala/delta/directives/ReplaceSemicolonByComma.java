package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;

/**
 * Implements the REPLACE SEMICOLON BY COMMA directive.
 *
 */
public class ReplaceSemicolonByComma extends AbstractRangeListDirective<DeltaContext> {

	
	public ReplaceSemicolonByComma() {
		super("replace", "semicolon", "by", "comma");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}
	
	@Override
	protected void processNumber(DeltaContext context, int characterNum) {
		context.replaceSemiColonWithCommon(characterNum);
	}
}
