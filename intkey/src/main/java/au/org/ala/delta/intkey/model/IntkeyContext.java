package au.org.ala.delta.intkey.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParser;
import au.org.ala.delta.intkey.model.specimen.CharacterValue;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Character;

/**
 * Model. Maintains global application state.
 * 
 * @author Chris
 * 
 */
public class IntkeyContext extends AbstractDeltaContext {

    // dataset
    // other settings

    // set of commands that have been run
    // other stuff

    private File _taxaFile;
    private File _charactersFile;

    private IntkeyDataset _dataset;
    private File _datasetInitFile;

    private Intkey _appUI;

    private Specimen _specimen;
    
    private boolean _matchInapplicables;
    private boolean _matchUnknowns;
    private MatchType _matchType;
    
    private int _tolerance;

    /**
     * Should executed directives be recorded in the history?
     */
    private boolean _recordDirectiveHistory;
    
    /**
     * Is an input file currently being processed?
     */
    private boolean _processingInputFile;

    // Use linked hashmap so that the keys list will be returned in
    // order of insertion.
    private LinkedHashMap<String, Set<Integer>> _userDefinedCharacterKeywords;
    public static final String CHARACTER_KEYWORD_ALL = "all";
    public static final String CHARACTER_KEYWORD_USED = "used";
    public static final String CHARACTER_KEYWORD_AVAILABLE = "available";

    public static final String TAXON_KEYWORD_ALL = "all";
    public static final String TAXON_KEYWORD_ELIMINATED = "eliminated";
    public static final String TAXON_KEYWORD_REMAINING = "remaining";

    private List<IntkeyDirectiveInvocation> _executedDirectives;

    public IntkeyContext(Intkey appUI) {
        _appUI = appUI;

        // Use linked hashmap so that the keys list will be returned in
        // order of insertion.
        _userDefinedCharacterKeywords = new LinkedHashMap<String, Set<Integer>>();

        _executedDirectives = new ArrayList<IntkeyDirectiveInvocation>();
        _recordDirectiveHistory = false;
        _processingInputFile = false;
        
        _matchInapplicables = true;
        _matchUnknowns = true;
        _matchType = MatchType.OVERLAP;
        
        _tolerance = 0;
    }

    public void setFileCharacters(String fileName) {
        Logger.log("Setting characters file to: %s", fileName);

        if (_datasetInitFile != null) {
            _charactersFile = new File(_datasetInitFile.getParentFile(), fileName);
        } else {
            _charactersFile = new File(fileName);
        }

        if (!_charactersFile.exists()) {
            String absoluteFileName = _charactersFile.getAbsolutePath();
            _charactersFile = null;
            throw new IllegalArgumentException(String.format(UIUtils.getResourceString("CharactersFileNotFound.error"), absoluteFileName));
        }

        if (_dataset == null && _taxaFile != null) {
            createNewDataSet();
        } else {
            _dataset = null;
            _taxaFile = null;
        }
    }

    public void setFileTaxa(String fileName) {
        Logger.log("Setting taxa file to: %s", fileName);

        if (_datasetInitFile != null) {
            _taxaFile = new File(_datasetInitFile.getParentFile(), fileName);
        } else {
            _taxaFile = new File(fileName);
        }

        if (!_taxaFile.exists()) {
            String absoluteFileName = _taxaFile.getAbsolutePath();
            _taxaFile = null;
            throw new IllegalArgumentException(String.format(UIUtils.getResourceString("TaxaFileNotFound.error"), absoluteFileName));
        }

        if (_dataset == null && _charactersFile != null) {
            createNewDataSet();
        } else {
            _dataset = null;
            _charactersFile = null;
        }
    }

    private void createNewDataSet() {
        _executedDirectives = new ArrayList<IntkeyDirectiveInvocation>();

        _dataset = IntkeyDatasetFileReader.readDataSet(_charactersFile, _taxaFile);
        _specimen = new Specimen(_dataset, _matchInapplicables, _matchInapplicables, _matchType);

        // TODO need a proper listener pattern here?
        if (_appUI != null) {
            _appUI.handleNewDataSet(_dataset);
        }
    }

    public void newDataSetFile(String fileName) {
        Logger.log("Reading in new Data Set file from: %s", fileName);

        // Don't record directive history while processing the data set file
        _recordDirectiveHistory = false;
        _processingInputFile = true;

        IntkeyDirectiveParser parser = IntkeyDirectiveParser.createInstance();

        try {
            _datasetInitFile = new File(fileName);
            parser.parse(new File(fileName), this);
        } catch (IOException ex) {
            Logger.log(ex.getMessage());
        }

        // re enable recording of directives executed
        _recordDirectiveHistory = true;
        _processingInputFile = false;
    }

