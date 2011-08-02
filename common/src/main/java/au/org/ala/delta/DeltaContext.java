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
package au.org.ala.delta;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.StateValueMatrix;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.CharacterNoteMarks;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.model.image.ImageInfo;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.translation.delta.DeltaFormatDataSetFilter;
import au.org.ala.delta.util.Functor;
import au.org.ala.delta.util.IdentificationKeyCharacterIterator;
import au.org.ala.delta.util.Utils;

/**
 * Context associated with a set of DELTA input files.
 */
public class DeltaContext extends AbstractDeltaContext {

	public static enum HeadingType {HEADING, REGISTRATION_SUBHEADING};
	
	private DeltaDataSet _dataSet;
	
	private Map<String, Object> _variables;
	private int _ListFilenameSize = 15;
	private List<String> _errorMessages = new ArrayList<String>();

	private TranslateType _translateType;
	private Set<Integer> _excludedCharacters = new HashSet<Integer>();
	private Set<Integer> _excludedItems = new HashSet<Integer>();
	private Set<Integer> _omitPeriodForCharacters = new HashSet<Integer>();	
	private Set<Integer> _replaceSemiColonWithComma = new HashSet<Integer>();
	private Set<Integer> _omitOrForCharacters = new HashSet<Integer>();
	private Set<Integer> _newParagraphCharacters = new HashSet<Integer>();
	private Set<Integer> _charactersForSynonymy = new HashSet<Integer>(); 
	private Set<Integer> _useControllingCharactersFirst = new HashSet<Integer>(); 
	
	private Map<Integer, String> _itemHeadings = new HashMap<Integer, String>();
	private Map<Integer, String> _itemSubHeadings = new HashMap<Integer, String>();
	private Map<Integer, String> _indexHeadings = new HashMap<Integer, String>();
	private Map<Integer, TypeSettingMark> _typeSettingMarks = new HashMap<Integer, TypeSettingMark>();
	private Map<Integer, TypeSettingMark> _formattingMarks = new HashMap<Integer, TypeSettingMark>();
	
	private Set<Set<Integer>> _linkedCharacters = new HashSet<Set<Integer>>();
	private Map<Integer,Set<Integer>> _emphasizedCharacters = new HashMap<Integer, Set<Integer>>();
	private Map<String,Set<Integer>> _emphasizedCharactersByDescription = new HashMap<String, Set<Integer>>();
	
	private Map<Integer,Set<Integer>> _addedCharacters = new HashMap<Integer, Set<Integer>>();
	private Map<String,Set<Integer>> _addedCharactersByDescription = new HashMap<String, Set<Integer>>();
	
	private Set<Integer> _emphasizedFeatures = new HashSet<Integer>();
	private Integer _characterForTaxonNames = null;
	private Map<Integer, List<ImageInfo>> _images = new HashMap<Integer, List<ImageInfo>>();
	private Map<Integer, IdentificationKeyCharacter> _keyCharacters = new HashMap<Integer, IdentificationKeyCharacter>();
	
	private int _numberOfCharacters;
	private int _maxNumberOfStates;
	private int _maxNumberOfItems;
	private boolean _omitTypeSettingMarks = false;
	private int _printWidth = 80;
	private boolean _replaceAngleBrackets;
	private boolean _omitCharacterNumbers = false;
	private boolean _omitInnerComments = false;
	private boolean _omitInapplicables = false;
	private Boolean _omitRedundantVariantAttributes = null;
	private boolean _useAlternateComma;
	private boolean _insertImplicitValues = false;
	private boolean _outputHtml = false;
	private boolean _enableDeltaOutput = true;
	private boolean _chineseFormat = false;
	
	private Map<HeadingType, String> _headings = new HashMap<HeadingType, String>();
	private Integer _characterForTaxonImages = null;

	private String _credits;

	private PrintStream _listStream;
	private PrintStream _errorStream;
	private PrintStream _printStream;

	private StateValueMatrix _matrix;

	private double[] _characterWeights;
	
	private DirectiveParserObserver _observer;
	
	public DeltaContext() {
		this(new DefaultDataSetFactory().createDataSet(""));
	}
	
