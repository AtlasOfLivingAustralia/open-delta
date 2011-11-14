package au.org.ala.delta.directives;

import java.io.StringReader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdListParser;

public class UseNormalValues extends AbstractDirective<DeltaContext> {

	private DirectiveArguments _args;
	
	public UseNormalValues() {
		super("use", "normal", "values");
	}
	
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
			context.setUseNormalValues((Integer)arg.getId(), true);
		}
	}

	@Override
	public int getOrder() {
		return 4;
	}
	
}
