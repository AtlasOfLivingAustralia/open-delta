package au.org.ala.delta.translation.print;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractIterativeTranslator;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;

/**
 * Writes the character list to the print file.
 */
public class ItemNamesPrinter extends AbstractIterativeTranslator {
	
	protected PrintFile _printer;
	protected ItemFormatter _itemFormatter;
	protected ItemListTypeSetter _typeSetter;
	
	public ItemNamesPrinter(
			DeltaContext context, 
			ItemFormatter formatter, PrintFile printFile,
			ItemListTypeSetter typeSetter) {
		
		_printer = printFile;
		_itemFormatter = formatter;
		_typeSetter = typeSetter;
	}
	
	@Override
	public void beforeFirstItem() {
		
	}
	
	@Override
	public void beforeItem(Item item) {
		_typeSetter.beforeItemName();
	}

	@Override
	public void afterItem(Item item) {
		String description = _itemFormatter.formatItemDescription(item);
		_printer.outputLine(description);
		
	}
	
	@Override
	public void afterLastItem() {
		_typeSetter.afterLastItem();
	}
}
