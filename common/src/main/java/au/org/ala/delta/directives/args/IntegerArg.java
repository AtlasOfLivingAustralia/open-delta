package au.org.ala.delta.directives.args;


public class IntegerArg implements DirectiveArgs {

	private int _value;
	
	public IntegerArg(int value) {
		_value = value;
	}
	
	public int getValue() {
		return _value;
	}
}
