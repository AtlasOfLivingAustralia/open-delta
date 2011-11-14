package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;

public class UseControllingCharactersFirst extends AbstractRangeListDirective<DeltaContext> {

	
	public UseControllingCharactersFirst() {
		super("use", "controlling", "characters", "first");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}
	
	@Override
	protected void processNumber(DeltaContext context, int number) {
		context.setUseControllingCharacterFirst(number, true);
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}
