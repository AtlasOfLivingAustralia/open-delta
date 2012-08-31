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
package au.org.ala.delta.translation.parameter;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.translation.DataSetTranslator;

import java.util.HashMap;
import java.util.Map;

/**
 * The ParameterBasedTranslator performs translations triggered by the OUTPUT PARAMETER directive.
 * The Nexus, Paup and Payne translations work in this manner.
 */
public abstract class ParameterBasedTranslator implements DataSetTranslator {
	
	private Map<String, ParameterTranslator> _supportedParameters;
	protected int _matchLength;
	
	public ParameterBasedTranslator() {
		_supportedParameters = new HashMap<String, ParameterTranslator>();
		 _matchLength = 2;
	}
	
	@Override
	public void translateCharacters() {}

	@Override
	public void translateItems() {}

	@Override
	public void translateOutputParameter(OutputParameter parameter) {

		String name = extractMatchString(parameter.parameter);
		ParameterTranslator translator = _supportedParameters.get(name);
		if (translator == null) {
			unrecognisedParameter(parameter.fullText);
		}
		else  {
			translator.translateParameter(parameter);
		}
		
	}
	
	public void addSupportedParameter(String parameterName, ParameterTranslator translator) {
		if (!parameterName.startsWith("#")) {
			throw new IllegalArgumentException("Parameters must begin with #");
		}
		String name = extractMatchString(parameterName);
		
		_supportedParameters.put(name, translator);
	}
	
	protected String extractMatchString(String parameterName) {
		String name = parameterName;
		if (parameterName.startsWith("#")) {
			if (parameterName.length() < _matchLength+1) {
				throw new IllegalArgumentException("Parameters must be at least 2 characters long");
			}
			name = parameterName.substring(1, _matchLength+1).toUpperCase();	
		}
		return name;
	}
	
	protected abstract void unrecognisedParameter(String parameter);
	
	
	protected String truncate(String value, int maxLength) {
		if (value.length() < maxLength) {
			return value;
		}
		else {
			value = value.substring(0, maxLength);
			return value.trim();
		}
	}
	
	protected String pad(String value, int columns) {
		StringBuilder paddedValue = new StringBuilder(value);
		while (paddedValue.length() % columns != 0) {
			paddedValue.append(' ');
		}
		return paddedValue.toString();
	}

	
}
