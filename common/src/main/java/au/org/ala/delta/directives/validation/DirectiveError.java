package au.org.ala.delta.directives.validation;

import java.util.ResourceBundle;

public class DirectiveError {

	private static final String ERROR_BUNDLE = "au.org.ala.delta.resources.errors";

	public enum Error {
		DIRECTIVE_OUT_OF_ORDER(2), EQUIVALENT_DIRECTIVE_USED(3),
		MISSING_DATA(10), ILLEGAL_VALUE(12), ILLEGAL_VALUE_NO_ARGS(22), INVALID_REAL_NUMBER(23), 
		ALL_CHARACTERS_EXCLUDED(25), ALL_ITEMS_EXCLUDED(26), ILLEGAL_DELIMETER(31),
		MULTISTATE_CHARACTERS_ONLY(33), DUPLICATE_VALUE(39), INTEGER_EXPECTED(66),
		ITEM_NAME_MISSING_SLASH(98), UNMATCHED_CLOSING_BRACKET(100), 
		DELIMITER_ONE_CHARACTER(128),
		CHARACTERS_MUST_BE_SAME_TYPE(132), KEY_STATES_TEXT_CHARACTER(166), 
		INVALID_CHARACTER_TYPE(167), FATAL_ERROR(168), MISSING_DIMENSIONS(169),
		DUPLICATE_DIMENSION(170), INVALID_OVERLAY_TYPE(171), NOT_HOTSPOT(172);
		
		private int _errorNumber;
		private Error(int number) {
			_errorNumber = number;
		}
		
		public int getErrorNumber() {
			return _errorNumber;
		}
	};
	
	public enum Warning {
		TAXON_NAMES_DUPLICATED_OR_UNMATCHED(103);
		
		private int _errorNumber;
		private Warning(int number) {
			_errorNumber = number;
		}
		
		public int getErrorNumber() {
			return _errorNumber;
		}
	}
	
	private ResourceBundle _resources;
	private String _message;
	private boolean _error;
	private boolean _warning;
	
	public DirectiveError(Error error) {
		_message = lookupMessage(error);
		_error = true;
		_warning = false;
	}
	
	public DirectiveError(Error error, Object... args) {
		_message = lookupMessage("error."+ error.getErrorNumber(), args);	
		_error = true;
		_warning = false;
	}
	
	public DirectiveError(Warning warning, Object... args) {
		_message = lookupMessage(warning, args);
		_error = false;
		_warning = true;
	}
	
	protected String lookupMessage(Warning warning, Object... args) {
		return lookupMessage("warning."+ warning.getErrorNumber(), args);	
	}

	protected String lookupMessage(Error error) {
		return lookupMessage("error."+ error.getErrorNumber());	
	}
	
	protected String lookupMessage(String key, Object... args) {
		_resources = ResourceBundle.getBundle(ERROR_BUNDLE);
		return String.format(_resources.getString(key), args);
	}
	
	
	
	public String getMessage() {
		return _message;
	}

	public boolean isFatal() {
		return false;
	}
	
	public boolean isError() {
		return _error;
	}
	
	public boolean isWarning() {
		return _warning;
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
