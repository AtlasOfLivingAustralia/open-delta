package au.org.ala.delta.translation.attribute;

import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
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
	
	public MultiStateAttributeTranslator(
			MultiStateCharacter character, 
			CharacterFormatter characterFormatter, 
			AttributeFormatter formatter,
			boolean omitOr) {
		super(formatter, omitOr);
		_character = character;
		_formatter = characterFormatter;
	}

	@Override
	public String translateValue(String value) {
		
		String state = "";
		try{ 
		int stateNum = Integer.parseInt(value);
		
		state = _formatter.formatState(_character, stateNum);
		}
		catch (NumberFormatException e) {
			System.err.println("Error translating character: "+_character.getCharacterId());
			e.printStackTrace();
			throw e;
		}
		return state;
	}

	@Override
	public String rangeSeparator() {
		return " to ";
	}
	

}
