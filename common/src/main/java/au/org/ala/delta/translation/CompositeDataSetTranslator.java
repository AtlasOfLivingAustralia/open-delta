package au.org.ala.delta.translation;

import java.util.List;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.directives.validation.DirectiveException;

/**
 * Allows multiple translations to occur (sequentially) on the same data
 * set.
 */
public class CompositeDataSetTranslator implements DataSetTranslator {

	private List<DataSetTranslator> _translators;
	
	public CompositeDataSetTranslator(List<DataSetTranslator> translators) {
		_translators = translators;
	}
	
	@Override
	public void translateCharacters() throws DirectiveException {
		for (DataSetTranslator translator : _translators) {
			translator.translateCharacters();
		}
	}

	@Override
	public void translateItems() throws DirectiveException {
		for (DataSetTranslator translator : _translators) {
			translator.translateItems();
		}
	}

	@Override
	public void translateOutputParameter(OutputParameter parameter) {
		for (DataSetTranslator translator : _translators) {
			translator.translateOutputParameter(parameter);
		}
	}

}
