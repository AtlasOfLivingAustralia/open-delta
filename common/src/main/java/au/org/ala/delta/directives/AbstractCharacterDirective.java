package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

public abstract class AbstractCharacterDirective extends AbstractDirective<DeltaContext> {

	private int _characterNum = -1;

	protected AbstractCharacterDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		DirectiveArguments args = new DirectiveArguments();
		args.addDirectiveArgument(_characterNum);
		return args;
	}

	public void process(DeltaContext context, String data) throws Exception {
		_characterNum = Integer.parseInt(data);

		processCharacter(context, _characterNum);
	}

	public abstract void processCharacter(DeltaContext context, int character) throws Exception;

}
