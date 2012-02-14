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
package au.org.ala.delta.key;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.key.directives.io.KeyOutputFileManager;
import au.org.ala.delta.util.Pair;

public class KeyContext extends DeltaContext {

    double _aBase;
    double _rBase;
    double _reuse;
    double _varyWt;

    private int _numberOfConfirmatoryCharacters;
    private int _stopAfterColumn;

    private File _charactersFile;
    private File _itemsFile;

    private File _keyOutputFile;
    private File _keyTypesettingFile;
    private File _listingFile;

    private File _dataDirectory;

    private boolean _addCharacterNumbers;
    private boolean _displayBracketedKey;
    private boolean _displayTabularKey;

    private boolean _allowImproperSubgroups;

    private boolean _treatUnknownAsInapplicable;

    private String _typeSettingFileHeaderText;

    private Map<Pair<Integer, Integer>, Integer> _presetCharacters;

    double[] _characterCosts;
    double[] _calculatedItemAbundanceValues;

    /**
     * Map of taxon number to set of character numbers - these characters have
     * been explicitly set as variable for the taxon using the TREAT CHARACTERS
     * AS VARIABLE
     */
    private Map<Integer, Set<Integer>> _taxonVariableCharacters;

    public KeyContext(File dataDirectory, PrintStream out, PrintStream err) {
        super(out, err);
        this._dataDirectory = dataDirectory;

        try {
            _outputFileSelector.setOutputDirectory(_dataDirectory.getAbsolutePath());
        } catch (Exception ex) {
            throw new RuntimeException("Error setting output directory");
        }

        _aBase = 2;
        _rBase = 1.4;
        _reuse = 1.01;
        _varyWt = 0.8;

        _stopAfterColumn = -1;

        _charactersFile = new File(_dataDirectory, "kchars");
        _itemsFile = new File(_dataDirectory, "kitems");

        _addCharacterNumbers = false;
        _displayBracketedKey = true;
        _displayTabularKey = true;
        _allowImproperSubgroups = false;

        _treatUnknownAsInapplicable = false;

        _presetCharacters = new HashMap<Pair<Integer, Integer>, Integer>();
        _characterCosts = null;
        _calculatedItemAbundanceValues = null;

        _taxonVariableCharacters = new HashMap<Integer, Set<Integer>>();
    }

