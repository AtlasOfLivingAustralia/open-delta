package au.org.ala.delta.translation;

public class Words {

	public enum Word {
		OR, TO, AND, VARIABLE, UNKNOWN, NOT_APPLICABLE, VARIANT, NOT_CODED, NEVER, MINIMUM, 
		MAXIMUM, UP_TO, OR_MORE, FULL_STOP, COMMA, ALTERNATE_COMMA, SEMICOLON, FULL_STOP_AGAIN, RANGE};
	
	
	private static String[] _vwords = {
			"or", "to", "and", "variable", "unknown", "not applicable", "(variant)", "not coded",
			"never", "minimum", "maximum", "up to", "or more", ".", ",", ",", ";", ".", "-"};
	
	
	public static String word(Word word) {
		return _vwords[word.ordinal()];
	}
	
	public static void setWord(Word word, String value) {
		_vwords[word.ordinal()] = value;
	}
	
}
