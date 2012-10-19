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
package au.org.ala.delta.intkey.model;

import au.org.ala.delta.Logger;
import au.org.ala.delta.best.Best;
import au.org.ala.delta.best.DiagType;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.intkey.LongRunningDirectiveSwingWorker;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParseException;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParser;
import au.org.ala.delta.intkey.directives.SetMatchDirective;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocationException;
import au.org.ala.delta.intkey.directives.invocation.LongRunningIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.UseDirectiveInvocation;
import au.org.ala.delta.intkey.ui.DirectivePopulator;
import au.org.ala.delta.intkey.ui.IntKeyDialogController;
import au.org.ala.delta.intkey.ui.IntkeyUI;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.ResourceSettings;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
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

/**
 * Model. Maintains global application state.
 * 
 * @author Chris
 * 
 */
public class IntkeyContext extends AbstractDeltaContext {

    /** Pre-defined character keywords */
    public static final String CHARACTER_KEYWORD_ALL = "all";
    public static final String CHARACTER_KEYWORD_USED = "used";
    public static final String CHARACTER_KEYWORD_AVAILABLE = "available";
    public static final String CHARACTER_KEYWORD_NONE = "none";
    public static final String CHARACTER_KEYWORD_SELECTED = "selected";

    /** Pre-defined taxon keywords */
    public static final String TAXON_KEYWORD_ALL = "all";
    public static final String TAXON_KEYWORD_ELIMINATED = "eliminated";
    public static final String TAXON_KEYWORD_REMAINING = "remaining";
    public static final String TAXON_KEYWORD_NONE = "none";
    public static final String TAXON_KEYWORD_SELECTED = "selected";

    /** Pre-defined keyword for the specimen */
    public static final String SPECIMEN_KEYWORD = "specimen";

    /** Width of output files - including log and journal files */
    public static final int OUTPUT_FILE_WIDTH = 80;

    /** The taxa file associated with the dataset. This is usually called iitems */
    private File _taxaFile;

    /**
     * The characters file associated with the dataset. This is usually called
     * ichars
     */
    private File _charactersFile;

    /** The currently-loaded dataset */
    private IntkeyDataset _dataset;

    /**
     * A URL pointing to the dataset startup file. If the dataset is located on
     * disk, this will be a file URL
     */
    private URL _datasetStartupURL;

    /**
     * A .ink file used to load a new dataset. May be a jnlp style file
     * specifying where data is to be downloaded from. Or may be a directives
     * file that is run to load the dataset in intkey, initialize values etc. In
     * the latter case, this value will be the same as _initializationFile (see
     * below).
     * 
     * If _datasetStartupURL points to remote content (i.e. it is not a file
     * URL), this will be a temporary file used to save the remote content
     * referenced by the URL.
     */
    private File _datasetStartupFile;

    /**
     * A directives file that is run to load the dataset in intkey, initialize
     * values etc. Typically a .ini file, but can also have extension .ink.
     */
    private File _initializationFile;

    /**
     * A directives file that sets preferred parameter settings. The directives
     * in this file are executed every time a new dataset is loaded.
     */
    private File _preferencesFile;

    private StartupFileData _startupFileData;

    private IntkeyDirectiveParser _directiveParser;

    /**
     * Is a file containing directive calls currently being processed?
     */
    private boolean _processingDirectivesFile;

    /**
     * Is an input file - a file with directive calls as specified by the FILE
     * INPUT directive currently being processed?
     */
    private boolean _processingInputFile;

    private List<IntkeyDirectiveInvocation> _executedDirectives;

    /**
     * The Intkey UI
     */
    private IntkeyUI _appUI;

    /**
     * Handles user prompts for input when handling directives
     */
    private DirectivePopulator _directivePopulator;

    /**
     * The specimen. Holds character values set by the user pertaining to the
     * current investigation
     */
    private Specimen _specimen;

    /** values set by SET directives */
    private boolean _autoTolerance;
    private int _diagLevel;
    private DiagType _diagType;
    private boolean _charactersFixed;
    private List<Integer> _fixedCharactersList;
    private Set<Integer> _exactCharactersSet;
    private List<String> _imagePathLocations;
    private List<String> _infoPathLocations;
    private boolean _matchInapplicables;
    private boolean _matchUnknowns;
    private MatchType _matchType;
    private double _rbase;
    private int _stopBest;
    private int _tolerance;
    private double _varyWeight;

    private boolean _demonstrationMode;

    /**
     * A saved version of the values set for the SET, INCLUDE and DISPLAY
     * directives at the time that SET DEMONSTRATION was set to ON. While SET
     * DEMONSTRATION is on, the IntkeyContext is reverted back to these baseline
     * settings every time the investigation is restarted (RESTART directive)
     */
    private DemonstrationModeSettings _demonstrationModeSettings;

    // values set by DISPLAY directives
    private boolean _displayNumbering;
    private boolean _displayInapplicables;
    private boolean _displayUnknowns;
    private boolean _displayComments;
    private boolean _displayContinuous;
    private ImageDisplayMode _displayImagesMode;
    private boolean _displayKeywords;
    private boolean _displayScaled;
    private boolean _displayEndIdentify;
    private boolean _displayInput;

    /** Ordering type to use when sorting characters for display */
    private IntkeyCharacterOrder _characterOrder;

    /** The taxon to be separated when using the SEPARATE character order. */
    private int _taxonToSeparate;

    /**
     * The BEST characters, or the characters used to SEPARATE a taxon, along
     * with the weight of each character as determined by the BEST algorithm. A
     * linked hash map is used to maintain character ordering.
     */
    private LinkedHashMap<Character, Double> _bestOrSeparateCharacters;

    /** The set of currently included characters */
    private Set<Integer> _includedCharacters;

    /** The set of currently included taxa */
    private Set<Integer> _includedTaxa;

    private List<Pair<String, String>> _taxonInformationDialogCommands;

    /**
     * The user defined character keywords, along with the characters associated
     * with each keyword. A linked hashmap is used so that the keys list will be
     * returned in order of insertion.
     */
    private LinkedHashMap<String, Set<Integer>> _userDefinedCharacterKeywords;

    /**
     * The user defined taxon keywords, along with the characters associated
     * with each keyword. A linked hashmap is used so that the keys list will be
     * returned in order of insertion.
     */
    private LinkedHashMap<String, Set<Integer>> _userDefinedTaxonKeywords;

    /**
     * A list of directive commands to run when a taxon is identified (i.e. 1
     * taxon remains during an investigation) and _displayEndIdentify is true
     */
    private List<String> _endIdentifyCommands;

    /**
     * Image subjects. The words or phrases are placed in the "subjects" list
     * box in the "select multiple images" dialog box. The images displayed are
     * then restricted to those whose "subjects" contain any of the words of
     * phrases selected in the list box
     */
    private List<String> _imageSubjects;

    /**
     * True if the last line output to an output file a comment, output via the
     * OUTPUT COMMENT directive
     */
    private boolean _lastOutputLineWasComment;

    /**
     * The log file
     */
    private File _logFile;

    /**
     * The journal file
     */
    private File _journalFile;

    /**
     * PrintFile wrapper for log file
     */
    private PrintFile _logPrintFile;

    /**
     * PrintFile wrapper for the journal file
     */
    private PrintFile _journalPrintFile;

    /**
     * The current output file. Intkey can only write to a single output file at
     * a time, so need to keep track of the current file
     */
    private File _currentOutputFile;

    /**
     * PrintFile wrapper for the current output file
     */
    private PrintFile _currentOutputPrintFile;

    /**
     * Cache of all lines output to log files
     */
    private List<String> _logCache;

    /**
     * Cache of all lines output to journal files
     */
    private List<String> _journalCache;

    /**
     * The directory used to cache images and files downloaded from remote
     * locations on the imagepath and infopath
     */
    private File _fileCacheDirectory;

    /**
     * Constructor
     * 
     * @param appUI
     *            A reference to the main Intkey UI
     * @param directivePopulator
     *            A reference to the directive populator
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
        _processingDirectivesFile = false;
        _processingInputFile = false;

        _directiveParser = IntkeyDirectiveParser.createInstance();
        _logCache = new ArrayList<String>();
        _journalCache = new ArrayList<String>();
        _characterOrder = IntkeyCharacterOrder.BEST;
        _displayImagesMode = ImageDisplayMode.AUTO;
        _displayEndIdentify = true;

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
        _displayScaled = true;
        _displayScaled = true;

        _endIdentifyCommands = new ArrayList<String>();
        _imageSubjects = new ArrayList<String>();
    }

    /**
     * Set the preferences file
     * 
     * @param preferencesFile
     *            the preferences file
     */
    public synchronized void setPreferencesFile(File preferencesFile) {
        _preferencesFile = preferencesFile;
    }

