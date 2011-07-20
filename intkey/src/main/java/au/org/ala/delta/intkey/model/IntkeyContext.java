package au.org.ala.delta.intkey.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.intkey.directives.DirectivePopulator;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParser;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.specimen.CharacterValue;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * Model. Maintains global application state. THIS CLASS IS NOT THREAD SAFE
 * 
 * @author Chris
 * 
 */
public class IntkeyContext extends AbstractDeltaContext {

    // dataset
    // other settings

    // set of commands that have been run
    // other stuff

    private boolean _isAdvancedMode;

    private File _taxaFile;
    private File _charactersFile;

    private IntkeyDataset _dataset;
    private File _datasetInitFile;

    private IntkeyUI _appUI;
    private DirectivePopulator _directivePopulator;

    private Specimen _specimen;

    private boolean _matchInapplicables;
    private boolean _matchUnknowns;
    private MatchType _matchType;

    private int _tolerance;

    private double _varyWeight;
    private double _rbase;

    private IntkeyCharacterOrder _characterOrder;

    private LinkedHashMap<Character, Double> _bestCharacters;

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

    // Use linked hashmap so that the keys list will be returned in
    // order of insertion.
    private LinkedHashMap<String, Set<Integer>> _userDefinedTaxonKeywords;

    public static final String CHARACTER_KEYWORD_ALL = "all";
    public static final String CHARACTER_KEYWORD_USED = "used";
    public static final String CHARACTER_KEYWORD_AVAILABLE = "available";

    public static final String TAXON_KEYWORD_ALL = "all";
    public static final String TAXON_KEYWORD_ELIMINATED = "eliminated";
    public static final String TAXON_KEYWORD_REMAINING = "remaining";

    public static final String SPECIMEN_KEYWORD = "specimen";

    private List<IntkeyDirectiveInvocation> _executedDirectives;

    /**
     * Constructor
     * 
     * @param appUI
     *            A reference to the main Intkey UI
     */
    public IntkeyContext(IntkeyUI appUI, DirectivePopulator directivePopulator) {
        if (appUI == null) {
            throw new IllegalArgumentException("UI Reference cannot be null");
        }

        if (directivePopulator == null) {
            throw new IllegalArgumentException("Directive populator cannot be null");
        }

        _appUI = appUI;
        _directivePopulator = directivePopulator;
        _recordDirectiveHistory = false;
        _processingInputFile = false;
        initializeIdentification();
    }

    /**
     * Called to set the initial state at the beginning of the identification of
     * a specimen
     */
    private void initializeIdentification() {
        // Use linked hashmap so that the keys list will be returned in
        // order of insertion.
        _userDefinedCharacterKeywords = new LinkedHashMap<String, Set<Integer>>();

        // Use linked hashmap so that the keys list will be returned in
        // order of insertion.
        _userDefinedTaxonKeywords = new LinkedHashMap<String, Set<Integer>>();

        _executedDirectives = new ArrayList<au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation>();

        _matchInapplicables = true;
        _matchUnknowns = true;
        _matchType = MatchType.OVERLAP;

        _tolerance = 0;
        _rbase = 1.1;
        _varyWeight = 1;

        _characterOrder = IntkeyCharacterOrder.BEST;
        _bestCharacters = null;
    }

    /**
     * @return Is Intkey currently running in advanced mode?
     */
    public boolean isAdvancedMode() {
        return _isAdvancedMode;
    }

    /**
     * Set whether or not Intkey is running in advanced mode
     * 
     * @param isAdvancedMode
     *            true if Intkey is running in advanced mode
     */
    public void setAdvancedMode(boolean isAdvancedMode) {
        this._isAdvancedMode = isAdvancedMode;
    }

    /**
     * Set the current characters file. If a new taxa file has previously been
     * set, calling this method will result in the new dataset being loaded. The
     * calling thread will block while the dataset is loaded.
     * 
     * @param fileName
     *            Path to the characters file
     */
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
            // cleanup the old dataset, e.g. items file needs to be closed.
            if (_dataset != null) {
                _dataset.cleanup();
            }

