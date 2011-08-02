package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;

public class CharactersForSynonymy extends AbstractRangeListDirective<DeltaContext> {

	
	public CharactersForSynonymy() {
		super("characters", "for", "synonymy");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}
	
	@Override
	protected void processNumber(DeltaContext context, int number) {
		context.addCharacterForSynonymy(number);
	}
}
