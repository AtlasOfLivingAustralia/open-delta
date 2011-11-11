package au.org.ala.delta.translation;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;


public interface DataSetTranslator {

	public void translateCharacters();
	
	public void translateItems();
	
	public void translateOutputParameter(OutputParameter parameterName);
}