            _dataset = null;
            _taxaFile = null;
        }
    }

    /**
     * Set the current taxa (items) file. If a new characters file has
     * previously been set, calling this method will result in the new dataset
     * being loaded. The calling thread will block while the dataset is loaded.
     * 
     * @param fileName
     *            Path to the taxa (items) file
     */
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
            // cleanup the old dataset, e.g. items file needs to be closed.
            if (_dataset != null) {
                _dataset.cleanup();
            }

            _dataset = null;
            _charactersFile = null;
        }
    }

    /**
     * Called to read the characters and taxa files and create a new dataset.
     * This method will block while the dataset information is read from the
     * character and taxa files
     */
    private void createNewDataSet() {

        initializeIdentification();

        _dataset = IntkeyDatasetFileReader.readDataSet(_charactersFile, _taxaFile);

        _specimen = new Specimen(_dataset, _matchInapplicables, _matchInapplicables, _matchType);

        // TODO need a proper listener pattern here?
        if (!_processingInputFile) {
            _appUI.handleNewDataset(_dataset);
        }

    }

    /**
     * Read and execute the specified dataset initialization file. This method
     * will block while the calling thread while the file is read, the dataset
     * is loaded, and other directives in the file are executed.
     * 
     * @param fileName
     *            Path to the dataset initialization file
     */
    public void newDataSetFile(String fileName) {

        Logger.log("Reading in new Data Set file from: %s", fileName);

        // Don't record directive history while processing the data set file
        _recordDirectiveHistory = false;
        _processingInputFile = true;

        IntkeyDirectiveParser parser = IntkeyDirectiveParser.createInstance();

        try {
            _datasetInitFile = new File(fileName);
            parser.parse(new File(fileName), IntkeyContext.this);
        } catch (IOException ex) {
            Logger.log(ex.getMessage());
        }

        _recordDirectiveHistory = true;
        _processingInputFile = false;

        _appUI.handleNewDataset(_dataset);
    }

    /**
     * Execute the supplied command pattern object representing the invocation
     * of a directive
     * 
     * @param invoc
     *            a command pattern object representing the invocation of a
     *            directive
     */
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

    /**
     * @return the currently loaded dataset
     */
    public IntkeyDataset getDataset() {
        return _dataset;
    }

    /**
     * Set the value for a character in the current specimen
     * 
     * @param ch
     *            the character
     * @param value
     *            the character value
     */
    public void setValueForCharacter(au.org.ala.delta.model.Character ch, CharacterValue value) {
        Logger.log("Using character");
        _specimen.setValueForCharacter(ch, value);
    }

    /**
     * Remove the value for a character from the current specimen
     * 
     * @param ch
     */
    public void removeValueForCharacter(Character ch) {
        Logger.log("Deleting character");
        _specimen.removeValueForCharacter(ch);
    }

    /**
     * Called at the end of a series of updates to the current specimen to
     * inform the context that all updates have been performed
     */
    public void specimenUpdateComplete() {
        // the specimen has been updated so the currently cached best characters
        // are no longer
        // valid
        _bestCharacters = null;

        _appUI.handleSpecimenUpdated();
    }

    /**
     * Add a new character keyword
     * 
     * @param keyword
     *            The keyword. Note that the system-defined keywords "all",
     *            "used" and "available" cannot be used.
     * @param characterNumbers
     *            The set of characters to be represented by the keyword
     */
    public void addCharacterKeyword(String keyword, Set<Integer> characterNumbers) {
        if (_dataset == null) {
            throw new IllegalStateException("Cannot define a character keyword if no dataset loaded");
        }

        keyword = keyword.toLowerCase();
        if (keyword.equals(CHARACTER_KEYWORD_ALL) || keyword.equals(CHARACTER_KEYWORD_USED) || keyword.equals(CHARACTER_KEYWORD_AVAILABLE)) {
            throw new IllegalArgumentException(String.format(UIUtils.getResourceString("RedefineSystemKeyword.error"), keyword));
        }
        _userDefinedCharacterKeywords.put(keyword.toLowerCase(), characterNumbers);
    }

    /**
     * Get the list of characters that are represented by the supplied keyword
     * 
     * @param keyword
     *            the keyword.
     * @return the list of characters that are represented by the supplied
     *         keyword
     */
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

    /**
     * @return A list of all character keywords. Includes the system defined
     *         keyords "all" and "available", as well as "used" if any
     *         characters have been used.
     */
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

    public void addTaxaKeyword(String keyword, Set<Integer> taxaNumbers) {
        _userDefinedTaxonKeywords.put(keyword, taxaNumbers);
    }

    public List<Item> getTaxaForKeyword(String keyword) {
        List<Item> retList = new ArrayList<Item>();

        if (keyword.equals(TAXON_KEYWORD_ALL)) {
            return _dataset.getTaxa();
        } else if (keyword.equals(TAXON_KEYWORD_ELIMINATED)) {
            Map<Item, Integer> diffTable = _specimen.getTaxonDifferences();
            for (Item taxon : diffTable.keySet()) {
                int diffCount = diffTable.get(taxon);
                if (diffCount > _tolerance) {
                    retList.add(taxon);
                }
            }
        } else if (keyword.equals(TAXON_KEYWORD_REMAINING)) {
            Map<Item, Integer> diffTable = _specimen.getTaxonDifferences();
            if (diffTable == null) {
                retList.addAll(_dataset.getTaxa());
            } else {
                for (Item taxon : diffTable.keySet()) {
                    int diffCount = diffTable.get(taxon);
                    if (diffCount <= _tolerance) {
                        retList.add(taxon);
                    }
                }
            }
        } else {
            Set<Integer> taxonNumbers = _userDefinedTaxonKeywords.get(keyword);

            if (taxonNumbers != null) {
                for (int taxonNumber : taxonNumbers) {
                    retList.add(_dataset.getTaxon(taxonNumber));
                }
            } else {
                throw new IllegalArgumentException("bad taxon keyword");
            }
        }

        Collections.sort(retList);

        return retList;
    }

    public List<String> getTaxaKeywords() {
        List<String> retList = new ArrayList<String>();
        retList.add(TAXON_KEYWORD_ALL);

        Map<Item, Integer> taxonDifferences = _specimen.getTaxonDifferences();

        int remainingTaxaCount = 0;

        if (taxonDifferences == null) {
            remainingTaxaCount = _dataset.getNumberOfTaxa();
        } else {
            for (Item taxon : taxonDifferences.keySet()) {
                int diffCount = taxonDifferences.get(taxon);
                if (diffCount <= _tolerance) {
                    remainingTaxaCount++;
                }
            }
        }

        if (remainingTaxaCount > 0) {
            retList.add(TAXON_KEYWORD_REMAINING);
        }

        if (remainingTaxaCount < _dataset.getNumberOfTaxa()) {
            retList.add(TAXON_KEYWORD_ELIMINATED);
        }

        return retList;
    }

    /**
     * @return A list of command pattern objects representing all directives
     *         that have been executed
     */
    public List<IntkeyDirectiveInvocation> getExecutedDirectives() {
        return new ArrayList<IntkeyDirectiveInvocation>(_executedDirectives);
    }

    /**
     * Resets the context state to prepare for a new identification
     */
    public void restartIdentification() {
        // TODO need to account for fixed characters etc here.

        if (_dataset != null) {
            // Create a new blank specimen
            _specimen = new Specimen(_dataset, _matchInapplicables, _matchInapplicables, _matchType);

            // As we are starting from the beginning, best characters must be
            // cleared as they are no longer valid
            _bestCharacters = null;

            _appUI.handleIdentificationRestarted();
        }
    }

    /**
     * @return The current specimen
     */
    public Specimen getSpecimen() {
        return _specimen;
    }

    /**
     * @return true if an input file is current being processed
     */
    public boolean isProcessingInputFile() {
        return _processingInputFile;
    }

    /**
     * FOR UNIT TESTING ONLY
     * 
     * @param processing
     */
    public void setProcessingInputFile(boolean processing) {
        _processingInputFile = processing;
    }

    /**
     * @return The current error tolerance. This is used when determining which
     *         taxa to eliminate following characters being used.
     */
    public int getTolerance() {
        return _tolerance;
    }

    /**
     * @return The current vary weight. This is used by the BEST algorithm
     */
    public double getVaryWeight() {
        return _varyWeight;
    }

    /**
     * Set the current vary weight. This is used by the BEST algorithm.
     * 
     * @param varyWeight
     *            The current vary weight
     */
    public void setVaryWeight(double varyWeight) {
        _varyWeight = varyWeight;
    }

    /**
     * Gets the rbase - the base of the logarithmic character-reliability scale,
     * which is used in determining the BEST characters during an identification
     * 
     * @return the current rbase value
     */
    public double getRBase() {
        return _rbase;
    }

    /**
     * Sets the rbase - the base of the logarithmic character-reliability scale,
     * which is used in determining the BEST characters during an identification
     * 
     * @param rbase
     *            the current rbase value
     */
    public void setRBase(double rbase) {
        _rbase = rbase;
    }

    /**
     * @return a reference to the current taxa file, or null if one has not been
     *         set
     */
    public File getTaxaFile() {
        return _taxaFile;
    }

    /**
     * @return a reference to the current characters file, or null if one has
     *         not been set
     */
    public File getCharactersFile() {
        return _charactersFile;
    }

    /**
     * @return The current character order being used to list available
     *         characters in the application
     */
    public IntkeyCharacterOrder getCharacterOrder() {
        return _characterOrder;
    }

    /**
     * Set the character order used to list available characters in the
     * application
     * 
     * @param characterOrder
     *            the new character order
     */
    public void setCharacterOrder(IntkeyCharacterOrder characterOrder) {
        this._characterOrder = characterOrder;
        _appUI.handleCharacterOrderChanged();
    }

    /**
     * @return The current best characters if they are cached
     */
    public LinkedHashMap<Character, Double> getBestCharacters() {
        return _bestCharacters;
    }

    /**
     * Calculates the best characters using the BEST algorithm. This method will
     * block the calling thread while the calculation is performed
     */
    public void calculateBestCharacters() {
        _bestCharacters = SortingUtils.orderBest(IntkeyContext.this);
    }

    public boolean getMatchInapplicables() {
        return _matchInapplicables;
    }

    public boolean getMatchUnkowns() {
        return _matchUnknowns;
    }

    public MatchType getMatchType() {
        return _matchType;
    }

    /**
     * Called prior to application shutdown.
     */
    public void cleanupForShutdown() {
        if (_dataset != null) {
            _dataset.cleanup();
        }
    }

    public IntkeyUI getUI() {
        return _appUI;
    }

    public DirectivePopulator getDirectivePopulator() {
        return _directivePopulator;
    }

}
