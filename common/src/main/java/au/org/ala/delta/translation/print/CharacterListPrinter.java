package au.org.ala.delta.translation.print;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.Printer;
import au.org.ala.delta.translation.delta.DeltaFormatTranslator;

/**
 * Writes the character list to the print file.
 */
public class CharacterListPrinter extends DeltaFormatTranslator implements PrintAction {
	public CharacterListPrinter(
			DeltaContext context, Printer printer, ItemFormatter itemFormatter) {
		super(context, printer, itemFormatter);
	}
		
	@Override
	public void translateItems() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void print() {
		translateCharacters();
	}
	
}
