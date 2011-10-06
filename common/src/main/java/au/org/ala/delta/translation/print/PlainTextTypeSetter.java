package au.org.ala.delta.translation.print;

import au.org.ala.delta.translation.Printer;

public class PlainTextTypeSetter implements CharacterListTypeSetter {

	protected Printer _printer;
	
	public PlainTextTypeSetter(Printer printer) {
		_printer = printer;
	}

	@Override
	public void beforeCharacterOrHeading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeFirstCharacterOrHeading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeCharacterHeading() {
		_printer.writeBlankLines(1, 0);
	}

	@Override
	public void afterCharacterHeading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeCharacter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeStateDescription() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeCharacterNotes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCharacterList() {
		// TODO Auto-generated method stub
		
	}

}
