package au.org.ala.delta.translation;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.directives.validation.DirectiveException;


public interface DataSetTranslator {

	public void translateCharacters() throws DirectiveException;
	
	public void translateItems() throws DirectiveException;
	
	public void translateOutputParameter(OutputParameter parameterName);
}