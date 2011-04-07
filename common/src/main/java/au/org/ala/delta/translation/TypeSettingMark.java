package au.org.ala.delta.translation;

public class TypeSettingMark {

	
	
	public enum CommonMarks { 
		RANGE_SYBOL(1);
	
		private int _id;
		private CommonMarks(int id) {
			_id = id;
		}
	};
	
	public enum PrintCharacterListMarks {
		BEFORE_CHARACTER_OR_HEADING(2), BEFORE_FIRST_CHARACTER_OR_HEADING(3), BEFORE_CHARACTER_HEADING(4),
		AFTER_CHARACTER_HEADING(5), BEFORE_CHARACTER(6), BEFORE_STATE_DESCRIPTION(7), 
		BEFORE_CHARACTER_NOTES(8), AFTER_CHARACTER_LIST(40);
		private int _id;
		private PrintCharacterListMarks(int id) {
			_id = id;
		}
	};
	
	public enum PrintItemDescriptionMarks {
		BEFORE_ITEM_WITH_NATURAL_LANGUAGE(9), BEFORE_ITEM_WITHOUT_NATURAL_LANGUAGE(10), AFTER_ITEM_NAME(11);
		private int _id;
		private PrintItemDescriptionMarks(int id) {
			_id = id;
		}
	}

	public enum NaturalLanguageMarks {
		BEFORE_ITEM_OR_HEADING(13), BEFORE_ITEM_NAME_AT_START_OF_FILE(14), AFTER_ITEM_NAME(15),
		BEFORE_NEW_PARAGRAPH_CHARACTER(16), BEFORE_EMPHASIZED_FEATURE(17), AFTER_EMPHASZIED_FEATURE(18),
		BEFORE_EMPHASIZED_CHARACTER(19), AFTER_EMPHASZIED_CHARACTER(20), 
		BEFORE_EMPHASIZED_STATE_DESCRIPTION(21), AFTER_EMPHASZIED_STATE_DESCRIPTION(22),
		BEFORE_NON_COMMENT_ITEM_NAME_SECTION(25), AFTER_NON_COMMENT_ITEM_NAME_SECTION(26),
		AFTER_ITEM(27), START_OF_FILE(28), AFTER_LAST_ITEM_IN_FILE(29), BEFORE_ITEM_HEADING(30),
		AFTER_ITEM_HEADING(31), BEFORE_ITEM_SUBHEADING(32), AFTER_ITEM_SUBHEADING(33),
		BEFORE_OUTPUT_FILE_NAME_INDEX_FILE(34), BETWEEN_OUTPUT_FILE_NAME_AND_TAXON_NAME_INDEX_FILE(35),
		AFTER_TAXON_NAME_INDEX_FILE(36), BEFORE_IMAGE_FILE_NAME(37), BETWEEN_IMAGE_FILE_NAME_AND_SUBJECT(38),
		AFTER_SUBJECT(39), BEFORE_ITEM_NAME(51);
		
		private int _id;
		private NaturalLanguageMarks(int id) {
			_id = id;
		}
	}
	
	public enum PrintItemNameMarks {
		
		BEFORE_ITEM_NAME(23);
		private int _id;
		private PrintItemNameMarks(int id) {
			_id = id;
		}
	};

	public enum PrintUncodedCharacterMarks { 
		BEFORE_LIST_OF_UNCODED_CHARACTERS(24);
		
		private int _id;
		private PrintUncodedCharacterMarks(int id) {
			_id = id;
		}
	};

	public enum KeyMarks {
		PARAMETERS(41), FIRST_LEAD_OF_FIRST_NODE(42), FIRST_LEAD_OF_NODE(43), 
		SUBSEQUENT_LEAD_OF_NODE(44), FIRST_DESTINATION_OF_LEAD(45), SUBSEQUENT_DESTINATION_OF_LEAD(46),
		AFTER_TAZXON_NAME(47), DESTINATION_OF_LEAD_NODE(48), AFTER_NODE(49), END_OF_KEY(50);
		private int _id;
		private KeyMarks(int id) {
			_id = id;
		}
	};
	
	
	private String _mark;
	public void setMark(String mark) {
		mark = _mark;
	}
	
}
