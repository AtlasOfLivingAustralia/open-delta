package au.org.ala.delta.translation;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;

public class PlainTextTypeSetter implements TypeSetter {

	private Printer _printer;
	
	public PlainTextTypeSetter(Printer typeSetter) {
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

	
	@Override
	public void beforeItemHeading() {
		
	}
	
	
	@Override
	public void afterItemHeading() {
		
	}
	
	
	@Override
	public void beforeItemName() {
		
	}
	

	@Override
	public void afterItemName() {
		
	}
	

	@Override
	public void newParagraph() {
		_printer.writeBlankLines(1, 2);
		_printer.setIndent(6);
		_printer.indent();
	}
	
}