	public DeltaContext(DeltaDataSet dataSet) {
		_variables = new HashMap<String, Object>();

		_variables.put("DATEFORMAT", "dd-MMM-yyyy");
		_variables.put("TIMEFORMAT", "HH:mm");

		_variables.put("DATE", new Functor() {
			@Override
			public Object invoke(DeltaContext context) {
				String dateFormat = (String) context.getVariable("DATEFORMAT", "dd-MMM-yyyy");
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				return sdf.format(new Date());
			}
		});

		_variables.put("TIME", new Functor() {

			@Override
			public Object invoke(DeltaContext context) {
				String timeFormat = (String) context.getVariable("TIMEFORMAT", "HH:mm");
				SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
				return sdf.format(new Date());
			}
		});

		_printStream = System.out;
		_errorStream = System.err;
		_listStream = System.out;
		
		_dataSet = dataSet;
	}
	
	
	public DeltaDataSet getDataSet() {
		return _dataSet;
	}

	public void setVariable(String name, Object value) {
		_variables.put(name.toUpperCase(), value);
	}

	public Object getVariable(String name, Object defvalue) {
		String key = name.toUpperCase();
		if (_variables.containsKey(key)) {
			Object val = _variables.get(key);
			if (val instanceof Functor) {
				return ((Functor) val).invoke(this);
			} else {
				return val;
			}
		} else {
			return defvalue;
		}
	}

	public void initializeMatrix() {
		assert getNumberOfCharacters() > 0;
		assert getMaximumNumberOfItems() > 0;
		_matrix = new StateValueMatrix(getNumberOfCharacters(), getMaximumNumberOfItems());
	}

	public StateValueMatrix getMatrix() {
		return _matrix;
	}

	public void setPrintStream(PrintStream stream) {
		_printStream = stream;
	}
	
	public PrintStream getPrintStream() {
		return _printStream;
	}

	public void setErrorStream(PrintStream stream) {
		_errorStream = stream;
	}

	public void setListingStream(PrintStream stream) {
		_listStream = stream;
	}

	public void ListMessage(String line) {

		if (_listStream != null) {
			String prefix = "";
			if (_listStream == System.out) {
				prefix = "LIST:";
			}

			ParsingContext pc = getCurrentParsingContext();
			String filename = Utils.truncate(String.format("%s,%d", pc.getFile().getAbsolutePath(), pc.getCurrentLine()), _ListFilenameSize);
			OutputMessage(_listStream, "%s%s %s", prefix, filename, line);
		}
	}

	public void ErrorMessage(String format, Object... args) {
		String message = String.format(format, args);
		_errorMessages.add(message);
		OutputMessage(_errorStream, format, args);
	}

	public List<String> getErrorMessages() {
		return _errorMessages;
	}

	private void OutputMessage(PrintStream stream, String format, Object... args) {
		if (stream != null) {
			if (args == null || args.length == 0) {
				stream.println(format);
			} else {
				String message = String.format(format, args);
				stream.println(message);
			}
		}
	}

	public void setCredits(String credits) {
		_credits = credits;
	}

	public String getCredits() {
		return _credits;
	}

	public void setNumberOfCharacters(int characters) {
		_numberOfCharacters = characters;
	}

	public int getNumberOfCharacters() {
		return _numberOfCharacters;
	}

	public void setMaximumNumberOfStates(int maxStates) {
		_maxNumberOfStates = maxStates;
	}

	public int getMaximumNumberOfStates() {
		return _maxNumberOfStates;
	}

	public void setMaximumNumberOfItems(int items) {
		_maxNumberOfItems = items;
	}

	public int getMaximumNumberOfItems() {
		return _maxNumberOfItems;
	}

	public Character getCharacter(int number) {
		if (number <= _numberOfCharacters) {
			Character c = _dataSet.getCharacter(number);
			if (c==null) {
				c = _dataSet.addCharacter(number, CharacterType.UnorderedMultiState);
				((UnorderedMultiStateCharacter)c).setNumberOfStates(2);
			}
			return c;
		}
		throw new RuntimeException("No such character number " + number);
	}

	public Item getItem(int index) {
		return _dataSet.getItem(index);
	}

