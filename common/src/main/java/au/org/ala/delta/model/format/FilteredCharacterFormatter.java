package au.org.ala.delta.model.format;

import au.org.ala.delta.model.IdentificationKeyCharacter;

public class FilteredCharacterFormatter extends CharacterFormatter {

	public String formatCharacterNumber(IdentificationKeyCharacter character) {
		StringBuilder number = new StringBuilder();
		number.append(character.getFilteredCharacterNumber());
		number.append("(").append(character.getCharacterNumber()).append(")");
		return number.toString();
	}
	
}
