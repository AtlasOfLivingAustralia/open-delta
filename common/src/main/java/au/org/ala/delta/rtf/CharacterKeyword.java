package au.org.ala.delta.rtf;

public class CharacterKeyword extends Keyword {
	
	private char _outputChar;

	public CharacterKeyword(String keyword, char outputChar) {
		super(keyword, KeywordType.Character);
		_outputChar = outputChar;
	}
	
	public char getOutputChar() {
		return _outputChar;
	}

}
