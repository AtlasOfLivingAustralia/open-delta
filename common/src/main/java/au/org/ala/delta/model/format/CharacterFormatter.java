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
		this(true, false, false);
	}
	public CharacterFormatter(boolean includeNumber, boolean stripComments, boolean stripFormatting) {
		super(stripComments, stripFormatting);
		_includeNumber = includeNumber;
	}
	
	/**
	 * Formats a character state like <number>. <state text>.
	 * @param character the character
	 * @param stateNumber the number of the state to format.
	 * @return a String describing the state.
	 */
	public String formatState(MultiStateCharacter character, int stateNumber) {
		StringBuilder state = new StringBuilder();
		if (_includeNumber) {
			state.append(stateNumber).append(". ");
		}
		String stateText = character.getState(stateNumber);
		
		state.append(defaultFormat(stateText));
		return state.toString();
	}

	public String formatCharacterDescription(Character character) {
		
		String description = character.getDescription();
		return defaultFormat(description);
	}
	
	/**
	 * Formats the units of a numeric character.
	 * @param character the character to format.
	 * @return the characters units, formatted.
	 */
	public String formatUnits(NumericCharacter<?> character) {
		String units = character.getUnits();
		return defaultFormat(units);
	}
	
}
