package au.org.ala.delta.directives;

import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.KeyStateParser;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
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
		DeltaDataSet dataSet = context.getDataSet();
		IdentificationKeyCharacter keyCharacter = null;
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			int tmpId = (Integer)arg.getId();
			
			if (tmpId != id) {
				keyCharacter = create(context, tmpId);
				context.addIdentificationKeyCharacter(keyCharacter);
			}
			
			CharacterType charType = dataSet.getCharacter(tmpId).getCharacterType();
			if (charType.isNumeric()) {
				keyCharacter.addState(arg.getValueAsInt(), arg.getData().get(0), arg.getData().get(1));
			}
			else if (charType == CharacterType.UnorderedMultiState){
				keyCharacter.addState(arg.getValueAsInt(), arg.getDataList());
			}
			else if (charType == CharacterType.OrderedMultiState) {
				List<Integer> states = arg.getDataList();
				if (states.size() > 1) {
					List<Integer> range = new ArrayList<Integer>();
					for (int i=states.get(0); i<=states.get(1); i++) {
						range.add(i);
					}
					states = range;
				}
				keyCharacter.addState(arg.getValueAsInt(), states);
			}
			
			id = tmpId;
		}
		
	}
	
	private IdentificationKeyCharacter create(DeltaContext context, int characterNumber) {
		DeltaDataSet dataSet = context.getDataSet();
		Character character = dataSet.getCharacter(characterNumber);
		IdentificationKeyCharacter keyChar = new IdentificationKeyCharacter(character, context.getUseNormalValues(characterNumber));
		return keyChar;
	}
}
