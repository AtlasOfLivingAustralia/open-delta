package au.org.ala.delta.directives;

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * This class parses the ITEM SUBHEADINGS directive.
 */
public class ItemSubHeadings extends AbstractTextDirective {

	public ItemSubHeadings() {
		super("item", "subheadings");
	}

	@Override
	public void process(DeltaContext context, String data) throws Exception {
		super.process(context, data);
		StringReader reader = new StringReader(data);
		IntegerTextListParser parser = new IntegerTextListParser(context, reader);
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
			context.itemSubheading((Integer)arg.getId(), arg.getText().trim());
		}
	}
}
