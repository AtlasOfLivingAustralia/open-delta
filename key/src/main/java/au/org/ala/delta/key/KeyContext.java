package au.org.ala.delta.key;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.DeltaContext;

public class KeyContext extends DeltaContext {

    double _aBase;
    double _rBase;
    double _varyWt;

    String charactersFilePath;
    String itemsFilePath;

    private List<Integer> _includedCharacters;
    private List<Integer> _includedItems;
    private Map<Integer, Float> _itemAbundances;

    private File _dataDirectory;

    public KeyContext() {
        // Set default values for settings

        _aBase = 2;
        _rBase = 1.4;
        _varyWt = 0.8;

        charactersFilePath = "kchars";
        itemsFilePath = "kitems";

        _itemAbundances = new HashMap<Integer, Float>();
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

    public List<Integer> getIncludedCharacters() {
        return _includedCharacters;
    }

    public void setIncludedCharacters(List<Integer> includedCharacters) {
        this._includedCharacters = includedCharacters;
    }

    public List<Integer> getIncludedItems() {
        return _includedItems;
    }

    public void setIncludedItems(List<Integer> includedItems) {
        this._includedItems = includedItems;
    }

    public float getItemAbundance(int itemNumber) {
        return _itemAbundances.get(itemNumber);
    }

    public void setItemAbundance(int itemNumber, float abundance) {
        _itemAbundances.put(itemNumber, abundance);
    }

}
