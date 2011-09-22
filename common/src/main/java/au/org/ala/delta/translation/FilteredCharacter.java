package au.org.ala.delta.translation;

import au.org.ala.delta.model.Character;

public class FilteredCharacter {

	private int _filteredNumber;
	private Character _character;
	
	public FilteredCharacter(int filteredNumber, Character character) {
		_filteredNumber = filteredNumber;
		_character = character;
	}
	
	public int getCharacterNumber() {
		return _filteredNumber;
	}
	
	public Character getCharacter() {
		return _character;
	}
}
