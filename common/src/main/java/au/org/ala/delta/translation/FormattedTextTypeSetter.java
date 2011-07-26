package au.org.ala.delta.translation;

import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.model.format.Formatter.CommentExtractor;


/**
 * Inserts specified typesetting marks at various points in the translation process.
 */
public class FormattedTextTypeSetter extends PlainTextTypeSetter {

	private Map<Integer, TypeSettingMark> _typeSettingMarks;
	private Printer _printer;
	
	public FormattedTextTypeSetter(Map<Integer, TypeSettingMark> typeSettingMarks, Printer typeSetter) {
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
		
		String afterItemMark = _typeSettingMarks.get(MarkPosition.AFTER_ITEM.getId()).getMarkText();
		if (StringUtils.isNotEmpty(afterItemMark)) {
			// The prepended space here seems unnecessary but it makes it match the CONFOR 
			// output which simplifies my testing.
			writeTypeSettingMark(" "+afterItemMark);
		}

	}

	@Override
	public void beforeAttribute(Attribute attribute) {}

	@Override
	public void afterAttribute(Attribute attribute) {}

	@Override
	public void afterLastItem() {
		_printer.printBufferLine();
		writeTypeSettingMark(MarkPosition.AFTER_LAST_ITEM_IN_FILE);
		_printer.printBufferLine();
	}
	
	private void writeTypeSettingMark(String mark) {
		_printer.writeTypeSettingMark(mark);
	}
	
	private void writeTypeSettingMark(MarkPosition mark) {
		
		writeTypeSettingMark(_typeSettingMarks.get(mark.getId()).getMarkText());
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
	
	@Override
	public String typeSetItemDescription(String description) {
		return typeSetCommentedValue(description, 
				MarkPosition.BEFORE_NON_COMMENT_ITEM_NAME_SECTION, 
				MarkPosition.AFTER_NON_COMMENT_ITEM_NAME_SECTION);

	}
	
	@Override
	public void beforeNewParagraphCharacter() {
		writeTypeSettingMark(MarkPosition.BEFORE_NEW_PARAGRAPH_CHARACTER);
	}
	
	@Override
	public String rangeSeparator() {
		return _typeSettingMarks.get(MarkPosition.RANGE_SYMBOL.getId()).getMarkText();
	}
		
	/**
	 * Parses the supplied value, inserting the supplied typesetting marks before and after each
	 * non-commented section of the value.
	 * @param value the commented value to typeset.
	 * @param beforePosition the typesetting mark to insert before each non-commented section of the value.
	 * @param afterPosition the typesetting mark to after before each non-commented section of the value.
	 */
	private String typeSetCommentedValue(String value, MarkPosition beforePosition, MarkPosition afterPosition) {
		String beforeValueMark = _typeSettingMarks.get(beforePosition.getId()).getMarkText();
		String afterValueMark = _typeSettingMarks.get(afterPosition.getId()).getMarkText();
		
		CommentedValueTypeSetter typeSetter = new CommentedValueTypeSetter(value, beforeValueMark, afterValueMark);
		try {
			typeSetter.parse();
		}
		catch (Exception e) {
			throw new RuntimeException("Unexpected error occured typesetting: "+value, e);
		}
		return typeSetter.getTypesetValue();
	}

	
	/**
	 * Typesets a value containing comments by inserting typesetting marks before and after
	 * non-comment portions of the value.
	 */
	class CommentedValueTypeSetter extends CommentExtractor {

		private String _beforeValueMark;
		private String _afterValueMark;
		
		private StringBuilder _typeSetValue;
		
		public CommentedValueTypeSetter(String value, String beforeValueMark, String afterValueMark) {
			super(value);
			_beforeValueMark = beforeValueMark;
			_afterValueMark = afterValueMark;
			_typeSetValue = new StringBuilder();
		}
		
		@Override
		public void comment(String comment) throws ParseException {
			_typeSetValue.append(comment);
		}

		@Override
		public void value(String value) throws ParseException {
			_typeSetValue.append(_beforeValueMark);
			
			_typeSetValue.append(value);
			
			_typeSetValue.append(_afterValueMark);
		}
		
		public String getTypesetValue() {
			return _typeSetValue.toString();
		}
	}
}
