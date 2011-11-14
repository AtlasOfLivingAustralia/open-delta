package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;

public class NonautomaticControllingCharacters extends AbstractRangeListDirective<DeltaContext> {

	
	public NonautomaticControllingCharacters() {
		super("nonautomatic", "controlling", "characters");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}
	
	@Override
	protected void processNumber(DeltaContext context, int number) {
		context.setNonautomaticControllingCharacter(number, true);
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}
