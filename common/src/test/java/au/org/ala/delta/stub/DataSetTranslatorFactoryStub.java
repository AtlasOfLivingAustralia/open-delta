package au.org.ala.delta.stub;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;
import au.org.ala.delta.translation.Printer;

public class DataSetTranslatorFactoryStub extends DataSetTranslatorFactory {

	@Override
	public DataSetTranslator createTranslator(DeltaContext context) {
		return new DataSetTranslatorStub();
	}

	@Override
	public DataSetTranslator createTranslator(DeltaContext context, Printer printer) {
		return new DataSetTranslatorStub();
	}
}
