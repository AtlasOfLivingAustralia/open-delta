package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the LIST ITEMS directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*LIST_ITEMS_
 */
public class ListCharacters extends AbstractNoArgDirective {

	public ListCharacters() {
		super("list", "items");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.enableItemListing();
		context.getOutputFileSelector().enableListing();
	}

}
