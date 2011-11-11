package au.org.ala.delta.translation;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;

/**
 * A translator that does nothing.
 */
public class NullTranslator implements DataSetTranslator {

	@Override
	public void translateCharacters() {}

	@Override
	public void translateItems() {}

	@Override
	public void translateOutputParameter(OutputParameter parameterName) {}
}
