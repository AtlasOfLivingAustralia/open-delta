package au.org.ala.delta.key;

import java.io.File;

import au.org.ala.delta.DeltaContext;

public class KeyContext extends DeltaContext {

    double _aBase;
    double _rBase;
    double _reuse;
    double _varyWt;

    private File _charactersFile;
    private File _itemsFile;
    
    private File _dataDirectory;
    
    private boolean _addCharacterNumbers;
    private boolean _displayBracketedKey;
    private boolean _displayTabularKey;
    

    public KeyContext(File dataDirectory) {
        // Set default values for settings
        
        this._dataDirectory = dataDirectory;

        _aBase = 2;
        _rBase = 1.4;
        _reuse = 1.01;
        _varyWt = 0.8;

        _charactersFile = new File(_dataDirectory, "kchars");
        _itemsFile = new File(_dataDirectory, "kitems");
        
        _addCharacterNumbers = false;
        _displayBracketedKey = true;
        _displayTabularKey = true;
    }

    public File getDataDirectory() {
        return _dataDirectory;
    }

    public double getABase() {
        return _aBase;
    }

    public void setABase(double aBase) {
        this._aBase = aBase;
    }

    public double getRBase() {
        return _rBase;
    }

    public void setRBase(double rBase) {
        this._rBase = rBase;
    }

    public double getReuse() {
        return _reuse;
    }

    public void setReuse(double reuse) {
        this._reuse = reuse;
    }
    
    public double getVaryWt() {
        return _varyWt;
    }

    public void setVaryWt(double varyWt) {
        this._varyWt = varyWt;
    }

    public File getCharactersFile() {
        return _charactersFile;
    }

    public void setCharactersFile(File charactersFile) {
        this._charactersFile = charactersFile;
    }

    public File getItemsFile() {
        return _itemsFile;
    }

    public void setItemsFile(File itemsFile) {
        this._itemsFile = itemsFile;
    }
    
    public boolean getAddCharacterNumbers() {
        return _addCharacterNumbers;
    }

    public void setAddCharacterNumbers(boolean addCharacterNumbers) {
        this._addCharacterNumbers = addCharacterNumbers;
    }
    
    public boolean getDisplayBracketedKey() {
        return _displayBracketedKey;
    }

    public void setDisplayBracketedKey(boolean displayBracketedKey) {
        this._displayBracketedKey = displayBracketedKey;
    }

    public boolean getDisplayTabularKey() {
        return _displayTabularKey;
    }

    public void setDisplayTabularKey(boolean displayTabularKey) {
        this._displayTabularKey = displayTabularKey;
    }

}