    /**
     * Execute the directives in the preferences file, if a preferences file has
     * been set.
     */
    public synchronized void executePreferencesFileDirectives() {
        if (_preferencesFile != null) {
            processDirectivesFile(_preferencesFile);
            updateUI();
        }
    }

    /**
     * Process the directives a file supplied via the FILE INPUT directive
     * 
     * @param directivesFile
     *            the directives file
     */
    public synchronized void processInputFile(File directivesFile) {
        // Keep track of old value for _processingInputFile. There could
        // be a case where there is a call to FILE INPUT inside a
        // preferences file itself. Unlikely, but it could happen
        boolean oldProcessingInputFile = _processingInputFile;
        _processingInputFile = true;
        processDirectivesFile(directivesFile);
        _processingInputFile = oldProcessingInputFile;
        updateUI();
    }

    /**
     * Set the current characters file. If a new taxa file has previously been
     * set, calling this method will result in the new dataset being loaded. The
     * calling thread will block while the dataset is loaded.
     * 
     * @param charactersFile
     *            The characters file
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
                _dataset.close();
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
     * @param taxaFile
     *            The taxa (items) file
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
                _dataset.close();
            }

            _dataset = null;
            _charactersFile = null;
        }
    }

    /**
     * Process the directives in the supplied file
     * 
     * @param directivesFile
     *            The directives file
     */
    private synchronized void processDirectivesFile(File directivesFile) {
        Logger.log("Reading in directives from file: %s", directivesFile.getAbsolutePath());

        if (directivesFile == null || !directivesFile.exists()) {
            throw new IllegalArgumentException("Could not open input file " + directivesFile.getAbsolutePath());
        }

        // May be in several levels of input file, so need to ensure we set this
        // back to the correct value.
        boolean oldProcessingInputFile = _processingDirectivesFile;
        _processingDirectivesFile = true;

        IntkeyDirectiveParser parser = IntkeyDirectiveParser.createInstance();

        try {
            parser.parse(directivesFile, IntkeyContext.this);
        } catch (Throwable th) {
            Logger.log(th.getMessage());
            _appUI.displayErrorMessage(UIUtils.getResourceString("ErrorProcessingDirectivesFile.error", directivesFile.getAbsolutePath(), th.getMessage()));
        }

        _processingDirectivesFile = oldProcessingInputFile;
    }

