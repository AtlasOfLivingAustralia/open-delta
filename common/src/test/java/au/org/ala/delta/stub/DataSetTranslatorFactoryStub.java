package au.org.ala.delta.stub;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;
import au.org.ala.delta.translation.PrintFile;

public class DataSetTranslatorFactoryStub extends DataSetTranslatorFactory {

	@Override
	public DataSetTranslator createTranslator(DeltaContext context) {
		return new DataSetTranslatorStub();
	}

	@Override
	public DataSetTranslator createTranslator(DeltaContext context, PrintFile printer) {
		return new DataSetTranslatorStub();
	}
}
