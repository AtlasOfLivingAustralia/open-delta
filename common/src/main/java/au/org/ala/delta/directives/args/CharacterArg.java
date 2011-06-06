package au.org.ala.delta.directives.args;


public class CharacterArg implements DirectiveArgs {

	private int _characterNum;
	
	public CharacterArg(int characterNum) {
		_characterNum = characterNum;
	}
	
	public int getCharacterNum() {
		return _characterNum;
	}
}
