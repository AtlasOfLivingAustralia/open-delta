package au.org.ala.delta.translation;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;

/**
 * Writes natural language description of a DeltaDataSet using the configuration specified by the
 * DeltaContext. 
 */
public class NaturalLanguageTranslator {

	private TypeSetter _typeSetter;
	private DeltaContext _context;
	private Formatter _formatter;
	
	public NaturalLanguageTranslator(DeltaContext context, TypeSetter typeSetter) {
		_context = context;
		_typeSetter = typeSetter;
		_formatter = new Formatter(context);
	}
	
	/**
	 * Translates the DeltaDataSet into a natural lanugage description.
	 */
	public void translate() {
		// TODO work out whether we need to output an "implicit attributes" section.
		// if not, i think we need to explicitly include the implicit attributes in the items...
		
		
		// Start of file mark.
		_typeSetter.insertTypeSettingMarks(28);
		
		DeltaDataSet dataSet = _context.getDataSet();
		
		int numItems = dataSet.getMaximumNumberOfItems();
		for (int i=1; i<=numItems; i++) {
			translateItem(dataSet.getItem(i));
		}
		
		// End of file mark
		_typeSetter.insertTypeSettingMarks(29);
	}
	

	private boolean _printingImplicitAttributeSection;
	
	/** This is a static local variable in the fortran code*/
	private int currentItemHeadingItemNumber;
	/** This is a static local variable in the fortran code */
	private int currentIndexHeadingItemNumber;
	
	
	enum TypeSetting {ADD_TYPESETTING_MARKS, DO_NOTHING, REMOVE_EXISTING_TYPESETTINGMARKS};
	TypeSetting _typeSettingMode;
	
	private void translateItem(Item item) {
	
		
		boolean ifBegin = false;
		boolean ifEnd = true;
		boolean chineseLanguageFormat = false;
		
		int itemNumber = item.getItemNumber();
		boolean isImplicitCharactersSection = item.getDescription().equals("Implicit Characters");
		
		if (!isImplicitCharactersSection) {
			if (StringUtils.isNotEmpty(_context.getItemHeading(itemNumber))) {
				currentItemHeadingItemNumber = itemNumber; // ITHD
			}
			if (StringUtils.isNotEmpty(_context.getIndexHeading(itemNumber))) {
				currentIndexHeadingItemNumber = itemNumber; // IXHD
			}
			
			// If Item is excluded by the EXCLUDE ITEMS directive
			if (_context.isExcluded(item.getItemNumber())) {
				return;
			}
		}
		
		// Output a typesetting mark at the start of each file.
		if (!ifBegin) {
			_typeSetter.insertTypeSettingMarks(28);
			ifBegin = true;
			ifEnd = false;
		}
		
		// The "Implicit Characters" section ignores the ADD CHARACTERS and EMPHASISE CHARACTERS
		// directive.
		if (!isImplicitCharactersSection) {
			// Copies IADDT(item number) into IADDC if ADD CHARACTERS directive not null and 
			// data exists for the item.
		
			// Otherwise initialise IADDC to all 0s.
			
			// Same again for EMPHAISE CHARACTERS - copies IEMPT(item number) into IEMPC 
			// Or initialises IEMPC to all zeros.
				
		}
		else {
			// initialises IEMPC and IADDC to all 0s.  
		}
		
		// Setup indentation.
		int pSeq = 0;
		_typeSetter.indent(0);
		int numBlankLines;
		
		if (chineseLanguageFormat) {
			numBlankLines=1;
		}
		else {
			numBlankLines = 2;
		}
		if (_typeSettingMode == TypeSetting.DO_NOTHING || _typeSettingMode == TypeSetting.REMOVE_EXISTING_TYPESETTINGMARKS) {
			_typeSetter.writeBlankLines(numBlankLines, 5);
		}
		else {
			_typeSetter.writeBlankLines(1, 0);
			if (!isImplicitCharactersSection) {
				_typeSetter.insertTypeSettingMarks(13);
			}
		}
		
		printItemHeading(item.getItemNumber());
		printIndexHeading(item.getItemNumber());
	
		if (!_printingImplicitAttributeSection) {
		
			
			printTaxonName(item);
			printTaxonNameToIndex(item);
		}
		
		
		
		
		
		int numChars = _context.getDataSet().getNumberOfCharacters();
		
		for (int i=1; i<=numChars; i++) {
			
			// Handle new paragraph directive here.
			
			// Deal with linked characters
			
			// Handle feature empahsis for linked emphasized characters
			
			Character character = _context.getDataSet().getCharacter(i);
			
			translateCharacterDescription(character);
			writeCharacterAttributes(item, character);
		}
	
	}
	
	private void translateCharacterDescription(Character character ) {
		if (_context.getNewParagraphCharacters().contains(character.getCharacterId())) {
			_typeSetter.newParagraph();
		}
		
		String characterDescription = _formatter.formatCharacterName(character.getDescription());
		_typeSetter.writeText(WordUtils.capitalize(characterDescription));
	}
	
	private void printTaxonNameToIndex(Item item) {
		// TODO Auto-generated method stub
		
	}


	private void printItemHeading(int itemNumber) {
		
		String heading = _context.getItemHeading(itemNumber);
		if (heading == null) {
			return;
		}
		_typeSetter.insertTypeSettingMarks(30);
		_typeSetter.writeSentence(heading);
		_typeSetter.insertTypeSettingMarks(31);
	}
	
	// Lines 857-882 of TNAT.FOR
	private void printIndexHeading(int itemNumber) {
		
		// ignoring for the moment....
	}
	
	private boolean _startOfNewFile = false;
	
	private void printTaxonName(Item item) {
		
		Integer characterForTaxonNames = _context.getCharacterForTaxonNames();
		
		int typeSettingMarkNum = 14;
		
		// if !_startOfNewFile || _already output item heading and value exists for type mark 51
		if (!_startOfNewFile) {
			typeSettingMarkNum = 51;
		}
		
		if (characterForTaxonNames != null) {
			//writeCharacterForTaxonName(characterForTaxonNames, typeSettingMarkNum);
		}
		else {
			//WNAME();
		}
		
		
		// A taxon name can be override by the CHARACTER FOR TAXON NAME directive
		// Need to treat variant items differently
		// Non alternative names have an extra typesetting mark and do something with comments.
		_typeSetter.writeSentence(_formatter.formatTaxonName(item.getDescription()));
		
		_typeSetter.insertTypeSettingMarks(15);
	}


	private void writeCharacterAttributes(Item item, Character character) {
		if (item.hasAttribute(character)) {
			_typeSetter.writeText(" "+_formatter.formatAttribute(character, item.getAttribute(character).getValue()));
		}
		
	}
	
	private void writeCharacterForTaxonName(Item item, int characterNumber, int typeSettingMarkNum, boolean masterItemMaskedIn) {
		if (_typeSettingMode == TypeSetting.ADD_TYPESETTING_MARKS) {
			_typeSetter.insertTypeSettingMarks(typeSettingMarkNum);
		}
		
		if (item.isVariant() && masterItemMaskedIn) {
			// next character is a capital
			
		}
		
	}
	
		
}
