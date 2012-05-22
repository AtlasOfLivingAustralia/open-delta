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
import java.math.BigDecimal;
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
import au.org.ala.delta.io.OutputFileSelector;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.CharacterNoteMarks;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.model.image.ImageInfo;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.util.Functor;

/**
 * Context associated with a set of DELTA input files.
 */
public class DeltaContext extends AbstractDeltaContext {

    public static enum HeadingType {
        HEADING, REGISTRATION_SUBHEADING, REGISTRATION_HEADING, SHOW
    };

    public static enum OutputFormat {
        RTF, HTML, NONE
    };

    public static enum PrintActionType {
        PRINT_CHARACTER_LIST, PRINT_ITEM_DESCRIPTIONS, PRINT_ITEM_NAMES, PRINT_SUMMARY, PRINT_UNCODED_CHARACTERS, TRANSLATE_UNCODED_CHARACTERS
    };

    private MutableDeltaDataSet _dataSet;
    protected OutputFileSelector _outputFileSelector;

    private OutputFormat _outputFormat = OutputFormat.RTF;
    private Map<String, Object> _variables;
    private List<PrintActionType> _outputActions = new ArrayList<PrintActionType>();

    private TranslateType _translateType = TranslateType.None;
    protected Set<Integer> _excludedCharacters = new HashSet<Integer>();
    protected Set<Integer> _excludedItems = new HashSet<Integer>();
    private Set<Integer> _omitPeriodForCharacters = new HashSet<Integer>();
    private Set<Integer> _omitOrForCharacters = new HashSet<Integer>();
    private Set<Integer> _newParagraphCharacters = new HashSet<Integer>();
    private Set<Integer> _charactersForSynonymy = new HashSet<Integer>();
    private Set<Integer> _useControllingCharactersFirst = new HashSet<Integer>();
    private Set<Integer> _nonautomaticControllingCharacters = new HashSet<Integer>();
    private Set<Integer> _treatIntegerCharacterAsReal = new HashSet<Integer>();
    private Set<Integer> _omitFinalCommaForCharacters = new HashSet<Integer>();
    private Set<Integer> _useAlternateComma = new HashSet<Integer>();

    private Map<String, String> _itemHeadings = new HashMap<String, String>();
    private Map<Integer, String> _itemSubHeadings = new HashMap<Integer, String>();
    private Map<Integer, String> _characterHeadings = new HashMap<Integer, String>();

    private Map<Integer, TypeSettingMark> _typeSettingMarks = new HashMap<Integer, TypeSettingMark>();
    private Map<Integer, TypeSettingMark> _formattingMarks = new HashMap<Integer, TypeSettingMark>();
    private Map<String, String> _taxonLinks = new HashMap<String, String>();
    private Set<Set<Integer>> _linkedCharacters = new HashSet<Set<Integer>>();
    private Set<Set<Integer>> _replaceSemiColonWithComma = new HashSet<Set<Integer>>();

    private Map<Integer, Set<Integer>> _emphasizedCharacters = new HashMap<Integer, Set<Integer>>();
    private Map<String, Set<Integer>> _emphasizedCharactersByDescription = new HashMap<String, Set<Integer>>();

    private Map<Integer, Set<Integer>> _addedCharacters = new HashMap<Integer, Set<Integer>>();
    private Map<String, Set<Integer>> _addedCharactersByDescription = new HashMap<String, Set<Integer>>();

    private Set<Integer> _emphasizedFeatures = new HashSet<Integer>();
    private Integer _characterForTaxonNames = null;
    private Map<Integer, List<ImageInfo>> _images = new HashMap<Integer, List<ImageInfo>>();
    private Map<Integer, IdentificationKeyCharacter> _keyCharacters = new HashMap<Integer, IdentificationKeyCharacter>();
    private Map<Integer, Double> _absolueErrorInCharacterAttributes = new HashMap<Integer, Double>();
    private Map<Integer, Double> _percentageErrorInCharacterAttributes = new HashMap<Integer, Double>();
    protected Map<Integer, Double> _itemAbundances = new HashMap<Integer, Double>();