	public TranslateType getTranslateType() {
		return _translateType;
	}

	public void setTranslateType(TranslateType t) {
		_translateType = t;
	}

	public void setOmitTypeSettingMarks(boolean b) {
		_omitTypeSettingMarks = b;
	}

	public boolean isOmitTypeSettingMarks() {
		return _omitTypeSettingMarks;
	}

	public int getPrintWidth() {
		return _printWidth;
	}

	public void setPrintWidth(int width) {
		_printWidth = width;
	}

	public void setReplaceAngleBrackets(boolean b) {
		_replaceAngleBrackets = b;
	}

	public boolean isReplaceAngleBrackets() {
		return _replaceAngleBrackets;
	}

	public void setOmitCharacterNumbers(boolean b) {
		_omitCharacterNumbers = b;
	}

	public void setOmitInnerComments(boolean b) {
		_omitInnerComments = b;
	}

	public boolean isOmitInnerComments() {
		return _omitInnerComments;
	}

	public void setOmitInapplicables(boolean b) {
		_omitInapplicables = b;
	}

	public boolean isOmitInapplicables() {
		return _omitInapplicables;
	}

	public void setCharacterForTaxonImages(Integer charIdx) {
		_characterForTaxonImages = charIdx;
	}

	public Integer getCharacterForTaxonImages() {
		return _characterForTaxonImages;
	}

	public void excludeCharacter(int charIndex) {
		_excludedCharacters.add(charIndex);
	}
	
	public boolean isCharacterExcluded(int charNumber) {
		return _excludedCharacters.contains(charNumber);
	}

	public Set<Integer> getExcludedCharacters() {
		return _excludedCharacters;
	}

	public void newParagraphAtCharacter(int charIndex) {
		_newParagraphCharacters.add(charIndex);
	}

	public Set<Integer> getNewParagraphCharacters() {
		return _newParagraphCharacters;
	}
	
	public boolean startNewParagraphAtCharacter(int charNumber) {
		return _newParagraphCharacters.contains(charNumber);
	}

	public void setCharacterWeight(int number, double weight) {
		if (_characterWeights == null) {
			_characterWeights = new double[getNumberOfCharacters()];
		}
		_characterWeights[number-1] = weight;
		
	}
	
	public void setCharacterReliability(int number, double reliability) {
		if (_characterWeights == null) {
			_characterWeights = new double[getNumberOfCharacters()];
		}
		_characterWeights[number-1] = Math.pow(2, reliability-5);
	}
	
	public double getCharacterWeight(int number) {
		if (_characterWeights != null) {
			return _characterWeights[number-1];
		}
		return 0;
	}

	
	public double getCharacterReliability(int number) {
		if (_characterWeights == null) {
			return 0;
		}
		return Math.log(_characterWeights[number-1])/Math.log(2) +5;
	}
	
	/**
	 * Returns true if the specified item has been marked as excluded by an EXCLUDED ITEMS directive
	 * @param itemNumber the item number to check.
	 * @return whether the specified item is excluded.
	 */
	public boolean isExcluded(int itemNumber) {
		return _excludedItems.contains(itemNumber);
	}

	/**
	 * Returns the heading for the supplied item as defined by the ITEM HEADINGS directive.  If no heading has
	 * been supplied, this method returns null.
	 * @param itemNumber the item to get the heading for.
	 * @return the heading defined for the specified item or null if no heading was defined.
	 */
	public String getItemHeading(int itemNumber) {
		
		return _itemHeadings.get(itemNumber);
	}

	
	public void itemSubheading(int characterNumber, String subheading) {
		_itemSubHeadings.put(characterNumber, subheading);
		
	}
	
	/**
	 * Returns the sub heading for the supplied character as defined by the ITEM SUBHEADINGS directive.  
	 * If no heading has been supplied, this method returns null.
	 * @param characterNumber the character number to get the heading for.
	 * @return the heading defined for the specified item or null if no heading was defined.
	 */
	public String getItemSubheading(int characterNumber) {
		return _itemSubHeadings.get(characterNumber);
	}
	
	
	/**
	 * Returns the index heading for the supplied item number as defined by the INDEX HEADINGS directive.  
	 * If no heading has been supplied, this method returns null.
	 * @param itemNumber the item to get the heading for.
	 * @return the index heading defined for the specified item or null if no heading was defined.
	 */
	public String getIndexHeading(int itemNumber) {
		return _indexHeadings.get(itemNumber);
	}
	
