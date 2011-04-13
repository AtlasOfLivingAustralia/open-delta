package au.org.ala.delta.intkey.model;

import java.io.File;
import java.util.List;

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
    public void setHeading(String heading) {
        this._heading = heading;
    }
    public void setSubHeading(String subHeading) {
        this._subHeading = subHeading;
    }
    public void setValidationString(String validationString) {
        this._validationString = validationString;
    }

    

}
