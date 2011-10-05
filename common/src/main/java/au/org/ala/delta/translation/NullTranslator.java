package au.org.ala.delta.translation;

/**
 * A translator that does nothing.
 */
public class NullTranslator implements DataSetTranslator {

	@Override
	public void translateCharacters() {}

	@Override
	public void translateItems() {}

}
