package au.org.ala.delta.directives;

import java.util.HashSet;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;

/**
 * Implements the REPLACE SEMICOLON BY COMMA directive.
 *
 */
public class ReplaceSemicolonByComma extends AbstractCharacterSetDirective<DeltaContext> {

	
	public ReplaceSemicolonByComma() {
		super("replace", "semicolon", "by", "comma");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARGROUPS;
	}
	
	@Override
	protected void processCharacterSet(DeltaContext context, List<Integer> characters) {
		context.replaceSemiColonWithCommon(new HashSet<Integer>(characters));
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}
