package au.org.ala.delta.translation;

import java.util.Map;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.TypeSettingMark.NaturalLanguageMarks;

public class FormattedTextTypeSetter extends PlainTextTypeSetter {

	private Map<TypeSettingMark, String> _typeSettingMarks;
	private TypeSetter _printer;
	
	public FormattedTextTypeSetter(Map<TypeSettingMark, String> typeSettingMarks, TypeSetter typeSetter) {
		super(typeSetter);
		_printer = typeSetter;
		_typeSettingMarks = typeSettingMarks;
	}
	
	@Override
	public void beforeFirstItem() {
		writeTypeSettingMark(TypeSettingMark.NaturalLanguageMarks.START_OF_FILE);
	}

	@Override
	public void beforeItem(Item item) {
		writeTypeSettingMark(NaturalLanguageMarks.BEFORE_ITEM_OR_HEADING);

	}

	@Override
	public void afterItem(Item item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeAttribute(Attribute attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterAttribute(Attribute attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterLastItem() {
		writeTypeSettingMark(NaturalLanguageMarks.AFTER_LAST_ITEM_IN_FILE);
	}
	
	private void writeTypeSettingMark(TypeSettingMark.NaturalLanguageMarks mark) {
		
		_printer.writeJustifiedText(_typeSettingMarks.get(mark), -1, false);
	}
	
	public void beforeItemHeading() {
		writeTypeSettingMark(NaturalLanguageMarks.BEFORE_ITEM_HEADING);
	}
	
	public void afterItemHeading() {
		writeTypeSettingMark(NaturalLanguageMarks.AFTER_ITEM_HEADING);
	}
	
	boolean startOfFile = false;
	public void beforeItemName() {
		if (startOfFile) {
			writeTypeSettingMark(NaturalLanguageMarks.BEFORE_ITEM_NAME_AT_START_OF_FILE);
		}
		else {
			writeTypeSettingMark(NaturalLanguageMarks.BEFORE_ITEM_NAME);
		}
		
	}
	public void afterItemName() {
		writeTypeSettingMark(NaturalLanguageMarks.AFTER_ITEM_NAME);
	}
	
	public void newParagraph() {
		_printer.endLine();
		_printer.insertTypeSettingMarks(16);
	}

}
