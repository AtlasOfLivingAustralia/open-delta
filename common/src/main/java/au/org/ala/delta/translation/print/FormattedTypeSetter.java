package au.org.ala.delta.translation.print;

import java.util.Map;

import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.translation.Printer;

/**
 * Inserts typesetting marks (specified using the TYPESETTING MARKS directive)
 * into a character list produced using the PRINT CHARACTER LIST directive.
 */
public class FormattedTypeSetter extends PlainTextTypeSetter {

	/** The typesetting marks to use */
	private Map<Integer, TypeSettingMark> _marks;
	
	public FormattedTypeSetter(Map<Integer, TypeSettingMark> typeSettingMarks, Printer printer) {
		super(printer);
		_marks = typeSettingMarks;
	}
	
	@Override
	public void beforeCharacterOrHeading() {
		writeTypeSettingMark(MarkPosition.BEFORE_CHARACTER_OR_HEADING);
	}

	@Override
	public void beforeFirstCharacterOrHeading() {
		writeTypeSettingMark(MarkPosition.BEFORE_FIRST_CHARACTER_OR_HEADING);
	}

	@Override
	public void beforeCharacterHeading() {
		writeTypeSettingMark(MarkPosition.BEFORE_CHARACTER_HEADING);
	}

	@Override
	public void afterCharacterHeading() {
		writeTypeSettingMark(MarkPosition.AFTER_CHARACTER_HEADING);
	}

	@Override
	public void beforeCharacter() {
		writeTypeSettingMark(MarkPosition.BEFORE_CHARACTER);
	}

	@Override
	public void beforeStateDescription() {
		writeTypeSettingMark(MarkPosition.BEFORE_STATE_DESCRIPTION);
	}

	@Override
	public void beforeCharacterNotes() {
		writeTypeSettingMark(MarkPosition.BEFORE_CHARACTER_NOTES);
		_printer.printBufferLine();
	}

	@Override
	public void afterCharacterList() {
		writeTypeSettingMark(MarkPosition.AFTER_CHARACTER_LIST);
	}
	
	private void writeTypeSettingMark(String mark) {
		_printer.writeTypeSettingMark(mark);
	}
	
	private void writeTypeSettingMark(MarkPosition mark) {
		
		writeTypeSettingMark(_marks.get(mark.getId()).getMarkText());
	}
}
