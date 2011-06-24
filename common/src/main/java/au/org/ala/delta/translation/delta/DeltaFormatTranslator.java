package au.org.ala.delta.translation.delta;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractDataSetTranslator;
import au.org.ala.delta.translation.Printer;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;
import au.org.ala.delta.translation.naturallanguage.NaturalLanguageDataSetFilter;

public class DeltaFormatTranslator extends AbstractDataSetTranslator {

	private Printer _printer;
	private ItemFormatter _itemFormatter;
	
	public DeltaFormatTranslator(DeltaContext context, Printer printer) {
		super(context, new NaturalLanguageDataSetFilter(context));
		
		_printer = new Printer(context.getPrintStream(), context.getPrintWidth());
		_itemFormatter = new ItemFormatter();
	}
	
	@Override
	public void beforeFirstItem() {
		outputLine("* ITEM DESCRIPTIONS");
	}

	@Override
	public void beforeItem(Item item) {
		output("#");
		output(_itemFormatter.formatItemDescription(item));
		output("/");
		_printer.printBufferLine();
	}

	@Override
	public void afterItem(Item item) {
		_printer.printBufferLine();
		_printer.writeBlankLines(1, 5);
	}

	@Override
	public void beforeAttribute(Attribute attribute) {
		StringBuilder attributeValue = new StringBuilder();
		
		au.org.ala.delta.model.Character character = attribute.getCharacter();
		attributeValue.append(Integer.toString(character.getCharacterId()));
		attributeValue.append(",");
		String value = attribute.getValueAsString();
		if (StringUtils.isNotEmpty(value)) {
			attributeValue.append(value);
		}
		output(attributeValue.toString());
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
	
	private void outputLine(String value) {
		output(value);
		_printer.printBufferLine();
	}
}
