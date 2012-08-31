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
		_error.setFatal(true);
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

	public DirectiveError getError() {
		return _error;
	}
	
}