    public KeyContext(File dataDirectory) {
        this(dataDirectory, System.out, System.err);
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
        if (varyWt < 0 || varyWt > 1) {
            throw new IllegalArgumentException("VARYWT must be a real number between 0 and 1");
        }
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

    public File getKeyOutputFile() {
        return _keyOutputFile;
    }

    public void setKeyOutputFile(File keyOutputFile) {
        this._keyOutputFile = keyOutputFile;
    }

    public File getKeyTypesettingFile() {
        return _keyTypesettingFile;
    }

    public void setKeyTypesettingFile(File keyTypesettingFile) {
        this._keyTypesettingFile = keyTypesettingFile;
    }

    public File getListingFile() {
        return _listingFile;
    }

    public void setListingFile(File listingFile) {
        this._listingFile = listingFile;
    }

    @Override
    protected void createOutputFileManager() {
        _outputFileSelector = new KeyOutputFileManager(getDataSet());
    }

    public KeyOutputFileManager getOutputFileManager() {
        return (KeyOutputFileManager) _outputFileSelector;
    }

    public String getTypeSettingFileHeaderText() {
        return _typeSettingFileHeaderText;
    }

    public void setTypeSettingFileHeaderText(String typeSettingFileHeaderText) {
        this._typeSettingFileHeaderText = typeSettingFileHeaderText;
    }

    public void setPresetCharacter(int characterNumber, int columnNumber, int groupNumber) {
        Pair<Integer, Integer> columnGroupPair = new Pair<Integer, Integer>(columnNumber, groupNumber);
        _presetCharacters.put(columnGroupPair, characterNumber);
    }

    /**
     * Returns the preset character number for the given column number and group
     * number, or -1 if no character has been preset for the column and group.
     * 
     * @param columnNumber
     * @param groupNumber
     * @return the preset character number for the given column number and group
     *         number, or -1 if no character has been preset for the column and
     *         group.
     */
    public int getPresetCharacter(int columnNumber, int groupNumber) {
        Pair<Integer, Integer> columnGroupPair = new Pair<Integer, Integer>(columnNumber, groupNumber);
        if (_presetCharacters.containsKey(columnGroupPair)) {
            return _presetCharacters.get(columnGroupPair);
        } else {
            return -1;
        }
    }

    public int getNumberOfConfirmatoryCharacters() {
        return _numberOfConfirmatoryCharacters;
    }

    public void setNumberOfConfirmatoryCharacters(int numberOfConfirmatoryCharacters) {
        if (numberOfConfirmatoryCharacters < 0 || numberOfConfirmatoryCharacters > 4) {
            throw new IllegalArgumentException("Number of confirmatory characters must be between 0 and 4");
        }
        this._numberOfConfirmatoryCharacters = numberOfConfirmatoryCharacters;
    }

    /**
     * Returns the column after which key generation stops, or -1 if no such
     * value has been set - in this case, key generation will proceed as far as
     * possible.
     * 
     * @param stopAfterColumn
     */
    public int getStopAfterColumn() {
        return _stopAfterColumn;
    }

    public void setStopAfterColumn(int stopAfterColumn) {
        if (stopAfterColumn <= 0) {
            throw new IllegalArgumentException("Value for STOP AFTER COLUMN must be a positive integer");
        }
        this._stopAfterColumn = stopAfterColumn;
    }

    public boolean getTreatUnknownAsInapplicable() {
        return _treatUnknownAsInapplicable;
    }

    public void setTreatUnknownAsInapplicable(boolean treatUnknownAsInapplicable) {
        this._treatUnknownAsInapplicable = treatUnknownAsInapplicable;
    }

    public boolean itemAbundancySet(int itemNumber) {
        return _itemAbundances.containsKey(itemNumber);
    }

    public void setCharacterCost(int characterNumber, double cost) {
        if (characterNumber < 1 || characterNumber > getNumberOfCharacters()) {
            throw new IllegalArgumentException("Invalid character number");
        }

        if (_characterCosts == null) {
            _characterCosts = new double[getNumberOfCharacters()];
        }

        _characterCosts[characterNumber - 1] = cost;
    }

    public double getCharacterCost(int characterNumber) {
        if (characterNumber < 1 || characterNumber > getNumberOfCharacters()) {
            throw new IllegalArgumentException("Invalid character number");
        }

        return _characterCosts[characterNumber - 1];
    }

    public double[] getCharacterCostsAsArray() {
        return _characterCosts;
    }

    public void setCalculatedItemAbundanceValue(int itemNumber, double value) {
        if (itemNumber < 1 || itemNumber > getMaximumNumberOfItems()) {
            throw new IllegalArgumentException("Invalid item number");
        }

        if (_calculatedItemAbundanceValues == null) {
            _calculatedItemAbundanceValues = new double[getMaximumNumberOfItems()];
        }

        _calculatedItemAbundanceValues[itemNumber - 1] = value;
    }

    public double getCalculatedItemAbundanceValue(int itemNumber) {
        if (itemNumber < 1 || itemNumber > getMaximumNumberOfItems()) {
            throw new IllegalArgumentException("Invalid item number");
        }

        return _calculatedItemAbundanceValues[itemNumber - 1];
    }

    public double[] getCalculatedItemAbundanceValuesAsArray() {
        return _calculatedItemAbundanceValues;
    }

    public boolean getAllowImproperSubgroups() {
        return _allowImproperSubgroups;
    }

    public void setAllowImproperSubgroups(boolean allowImproperSubgroups) {
        this._allowImproperSubgroups = allowImproperSubgroups;
    }

    /**
     * Set a set of characters as variable for the specified taxon. Used when
     * processing the TREAT CHARACTERS AS VARIABLE directive
     * 
     * @param taxonNumber
     *            the taxon number
     * @param characterNumbers
     *            the character numbers
     */
    public void setVariableCharactersForTaxon(int taxonNumber, Set<Integer> characterNumbers) {
        _taxonVariableCharacters.put(taxonNumber, characterNumbers);
    }

    /**
     * Get the set of characters that have been set as variable for the
     * specified taxon, through the TREAT CHARACTERS AS VARIABLE directive
     * @param taxonNumber the taxon number
     * @return a set of character numbers
     */
    public Set<Integer> getVariableCharactersForTaxon(int taxonNumber) {
        return _taxonVariableCharacters.get(taxonNumber);
    }

}
