package au.org.ala.delta.directives;

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

	public void process(DeltaContext context, String data) throws Exception {
		_value = Integer.parseInt(data);

		processInteger(context, _value);
	}

	protected abstract void processInteger(DeltaContext context, int character) throws Exception;

}
