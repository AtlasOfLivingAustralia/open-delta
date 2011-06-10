package au.org.ala.delta.directives;

import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

public abstract class AbstractIntegerDirective extends AbstractDirective<DeltaContext> {

	private int _value = -1;

	protected AbstractIntegerDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {

		DirectiveArguments args = new DirectiveArguments();
		args.addDirectiveArgument(_value);
		return args;
	}

	
	
	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		_value = Integer.parseInt(data);
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		processInteger(context, _value);
		
	}

	protected abstract void processInteger(DeltaContext context, int character) throws Exception;

}
