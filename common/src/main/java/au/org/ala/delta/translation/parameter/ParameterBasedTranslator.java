package au.org.ala.delta.translation.parameter;

import java.util.HashMap;
import java.util.Map;

import au.org.ala.delta.translation.DataSetTranslator;

public abstract class ParameterBasedTranslator implements DataSetTranslator {

	private Map<String, ParameterTranslator> _supportedParameters;
	
	public ParameterBasedTranslator() {
		_supportedParameters = new HashMap<String, ParameterTranslator>();
	}
	
	@Override
	public void translateCharacters() {}

	@Override
	public void translateItems() {}

	@Override
	public void translateOutputParameter(String parameterName) {
		
		String name = extractMatchString(parameterName);
		ParameterTranslator translator = _supportedParameters.get(name);
		if (translator == null) {
			unrecognisedParameter(parameterName);
		}
		else  {
			translator.translateParameter(parameterName);
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
			if (parameterName.length() < 3) {
				throw new IllegalArgumentException("Parameters must be at least 2 characters long");
			}
			name = parameterName.substring(1, 3).toUpperCase();	
		}
		return name;
	}
	
	protected abstract void unrecognisedParameter(String parameter);
	

}
