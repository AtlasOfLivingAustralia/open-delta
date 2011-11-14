package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the LIST CHARARACTERS directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*LIST_CHARACTERS_
 */
public class ListItems extends AbstractNoArgDirective {

	public ListItems() {
		super("list", "characters");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.enableCharacterListing();
		context.getOutputFileSelector().enableListing();
	}

	@Override
	public int getOrder() {
		return 4;
	}
}
