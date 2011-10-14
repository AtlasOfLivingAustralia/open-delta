package au.org.ala.delta.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A TypeSettingMark holds the text to be inserted at particular points during the translation of
 * a DELTA data set.
 * This text is specified by the TYPESETTING MARKS directive.
 */
public class TypeSettingMark {

	public static enum MarkPosition {
		// Common marks.
		RANGE_SYMBOL(1),
		
		// Marks used by PRINT CHARACTER LIST
		BEFORE_CHARACTER_OR_HEADING(2), BEFORE_FIRST_CHARACTER_OR_HEADING(3), BEFORE_CHARACTER_HEADING(4),
		AFTER_CHARACTER_HEADING(5), BEFORE_CHARACTER(6), BEFORE_STATE_DESCRIPTION(7), 
		BEFORE_CHARACTER_NOTES(8), AFTER_CHARACTER_LIST(40),
		
		
		// Marks used by PRINT ITEM DESCRIPTIONS
		BEFORE_ITEM_WITH_NATURAL_LANGUAGE(9), BEFORE_ITEM_WITHOUT_NATURAL_LANGUAGE(10), 
		ITEM_DESCRIPTION_AFTER_ITEM_NAME(11), ITEM_DESCRIPTION_BEFORE_ITEM_NAME(23),
		
		// Marks used by TRANSLATE INTO NATURAL LANGUAGE
		BEFORE_ITEM_OR_HEADING(13), BEFORE_ITEM_NAME_AT_START_OF_FILE(14), AFTER_ITEM_NAME(15),
		BEFORE_NEW_PARAGRAPH_CHARACTER(16), BEFORE_EMPHASIZED_FEATURE(17), AFTER_EMPHASIZED_FEATURE(18),
		BEFORE_EMPHASIZED_CHARACTER(19), AFTER_EMPHASIZED_CHARACTER(20), 
		BEFORE_EMPHASIZED_STATE_DESCRIPTION(21), AFTER_EMPHASZIED_STATE_DESCRIPTION(22),
		BEFORE_NON_COMMENT_ITEM_NAME_SECTION(25), AFTER_NON_COMMENT_ITEM_NAME_SECTION(26),
		AFTER_ITEM(27), START_OF_FILE(28), END_OF_FILE(29), BEFORE_ITEM_HEADING(30),
		AFTER_ITEM_HEADING(31), BEFORE_ITEM_SUBHEADING(32), AFTER_ITEM_SUBHEADING(33),
		BEFORE_OUTPUT_FILE_NAME_INDEX_FILE(34), BETWEEN_OUTPUT_FILE_NAME_AND_TAXON_NAME_INDEX_FILE(35),
		AFTER_TAXON_NAME_INDEX_FILE(36), BEFORE_IMAGE_FILE_NAME(37), BETWEEN_IMAGE_FILE_NAME_AND_SUBJECT(38),
		AFTER_SUBJECT(39), BEFORE_ITEM_NAME(51),
		
		// Marks used by PRINT UNCODED CHARACTERS
		BEFORE_LIST_OF_UNCODED_CHARACTERS(24),
		
		// Marks used by TRANSLATE INTO KEY FORMAT
		PARAMETERS(41), FIRST_LEAD_OF_FIRST_NODE(42), FIRST_LEAD_OF_NODE(43), 
		SUBSEQUENT_LEAD_OF_NODE(44), FIRST_DESTINATION_OF_LEAD(45), SUBSEQUENT_DESTINATION_OF_LEAD(46),
		AFTER_TAZXON_NAME(47), DESTINATION_OF_LEAD_NODE(48), AFTER_NODE(49), END_OF_KEY(50);
		
		
		
		private static Map<Integer, MarkPosition> _markIds = new HashMap<Integer, MarkPosition>();
		static {
			for (MarkPosition mark : MarkPosition.values()) {
			    _markIds.put(mark.getId(), mark);
			}
		}
		
		private int _id;
		private MarkPosition(int id) {
			_id = id;
		};
		public int getId() {
			return _id;
		}
		
		
		public static MarkPosition fromId(int id) {
			return _markIds.get(id);
		}
	} 
	
	public static enum CharacterNoteMarks {
		// Marks used by the FORMATTING MARKS directive
		CHARACTER_NOTES_FORMAT(2), CHARACTER_NOTES_HELP_FORMAT(3);
		private int _id;
		private CharacterNoteMarks(int id) {
			_id = id;
		};
		public int getId() {
			return _id;
		}
	}
	
	
	private int _id;
	private String _text;
	private boolean _allowLineBreaks;
	
	/**
	 * Assigns text to a particular Mark.
	 * @param mark identifies the point in the translation for the output.
	 * @param text the text to be output.
	 * @param allowLineBreaks whether or not the text can be split over more than one line.
	 */
	public TypeSettingMark(int id, String text, boolean allowLineBreaks) {
		_id = id;
		_text = text;
		_allowLineBreaks = allowLineBreaks;
	}
	
	public int getId() {
		return _id;
	}
	
	public String getMarkText() {
		return _text;
	}
	
	public boolean getAllowLineBreaks() {
		return _allowLineBreaks;
	}
}
