package au.org.ala.delta.translation;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;

/**
 * Doesn't do much in the way of typesetting or formatting, inserts some blank lines 
 * for paragraph marks.
 */
public class PlainTextTypeSetter implements ItemListTypeSetter {

	private PrintFile _printer;
	
	public PlainTextTypeSetter(PrintFile typeSetter) {
		_printer = typeSetter;
	}

	@Override
	public void beforeFirstItem() {}

	@Override
	public void beforeItem(Item item) {
		_printer.writeBlankLines(2, 5);
	}

	@Override
	public void afterItem(Item item) {}
	
	@Override
	public void beforeAttribute(Attribute attribute) {}
	
	@Override
	public void afterAttribute(Attribute attribute) {}
	
	@Override
	public void afterLastItem() {
		_printer.printBufferLine();
	}
	
	@Override
	public void beforeItemHeading() {}
	
	@Override
	public void afterItemHeading() {}
	
	@Override
	public void beforeItemName() {}

	@Override
	public void afterItemName() {}
	
	@Override
	public void newParagraph() {
		_printer.writeBlankLines(1, 2);
		_printer.setIndent(6);
		_printer.indent();
	}

	@Override
	public String typeSetItemDescription(String description) {
		return description;
	}

	@Override
	public void beforeNewParagraphCharacter() {	}

	@Override
	public String rangeSeparator() {
		return "-";
	}
}
