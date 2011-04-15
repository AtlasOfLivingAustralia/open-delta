package au.org.ala.delta.model.format;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.rtf.RTFUtils;

/**
 * Knows how to format Characters.
 */
public class CharacterFormatter extends Formatter {

	private boolean _includeNumber;
	private boolean _stripComments;
	private boolean _stripFormatting;
	
	public CharacterFormatter() {
		this(true, false, false);
	}
	public CharacterFormatter(boolean includeNumber, boolean stripComments, boolean stripFormatting) {
		
		_includeNumber = includeNumber;
		_stripComments = stripComments;
		_stripFormatting = stripFormatting;
		
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
		if (_stripFormatting) {
			stateText = RTFUtils.stripFormatting(stateText);
		}
		if (_stripComments) {
			stateText = stripComments(stateText);
		}
		state.append(stateText);
		return state.toString();
	}

	public String formatCharacterDescription(Character character) {
		
		String description = character.getDescription();
		if (StringUtils.isEmpty(description)) {
			return "";
		}
		
		if (_stripComments) {
			description = RTFUtils.stripFormatting(description);
		}
		if (_stripComments) {
			description = stripComments(description);
		}
		return description;
	}
	
}
