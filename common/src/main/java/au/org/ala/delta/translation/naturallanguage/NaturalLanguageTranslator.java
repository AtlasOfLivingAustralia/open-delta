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
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractDataSetTranslator;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;
import au.org.ala.delta.translation.attribute.AttributeParser;
import au.org.ala.delta.translation.attribute.AttributeTranslator;
import au.org.ala.delta.translation.attribute.MultiStateAttributeTranslator;
import au.org.ala.delta.translation.attribute.NumericAttributeTranslator;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;
import au.org.ala.delta.translation.attribute.TextAttributeTranslator;

/**
 * The NaturalLanguageTranslator is responsible for turning a DELTA data set
 * into formatted natural language.
 */
public class NaturalLanguageTranslator extends AbstractDataSetTranslator {

    private DeltaContext _context;
    private PrintFile _printer;
    private DeltaDataSet _dataSet;
    private ItemListTypeSetter _typeSetter;
    private ItemFormatter _itemFormatter;
    private CharacterFormatter _characterFormatter;
    private AttributeFormatter _attributeFormatter;

    public NaturalLanguageTranslator(DeltaContext context, ItemListTypeSetter typeSetter, PrintFile printer, ItemFormatter itemFormatter, CharacterFormatter characterFormatter,
            AttributeFormatter attributeFormatter) {
        super(context, new NaturalLanguageDataSetFilter(context));
        _context = context;
        _printer = printer;
        _dataSet = _context.getDataSet();
        _typeSetter = typeSetter;
        _itemFormatter = itemFormatter;
        _characterFormatter = characterFormatter;
        _attributeFormatter = attributeFormatter;
    }

    @Override
    public void beforeFirstItem() {
        _typeSetter.beforeFirstItem();

        // Insert the implicit attributes section if required.

    }

    @Override
    public void beforeItem(Item item) {

        _typeSetter.beforeItem(item);

        printItemHeading(item);

        printTaxonName(item);

        _newParagraph = true;
        // if html output printIndexHeading(item) - better in a whole other
        // Something.

    }

    @Override
    public void afterItem(Item item) {

        if (!_characters.isEmpty()) {
            writeAttributes(item, _characters);
            _characters = new ArrayList<Character>();
        }
        if (_characterOutputSinceLastPuntuation) {
            writePunctuation(Word.FULL_STOP);
        }
        _lastCharacterOutput = 0;
        _previousCharInSentence = 0;
        _typeSetter.afterItem(item);
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
        // TODO the CONFOR code (TNAT) strips comments. Not sure how nested
        // comments are treated
        // as I don't yet understand how item descriptions are broken into the
        // subgroups.
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

        for (Character character : characters) {
            int characterNumber = character.getCharacterId();

            boolean subsequentPartOfLinkedSet = (characters.size() > 1) && (character != characters.get(0));

            String description = _characterFormatter.formatCharacterDescription(character);
            if (subsequentPartOfLinkedSet) {
                description = removeCommonPrefix(characters.get(0).getDescription(), description);
            }
            writeFeature(description, true, item.getItemNumber(), characterNumber, _context.getItemSubheading(characterNumber), false, false, new int[_context.getNumberOfCharacters()],
                    subsequentPartOfLinkedSet);
            writeCharacterAttribute(item, character);
        }
    }

    private String removeCommonPrefix(String master, String text) {
        if (StringUtils.isEmpty(master) || StringUtils.isEmpty(text)) {
            return text;
        }

        int minLength = Math.min(master.length(), text.length());

        int i = 0;
        while (i < minLength && master.charAt(i) == text.charAt(i)) {
            i++;
        }
        return text.substring(i);
    }

    private void writeCharacterAttribute(Item item, Character character) {

        Attribute attribute = item.getAttribute(character);
        AttributeParser parser = new AttributeParser();
        AttributeTranslator translator = translatorFor(character);

        String value = attribute.getValueAsString();

        if (attribute instanceof MultiStateAttribute) {
            MultiStateAttribute msAttr = (MultiStateAttribute) attribute;
            if (msAttr.isImplicit()) {
                value = Integer.toString(msAttr.getImplicitValue());
            }
        }

        String formattedAttribute = translator.translate(parser.parse(value));
        _printer.writeJustifiedText(formattedAttribute, -1);
        _characterOutputSinceLastPuntuation = true;

    }

    private AttributeTranslator translatorFor(Character character) {
        if (character instanceof MultiStateCharacter) {
            return new MultiStateAttributeTranslator((MultiStateCharacter) character, _characterFormatter, _attributeFormatter);
        }
        if (character instanceof NumericCharacter<?>) {
            return new NumericAttributeTranslator((NumericCharacter<?>) character, _typeSetter, _attributeFormatter, _context.getOmitSpaceBeforeUnits());
        }

        return new TextAttributeTranslator(_attributeFormatter);
    }

    private boolean _newParagraph;
    private int _lastCharacterOutput;
    private int _previousCharInSentence;
    private boolean _characterOutputSinceLastPuntuation;
    private boolean _textOutputSinceLastParagraph;

    private void writeFeature(String description, boolean omitFinalPeriod, int itemNumber, int characterNumber, String subHeading, boolean emphasizeFeature, boolean emphasizeCharacter, int[] offsets,
            boolean subsequentPartOfLinkedSet) {

        // Insert a full stop if required.
        if (_newParagraph == true || StringUtils.isNotEmpty(subHeading) || (_previousCharInSentence == 0) || (!subsequentPartOfLinkedSet)) {

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
            _textOutputSinceLastParagraph = false;
        }

        if (StringUtils.isNotEmpty(subHeading)) {
            _printer.insertTypeSettingMarks(32);
            writeSentence(subHeading, 0, 0);

            _printer.insertTypeSettingMarks(33);
        }
        if (!_context.omitCharacterNumbers()) {
            _printer.writeJustifiedText("(" + characterNumber + ")", -1);
        }

        int ioffset = 0;
        // Check if we are starting a new sentence or starting a new set of
        // linked characters.
        if ((_previousCharInSentence == 0) || (!subsequentPartOfLinkedSet && _lastCharacterOutput < _previousCharInSentence)) {
            ioffset = 0;
            _printer.capitaliseNextWord();
        }

        int completionAction = -1;
        boolean emphasisApplied = false;
        if (emphasizeCharacter) {
            if ((ioffset == 0) || _context.isCharacterEmphasized(itemNumber, characterNumber)) {
                _printer.insertTypeSettingMarks(19);
                emphasisApplied = true;
                completionAction = -1;
                if (ioffset == 0 && (offsets[characterNumber] > 0)) {
                    ioffset = -offsets[characterNumber];
                }
            }
        }

        writeSentence(description, 0, completionAction);

        if (emphasizeCharacter) {
            if (emphasisApplied) {
                _printer.insertTypeSettingMarks(20);
            }
            if (emphasizeFeature) {
                _printer.insertTypeSettingMarks(18);
            }
        }

        _previousCharInSentence = characterNumber;

    }

    private void writeSentence(String sentence, int commentAction, int completionAction) {
        // TODO This does a bunch of stuff including inserting typesetting marks
        // in the middle
        // of the sentence.
        _printer.writeJustifiedText(sentence, completionAction);

    }

    private void writePunctuation(Word punctuationMark) {
        _printer.insertPunctuationMark(punctuationMark);
        _characterOutputSinceLastPuntuation = false;
    }

}
