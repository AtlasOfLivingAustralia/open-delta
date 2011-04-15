package au.org.ala.delta.translation;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;

public class PlainTextTypeSetter implements DataSetTranslator {

	private TypeSetter _printer;
	
	public PlainTextTypeSetter(TypeSetter typeSetter) {
		_printer = typeSetter;
	}
	
	
	@Override
	public void beforeFirstItem() {
		

	}

	@Override
	public void beforeItem(Item item) {
		
		_printer.writeBlankLines(2, 5);

	}

	@Override
	public void afterItem(Item item) {
		

	}

	@Override
	public void beforeAttribute(Attribute attribute) {
		

	}

	@Override
	public void afterAttribute(Attribute attribute) {
		

	}

	@Override
	public void afterLastItem() {
		_printer.printBufferLine();

	}

	public void beforeItemHeading() {
		
	}
	
	public void afterItemHeading() {
		
	}
	
	public void beforeItemName() {
		
	}
	public void afterItemName() {
		
	}
	public void newParagraph() {
		_printer.writeBlankLines(1, 2);
		_printer.setIndent(6);
		_printer.indent();
	}


	@Override
	public void attributeComment(String comment) {
		
	}


	@Override
	public void attributeValues(Values values) {
		
	}
	
}