	public void linkCharacters(Set<Integer> linkedCharacters) {
		_linkedCharacters.add(linkedCharacters);
	}
	
	/**
	 * Returns the set of characters linked to the supplied character.
	 * @param characterNumber the character of interest.
	 * @return the set of characters linked to the supplied character number, or null if the 
	 * character is not linked to any other characters.  The returned
	 * set includes the supplied character.
	 */
	public Set<Integer> getLinkedCharacters(int characterNumber) {
		for (Set<Integer> linkedCharacters : _linkedCharacters) {
			if (linkedCharacters.contains(characterNumber)) {
				return linkedCharacters;
			}
		}
		return null;
	}

	public void setCharacterForTaxonNames(Integer characterNum) {
		_characterForTaxonNames = characterNum;
	}
	
	/**
	 * @return the character specified by the CHARACTER FOR TAXON NAMES directive.  The value of the item attribute
	 * for this character will be used as the item name.
	 */
	public Integer getCharacterForTaxonNames() {
		return _characterForTaxonNames;
	}

	/**
	 * Specifies whether attributes in a variant items are output in natural language translations.
	 * Controlled by the OMIT REDUNDANT VARIANT ATTRIBUTES and INCLUDE REDUNDANT VARIANT ATTRIBUTES
	 * directives. If neither directive is given, the default is to output attributes as coded.
	 * @param b true if redundant variant attributes should be omitted.
	 */
	public void setOmitRedundantVariantAttributes(Boolean b) {
		_omitRedundantVariantAttributes = b;
		
	}
	
	/**
	 * Specifies whether attributes in a variant items are output in natural language translations.
	 * Controlled by the OMIT REDUNDANT VARIANT ATTRIBUTES and INCLUDE REDUNDANT VARIANT ATTRIBUTES
	 * directives. If neither directive is given, the default is to output attributes as coded.
	 * @param b true if redundant variant attributes should be omitted.
	 */
	public Boolean getOmitRedundantVariantAttributes() {
		return _omitRedundantVariantAttributes;
	}

	public boolean getOmitPeriodForCharacter(int characterNum) {
		return _omitPeriodForCharacters.contains(characterNum);
	}

	public boolean replaceSemiColonWithComma(int characterNum) {
		return _replaceSemiColonWithComma.contains(characterNum);
	}
	public boolean useAlternateComma() {
		return _useAlternateComma;
	}
	
	public boolean isCharacterEmphasized(int itemNum, int characterNum) {
		boolean emphasized = entryExists(_emphasizedCharacters, itemNum, characterNum);
		if (!emphasized) {
			emphasized = itemDescriptionEntryExists(_emphasizedCharactersByDescription, itemNum, characterNum);
		}
		return emphasized;
	}
	
	public void emphasizeCharacters(int itemNum, Set<Integer> characters) {
		_emphasizedCharacters.put(itemNum, characters);
	}
	
	public void emphasizeCharacters(String itemDescription, Set<Integer> characters) {
		_emphasizedCharactersByDescription.put(RTFUtils.stripFormatting(itemDescription), characters);
	}
	
	public void addCharacters(int itemNum, Set<Integer> characters) {
		_addedCharacters.put(itemNum, characters);
	}
	
	public void addCharacters(String itemDescription, Set<Integer> characters) {
		_addedCharactersByDescription.put(RTFUtils.stripFormatting(itemDescription), characters);
	}
	
	public boolean isCharacterAdded(int itemNum, int characterNum) {
		boolean added = entryExists(_addedCharacters, itemNum, characterNum);
		if (!added) {
			itemDescriptionEntryExists(_addedCharactersByDescription, itemNum, characterNum);
		}
		return added;
	}
	
