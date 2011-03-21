package au.org.ala.delta.translation;

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
	
	private void translateItem(Item item) {
	
		
		// If Item is excluded by the EXCLUDE ITEMS directive
		if (_context.isExcluded(item.getItemNumber())) {
			return;
		}
		
		if (!_printingImplicitAttributeSection) {
			
			// Check if characters have been added to this Item via the ADD CHARACTERS directive
			// Sets the local array IADDC.
			
			// Same for emphasised characters - initialises IEMPC		
		}
		else {
			// Set all elements of IADDC to 0.
			// Set all elements of IEMPC to 0
		}
		
		
		// Setup indentation.
		
		// If we are doing typesetting do _output.blankLine(1, 0);
		// 
		_typeSetter.writeBlankLines(2, 5); // different for typeset....
		
		if (!_printingImplicitAttributeSection) {
			_typeSetter.insertTypeSettingMarks(13);
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
		
		if (_startOfNewFile) {
			_typeSetter.insertTypeSettingMarks(14);
		}
		else  {
			_typeSetter.insertTypeSettingMarks(51);
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
	
		
}
