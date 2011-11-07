package au.org.ala.delta.directives.validation;

public class DirectiveError {

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
	
	
	public DirectiveError(Error error) {
		
	}
	
	public DirectiveError(Error error, Object... args) {
		
	}
	
	public String getMessage() {
		return "test";
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
