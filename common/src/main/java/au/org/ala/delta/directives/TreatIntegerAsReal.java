package au.org.ala.delta.directives;

import java.io.StringReader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdListParser;

/**
 * Processes the TREAT INTEGER AS REAL directive.
 */
public class TreatIntegerAsReal extends AbstractDirective<DeltaContext> {

	public TreatIntegerAsReal() {
		super("treat", "integer", "as", "real");
	}
	
	private DirectiveArguments _args;
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		IdListParser parser = new IdListParser(context, new StringReader(data));
		parser.parse();
		_args = parser.getDirectiveArgs();
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			context.setTreatIntegerCharacterAsReal((Integer)arg.getId(), true);
		}
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}
