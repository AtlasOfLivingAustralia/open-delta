package au.org.ala.delta.key;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.DeltaContext;

public class KeyContext extends DeltaContext {

    double _aBase;
    double _rBase;
    double _reuse;
    double _varyWt;

    String charactersFilePath;
    String itemsFilePath;

    private List<Integer> _includedCharacters;
    private List<Integer> _includedItems;

    private File _dataDirectory;

    public KeyContext() {
        // Set default values for settings

        _aBase = 2;
        _rBase = 1.4;
        _reuse = 1.01;
        _varyWt = 0.8;

        charactersFilePath = "kchars";
        itemsFilePath = "kitems";
    }

    public File getDataDirectory() {
        return _dataDirectory;
    }

    public void setDataDirectory(File dataDirectory) {
        this._dataDirectory = dataDirectory;
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

    public String getCharactersFilePath() {
        return charactersFilePath;
    }

    public void setCharactersFilePath(String charactersFilePath) {
        this.charactersFilePath = charactersFilePath;
    }

    public String getItemsFilePath() {
        return itemsFilePath;
    }

    public void setItemsFilePath(String itemsFilePath) {
        this.itemsFilePath = itemsFilePath;
    }

}
