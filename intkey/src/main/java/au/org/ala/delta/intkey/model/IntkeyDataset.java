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
package au.org.ala.delta.intkey.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;
import au.org.ala.delta.model.impl.ControllingInfo;

/**
 * Intkey dataset
 * 
 * @author ChrisF
 * 
 */
public class IntkeyDataset implements DeltaDataSet {

    /**
     * The characters file used to create the dataset
     */
    private File _charactersFile;

    /**
     * The items (taxa) file used create the dataset
     */
    private File _itemsFile;

    /**
     * BinFile wrapper for items file
     */
    private BinFile _itemsBinFile;

    /**
     * items (taxa) file header
     */
    private ItemsFileHeader _itemsFileHeader;

    /**
     * Characters file header
     */
    private CharactersFileHeader _charactersFileHeader;

    /**
     * Dataset characters
     */
    private List<Character> _characters;

    /**
     * The set of character that should be ignored when ordering the best
     * characters using the BEST algorithm
     */
    private Set<Character> _charactersToIgnoreForBest;

    /**
     * Dataset taxa
     */
    private List<Item> _taxa;

    /**
     * Dataset heading
     */
    private String _heading;

    /**
     * Dataset subheading
     */
    private String _subHeading;

    /**
     * Dataset validation string. Not used in this implementation of Intkey.
     */
    private String _validationString;

    /**
     * Formatting information (TYPSET) to be used when character notes are
     * written to the main intkey window. Not used in this implementation of
     * Intkey.
     */
    private String _mainCharNotesFormattingInfo;

    /**
     * Formatting information (TYPSET) to be used when character notes are
     * written to a help window. Not used in this implementation of Intkey.
     */
    private String _helpCharNotesFormattingInfo;

    /**
     * The string to be used for the word "or" in natural language descriptions
     */
    private String _orWord;

    /**
     * Startup images for the dataset
     */
    private List<Image> _startupImages;

    /**
     * Character keyword images
     */
    private List<Image> _characterKeywordImages;

    /**
     * Taxon keyword images
     */
    private List<Image> _taxonKeywordImages;

    /**
     * Details of fonts to be used for text in image overlays. First item is the
     * default font, second item is the default button font, third item is the
     * default feature font. Second and third items may not be present.
     */
    private List<FontInfo> _overlayFonts;

    /**
     * If true, output of DELTA format data by Intkey using the OUTPUT SUMMARY
     * command is permitted. This value is ignored by this implementation of
     * intkey.
     */
    private boolean _deltaOutputPermitted;

    /**
     * If true, the text in the data set should be treated as chinese. This
     * value is not required - chinese text is treated correctly anyway. This
     * value is ignored by this implementation of intkey.
     */
    private boolean _chineseFormat;

    /**
     * If true, item (taxa) subheadings have been defined for the dataset.
     */
    private boolean _itemSubheadingsPresent;

    /**
     * A list of text characters that contain synonmy information. The values of
     * such characters for each taxon contain synonyms for that taxon.
     */
    private List<TextCharacter> _synonymyCharacters;

    /**
     * Constructor
     */
    public IntkeyDataset() {
        _characters = new ArrayList<Character>();
        _taxa = new ArrayList<Item>();
        _startupImages = new ArrayList<Image>();
        _characterKeywordImages = new ArrayList<Image>();
        _taxonKeywordImages = new ArrayList<Image>();
        _overlayFonts = new ArrayList<FontInfo>();
        _synonymyCharacters = new ArrayList<TextCharacter>();
    }

    /**
     * @return The dataset's characters file
     */
    public File getCharactersFile() {
        return _charactersFile;
    }

    /**
     * @return The dataset's items (taxa) file
     */
    public File getItemsFile() {
        return _itemsFile;
    }

    /**
     * @return Header information for the items file
     */
    public ItemsFileHeader getItemsFileHeader() {
        return _itemsFileHeader;
    }

    /**
     * @return Header information for the characters file
     */
    public CharactersFileHeader getCharactersFileHeader() {
        return _charactersFileHeader;
    }

    /**
     * @return Dataset characters ordered by character number
     */
    @Override
    public List<Character> getCharactersAsList() {
        // defensive copy
        return new ArrayList<Character>(_characters);
    }

    /**
     * @return Dataset taxa ordered by character number
     */
    @Override
    public List<Item> getItemsAsList() {
        // defensive copy
        return new ArrayList<Item>(_taxa);
    }

    /**
     * @return Dataset heading
     */
    public String getHeading() {
        return _heading;
    }

    /**
     * @return Dataset subheading
     */
    public String getSubHeading() {
        return _subHeading;
    }

    /**
     * @return Dataset validation string. Not used in this implementation of
     *         Intkey
     */
    public String getValidationString() {
        return _validationString;
    }

