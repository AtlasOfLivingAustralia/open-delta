package au.org.ala.delta.model.format;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;

/**
 * Knows how to format Characters.
 */
public class CharacterFormatter extends Formatter {

	private boolean _includeNumber;
	
	public CharacterFormatter() {
		this(true, false, AngleBracketHandlingMode.RETAIN, false);
	}
	public CharacterFormatter(boolean includeNumber, boolean stripComments, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting) {
		super(stripComments, angleBracketHandlingMode, stripFormatting);
		_includeNumber = includeNumber;
	}
	
	public String formatState(MultiStateCharacter character, int stateNumber) {
		return formatState(character, stateNumber, _stripComments);
	}

	
	/**
	 * Formats a character state like <number>. <state text>.
	 * @param character the character
	 * @param stateNumber the number of the state to format.
	 * @return a String describing the state.
	 */
	public String formatState(MultiStateCharacter character, int stateNumber, boolean stripComments) {
		StringBuilder state = new StringBuilder();
		if (_includeNumber) {
			state.append(stateNumber).append(". ");
		}
		String stateText = character.getState(stateNumber);
		
		state.append(defaultFormat(stateText, stripComments, _stripFormatting));
		return state.toString();
	}

	public String formatCharacterDescription(Character character) {
		
		return formatCharacterDescription(character, _stripComments);
	}
	
	public String formatCharacterDescription(Character character, boolean stripComments) {

		String description = character.getDescription();
		String formattedDescription = defaultFormat(description, stripComments, _stripFormatting);
		
		if (_includeNumber) {
			formattedDescription = character.getCharacterId()+". "+formattedDescription;
		}
		return formattedDescription;
	}
	
	/**
	 * Formats the units of a numeric character.
	 * @param character the character to format.
	 * @return the characters units, formatted.
	 */
	public String formatUnits(NumericCharacter<?> character) {
		return formatUnits(character, _stripComments);
	}
	
	public String formatUnits(NumericCharacter<?> character, boolean stripComments) {
		String units = character.getUnits();
		return defaultFormat(units, stripComments, _stripFormatting);
	}
	
}
