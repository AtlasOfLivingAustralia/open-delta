package au.org.ala.delta.translation.naturallanguage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractDataSetTranslator;
import au.org.ala.delta.translation.DataSetFilter;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;
import au.org.ala.delta.translation.attribute.AttributeParser;
import au.org.ala.delta.translation.attribute.AttributeTranslator;
import au.org.ala.delta.translation.attribute.AttributeTranslatorFactory;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;

/**
 * The NaturalLanguageTranslator is responsible for turning a DELTA data set
 * into formatted natural language.
 */
public class NaturalLanguageTranslator extends AbstractDataSetTranslator {

	protected DeltaContext _context;
    protected PrintFile _printer;
    protected DeltaDataSet _dataSet;
    protected ItemListTypeSetter _typeSetter;
    protected ItemFormatter _itemFormatter;
    private CharacterFormatter _characterFormatter;
    private AttributeFormatter _attributeFormatter;
    private AttributeTranslatorFactory _attributeTranslatorFactory;
    
    public NaturalLanguageTranslator(
    		DeltaContext context, 
    		DataSetFilter filter,
    		ItemListTypeSetter typeSetter, 
    		PrintFile printer, 
    		ItemFormatter itemFormatter, 
    		CharacterFormatter characterFormatter,
            AttributeFormatter attributeFormatter) {
        super(context, filter);
        _context = context;
        _printer = printer;
        _dataSet = _context.getDataSet();
        _typeSetter = typeSetter;
        _itemFormatter = itemFormatter;
        _characterFormatter = characterFormatter;
        _attributeFormatter = attributeFormatter;
        _attributeTranslatorFactory = new AttributeTranslatorFactory(
        		context, _characterFormatter, _attributeFormatter, _typeSetter);
    }

  
    @Override
    public void beforeItem(Item item) {

    	boolean newFile = _context.getOutputFileSelector().createNewFileIfRequired(item);
    	if (newFile) {
    		_typeSetter.beforeFirstItem();
    	}
        _typeSetter.beforeItem(item);

        printItemHeading(item);

        printTaxonName(item);

        _newParagraph = true;
    }

    @Override
    public void afterItem(Item item) {
       finishWritingAttributes(item);
        _typeSetter.afterItem(item);
    }
    
    /**
     * This is necessary as attributes aren't written until all attributes of
     * a linked set have been processed.
     * @param item the item being finished.
     */
    protected void finishWritingAttributes(Item item) {
    	 if (!_characters.isEmpty()) {
             writeAttributes(item, _characters);
             _characters = new ArrayList<Character>();
         }
         if (_characterOutputSinceLastPuntuation) {
             writePunctuation(Word.FULL_STOP);
         }
         _lastCharacterOutput = 0;
         _previousCharInSentence = 0;
         _newParagraph = true;
    }

    private List<Character> _characters = new ArrayList<Character>();
    Set<Integer> _linkedCharacters = new HashSet<Integer>();

    @Override
    public void beforeAttribute(Attribute attribute) {

        Item item = attribute.getItem();
        au.org.ala.delta.model.Character character = attribute.getCharacter();
       
        String comma = Words.word(Word.COMMA);
        if (_context.useAlternateComma()) {
            comma = Words.word(Word.ALTERNATE_COMMA);
        }

        // It is most convenient to write linked characters out in a block.
        if (isPartOfLinkedSet(_linkedCharacters, character)) {
            _characters.add(character);
        } else {

            // This character is not a part of the previous set - write out the
            // previous set.
            if (!_characters.isEmpty()) {
                writeAttributes(item, _characters);
                _characters = new ArrayList<Character>();
            }

            _linkedCharacters = _context.getLinkedCharacters(character.getCharacterId());
            _characters.add(character);

        }
    }

    private boolean isPartOfLinkedSet(Set<Integer> linkedCharacters, Character character) {
        int characterNumber = character.getCharacterId();
        return ((linkedCharacters != null) && linkedCharacters.contains(characterNumber));
    }

    @Override
    public void afterAttribute(Attribute attribute) {
    }

