package au.org.ala.delta.translation.print;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.delta.DeltaFormatTranslator;

/**
 * Writes the item descriptions to the print file.
 */
public class ItemDescriptionsPrinter extends DeltaFormatTranslator {
	
	private ItemListTypeSetter _typeSetter;
	private AttributeFormatter _attributeFormatter;
	
	public ItemDescriptionsPrinter(
			DeltaContext context,
			PrintFile printer,
			ItemFormatter itemFormatter, 
			AttributeFormatter attributeFormatter,
			ItemListTypeSetter typeSetter) {
		super(context, printer, itemFormatter, null, null);
		_typeSetter = typeSetter;
		_attributeFormatter = attributeFormatter;
	}
	
	@Override
	public void beforeFirstItem() {}
	
	@Override
	public void beforeItem(Item item) {
		_typeSetter.beforeItem(item);
		super.beforeItem(item);
		_typeSetter.afterItemName();
	}

	@Override
	public void afterItem(Item item) {
		super.afterItem(item);
		
	}
	
	@Override
	public void afterLastItem() {
		_typeSetter.afterLastItem();
	}
	
	@Override
	protected String getAttributeValue(Attribute attribute) {
		String value = super.getAttributeValue(attribute);
		return _attributeFormatter.formatComment(value);
	}
}