    /**
     * Set the reference to the dataset characters file
     * 
     * @param charactersFile
     *            the characters file
     */
    void setCharactersFile(File charactersFile) {
        this._charactersFile = charactersFile;
    }

    /**
     * Set the reference to the dataset items (taxa) file
     * 
     * @param itemsFile
     *            the items file
     */
    void setItemsFile(File itemsFile) {
        this._itemsFile = itemsFile;
    }

    /**
     * Set the reference to the items file header
     * 
     * @param itemsFileHeader
     *            the items file header
     */
    void setItemsFileHeader(ItemsFileHeader itemsFileHeader) {
        this._itemsFileHeader = itemsFileHeader;
    }

    /**
     * Set the reference to the characters file header
     * 
     * @param charactersFileHeader
     */
    void setCharactersFileHeader(CharactersFileHeader charactersFileHeader) {
        this._charactersFileHeader = charactersFileHeader;
    }

    /**
     * Set the dataset's characters
     * 
     * @param characters
     *            The dataset's characters, ordered by character number
     */
    void setCharacters(List<au.org.ala.delta.model.Character> characters) {
        this._characters = characters;
        determineCharactersToIgnoreForBest();
    }

    /**
     * Examine the characters in the dataset and based on their types and
     * reliabilities, determine which characters should be ignored when
     * determining the best ordering.
     */
    public void determineCharactersToIgnoreForBest() {
        _charactersToIgnoreForBest = new HashSet<Character>();
        for (Character ch : _characters) {
            // Ignore character if its reliability is zero
            if (ch.getReliability() == 0) {
                _charactersToIgnoreForBest.add(ch);
                continue;
            }

            // Ignore character if it is a text character
            if (ch instanceof TextCharacter) {
                _charactersToIgnoreForBest.add(ch);
                continue;
            }

            // Ignore real characters if there are no key states
            // for real characters
            if (ch instanceof RealCharacter && !realCharacterKeyStateBoundariesPresent()) {
                _charactersToIgnoreForBest.add(ch);
            }

        }
    }

    /**
     * @return The set of characters that should be ignored when ordering
     *         characters using the BEST algorithm
     */
    public Set<Character> getCharactersToIgnoreForBest() {
        return new HashSet<Character>(_charactersToIgnoreForBest);
    }

    /**
     * Set the dataset's data
     * 
     * @param taxa
     *            The dataset's taxa, ordered by taxon number
     */
    void setTaxa(List<Item> taxa) {
        this._taxa = taxa;
    }

    /**
     * Set the dataset heading
     * 
     * @param heading
     *            dataset heading
     */
    void setHeading(String heading) {
        this._heading = heading;
    }

    /**
     * Set the dataset subheading
     * 
     * @param subHeading
     *            dataset subheading
     */
    void setSubHeading(String subHeading) {
        this._subHeading = subHeading;
    }

    /**
     * Set the dataset validation string. Note the the validation string is not
     * used in this implementation of Intkey
     * 
     * @param validationString
     */
    void setValidationString(String validationString) {
        this._validationString = validationString;
    }

    /**
     * @return Formatting information (TYPSET) to be used when character notes
     *         are written to the main Intkey window. Not used in this
     *         implementation of Intkey.
     */
    public String getMainCharNotesFormattingInfo() {
        return _mainCharNotesFormattingInfo;
    }

    /**
     * Set formatting information (TYPSET) to be used when character notes are
     * written to the main Intkey. This data is not used in this implementation
     * of Intkey.
     * 
     * @param mainCharNotesFormattingInfo
     *            formatting information
     */
    void setMainCharNotesFormattingInfo(String mainCharNotesFormattingInfo) {
        this._mainCharNotesFormattingInfo = mainCharNotesFormattingInfo;
    }

    /**
     * 
     * @return Formatting information (TYPSET) to be used when character notes
     *         are written to a help window. Not used in this implementation of
     *         Intkey.
     */
    public String getHelpCharNotesFormattingInfo() {
        return _helpCharNotesFormattingInfo;
    }

    /**
     * Set formatting information (TYPSET) to be used when character notes are
     * written to a help window. This data is not used in this implementation of
     * Intkey.
     * 
     * @param helpCharNotesFormattingInfo
     */
    void setHelpCharNotesFormattingInfo(String helpCharNotesFormattingInfo) {
        this._helpCharNotesFormattingInfo = helpCharNotesFormattingInfo;
    }

    /**
     * @return The string to be used for the word "or" in natural language
     *         descriptions
     */
    public String getOrWord() {
        return _orWord;
    }

    /**
     * Set the string to be used for the word "or" in natural language
     * descriptions
     * 
     * @param orWord
     *            The string to be used for the word "or" in natural language
     *            descriptions
     */
    void setOrWord(String orWord) {
        this._orWord = orWord;
    }