    @Override
    public void afterLastItem() {
        _typeSetter.afterLastItem();
    }

    @Override
    public void attributeComment(String comment) {
    }

    @Override
    public void attributeValues(Values values) {
    }

    protected void printItemHeading(Item item) {

        String heading = _context.getItemHeading(item.getItemNumber());
        if (heading == null) {
            return;
        }
        _typeSetter.beforeItemHeading();

        writeSentence(heading);

        _typeSetter.afterItemHeading();
    }

    private void writeSentence(String heading) {
        _printer.writeJustifiedText(heading, -1);
    }

    private void printTaxonName(Item item) {

        Integer characterForTaxonNames = _context.getCharacterForTaxonNames();

        _typeSetter.beforeItemName();

        if (characterForTaxonNames != null) {
            writeCharacterForTaxonName(item, characterForTaxonNames, 0);
        } else {
            writeName(item, 1, 0);
        }

        // writeNameToIndexFile();

        _typeSetter.afterItemName();

    }

    private void writeCharacterForTaxonName(Item item, int characterNumber, int completionAction) {

        if (item.isVariant()) {
            // next character is a capital
            _printer.capitaliseNextWord();
            _printer.writeJustifiedText(Words.word(Word.VARIANT), 0);
        }

        Attribute attribute = _dataSet.getAttribute(item.getItemNumber(), characterNumber);
       
        String itemDescription = attribute.getValueAsString();
        _printer.writeJustifiedText(itemDescription, 0);

        complete(completionAction, itemDescription);

    }

    /**
     * 
     * @param item
     *            the item to write the name of
     * @param commentAction
     *            0 = omit comments, 1 = output comments with angle brackets, 2
     *            - output comments without angle brackets
     * @param typeSettingMarkNum
     * @param completionAction
     */
    private void writeName(Item item, int commentAction, int completionAction) {

        String description = _itemFormatter.formatItemDescription(item);

        if (item.isVariant()) {
            // next character is a capital
            _printer.capitaliseNextWord();
        }

        _printer.writeJustifiedText(description, -1);

        complete(completionAction, description);
    }

    private void complete(int completionAction, String description) {
        if (completionAction > 0) {
            _printer.writeJustifiedText("", completionAction);
            if (StringUtils.isEmpty(description)) {
                _printer.writeBlankLines(1, 0);
            }
        }
    }

    /**
     * Writes the attributes of an Item corresponding to the supplied list of
     * Characters. If the list has more than one Character it is implied the
     * Characters are linked.
     * 
     * @param item
     *            the item to write attributes of.
     * @param characters
     *            the characters to write.
     */
    private void writeAttributes(Item item, List<Character> characters) {

    	Character first = characters.get(0);
	    if (_context.startNewParagraphAtCharacter(first.getCharacterId())) {
         	_newParagraph = true;
        }
        for (Character character : characters) {
            
            boolean subsequentPartOfLinkedSet = (characters.size() > 1) && (character != characters.get(0));

            String description = _characterFormatter.formatCharacterDescription(character);
            String firstDescription = _characterFormatter.formatCharacterDescription(characters.get(0));
            if (subsequentPartOfLinkedSet) {
                description = removeCommonPrefix(firstDescription, description);
            }
            writeFeature(character, item, description, true, subsequentPartOfLinkedSet);
            writeCharacterAttribute(item, character);
        }
    }

    private String removeCommonPrefix(String master, String text) {
        if (StringUtils.isEmpty(master) || StringUtils.isEmpty(text)) {
            return text;
        }

        int minLength = Math.min(master.length(), text.length());

        int i = 0;
        int spaceIndex = 0;
        while (i < minLength && master.charAt(i) == text.charAt(i)) {
        	if (master.charAt(i) == ' ') {
        		spaceIndex = i;
        	}
            i++;
        }
        // Don't match partial words.
        if (i != spaceIndex + 1) {
        	i = spaceIndex;
        }
        return text.substring(i);
    }

