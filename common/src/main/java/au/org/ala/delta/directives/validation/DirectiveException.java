package au.org.ala.delta.directives.validation;

import java.text.ParseException;

public class DirectiveException extends ParseException {

	private static final long serialVersionUID = -1730496606394420737L;

	private DirectiveError _error;
	
	public DirectiveException(String s, long errorOffset) {
		super(s, (int)errorOffset);
	}
	
	public DirectiveException(DirectiveError error, long errorOffset) {
		super(error.getMessage(), (int)errorOffset);
		_error = error;
	}
	
	public boolean isFatal() {
		return true;
	}
	
	public boolean isError() {
		return true;
	}
	
	public int getErrorNumber() {
		return _error.getErrorNumber();
	}

	
}
