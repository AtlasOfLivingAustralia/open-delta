package au.org.ala.delta.directives;

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IntegerTextListParser;

/**
 * This class parses the ITEM SUBHEADINGS directive.
 */
public class ItemSubHeadings extends AbstractCustomDirective {

	public ItemSubHeadings() {
		super("item", "subheadings");
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
			context.itemSubheading((Integer)arg.getId(), arg.getText().trim());
		}
	}
}