    private void writeCharacterAttribute(Item item, Character character) {

        Attribute attribute = item.getAttribute(character);
        // Unknown attributes get through the filter if there is an item
        // subheading for the character.
        if (attribute.isUnknown()) {
        	return;
        }
        AttributeParser parser = new AttributeParser();
        AttributeTranslator translator = _attributeTranslatorFactory.translatorFor(character);

        String value = attribute.getValueAsString();

        if (attribute instanceof MultiStateAttribute) {
            MultiStateAttribute msAttr = (MultiStateAttribute) attribute;
            if (msAttr.isImplicit()) {
                value = Integer.toString(msAttr.getImplicitValue());
            }
        }

        String formattedAttribute = translator.translate(parser.parse(value));
        
        _typeSetter.beforeAttribute(attribute);
        _printer.writeJustifiedText(formattedAttribute, -1);
        _typeSetter.afterAttribute(attribute);
        _characterOutputSinceLastPuntuation = true;

    }

    private boolean _newParagraph;
    private int _lastCharacterOutput;
    private int _previousCharInSentence;
    private boolean _characterOutputSinceLastPuntuation;

    private void writeFeature(Character character, Item item, String description, boolean omitFinalPeriod,
            boolean subsequentPartOfLinkedSet) {

    	int characterNumber = character.getCharacterId();
        // Insert a full stop if required.
        if (_newParagraph == true || (_previousCharInSentence == 0) || (!subsequentPartOfLinkedSet)) {

            if ((_previousCharInSentence != 0) && (!_context.getOmitPeriodForCharacter(_lastCharacterOutput))) {
                _printer.insertPunctuationMark(Word.FULL_STOP);
            }
            _previousCharInSentence = 0;
            _characterOutputSinceLastPuntuation = false;
        } else {
            // do we need to insert a ; or ,?
            Word punctuationMark = Word.SEMICOLON;
            if (_context.replaceSemiColonWithComma(characterNumber) && _context.replaceSemiColonWithComma(_lastCharacterOutput)) {
                punctuationMark = Word.COMMA;
                if (_context.useAlternateComma()) {
                    punctuationMark = Word.ALTERNATE_COMMA;
                }
            }
            if (_characterOutputSinceLastPuntuation) {
                writePunctuation(punctuationMark);
            }
        }

        if (_newParagraph == true) {
            _typeSetter.newParagraph();
            _typeSetter.beforeNewParagraphCharacter();
            _newParagraph = false;
        }

        writeItemSubheading(character);
        if (!_context.omitCharacterNumbers()) {
            _printer.writeJustifiedText("(" + characterNumber + ")", -1);
        }

        writeCharacterDescription(character, item, description, subsequentPartOfLinkedSet);

        _previousCharInSentence = characterNumber;

    }

	protected void writeCharacterDescription(Character character, Item item, String description,
			boolean subsequentPartOfLinkedSet) {
		// The character description is commonly empty when writing linked
		// characters.
		
		// Check if we are starting a new sentence or starting a new set of
        // linked characters.
        if ((_previousCharInSentence == 0) || (!subsequentPartOfLinkedSet && _lastCharacterOutput < _previousCharInSentence)) {
            _printer.capitaliseNextWord();
        }
		if (StringUtils.isNotEmpty(description)) {
			_typeSetter.beforeCharacterDescription(character, item);
	
	        writeSentence(description, -1);
	
	        _typeSetter.afterCharacterDescription(character, item);
		}
	}

	protected void writeItemSubheading(Character character) {
		String itemSubheading = _context.getItemSubheading(character.getCharacterId());
		itemSubheading = _characterFormatter.defaultFormat(itemSubheading);
        
		if (StringUtils.isNotEmpty(itemSubheading)) {
            _printer.insertTypeSettingMarks(32);
            writeSentence(itemSubheading, 0);

            _printer.insertTypeSettingMarks(33);
        }
	}

    protected void writeSentence(String sentence, int completionAction) {
       
        _printer.writeJustifiedText(sentence, completionAction);

    }

    private void writePunctuation(Word punctuationMark) {
        _printer.insertPunctuationMark(punctuationMark);
        _characterOutputSinceLastPuntuation = false;
    }

}
