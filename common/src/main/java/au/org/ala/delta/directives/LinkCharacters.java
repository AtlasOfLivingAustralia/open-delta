package au.org.ala.delta.directives;

import java.util.Set;

import au.org.ala.delta.DeltaContext;

/**
 * The link characters directive specifies that a set of characters that should be placed in the same 
 * sentence in natural language descriptions.
 */
public class LinkCharacters extends AbstractCharacterSetDirective<DeltaContext> {
	
	public LinkCharacters() {
		super("link", "characters");
	}

	@Override
	protected void processCharacterSet(DeltaContext context, Set<Integer> characters) {
		context.linkCharacters(characters);
	}	
}
