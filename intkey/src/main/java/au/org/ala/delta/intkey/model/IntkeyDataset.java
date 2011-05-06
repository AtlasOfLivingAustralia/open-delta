package au.org.ala.delta.intkey.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

public class IntkeyDataset {
    
    private File _charactersFile;
    private File _itemsFile;
    private ItemsFileHeader _itemsFileHeader;
    private CharactersFileHeader _charactersFileHeader;
    private List<au.org.ala.delta.model.Character> _characters;
    private List<Item> _taxa;
    
    private String _heading;
    private String _subHeading;
    private String _validationString;

    private String _mainCharNotesFormattingInfo;
    private String _helpCharNotesFormattingInfo;
    
    private String _orWord;
    
    private String _startupImageData;
    private String _characterKeywordImageData;
    private String _taxonKeywordImageData;
    private List<String> _overlayFonts;
    
    private boolean _deltaOutputPermitted;
    private boolean chineseFormat;
    
    public File getCharactersFile() {
        return _charactersFile;
    }
    public File getItemsFile() {
        return _itemsFile;
    }
    public ItemsFileHeader getItemsFileHeader() {
        return _itemsFileHeader;
    }
    public CharactersFileHeader getCharactersFileHeader() {
        return _charactersFileHeader;
    }
    public List<au.org.ala.delta.model.Character> getCharacters() {
        return _characters;
    }
    public List<Item> getTaxa() {
        return _taxa;
    }
    public String getHeading() {
        return _heading;
    }
    public String getSubHeading() {
        return _subHeading;
    }
    public String getValidationString() {
        return _validationString;
    }
    
    void setCharactersFile(File charactersFile) {
        this._charactersFile = charactersFile;
    }
    void setItemsFile(File itemsFile) {
        this._itemsFile = itemsFile;
    }
    void setItemsFileHeader(ItemsFileHeader itemsFileHeader) {
        this._itemsFileHeader = itemsFileHeader;
    }
    void setCharactersFileHeader(CharactersFileHeader charactersFileHeader) {
        this._charactersFileHeader = charactersFileHeader;
    }
    void setCharacters(List<au.org.ala.delta.model.Character> characters) {
        this._characters = characters;
    }
    void setTaxa(List<Item> taxa) {
        this._taxa = taxa;
    }
    void setHeading(String heading) {
        this._heading = heading;
    }
    void setSubHeading(String subHeading) {
        this._subHeading = subHeading;
    }
    void setValidationString(String validationString) {
        this._validationString = validationString;
    }
    public String getMainCharNotesFormattingInfo() {
        return _mainCharNotesFormattingInfo;
    }
    void setMainCharNotesFormattingInfo(String mainCharNotesFormattingInfo) {
        this._mainCharNotesFormattingInfo = mainCharNotesFormattingInfo;
    }
    public String getHelpCharNotesFormattingInfo() {
        return _helpCharNotesFormattingInfo;
    }
    void setHelpCharNotesFormattingInfo(String helpCharNotesFormattingInfo) {
        this._helpCharNotesFormattingInfo = helpCharNotesFormattingInfo;
    }
    public String getOrWord() {
        return _orWord;
    }
    void setOrWord(String orWord) {
        this._orWord = orWord;
    }
    public String getStartupImageData() {
        return _startupImageData;
    }
    void setStartupImageData(String startupImageData) {
        this._startupImageData = startupImageData;
    }
    public String getCharacterKeywordImageData() {
        return _characterKeywordImageData;
    }
    void setCharacterKeywordImageData(String characterKeywordImageData) {
        this._characterKeywordImageData = characterKeywordImageData;
    }
    public String getTaxonKeywordImageData() {
        return _taxonKeywordImageData;
    }
    void setTaxonKeywordImageData(String taxonKeywordImageData) {
        this._taxonKeywordImageData = taxonKeywordImageData;
    }
    public List<String> getOverlayFonts() {
        //return defensive copy
        return new ArrayList<String>(_overlayFonts);
    }
    void setOverlayFonts(List<String> overlayFonts) {
        this._overlayFonts = overlayFonts;
    }
    public boolean isDeltaOutputPermitted() {
        return _deltaOutputPermitted;
    }
    void setDeltaOutputPermitted(boolean deltaOutputPermitted) {
        this._deltaOutputPermitted = deltaOutputPermitted;
    }
    public boolean isChineseFormat() {
        return chineseFormat;
    }
    void setChineseFormat(boolean chineseFormat) {
        this.chineseFormat = chineseFormat;
    }
    
    public Character getCharacter(int charNum) {
        if (charNum < 1 || charNum > _characters.size()) {
            throw new IllegalArgumentException("Invalid character number");
        }
        return _characters.get(charNum - 1);
    }
    
    public int getNumberOfCharacters() {
        return _characters.size();
    }
}
