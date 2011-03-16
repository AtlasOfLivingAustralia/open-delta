package au.org.ala.delta.editor.ui.validator;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;



/**
 * The result of a validation. 
 */
public class ValidationResult {
	
	private static final ResourceMap RESOURCES = Application.getInstance().getContext().getResourceMap();
	
	private int _invalidCharacterPosition;
	private String _errorMessageKey;
	
	/**
	 * Creates a new ValidationResult representing a failed validation.
	 * The messageKey is used to lookup a message in the errors resource bundle.
	 * The invalidCharactorPosition provides extra information to clients about the first
	 * invalid character in the string.
	 * @param messageKey the key to the error message in the error resource bundle
	 * @param invalidCharacterPosition the position (assuming a text string) at which the value became invalid.
	 */
	public ValidationResult(String messageKey, int invalidCharacterPosition) {
	
		_errorMessageKey = "errors."+messageKey;
		_invalidCharacterPosition = invalidCharacterPosition;
	}

	/**
	 * Creates a new ValidationResult representing a successful validation.
	 */
	public ValidationResult() {
		_errorMessageKey = null;
		_invalidCharacterPosition = -1;
	}
	
	/**
	 * @return true if there are no messages associated with this result.
	 */
	public boolean isValid() {
		return (_errorMessageKey == null);
	}

	public String getMessage() {
		if (_invalidCharacterPosition != -1) {
			return RESOURCES.getString(_errorMessageKey, _invalidCharacterPosition);
		}
		else {
			return RESOURCES.getString(_errorMessageKey);
		}
	}
	
	public int getInvalidCharacterPosition() {
		return _invalidCharacterPosition;
	}

	
}
