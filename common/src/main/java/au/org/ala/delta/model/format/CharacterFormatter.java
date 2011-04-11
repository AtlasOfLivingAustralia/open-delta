package au.org.ala.delta.model.format;

import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Knows how to format Characters.
 */
public class CharacterFormatter {

	/**
	 * Formats a character state like <number>. <state text>.
	 * @param character the character
	 * @param stateNumber the number of the state to format.
	 * @return a String describing the state.
	 */
	public String formatState(MultiStateCharacter character, int stateNumber) {
		
		return stateNumber + ". " + character.getState(stateNumber);
	}
	
}
