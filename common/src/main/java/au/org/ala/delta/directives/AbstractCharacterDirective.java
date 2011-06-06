package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.CharacterArg;
import au.org.ala.delta.directives.args.DirectiveArgs;

public abstract class AbstractCharacterDirective extends AbstractDirective<DeltaContext> {

	private int _characterNum = -1;

	protected AbstractCharacterDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public DirectiveArgs getDirectiveArgs() {

		return new CharacterArg(_characterNum);
	}

	public void process(DeltaContext context, String data) throws Exception {
		_characterNum = Integer.parseInt(data);

		processCharacter(context, _characterNum);
	}

	public abstract void processCharacter(DeltaContext context, int character) throws Exception;

}
