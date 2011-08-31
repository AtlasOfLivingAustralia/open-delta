package au.org.ala.delta.directives;

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IntegerTextListParser;

/**
 * This class parses the CHARACTER HEADINGS directive.
 */
public class CharacterHeadings extends AbstractCustomDirective {

	public CharacterHeadings() {
		super("character", "headings");
	}
	
	@Override
	public int getArgType() {
		
		return DirectiveArgType.DIRARG_CHARTEXTLIST;
	}
	
	@Override
	protected DirectiveArgsParser createParser(DeltaContext context, StringReader reader) {
		return new IntegerTextListParser(context, reader);
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
			context.addCharacterHeading((Integer)arg.getId(), arg.getText().trim());
		}
	}
}
