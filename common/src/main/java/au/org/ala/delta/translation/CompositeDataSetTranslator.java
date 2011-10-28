package au.org.ala.delta.translation;

import java.util.List;

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
	public void translateCharacters() {
		for (DataSetTranslator translator : _translators) {
			translator.translateCharacters();
		}
	}

	@Override
	public void translateItems() {
		for (DataSetTranslator translator : _translators) {
			translator.translateItems();
		}
	}

	@Override
	public void translateOutputParameter(String parameterName) {
		for (DataSetTranslator translator : _translators) {
			translator.translateOutputParameter(parameterName);
		}
	}

}
