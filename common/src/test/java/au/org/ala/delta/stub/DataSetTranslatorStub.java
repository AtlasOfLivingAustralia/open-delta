package au.org.ala.delta.stub;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.translation.DataSetTranslator;

/**
 * Stubs the DataSetTranslator interface.
 */
public class DataSetTranslatorStub implements DataSetTranslator {

	@Override
	public void translateCharacters() { }

	@Override
	public void translateItems() { }
	
	@Override
	public void translateOutputParameter(OutputParameter parameterName) {}
}
