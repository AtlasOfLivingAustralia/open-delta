package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class ChineseFormat extends AbstractNoArgDirective {

	public ChineseFormat() {
		super("chinese", "format");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.disableDeltaOutput();
	}
	
	@Override
	public int getOrder() {
		return 4;
	}

}
