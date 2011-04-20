package au.org.ala.delta.translation;

import java.util.Map;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;

public class FormattedTextTypeSetter extends PlainTextTypeSetter {

	private Map<MarkPosition, TypeSettingMark> _typeSettingMarks;
	private Printer _printer;
	
	public FormattedTextTypeSetter(Map<MarkPosition, TypeSettingMark> typeSettingMarks, Printer typeSetter) {
		super(typeSetter);
		_printer = typeSetter;
		_typeSettingMarks = typeSettingMarks;
	}
	
	@Override
	public void beforeFirstItem() {
		writeTypeSettingMark(MarkPosition.START_OF_FILE);
	}

	@Override
	public void beforeItem(Item item) {
		_printer.writeBlankLines(1, 0);
		writeTypeSettingMark(MarkPosition.BEFORE_ITEM_OR_HEADING);

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
		writeTypeSettingMark(MarkPosition.AFTER_LAST_ITEM_IN_FILE);
	}
	
	private void writeTypeSettingMark(MarkPosition mark) {
		
		_printer.writeJustifiedText(_typeSettingMarks.get(mark).getMarkText(), -1, false);
	}
	
	public void beforeItemHeading() {
		writeTypeSettingMark(MarkPosition.BEFORE_ITEM_HEADING);
	}
	
	public void afterItemHeading() {
		writeTypeSettingMark(MarkPosition.AFTER_ITEM_HEADING);
	}
	
	boolean startOfFile = false;
	public void beforeItemName() {
		if (startOfFile) {
			writeTypeSettingMark(MarkPosition.BEFORE_ITEM_NAME_AT_START_OF_FILE);
		}
		else {
			writeTypeSettingMark(MarkPosition.BEFORE_ITEM_NAME);
		}
		
	}
	public void afterItemName() {
		writeTypeSettingMark(MarkPosition.AFTER_ITEM_NAME);
	}
	
	public void newParagraph() {
		_printer.endLine();
		_printer.insertTypeSettingMarks(16);
	}

}
