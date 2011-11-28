/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.translation.naturallanguage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractIterativeTranslator;
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
public class NaturalLanguageTranslator extends AbstractIterativeTranslator {

	protected DeltaContext _context;
    protected PrintFile _printer;
    protected MutableDeltaDataSet _dataSet;
    protected ItemListTypeSetter _typeSetter;
    protected ItemFormatter _itemFormatter;
    private CharacterFormatter _characterFormatter;
    private AttributeFormatter _attributeFormatter;
    private AttributeTranslatorFactory _attributeTranslatorFactory;
    private AttributeParser _attributeParser;
    
    /** Tracks the item subheadings that have been output */
    protected int _lastItemSubheadingWritten;
    /** Tracks the last character checked for a start of paragraph marker */
    protected int _lastNewParagraphCharacterChecked;
    
    public NaturalLanguageTranslator(
    		DeltaContext context, 
    		ItemListTypeSetter typeSetter, 
    		PrintFile printer, 
    		ItemFormatter itemFormatter, 
    		CharacterFormatter characterFormatter,
            AttributeFormatter attributeFormatter) {
        
        _context = context;
        _printer = printer;
        _dataSet = _context.getDataSet();
        _typeSetter = typeSetter;
        _itemFormatter = itemFormatter;
        _characterFormatter = characterFormatter;
        _attributeFormatter = attributeFormatter;
        _attributeTranslatorFactory = new AttributeTranslatorFactory(
        		context, _characterFormatter, _attributeFormatter, _typeSetter);
        _lastItemSubheadingWritten = 0;
        _lastNewParagraphCharacterChecked = 0;
        _attributeParser = new AttributeParser();
        
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
        _lastItemSubheadingWritten = 0;
        _lastNewParagraphCharacterChecked = 0;
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
    
    /**
     * Returns most recently encountered item subheading that has not 
     * with character number above a supplied character.
     * @param charNumber the number of the character being processed.
     * @param toChar the character to stop the check at.
     */
    private String getItemSubHeading(int charNumber, int toChar) {
    	String itemSubHeading = "";
    	while (charNumber > toChar && StringUtils.isBlank(itemSubHeading)) {
    		itemSubHeading = _context.getItemSubheading(charNumber);
    		charNumber--;
    	}
    	
    	return itemSubHeading;
    }

    /**
     * Checks if the supplied character is a part of a set of linked characters.
     * The "NEW PARAGRAPH AT CHARACTERS" directive will override the 
     * inclusion of the character in a linked set.
     * @param linkedCharacters the set of linked characters being processed.
     * @param character the character to check.
     * @return true if the character is a part of the supplied linked set.
     */
    private boolean isPartOfLinkedSet(Set<Integer> linkedCharacters, Character character) {
        int characterNumber = character.getCharacterId();
        if (_context.getNewParagraphCharacters().contains(characterNumber) ||
        	(StringUtils.isNotBlank(_context.getItemSubheading(characterNumber)))) {
        	return false;
        }
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
	    checkForNewParagraph(first.getCharacterId());
	    
	    Set<Character> featuresToEmphasize = getEmphasizedFeatures(item, characters);
	    
	    boolean characterInSetOutput = false;
        for (Character character : characters) {
            
            Attribute attribute = item.getAttribute(character);
            String translatedAttribute = translateAttribute(attribute);
            
            // It's possible that attributes, even if present, will not produce
            // any translated output (e.g. if they are just comments and
            // comments are ommitted).
            if (StringUtils.isNotBlank(translatedAttribute)) {
            	
            	String itemSubheading = getItemSubHeading(character.getCharacterId(), _lastItemSubheadingWritten);
        		
            	// Even if we are are part of a linked set of characters, an
            	// item subheading can break up the text. 
            	boolean subsequentPartOfLinkedSet = characterInSetOutput && StringUtils.isBlank(itemSubheading);

            	insertPunctuation(subsequentPartOfLinkedSet, character.getCharacterId());
	            
            	String description = _characterFormatter.formatCharacterDescription(character);
                String firstDescription = _characterFormatter.formatCharacterDescription(characters.get(0));
                if (subsequentPartOfLinkedSet) {
                    description = removeCommonPrefix(firstDescription, description);
                }
                boolean emphasize = featuresToEmphasize.contains(character);
            	writeFeature(character, item, description, emphasize, subsequentPartOfLinkedSet, itemSubheading);
	            writeCharacterAttribute(attribute, translatedAttribute);
	            
	            characterInSetOutput = true;
            }
        }
    }
    
    /**
     * Returns the characters in the supplied list that should be emphasized
     * due to an EMPHASIZE CHARACTERS directive.  A character can be emphasized
     * if a subsequent character in the set is emphasized but is missing a 
     * feature description due to the behaviour of the LINK CHARACTERS 
     * directive. (ommiting common feature descriptions in a sentence composed
     * of LINKed CHARACTERS).
     * @param item the item to be output.
     * @param characters the characters in the sentence.
     * @return a Set containing the Characters that require emphasis.
     */
    private Set<Character> getEmphasizedFeatures(Item item, List<Character> characters) {
    	
    	Character firstChar = null;
    	Set<Character> emphasizedChars = new HashSet<Character>();
    	int itemNum = item.getItemNumber();
    	int lastSubHeading = _lastItemSubheadingWritten;
    	boolean firstOfSet = true;
    	for (Character character : characters) {
            
            Attribute attribute = item.getAttribute(character);
            String translatedAttribute = translateAttribute(attribute);

            // It's possible that attributes, even if present, will not produce
            // any translated output (e.g. if they are just comments and
            // comments are ommitted).
            if (StringUtils.isNotBlank(translatedAttribute)) {
            	
            	String itemSubheading = getItemSubHeading(character.getCharacterId(), lastSubHeading);
            	if (StringUtils.isNotBlank(itemSubheading)) {
            		lastSubHeading = character.getCharacterId();
            		firstOfSet = true;
            	}
        		
                if (!firstOfSet) {
                	String description = _characterFormatter.formatCharacterDescription(character);
                    String firstDescription = _characterFormatter.formatCharacterDescription(characters.get(0));
                    
                    description = removeCommonPrefix(firstDescription, description);
                    if (StringUtils.isEmpty(description)) {
                    	if (_context.isCharacterEmphasized(itemNum, character.getCharacterId())) {
                    		emphasizedChars.add(firstChar);
                    	}
                    }
                }
                else {
                	firstChar = character;
                	if (_context.isCharacterEmphasized(itemNum, character.getCharacterId())) {
                		emphasizedChars.add(character);
                	}
                	firstOfSet = false;
                }
            }
        }
        return emphasizedChars;
	}


	/**
     * Goes through the characters since the last attribute output and checks
     * if any of them were marked by the NEW PARAGRAPH AT CHARACTERS 
     * directive. (This is necessary as a flagged character may have been
     * excluded by the filter because it was unknown or inapplicable).
     * @param charNumber the current character being processed.
     */
    private void checkForNewParagraph(int charNumber) {
    	for (int i=_lastNewParagraphCharacterChecked+1; i<=charNumber; i++) {
	    	 if (_context.startNewParagraphAtCharacter(i)) {
	          	_newParagraph = true;
	         }
    	}
    	 _lastNewParagraphCharacterChecked = charNumber;
    }
    

    private String removeCommonPrefix(String master, String text) {
       
    	if (StringUtils.isEmpty(master) || StringUtils.isEmpty(text)) {
            return text;
        }
        String[] masterWords = master.split("\\s+");
        String[] words = text.split("\\s+");
        
        int minWords = Math.min(masterWords.length, words.length);
        int matchingWord = -1;
        for (int i=0; i<minWords; i++) {
        	if (!masterWords[i].equals(words[i])) {
        		break;
        	}
        	matchingWord = i;
        }
        
        if (matchingWord == words.length-1) {
        	return "";
        }
        else {
        	return text.substring(text.indexOf(words[matchingWord+1]));
        }
        
    }

    private void writeCharacterAttribute(Attribute attribute, String naturalLanguageDescription) {

        _typeSetter.beforeAttribute(attribute);
        _printer.writeJustifiedText(naturalLanguageDescription, -1);
        _typeSetter.afterAttribute(attribute);
        _characterOutputSinceLastPuntuation = true;
        _lastCharacterOutput = attribute.getCharacter().getCharacterId();

    }

    /**
     * Translates an attribute into natural language and returns it as a String.
     * @param attribute the attribute to translate.
     * @return a natural language description of the attribute.
     */
	protected String translateAttribute(Attribute attribute) {
		AttributeTranslator translator = _attributeTranslatorFactory.translatorFor(attribute.getCharacter());

        String value = attribute.getValueAsString();

        if (attribute instanceof MultiStateAttribute) {
            MultiStateAttribute msAttr = (MultiStateAttribute) attribute;
            if (msAttr.isImplicit()) {
                value = Integer.toString(msAttr.getImplicitValue());
            }
        }

        String formattedAttribute = translator.translate(_attributeParser.parse(value));
		return formattedAttribute;
	}

    private boolean _newParagraph;
    private int _lastCharacterOutput;
    private int _previousCharInSentence;
    private boolean _characterOutputSinceLastPuntuation;

    private void writeFeature(Character character, Item item, String description,
            boolean emphasize, boolean subsequentPartOfLinkedSet, String itemSubHeading) {

        writeItemSubheading(character, itemSubHeading);
       
        int characterNumber = character.getCharacterId();
        if (!_context.omitCharacterNumbers()) {
            _printer.writeJustifiedText("(" + characterNumber + ")", -1);
        }

        writeCharacterDescription(character, item, description, emphasize, subsequentPartOfLinkedSet);

        _previousCharInSentence = characterNumber;

    }


	protected void insertPunctuation(boolean subsequentPartOfLinkedSet, int characterNumber) {
		
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
           
            Set<Integer> useComma = _context.getReplaceSemiColonWithComma(characterNumber);
           
            if (useComma.contains(characterNumber) && useComma.contains(_lastCharacterOutput)) {
                punctuationMark = Word.COMMA;
                if (_context.getUseAlternateComma(characterNumber)) {
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
	}

	protected void writeCharacterDescription(
			Character character, Item item, String description, 
			 boolean emphasize, boolean subsequentPartOfLinkedSet) {
		// The character description is commonly empty when writing linked
		// characters.
		
		// Check if we are starting a new sentence or starting a new set of
        // linked characters.
        if ((_previousCharInSentence == 0) || (!subsequentPartOfLinkedSet && _lastCharacterOutput < _previousCharInSentence)) {
            _printer.capitaliseNextWord();
        }
        
		if (StringUtils.isNotEmpty(description)) {
			_typeSetter.beforeCharacterDescription(character, item);
			if (emphasize) {
				_typeSetter.beforeEmphasizedCharacter();
			}
	        writeSentence(description, -1);
	        if (emphasize) {
	        	_typeSetter.afterEmphasizedCharacter();
	        }
	        _typeSetter.afterCharacterDescription(character, item);
		}
		else {
			// This is a bit silly but CONFOR does it so it makes my testing
			// slightly easier....
			//_typeSetter.beforeEmphasizedCharacter();
			//_typeSetter.afterEmphasizedCharacter();
		}
	}

	protected void writeItemSubheading(Character character, String itemSubheading) {
		
		itemSubheading = _characterFormatter.defaultFormat(itemSubheading);
        
		if (StringUtils.isNotEmpty(itemSubheading)) {
			_lastItemSubheadingWritten = character.getCharacterId();
			
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