	private boolean itemDescriptionEntryExists(Map<String, Set<Integer>> map, int itemNum, int characterNum) {
		Item item = getDataSet().getItem(itemNum);
		String description = RTFUtils.stripFormatting(item.getDescription());
		Set<Integer> chars =  _addedCharactersByDescription.get(description);
		if (chars != null) {
			return chars.contains(characterNum);
		}
		return false;
	}
	
	private boolean entryExists(Map<Integer, Set<Integer>> map, int itemNum, int characterNum) {
		Set<Integer> characterSet = map.get(itemNum);
		if (characterSet != null) {
			return characterSet.contains(characterNum);
		}
		return false;
	}
	

	public boolean isFeatureEmphasized(int i) {
		return _emphasizedFeatures.contains(i);
	}

	public boolean setInsertImplicitValues(boolean insertImplicitValues) {
		return _insertImplicitValues = insertImplicitValues;
	}
	
	public boolean getInsertImplicitValues() {
		return _insertImplicitValues;
	}

	public boolean isOrOmmitedForCharacter(int i) {
		return _omitOrForCharacters.contains(i);
	}
	
	public void omitOrForCharacter(int charNumber) {
		_omitOrForCharacters.add(charNumber);
	}
	
	public boolean isUseControllingCharacterFirst(int i) {
		return _useControllingCharactersFirst.contains(i);
	}
	
	public void setUseControllingCharacterFirst(int charNumber, boolean useControllingCharFirst) {
		if (useControllingCharFirst) {
			_useControllingCharactersFirst.add(charNumber);
		}
	}


	public boolean omitCharacterNumbers() {
		return _omitCharacterNumbers;
	}

	public void setOutputHtml(boolean b) {
		_outputHtml = b;
	}
	
	public boolean getOutputHtml() {
		return _outputHtml;
	}
	
	public DirectiveParserObserver getDirectiveParserObserver() {
		return _observer;
	}
	
	public void setDirectiveParserObserver(DirectiveParserObserver observer) {
		_observer = observer;
	}

	public void addTypeSettingMark(TypeSettingMark mark) {
		_typeSettingMarks.put(mark.getId(), mark);
	}
	
	public Map<Integer, TypeSettingMark> getTypeSettingMarks() {
		return _typeSettingMarks;
	}
	
	public TypeSettingMark getTypeSettingMark(MarkPosition mark) {
		return _typeSettingMarks.get(mark.getId());
	}
	
	public void setImages(int imageType, List<ImageInfo> images) {
		_images.put(imageType, images);
	}
	
	public List<ImageInfo> getImages(int imageType) {
		List<ImageInfo> images = _images.get(imageType);
		if (images == null) {
			images = new ArrayList<ImageInfo>();
		}
		return images;
	}

	public void addFormattingMark(TypeSettingMark mark) {
		_formattingMarks.put(mark.getId(), mark);
	}
	
	public TypeSettingMark getFormattingMark(CharacterNoteMarks mark) {
		return _formattingMarks.get(mark.getId());
	}
	
	public void setHeading(HeadingType type, String heading) {
		_headings.put(type, heading);
	}
	
	public String getHeading(HeadingType type) {
		return _headings.get(type);
	}

	public void addIdentificationKeyCharacter(IdentificationKeyCharacter keyCharacter) {
		_keyCharacters.put(keyCharacter.getCharacterNumber(), keyCharacter);
		
	}
	
	public IdentificationKeyCharacter getIdentificationKeyCharacter(int characterNumber) {
		return _keyCharacters.get(characterNumber);
	}
	
	public void disableDeltaOutput() {
		_enableDeltaOutput = false;
	}
	
	public boolean isDeltaOutputDisabled() {
		return !_enableDeltaOutput;
	}
	
	public void setChineseFormat(boolean chineseFormat) {
		_chineseFormat = chineseFormat;
	}
	
	public boolean isChineseFormat() {
		return _chineseFormat;
	}
	
	public Iterator<IdentificationKeyCharacter> identificationKeyCharacterIterator() {
		return new IdentificationKeyCharacterIterator(this, new DeltaFormatDataSetFilter(this));
	}

	public void addCharacterForSynonymy(int number) {
		_charactersForSynonymy.add(number);
	}
	
	public Set<Integer> getCharactersForSynonymy() {
		return _charactersForSynonymy;
	}
}
