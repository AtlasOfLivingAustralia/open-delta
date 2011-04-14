package au.org.ala.delta.translation.attribute;

import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;

/**
 * The MultiStateAttributeTranslator is responsible for translating MultiStateCharacter attributes into 
 * natural language.
 */
public class MultiStateAttributeTranslator extends AttributeTranslator {

	/** The character associated with the attribute to translate */
	private MultiStateCharacter _character;
	
	/** Knows how to format character states */
	private CharacterFormatter _formatter;
	
	public MultiStateAttributeTranslator(MultiStateCharacter character) {
		_character = character;
		_formatter = new CharacterFormatter(false, true, true);
	}

	@Override
	public String translateValue(String value) {
		int stateNum = Integer.parseInt(value);
		
		String state = _formatter.formatState(_character, stateNum);
		
		return state;
	}

	@Override
	public String rangeSeparator() {
		return " to ";
	}
	

}
