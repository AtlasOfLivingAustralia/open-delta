package au.org.ala.delta.stub;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;

public class DataSetTranslatorFactoryStub extends DataSetTranslatorFactory {

	@Override
	public DataSetTranslator createTranslator(DeltaContext context) {
		return new DataSetTranslatorStub();
	}
}
