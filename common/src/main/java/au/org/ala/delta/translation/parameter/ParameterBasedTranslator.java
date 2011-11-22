package au.org.ala.delta.translation.parameter;

import java.util.HashMap;
import java.util.Map;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.translation.DataSetTranslator;

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
}
