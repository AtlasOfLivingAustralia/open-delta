package au.org.ala.delta.directives.validation;

import java.util.ResourceBundle;

public class DirectiveError {

	private static final String ERROR_BUNDLE = "au.org.ala.delta.resources.errors";

	public enum Error {
		DUPLICATE_VALUE(39), INTEGER_EXPECTED(66);
		
		private int _errorNumber;
		private Error(int number) {
			_errorNumber = number;
		}
		
		public int getErrorNumber() {
			return _errorNumber;
		}
	};
	
	private ResourceBundle _resources;
	private String _message;
	
	public DirectiveError(Error error) {
		_message = lookupMessage(error);
	}

	protected String lookupMessage(Error error) {
		_resources = ResourceBundle.getBundle(ERROR_BUNDLE);
		String key = "error."+ error.getErrorNumber();
		return _resources.getString(key);
	}
	
	public DirectiveError(Error error, Object... args) {
		_message = String.format(lookupMessage(error), args);
	}
	
	public String getMessage() {
		return _message;
	}

	public boolean isFatal() {
		return false;
	}
	
	public static DirectiveException asException(Error error, long offset, Object... args) {
		DirectiveError directiveError = new DirectiveError(error, args);
		return new DirectiveException(directiveError, offset);
	}
	
	public static DirectiveException asException(Error error, long offset) {
		DirectiveError directiveError = new DirectiveError(error);
		return new DirectiveException(directiveError, offset);
	}
	
}
