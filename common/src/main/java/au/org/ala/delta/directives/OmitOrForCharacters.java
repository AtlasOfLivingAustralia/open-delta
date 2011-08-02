package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;

public class OmitOrForCharacters extends AbstractRangeListDirective<DeltaContext> {

	
	public OmitOrForCharacters() {
		super("omit", "or", "for", "characters");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}
	
	@Override
	protected void processNumber(DeltaContext context, int number) {
		context.omitOrForCharacter(number);
	}
}
