package au.org.ala.delta.translation.print;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.translation.Printer;
import au.org.ala.delta.translation.delta.DeltaFormatTranslator;

/**
 * Writes the character list to the print file.
 */
public class CharacterListPrinter extends DeltaFormatTranslator implements PrintAction {
	
	private CharacterListTypeSetter _typeSetter;
	
	public CharacterListPrinter(
			DeltaContext context, Printer printer, CharacterFormatter characterFormatter, CharacterListTypeSetter typeSetter) {
		super(context, printer, null, characterFormatter);
		_typeSetter = typeSetter;
	}
		
	@Override
	public void translateItems() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void print() {
		translateCharacters();
	}
	
	@Override
	public void beforeFirstCharacter() {
		_typeSetter.beforeFirstCharacter();
	}

	@Override
	public void beforeCharacter(Character character) {
		
		printCharacterHeading(character);
		
		_typeSetter.beforeCharacter();
		super.beforeCharacter(character);
		
	}

	private void printCharacterHeading(Character character) {
		String heading = _context.getCharacterHeading(character.getCharacterId());
		if (StringUtils.isNotBlank(heading)) {
			
			_typeSetter.beforeCharacterHeading();
			_printer.outputLine(0, _characterFormatter.defaultFormat(heading), 1);
			_typeSetter.afterCharacterHeading();
		}
	}

	@Override
	public void afterCharacter(Character character) {
		if (character.hasNotes()) {
			_typeSetter.beforeCharacterNotes();
			_printer.setIndentOnLineWrap(false);
			_printer.outputLine(0, _characterFormatter.defaultFormat(character.getNotes(), false), 0);
			_printer.setIndentOnLineWrap(true);
		}
		_printer.writeBlankLines(1, 0);
	}
	
	@Override
	protected void outputState(MultiStateCharacter character, int stateNumber) {
		_typeSetter.beforeStateDescription();
		super.outputState(character, stateNumber);
	}

	@Override
	protected void outputUnits(NumericCharacter<? extends Number> character) {
		if (character.hasUnits()) {
			_typeSetter.beforeStateDescription();
		
			super.outputUnits(character);
		}
	}
	
	
	
}
