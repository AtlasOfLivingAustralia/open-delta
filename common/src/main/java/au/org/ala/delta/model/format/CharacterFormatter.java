package au.org.ala.delta.model.format;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;

/**
 * Knows how to format Characters.
 */
public class CharacterFormatter extends Formatter {

	private boolean _includeNumber;
	private boolean _useBrackettedNumber;
	
	public CharacterFormatter() {
		this(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, false, false);
	}
	public CharacterFormatter(boolean includeNumber, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting, boolean capitaliseFirstWord) {
		super(commentStrippingMode, angleBracketHandlingMode, stripFormatting, capitaliseFirstWord);
		_includeNumber = includeNumber;
		_useBrackettedNumber = false;
	}
	
	public void setUseBrackettedNumber(boolean useBracketedNumber) {
		_useBrackettedNumber = useBracketedNumber;
	}
	
	public String formatState(MultiStateCharacter character, int stateNumber) {
		
		CommentStrippingMode mode = _commentStrippingMode;
		AngleBracketHandlingMode angleMode = _angleBracketHandlingMode;
		if (_angleBracketHandlingMode == AngleBracketHandlingMode.CONTEXT_SENSITIVE_REPLACE) {
			mode = CommentStrippingMode.STRIP_ALL;
			angleMode = AngleBracketHandlingMode.RETAIN;
		}
		return formatState(character, stateNumber, mode, angleMode);
	}

	
	/**
	 * Formats a character state like <number>. <state text>.
	 * @param character the character
	 * @param stateNumber the number of the state to format.
	 * @return a String describing the state.
	 */
	public String formatState(MultiStateCharacter character, int stateNumber, CommentStrippingMode commentStrippingMode) {
		return formatState(character, stateNumber, commentStrippingMode, _angleBracketHandlingMode);	
	}
	
	/**
	 * Formats a character state like <number>. <state text>.
	 * @param character the character
	 * @param stateNumber the number of the state to format.
	 * @return a String describing the state.
	 */
	public String formatState(MultiStateCharacter character, int stateNumber, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleMode) {
		StringBuilder state = new StringBuilder();
		if (_includeNumber) {
			state.append(stateNumber).append(". ");
		}
		String stateText = character.getState(stateNumber);
		
		state.append(defaultFormat(stateText, commentStrippingMode, _angleBracketHandlingMode, _stripFormatting, false));
		return state.toString();
	}


	public String formatCharacterDescription(Character character) {
		CommentStrippingMode mode = _commentStrippingMode;
		AngleBracketHandlingMode angleMode = _angleBracketHandlingMode;
		if (_angleBracketHandlingMode == AngleBracketHandlingMode.CONTEXT_SENSITIVE_REPLACE) {
			mode = CommentStrippingMode.STRIP_ALL;
			angleMode = AngleBracketHandlingMode.RETAIN;
		}
		return formatCharacterDescription(character, mode, angleMode);
	}
	
	public String formatCharacterDescription(Character character, CommentStrippingMode commentStrippingMode) {

		return formatCharacterDescription(character, commentStrippingMode, _angleBracketHandlingMode);
	}
	
	public String formatCharacterDescription(Character character, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketMode) {

		String description = character.getDescription();
		String formattedDescription = defaultFormat(description, commentStrippingMode, angleBracketMode, _stripFormatting, _capitaliseFirstWord);
		
		if (_includeNumber) {
			formattedDescription = formatCharacterNumber(character.getCharacterId())+formattedDescription;
		}
		return formattedDescription;
	}
	
	protected String formatCharacterNumber(int number) {
		if (_useBrackettedNumber) {
			return "("+number+") ";
		}
		return Integer.toString(number) + ". ";
	}
	/**
	 * Formats the units of a numeric character.
	 * @param character the character to format.
	 * @return the characters units, formatted.
	 */
	public String formatUnits(NumericCharacter<?> character) {
		return formatUnits(character, _commentStrippingMode);
	}
	
	public String formatUnits(NumericCharacter<?> character, CommentStrippingMode commentStrippingMode) {
		String units = character.getUnits();
		return defaultFormat(units, commentStrippingMode, _angleBracketHandlingMode, _stripFormatting, false);
	}
	
	public String formatNotes(Character character) {
		String notes = character.getNotes();
		if (StringUtils.isEmpty(notes)) {
			return "";
		}
		return defaultFormat(notes, false, false);
	}
	
}