    public void executeDirective(IntkeyDirectiveInvocation invoc) {
        // record correct insertion index in case execution of directive results
        // in further directives being
        // run (such as in the case of the File Input directive).
        int insertionIndex = _executedDirectives.size();

        boolean success = invoc.execute(this);
        if (success && _recordDirectiveHistory) {
            if (_executedDirectives.size() < insertionIndex) {
                // executed directives list has been cleared, just add this
                // directive to the end of the list
                _executedDirectives.add(invoc);
            } else {
                _executedDirectives.add(insertionIndex, invoc);
            }
        }
    }

    public IntkeyDataset getDataset() {
        return _dataset;
    }

    public void setValueForCharacter(au.org.ala.delta.model.Character ch, CharacterValue value) {
        Logger.log("Using character");
        _specimen.setValueForCharacter(ch, value);
        if (_appUI != null) {
            _appUI.handleSpecimenUpdated();
        }
    }
    
    public void removeValueForCharacter(Character ch) {
        Logger.log("Deleting character");
        _specimen.removeValueForCharacter(ch);
        if (_appUI != null) {
            _appUI.handleSpecimenUpdated();
        }
    }
    
    public void addCharacterKeyword(String keyword, Set<Integer> characterNumbers) {
        keyword = keyword.toLowerCase();
        if (keyword.equals(CHARACTER_KEYWORD_ALL) || keyword.equals(CHARACTER_KEYWORD_USED) || keyword.equals(CHARACTER_KEYWORD_AVAILABLE)) {
            throw new IllegalArgumentException(String.format(UIUtils.getResourceString("RedefineSystemKeyword.error"), keyword));
        }
        _userDefinedCharacterKeywords.put(keyword.toLowerCase(), characterNumbers);
    }

    public List<au.org.ala.delta.model.Character> getCharactersForKeyword(String keyword) {
        keyword = keyword.toLowerCase();

        if (keyword.equals(CHARACTER_KEYWORD_ALL)) {
            return new ArrayList<au.org.ala.delta.model.Character>(_dataset.getCharacters());
        } else if (keyword.equals(CHARACTER_KEYWORD_USED)) {
            return _specimen.getUsedCharacters();
        } else if (keyword.equals(CHARACTER_KEYWORD_AVAILABLE)) {
            List<au.org.ala.delta.model.Character> availableCharacters = new ArrayList<au.org.ala.delta.model.Character>(_dataset.getCharacters());
            availableCharacters.removeAll(_specimen.getUsedCharacters());
            return availableCharacters;
        } else {
            Set<Integer> characterNumbersSet = _userDefinedCharacterKeywords.get(keyword.toLowerCase());

            // If there is no exact match for the specified keyword text, try
            // and match a single
            // keyword that begins with the text
            if (characterNumbersSet == null) {
                List<String> matches = new ArrayList<String>();
                for (String savedKeyword : _userDefinedCharacterKeywords.keySet()) {
                    // ignore leading and trailing whitespace when matching
                    // against a keyword
                    if (savedKeyword.trim().startsWith(keyword.trim())) {
                        matches.add(savedKeyword);
                    }
                }

                if (matches.size() == 0) {
                    throw new IllegalArgumentException(String.format(UIUtils.getResourceString("KeywordNotFound.error"), keyword));
                } else if (matches.size() == 1) {
                    characterNumbersSet = _userDefinedCharacterKeywords.get(matches.get(0));
                } else {
                    throw new IllegalArgumentException(String.format(UIUtils.getResourceString("KeywordAmbiguous.error"), keyword));
                }
            }

            List<au.org.ala.delta.model.Character> retList = new ArrayList<au.org.ala.delta.model.Character>();
            for (int charNum : characterNumbersSet) {
                retList.add(_dataset.getCharacter(charNum));
            }
            Collections.sort(retList);
            return retList;
        }
    }

    public List<String> getCharacterKeywords() {
        List<String> retList = new ArrayList<String>();
        retList.add(CHARACTER_KEYWORD_ALL);

        if (_specimen.getUsedCharacters().size() > 0) {
            retList.add(CHARACTER_KEYWORD_USED);
        }

        retList.add(CHARACTER_KEYWORD_AVAILABLE);
        retList.addAll(_userDefinedCharacterKeywords.keySet());

        return retList;
    }

    public List<IntkeyDirectiveInvocation> getExecutedDirectives() {
        return new ArrayList<IntkeyDirectiveInvocation>(_executedDirectives);
    }

    public void restartIdentification() {
        // TODO need to account for fixed characters etc here.

        if (_dataset != null) {
            // Create a new blank specimen
            _specimen = new Specimen(_dataset, _matchInapplicables, _matchInapplicables, _matchType);
            _appUI.handleSpecimenUpdated();
        }
    }

    public Specimen getSpecimen() {
        return _specimen;
    }
    
    public boolean isProcessingInputFile() {
        return _processingInputFile;
    }
    
    public int getTolerance() {
        return _tolerance;
    }
}
