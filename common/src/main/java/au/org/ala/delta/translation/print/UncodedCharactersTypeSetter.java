package au.org.ala.delta.translation.print;

import au.org.ala.delta.translation.PrintFile;

public class UncodedCharactersTypeSetter extends PlainTextTypeSetter {

	public UncodedCharactersTypeSetter(PrintFile printer) {
		super(printer);
	}

	public void beforeUncodedCharacterList() {
		_printer.writeBlankLines(1, 0);
	}
	
	
	public void beforeNewParagraph() {
		_printer.printBufferLine();
	}
	
	public String rangeSeparator() {
		return "-";
	}
}
