package au.org.ala.delta.translation;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * Doesn't do much in the way of typesetting or formatting, inserts some blank lines 
 * for paragraph marks.
 */
public class PlainTextTypeSetter implements ItemListTypeSetter {

	private PrintFile _printer;
	private int _blankLinesBeforeItem;
	
	public PlainTextTypeSetter(PrintFile output) {
		this(output, 2);
	}
	
	public PlainTextTypeSetter(PrintFile output, int blankLinesBeforeItem) {
		_printer = output;
		_blankLinesBeforeItem = blankLinesBeforeItem;
		
	}

	@Override
	public void beforeFirstItem() {}

	@Override
	public void beforeItem(Item item) {
		_printer.writeBlankLines(_blankLinesBeforeItem, 5);
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

	@Override
	public void beforeCharacterDescription(Character character, Item item) {}

	@Override
	public void afterCharacterDescription(Character character, Item item) {}

	@Override
	public void beforeEmphasizedCharacter() {}

	@Override
	public void afterEmphasizedCharacter() {}
	
}
