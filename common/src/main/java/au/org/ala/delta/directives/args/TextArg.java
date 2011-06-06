package au.org.ala.delta.directives.args;

public class TextArg implements DirectiveArgs {

	private String _text;
	
	public TextArg(String text) {
		_text = text;
	}
	
	public String getText() {
		return _text;
	}
}