    /**
     * Called to read the characters and taxa files and create a new dataset.
     * This method will block while the dataset information is read from the
     * character and taxa files
     */
    private void createNewDataSet() {
        _dataset = IntkeyDatasetFileReader.readDataSet(_charactersFile, _taxaFile);

        _specimen = new Specimen(_dataset, false, _matchInapplicables, _matchInapplicables, _matchType);

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
        if (!_processingDirectivesFile) {
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
     * @param datasetFileURL
     *            The dataset initialization file
     * @return SwingWorker used to load the dataset in a separate thread - unit
     *         tests need this so that they can block until the dataset is
     *         loaded.
     */
    public synchronized void newDataSetFile(final URL datasetFileURL) {
        Logger.log("Reading in directives from url: %s", datasetFileURL.toString());

        // Close any dialogs that have been left open.
        IntKeyDialogController.closeWindows();

        cleanupOldDataset();

        initializeIdentification();

        // Loading of a new dataset can take a long time and hence can lock up
        // the UI. If this method is called from the Swing Event Dispatch
        // Thread, load the
        // new dataset on a background thread using a SwingWorker.
        if (SwingUtilities.isEventDispatchThread()) {
            _appUI.displayBusyMessage(UIUtils.getResourceString("LoadingDataset.caption"));
            SwingWorker<Void, Void> startupWorker = new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    processStartupFile(datasetFileURL);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        appendToLog(_dataset.getHeading());
                        appendToLog(_dataset.getSubHeading());
                        _appUI.handleNewDataset(_dataset);
                    } catch (Exception ex) {
                        Logger.error("Error reading dataset file", ex);
                        _appUI.displayErrorMessage(UIUtils.getResourceString("ErrorReadingReadsetFile.error", datasetFileURL.toString(), ex.getMessage()));
                    } finally {
                        _appUI.removeBusyMessage();
                    }
                }
            };

            startupWorker.execute();
        } else {
            try {
                processStartupFile(datasetFileURL);
                appendToLog(_dataset.getHeading());
                appendToLog(_dataset.getSubHeading());
                _appUI.handleNewDataset(_dataset);
            } catch (Exception ex) {
                Logger.error("Error reading dataset file", ex);
                _appUI.displayErrorMessage(UIUtils.getResourceString("ErrorReadingReadsetFile.error", datasetFileURL.toString(), ex.getMessage()));
            }
        }
    }

    /**
     * Process the directives in a dataset initialization file (typically will
     * have extension .ink or .ini)
     * 
     * @param initializationFile
     *            The initialization file
     */
    private void processInitializationFile(File initializationFile) {
        _initializationFile = initializationFile;
        processDirectivesFile(initializationFile);
        executePreferencesFileDirectives();
    }

    /**
     * Parse and execute a directive command
     * 
     * @param command
     *            The directive command
     */
    public synchronized void parseAndExecuteDirective(String command) {
        try {
            _directiveParser.parse(new StringReader(command), this);
        } catch (Throwable th) {
            String msg;
            if (th instanceof IntkeyDirectiveParseException) {
                msg = th.getMessage();
            } else {
                msg = UIUtils.getResourceString("ErrorWhileProcessingCommand.error", command.toUpperCase(), th.getMessage());
            }
            _appUI.displayErrorMessage(msg);
            Logger.error(msg);
            Logger.error(th);
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
        int executedDirectivesIndex = _executedDirectives.size();

        // Record correct insertion index for the directive call here so that if
        // unsuccessful the directive call can be removed from the log. It is
        // necessary to add the directive call to the log here to ensure that it
        // appears above any subsequent status messages in the log.
        int logInsertionIndex = _logCache.size();

        // The use directive is a special case. The use directive logic handles
        // appending to the log itself.
        if (!(invoc instanceof UseDirectiveInvocation)) {
            if (!_processingDirectivesFile || (_processingInputFile && _displayInput)) {
                appendToLog("*" + invoc.toString());
            }
        }

        // If this is a long running directive and we are on the event dispatch
        // thread, run the task in the
        // background using a SwingWorker.
        if (invoc instanceof LongRunningIntkeyDirectiveInvocation && SwingUtilities.isEventDispatchThread()) {
            LongRunningIntkeyDirectiveInvocation<?> longInvoc = (LongRunningIntkeyDirectiveInvocation<?>) invoc;
            LongRunningDirectiveSwingWorker worker = new LongRunningDirectiveSwingWorker(longInvoc, this, _appUI, executedDirectivesIndex, logInsertionIndex);
            worker.execute();
        } else {
            try {
                boolean success = invoc.execute(this);
                if (success) {
                    handleDirectiveExecutionComplete(invoc, executedDirectivesIndex);
                } else {
                    handleDirectiveExecutionFailed(invoc, logInsertionIndex);
                }
            } catch (IntkeyDirectiveInvocationException ex) {
                _appUI.displayErrorMessage(ex.getMessage());
            }
        }
    }

    /**
     * Called when a directive has successfully been executed
     * 
     * @param invoc
     *            Command pattern object representing the executed directive
     * @param executedDirectivesIndex
     *            index in which to insert the executed directive in the list of
     *            executed directives
     */
    public void handleDirectiveExecutionComplete(IntkeyDirectiveInvocation invoc, int executedDirectivesIndex) {
        // Omit directive calls from the log and journal if a directives
        // file is being processes, except in the case
        // that the file is an "input" file as specified by FILE INPUT, and
        // DISPLAY INPUT is set to ON.
        if (!_processingDirectivesFile || (_processingInputFile && _displayInput)) {
            if (_executedDirectives.size() < executedDirectivesIndex) {
                // executed directives list has been cleared, just add this
                // directive to the end of the list
                _executedDirectives.add(invoc);
            } else {
                _executedDirectives.add(executedDirectivesIndex, invoc);
            }
            appendToJournal("*" + invoc.toString());
        }
    }

    /**
     * Called when execution of a directive failed.
     * 
     * @param invoc
     *            Command pattern object representing the executed directive
     * @param logInsertionIndex
     *            The in the log at which the directive was recorded.
     */
    public void handleDirectiveExecutionFailed(IntkeyDirectiveInvocation invoc, int logInsertionIndex) {
        // The directive failed so remove the directive call from the log. The
        // use case is a special case. The use directive logic handles appending
        // to
        // the log itself.
        if (!(invoc instanceof UseDirectiveInvocation)) {
            _logCache.remove(logInsertionIndex);
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
     * @param attribute
     *            the character value
     */
    public synchronized void setSpecimenAttributeForCharacter(au.org.ala.delta.model.Character ch, Attribute attribute) {
        Logger.log("Using character");
        _specimen.setAttributeForCharacter(ch, attribute);
    }

    /**
     * Remove the value for a character from the current specimen
     * 
     * @param ch
     */
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

        // write remaining number of taxa to the log
        int numAvailableTaxa = getAvailableTaxa().size();
        if (numAvailableTaxa == 1) {
            appendToLog(UIUtils.getResourceString("OneTaxonRemains.log"));
        } else {
            appendToLog(UIUtils.getResourceString("MultipleTaxaRemain.log", numAvailableTaxa));
        }

        // If a taxon has been identified, run commands specified by the DEFINE
        // ENDIDENTIFY directive. Only do this
        // if DISPLAY ENDIDENTIFY is set to ON.
        if (_displayEndIdentify && getAvailableTaxa().size() == 1) {
            executeEndIdentifyCommands();
        }
    }

    /**
     * Add, modify or remove a character keyword
     * 
     * @param keyword
     *            The keyword. Note that the system-defined keywords "all",
     *            "used", "available" and "none" cannot be used.
     * @param characterNumbers
     *            The set of characters to be represented by the keyword. If
     *            empty, the specified keyword will be removed. Otherwise the
     *            keyword will be added, modified to point to the specified
     *            characters
     */
    public synchronized void setCharacterKeyword(String keyword, Set<Integer> characterNumbers) {
        if (_dataset == null) {
            throw new IllegalStateException("Cannot define a character keyword if no dataset loaded");
        }

        keyword = keyword.toLowerCase();
        if (keyword.equals(CHARACTER_KEYWORD_ALL) || keyword.equals(CHARACTER_KEYWORD_USED) || keyword.equals(CHARACTER_KEYWORD_AVAILABLE) || keyword.equals(CHARACTER_KEYWORD_NONE)
                || keyword.equals(CHARACTER_KEYWORD_SELECTED)) {
            throw new IllegalArgumentException(UIUtils.getResourceString("RedefineSystemKeyword.error", keyword));
        }

        if (characterNumbers.isEmpty()) {
            _userDefinedCharacterKeywords.remove(keyword);
            appendToLog(UIUtils.getResourceString("KeywordDeleted.log"));
        } else {
            for (int chNum : characterNumbers) {
                if (chNum < 1 || chNum > _dataset.getNumberOfCharacters()) {
                    throw new IllegalArgumentException(String.format("Invalid character number %s", chNum));
                }
            }
            _userDefinedCharacterKeywords.put(keyword, characterNumbers);
        }
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
            return new ArrayList<au.org.ala.delta.model.Character>(_dataset.getCharactersAsList());
        } else if (keyword.equals(CHARACTER_KEYWORD_USED)) {
            return _specimen.getUsedCharacters();
        } else if (keyword.equals(CHARACTER_KEYWORD_AVAILABLE)) {
            List<au.org.ala.delta.model.Character> availableCharacters = new ArrayList<au.org.ala.delta.model.Character>(_dataset.getCharactersAsList());
            availableCharacters.removeAll(_specimen.getUsedCharacters());
            return availableCharacters;
        } else if (keyword.equals(CHARACTER_KEYWORD_SELECTED)) {
            return _appUI.getSelectedCharacters();
        } else if (keyword.equals(CHARACTER_KEYWORD_NONE)) {
            return Collections.EMPTY_LIST;
        } else {
            Set<Integer> characterNumbersSet = _userDefinedCharacterKeywords.get(keyword.toLowerCase());

            // If there is no exact match for the specified keyword text, try
            // and match a single
            // keyword that begins with the text
            if (characterNumbersSet == null) {
                List<String> matches = new ArrayList<String>();

                if (CHARACTER_KEYWORD_ALL.startsWith(keyword)) {
                    matches.add(CHARACTER_KEYWORD_ALL);
                }

                if (CHARACTER_KEYWORD_ALL.startsWith(keyword)) {
                    matches.add(CHARACTER_KEYWORD_USED);
                }

                if (CHARACTER_KEYWORD_AVAILABLE.startsWith(keyword)) {
                    matches.add(CHARACTER_KEYWORD_AVAILABLE);
                }

                if (CHARACTER_KEYWORD_NONE.startsWith(keyword)) {
                    matches.add(CHARACTER_KEYWORD_NONE);
                }

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
                    return getCharactersForKeyword(matches.get(0));
                } else {
                    throw new IllegalArgumentException("Keyword ambiguous");
                }
            } else {
                List<au.org.ala.delta.model.Character> retList = new ArrayList<au.org.ala.delta.model.Character>();
                for (int charNum : characterNumbersSet) {
                    retList.add(_dataset.getCharacter(charNum));
                }
                Collections.sort(retList);
                return retList;
            }
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

        // Include "Used" keyword if there are any used characters
        if (_specimen.getUsedCharacters().size() > 0) {
            retList.add(CHARACTER_KEYWORD_USED);
        }

        // Include "selected" keyword if there are any selected characters
        if (_appUI != null && !_appUI.getSelectedCharacters().isEmpty()) {
            retList.add(CHARACTER_KEYWORD_SELECTED);
        }

        retList.add(CHARACTER_KEYWORD_AVAILABLE);
        retList.add(CHARACTER_KEYWORD_NONE);
        retList.addAll(_userDefinedCharacterKeywords.keySet());

        return retList;
    }

    /**
     * Add, modify or remove a taxa keyword
     * 
     * @param keyword
     *            The keyword. Note that the system-defined keywords "all",
     *            "eliminated", "remaining", "selected", "none" and "specimen"
     *            cannot be used.
     * @param taxaNumbers
     *            The set of taxa to be represented by the keyword. If empty,
     *            the specified keyword will be removed. Otherwise the keyword
     *            will be added, modified to point to the specified taxa
     */
    public synchronized void setTaxaKeyword(String keyword, Set<Integer> taxaNumbers) {
        if (_dataset == null) {
            throw new IllegalStateException("Cannot define a taxa keyword if no dataset loaded");
        }

        keyword = keyword.toLowerCase();
        if (keyword.equals(TAXON_KEYWORD_ALL) || keyword.equals(TAXON_KEYWORD_ELIMINATED) || keyword.equals(TAXON_KEYWORD_REMAINING) || keyword.equals(TAXON_KEYWORD_SELECTED)
                || keyword.equals(TAXON_KEYWORD_NONE) || keyword.equals(SPECIMEN_KEYWORD)) {
            throw new IllegalArgumentException(UIUtils.getResourceString("RedefineSystemKeyword.error", keyword));
        }

        for (int taxonNum : taxaNumbers) {
            if (taxonNum < 1 || taxonNum > _dataset.getNumberOfTaxa()) {
                throw new IllegalArgumentException(String.format("Invalid taxon number %s", taxonNum));
            }
        }

        _userDefinedTaxonKeywords.put(keyword, taxaNumbers);
    }

    /**
     * Get the list of taxa that are represented by the supplied keyword
     * 
     * @param keyword
     *            the keyword.
     * @return the list of taxa that are represented by the supplied keyword
     */
    public synchronized List<Item> getTaxaForKeyword(String keyword) {
        List<Item> retList = new ArrayList<Item>();

        keyword = keyword.toLowerCase();

        if (keyword.equals(TAXON_KEYWORD_ALL)) {
            return _dataset.getItemsAsList();
        } else if (keyword.equals(TAXON_KEYWORD_ELIMINATED)) {
            return getEliminatedTaxa();
        } else if (keyword.equals(TAXON_KEYWORD_REMAINING)) {
            return getAvailableTaxa();
        } else if (keyword.equals(TAXON_KEYWORD_SELECTED)) {
            return _appUI.getSelectedTaxa();
        } else if (keyword.equals(TAXON_KEYWORD_NONE)) {
            return Collections.EMPTY_LIST;
        } else {
            Set<Integer> taxaNumbersSet = _userDefinedTaxonKeywords.get(keyword);

            // If there is no exact match for the specified keyword text, try
            // and match a single
            // keyword that begins with the text
            if (taxaNumbersSet == null) {
                List<String> matches = new ArrayList<String>();

                if (TAXON_KEYWORD_ALL.startsWith(keyword)) {
                    matches.add(TAXON_KEYWORD_ALL);
                }

                if (TAXON_KEYWORD_ELIMINATED.startsWith(keyword)) {
                    matches.add(TAXON_KEYWORD_ELIMINATED);
                }

                if (TAXON_KEYWORD_REMAINING.startsWith(keyword)) {
                    matches.add(TAXON_KEYWORD_REMAINING);
                }

                if (TAXON_KEYWORD_SELECTED.startsWith(keyword)) {
                    matches.add(TAXON_KEYWORD_SELECTED);
                }

                if (TAXON_KEYWORD_NONE.startsWith(keyword)) {
                    matches.add(TAXON_KEYWORD_NONE);
                }

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
                    return getTaxaForKeyword(matches.get(0));
                } else {
                    throw new IllegalArgumentException("keyword ambiguous");
                }
            } else {
                for (int taxonNumber : taxaNumbersSet) {
                    retList.add(_dataset.getItem(taxonNumber));
                }
            }
        }

        Collections.sort(retList);

        return retList;
    }

    /**
     * Return a list of valid taxon keywords
     * 
     * @param includeSpecimen
     *            if true, include the specimen keyword in the list if the
     *            current specimen is not empty i.e. characters have been used
     * @return A list of valid taxon keywords. The specimen keyword is included
     *         if applicable.
     */
    public synchronized List<String> getTaxaKeywords(boolean includeSpecimen) {
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

        // Include "eliminated" keyword if taxa have been eliminated
        if (remainingTaxaCount < _dataset.getNumberOfTaxa()) {
            retList.add(TAXON_KEYWORD_ELIMINATED);
        }

        // Include "remaining" keyword if there are remaining taxa
        if (remainingTaxaCount > 0) {
            retList.add(TAXON_KEYWORD_REMAINING);
        }

        // Include "selected" keyword if there are selected taxa
        if (_appUI != null && !_appUI.getSelectedTaxa().isEmpty()) {
            retList.add(TAXON_KEYWORD_SELECTED);
        }

        retList.add(TAXON_KEYWORD_NONE);

        // Include the "specimen" keyword if desired, and the specimen is not
        // empty.
        if (includeSpecimen && !_specimen.getUsedCharacters().isEmpty()) {
            retList.add(SPECIMEN_KEYWORD);
        }

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

        if (_dataset != null) {

            Specimen oldSpecimen = _specimen;

            // Create a new blank specimen
            _specimen = new Specimen(_dataset, false, _matchInapplicables, _matchUnknowns, _matchType);

            // Any character values that have been fixed need to be copied into
            // the new specimen
            if (_charactersFixed) {
                for (int characterNumber : _fixedCharactersList) {
                    Character ch = _dataset.getCharacter(characterNumber);
                    _specimen.setAttributeForCharacter(ch, oldSpecimen.getAttributeForCharacter(ch));
                }
            }

            // As we are starting from the beginning, best characters must be
            // cleared as they are no longer valid
            _bestOrSeparateCharacters = null;

            // If demonstration mode is on, need to revert back to the values of
            // SET, DISPLAY and INCLUDE directives
            // that were saved when demonstration mode enabled.
            if (_demonstrationMode) {
                _demonstrationModeSettings.loadIntoContext(this);
            }

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
    public synchronized boolean isProcessingDirectivesFile() {
        return _processingDirectivesFile;
    }

    /**
     * FOR UNIT TESTING ONLY
     * 
     * @param processing
     *            if true, a directives file is currently being processed.
     */
    public synchronized void setProcessingDirectivesFile(boolean processing) {
        _processingDirectivesFile = processing;
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

        // best characters need to be recalculated when the error tolerance is
        // changed.
        _bestOrSeparateCharacters = null;

        if (_dataset != null) {
            updateUI();
        }

        appendToLog(UIUtils.getResourceString("ErrorToleranceSet.log", _tolerance));
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
        if (varyWeight >= 0.0 && varyWeight <= 1.0) {
            _varyWeight = varyWeight;
        } else {
            throw new IllegalArgumentException("VaryWt must be a double value in range 0-1");
        }
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
        if (rbase >= 1.0 && rbase <= 5.0) {
            _rbase = rbase;
        } else {
            throw new IllegalArgumentException("RBase must be a double value in range 1-5");
        }

    }

    /**
     * @return The required level for diagnostic descriptions
     */
    public synchronized int getDiagLevel() {
        return _diagLevel;
    }

    /**
     * Sets the required level of diagnostic descriptions.
     * 
     * @param diagLevel
     */
    public synchronized void setDiagLevel(int diagLevel) {
        if (diagLevel >= 1) {
            this._diagLevel = diagLevel;
        } else {
            throw new IllegalArgumentException("DiagLevel must be an integer greater than one");
        }
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
     * @return An .ink file used to load a new dataset. May be a jnlp style file
     *         specifying where data is to be downloaded from. Or may be a
     *         directives file that is run to load the dataset in intkey,
     *         initialize values etc. In the latter case, this value will be the
     *         same as _initializationFile (see below).
     * 
     *         If _datasetStartupURL points to remote content (i.e. it is not a
     *         file URL), this will be a temporary file used to save the remote
     *         content referenced by the URL.
     * 
     *         If no dataset has been loaded, NULL will be returned
     * 
     */
    public synchronized File getDatasetStartupFile() {
        return _datasetStartupFile;
    }

    /**
     * @return A URL pointing to the dataset startup file. If the dataset is
     *         located on disk, this will be a file URL. NULL will be returned
     *         if no dataset has been loaded
     */
    public synchronized URL getDatasetStartupURL() {
        return _datasetStartupURL;
    }

    /**
     * @return The directory in which the currently loaded dataset is located,
     *         or NULL is no dataset has been loaded
     */
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

    /**
     * Use BEST character ordering
     */
    public synchronized void setCharacterOrderBest() {
        this._characterOrder = IntkeyCharacterOrder.BEST;
        this._taxonToSeparate = -1;
        _bestOrSeparateCharacters = null;
        if (_dataset != null) {
            updateUI();
        }
    }

    /**
     * Use NATURAL character ordering
     */
    public synchronized void setCharacterOrderNatural() {
        this._characterOrder = IntkeyCharacterOrder.NATURAL;
        this._taxonToSeparate = -1;
        if (_dataset != null) {
            updateUI();
        }
    }

    /**
     * Use SEPARATE character ordering to separate the specified taxon
     * 
     * @param taxonToSeparate
     *            the number of the taxon to separate
     */
    public synchronized void setCharacterOrderSeparate(int taxonToSeparate) {
        this._characterOrder = IntkeyCharacterOrder.SEPARATE;
        this._taxonToSeparate = taxonToSeparate;
        _bestOrSeparateCharacters = null;
        if (_dataset != null) {
            updateUI();
        }
    }

    /**
     * @return The number of the taxon currently being separated using SEPARATE
     *         character ordering
     */
    public synchronized int getTaxonToSeparate() {
        return _taxonToSeparate;
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
        List<Integer> characterNumbers = new ArrayList<Integer>();
        List<Integer> taxonNumbers = new ArrayList<Integer>();

        List<Character> availableCharacters = getAvailableCharacters();
        availableCharacters.removeAll(_dataset.getCharactersToIgnoreForBest());

        for (Character ch : availableCharacters) {
            characterNumbers.add(ch.getCharacterId());
        }

        for (Item taxon : getAvailableTaxa()) {
            taxonNumbers.add(taxon.getItemNumber());
        }

        if (_characterOrder == IntkeyCharacterOrder.BEST) {
            _bestOrSeparateCharacters = Best.orderBest(_dataset, characterNumbers, taxonNumbers, _rbase, _varyWeight);
        } else if (_characterOrder == IntkeyCharacterOrder.SEPARATE) {
            _bestOrSeparateCharacters = Best.orderSeparate(_taxonToSeparate, _dataset, characterNumbers, taxonNumbers, _rbase, _varyWeight);
        }
    }

    /**
     * @return true if inapplicable should match any value when comparing
     *         character values when processing the USE, DIFFERENCES or
     *         SIMILARITIES directives
     */
    public synchronized boolean getMatchInapplicables() {
        return _matchInapplicables;
    }

    /**
     * @return true if inapplicable should match any value when comparing
     *         character values when processing the USE, DIFFERENCES or
     *         SIMILARITIES directives
     */
    public synchronized boolean getMatchUnknowns() {
        return _matchUnknowns;
    }

    /**
     * @return The match type - one of overlap (two sets of values match if they
     *         overlap), subset (two sets of values match if one is a subset of
     *         the other) or exact (two sets of values match only if they are
     *         identical) - to use when comparing character values when
     *         processing the USE, DIFFERENCES or SIMILARITIES directives
     */
    public synchronized MatchType getMatchType() {
        return _matchType;
    }

    /**
     * Set the match settings - these specifiy which character values are to be
     * regarded as equal when processing the USE, DIFFERENCES or SIMILARITIES
     * directives
     * 
     * @param matchUnknowns
     *            if true, unknown should match any value
     * @param matchInapplicables
     *            if true, inapplicable should match any value
     * @param matchType
     *            the match type - one of overlap (two sets of values match if
     *            they overlap), subset (two sets of values match if one is a
     *            subset of the other) or exact (two sets of values match only
     *            if they are identical)
     */
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

        // Write a log message informing if the new match setting.
        List<String> matchSettingWords = new ArrayList<String>();
        if (matchInapplicables) {
            matchSettingWords.add(SetMatchDirective.INAPPLICABLES_WORD);
        }

        if (matchUnknowns) {
            matchSettingWords.add(SetMatchDirective.UNKNOWNS_WORD);
        }

        matchSettingWords.add(matchType.toString().toLowerCase());

        appendToLog(UIUtils.getResourceString("SetMatch.log", StringUtils.join(matchSettingWords, ", ")));
    }

    /**
     * Update the specimen with new match settings. This needs to be called each
     * time the match settings are updated so that the eliminated taxa can be
     * recalculated given the new match settings.
     */
    private void updateSpecimenMatchSettings() {
        if (_dataset != null) {
            Specimen newSpecimen = new Specimen(_dataset, false, _matchInapplicables, _matchUnknowns, _matchType, _specimen);
            _specimen = newSpecimen;
            updateUI();
        }
    }

    /**
     * @return The diagType. This specifies whether DIAGNOSE will generate
     *         specimen- or taxon-diagnostic descriptions. A specimen-diagnostic
     *         description must be able to distinguish any specimen belonging to
     *         the diagnosed taxon from any specimen belonging to any other
     *         taxon. A taxon-diagnostic description must be able to distinguish
     *         the diagnosed taxon from any other taxon. When generating
     *         specimen-diagnostic descriptions, the program will not include
     *         characters which may be inapplicable to specimens in the
     *         diagnosed taxon, because, for some specimens, such characters
     *         cannot contribute to the desired separation. The default is
     *         SPECIMENS.
     */
    public synchronized DiagType getDiagType() {
        return _diagType;
    }

    /**
     * Sets the diag type. The diagType Specifies whether DIAGNOSE will generate
     * specimen- or taxon-diagnostic descriptions. A specimen-diagnostic
     * description must be able to distinguish any specimen belonging to the
     * diagnosed taxon from any specimen belonging to any other taxon. A
     * taxon-diagnostic description must be able to distinguish the diagnosed
     * taxon from any other taxon. When generating specimen-diagnostic
     * descriptions, the program will not include characters which may be
     * inapplicable to specimens in the diagnosed taxon, because, for some
     * specimens, such characters cannot contribute to the desired separation.
     * The default is SPECIMENS.
     * 
     * @param diagType
     *            The diag type
     */
    public synchronized void setDiagType(DiagType diagType) {
        this._diagType = diagType;

        appendToLog(UIUtils.getResourceString("DiagtypeSet.log", diagType.toString()));
    }

    /**
     * @return included characters ordered by character number
     */
    public synchronized List<Character> getIncludedCharacters() {
        List<Character> retList = new ArrayList<Character>();

        for (int charNum : _includedCharacters) {
            retList.add(_dataset.getCharacter(charNum));
        }

        Collections.sort(retList);

        return retList;
    }

    /**
     * @return excluded characters ordered by character number
     */
    public synchronized List<Character> getExcludedCharacters() {
        List<Character> excludedCharacters = _dataset.getCharactersAsList();
        excludedCharacters.removeAll(getIncludedCharacters());
        return excludedCharacters;
    }

    /**
     * @return included taxa ordered by taxon number
     */
    public synchronized List<Item> getIncludedTaxa() {
        List<Item> retList = new ArrayList<Item>();

        for (int charNum : _includedTaxa) {
            retList.add(_dataset.getItem(charNum));
        }

        Collections.sort(retList);

        return retList;
    }

    /**
     * @return excluded taxa ordered by taxon number
     */
    public synchronized List<Item> getExcludedTaxa() {
        List<Item> excludedTaxa = _dataset.getItemsAsList();
        excludedTaxa.removeAll(getIncludedTaxa());
        return excludedTaxa;
    }

    /**
     * Set what characters are included
     * 
     * @param includedCharacters
     *            the set of characters that are included
     */
    public synchronized void setIncludedCharacters(Set<Integer> includedCharacters) {
        doSetIncludedCharacters(includedCharacters);

        if (_includedCharacters.size() == 1) {
            appendToLog(UIUtils.getResourceString("OneCharacterIncluded.log"));
        } else {
            appendToLog(UIUtils.getResourceString("MultipleCharactersIncluded.log", _includedCharacters.size()));
        }
    }

    /**
     * Helper method for setIncludedCharacters() and setExcludedCharacters()
     * 
     * @param includedCharacters
     *            the set of included characters
     */
    private synchronized void doSetIncludedCharacters(Set<Integer> includedCharacters) {
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

    /**
     * Set what taxa are included
     * 
     * @param includedTaxa
     *            the set of included taxa
     */
    public synchronized void setIncludedTaxa(Set<Integer> includedTaxa) {
        doSetIncludedTaxa(includedTaxa);

        if (_includedTaxa.size() == 1) {
            appendToLog(UIUtils.getResourceString("OneTaxonIncluded.log"));
        } else {
            appendToLog(UIUtils.getResourceString("MultipleTaxaIncluded.log", _includedTaxa.size()));
        }
    }

    /**
     * Helper method for setIncludedTaxa() and setExcludedTaxa()
     * 
     * @param includedTaxa
     */
    private synchronized void doSetIncludedTaxa(Set<Integer> includedTaxa) {
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

    /**
     * Set what characters are excluded. All characters aside from those
     * specified will be included.
     * 
     * @param excludedCharacters
     *            The set of excluded characters.
     */
    public synchronized void setExcludedCharacters(Set<Integer> excludedCharacters) {
        Set<Integer> includedCharacters = new HashSet<Integer>();
        for (int i = 1; i < _dataset.getNumberOfCharacters() + 1; i++) {
            includedCharacters.add(i);
        }

        includedCharacters.removeAll(excludedCharacters);

        doSetIncludedCharacters(includedCharacters);

        if (excludedCharacters.size() == 1) {
            appendToLog(UIUtils.getResourceString("OneCharacterExcluded.log"));
        } else {
            appendToLog(UIUtils.getResourceString("MultipleCharactersExcluded.log", excludedCharacters.size()));
        }
    }

    /**
     * Set what characters are excluded. All characters aside from those
     * specified will be included.
     * 
     * @param excludedCharacters
     *            The set of excluded characters.
     */
    public synchronized void setExcludedTaxa(Set<Integer> excludedTaxa) {
        Set<Integer> includedTaxa = new HashSet<Integer>();
        for (int i = 1; i < _dataset.getNumberOfTaxa() + 1; i++) {
            includedTaxa.add(i);
        }

        includedTaxa.removeAll(excludedTaxa);

        doSetIncludedTaxa(includedTaxa);

        if (excludedTaxa.size() == 1) {
            appendToLog(UIUtils.getResourceString("OneTaxonExcluded.log"));
        } else {
            appendToLog(UIUtils.getResourceString("MultipleTaxaExcluded.log", excludedTaxa.size()));
        }
    }

    /**
     * @return The currently included characters minus the characters that have
     *         values set in the specimen. Ordered by character number.
     */
    public synchronized List<Character> getAvailableCharacters() {
        List<Character> retList = getIncludedCharacters();

        // Used characters are not available
        retList.removeAll(_specimen.getUsedCharacters());

        // Neither are characters that have been made inapplicable
        retList.removeAll(_specimen.getInapplicableCharacters());

        return retList;
    }

    /**
     * @return The list of characters that have values set in the specimen.
     *         Ordered by character number
     */
    public synchronized List<Character> getUsedCharacters() {
        return _specimen.getUsedCharacters();
    }

    /**
     * @return The list of included taxa, minus the characters that have been
     *         eliminated from the current investigation. Ordered by taxon
     *         number.
     */
    public synchronized List<Item> getAvailableTaxa() {
        List<Item> availableTaxa = getIncludedTaxa();
        availableTaxa.removeAll(getEliminatedTaxa());
        return availableTaxa;
    }

    /**
     * @return The list of taxa that have been eliminated from the current
     *         investigation. Ordered by taxon number.
     */
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

    /**
     * @return An instance of ImageSettings which contains the default settings
     *         to use when displaying images and image overlays.
     */
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

        imageSettings.setCacheDirectory(_fileCacheDirectory);

        return imageSettings;
    }

    public synchronized ResourceSettings getInfoSettings() {
        ResourceSettings infoSettings = new ResourceSettings();

        infoSettings.setDataSetPath(getDatasetDirectory().getAbsolutePath());

        infoSettings.setResourcePaths(_infoPathLocations);

        infoSettings.setCacheDirectory(_fileCacheDirectory);

        return infoSettings;
    }

    /**
     * This method is only used for saving settings when SET DEMONSTRATION is
     * set to ON.
     * 
     * @return The list of locations to search for images.
     */
    synchronized List<String> getImagePaths() {
        return new ArrayList<String>(_imagePathLocations);
    }

    /**
     * Sets locations which will be searched for images (characters, taxa,
     * keywords or startup images).
     * 
     * @param imagePaths
     *            The list of locations to search for images. These may be
     *            either file paths (relative to the dataset directory if not
     *            absolute) or urls for remote locations.
     */
    public synchronized void setImagePaths(List<String> imagePaths) {
        _imagePathLocations = new ArrayList<String>(imagePaths);

        appendToLog(UIUtils.getResourceString("ImagepathSet.log", StringUtils.join(imagePaths, ";")));
    }

    /**
     * This method is only used for saving settings when SET DEMONSTRATION is
     * set to ON.
     * 
     * @return The list of locations to search for information files.
     */
    synchronized List<String> getInfoPaths() {
        return new ArrayList<String>(_infoPathLocations);
    }

    /**
     * Sets locations which will be searched for files listed in the taxon
     * information dialog (accessed via the INFORMATION directive).
     * 
     * @param infoPaths
     *            The list of locations to search for information files. These
     *            may be either file paths (relative to the dataset directory if
     *            not absolute) or urls for remote locations.
     */
    public synchronized void setInfoPaths(List<String> infoPaths) {
        _infoPathLocations = new ArrayList<String>(infoPaths);
        appendToLog(UIUtils.getResourceString("InfopathSet.log", StringUtils.join(infoPaths, ";")));
    }

    /**
     * Makes a command available in the taxon information dialog.
     * 
     * @param subject
     *            The subject under which the command should be listed in the
     *            taxon information dialog
     * @param command
     *            The directive command to run when the associated subject is
     *            selected in the taxon information dialog. This may include the
     *            special parameter "?S" which will be substituted for the
     *            number of the taxon being examined in the taxon information
     *            dialog.
     */
    public synchronized void addTaxonInformationDialogCommand(String subject, String command) {
        _taxonInformationDialogCommands.add(new Pair<String, String>(subject, command));
    }

    /**
     * @return The subject/command pairs that are listed in the taxon
     *         information dialog. The command text may include the special
     *         parameter "?S" which will be substituted for the number of the
     *         taxon being examined in the dialog when the command is run.
     */
    public synchronized List<Pair<String, String>> getTaxonInformationDialogCommands() {
        return new ArrayList<Pair<String, String>>(_taxonInformationDialogCommands);
    }

    /**
     * Called before a new dataset is loaded
     */
    private void cleanupOldDataset() {
        // need to do this first, as the UI may want to access the
        // _startupFileData as part of its cleanup process
        _appUI.handleDatasetClosed();

        if (_dataset != null) {
            _dataset.close();
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
    }

    /**
     * Called prior to application shutdown.
     */
    public synchronized void cleanupForShutdown() {
        cleanupOldDataset();
        if (_logPrintFile != null) {
            _logPrintFile.close();
        }

        if (_journalPrintFile != null) {
            _journalPrintFile.close();
        }

        if (_currentOutputPrintFile != null) {
            _currentOutputPrintFile.close();
        }
    }

    /**
     * @return The IntkeyUI
     */
    public synchronized IntkeyUI getUI() {
        return _appUI;
    }

    /**
     * 
     * @return The data contained within the JNLP-style startup file for the
     *         current dataset, if the dataset was loaded using such a file.
     *         Otherwise the method returns null.
     */
    public synchronized StartupFileData getStartupFileData() {
        return _startupFileData;
    }

    /**
     * @return The directive populator
     */
    public synchronized DirectivePopulator getDirectivePopulator() {
        return _directivePopulator;
    }

    /**
     * 
     * @return true if the values (attributes) set for characters in the
     *         specimen are fixed such that they are not cleared by the RESTART
     *         command. In addition, the values set for these characters cannot
     *         be changed. Used by the SET FIX directive.
     */
    public synchronized boolean charactersFixed() {
        return _charactersFixed;
    }

    /**
     * Used by the SET FIX directive. Sets whether or not character values
     * (attributes) in the specimen are fixed.
     * 
     * @param charactersFixed
     *            If true, the values (attributes) set for characters in the
     *            specimen will fixed such that they are not cleared by the
     *            RESTART command. In addition, the values set for these
     *            characters will be unable to be changed.
     */
    public synchronized void setCharactersFixed(boolean charactersFixed) {
        if (charactersFixed != this._charactersFixed) {
            this._charactersFixed = charactersFixed;
            if (_charactersFixed) {
                _fixedCharactersList = new ArrayList<Integer>();
                for (Character usedCharacter : getUsedCharacters()) {
                    _fixedCharactersList.add(usedCharacter.getCharacterId());
                }
                appendToLog(UIUtils.getResourceString("CharactersFixed.log"));
            } else {
                appendToLog(UIUtils.getResourceString("FixOff.log"));
                _fixedCharactersList = new ArrayList<Integer>();
                // If in demonstration mode, need to temporarily disable it,
                // otherwise
                // fix mode back be immediately turned back on by restarting the
                // identification.
                if (_demonstrationMode) {
                    _demonstrationMode = false;
                    restartIdentification();
                    _demonstrationMode = true;
                } else {
                    restartIdentification();
                }
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

    /**
     * Take the supplied attributes, set them in the specimen, then set the
     * corresponding characters as being fixed.
     * 
     * @param attrs
     *            The list of attributes. These should be in the order of use in
     *            the specimen - i.e. attributes for any controlling characters
     *            should appear in the list before their dependent characters
     */
    synchronized void setFixedCharactersFromAttributes(List<Attribute> attrs) {
        this._charactersFixed = true;
        _fixedCharactersList.clear();
        for (Attribute attr : attrs) {
            _specimen.setAttributeForCharacter(attr.getCharacter(), attr);
            _fixedCharactersList.add(attr.getCharacter().getCharacterId());
        }
    }

    /**
     * @return true if the TOLERANCE is decreased automatically by the program
     *         to the smallest value such that the number of taxa remaining is
     *         non-zero; the value is never automatically increased. The default
     *         is ON in Normal mode, and OFF in Advanced mode.
     */
    public synchronized boolean isAutoTolerance() {
        return _autoTolerance;
    }

    /**
     * Sets auto tolerance on or off.
     * 
     * @param autoTolerance
     *            if true then the TOLERANCE is decreased automatically by the
     *            program to the smallest value such that the number of taxa
     *            remaining is non-zero; the value is never automatically
     *            increased. The default is ON in Normal mode, and OFF in
     *            Advanced mode.
     */
    public synchronized void setAutoTolerance(boolean autoTolerance) {
        this._autoTolerance = autoTolerance;
    }

    /**
     * When DEMONSTRATION mode is ON, the RESTART command restores the SET,
     * DISPLAY, and INCLUDE parameters to the values they had when demonstration
     * mode was turned on, or the values associated with the selected operation
     * mode (automatic or manual). When DEMONSTRATION is turned ON from an input
     * command file it cannot subsequently be turned OFF except from another
     * input file. This option is intended for running unattended demonstrations
     * of the program. The default is OFF.
     * 
     * @return true if demonstration mode is set to ON.
     */
    public synchronized boolean isDemonstrationMode() {
        return _demonstrationMode;
    }

    /**
     * Sets demonstration mode on or off. When DEMONSTRATION mode is ON, the
     * RESTART command restores the SET, DISPLAY, and INCLUDE parameters to the
     * values they had when demonstration mode was turned on, or the values
     * associated with the selected operation mode (automatic or manual). When
     * DEMONSTRATION is turned ON from an input command file it cannot
     * subsequently be turned OFF except from another input file. This option is
     * intended for running unattended demonstrations of the program. The
     * default is OFF.
     * 
     * @param demonstrationMode
     */
    public synchronized void setDemonstrationMode(boolean demonstrationMode) {
        this._demonstrationMode = demonstrationMode;
        if (_demonstrationMode) {
            _demonstrationModeSettings = new DemonstrationModeSettings(this);
        } else {
            _demonstrationModeSettings = null;
        }
        getUI().setDemonstrationMode(demonstrationMode);
    }

    /**
     * Gets the set of exact characters. These are characters which are to be
     * regarded as not subject to error. When such a character is USEd, taxa
     * inconsistent with the specified value are eliminated, regardless of the
     * TOLERANCE value.
     * 
     * @return The set of exact characters
     */
    public synchronized Set<Character> getExactCharacters() {
        Set<Character> exactCharacters = new HashSet<Character>();
        for (int charNum : _exactCharactersSet) {
            exactCharacters.add(_dataset.getCharacter(charNum));
        }
        return exactCharacters;
    }

    /**
     * Sets the set of exact characters. These are characters which are to be
     * regarded as not subject to error. When such a character is USEd, taxa
     * inconsistent with the specified value are eliminated, regardless of the
     * TOLERANCE value.
     * 
     * @param characters
     *            The set of exact characters
     */
    public synchronized void setExactCharacters(Set<Integer> characters) {
        _exactCharactersSet = new HashSet<Integer>(characters);

        if (_exactCharactersSet.isEmpty()) {
            appendToLog(UIUtils.getResourceString("NoExactCharacters.log"));
        } else {
            appendToLog(UIUtils.getResourceString("ExactCharacters.log"));
        }
    }

    /**
     * Returns true if a character is exact - if it is regarded as not subject
     * to error. When such a character is USEd, taxa inconsistent with the
     * specified value are eliminated, regardless of the TOLERANCE value.
     * 
     * @param ch
     *            The character
     * @return true if the specified character is set to exact.
     */
    private boolean isCharacterExact(Character ch) {
        return _exactCharactersSet.contains(ch.getCharacterId());
    }

    /**
     * @return The number of characters which a DIAGNOSE command must find
     *         before it stops searching for acceptable characters. Characters
     *         are processed in order of decreasing reliability. If n is 0,
     *         which is the default, all the characters are examined.
     */
    public synchronized int getStopBest() {
        return _stopBest;
    }

    /**
     * Sets the stopbest value - the number of characters which a DIAGNOSE
     * command must find before it stops searching for acceptable characters.
     * Characters are processed in order of decreasing reliability. If n is 0,
     * which is the default, all the characters are examined.
     * 
     * @param stopBest
     *            The stopbest value
     */
    public synchronized void setStopBest(int stopBest) {
        this._stopBest = stopBest;
    }

    /**
     * @return true of numbering should be displayed beside character and taxon
     *         names
     */
    public synchronized boolean displayNumbering() {
        return _displayNumbering;
    }

    /**
     * Sets whether or not numbering should be displayed beside character and
     * taxon names
     * 
     * @param displayNumbering
     *            if true, numbering will be displayed
     */
    public synchronized void setDisplayNumbering(boolean displayNumbering) {
        this._displayNumbering = displayNumbering;
        updateUI();
    }

    /**
     * @return true if inapplicable characters should be displayed in the output
     *         of the DESCRIBE command
     */
    public synchronized boolean displayInapplicables() {
        return _displayInapplicables;
    }

    /**
     * Sets whether or not inapplicable characters should be displayed in the
     * output of the DESCIRBE command.
     * 
     * @param displayInapplicables
     *            if true, inapplicable characters will be displayed in the
     *            output of the DESCRIBE command.
     */
    public synchronized void setDisplayInapplicables(boolean displayInapplicables) {
        this._displayInapplicables = displayInapplicables;
    }

    /**
     * @return true if unknown characters should be displayed in the output of
     *         the DESCRIBE command
     */
    public synchronized boolean displayUnknowns() {
        return _displayUnknowns;
    }

    /**
     * Sets whether or not unknown characters should be displayed in the output
     * of the DESCIRBE command.
     * 
     * @param displayUnknowns
     *            if true, inapplicable characters will be displayed in the
     *            output of the DESCRIBE command.
     */
    public synchronized void setDisplayUnknowns(boolean displayUnknowns) {
        this._displayUnknowns = displayUnknowns;
    }

    /**
     * @return true if comments in taxon names should be displayed in list boxes
     *         and in the output of the TAXA command
     */
    public synchronized boolean displayComments() {
        return _displayComments;
    }

    /**
     * Sets whether or not comments in taxon names should be displayed in list
     * boxes and in the output of the TAXA command
     * 
     * @param displayComments
     *            if true, comments in taxon names will be displayed in list
     *            boxes and in the output of the TAXA command
     */
    public synchronized void setDisplayComments(boolean displayComments) {
        this._displayComments = displayComments;
        updateUI();
    }

    /**
     * @return true if CONTINUOUS is on. When CONTINUOUS is ON, images are
     *         displayed in a continuous loop, and the "Multiple Images" option
     *         in taxon-image windows is unavailable. The default is OFF.
     */
    public synchronized boolean displayContinuous() {
        return _displayContinuous;
    }

    /**
     * Sets whether or not CONTINUOUS is on. When CONTINUOUS is ON, images are
     * displayed in a continuous loop, and the "Multiple Images" option in
     * taxon-image windows is unavailable. The default is OFF.
     * 
     * @param displayContinuous
     *            if true, continuous will be set to on.
     */
    public synchronized void setDisplayContinuous(boolean displayContinuous) {
        this._displayContinuous = displayContinuous;
    }

    /**
     * Gets the image display mode
     * 
     * @return the image display mode
     */
    public synchronized ImageDisplayMode getImageDisplayMode() {
        return _displayImagesMode;
    }

    /**
     * Sets the image display mode
     * 
     * @param imageDisplayMode
     *            the image display mode
     */
    public synchronized void setImageDisplayMode(ImageDisplayMode imageDisplayMode) {
        this._displayImagesMode = imageDisplayMode;
    }

    /**
     * @return true if keyword selection dialogs should be displayed when
     *         prompting for characters or taxa. If false, the default character
     *         or taxon menu will be displayed
     */
    public synchronized boolean displayKeywords() {
        return _displayKeywords;
    }

    /**
     * Sets whether or not keyword selection dialogs should be displayed when
     * prompting for characters or tax
     * 
     * @param displayKeywords
     *            if true, keyword selection dialogs will be displayed when
     *            prompting for characters or taxa. If false, the default
     *            character or taxon menu will be displayed
     */
    public synchronized void setDisplayKeywords(boolean displayKeywords) {
        this._displayKeywords = displayKeywords;
    }

    /**
     * @return If true, images should be automatically scaled to fit within the
     *         image window.
     */
    public synchronized boolean displayScaled() {
        return _displayScaled;
    }

    /**
     * Sets whether or not images should be automatically scaled to fit within
     * the image window.
     * 
     * @param displayScaled
     *            True if images should be automatically scaled to fit within
     *            the image window
     */
    public synchronized void setDisplayScaled(boolean displayScaled) {
        this._displayScaled = displayScaled;
    }

    /**
     * @return True if commands specified by DEFINE ENDIDENTIFY are executed at
     *         the end of an identification.
     */
    public synchronized boolean displayEndIdentify() {
        return _displayEndIdentify;
    }

    /**
     * Sets whether or not commands specified by DEFINE ENDIDENTIFY are executed
     * when a taxon is successfully identified in an investigation.
     * 
     * @param displayEndIdentify
     *            True if commands specified by DEFINE ENDIDENTIFY are executed
     *            when a taxon is successfully identified in an investigation.
     */
    public synchronized void setDisplayEndIdentify(boolean displayEndIdentify) {
        this._displayEndIdentify = displayEndIdentify;
    }

    /**
     * Sets the directive commands that should be run when a taxon is
     * successfully identified in an investigation.
     * 
     * @param commands
     *            The commands to run when a taxon is successfully identified in
     *            an investigation
     */
    public synchronized void setEndIdentifyCommands(List<String> commands) {
        _endIdentifyCommands = new ArrayList<String>(commands);
    }

    /**
     * Run the directive commands set for execution when a taxon is successfully
     * identified in an investigation.
     */
    private void executeEndIdentifyCommands() {
        if (_endIdentifyCommands != null) {
            for (String cmd : _endIdentifyCommands) {
                parseAndExecuteDirective(cmd);
            }
        }
    }

    /**
     * @return true if the contents of an input file are displayed (and included
     *         in the LOG and JOURNAL files) as it is executed
     */
    public synchronized boolean displayInput() {
        return _displayInput;
    }

    /**
     * Sets whether or not the contents of an input file are displayed (and
     * included in the LOG and JOURNAL files) as it is executed
     * 
     * @param displayInput
     *            If true, the contents of an input file will be displayed (and
     *            included in the LOG and JOURNAL files) as it is executed
     */
    public synchronized void setDisplayInput(boolean displayInput) {
        this._displayInput = displayInput;
    }

    /**
     * Sets the file to write logging output to
     * 
     * @param logFile
     *            the log file
     * @throws IOException
     */
    public synchronized void setLogFile(File logFile) throws IOException {
        if (_logPrintFile != null) {
            _logPrintFile.close();
        }

        _logPrintFile = new PrintFile(new PrintStream(logFile), OUTPUT_FILE_WIDTH);
        _logFile = logFile;

        _logPrintFile.outputLine(_logCache.toString());
        _logPrintFile.setTrimInput(false, true);
    }

    /**
     * Sets the file to write journal output to
     * 
     * @param journalFile
     *            the journal file
     * @throws IOException
     */
    public synchronized void setJournalFile(File journalFile) throws IOException {
        if (_journalPrintFile != null) {
            _journalPrintFile.close();
        }

        _journalPrintFile = new PrintFile(new PrintStream(journalFile), OUTPUT_FILE_WIDTH);
        _journalFile = journalFile;

        StringBuilder journalContentBuilder = new StringBuilder();
        for (String line : _journalCache) {
            journalContentBuilder.append(line);
            journalContentBuilder.append("\n");
        }
        _journalPrintFile.outputLine(journalContentBuilder.toString());
        _journalPrintFile.setTrimInput(false, true);
    }

    /**
     * Use a new file to write output information to
     * 
     * @param outputFile
     *            the new output file
     * @throws IOException
     */
    public synchronized void newOutputFile(File outputFile) throws IOException {
        if (_currentOutputFile != null && !_currentOutputFile.equals(outputFile)) {
            if (_currentOutputFile != null) {
                closeOutputFile(_currentOutputFile);
            }
        }

        _currentOutputFile = outputFile;
        _currentOutputPrintFile = new PrintFile(new PrintStream(outputFile), OUTPUT_FILE_WIDTH);
        _currentOutputPrintFile.setTrimInput(false, true);
    }

    /**
     * Close the specified output file
     * 
     * @param outputFile
     *            the output file to close
     */
    public synchronized void closeOutputFile(File outputFile) {
        if (_currentOutputFile != null && _currentOutputFile.equals(outputFile)) {
            _currentOutputPrintFile.close();
            _currentOutputFile = null;
        }
    }

    /**
     * @return The journal file, or null if one has not been specified
     */
    public synchronized File getJournalFile() {
        return _journalFile;
    }

    /**
     * The log file, or null if one has not been specified
     * 
     * @return
     */
    public synchronized File getLogFile() {
        return _logFile;
    }

    /**
     * The current output file, or null if one has not been specified.
     * 
     * @return
     */
    public synchronized File getOutputFile() {
        return _currentOutputFile;
    }

    /**
     * Appends the supplied text to the current output file. Note that
     * whitespace characters will be trimmed from the beginning of lines when
     * line wrapping is done. To insert blank lines in the output file, use
     * 
     * @param text
     */
    public synchronized void appendTextToOutputFile(String text) {
        if (_currentOutputFile == null) {
            throw new IllegalStateException("No output file is open");
        }

        _currentOutputPrintFile.outputLine(text);
    }

    /**
     * Appends a blank line to the current output file
     */
    public synchronized void appendBlankLineToOutputFile() {
        if (_currentOutputFile == null) {
            throw new IllegalStateException("No output file is open");
        }

        _currentOutputPrintFile.writeBlankLines(1, 0);
    }

    /**
     * Returns true if the last line appended to the current output file was a
     * comment specified via the OUTPUT COMMENT directive
     * 
     * @return
     */
    public synchronized boolean getLastOutputLineWasComment() {
        return _lastOutputLineWasComment;
    }

    /**
     * Sets whether or not the last line appended to the current output file was
     * a comment specified via the OUTPUT COMMENT directive
     * 
     * @param lastOutputLineWasComment
     *            true if the last line appended to the current output file was
     *            a comment specified via the OUTPUT COMMENT directive
     * @return
     */
    public synchronized boolean setLastOutputLineWasComment(boolean lastOutputLineWasComment) {
        return _lastOutputLineWasComment = lastOutputLineWasComment;
    }

    /**
     * @return The image subjects. These words or phrases are placed in the
     *         "Subjects" list box in the "Select Multiple Images" dialog box,
     *         which is invoked by the "Multiple Images" option of the "Control"
     *         menu in taxon-image windows. The images displayed are restricted
     *         to those whose "subjects" contain any of the words or phrases
     *         selected in the list box.
     */
    public synchronized List<String> getImageSubjects() {
        return _imageSubjects;
    }

    /**
     * Sets the image subjects.
     * 
     * @param imageSubjects
     *            These words or phrases will be placed in the "Subjects" list
     *            box in the "Select Multiple Images" dialog box, which is
     *            invoked by the "Multiple Images" option of the "Control" menu
     *            in taxon-image windows. The images displayed are restricted to
     *            those whose "subjects" contain any of the words or phrases
     *            selected in the list box.
     */
    public synchronized void setImageSubjects(List<String> imageSubjects) {
        _imageSubjects = new ArrayList<String>(imageSubjects);
    }

    /**
     * Appends the supplied text to the log
     * 
     * @param text
     */
    public synchronized void appendToLog(String text) {
        try {
            if (StringUtils.isBlank(text)) {
                return;
            }
            if (_logPrintFile != null) {
                _logPrintFile.outputLine(text);
            }

            _logCache.add(text);

            _appUI.updateLog();
        } catch (Exception e) {
            Logger.error("Unable to update the Intkey log", e);
        }
    }

    /**
     * Appends the supplied text to the journal
     * 
     * @param text
     */
    public synchronized void appendToJournal(String text) {
        if (_journalPrintFile != null) {
            _journalPrintFile.outputLine(text);
        }

        _journalCache.add(text);
    }

    /**
     * Returns all lines previously written to the log
     * 
     * @return The list of lines previously written to the log
     */
    public synchronized List<String> getLogEntries() {
        return new ArrayList<String>(_logCache);
    }

    /**
     * @return The directory in which images and information files pulled from
     *         remote locations are cached.
     */
    public synchronized File getFileCacheDirectory() {
        return _fileCacheDirectory;
    }

    /**
     * Updates the entire UI.
     */
    private void updateUI() {
        // Don't update the UI in the middle of processing an input file. Wait
        // and update once when the
        // entire file has finished being processed.
        if (!_processingDirectivesFile) {
            _appUI.handleUpdateAll();
        }
    }

    /**
     * Process the dataset startup file at the specified URL. This URL may point
     * to either a JNLP style "webstart" file for a dataset, or an actual
     * directives file with directive commands used to initalize and load the
     * dataset.
     * 
     * @param startupFileUrl
     *            URL to the startup file.
     * @throws Exception
     */
    private synchronized void processStartupFile(URL startupFileUrl) throws Exception {
        URL inkFileLocation = null;
        URL dataFileLocation = null;
        String initializationFileLocation = null;
        String imagePath = null;
        String infoPath = null;

        File startupFile = Utils.saveURLToTempFile(startupFileUrl, "Intkey", 30000);

        BufferedReader reader = new BufferedReader(new FileReader(startupFile));

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
                    if (value.equals(startupFile.getAbsolutePath())) {
                        inkFileLocation = startupFileUrl.toURI().toURL();
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
            tempDir.deleteOnExit();

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
                FileUtils.copyURLToFile(dataFileLocation, localDataFile, 30000, 30000);
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
                String startupImagePath = _startupFileData.getImagePath();
                List<String> imagePaths = ResourceSettings.parse(startupImagePath);
                setImagePaths(imagePaths);
            }

            if (startupFileData.getInfoPath() != null) {
                setInfoPaths(Arrays.asList(_startupFileData.getInfoPath()));
            }
        } else {
            processInitializationFile(startupFile);
            _startupFileData = null;
        }

        _datasetStartupFile = startupFile;
        _datasetStartupURL = startupFileUrl;

        // create a directory to save cached versions of any images or files
        // downloaded for this dataset from
        // remote locations on the image path or info path. Use a random UUID as
        // the name of the directory
        _fileCacheDirectory = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString());
        _fileCacheDirectory.mkdir();
        _fileCacheDirectory.deleteOnExit();

    }

}
