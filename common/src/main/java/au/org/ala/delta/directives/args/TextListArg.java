package au.org.ala.delta.directives.args;

import java.util.ArrayList;
import java.util.List;

public class TextListArg implements DirectiveArgs {

	private List<String> _text;
	
	public TextListArg() {
		_text = new ArrayList<String>();
	}
	
	public List<String> getText() {
		return _text;
	}
	
	public void add(String value) {
		_text.add(value);
	}
}
