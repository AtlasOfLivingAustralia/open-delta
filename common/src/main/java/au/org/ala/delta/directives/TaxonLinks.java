package au.org.ala.delta.directives;

import java.io.StringReader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.StringTextListParser;

/**
 * Processes the TAXON LINKS directive.
 */
public class TaxonLinks extends AbstractDirective<DeltaContext> {

	private DirectiveArguments _args;
	
	public TaxonLinks() {
		super("taxon", "links");
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_ITEMFILELIST;
	}

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		StringTextListParser parser = new StringTextListParser(context, new StringReader(data));
		parser.parse();
		
		_args = parser.getDirectiveArgs();
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			context.addTaxonLinks((String)arg.getId(), arg.getText());
		}
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}
