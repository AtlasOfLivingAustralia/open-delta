package au.org.ala.delta.editor.slotfile.model;

import au.org.ala.delta.editor.slotfile.CharType;
import au.org.ala.delta.model.CharacterType;

public class CharacterTypeConverter {
	
	/**
	 * Converts a slotfile CharType int into a model class CharacterType enum.
	 * @param charType the slotfile character type.
	 * @return the appropriate matching CharacterType for the supplied char type.
	 */
	public static CharacterType fromCharType(int charType) {
		switch (charType) {
		case CharType.TEXT:
			return CharacterType.Text;
		case CharType.INTEGER:
			return CharacterType.IntegerNumeric;
		case CharType.REAL:
			return CharacterType.RealNumeric;
		case CharType.ORDERED:
			return CharacterType.OrderedMultiState;
		case CharType.UNORDERED:
			return CharacterType.UnorderedMultiState;
		default:
			throw new RuntimeException("Unrecognised character type: " + charType);
		}
		
		
	}
	
	
	/**
	 * Converts a model CharacterType into a slotfile int CharType.
	 * @param characterType the model character type.
	 * @return the appropriate matching int for the supplied character type.
	 */
	public static int toCharType(CharacterType characterType) {
		switch (characterType) {
		case Text:
			return CharType.TEXT;
		case IntegerNumeric:
			return CharType.INTEGER;
		case RealNumeric:
			return CharType.REAL;
		case OrderedMultiState:
			return CharType.ORDERED;
		case UnorderedMultiState:
			return CharType.UNORDERED;
		default:
			throw new RuntimeException("Unrecognised character type: " + characterType);
		}
	}
}
