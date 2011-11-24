package au.org.ala.delta.translation.print;

import java.util.Map;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.translation.PrintFile;

public class FormattedUncodedCharactersTypeSetter extends UncodedCharactersTypeSetter {

	private Map<Integer, TypeSettingMark> _typeSettingMarks;
	
	public FormattedUncodedCharactersTypeSetter(PrintFile printer, DeltaContext context) {
		super(printer);
		_typeSettingMarks = context.getTypeSettingMarks();
	}
	
	public void beforeUncodedCharacterList() {
		_printer.printBufferLine();
		writeTypeSettingMark(MarkPosition.BEFORE_LIST_OF_UNCODED_CHARACTERS);
	}
	
	
	public void beforeNewParagraph() {
		writeTypeSettingMark(MarkPosition.BEFORE_NEW_PARAGRAPH_CHARACTER);
	}
	
	public String rangeSeparator() {
		TypeSettingMark typesettingMark =_typeSettingMarks.get(MarkPosition.RANGE_SYMBOL.getId());
		if (typesettingMark != null) {
			return typesettingMark.getMarkText();
		}
		return super.rangeSeparator();
	}
	
	protected void writeTypeSettingMark(MarkPosition mark) {
		TypeSettingMark typesettingMark = _typeSettingMarks.get(mark.getId());
		if (typesettingMark != null) {
			_printer.writeTypeSettingMark(typesettingMark.getMarkText());
		}
	}
}
