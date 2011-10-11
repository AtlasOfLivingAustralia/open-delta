package au.org.ala.delta.translation.print;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.delta.DeltaFormatTranslator;

/**
 * Writes the item descriptions to the print file.
 */
public class ItemDescriptionsPrinter extends DeltaFormatTranslator implements PrintAction {
	
	private ItemListTypeSetter _typeSetter;
	
	
	public ItemDescriptionsPrinter(
			DeltaContext context, PrintFile printer, ItemFormatter itemFormatter, ItemListTypeSetter typeSetter) {
		super(context, printer, itemFormatter, null, null);
	}
		
	@Override
	public void translateCharacters() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void print() {
		translateItems();
	}
	
	
	@Override
	public void beforeItem(Item item) {
		//_typeSetter.beforeItem(item);
	}

	@Override
	public void afterItem(Item item) {
		super.afterItem(item);
		
	}
	
	@Override
	public void afterLastItem() {
		//_typeSetter.afterLastItem();
	}
}
