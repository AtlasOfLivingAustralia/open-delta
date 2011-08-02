package au.org.ala.delta.directives;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.KeyStateParser;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IdentificationKeyCharacter;

/**
 * Processes the KEY STATES directive.
 */
public class KeyStates extends AbstractDirective<DeltaContext> {

	
	private DirectiveArguments _args;
	
	public KeyStates() {
		super("key", "states");
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_KEYSTATE;
	}

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		
		KeyStateParser parser = new KeyStateParser(context, new StringReader(data));
		parser.parse();
		
		_args = parser.getDirectiveArgs();
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		int id = -1;
		IdentificationKeyCharacter keyCharacter = null;
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			int tmpId = (Integer)arg.getId();
			
			if (tmpId != id) {
				keyCharacter = create(context, tmpId);
				context.addIdentificationKeyCharacter(keyCharacter);
			}
			if (context.getCharacter(id).getCharacterType().isNumeric()) {
				keyCharacter.addState(arg.getValueAsInt(), argToFloatRange(arg));
			}
			else {
				keyCharacter.addState(arg.getValueAsInt(), arg.getDataList());
			}
			
			id = tmpId;
		}
		
	}
	
	private IdentificationKeyCharacter create(DeltaContext context, int id) {
		Character character = context.getDataSet().getCharacter(id);
		IdentificationKeyCharacter keyChar = new IdentificationKeyCharacter(character);
		return keyChar;
	}
	
	private FloatRange argToFloatRange(DirectiveArgument<?> arg) {
		BigDecimal first = arg.getData().get(0);
		BigDecimal last = arg.getData().get(1);
		return new FloatRange(first.floatValue(), last.floatValue());
	}
	
	
	
}
