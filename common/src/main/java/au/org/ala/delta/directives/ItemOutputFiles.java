package au.org.ala.delta.directives;

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.StringTextListParser;

/**
 * This class processes the ITEM OUTPUT FILES directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*ITEM_OUTPUT_FILES
 */
public class ItemOutputFiles extends AbstractCustomDirective {

	public ItemOutputFiles() {
		super("item", "output", "files");
	}
	
	@Override
	public int getArgType() {
		
		return DirectiveArgType.DIRARG_ITEMTEXTLIST;
	}
	
	@Override
	protected DirectiveArgsParser createParser(DeltaContext context, StringReader reader) {
		return new StringTextListParser(context, reader);
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
			// The delimiter is stored with id = Short.MIN_VALUE
			if (arg.getId() instanceof String) {
				context.getOutputFileSelector().setItemOutputFile((String)arg.getId(), arg.getText().trim());
			}
		}
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}
