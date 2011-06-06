package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgs;
import au.org.ala.delta.directives.args.IntegerArg;

public abstract class AbstractIntegerDirective extends AbstractDirective<DeltaContext> {

	private int _value = -1;

	protected AbstractIntegerDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public DirectiveArgs getDirectiveArgs() {

		return new IntegerArg(_value);
	}

	public void process(DeltaContext context, String data) throws Exception {
		_value = Integer.parseInt(data);

		processInteger(context, _value);
	}

	protected abstract void processInteger(DeltaContext context, int character) throws Exception;

}
