package au.org.ala.delta.directives;

import java.util.HashSet;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;

/**
 * The link characters directive specifies that a set of characters that should be placed in the same 
 * sentence in natural language descriptions.
 */
public class LinkCharacters extends AbstractCharacterSetDirective<DeltaContext> {
	
	public LinkCharacters() {
		super("link", "characters");
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARGROUPS;
	}
	
	@Override
	protected void processCharacterSet(DeltaContext context, List<Integer> characters) {
		context.linkCharacters(new HashSet<Integer>(characters));
	}	
	
	@Override
	public int getOrder() {
		return 4;
	}
}