    private Set<Integer> _omitLowerRangeCharacters = new HashSet<Integer>();

    private int _numberOfCharacters;
    private int _maxNumberOfStates;
    private int _maxNumberOfItems;

    private boolean _omitTypeSettingMarks = false;
    private boolean _replaceAngleBrackets;
    private boolean _omitCharacterNumbers = false;
    private boolean _omitInnerComments = false;
    private boolean _omitInapplicables = false;
    private Boolean _omitRedundantVariantAttributes = null;
    private boolean _insertImplicitValues = false;
    private boolean _outputHtml = false;
    private boolean _enableDeltaOutput = true;
    private boolean _chineseFormat = false;
    private Map<Integer, Boolean> _useNormalValues = new HashMap<Integer, Boolean>();
    private boolean _omitSpaceBeforeUnits = false;
    private boolean _keyCharacterListUsed = false;
    private boolean _numberStatesFromZero = false;
    private boolean _useMeanValues = false;
    private boolean _listCharacters = false;
    private boolean _listItems = false;
    private boolean _treatVariableAsUnknown = false;
    private boolean _useLastValueCoded = false;

    private Map<HeadingType, String> _headings = new HashMap<HeadingType, String>();
    private Integer _characterForTaxonImages = null;

    private String _credits;

    // private StateValueMatrix _matrix;

    private BigDecimal[] _characterWeights;

    private DirectiveParserObserver _observer;

    private Map<String, String> _indexHeadings = new HashMap<String, String>();
    protected PrintStream _defaultOut;
    protected PrintStream _defaultErr;

    public DeltaContext() {
        this(new DefaultDataSetFactory().createDataSet(""));
    }
    
    public DeltaContext(PrintStream out, PrintStream err) {
    	this(new DefaultDataSetFactory().createDataSet(""), out, err);
    }
    
    public DeltaContext(MutableDeltaDataSet dataSet, PrintStream out, PrintStream err) {
    	_defaultOut = out;   
    	_defaultErr = err;
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

           _dataSet = dataSet;

           createOutputFileManager();
           _outputFileSelector.setOutputFormat(_outputFormat);
           _outputFileSelector.setPrintStream(out);

    }

    public DeltaContext(MutableDeltaDataSet dataSet) {
    	this(dataSet, System.out, System.err);
    }
    
    public void out(String line) {
    	_defaultOut.println(line);
    }

    protected void createOutputFileManager() {
        _outputFileSelector = new OutputFileSelector(_dataSet, _defaultOut, _defaultErr);
    }

    public OutputFileSelector getOutputFileSelector() {
        return _outputFileSelector;
    }

