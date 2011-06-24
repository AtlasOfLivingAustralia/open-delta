package au.org.ala.delta.translation.delta;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractDataSetTranslator;
import au.org.ala.delta.translation.DataSetFilter;
import au.org.ala.delta.translation.Printer;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;

public class DeltaFormatTranslator extends AbstractDataSetTranslator {

	private Printer _printer;
	private ItemFormatter _itemFormatter;
	
	public DeltaFormatTranslator(DeltaContext context, DataSetFilter filter) {
		super(context, filter);
		_printer = new Printer(context.getPrintStream(), context.getPrintWidth());
		_itemFormatter = new ItemFormatter();
	}
	
	@Override
	public void beforeFirstItem() {}

	@Override
	public void beforeItem(Item item) {
		output("#");
		output(_itemFormatter.formatItemDescription(item));
		output("/");
	}

	@Override
	public void afterItem(Item item) {}

	@Override
	public void beforeAttribute(Attribute attribute) {
		output(attribute.getValueAsString());
	}

	@Override
	public void afterAttribute(Attribute attribute) {}

	@Override
	public void afterLastItem() {}

	@Override
	public void attributeComment(String comment) {}

	@Override
	public void attributeValues(Values values) {}

	
	private void output(String value) {
		_printer.writeJustifiedText(value, -1);
	}
}
