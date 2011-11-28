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
package au.org.ala.delta.editor.ui.validator;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;



/**
 * The result of a validation. 
 */
public class ValidationResult {
	
	public static enum ResultType {SUCCESS, ERROR, WARNING};
	
	private static final int NO_POSITION = -1;
	private static final ResourceMap RESOURCES = Application.getInstance().getContext().getResourceMap();
	
	private int _invalidCharacterPosition;
	private String _errorMessageKey;
	private ResultType _type;
	private Object[] _messageArgs;
	
	/**
	 * Creates a new ValidationResult representing a failed validation.
	 * The messageKey is used to lookup a message in the errors resource bundle.
	 * The invalidCharactorPosition provides extra information to clients about the first
	 * invalid character in the string.
	 * @param messageKey the key to the error message in the error resource bundle
	 * @param invalidCharacterPosition the position (assuming a text string) at which the value became invalid.
	 */
	public ValidationResult(String messageKey, int invalidCharacterPosition) {
		this(ResultType.ERROR, messageKey, invalidCharacterPosition);
	}
	
	public ValidationResult(ResultType type, String messageKey, int position) {
		_type = type;
		_errorMessageKey = "errors."+messageKey;
		_invalidCharacterPosition = position;
	}

	/**
	 * Creates a new ValidationResult representing a successful validation.
	 */
	public ValidationResult() {
		_type = ResultType.SUCCESS;
		_errorMessageKey = null;
		_invalidCharacterPosition = -1;
	}
	
	public void setMessageArgs(Object... args) {
		if (_invalidCharacterPosition >= 0) {
			_messageArgs = new Object[args.length+1];
			_messageArgs[0] = _invalidCharacterPosition;
			System.arraycopy(args, 0, _messageArgs, 1, args.length);
		}
		else {
			_messageArgs = args;
		}
	}
	
	/**
	 * @return true if there are no messages associated with this result.
	 */
	public boolean isValid() {
		return (_type.equals(ResultType.SUCCESS));
	}
	
	public boolean isError() {
		return (_type.equals(ResultType.ERROR));
	}
	
	public boolean isWarning() {
		return (_type.equals(ResultType.WARNING));
	}

	public String getMessageKey() {
		return _errorMessageKey;
	}
	
	public String getMessage() {
		if (_messageArgs != null) {
			return RESOURCES.getString(_errorMessageKey, _messageArgs);
		}
		else if (_invalidCharacterPosition != -1) {
			return RESOURCES.getString(_errorMessageKey, _invalidCharacterPosition);
		}
		else {
			return RESOURCES.getString(_errorMessageKey);
		}
	}
	
	public int getInvalidCharacterPosition() {
		return _invalidCharacterPosition;
	}

	public static ValidationResult warning(String messageKey, int position) {
		return new ValidationResult(ResultType.WARNING, messageKey, position);
	}
	
	public static ValidationResult warning(String messageKey) {
		return new ValidationResult(ResultType.WARNING, messageKey, NO_POSITION);
	}
	
	public static ValidationResult error(String messageKey, int position) {
		return new ValidationResult(ResultType.ERROR, messageKey, position);
	}
	
	public static ValidationResult error(String messageKey) {
		return new ValidationResult(ResultType.ERROR, messageKey, NO_POSITION);
	}
	
	public static ValidationResult success() {
		return new ValidationResult();
	}
}
