package au.org.ala.delta.intkey.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParser;
import au.org.ala.delta.intkey.model.specimen.CharacterValue;
import au.org.ala.delta.intkey.model.specimen.Specimen;

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

    // Use linked hashmap so that the keys list will be returned in
    // order of insertion.
    private LinkedHashMap<String, Set<Integer>> _characterKeywords;
    private static final String CHARACTER_KEYWORD_ALL = "all";
    private static final String CHARACTER_KEYWORD_USED = "used";
    private static final String CHARACTER_KEYWORD_AVAILABLE = "available";

    private static final String TAXON_KEYWORD_ALL = "all";
    private static final String TAXON_KEYWORD_ELIMINATED = "eliminated";
    private static final String TAXON_KEYWORD_REMAINING = "remaining";

    private List<IntkeyDirectiveInvocation> _executedDirectives;

    public IntkeyContext(Intkey appUI) {
        _appUI = appUI;
        _specimen = new Specimen();

        // Use linked hashmap so that the keys list will be returned in
        // order of insertion.
        _characterKeywords = new LinkedHashMap<String, Set<Integer>>();

        _executedDirectives = new ArrayList<IntkeyDirectiveInvocation>();
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
            throw new IllegalArgumentException(String.format("Characters file '%s' could not be found", absoluteFileName));
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
            throw new IllegalArgumentException(String.format("Taxa file '%s' could not be found", absoluteFileName));
        }

        if (_dataset == null && _charactersFile != null) {
            createNewDataSet();
        } else {
            _dataset = null;
            _charactersFile = null;
        }
    }

    private void createNewDataSet() {
        _dataset = new IntkeyDatasetFileBuilder().readDataSet(_charactersFile, _taxaFile);

        // TODO really need a proper listener pattern here
        if (_appUI != null) {
            _appUI.handleNewDataSet(_dataset);
        }
    }

    public void newDataSetFile(String fileName) {
        Logger.log("Reading in new Data Set file from: %s", fileName);

        IntkeyDirectiveParser parser = IntkeyDirectiveParser.createInstance();

        try {
            _datasetInitFile = new File(fileName);
            parser.parse(new File(fileName), this);
        } catch (IOException ex) {
            Logger.log(ex.getMessage());
        }
    }

    public void executeDirective(IntkeyDirectiveInvocation invoc) {
        // record correct insertion index in case execution of directive results
        // in further directives being
        // run (such as in the case of the NewDataSet directive).
        int insertionIndex = _executedDirectives.size();

        boolean success = invoc.execute(this);
        if (success) {
            _executedDirectives.add(insertionIndex, invoc);
        }
    }

    public JFrame getMainFrame() {
        return _appUI.getMainFrame();
    }

    public IntkeyDataset getDataset() {
        return _dataset;
    }

    public void setValueForCharacter(au.org.ala.delta.model.Character ch, CharacterValue value) {
        Logger.log("Using character");
        _specimen.setValueForCharacter(ch, value);
        _appUI.handleCharacterUsed(ch, value);
    }

    public void addCharacterKeyword(String keyword, Set<Integer> characterNumbers) {
        keyword = keyword.toLowerCase();
        if (keyword.equals(CHARACTER_KEYWORD_ALL) || keyword.equals(CHARACTER_KEYWORD_USED) || keyword.equals(CHARACTER_KEYWORD_AVAILABLE)) {
            throw new IllegalArgumentException(String.format("'%s' is a system keyword and cannot be redefined", keyword));
        }
        _characterKeywords.put(keyword.toLowerCase(), characterNumbers);
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
            Set<Integer> characterNumbersSet = _characterKeywords.get(keyword.toLowerCase());

            // If there is no exact match for the specified keyword text, try
            // and match a single
            // keyword that begins with the text
            if (characterNumbersSet == null) {
                List<String> matches = new ArrayList<String>();
                for (String savedKeyword : _characterKeywords.keySet()) {
                    if (savedKeyword.startsWith(keyword)) {
                        matches.add(savedKeyword);
                    }
                }

                if (matches.size() == 1) {
                    characterNumbersSet = _characterKeywords.get(matches.get(0));
                } else {
                    throw new IllegalArgumentException(String.format("Keyword '%s' is ambiguous", keyword));
                }
            }

            if (characterNumbersSet != null) {
                List<au.org.ala.delta.model.Character> retList = new ArrayList<au.org.ala.delta.model.Character>();
                for (int charNum : characterNumbersSet) {
                    retList.add(_dataset.getCharacter(charNum));
                }
                Collections.sort(retList, new CharacterComparator());
                return retList;
            } else {
                throw new IllegalArgumentException(String.format("Keyword '%s' not found", keyword));
            }
        }
    }

    public List<String> getCharacterKeywords() {
        return new ArrayList<String>(_characterKeywords.keySet());
    }

    public List<IntkeyDirectiveInvocation> getExecutedDirectives() {
        return new ArrayList<IntkeyDirectiveInvocation>(_executedDirectives);
    }

    public void restartIdentification() {
        // TODO need to account for fixed characters etc here.

        // Create a new blank specimen
        _specimen = new Specimen();
        _appUI.handleRestartIdentification();
    }
}