    public MutableDeltaDataSet getDataSet() {
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

//    public void initializeMatrix() {
//        assert getNumberOfCharacters() > 0;
//        assert getMaximumNumberOfItems() > 0;
//        _matrix = new StateValueMatrix(getNumberOfCharacters(), getMaximumNumberOfItems());
//    }
//
//    public StateValueMatrix getMatrix() {
//        return _matrix;
//    }

    /**
     * Currently used only by units tests.
     */
    public void setPrintStream(PrintStream stream) {
        getOutputFileSelector().setPrintStream(stream);
    }

    public PrintFile getPrintFile() {
        return getOutputFileSelector().getPrintFile();
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

    /**
     * A "safe" version of DeltaDataSet.getCharacter() that does not throw an
     * exception if the character number is not yet in existence. Designed to
     * handle out of order creation of characters in the CharacterTypes
     * directive.
     * 
     * @param number
     *            the character number to retrieve.
     * @return the Character with the specified number, or null if no such
     *         character exists.
     */
    public Character getCharacter(int number) {
        Character c = null;

        try {
            c = _dataSet.getCharacter(number);
        } catch (IndexOutOfBoundsException e) {
            if (number > getNumberOfCharacters()) {
                throw e;
            }
        }

        return c;
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
        if (b) {
            _outputFormat = OutputFormat.NONE;
            _outputFileSelector.setOutputFormat(_outputFormat);
        }
    }

    public boolean isOmitTypeSettingMarks() {
        return _omitTypeSettingMarks;
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

    public boolean getOmitInnerComments() {
        return _omitInnerComments;
    }

    public void setOmitInapplicables(boolean b) {
        _omitInapplicables = b;
    }

    public boolean getOmitInapplicables() {
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

    public void setCharacterWeight(int number, BigDecimal weight) {
        if (_characterWeights == null) {
            _characterWeights = new BigDecimal[getNumberOfCharacters()];
        }
        _characterWeights[number - 1] = weight;

    }

    public void setCharacterReliability(int number, BigDecimal reliability) {
        if (_characterWeights == null) {
            _characterWeights = new BigDecimal[getNumberOfCharacters()];
        }
        _characterWeights[number - 1] = new BigDecimal(Math.pow(2, reliability.doubleValue() - 5));
        getCharacter(number).setReliability(reliability.floatValue());
    }

    public double getCharacterWeight(int number) {
        if (_characterWeights != null) {
            return _characterWeights[number - 1].doubleValue();
        }
        return 1;
    }

    public BigDecimal getCharacterWeightAsBigDecimal(int charNumber) {
        if (_characterWeights != null) {
            return _characterWeights[charNumber - 1];
        }
        return new BigDecimal(1);
    }

    public double getCharacterReliability(int number) {
        if (_characterWeights == null) {
            return 5;
        }
        return Math.log(_characterWeights[number - 1].doubleValue()) / Math.log(2) + 5;
    }

    /**
     * Returns true if the specified item has been marked as excluded by an
     * EXCLUDED ITEMS directive
     * 
     * @param itemNumber
     *            the item number to check.
     * @return whether the specified item is excluded.
     */
    public boolean isItemExcluded(int itemNumber) {
        return _excludedItems.contains(itemNumber);
    }

    public void excludeItem(int itemNumber) {
        _excludedItems.add(itemNumber);
    }

    public Set<Integer> getExcludedItems() {
        return _excludedItems;
    }

    /**
     * Returns the heading for the supplied item as defined by the ITEM HEADINGS
     * directive. If no heading has been supplied, this method returns null.
     * 
     * @param itemNumber
     *            the item to get the heading for.
     * @return the heading defined for the specified item or null if no heading
     *         was defined.
     */
    public String getItemHeading(int itemNumber) {
        Item item = getDataSet().getItem(itemNumber);
        String description = RTFUtils.stripFormatting(item.getDescription());

        return _itemHeadings.get(description);
    }

    public void setItemHeading(String itemDescription, String heading) {
        String description = RTFUtils.stripFormatting(itemDescription);
        _itemHeadings.put(description, heading);
    }

    public void itemSubheading(int characterNumber, String subheading) {
        _itemSubHeadings.put(characterNumber, subheading);

    }

    /**
     * Returns the sub heading for the supplied character as defined by the ITEM
     * SUBHEADINGS directive. If no heading has been supplied, this method
     * returns null.
     * 
     * @param characterNumber
     *            the character number to get the heading for.
     * @return the heading defined for the specified item or null if no heading
     *         was defined.
     */
    public String getItemSubheading(int characterNumber) {
        return _itemSubHeadings.get(characterNumber);
    }

    public void addCharacterHeading(int characterNumber, String heading) {
        _characterHeadings.put(characterNumber, heading);
    }

    public String getCharacterHeading(int characterNumber) {
        return _characterHeadings.get(characterNumber);
    }

    /**
     * Returns the index heading for the supplied item number as defined by the
     * INDEX HEADINGS directive. If no heading has been supplied, this method
     * returns null.
     * 
     * @param itemNumber
     *            the item to get the heading for.
     * @return the index heading defined for the specified item or null if no
     *         heading was defined.
     */
    public String getIndexHeading(int itemNumber) {
        String itemDescription = itemDescriptionFor(itemNumber);
        return _indexHeadings.get(itemDescription);
    }

    public Map<String, String> getIndexHeadings() {
        return _indexHeadings;
    }

    public void setIndexHeading(String itemDescription, String heading) {
        String unformattedItem = RTFUtils.stripFormatting(itemDescription);
        _indexHeadings.put(unformattedItem, heading);
    }

    public void linkCharacters(Set<Integer> linkedCharacters) {
        _linkedCharacters.add(linkedCharacters);
    }

    /**
     * Returns the set of characters linked to the supplied character.
     * 
     * @param characterNumber
     *            the character of interest.
     * @return the set of characters linked to the supplied character number, or
     *         null if the character is not linked to any other characters. The
     *         returned set includes the supplied character.
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
     * @return the character specified by the CHARACTER FOR TAXON NAMES
     *         directive. The value of the item attribute for this character
     *         will be used as the item name.
     */
    public Integer getCharacterForTaxonNames() {
        return _characterForTaxonNames;
    }

    /**
     * Specifies whether attributes in a variant items are output in natural
     * language translations. Controlled by the OMIT REDUNDANT VARIANT
     * ATTRIBUTES and INSERT REDUNDANT VARIANT ATTRIBUTES directives. If neither
     * directive is given, the default is to output attributes as coded.
     * 
     * @param b
     *            true if redundant variant attributes should be omitted.
     */
    public void setOmitRedundantVariantAttributes(Boolean b) {
        _omitRedundantVariantAttributes = b;
    }

    /**
     * Specifies whether attributes in a variant items are output in natural
     * language translations. Controlled by the OMIT REDUNDANT VARIANT
     * ATTRIBUTES and INSERT REDUNDANT VARIANT ATTRIBUTES directives. If neither
     * directive is given, the default is to output attributes as coded.
     * 
     * @param b
     *            true if redundant variant attributes should be omitted.
     */
    public Boolean getOmitRedundantVariantAttributes() {
        return _omitRedundantVariantAttributes;
    }

    public boolean getOmitPeriodForCharacter(int characterNum) {
        return _omitPeriodForCharacters.contains(characterNum);
    }

    public void setOmitPeriodForCharacter(int charNumber, boolean omit) {
        if (omit) {
            _omitPeriodForCharacters.add(charNumber);
        }
    }

    public Set<Integer> getReplaceSemiColonWithComma(int characterNum) {
        for (Set<Integer> charactersToRepalce : _replaceSemiColonWithComma) {
            if (charactersToRepalce.contains(characterNum)) {
                return charactersToRepalce;
            }
        }
        return new HashSet<Integer>();
    }

    public void replaceSemiColonWithCommon(Set<Integer> characters) {
        _replaceSemiColonWithComma.add(characters);
    }

    public boolean useAlternateCommaForCharacter(int charNum) {
        return _useAlternateComma.add(charNum);
    }

    public boolean getUseAlternateComma(int charNum) {
        return _useAlternateComma.contains(charNum);
    }

    public boolean isCharacterEmphasized(int itemNum, int characterNum) {
        boolean emphasized = entryExists(_emphasizedCharacters, itemNum, characterNum);
        if (!emphasized) {
            emphasized = itemDescriptionEntryExists(_emphasizedCharactersByDescription, itemNum, characterNum);
        }
        return emphasized;
    }

    public void emphasizeCharacters(int itemNum, Set<Integer> characters) {
        if (_emphasizedCharacters.containsKey(itemNum)) {
            characters.addAll(_emphasizedCharacters.get(itemNum));
        }
        _emphasizedCharacters.put(itemNum, characters);
    }

    public void emphasizeCharacters(String itemDescription, Set<Integer> characters) {
        String unformattedDescription = RTFUtils.stripFormatting(itemDescription);
        if (_emphasizedCharactersByDescription.containsKey(unformattedDescription)) {
            characters.addAll(_emphasizedCharactersByDescription.get(unformattedDescription));
        }
        _emphasizedCharactersByDescription.put(unformattedDescription, characters);
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
        if (item == null) {
    	    return false;
        }
        String description = RTFUtils.stripFormatting(item.getDescription());
        Set<Integer> chars = map.get(description);
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

    public void addTaxonLinks(String itemDescription, String links) {
        _taxonLinks.put(RTFUtils.stripFormatting(itemDescription), links);
    }

    public String getTaxonLinks(int itemNumber) {
        String description = itemDescriptionFor(itemNumber);
        return _taxonLinks.get(description);
    }

    private String itemDescriptionFor(int itemNumber) {
        Item item = getDataSet().getItem(itemNumber);
        String description = RTFUtils.stripFormatting(item.getDescription());
        return description;
    }

    public boolean isFeatureEmphasized(int i) {
        return _emphasizedFeatures.contains(i);
    }

    public void emphasizeFeature(int charNumber) {
        _emphasizedFeatures.add(charNumber);
    }

    public Set<Integer> getEmphasizedFeatures() {
        return _emphasizedFeatures;
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

    public boolean isFinalCommaOmmitedForCharacter(int i) {
        return _omitFinalCommaForCharacters.contains(i);
    }

    public void omitFinalCommaForCharacter(int charNumber) {
        _omitFinalCommaForCharacters.add(charNumber);
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
        if (b) {
            getOutputFileSelector().setOutputFormat(OutputFormat.HTML);
        }
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
        IdentificationKeyCharacter keyChar = _keyCharacters.get(characterNumber);
        // Because the TRANSLATE TYPE, USE NORMAL VALUES and KEY STATES
        // directives
        // are all type 4, we have to defer the determination of whether the
        // KeyChar needs to use normal values or not till now rather at
        // construction time.
        if (keyChar != null) {
            keyChar.setUseNormalValues(_translateType == TranslateType.NexusFormat || _translateType == TranslateType.Hennig86 || _translateType == TranslateType.Dist
                    || _translateType == TranslateType.PAUP || _useNormalValues.containsKey(characterNumber));

            keyChar.setUseMeanValues(_useMeanValues && (_translateType == TranslateType.NexusFormat || _translateType == TranslateType.Hennig86 || _translateType == TranslateType.PAUP));
        }
        return keyChar;
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

    public void addCharacterForSynonymy(int number) {
        _charactersForSynonymy.add(number);
    }

    public Set<Integer> getCharactersForSynonymy() {
        return _charactersForSynonymy;
    }

    public void setNonautomaticControllingCharacter(int number, boolean value) {
        if (value) {
            _nonautomaticControllingCharacters.add(number);
        }
    }

    public boolean getNonautomaticControllingCharacter(int number) {
        return _nonautomaticControllingCharacters.contains(number);
    }

    public boolean getUseNormalValues(int characterNumber) {
        Boolean useNormalValues = _useNormalValues.get(characterNumber);
        return useNormalValues != null ? useNormalValues : false;
    }

    public void setUseNormalValues(int characterNumber, boolean useNormalValues) {
        _useNormalValues.put(characterNumber, useNormalValues);
    }

    public void setAbsoluteError(int characterNumber, Double error) {
        _absolueErrorInCharacterAttributes.put(characterNumber, error);
    }

    public Double getAbsoluteError(int characterNumber) {
        return _absolueErrorInCharacterAttributes.get(characterNumber);
    }

    public boolean hasAbsoluteError(int characterNumber) {
        return _absolueErrorInCharacterAttributes.containsKey(characterNumber);
    }

    public void setPercentageError(int characterNumber, Double error) {
        _percentageErrorInCharacterAttributes.put(characterNumber, error);
    }

    public Double getPercentageError(int characterNumber) {
        return _percentageErrorInCharacterAttributes.get(characterNumber);
    }

    public boolean hasPercentageError(int characterNumber) {
        return _percentageErrorInCharacterAttributes.containsKey(characterNumber);
    }

    public void setOmitLowerForCharacter(int characterNumber, boolean omitLower) {
        if (omitLower) {
            _omitLowerRangeCharacters.add(characterNumber);
        } else {
            _omitLowerRangeCharacters.remove(characterNumber);
        }
    }

    public boolean getOmitLowerForCharacter(int characterNumber) {
        return _omitLowerRangeCharacters.contains(characterNumber);
    }

    public void setOmitSpaceBeforeUnits(boolean b) {
        _omitSpaceBeforeUnits = b;
    }

    public boolean getOmitSpaceBeforeUnits() {
        return _omitSpaceBeforeUnits;
    }

    @Override
    public ParsingContext newParsingContext() {
        ParsingContext context = super.newParsingContext();
        _outputFileSelector.setParsingContext(context);
        return context;
    }

    @Override
    public ParsingContext endCurrentParsingContext() {
        ParsingContext context = super.endCurrentParsingContext();
        _outputFileSelector.setParsingContext(context);
        return context;
    }

    public void addItemAbundancy(int itemNumber, double value) {
        _itemAbundances.put(itemNumber, value);
    }

    public double getItemAbundancy(int itemNumber) {
        Double abundancy = _itemAbundances.get(itemNumber);
        if (abundancy == null) {
            abundancy = 5d;
        }
        return abundancy;
    }

    public void setKeyCharacterListUsed(boolean keyCharacterListUsed) {
        _keyCharacterListUsed = keyCharacterListUsed;
    }

    public boolean getKeyCharacterListUsed() {
        return _keyCharacterListUsed;
    }

    public void setTreatIntegerCharacterAsReal(int characterNumber, boolean treatAsReal) {
        if (treatAsReal) {
            _treatIntegerCharacterAsReal.add(characterNumber);
        } else {
            _treatIntegerCharacterAsReal.remove(characterNumber);
        }
    }

    public boolean getTreatIntegerCharacterAsReal(int characterNumber) {
        return _treatIntegerCharacterAsReal.contains(characterNumber);
    }

    public void addPrintAction(PrintActionType action) {
        _outputActions.add(action);
    }

    public List<PrintActionType> getPrintActions() {
        return _outputActions;
    }

    public void print(String heading) {
        getOutputFileSelector().getPrintFile().writeJustifiedText(heading, 1);

    }

    public void numberStatesFromZero() {
        _numberStatesFromZero = true;
    }

    public boolean getNumberStatesFromZero() {
        return _translateType == TranslateType.Hennig86 || (_numberStatesFromZero && (_translateType == TranslateType.PAUP || _translateType == TranslateType.NexusFormat));
    }

    public void useMeanValues() {
        _useMeanValues = true;
    }

    public boolean getUseMeanValues() {
        return _useMeanValues;
    }

    /**
     * Resets the effect of any include/exclude character calls
     */
    public void includeAllCharacters() {
        _excludedCharacters.clear();
    }

    /**
     * Resets the effect of any include/exclude item calls
     */
    public void includeAllItems() {
        _excludedItems.clear();
    }

    /**
     * CHARACTER LIST, CHARACTER NOTES and CHARACTER IMAGES will be output to
     * the listing file. (By default they are not).
     */
    public void enableCharacterListing() {
        _listCharacters = true;
    }

    public boolean isCharacterListingEnabled() {
        return _listCharacters;
    }

    /**
     * ITEM DESCRIPTIONS, TAXON IMAGES will be output to the listing file. (By
     * default they are not).
     */
    public void enableItemListing() {
        _listItems = true;
    }

    public boolean isItemListingEnabled() {
        return _listItems;
    }

    // Used to assist item description validation.
    public Iterator<String> addCharacterDescriptions() {
        return _addedCharactersByDescription.keySet().iterator();
    }

    public Iterator<String> emphasizedCharacterDescriptions() {
        return _emphasizedCharactersByDescription.keySet().iterator();
    }

    public Iterator<String> itemHeadingDescriptions() {
        return _itemHeadings.keySet().iterator();
    }

    public Iterator<String> itemOutputFilesDescriptions() {
        return _outputFileSelector.itemOutputFileDescriptions();
    }

    public Iterator<String> indexHeadingsDescriptions() {
        return _indexHeadings.keySet().iterator();
    }

    public void treatVariableAsUnknown() {
        _treatVariableAsUnknown = true;
    }

    public boolean getTreatVariableAsUnknown() {
        return _treatVariableAsUnknown;
    }

    public boolean getUseLastValueCoded() {
        return _useLastValueCoded;
    }

    public void useLastValueCoded() {
        _useLastValueCoded = true;
    }
}
