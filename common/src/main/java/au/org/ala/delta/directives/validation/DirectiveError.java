/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.directives.validation;

import java.util.ResourceBundle;

public class DirectiveError {

	private static final String ERROR_BUNDLE = "au.org.ala.delta.resources.errors";

	public enum Error {
		DIRECTIVE_OUT_OF_ORDER(2), EQUIVALENT_DIRECTIVE_USED(3),
		EXPECTED_CHARACTER_NUMBER(8), STATE_NUMBER_EXPECTED(9),
		MISSING_DATA(10), ILLEGAL_SYMBOL(12), ILLEGAL_SYMBOL_WITH_ARGS(1200), 
		CHARACTER_NUMBER_TOO_HIGH(13), CHARACTER_OUT_OF_ORDER(14),
		NUMBER_OF_STATES_WRONG(16), TOO_MANY_UNITS(18), STATES_NOT_ALLOWED(19),
		ILLEGAL_VALUE_NO_ARGS(22), INVALID_REAL_NUMBER(23), 
		ALL_CHARACTERS_EXCLUDED(25), ALL_ITEMS_EXCLUDED(26), ILLEGAL_DELIMETER(31),
		MULTISTATE_CHARACTERS_ONLY(33), FAILED_PREREQUISITES(36), INVALID_TAXON_NUMBER(37), DUPLICATE_VALUE(39),
		TOO_MANY_ITEMS(46),
		FILE_DOES_NOT_EXIST(55), FILE_INACCESSABLE(57), FILE_CANNOT_BE_OPENED(58),
		CHARACTER_ALREADY_SPECIFIED(64), VALUE_OUT_OF_ORDER(65), INTEGER_EXPECTED(66),
		CLOSING_BRACKET_MISSING(91),
		ITEM_NAME_MISSING_SLASH(98), UNMATCHED_CLOSING_BRACKET(100), 
		MISSING_OUTPUT_FILE(117),
		DELIMITER_ONE_CHARACTER(128),
		CHARACTERS_MUST_BE_SAME_TYPE(132), WRONG_CHARACTER_COUNT(135), STATE_NUMBER_GREATER_THAN_MAX(138),
		DIRECTORY_DOES_NOT_EXIST_CANNOT_CREATE(163),
		KEY_STATES_TEXT_CHARACTER(166), 
		INVALID_CHARACTER_TYPE(167), FATAL_ERROR(168), MISSING_DIMENSIONS(169),
		DUPLICATE_DIMENSION(170), INVALID_OVERLAY_TYPE(171), NOT_HOTSPOT(172),
		EXCLUSIVE_ERROR(173), UNMATCHED_RTF_BRACKETS(174), INVALID_RTF(175), UNSUPPORTED_TRANSLATION(176);
		
		private int _errorNumber;
		private Error(int number) {
			_errorNumber = number;
		}
		
		public int getErrorNumber() {
			return _errorNumber;
		}
	};
	
	public enum Warning {
		TAXON_NAMES_DUPLICATED_OR_UNMATCHED(103), FIRST_ITEM_CANNOT_BE_VARIANT(158);
		
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
	private int _errorNumber;
	private long _position;
	private boolean _fatal = false;
	
	public DirectiveError(Error error, long position) {
		_message = lookupMessage(error);
		_error = true;
		_warning = false;
		_errorNumber = error.getErrorNumber();
		_position = position;
	}
	
	public DirectiveError(Error error, long position, Object... args) {
		_message = lookupMessage("error."+ error.getErrorNumber(), args);	
		_error = true;
		_warning = false;
		_errorNumber = error.getErrorNumber();
		_position = position;
	}
	
	public DirectiveError(Warning warning, long position, Object... args) {
		_message = lookupMessage(warning, args);
		_error = false;
		_warning = true;
		_errorNumber = warning.getErrorNumber();
		_position = position;
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
	
	public long getPosition() {
		return _position;
	}
	
	public void setPosition(long pos) {
		_position = pos;
	}
	
	public String getMessage() {
		return _message;
	}

	public boolean isFatal() {
		return _fatal;
	}
	
	public void setFatal(boolean fatal) {
		_fatal = fatal;
	}
	
	public boolean isError() {
		return _error;
	}
	
	public boolean isWarning() {
		return _warning;
	}
	
	public static DirectiveException asException(Error error, int offset, Object... args) {
		DirectiveError directiveError = new DirectiveError(error, offset, args);
		return new DirectiveException(directiveError, offset);
	}
	
	public static DirectiveException asException(Error error, int offset) {
		DirectiveError directiveError = new DirectiveError(error, offset);
		return new DirectiveException(directiveError, offset);
	}
	
	public DirectiveException asException() {
		return new DirectiveException(this, this.getPosition());
	}

	public int getErrorNumber() {
		return _errorNumber;
	}
	
}
