package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;


/**
 * Processes the OMIT LOWER FOR CHARACTERS directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*OMIT_LOWER_FOR
 */
public class OmitLowerForCharacters extends AbstractRangeListDirective<DeltaContext> {

	
	public OmitLowerForCharacters() {
		super("omit", "lower", "for", "characters");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}
	
	@Override
	protected void processNumber(DeltaContext context, int number) {
		context.setOmitLowerForCharacter(number, true);
	}
}