    /**
     * @return The dataset startup images
     */
    public List<Image> getStartupImages() {
        // defensive copy
        return new ArrayList<Image>(_startupImages);

    }

    /**
     * Set the dataset startup images
     * 
     * @param startupImages
     *            The dataset images
     */
    void setStartupImages(List<Image> startupImages) {
        _startupImages = new ArrayList<Image>(startupImages);
    }

    /**
     * @return Character keyword images
     */
    public List<Image> getCharacterKeywordImages() {
        // defensive copy
        return new ArrayList<Image>(_characterKeywordImages);
    }

    /**
     * Set the character keyword images
     * 
     * @param characterKeywordImages
     *            character keyword images
     */
    void setCharacterKeywordImages(List<Image> characterKeywordImages) {
        this._characterKeywordImages = new ArrayList<Image>(characterKeywordImages);
    }

    /**
     * @return Taxon keyword images
     */
    public List<Image> getTaxonKeywordImages() {
        // defensive copy
        return new ArrayList<Image>(_taxonKeywordImages);
    }

    /**
     * Set the taxon keyword images
     * 
     * @param taxonKeywordImages
     *            taxon keyword images
     */
    void setTaxonKeywordImages(List<Image> taxonKeywordImages) {
        this._taxonKeywordImages = new ArrayList<Image>(taxonKeywordImages);
    }

    /**
     * @return Details of fonts to be used for text in image overlays. First
     *         item is the default font, second item is the default button font,
     *         third item is the default feature font. Second and third items
     *         may not be present.
     */
    public List<FontInfo> getOverlayFonts() {
        // return defensive copy
        return new ArrayList<FontInfo>(_overlayFonts);
    }

    /**
     * Set the details of fonts to be used for text in images overlays.
     * 
     * @param overlayFonts
     *            Details of fonts to be used for text in image overlays. First
     *            item is the default font, second item is the default button
     *            font, third item is the default feature font. Second and third
     *            items may not be present.
     */
    void setOverlayFonts(List<FontInfo> overlayFonts) {
        this._overlayFonts = overlayFonts;
    }

    /**
     * 
     * @return True if output of DELTA format data by Intkey using the OUTPUT
     *         SUMMARY command is permitted. This value is ignored by this
     *         implementation of intkey.
     */
    public boolean isDeltaOutputPermitted() {
        return _deltaOutputPermitted;
    }

    /**
     * Set whether output of DELTA format data by Intkey using the OUTPUT
     * SUMMARY command is permitted. This value is ignored by this
     * implementation of intkey.
     * 
     * @param deltaOutputPermitted
     */
    void setDeltaOutputPermitted(boolean deltaOutputPermitted) {
        this._deltaOutputPermitted = deltaOutputPermitted;
    }

    /**
     * 
     * @return If true, the text in the data set should be treated as chinese.
     *         This value is not required - chinese text is treated correctly
     *         anyway. This value is ignored by this implementation of intkey.
     */
    public boolean isChineseFormat() {
        return _chineseFormat;
    }

    /**
     * Set whether or not dataset format should be treated as chinese. This
     * value is not required - chinese text is treated correctly anyway. This
     * value is ignored by this implementation of intkey.
     * 
     * @param chineseFormat
     */
    void setChineseFormat(boolean chineseFormat) {
        this._chineseFormat = chineseFormat;
    }

    /**
     * Get the specified character
     * 
     * @param charNum
     *            the number of the desired character
     * @return The character with the specified character number
     */
    @Override
    public Character getCharacter(int charNum) {
        if (charNum < 1 || charNum > _characters.size()) {
            throw new IllegalArgumentException("Invalid character number " + charNum);
        }
        return _characters.get(charNum - 1);
    }

    /**
     * @return the number of characters in the dataset
     */
    @Override
    public int getNumberOfCharacters() {
        return _characters.size();
    }

    /**
     * Get the specified taxon
     * 
     * @param taxonNum
     *            the number of the desired taxon
     * @return The taxon with the specified taxon number
     */
    @Override
    public Item getItem(int taxonNum) {
        if (taxonNum < 1 || taxonNum > _taxa.size()) {
            throw new IllegalArgumentException("Invalid taxon number " + taxonNum);
        }
        return _taxa.get(taxonNum - 1);
    }

    /**
     * The number of taxa in the dataset
     * 
     * @return
     */
    public int getNumberOfTaxa() {
        return _taxa.size();
    }

    /**
     * Set the BinFile wrapper for the dataset items file
     * 
     * @param itemsBinFile
     *            dataset items file wrapped in a BinFile
     */
    void setItemsBinFile(BinFile itemsBinFile) {
        _itemsBinFile = itemsBinFile;
    }

