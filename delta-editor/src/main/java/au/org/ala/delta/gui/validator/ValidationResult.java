package au.org.ala.delta.gui.validator;

public class ValidationResult {
	
	public ValidationResult(String message, int invalidCharacterPosition) {
		_valid = false;
		// TODO resource bundle!
		_errorMessage = message;
		_invalidCharacterPosition = invalidCharacterPosition;
	}

	public ValidationResult() {
		_valid = true;
	}
	
	public boolean isValid() {
		return (_errorMessage == null);
	}

	public boolean _valid;
	public int _invalidCharacterPosition;
	public String _errorMessage;
	
}
