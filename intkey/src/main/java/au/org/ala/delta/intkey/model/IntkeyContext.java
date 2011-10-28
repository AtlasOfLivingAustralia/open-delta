package au.org.ala.delta.intkey.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.intkey.directives.DirectivePopulator;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParser;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocationException;
import au.org.ala.delta.intkey.model.specimen.CharacterValue;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.ResourceSettings;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

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

    private boolean _isAdvancedMode;

    private File _taxaFile;
    private File _charactersFile;

    private IntkeyDataset _dataset;

    private String _remoteInkFileLocation;
    private File _zippedDatasetFile;

    /**
     * A .ink file used to load a new dataset. May be a jnlp style file
     * specifying where data is to be downloaded from. Or may be a directives
     * file that is run to load the dataset in intkey, initialize values etc. In
     * the latter case, this value will be the same as _initializationFile (see
     * below).
     */
    private File _datasetStartupFile;

    /**
     * A directives file that is run to load the dataset in intkey, initialize
     * values etc. Typically a .ini file, but can also have extension .ink.
     */
    private File _initializationFile;

    private StartupFileData _startupFileData;

    private IntkeyUI _appUI;
    private DirectivePopulator _directivePopulator;

    private Specimen _specimen;

    private boolean _matchInapplicables;
    private boolean _matchUnknowns;
    private MatchType _matchType;

    private int _tolerance;

    private double _varyWeight;
    private double _rbase;

    private DiagType _diagType;
    private int _diagLevel;
    private int _stopBest;

    private IntkeyCharacterOrder _characterOrder;

    // The taxon to be separated when using the SEPARATE character order.
    private int _taxonToSeparate;

    private LinkedHashMap<Character, Double> _bestOrSeparateCharacters;

    private Set<Integer> _includedCharacters;
    private Set<Integer> _includedTaxa;

    private List<String> _imagePathLocations;
    private List<String> _infoPathLocations;

    private List<Pair<String, String>> _taxonInformationDialogCommands;

    private IntkeyDirectiveParser _directiveParser;

    private boolean _charactersFixed;
    private List<Integer> _fixedCharactersList;
    private Set<Integer> _exactCharactersSet;

    private boolean _autoTolerance;

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
    public static final String CHARACTER_KEYWORD_NONE = "none";

    public static final String TAXON_KEYWORD_ALL = "all";
    public static final String TAXON_KEYWORD_ELIMINATED = "eliminated";
    public static final String TAXON_KEYWORD_REMAINING = "remaining";
    public static final String TAXON_KEYWORD_NONE = "none";
    public static final String TAXON_KEYWORD_SELECTED = "selected";

    public static final String SPECIMEN_KEYWORD = "specimen";

    private List<IntkeyDirectiveInvocation> _executedDirectives;

    private boolean _displayNumbering;
    private boolean _displayInapplicables;
    private boolean _displayUnknowns;
    private boolean _displayComments;
    private boolean _displayContinuous;
    private ImageDisplayMode _displayImagesMode;
    private boolean _displayKeywords;

    private File _logFile;
    private File _journalFile;
    private PrintWriter _logFileWriter;
    private PrintWriter _journalFileWriter;

    // Intkey can only write to a single output file at a time, so need to keep
    // track of the
    // current file
    private File _currentOutputFile;
    private PrintWriter _currentOutputFileWriter;

    private StringBuilder _logCache;
    private StringBuilder _journalCache;

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
        _processingInputFile = false;

        _directiveParser = IntkeyDirectiveParser.createInstance();
        _logCache = new StringBuilder();
        _journalCache = new StringBuilder();

        initializeIdentification();
    }

    /**
     * Called to set the initial state when a new dataset is loaded
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
        _diagType = DiagType.SPECIMENS;

        _tolerance = 0;
        _rbase = 1.1;
        _varyWeight = 1;
        _diagLevel = 1;

        _characterOrder = IntkeyCharacterOrder.BEST;
        _taxonToSeparate = -1;
        _bestOrSeparateCharacters = null;

        _imagePathLocations = new ArrayList<String>();
        _infoPathLocations = new ArrayList<String>();

        _taxonInformationDialogCommands = new ArrayList<Pair<String, String>>();

        _charactersFixed = false;
        _fixedCharactersList = new ArrayList<Integer>();
        _exactCharactersSet = new HashSet<Integer>();

        _displayNumbering = false;
        _displayInapplicables = true;
        _displayUnknowns = true;
        _displayComments = false;
        _displayKeywords = true;

        // TODO the setting of this should be dependent on whether the UI is in
        // normal or advanced mode.
        _displayImagesMode = ImageDisplayMode.AUTO;

    }

    /**
     * @return Is Intkey currently running in advanced mode?
     */
    public synchronized boolean isAdvancedMode() {
        return _isAdvancedMode;
    }

    /**
     * Set whether or not Intkey is running in advanced mode
     * 
     * @param isAdvancedMode
     *            true if Intkey is running in advanced mode
     */
    public synchronized void setAdvancedMode(boolean isAdvancedMode) {
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
    public synchronized void setFileCharacters(File charactersFile) {
        Logger.log("Setting characters file to: %s", charactersFile.getAbsolutePath());

        if (!charactersFile.exists()) {
            String absoluteFileName = charactersFile.getAbsolutePath();
            throw new IllegalArgumentException(UIUtils.getResourceString("CharactersFileNotFound.error", absoluteFileName));
        }

        _charactersFile = charactersFile;

        if (_dataset == null && _taxaFile != null) {
            createNewDataSet();
        } else {
            // cleanup the old dataset, in case the FILE CHARACTERS directive
            // has
            // been called without loading a new dataset file.
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
    public synchronized void setFileTaxa(File taxaFile) {
        Logger.log("Setting taxa file to: %s", taxaFile.getAbsolutePath());

        if (!taxaFile.exists()) {
            String absoluteFileName = taxaFile.getAbsolutePath();
            throw new IllegalArgumentException(UIUtils.getResourceString("TaxaFileNotFound.error", absoluteFileName));
        }

        _taxaFile = taxaFile;

        if (_dataset == null && _charactersFile != null) {
            createNewDataSet();
        } else {
            // cleanup the old dataset, in case the FILE TAXA directive has
            // been called without loading a new dataset file.
            if (_dataset != null) {
                _dataset.cleanup();
            }

            _dataset = null;
            _charactersFile = null;
        }
    }

    public synchronized void processInputFile(File inputFile) {
        Logger.log("Reading in directives from file: %s", inputFile.getAbsolutePath());

        if (inputFile == null || !inputFile.exists()) {
            throw new IllegalArgumentException("Could not open input file " + inputFile.getAbsolutePath());
        }

        // May be in several levels of input file, so need to ensure we set this
        // back to the correct value.
        boolean oldProcessingInputFile = _processingInputFile;
        _processingInputFile = true;

        IntkeyDirectiveParser parser = IntkeyDirectiveParser.createInstance();

        try {
            parser.parse(inputFile, IntkeyContext.this);
        } catch (IOException ex) {
            Logger.log(ex.getMessage());
            _appUI.displayErrorMessage(String.format("Error reading file '%s'", inputFile.getAbsolutePath()));
        }

        _processingInputFile = oldProcessingInputFile;
    }

    /**
     * Called to read the characters and taxa files and create a new dataset.
     * This method will block while the dataset information is read from the
     * character and taxa files
     */
    private void createNewDataSet() {
        _dataset = IntkeyDatasetFileReader.readDataSet(_charactersFile, _taxaFile);

        _specimen = new Specimen(_dataset, _matchInapplicables, _matchInapplicables, _matchType);

        _includedCharacters = new HashSet<Integer>();
        IntRange charNumRange = new IntRange(1, _dataset.getNumberOfCharacters());
        for (int i : charNumRange.toArray()) {
            _includedCharacters.add(i);
        }

        _includedTaxa = new HashSet<Integer>();
        IntRange taxaNumRange = new IntRange(1, _dataset.getNumberOfTaxa());
        for (int i : taxaNumRange.toArray()) {
            _includedTaxa.add(i);
        }

        // TODO need a proper listener pattern here?
        if (!_processingInputFile) {
            _appUI.handleNewDataset(_dataset);
        }

        _stopBest = _dataset.getNumberOfCharacters();
        _appUI.clearToolbar();
    }

    /**
     * Read and execute the specified dataset startup file. This file may be
     * either a "webstart" file, or a file containing actual directives to
     * initialize the dataset.
     * 
     * This method will block while the calling thread while the file is read,
     * the dataset is loaded, and other directives in the file are executed.
     * 
     * @param fileName
     *            Path to the dataset initialization file
     * @return SwingWorker used to load the dataset in a separate thread - unit
     *         tests need this so that they can block until the dataset is
     *         loaded.
     */
    public synchronized SwingWorker<?, ?> newDataSetFile(File datasetFile) {
        Logger.log("Reading in directives from file: %s", datasetFile.getAbsolutePath());

        cleanupOldDataset();

        initializeIdentification();

        if (datasetFile == null || !datasetFile.exists()) {
            throw new IllegalArgumentException("Could not open dataset file " + datasetFile.getAbsolutePath());
        }

        StartupFileLoader loader = new StartupFileLoader(datasetFile);
        loader.execute();

        _datasetStartupFile = datasetFile;

        return loader;
    }

    private void processInitializationFile(File initializationFile) {
        _initializationFile = initializationFile;
        processInputFile(initializationFile);
    }

    // TODO Does this belong here?
    public synchronized void parseAndExecuteDirective(String command) {
        try {
            _directiveParser.parse(new StringReader(command), this);
        } catch (Exception ex) {
            _appUI.displayErrorMessage(String.format("Exception thrown while processing directive \"%s\"", command));
            ex.printStackTrace();
        }
    }

    /**
     * Execute the supplied command pattern object representing the invocation
     * of a directive
     * 
     * @param invoc
     *            a command pattern object representing the invocation of a
     *            directive
     */
    public synchronized void executeDirective(IntkeyDirectiveInvocation invoc) {
        // record correct insertion index in case execution of directive results
        // in further directives being
        // run (such as in the case of the File Input directive).
        int insertionIndex = _executedDirectives.size();

        try {
            boolean success = invoc.execute(this);
            if (success && !_processingInputFile) {
                if (_executedDirectives.size() < insertionIndex) {
                    // executed directives list has been cleared, just add this
                    // directive to the end of the list
                    _executedDirectives.add(invoc);
                } else {
                    _executedDirectives.add(insertionIndex, invoc);
                }

                appendToLog("*" + invoc.toString());
                appendToJournal("*" + invoc.toString());
            }
        } catch (IntkeyDirectiveInvocationException ex) {
            _appUI.displayErrorMessage(ex.getMessage());
        }
    }

    /**
     * @return the currently loaded dataset
     */
    public synchronized IntkeyDataset getDataset() {
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
    // TODO take a character number rather than a character object?
    public synchronized void setValueForCharacter(au.org.ala.delta.model.Character ch, CharacterValue value) {
        Logger.log("Using character");
        _specimen.setValueForCharacter(ch, value);
    }

    /**
     * Remove the value for a character from the current specimen
     * 
     * @param ch
     */
    // TODO take a character number rather than a character object?
    public synchronized void removeValueForCharacter(Character ch) {
        Logger.log("Deleting character");
        _specimen.removeValueForCharacter(ch);

        if (_charactersFixed) {
            // Need to manually box the int - otherwise will use the character
            // id as an index.
            _fixedCharactersList.remove(Integer.valueOf(ch.getCharacterId()));
        }
    }

    /**
     * Called at the end of a series of updates to the current specimen to
     * inform the context that all updates have been performed
     */
    public synchronized void specimenUpdateComplete() {
        // the specimen has been updated so the currently cached best characters
        // are no longer
        // valid
        _bestOrSeparateCharacters = null;

        // if the autotolerance (as specified by SET AUTOTOLERANCE directive) is
        // on,
        // reduce the tolerance to the smallest value such that the number of
        // taxa remaining is non-zero.
        if (_autoTolerance) {
            Map<Item, Set<Character>> taxaDifferingCharacters = _specimen.getTaxonDifferences();

            int minDiff = Integer.MAX_VALUE;
            for (Set<Character> differingCharacters : taxaDifferingCharacters.values()) {
                if (differingCharacters.size() < minDiff) {
                    minDiff = differingCharacters.size();
                }

                if (minDiff == 0) {
                    break;
                }
            }

            _tolerance = minDiff;
        }

        updateUI();
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
    // TODO check character number is a valid character number
    public synchronized void addCharacterKeyword(String keyword, Set<Integer> characterNumbers) {
        if (_dataset == null) {
            throw new IllegalStateException("Cannot define a character keyword if no dataset loaded");
        }

        keyword = keyword.toLowerCase();
        if (keyword.equals(CHARACTER_KEYWORD_ALL) || keyword.equals(CHARACTER_KEYWORD_USED) || keyword.equals(CHARACTER_KEYWORD_AVAILABLE) || keyword.equals(CHARACTER_KEYWORD_NONE)) {
            throw new IllegalArgumentException(UIUtils.getResourceString("RedefineSystemKeyword.error", keyword));
        }

        for (int chNum : characterNumbers) {
            if (chNum < 1 || chNum > _dataset.getNumberOfCharacters()) {
                throw new IllegalArgumentException(String.format("Invalid character number %s", chNum));
            }
        }

        _userDefinedCharacterKeywords.put(keyword, characterNumbers);
    }

    /**
     * Get the list of characters that are represented by the supplied keyword
     * 
     * @param keyword
     *            the keyword.
     * @return the list of characters that are represented by the supplied
     *         keyword
     */
    public synchronized List<au.org.ala.delta.model.Character> getCharactersForKeyword(String keyword) {
        keyword = keyword.toLowerCase();

        if (keyword.equals(CHARACTER_KEYWORD_ALL)) {
            return new ArrayList<au.org.ala.delta.model.Character>(_dataset.getCharacters());
        } else if (keyword.equals(CHARACTER_KEYWORD_USED)) {
            return _specimen.getUsedCharacters();
        } else if (keyword.equals(CHARACTER_KEYWORD_AVAILABLE)) {
            List<au.org.ala.delta.model.Character> availableCharacters = new ArrayList<au.org.ala.delta.model.Character>(_dataset.getCharacters());
            availableCharacters.removeAll(_specimen.getUsedCharacters());
            return availableCharacters;
        } else if (keyword.equals(CHARACTER_KEYWORD_NONE)) {
            return Collections.EMPTY_LIST;
        } else {
            Set<Integer> characterNumbersSet = _userDefinedCharacterKeywords.get(keyword.toLowerCase());

            // If there is no exact match for the specified keyword text, try
            // and match a single
            // keyword that begins with the text
            if (characterNumbersSet == null) {
                List<String> matches = new ArrayList<String>();
                for (String savedKeyword : _userDefinedCharacterKeywords.keySet()) {

                    // Ignore case when matching keywords
                    String modifiedKeyword = keyword.toLowerCase();
                    String modifiedSavedKeyword = savedKeyword.toLowerCase();

                    // Ignore whitespace characters
                    modifiedKeyword = modifiedKeyword.replaceAll("\\s", "");
                    modifiedSavedKeyword = modifiedSavedKeyword.replaceAll("\\s", "");

                    // Ignore trailing and leading whitespace
                    modifiedKeyword = modifiedKeyword.trim();
                    modifiedSavedKeyword = modifiedSavedKeyword.trim();

                    if (modifiedSavedKeyword.startsWith(modifiedKeyword)) {
                        matches.add(savedKeyword);
                    }
                }

                if (matches.size() == 0) {
                    throw new IllegalArgumentException("Keyword not found");
                } else if (matches.size() == 1) {
                    characterNumbersSet = _userDefinedCharacterKeywords.get(matches.get(0));
                } else {
                    throw new IllegalArgumentException("Keyword ambiguous");
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
    public synchronized List<String> getCharacterKeywords() {
        List<String> retList = new ArrayList<String>();
        retList.add(CHARACTER_KEYWORD_ALL);

        if (_specimen.getUsedCharacters().size() > 0) {
            retList.add(CHARACTER_KEYWORD_USED);
        }

        retList.add(CHARACTER_KEYWORD_AVAILABLE);
        retList.add(CHARACTER_KEYWORD_NONE);
        retList.addAll(_userDefinedCharacterKeywords.keySet());

        return retList;
    }

    public synchronized void addTaxaKeyword(String keyword, Set<Integer> taxaNumbers) {
        if (_dataset == null) {
            throw new IllegalStateException("Cannot define a taxa keyword if no dataset loaded");
        }

        if (keyword.equals(TAXON_KEYWORD_ALL) || keyword.equals(TAXON_KEYWORD_ELIMINATED) || keyword.equals(TAXON_KEYWORD_REMAINING) || keyword.equals(TAXON_KEYWORD_SELECTED)
                || keyword.equals(TAXON_KEYWORD_NONE)) {
            throw new IllegalArgumentException(UIUtils.getResourceString("RedefineSystemKeyword.error", keyword));
        }

        for (int taxonNum : taxaNumbers) {
            if (taxonNum < 1 || taxonNum > _dataset.getNumberOfTaxa()) {
                throw new IllegalArgumentException(String.format("Invalid taxon number %s", taxonNum));
            }
        }

        _userDefinedTaxonKeywords.put(keyword, taxaNumbers);
    }

    public synchronized List<Item> getTaxaForKeyword(String keyword) {
        List<Item> retList = new ArrayList<Item>();

        keyword = keyword.toLowerCase();

        if (keyword.equals(TAXON_KEYWORD_ALL)) {
            return _dataset.getTaxa();
        } else if (keyword.equals(TAXON_KEYWORD_ELIMINATED)) {
            return getEliminatedTaxa();
        } else if (keyword.equals(TAXON_KEYWORD_REMAINING)) {
            return getAvailableTaxa();
        } else if (keyword.equals(TAXON_KEYWORD_SELECTED)) {
            return _appUI.getSelectedTaxa();
        } else if (keyword.equals(TAXON_KEYWORD_NONE)) {
            return Collections.EMPTY_LIST;
        } else {
            // TODO match if supplied text matches the beginning of a taxon name
            Set<Integer> taxaNumbersSet = _userDefinedCharacterKeywords.get(keyword);

            // If there is no exact match for the specified keyword text, try
            // and match a single
            // keyword that begins with the text
            if (taxaNumbersSet == null) {
                List<String> matches = new ArrayList<String>();
                for (String savedKeyword : _userDefinedTaxonKeywords.keySet()) {
                    // Ignore case when matching keywords
                    String modifiedKeyword = keyword.toLowerCase();
                    String modifiedSavedKeyword = savedKeyword.toLowerCase();

                    // Ignore whitespace characters
                    modifiedKeyword = modifiedKeyword.replaceAll("\\s", "");
                    modifiedSavedKeyword = modifiedSavedKeyword.replaceAll("\\s", "");

                    // Ignore trailing and leading whitespace
                    modifiedKeyword = modifiedKeyword.trim();
                    modifiedSavedKeyword = modifiedSavedKeyword.trim();

                    if (modifiedSavedKeyword.startsWith(modifiedKeyword)) {
                        matches.add(savedKeyword);
                    }
                }

                if (matches.size() == 0) {
                    throw new IllegalArgumentException("keyword not found");
                } else if (matches.size() == 1) {
                    taxaNumbersSet = _userDefinedTaxonKeywords.get(matches.get(0));
                } else {
                    throw new IllegalArgumentException("keyword ambiguous");
                }
            }

            for (int taxonNumber : taxaNumbersSet) {
                retList.add(_dataset.getTaxon(taxonNumber));
            }

        }

        Collections.sort(retList);

        return retList;
    }

    public synchronized List<String> getTaxaKeywords() {
        List<String> retList = new ArrayList<String>();
        retList.add(TAXON_KEYWORD_ALL);

        Map<Item, Set<Character>> taxonDifferingCharacters = _specimen.getTaxonDifferences();

        int remainingTaxaCount = 0;

        if (taxonDifferingCharacters == null) {
            remainingTaxaCount = _dataset.getNumberOfTaxa();
        } else {
            for (Item taxon : taxonDifferingCharacters.keySet()) {
                int diffCount = taxonDifferingCharacters.get(taxon).size();
                if (diffCount <= _tolerance) {
                    remainingTaxaCount++;
                }
            }
        }

        if (remainingTaxaCount < _dataset.getNumberOfTaxa()) {
            retList.add(TAXON_KEYWORD_ELIMINATED);
        }

        if (remainingTaxaCount > 0) {
            retList.add(TAXON_KEYWORD_REMAINING);
        }

        retList.add(TAXON_KEYWORD_NONE);

        retList.addAll(_userDefinedTaxonKeywords.keySet());

        return retList;
    }

    /**
     * @return A list of command pattern objects representing all directives
     *         that have been executed
     */
    public synchronized List<IntkeyDirectiveInvocation> getExecutedDirectives() {
        return new ArrayList<IntkeyDirectiveInvocation>(_executedDirectives);
    }

    /**
     * Resets the context state to prepare for a new identification
     */
    public synchronized void restartIdentification() {
        // TODO need to account for fixed characters etc here.

        if (_dataset != null) {

            Specimen oldSpecimen = _specimen;

            // Create a new blank specimen
            _specimen = new Specimen(_dataset, _matchInapplicables, _matchUnknowns, _matchType);

            // Any character values that have been fixed need to be copied into
            // the new specimen
            if (_charactersFixed) {
                for (int characterNumber : _fixedCharactersList) {
                    Character ch = _dataset.getCharacter(characterNumber);
                    _specimen.setValueForCharacter(ch, oldSpecimen.getValueForCharacter(ch));
                }
            }

            // As we are starting from the beginning, best characters must be
            // cleared as they are no longer valid
            _bestOrSeparateCharacters = null;

            _appUI.handleIdentificationRestarted();
        }
    }

    /**
     * @return The current specimen
     */
    public synchronized Specimen getSpecimen() {
        return _specimen;
    }

    /**
     * @return true if an input file is current being processed
     */
    public synchronized boolean isProcessingInputFile() {
        return _processingInputFile;
    }

    /**
     * FOR UNIT TESTING ONLY
     * 
     * @param processing
     */
    public synchronized void setProcessingInputFile(boolean processing) {
        _processingInputFile = processing;
    }

    /**
     * @return The current error tolerance. This is used when determining which
     *         taxa to eliminate following characters being used.
     */
    public synchronized int getTolerance() {
        return _tolerance;
    }

    /**
     * Set the current error tolerance. This is used when determining which taxa
     * to eliminate following characters being used.
     * 
     * @param toleranceValue
     */
    public synchronized void setTolerance(int toleranceValue) {
        _tolerance = toleranceValue;
        if (_dataset != null) {
            updateUI();
        }
    }

    /**
     * @return The current vary weight. This is used by the BEST algorithm
     */
    public synchronized double getVaryWeight() {
        return _varyWeight;
    }

    /**
     * Set the current vary weight. This is used by the BEST algorithm.
     * 
     * @param varyWeight
     *            The current vary weight
     */
    public synchronized void setVaryWeight(double varyWeight) {
        _varyWeight = varyWeight;
    }

    /**
     * Gets the rbase - the base of the logarithmic character-reliability scale,
     * which is used in determining the BEST characters during an identification
     * 
     * @return the current rbase value
     */
    public synchronized double getRBase() {
        return _rbase;
    }

    /**
     * Sets the rbase - the base of the logarithmic character-reliability scale,
     * which is used in determining the BEST characters during an identification
     * 
     * @param rbase
     *            the current rbase value
     */
    public synchronized void setRBase(double rbase) {
        _rbase = rbase;

    }

    public synchronized int getDiagLevel() {
        return _diagLevel;
    }

    public synchronized void setDiagLevel(int diagLevel) {
        this._diagLevel = diagLevel;
    }

    /**
     * @return a reference to the current taxa file, or null if one has not been
     *         set
     */
    public synchronized File getTaxaFile() {
        return _taxaFile;
    }

    /**
     * @return a reference to the current characters file, or null if one has
     *         not been set
     */
    public synchronized File getCharactersFile() {
        return _charactersFile;
    }

    /**
     * @return a reference to the most recently loaded dataset initialization
     *         file, or null if no such files have been loaded
     */
    public synchronized File getDatasetStartupFile() {
        return _datasetStartupFile;
    }

    public synchronized File getDatasetDirectory() {
        if (_initializationFile != null) {
            return _initializationFile.getParentFile();
        } else if (_charactersFile != null) {
            return _charactersFile.getParentFile();
        } else if (_taxaFile != null) {
            return _taxaFile.getParentFile();
        } else {
            return null;
        }
    }

    /**
     * @return The current character order being used to list available
     *         characters in the application
     */
    public synchronized IntkeyCharacterOrder getCharacterOrder() {
        return _characterOrder;
    }

    public synchronized void setCharacterOrderBest() {
        this._characterOrder = IntkeyCharacterOrder.BEST;
        this._taxonToSeparate = -1;
        _bestOrSeparateCharacters = null;
        if (_dataset != null) {
            updateUI();
        }
    }

    public synchronized void setCharacterOrderNatural() {
        this._characterOrder = IntkeyCharacterOrder.NATURAL;
        this._taxonToSeparate = -1;
        if (_dataset != null) {
            updateUI();
        }
    }

    public synchronized void setCharacterOrderSeparate(int taxonToSeparate) {
        this._characterOrder = IntkeyCharacterOrder.SEPARATE;
        this._taxonToSeparate = taxonToSeparate;
        _bestOrSeparateCharacters = null;
        if (_dataset != null) {
            updateUI();
        }
    }

    public synchronized int getTaxonToSeparate() {
        return _taxonToSeparate;
    }

    public synchronized void setTaxonToSeparate(int taxonToSeparate) {
        this._taxonToSeparate = taxonToSeparate;
    }

    /**
     * @return The current best characters if they are cached
     */
    public synchronized LinkedHashMap<Character, Double> getBestOrSeparateCharacters() {
        return _bestOrSeparateCharacters;
    }

    /**
     * Clear the cached best characters. Used to force the UI to recalculate the
     * best characters next time it needs them
     */
    public synchronized void clearBestOrSeparateCharacters() {
        _bestOrSeparateCharacters = null;
    }

    /**
     * Calculates the best characters using the BEST algorithm. This method will
     * block the calling thread while the calculation is performed
     */
    public synchronized void calculateBestOrSeparateCharacters() {
        if (_characterOrder == IntkeyCharacterOrder.BEST) {
            _bestOrSeparateCharacters = SortingUtils.orderBest(IntkeyContext.this);
        } else if (_characterOrder == IntkeyCharacterOrder.SEPARATE) {
            _bestOrSeparateCharacters = SortingUtils.orderSeparate(this, _dataset.getTaxon(_taxonToSeparate));
        }
    }

    public synchronized boolean getMatchInapplicables() {
        return _matchInapplicables;
    }

    public synchronized boolean getMatchUnknowns() {
        return _matchUnknowns;
    }

    public synchronized MatchType getMatchType() {
        return _matchType;
    }

    public synchronized void setMatchSettings(boolean matchUnknowns, boolean matchInapplicables, MatchType matchType) {
        _matchType = matchType;

        // A match type of EXACT implies that inapplicables and unknowns should
        // not be matched.
        if (_matchType == MatchType.EXACT) {
            _matchInapplicables = false;
            _matchUnknowns = false;
        } else {
            _matchUnknowns = matchUnknowns;
            _matchInapplicables = matchInapplicables;
        }

        updateSpecimenMatchSettings();
    }

    // Update the specimen with new match settings. This needs to be called each
    // time the match settings are updated
    // so that the eliminated taxa can be recalculated given the new match
    // settings.
    private void updateSpecimenMatchSettings() {
        Specimen newSpecimen = new Specimen(_dataset, _matchInapplicables, _matchUnknowns, _matchType, _specimen);
        _specimen = newSpecimen;
        updateUI();
    }

    public synchronized DiagType getDiagType() {
        return _diagType;
    }

    public synchronized void setDiagType(DiagType diagType) {
        this._diagType = diagType;
    }

    // Returns included characters ordered by character number
    public synchronized List<Character> getIncludedCharacters() {
        List<Character> retList = new ArrayList<Character>();

        for (int charNum : _includedCharacters) {
            retList.add(_dataset.getCharacter(charNum));
        }

        Collections.sort(retList);

        return retList;
    }

    // Returns included characters ordered by character number
    public synchronized List<Character> getExcludedCharacters() {
        List<Character> excludedCharacters = _dataset.getCharacters();
        excludedCharacters.removeAll(getIncludedCharacters());
        return excludedCharacters;
    }

    // Returns included taxa ordered by taxon number
    public synchronized List<Item> getIncludedTaxa() {
        List<Item> retList = new ArrayList<Item>();

        for (int charNum : _includedTaxa) {
            retList.add(_dataset.getTaxon(charNum));
        }

        Collections.sort(retList);

        return retList;
    }

    // Returns included taxa ordered by taxon number
    public synchronized List<Item> getExcludedTaxa() {
        List<Item> excludedTaxa = _dataset.getTaxa();
        excludedTaxa.removeAll(getIncludedTaxa());
        return excludedTaxa;
    }

    public synchronized void setIncludedCharacters(Set<Integer> includedCharacters) {
        if (includedCharacters == null || includedCharacters.isEmpty()) {
            throw new IllegalArgumentException("Cannot exclude all characters");
        }

        // defensive copy
        _includedCharacters = new HashSet<Integer>(includedCharacters);

        // best characters need to be recalculated to account for characters
        // that
        // have been included/excluded
        _bestOrSeparateCharacters = null;

        if (_dataset != null) {
            updateUI();
        }
    }

    public synchronized void setIncludedTaxa(Set<Integer> includedTaxa) {
        if (includedTaxa == null || includedTaxa.isEmpty()) {
            throw new IllegalArgumentException("Cannot exclude all taxa");
        }

        // defensive copy
        _includedTaxa = new HashSet<Integer>(includedTaxa);

        // best characters need to be recalculated to account for taxa that
        // have been included/excluded
        _bestOrSeparateCharacters = null;

        if (_dataset != null) {
            updateUI();
        }
    }

    // Use all available characters aside from those specified.
    public synchronized void setExcludedCharacters(Set<Integer> excludedCharacters) {
        Set<Integer> includedCharacters = new HashSet<Integer>();
        for (int i = 1; i < _dataset.getNumberOfCharacters() + 1; i++) {
            includedCharacters.add(i);
        }

        includedCharacters.removeAll(excludedCharacters);

        setIncludedCharacters(includedCharacters);
    }

    // Use all available taxa aside from those specified.
    public synchronized void setExcludedTaxa(Set<Integer> excludedTaxa) {
        Set<Integer> includedTaxa = new HashSet<Integer>();
        for (int i = 1; i < _dataset.getNumberOfTaxa() + 1; i++) {
            includedTaxa.add(i);
        }

        includedTaxa.removeAll(excludedTaxa);

        setIncludedTaxa(includedTaxa);
    }

    // The currently included characters minus the characters
    // that have values set in the specimen
    public synchronized List<Character> getAvailableCharacters() {
        List<Character> retList = getIncludedCharacters();

        // Used characters are not available
        retList.removeAll(_specimen.getUsedCharacters());

        // Neither are characters that have been made inapplicable
        retList.removeAll(_specimen.getInapplicableCharacters());

        return retList;
    }

    public synchronized List<Character> getUsedCharacters() {
        return _specimen.getUsedCharacters();
    }

    public synchronized List<Item> getAvailableTaxa() {
        List<Item> availableTaxa = getIncludedTaxa();
        availableTaxa.removeAll(getEliminatedTaxa());
        return availableTaxa;
    }

    public synchronized List<Item> getEliminatedTaxa() {
        Map<Item, Set<Character>> taxaDifferingCharacters = _specimen.getTaxonDifferences();

        List<Item> includedTaxa = getIncludedTaxa();
        List<Item> eliminatedTaxa = new ArrayList<Item>();

        if (taxaDifferingCharacters != null) {
            for (Item taxon : includedTaxa) {
                if (taxaDifferingCharacters.containsKey(taxon)) {

                    // A taxon is eliminated if:
                    // 1. Its value for a character specifed as an "exact"
                    // character does not match
                    // the value set in the specimen
                    // 2. The total number of characters for the taxon whose
                    // values differ to the specimen
                    // is greater than the tolerance setting.
                    Set<Character> taxonDifferingCharacters = taxaDifferingCharacters.get(taxon);

                    boolean nonMatchingExactCharacter = false;
                    for (Character ch : taxonDifferingCharacters) {
                        if (isCharacterExact(ch)) {
                            nonMatchingExactCharacter = true;
                            break;
                        }
                    }

                    if (nonMatchingExactCharacter) {
                        eliminatedTaxa.add(taxon);
                    } else {
                        int diffCount = taxaDifferingCharacters.get(taxon).size();
                        if (diffCount > _tolerance) {
                            eliminatedTaxa.add(taxon);
                        }
                    }
                }
            }
        }

        return eliminatedTaxa;
    }

    public synchronized ImageSettings getImageSettings() {
        ImageSettings imageSettings = new ImageSettings();

        List<FontInfo> overlayFonts = _dataset.getOverlayFonts();

        if (overlayFonts.size() > 0) {
            FontInfo defaultOverlayFontInfo = overlayFonts.get(0);
            imageSettings.setDefaultFontInfo(defaultOverlayFontInfo);
        }

        if (overlayFonts.size() > 1) {
            FontInfo buttonOverlayFontInfo = overlayFonts.get(1);
            imageSettings.setDefaultButtonFontInfo(buttonOverlayFontInfo);
        }

        if (overlayFonts.size() > 2) {
            FontInfo featureOverlayFontInfo = overlayFonts.get(2);
            imageSettings.setDefaultFeatureFontInfo(featureOverlayFontInfo);
        }

        imageSettings.setDataSetPath(getDatasetDirectory().getAbsolutePath());

        imageSettings.setResourcePaths(_imagePathLocations);

        imageSettings.setDatasetName(_dataset.getHeading());

        return imageSettings;
    }

    public synchronized ResourceSettings getInfoSettings() {
        ResourceSettings infoSettings = new ResourceSettings();

        infoSettings.setDataSetPath(getDatasetDirectory().getAbsolutePath());

        infoSettings.setResourcePaths(_infoPathLocations);

        return infoSettings;
    }

    public synchronized void setImagePaths(List<String> imagePaths) {
        _imagePathLocations = new ArrayList<String>(imagePaths);
    }

    public synchronized void setInfoPaths(List<String> infoPaths) {
        _infoPathLocations = new ArrayList<String>(infoPaths);
    }

    public synchronized void addTaxonInformationDialogCommand(String subject, String command) {
        _taxonInformationDialogCommands.add(new Pair<String, String>(subject, command));
    }

    public synchronized List<Pair<String, String>> getTaxonInformationDialogCommands() {
        return new ArrayList<Pair<String, String>>(_taxonInformationDialogCommands);
    }

    private void cleanupOldDataset() {
        if (_dataset != null) {
            _dataset.cleanup();
        }

        if (_startupFileData != null) {
            try {
                FileUtils.deleteDirectory(_startupFileData.getDataFileLocalCopy().getParentFile());
                _startupFileData = null;
            } catch (IOException ex) {
                // do nothing, as we are closing the dataset there is not a lot
                // we can
                // do. The worst that can
                // happen is that files get left in the temporary folder.
            }
        }

        _appUI.handleDatasetClosed();

    }

    /**
     * Called prior to application shutdown.
     */
    public synchronized void cleanupForShutdown() {
        cleanupOldDataset();
        if (_logFileWriter != null) {
            _logFileWriter.flush();
            _logFileWriter.close();
        }

        if (_journalFileWriter != null) {
            _journalFileWriter.flush();
            _journalFileWriter.close();
        }

        if (_currentOutputFileWriter != null) {
            _currentOutputFileWriter.flush();
            _currentOutputFileWriter.close();
        }
    }

    public synchronized IntkeyUI getUI() {
        return _appUI;
    }

    public synchronized StartupFileData getStartupFileData() {
        return _startupFileData;
    }

    public synchronized DirectivePopulator getDirectivePopulator() {
        return _directivePopulator;
    }

    public synchronized boolean charactersFixed() {
        return _charactersFixed;
    }

    public synchronized void setCharactersFixed(boolean charactersFixed) {
        if (charactersFixed != this._charactersFixed) {
            this._charactersFixed = charactersFixed;
            if (_charactersFixed) {
                _fixedCharactersList = new ArrayList<Integer>();
                for (Character usedCharacter : getUsedCharacters()) {
                    _fixedCharactersList.add(usedCharacter.getCharacterId());
                }
            } else {
                _fixedCharactersList = new ArrayList<Integer>();
                restartIdentification();
            }
        }
    }

    /**
     * @return A list of character ids for the characters that have been fixed,
     *         or null if characters have not been fixed.
     */
    public synchronized List<Integer> getFixedCharactersList() {
        return _fixedCharactersList;
    }

    public synchronized boolean isAutoTolerance() {
        return _autoTolerance;
    }

    public synchronized void setAutoTolerance(boolean autoTolerance) {
        this._autoTolerance = autoTolerance;
    }

    public synchronized Set<Character> getExactCharacters() {
        Set<Character> exactCharacters = new HashSet<Character>();
        for (int charNum : _exactCharactersSet) {
            exactCharacters.add(_dataset.getCharacter(charNum));
        }
        return exactCharacters;
    }

    public synchronized void setExactCharacters(Set<Integer> characters) {
        _exactCharactersSet = new HashSet<Integer>(characters);
    }

    private boolean isCharacterExact(Character ch) {
        return _exactCharactersSet.contains(ch.getCharacterId());
    }

    public synchronized int getStopBest() {
        return _stopBest;
    }

    public synchronized void setStopBest(int stopBest) {
        this._stopBest = stopBest;
    }

    public synchronized boolean displayNumbering() {
        return _displayNumbering;
    }

    public synchronized void setDisplayNumbering(boolean displayNumbering) {
        this._displayNumbering = displayNumbering;
        updateUI();
    }

    public synchronized boolean displayInapplicables() {
        return _displayInapplicables;
    }

    public synchronized void setDisplayInapplicables(boolean displayInapplicables) {
        this._displayInapplicables = displayInapplicables;
    }

    public synchronized boolean displayUnknowns() {
        return _displayUnknowns;
    }

    public synchronized void setDisplayUnknowns(boolean displayUnknowns) {
        this._displayUnknowns = displayUnknowns;
    }

    public synchronized boolean displayComments() {
        return _displayComments;
    }

    public synchronized void setDisplayComments(boolean displayComments) {
        this._displayComments = displayComments;
        updateUI();
    }

    public synchronized boolean displayContinuous() {
        return _displayContinuous;
    }

    public synchronized void setDisplayContinuous(boolean displayContinuous) {
        this._displayContinuous = displayContinuous;
    }

    public synchronized ImageDisplayMode getImageDisplayMode() {
        return _displayImagesMode;
    }

    public synchronized void setImageDisplayMode(ImageDisplayMode imageDisplayMode) {
        this._displayImagesMode = imageDisplayMode;
    }

    public synchronized boolean displayKeywords() {
        return _displayKeywords;
    }

    public synchronized void setDisplayKeywords(boolean displayKeywords) {
        this._displayKeywords = displayKeywords;
    }

    public synchronized void setLogFile(File logFile) throws IOException {
        if (_logFileWriter != null) {
            _logFileWriter.flush();
            _logFileWriter.close();
        }

        _logFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
        _logFile = logFile;

        _logFileWriter.append(_logCache.toString());
    }

    public synchronized void setJournalFile(File journalFile) throws IOException {
        if (_journalFileWriter != null) {
            _journalFileWriter.flush();
            _journalFileWriter.close();
        }

        _journalFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(journalFile)));
        _journalFile = journalFile;

        _journalFileWriter.append(_journalCache.toString());
    }

    public synchronized void newOutputFile(File outputFile) throws IOException {
        if (_currentOutputFile != null && !_currentOutputFile.equals(outputFile)) {
            if (_currentOutputFile != null) {
                closeOutputFile(_currentOutputFile);
            }
        }

        _currentOutputFile = outputFile;
        _currentOutputFileWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
    }

    public synchronized void closeOutputFile(File outputFile) {
        if (_currentOutputFile != null && _currentOutputFile.equals(outputFile)) {
            _currentOutputFileWriter.flush();
            _currentOutputFileWriter.close();
            _currentOutputFile = null;
        }
    }

    public synchronized File getJournalFile() {
        return _journalFile;
    }

    public synchronized File getLogFile() {
        return _logFile;
    }

    public synchronized File getOutputFile() {
        return _currentOutputFile;
    }

    public synchronized List<File> getOutputFiles() {
        // List<File> fileList = new
        // ArrayList<File>(_outputFileWriters.keySet());
        // Collections.sort(fileList);
        // return fileList;
        return null;
    }

    /**
     * Appends the supplied text to the current output file
     * 
     * @param text
     */
    public synchronized void appendToOutputFile(String text) {
        if (_currentOutputFile == null) {
            throw new IllegalStateException("No output file is open");
        }

        _currentOutputFileWriter.println(text);
        _currentOutputFileWriter.flush();
    }

    /**
     * Appends the supplied text to the log
     * 
     * @param text
     */
    public synchronized void appendToLog(String text) {
        if (_logFileWriter != null) {
            _logFileWriter.println(text);
            _logFileWriter.flush();
        }

        _logCache.append(text);
        _logCache.append("\n");

        _appUI.updateLog();
    }

    public synchronized void appendToJournal(String text) {
        if (_journalFileWriter != null) {
            _journalFileWriter.println(text);
            _journalFileWriter.flush();
        }

        _journalCache.append(text);
        _journalCache.append("\n");
    }

    public synchronized String getLogText() {
        return _logCache.toString();
    }

    private void updateUI() {
        if (!_processingInputFile) {
            _appUI.handleUpdateAll();
        }
    }

    private class StartupFileLoader extends SwingWorker<Void, String> {

        private File _startupFile;

        public StartupFileLoader(File startupFile) {
            _startupFile = startupFile;
        }

        @Override
        protected Void doInBackground() throws Exception {
            publish("Loading dataset...");

            URL inkFileLocation = null;
            URL dataFileLocation = null;
            String initializationFileLocation = null;
            String imagePath = null;
            String infoPath = null;

            BufferedReader reader = new BufferedReader(new FileReader(_startupFile));
            System.out.println(_startupFile.getAbsolutePath());

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("=");

                if (tokens.length == 2) {
                    String keyword = tokens[0];
                    String value = tokens[1];

                    if (keyword.equals(Constants.INIT_FILE_INK_FILE_KEYWORD)) {
                        // Datasets saved with the old implementation of Intkey
                        // used simple file paths
                        // for startup files that were saved to disk. Check if
                        // this is the format that is
                        // used before attempting to read as a URL.
                        if (value.equals(_startupFile.getAbsolutePath())) {
                            inkFileLocation = _startupFile.toURI().toURL();
                        } else {
                            inkFileLocation = new URL(value);
                        }
                    } else if (keyword.equals(Constants.INIT_FILE_DATA_FILE_KEYWORD)) {
                        dataFileLocation = new URL(value);
                    } else if (keyword.equals(Constants.INIT_FILE_INITIALIZATION_FILE_KEYWORD)) {
                        initializationFileLocation = value;
                    } else if (keyword.equals(Constants.INIT_FILE_IMAGE_PATH_KEYWORD)) {
                        imagePath = value;
                    } else if (keyword.equals(Constants.INIT_FILE_INFO_PATH_KEYWORD)) {
                        infoPath = value;
                    }
                }
            }

            if (inkFileLocation != null && initializationFileLocation != null && dataFileLocation != null) {
                File tempDir = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString());
                tempDir.mkdir();

                String[] dataFileLocationTokens = dataFileLocation.toString().split("/");
                String dataFileName = dataFileLocationTokens[dataFileLocationTokens.length - 1];
                File localDataFile = new File(tempDir, dataFileName);

                // If the ink file location points to a local file, the dataset
                // has been saved locally. Look for the
                // zipped data file in the same directory as the ink file.
                boolean savedDatasetOpened = false;
                if (inkFileLocation.getProtocol().equals("file")) {
                    File savedInkFile = new File(inkFileLocation.toURI());
                    if (savedInkFile.exists()) {
                        File saveDirectory = savedInkFile.getParentFile();
                        File savedDataFile = new File(saveDirectory, dataFileName);
                        if (savedDataFile.exists()) {
                            FileUtils.copyFile(savedDataFile, localDataFile);
                            savedDatasetOpened = true;
                        }
                    }
                }

                if (!savedDatasetOpened) {
                    // Data set is hosted remotely. Download it.
                    publish("downloading dataset from " + dataFileLocation.toString());
                    FileUtils.copyURLToFile(dataFileLocation, localDataFile, 10000, 10000);
                }

                Utils.extractZipFile(localDataFile, tempDir);

                StartupFileData startupFileData = new StartupFileData();
                startupFileData.setInkFileLocation(inkFileLocation);
                startupFileData.setDataFileLocation(dataFileLocation);
                startupFileData.setInitializationFileLocation(initializationFileLocation);
                startupFileData.setDataFileLocalCopy(localDataFile);
                startupFileData.setInitializationFileLocalCopy(new File(tempDir, initializationFileLocation));
                startupFileData.setImagePath(imagePath);
                startupFileData.setInfoPath(infoPath);
                startupFileData.setRemoteDataset(!savedDatasetOpened);

                _startupFileData = startupFileData;
                processInitializationFile(_startupFileData.getInitializationFileLocalCopy());

                if (startupFileData.getImagePath() != null) {
                    setImagePaths(Arrays.asList(_startupFileData.getImagePath()));
                }

                if (startupFileData.getInfoPath() != null) {
                    setInfoPaths(Arrays.asList(_startupFileData.getInfoPath()));
                }
            } else {
                processInitializationFile(_startupFile);
            }

            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            String lastProgressMessage = chunks.get(chunks.size() - 1);
            _appUI.displayBusyMessage(lastProgressMessage);
        }

        @Override
        protected void done() {
            try {
                _appUI.removeBusyMessage();

                // Need to call get() so that we can handle any exceptions that
                // occurred on the other thread.
                get();

                if (_dataset != null) {
                    appendToLog(_dataset.getHeading());
                    appendToLog(_dataset.getSubHeading());
                    _appUI.handleNewDataset(_dataset);
                }
            } catch (Exception e) {
                _appUI.displayErrorMessage("Error reading dataset file " + _startupFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

}