    /**
     * Get all attributes for the character with the supplied number.
     * Synchronized because multiple threads reading from the characters and
     * items files simultaneously can cause problems.
     * 
     * @param charNo
     *            character number
     * @return A list of all attributes for the character, ordered by taxon
     *         number in ascending order
     */
    @Override
    public synchronized List<Attribute> getAllAttributesForCharacter(int charNo) {
        List<Attribute> attrList = IntkeyDatasetFileReader.readAllAttributesForCharacter(_itemsFileHeader, _itemsBinFile, getCharacter(charNo), _taxa);
        return attrList;
    }

    /**
     * Get the attribute for the supplied taxon/character pair
     * 
     * @param itemNo
     *            the taxon number
     * @param charNo
     * @return A list of all attributes for the character, ordered by taxon
     *         number in ascending order
     */
    public synchronized Attribute getAttribute(int itemNo, int charNo) {
        return IntkeyDatasetFileReader.readAttribute(_itemsFileHeader, _itemsBinFile, getCharacter(charNo), getItem(itemNo));
    }

    /**
     * @return true if real character key state boundaries have been defined for
     *         the dataset. Key state boundaries are used to convert real values
     *         into multistate values. The key state boundaries define a number
     *         of intervals for the real values. Each interval has a "state"
     *         associated with it.
     */
    public boolean realCharacterKeyStateBoundariesPresent() {
        return _itemsFileHeader.getLSbnd() > 0;
    }

    /**
     * @return The characters that contains synonymy information
     */
    public List<TextCharacter> getSynonymyCharacters() {
        // defensive copy
        return new ArrayList<TextCharacter>(_synonymyCharacters);
    }

    /**
     * Set the characters that contain synonymy information
     * 
     * @param synonymyCharacters
     *            The characters that contain synonymy information
     */
    void setSynonymyCharacters(List<TextCharacter> synonymyCharacters) {
        _synonymyCharacters = new ArrayList<TextCharacter>(synonymyCharacters);
    }

    /**
     * Get taxon by name
     * 
     * @param taxonName
     *            taxon name without comments
     * @return The taxon with the supplied name
     */
    public Item getTaxonByName(String taxonName) {
        ItemFormatter formatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, true, false, false);
        for (Item taxon : _taxa) {
            // String comments, RTF etc. from taxon description
            String formattedTaxonName = formatter.formatItemDescription(taxon);

            if (formattedTaxonName.equalsIgnoreCase(taxonName)) {
                return taxon;
            }
        }
        return null;
    }

    /**
     * @return A map of taxa, to attributes for each taxa associated with the
     *         synonymy characters.
     */
    public Map<Item, List<TextAttribute>> getSynonymyAttributesForTaxa() {
        Map<Item, List<TextAttribute>> taxonSynonymyAttributes = new HashMap<Item, List<TextAttribute>>();

        for (Item taxon : _taxa) {
            List<TextAttribute> synonymyAttributesList = new ArrayList<TextAttribute>();
            taxonSynonymyAttributes.put(taxon, synonymyAttributesList);
        }

        for (TextCharacter ch : _synonymyCharacters) {
            List<Attribute> attrs = getAllAttributesForCharacter(ch.getCharacterId());

            for (Attribute attr : attrs) {
                TextAttribute textAttr = (TextAttribute) attr;

                Item taxon = attr.getItem();
                List<TextAttribute> synonymyStringList = taxonSynonymyAttributes.get(taxon);
                synonymyStringList.add(textAttr);
            }
        }

        return taxonSynonymyAttributes;
    }

    /**
     * @return True if item (taxa) subheadings have been defined for the dataset
     */
    public boolean itemSubheadingsPresent() {
        return _itemSubheadingsPresent;
    }

    /**
     * Set if item (taxa) subheadings have been defined for the dataset
     * @param itemSubheadingsPresent
     */
    void setItemSubheadingsPresent(boolean itemSubheadingsPresent) {
        this._itemSubheadingsPresent = itemSubheadingsPresent;
    }

    /**
     * Called prior to application shutdown.
     */
    @Override
    public void close() {
        _itemsBinFile.close();
    }

    /**
     * @return dataset name
     */
    @Override
    public String getName() {
        return getHeading();
    }

    @Override
    public String getAttributeAsString(int itemNumber, int characterNumber) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return number of taxa in the dataset
     */
    @Override
    public int getMaximumNumberOfItems() {
        return getNumberOfTaxa();
    }

    @Override
    public List<Item> getUncodedItems(Character character) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Item> getItemsWithMultipleStatesCoded(MultiStateCharacter character) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CharacterDependency> getAllCharacterDependencies() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImageSettings getImageSettings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item itemForDescription(String description) {
        return getTaxonByName(description);
    }

    @Override
    public ControllingInfo checkApplicability(Character character, Item item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUncoded(Item item, Character character) {
        throw new UnsupportedOperationException();
    }

}
