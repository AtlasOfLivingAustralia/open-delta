package au.org.ala.delta.key;

import java.io.File;

import au.org.ala.delta.DeltaContext;

public class KeyContext extends DeltaContext {

    double _aBase;
    double _rBase;
    double _varyWt;

    String charactersFilePath;
    String itemsFilePath;

    private File _dataDirectory;

    public KeyContext() {
        // Set default values for settings

        _aBase = 2;
        _rBase = 1.4;
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

    public double getaBase() {
        return _aBase;
    }

    public void setaBase(double aBase) {
        this._aBase = aBase;
    }

    public double getrBase() {
        return _rBase;
    }

    public void setrBase(double rBase) {
        this._rBase = rBase;
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
