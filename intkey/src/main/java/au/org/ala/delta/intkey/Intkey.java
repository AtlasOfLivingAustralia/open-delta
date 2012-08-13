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
package au.org.ala.delta.intkey;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.intkey.directives.*;
import au.org.ala.delta.intkey.model.DisplayImagesReportType;
import au.org.ala.delta.intkey.model.ImageDisplayMode;
import au.org.ala.delta.intkey.model.IntkeyCharacterOrder;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.SearchUtils;
import au.org.ala.delta.intkey.model.StartupFileData;
import au.org.ala.delta.intkey.model.StartupUtils;
import au.org.ala.delta.intkey.ui.*;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.ui.AboutBox;
import au.org.ala.delta.ui.DeltaSingleFrameApplication;
import au.org.ala.delta.ui.help.HelpController;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;
import au.org.ala.delta.ui.util.IconHelper;
import au.org.ala.delta.util.Pair;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.jdesktop.application.Action;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Intkey extends DeltaSingleFrameApplication implements IntkeyUI, DirectivePopulator {

    public static final String HELPSET_PATH = "help/Intkey";

    public static final String HELP_ID_TOPICS = "topics";
    public static final String HELP_ID_COMMANDS = "commands";

    public static final String HELP_ID_NO_MATCHING_TAXA_REMAIN = "no_taxa_match_the_specimen";
    public static final String HELP_ID_IDENTIFICATION_COMPLETE = "checking_an_identification";
    public static final String HELP_ID_NO_CHARACTERS_REMAINING = "not_enough_characters_for_identification";

    public static final String HELP_ID_CHARACTERS_TOOLBAR_RESTART = "characters_toolbar_restart";
    public static final String HELP_ID_CHARACTERS_TOOLBAR_BEST = "characters_toolbar_best";
    public static final String HELP_ID_CHARACTERS_TOOLBAR_SEPARATE = "characters_toolbar_separate";
    public static final String HELP_ID_CHARACTERS_TOOLBAR_NATURAL = "characters_toolbar_natural";
    public static final String HELP_ID_CHARACTERS_TOOLBAR_DIFF_SPECIMEN_REMAINING = "characters_toolbar_diff_specimen_remaining";
    public static final String HELP_ID_CHARACTERS_TOOLBAR_TOLERANCE = "characters_toolbar_tolerance";
    public static final String HELP_ID_CHARACTERS_TOOLBAR_SET_MATCH = "characters_toolbar_set_match";
    public static final String HELP_ID_CHARACTERS_TOOLBAR_SUBSET_CHARACTERS = "characters_toolbar_subset_characters";
    public static final String HELP_ID_CHARACTERS_TOOLBAR_FIND_CHARACTERS = "characters_toolbar_find_characters";

    public static final String HELP_ID_TAXA_TOOLBAR_INFO = "taxa_toolbar_info";
    public static final String HELP_ID_TAXA_TOOLBAR_DIFF_TAXA = "taxa_toolbar_diff_taxa";
    public static final String HELP_ID_TAXA_TOOLBAR_SUBSET_TAXA = "taxa_toolbar_subset_taxa";
    public static final String HELP_ID_TAXA_TOOLBAR_FIND_TAXA = "taxa_toolbar_find_taxa";

    // Resource strings
    @Resource
    String windowTitleWithDatasetTitle;

    @Resource
    String availableCharactersCaption;

    @Resource
    String bestCharactersCaption;

    @Resource
    String separateCharactersCaption;

    @Resource
    String usedCharactersCaption;

    @Resource
    String remainingTaxaCaption;

    @Resource
    String eliminatedTaxaCaption;

    @Resource
    String calculatingBestCaption;

    @Resource
    String displayingReportCaption;

    @Resource
    String identificationCompleteCaption;

    @Resource
    String availableCharactersCannotSeparateCaption;

    @Resource
    String noMatchingTaxaRemainCaption;

    @Resource
    String charactersExcludedCannotSeparateCaption;

    @Resource
    String mismatchesAllowCannotSeparateCaption;

    @Resource
    String selectCharacterKeywordsCaption;

    @Resource
    String selectTaxonKeywordsCaption;

    @Resource
    String logDialogTitle;

    @Resource
    String errorDlgTitle;

    @Resource
    String informationDlgTitle;

    @Resource
    String badlyFormedRTFContentMessage;

    @Resource
    String separateInformationMessage;

    @Resource
    String noHelpAvailableCaption;

    @Resource
    String saveReportToFilePrompt;

    @Resource
    String errorWritingToFileError;

    @Resource
    String errorReadingRTFFileError;

    @Resource
    String rtfFileTooLargeError;

    // GUI components
    private JPanel _rootPanel;
    private JSplitPane _rootSplitPane;
    private JSplitPane _innerSplitPaneRight;
    private JSplitPane _innerSplitPaneLeft;
    private JTextField _txtFldCmdBar;

    private Map<String, JMenu> _cmdMenus;

    private IntkeyContext _context;
    private JList _listAvailableCharacters;
    private JList _listUsedCharacters;
    private JList _listRemainingTaxa;
    private JList _listEliminatedTaxa;

    private CharacterCellRenderer _availableCharactersListCellRenderer;
    private AttributeCellRenderer _usedCharactersListCellRenderer;
    private TaxonCellRenderer _availableTaxaCellRenderer;
    private TaxonWithDifferenceCountCellRenderer _eliminatedTaxaCellRenderer;

    private DefaultListModel _availableCharacterListModel;
    private DefaultListModel _usedCharacterListModel;
    private DefaultListModel _availableTaxaListModel;
    private DefaultListModel _eliminatedTaxaListModel;

    private JLabel _lblNumAvailableCharacters;
    private JLabel _lblNumUsedCharacters;

    private BusyGlassPane _busyGlassPane = null;
    private Component _defaultGlassPane;

    private boolean _advancedMode = false;

    private List<Character> _foundAvailableCharacters = null;
    private List<Character> _foundUsedCharacters = null;
    private List<Item> _foundAvailableTaxa = null;
    private List<Item> _foundEliminatedTaxa = null;

    private List<JButton> _advancedModeOnlyDynamicButtons;
    private List<JButton> _normalModeOnlyDynamicButtons;
    private List<JButton> _activeOnlyWhenCharactersUsedButtons;
    private Map<JButton, String> _dynamicButtonsFullHelp;

    private JLabel _lblNumRemainingTaxa;
    private JLabel _lblEliminatedTaxa;
    private JButton _btnRestart;
    private JButton _btnBestOrder;
    private JButton _btnSeparate;
    private JButton _btnNaturalOrder;
    private JButton _btnDiffSpecimenTaxa;
    private JButton _btnSetTolerance;
    private JButton _btnSetMatch;
    private JButton _btnSubsetCharacters;
    private JButton _btnFindCharacter;
    private JButton _btnTaxonInfo;
    private JButton _btnDiffTaxa;
    private JButton _btnSubsetTaxa;
    private JButton _btnFindTaxon;
    private JButton _btnContextHelp;
    private JPanel _pnlAvailableCharacters;
    private JPanel _pnlAvailableCharactersButtons;
    private JPanel _pnlUsedCharacters;
    private JScrollPane _sclPnUsedCharacters;
    private JPanel _pnlUsedCharactersHeader;
    private JPanel _pnlRemainingTaxa;
    private JScrollPane _sclPnRemainingTaxa;
    private JPanel _pnlRemainingTaxaHeader;
    private JPanel _pnlRemainingTaxaButtons;
    private JPanel _pnlEliminatedTaxa;
    private JScrollPane _sclPnEliminatedTaxa;
    private JPanel _pnlEliminatedTaxaHeader;
    private JPanel _globalOptionBar;
    private JScrollPane _sclPaneAvailableCharacters;
    private JPanel _pnlAvailableCharactersHeader;
    private JPanel _pnlDynamicButtons;

    private RtfReportDisplayDialog _logDialog;

    private static String INTKEY_ICON_PATH = "/au/org/ala/delta/intkey/resources/icons";

    private static String MRU_FILES_PREF_KEY = "MRU";
    private static String MRU_FILES_SEPARATOR = "\n";
    private static String MRU_ITEM_SEPARATOR = ";";
    private static int MAX_SIZE_MRU = 4;

    private static String MODE_PREF_KEY = "MODE";
    private static String BASIC_MODE_PREF_VALUE = "BASIC";
    private static String ADVANCED_MODE_PREF_VALUE = "ADVANCED";

    private static String LAST_OPENED_DATASET_LOCATION_PREF_KEY = "LAST_OPENED_DATASET_LOCATION";

    private static String rtfFileExtension = "rtf";

    private String _datasetInitFileToOpen = null;
    private String _startupPreferencesFile = null;
    private boolean _suppressStartupImages = false;
    private File _lastOpenedDatasetDirectory = null;

    private ItemFormatter _taxonformatter;

    private HelpController _helpController;

    /**
     * Calls Desktop.getDesktop on a background thread as it's slow to
     * initialise
     */
    private SwingWorker<Desktop, Void> _desktopWorker;

    public static void main(String[] args) {
        setupMacSystemProperties(Intkey.class);
        launch(Intkey.class, args);
    }

    @Override
    protected void initialize(String[] args) {
        ResourceMap resourceMap = getContext().getResourceMap(Intkey.class);
        resourceMap.injectFields(this);

        // Define and parse command line arguments
        Options options = new Options();
        options.addOption("A", false, "Startup in advanced mode.");
        options.addOption("I", false, "Suppress display of startup images.");
        Option preferencesOption = OptionBuilder.withArgName("filename").hasArg().withDescription("Use the specified file as the preferences file.").create("P");
        options.addOption(preferencesOption);

        boolean cmdLineParseSuccess = true;
        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmdLine = parser.parse(options, args, false);

            if (cmdLine.hasOption("A")) {
                _advancedMode = true;
            }

            if (cmdLine.hasOption("I")) {
                _suppressStartupImages = true;
            }

            if (cmdLine.hasOption("P")) {
                _startupPreferencesFile = cmdLine.getOptionValue("P");
                if (StringUtils.isEmpty(_startupPreferencesFile)) {
                    cmdLineParseSuccess = false;
                }
            }

            if (cmdLine.getArgList().size() == 1) {
                _datasetInitFileToOpen = (String) cmdLine.getArgList().get(0);
            }

            if (cmdLine.getArgList().size() > 1) {
                cmdLineParseSuccess = false;
            }

        } catch (ParseException ex) {
            cmdLineParseSuccess = false;
        }

        if (!cmdLineParseSuccess) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Intkey [dataset-init-file] [options]", options);
            System.exit(0);
        }

        // If _startupInAdvancedMode has not already been set to true using the
        // "-A" command line option (see above),
        // Check saved application state for the mode (advanced or basic) that
        // was last used in the application.
        if (!_advancedMode) {
            _advancedMode = getPreviousApplicationMode();
        }

        // Get location of last opened dataset from saved application state
        _lastOpenedDatasetDirectory = getSavedLastOpenedDatasetDirectory();
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    protected void startup() {
        final JFrame mainFrame = getMainFrame();
        _defaultGlassPane = mainFrame.getGlassPane();
        mainFrame.setTitle("Intkey");
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setIconImages(IconHelper.getRedIconList());

        _helpController = new HelpController(HELPSET_PATH);

        _taxonformatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, true);
        _context = new IntkeyContext(this, this);

        _advancedModeOnlyDynamicButtons = new ArrayList<JButton>();
        _normalModeOnlyDynamicButtons = new ArrayList<JButton>();
        _activeOnlyWhenCharactersUsedButtons = new ArrayList<JButton>();
        _dynamicButtonsFullHelp = new HashMap<JButton, String>();

        ActionMap actionMap = getContext().getActionMap();

        _rootPanel = new JPanel();
        _rootPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        _rootPanel.setBackground(SystemColor.control);
        _rootPanel.setLayout(new BorderLayout(0, 0));

        _globalOptionBar = new JPanel();
        _globalOptionBar.setBorder(new EmptyBorder(0, 5, 0, 5));
        _rootPanel.add(_globalOptionBar, BorderLayout.NORTH);
        _globalOptionBar.setLayout(new BorderLayout(0, 0));

        _pnlDynamicButtons = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) _pnlDynamicButtons.getLayout();
        flowLayout_1.setVgap(0);
        flowLayout_1.setHgap(0);
        _globalOptionBar.add(_pnlDynamicButtons, BorderLayout.WEST);

        _btnContextHelp = new JButton();
        _btnContextHelp.setMinimumSize(new Dimension(30, 30));
        _btnContextHelp.setMaximumSize(new Dimension(30, 30));
        _btnContextHelp.setAction(actionMap.get("btnContextHelp"));
        _btnContextHelp.setPreferredSize(new Dimension(30, 30));
        _btnContextHelp.setMargin(new Insets(2, 5, 2, 5));
        _btnContextHelp.addActionListener(actionMap.get("btnContextHelp"));
        _globalOptionBar.add(_btnContextHelp, BorderLayout.EAST);

        _rootSplitPane = new JSplitPane();
        _rootSplitPane.setDividerSize(3);
        _rootSplitPane.setResizeWeight(0.5);
        _rootSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        _rootSplitPane.setContinuousLayout(true);
        _rootPanel.add(_rootSplitPane);

        _innerSplitPaneLeft = new JSplitPane();
        _innerSplitPaneLeft.setAlignmentX(Component.CENTER_ALIGNMENT);
        _innerSplitPaneLeft.setDividerSize(3);
        _innerSplitPaneLeft.setResizeWeight(0.5);

        _innerSplitPaneLeft.setContinuousLayout(true);
        _innerSplitPaneLeft.setOrientation(JSplitPane.VERTICAL_SPLIT);
        _rootSplitPane.setLeftComponent(_innerSplitPaneLeft);

        _pnlAvailableCharacters = new JPanel();
        _innerSplitPaneLeft.setLeftComponent(_pnlAvailableCharacters);
        _pnlAvailableCharacters.setLayout(new BorderLayout(0, 0));

        _sclPaneAvailableCharacters = new JScrollPane();
        _pnlAvailableCharacters.add(_sclPaneAvailableCharacters, BorderLayout.CENTER);

        _listAvailableCharacters = new JList();
        // _listAvailableCharacters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _listAvailableCharacters.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _listAvailableCharacters.setCellRenderer(_availableCharactersListCellRenderer);
        _listAvailableCharacters.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int selectedIndex = _listAvailableCharacters.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        try {
                            Character ch = (Character) _availableCharacterListModel.getElementAt(selectedIndex);
                            executeDirective(new UseDirective(), Integer.toString(ch.getCharacterId()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        _sclPaneAvailableCharacters.setViewportView(_listAvailableCharacters);

        _pnlAvailableCharactersHeader = new JPanel();
        _pnlAvailableCharacters.add(_pnlAvailableCharactersHeader, BorderLayout.NORTH);
        _pnlAvailableCharactersHeader.setLayout(new BorderLayout(0, 0));

        _lblNumAvailableCharacters = new JLabel();
        _lblNumAvailableCharacters.setFont(new Font("Tahoma", Font.PLAIN, 15));
        _lblNumAvailableCharacters.setText(MessageFormat.format(availableCharactersCaption, 0));
        _pnlAvailableCharactersHeader.add(_lblNumAvailableCharacters, BorderLayout.WEST);

        _pnlAvailableCharactersButtons = new JPanel();
        FlowLayout flowLayout = (FlowLayout) _pnlAvailableCharactersButtons.getLayout();
        flowLayout.setVgap(2);
        flowLayout.setHgap(2);
        _pnlAvailableCharactersHeader.add(_pnlAvailableCharactersButtons, BorderLayout.EAST);

        _btnRestart = new JButton();
        _btnRestart.setAction(actionMap.get("btnRestart"));
        _btnRestart.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnRestart);

        _btnBestOrder = new JButton();
        _btnBestOrder.setAction(actionMap.get("btnBestOrder"));
        _btnBestOrder.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnBestOrder);

        _btnSeparate = new JButton();
        _btnSeparate.setAction(actionMap.get("btnSeparate"));
        _btnSeparate.setVisible(_advancedMode);
        _btnSeparate.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnSeparate);

        _btnNaturalOrder = new JButton();
        _btnNaturalOrder.setAction(actionMap.get("btnNaturalOrder"));
        _btnNaturalOrder.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnNaturalOrder);

        _btnDiffSpecimenTaxa = new JButton();
        _btnDiffSpecimenTaxa.setAction(actionMap.get("btnDiffSpecimenTaxa"));
        _btnDiffSpecimenTaxa.setEnabled(false);
        _btnDiffSpecimenTaxa.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnDiffSpecimenTaxa);

        _btnSetTolerance = new JButton();
        _btnSetTolerance.setAction(actionMap.get("btnSetTolerance"));
        _btnSetTolerance.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnSetTolerance);

        _btnSetMatch = new JButton();
        _btnSetMatch.setAction(actionMap.get("btnSetMatch"));
        _btnSetMatch.setVisible(_advancedMode);
        _btnSetMatch.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnSetMatch);

        _btnSubsetCharacters = new JButton();
        _btnSubsetCharacters.setAction(actionMap.get("btnSubsetCharacters"));
        _btnSubsetCharacters.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnSubsetCharacters);

        _btnFindCharacter = new JButton();
        _btnFindCharacter.setAction(actionMap.get("btnFindCharacter"));
        _btnFindCharacter.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnFindCharacter);

        _pnlUsedCharacters = new JPanel();
        _innerSplitPaneLeft.setRightComponent(_pnlUsedCharacters);
        _pnlUsedCharacters.setLayout(new BorderLayout(0, 0));

        _sclPnUsedCharacters = new JScrollPane();
        _pnlUsedCharacters.add(_sclPnUsedCharacters, BorderLayout.CENTER);

        _listUsedCharacters = new JList();
        _listUsedCharacters.setCellRenderer(_usedCharactersListCellRenderer);
        _listUsedCharacters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _listUsedCharacters.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int selectedIndex = _listUsedCharacters.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        try {
                            Attribute attr = (Attribute) _usedCharacterListModel.getElementAt(selectedIndex);

                            if (_context.charactersFixed() && _context.getFixedCharactersList().contains(attr.getCharacter().getCharacterId())) {
                                return;
                            }

                            executeDirective(new ChangeDirective(), Integer.toString(attr.getCharacter().getCharacterId()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        _sclPnUsedCharacters.setViewportView(_listUsedCharacters);

        _pnlUsedCharactersHeader = new JPanel();
        _pnlUsedCharacters.add(_pnlUsedCharactersHeader, BorderLayout.NORTH);
        _pnlUsedCharactersHeader.setLayout(new BorderLayout(0, 0));

        _lblNumUsedCharacters = new JLabel();
        _lblNumUsedCharacters.setBorder(new EmptyBorder(7, 0, 7, 0));
        _lblNumUsedCharacters.setFont(new Font("Tahoma", Font.PLAIN, 15));
        _lblNumUsedCharacters.setText(MessageFormat.format(usedCharactersCaption, 0));
        _pnlUsedCharactersHeader.add(_lblNumUsedCharacters, BorderLayout.WEST);

        _innerSplitPaneRight = new JSplitPane();
        _innerSplitPaneRight.setDividerSize(3);
        _innerSplitPaneRight.setResizeWeight(0.5);
        _innerSplitPaneRight.setContinuousLayout(true);
        _innerSplitPaneRight.setOrientation(JSplitPane.VERTICAL_SPLIT);
        _rootSplitPane.setRightComponent(_innerSplitPaneRight);

        _pnlRemainingTaxa = new JPanel();
        _innerSplitPaneRight.setLeftComponent(_pnlRemainingTaxa);
        _pnlRemainingTaxa.setLayout(new BorderLayout(0, 0));

        _sclPnRemainingTaxa = new JScrollPane();
        _pnlRemainingTaxa.add(_sclPnRemainingTaxa, BorderLayout.CENTER);

        _listRemainingTaxa = new JList();
        _listRemainingTaxa.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                taxonSelectionChanged();
            }
        });

        _listRemainingTaxa.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    displayInfoForSelectedTaxa();
                }
            }
        });

        _sclPnRemainingTaxa.setViewportView(_listRemainingTaxa);

        _pnlRemainingTaxaHeader = new JPanel();
        _pnlRemainingTaxa.add(_pnlRemainingTaxaHeader, BorderLayout.NORTH);
        _pnlRemainingTaxaHeader.setLayout(new BorderLayout(0, 0));

        _lblNumRemainingTaxa = new JLabel();
        _lblNumRemainingTaxa.setFont(new Font("Tahoma", Font.PLAIN, 15));
        _lblNumRemainingTaxa.setText(MessageFormat.format(remainingTaxaCaption, 0));
        _pnlRemainingTaxaHeader.add(_lblNumRemainingTaxa, BorderLayout.WEST);

        _pnlRemainingTaxaButtons = new JPanel();
        FlowLayout fl_pnlRemainingTaxaButtons = (FlowLayout) _pnlRemainingTaxaButtons.getLayout();
        fl_pnlRemainingTaxaButtons.setVgap(2);
        fl_pnlRemainingTaxaButtons.setHgap(2);
        _pnlRemainingTaxaHeader.add(_pnlRemainingTaxaButtons, BorderLayout.EAST);

        _btnTaxonInfo = new JButton();
        _btnTaxonInfo.setAction(actionMap.get("btnTaxonInfo"));
        _btnTaxonInfo.setPreferredSize(new Dimension(30, 30));
        _pnlRemainingTaxaButtons.add(_btnTaxonInfo);

        _btnDiffTaxa = new JButton();
        _btnDiffTaxa.setAction(actionMap.get("btnDiffTaxa"));
        _btnDiffTaxa.setPreferredSize(new Dimension(30, 30));
        _btnDiffTaxa.setEnabled(false);
        _pnlRemainingTaxaButtons.add(_btnDiffTaxa);

        _btnSubsetTaxa = new JButton();
        _btnSubsetTaxa.setAction(actionMap.get("btnSubsetTaxa"));
        _btnSubsetTaxa.setPreferredSize(new Dimension(30, 30));
        _pnlRemainingTaxaButtons.add(_btnSubsetTaxa);

        _btnFindTaxon = new JButton();
        _btnFindTaxon.setAction(actionMap.get("btnFindTaxon"));
        _btnFindTaxon.setPreferredSize(new Dimension(30, 30));
        _pnlRemainingTaxaButtons.add(_btnFindTaxon);

        _pnlEliminatedTaxa = new JPanel();
        _innerSplitPaneRight.setRightComponent(_pnlEliminatedTaxa);
        _pnlEliminatedTaxa.setLayout(new BorderLayout(0, 0));

        _sclPnEliminatedTaxa = new JScrollPane();
        _pnlEliminatedTaxa.add(_sclPnEliminatedTaxa, BorderLayout.CENTER);

        _listEliminatedTaxa = new JList();
        _listEliminatedTaxa.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                taxonSelectionChanged();
            }
        });

        _listEliminatedTaxa.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    displayInfoForSelectedTaxa();
                }
            }
        });

        _sclPnEliminatedTaxa.setViewportView(_listEliminatedTaxa);

        _pnlEliminatedTaxaHeader = new JPanel();
        _pnlEliminatedTaxa.add(_pnlEliminatedTaxaHeader, BorderLayout.NORTH);
        _pnlEliminatedTaxaHeader.setLayout(new BorderLayout(0, 0));

        _lblEliminatedTaxa = new JLabel();
        _lblEliminatedTaxa.setBorder(new EmptyBorder(7, 0, 7, 0));
        _lblEliminatedTaxa.setFont(new Font("Tahoma", Font.PLAIN, 15));
        _lblEliminatedTaxa.setText(MessageFormat.format(eliminatedTaxaCaption, 0));
        _pnlEliminatedTaxaHeader.add(_lblEliminatedTaxa, BorderLayout.WEST);

        JMenuBar menuBar = buildMenus(_advancedMode);
        getMainView().setMenuBar(menuBar);

        _txtFldCmdBar = new JTextField();
        _txtFldCmdBar.setCaretColor(Color.WHITE);
        _txtFldCmdBar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String cmdStr = _txtFldCmdBar.getText();

                cmdStr = cmdStr.trim();
                if (_cmdMenus.containsKey(cmdStr)) {
                    JMenu cmdMenu = _cmdMenus.get(cmdStr);
                    cmdMenu.doClick();
                } else {
                    _context.parseAndExecuteDirective(cmdStr);
                }
                _txtFldCmdBar.setText(null);
            }
        });

        _txtFldCmdBar.setFont(new Font("Courier New", Font.BOLD, 13));
        _txtFldCmdBar.setForeground(SystemColor.text);
        _txtFldCmdBar.setBackground(Color.BLACK);
        _txtFldCmdBar.setOpaque(true);
        _txtFldCmdBar.setVisible(_advancedMode);
        _rootPanel.add(_txtFldCmdBar, BorderLayout.SOUTH);
        _txtFldCmdBar.setColumns(10);

        _logDialog = new RtfReportDisplayDialog(getMainFrame(), new SimpleRtfEditorKit(null), null, logDialogTitle);

        // Set context-sensitive help keys for toolbar buttons
        _helpController.setHelpKeyForComponent(_btnRestart, HELP_ID_CHARACTERS_TOOLBAR_RESTART);
        _helpController.setHelpKeyForComponent(_btnBestOrder, HELP_ID_CHARACTERS_TOOLBAR_BEST);
        _helpController.setHelpKeyForComponent(_btnSeparate, HELP_ID_CHARACTERS_TOOLBAR_SEPARATE);
        _helpController.setHelpKeyForComponent(_btnNaturalOrder, HELP_ID_CHARACTERS_TOOLBAR_NATURAL);
        _helpController.setHelpKeyForComponent(_btnDiffSpecimenTaxa, HELP_ID_CHARACTERS_TOOLBAR_DIFF_SPECIMEN_REMAINING);
        _helpController.setHelpKeyForComponent(_btnSetTolerance, HELP_ID_CHARACTERS_TOOLBAR_TOLERANCE);
        _helpController.setHelpKeyForComponent(_btnSetMatch, HELP_ID_CHARACTERS_TOOLBAR_SET_MATCH);
        _helpController.setHelpKeyForComponent(_btnSubsetCharacters, HELP_ID_CHARACTERS_TOOLBAR_SUBSET_CHARACTERS);
        _helpController.setHelpKeyForComponent(_btnFindCharacter, HELP_ID_CHARACTERS_TOOLBAR_FIND_CHARACTERS);

        _helpController.setHelpKeyForComponent(_btnTaxonInfo, HELP_ID_TAXA_TOOLBAR_INFO);
        _helpController.setHelpKeyForComponent(_btnDiffTaxa, HELP_ID_TAXA_TOOLBAR_DIFF_TAXA);
        _helpController.setHelpKeyForComponent(_btnSubsetTaxa, HELP_ID_TAXA_TOOLBAR_SUBSET_TAXA);
        _helpController.setHelpKeyForComponent(_btnFindTaxon, HELP_ID_TAXA_TOOLBAR_FIND_TAXA);

        // This mouse listener on the default glasspane is to assist with
        // context senstive help. It intercepts the mouse events,
        // determines what component was being clicked on, then takes the
        // appropriate action to provide help for the component
        _defaultGlassPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // Determine what point has been clicked on
                Point glassPanePoint = e.getPoint();
                Point containerPoint = SwingUtilities.convertPoint(getMainFrame().getGlassPane(), glassPanePoint, getMainFrame().getContentPane());
                Component component = SwingUtilities.getDeepestComponentAt(getMainFrame().getContentPane(), containerPoint.x, containerPoint.y);

                // Get the java help ID for this component. If none has been
                // defined, this will be null
                String helpID = _helpController.getHelpKeyForComponent(component);

                // change the cursor back to the normal one and take down the
                // classpane
                mainFrame.setCursor(Cursor.getDefaultCursor());
                mainFrame.getGlassPane().setVisible(false);

                // If a help ID was found, display the related help page in the
                // help viewer
                if (_helpController.getHelpKeyForComponent(component) != null) {
                    _helpController.helpAction().actionPerformed(new ActionEvent(component, 0, null));
                    _helpController.displayHelpTopic(mainFrame, helpID);
                } else {
                    // If a dynamically-defined toolbar button was clicked, show
                    // the help for this button in the ToolbarHelpDialog.
                    if (component instanceof JButton) {
                        JButton button = (JButton) component;
                        if (_dynamicButtonsFullHelp.containsKey(button)) {
                            String fullHelpText = _dynamicButtonsFullHelp.get(button);
                            if (fullHelpText == null) {
                                fullHelpText = noHelpAvailableCaption;
                            }
                            RTFBuilder builder = new RTFBuilder();
                            builder.startDocument();
                            builder.appendText(fullHelpText);
                            builder.endDocument();
                            ToolbarHelpDialog dlg = new ToolbarHelpDialog(mainFrame, builder.toString(), button.getIcon());
                            show(dlg);
                        }
                    }
                }
            }
        });

        show(_rootPanel);
    }

    @Override
    protected void ready() {
        super.ready();
        _rootSplitPane.setDividerLocation(2.0 / 3.0);
        _innerSplitPaneLeft.setDividerLocation(2.0 / 3.0);
        _innerSplitPaneRight.setDividerLocation(2.0 / 3.0);

        // If a dataset was supplied on the command line, load it
        if (_datasetInitFileToOpen != null) {
            // Need to surround file path in quotes, otherwise it may be broken
            // up into more than 1 token.
            executeDirective(new NewDatasetDirective(), "\"" + _datasetInitFileToOpen + "\"");
        }

        // If a preferences file was supplied on the command line, process it
        if (_startupPreferencesFile != null) {
            // Need to surround file path in quotes, otherwise it may be broken
            // up into more than 1 token.
            executeDirective(new PreferencesDirective(), "\"" + _startupPreferencesFile + "\"");
        }

        loadDesktopInBackground();
    }

    @Override
    protected void shutdown() {
        savePreviousApplicationMode(_advancedMode);
        saveLastOpenedDatasetDirectory(_lastOpenedDatasetDirectory);
        _context.cleanupForShutdown();
        super.shutdown();
    }

    private JMenuBar buildMenus(boolean advancedMode) {

        _cmdMenus = new HashMap<String, JMenu>();

        ActionMap actionMap = getContext().getActionMap();

        JMenuBar menuBar = new JMenuBar();

        menuBar.add(buildFileMenu(true, actionMap));

        if (advancedMode) {
            menuBar.add(buildQueriesMenu(actionMap));
            menuBar.add(buildBrowsingMenu(actionMap));
            menuBar.add(buildSettingsMenu(actionMap));
            menuBar.add(buildReExecuteMenu(actionMap));
        }
        menuBar.add(buildWindowMenu(actionMap));
        menuBar.add(buildHelpMenu(advancedMode, actionMap));

        return menuBar;
    }

    private JMenu buildFileMenu(boolean advancedMode, ActionMap actionMap) {
        MenuBuilder mnuFileBuilder = new MenuBuilder("mnuFile", _context);

        mnuFileBuilder.addDirectiveMenuItem("mnuItNewDataSet", new NewDatasetDirective());

        mnuFileBuilder.addPreconfiguredJMenu(buildRecentFilesMenu());

        if (_advancedMode) {
            mnuFileBuilder.addDirectiveMenuItem("mnuItPreferences", new PreferencesDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItContents", new ContentsDirective());

            mnuFileBuilder.addSeparator();

            mnuFileBuilder.startSubMenu("mnuFileCmds", true);
            mnuFileBuilder.addDirectiveMenuItem("mnuItFileInput", new FileInputDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItFileOutput", new FileOutputDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItFileDisplay", new FileDisplayDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItFileLog", new FileLogDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItFileJournal", new FileJournalDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItFileClose", new FileCloseDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItFileCharacters", new FileCharactersDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItFileTaxa", new FileCharactersDirective());
            mnuFileBuilder.endSubMenu();

            mnuFileBuilder.startSubMenu("mnuOutputCmds", true);
            mnuFileBuilder.addDirectiveMenuItem("mnuItOutputCharacters", new OutputCharactersDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItOutputTaxa", new OutputTaxaDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItOutputDescribe", new OutputDescribeDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItOutputSummary", new OutputSummaryDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItOutputDiagnose", new OutputDiagnoseDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItOutputDifferences", new OutputDifferencesDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItOutputSimilarities", new OutputSimilaritiesDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItOutputComment", new OutputCommentDirective());
            mnuFileBuilder.endSubMenu();
            mnuFileBuilder.addSeparator();

            mnuFileBuilder.addDirectiveMenuItem("mnuItComment", new CommentDirective());
            mnuFileBuilder.addDirectiveMenuItem("mnuItShow", new ShowDirective());

            mnuFileBuilder.addSeparator();

            mnuFileBuilder.addActionMenuItem(actionMap.get("mnuItNormalMode"));
        } else {
            mnuFileBuilder.addSeparator();
            mnuFileBuilder.addActionMenuItem(actionMap.get("mnuItAdvancedMode"));
        }

        mnuFileBuilder.addSeparator();

        mnuFileBuilder.addActionMenuItem(actionMap.get("mnuItExitApplication"));

        return mnuFileBuilder.getMenu();
    }

    private JMenu buildRecentFilesMenu() {
        JMenu mnuFileRecents = new JMenu();
        mnuFileRecents.setName("mnuFileRecents");

        List<Pair<String, String>> recentFiles = getPreviouslyUsedFiles();

        for (Pair<String, String> recentFile : recentFiles) {
            final String filePath = recentFile.getFirst();
            String title = recentFile.getSecond();

            JMenuItem mnuItRecentFile = new JMenuItem(title);
            mnuItRecentFile.setToolTipText(filePath);

            mnuItRecentFile.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    openPreviouslyOpenedFile(filePath);
                }
            });

            mnuFileRecents.add(mnuItRecentFile);
        }

        return mnuFileRecents;
    }

    private JMenu buildQueriesMenu(ActionMap actionMap) {
        JMenu mnuQueries = new JMenu();
        mnuQueries.setName("mnuQueries");

        JMenuItem mnuItRestart = new JMenuItem(new DirectiveAction(new RestartDirective(), _context));
        mnuItRestart.setName("mnuItRestart");
        mnuQueries.add(mnuItRestart);

        mnuQueries.addSeparator();

        JMenuItem mnuItDescribe = new JMenuItem(new DirectiveAction(new DescribeDirective(), _context));
        mnuItDescribe.setName("mnuItDescribe");
        mnuQueries.add(mnuItDescribe);

        JMenuItem mnuItDiagnose = new JMenuItem(new DirectiveAction(new DiagnoseDirective(), _context));
        mnuItDiagnose.setName("mnuItDiagnose");
        mnuItDiagnose.setEnabled(false);
        mnuQueries.add(mnuItDiagnose);

        mnuQueries.addSeparator();

        JMenuItem mnuItDifferences = new JMenuItem(new DirectiveAction(new DifferencesDirective(), _context));
        mnuItDifferences.setName("mnuItDifferences");
        mnuQueries.add(mnuItDifferences);
        JMenuItem mnuItSimilarities = new JMenuItem(new DirectiveAction(new SimilaritiesDirective(), _context));
        mnuItSimilarities.setName("mnuItSimilarities");
        mnuQueries.add(mnuItSimilarities);

        mnuQueries.addSeparator();

        JMenuItem mnuItSummary = new JMenuItem(new DirectiveAction(new SummaryDirective(), _context));
        mnuItSummary.setName("mnuItSummary");
        mnuQueries.add(mnuItSummary);

        return mnuQueries;
    }

    private JMenu buildBrowsingMenu(ActionMap actionMap) {
        JMenu mnuBrowsing = new JMenu();
        mnuBrowsing.setName("mnuBrowsing");

        JMenuItem mnuItCharacters = new JMenuItem(new DirectiveAction(new CharactersDirective(), _context));
        mnuItCharacters.setName("mnuItCharacters");
        mnuBrowsing.add(mnuItCharacters);
        JMenuItem mnuItTaxa = new JMenuItem(new DirectiveAction(new TaxaDirective(), _context));
        mnuItTaxa.setName("mnuItTaxa");
        mnuBrowsing.add(mnuItTaxa);

        mnuBrowsing.addSeparator();

        JMenu mnuFind = new JMenu();
        mnuFind.setName("mnuFind");
        JMenuItem mnuItFindCharacters = new JMenuItem(new DirectiveAction(new FindCharactersDirective(), _context));
        mnuItFindCharacters.setName("mnuItFindCharacters");
        mnuFind.add(mnuItFindCharacters);
        JMenuItem mnuItFindTaxa = new JMenuItem(new DirectiveAction(new FindTaxaDirective(), _context));
        mnuItFindTaxa.setName("mnuItFindTaxa");
        mnuFind.add(mnuItFindTaxa);

        mnuBrowsing.add(mnuFind);

        mnuBrowsing.addSeparator();

        JMenu mnuIllustrate = new JMenu();
        mnuIllustrate.setName("mnuIllustrate");
        JMenuItem mnuItIllustrateCharacters = new JMenuItem(new DirectiveAction(new IllustrateCharactersDirective(), _context));
        mnuItIllustrateCharacters.setName("mnuItIllustrateCharacters");
        mnuIllustrate.add(mnuItIllustrateCharacters);
        JMenuItem mnuItIllustrateTaxa = new JMenuItem(new DirectiveAction(new IllustrateTaxaDirective(), _context));
        mnuItIllustrateTaxa.setName("mnuItIllustrateTaxa");
        mnuIllustrate.add(mnuItIllustrateTaxa);

        mnuBrowsing.add(mnuIllustrate);

        mnuBrowsing.addSeparator();

        JMenuItem mnuItInformation = new JMenuItem(new DirectiveAction(new InformationDirective(), _context));
        mnuItInformation.setName("mnuItInformation");
        mnuBrowsing.add(mnuItInformation);

        return mnuBrowsing;
    }

    private JMenu buildSettingsMenu(ActionMap actionMap) {
        JMenu mnuSettings = new JMenu();
        mnuSettings.setName("mnuSettings");

        // "Set" submenu
        MenuBuilder mnuSetBuilder = new MenuBuilder("mnuSet", _context);

        mnuSetBuilder.startSubMenu("mnuAutotolerance", true);
        mnuSetBuilder.addOnOffDirectiveMenuItem("mnuItAutotoleranceOn", new SetAutoToleranceDirective(), true);
        mnuSetBuilder.addOnOffDirectiveMenuItem("mnuItAutotoleranceOff", new SetAutoToleranceDirective(), false);
        mnuSetBuilder.endSubMenu();

        mnuSetBuilder.startSubMenu("mnuDemonstration", true);
        mnuSetBuilder.addOnOffDirectiveMenuItem("mnuItDemonstrationOn", new SetDemonstrationDirective(), true);
        mnuSetBuilder.addOnOffDirectiveMenuItem("mnuItDemonstrationOff", new SetDemonstrationDirective(), false);
        mnuSetBuilder.endSubMenu();

        mnuSetBuilder.addDirectiveMenuItem("mnuItDiagLevel", new SetDiagLevelDirective());

        mnuSetBuilder.startSubMenu("mnuDiagType", true);
        mnuSetBuilder.addDirectiveMenuItem("mnuItDiagTypeSpecimens", new SetDiagTypeSpecimensDirective());
        mnuSetBuilder.addDirectiveMenuItem("mnuItDiagTypeTaxa", new SetDiagTypeTaxaDirective());
        mnuSetBuilder.endSubMenu();

        mnuSetBuilder.addDirectiveMenuItem("mnuItExact", new SetExactDirective());

        mnuSetBuilder.startSubMenu("mnuFix", true);
        mnuSetBuilder.addOnOffDirectiveMenuItem("mnuItFixOn", new SetFixDirective(), true);
        mnuSetBuilder.addOnOffDirectiveMenuItem("mnuItFixOff", new SetFixDirective(), false);
        mnuSetBuilder.endSubMenu();

        mnuSetBuilder.addDirectiveMenuItem("mnuItImagePath", new SetImagePathDirective());

        mnuSetBuilder.addDirectiveMenuItem("mnuItInfoPath", new SetInfoPathDirective());

        mnuSetBuilder.addDirectiveMenuItem("mnuItMatch", new SetMatchDirective());

        mnuSetBuilder.addDirectiveMenuItem("mnuItRbase", new SetRBaseDirective());

        mnuSetBuilder.addDirectiveMenuItem("mnuItReliabilities", new SetReliabilitiesDirective());

        mnuSetBuilder.addDirectiveMenuItem("mnuItStopBest", new SetStopBestDirective());

        mnuSetBuilder.addDirectiveMenuItem("mnuItTolerance", new SetToleranceDirective());

        mnuSetBuilder.addDirectiveMenuItem("mnuItVaryWt", new SetVaryWtDirective());

        mnuSettings.add(mnuSetBuilder.getMenu());

        // "Display" submenu
        MenuBuilder mnuDisplayBuilder = new MenuBuilder("mnuDisplay", _context);
        mnuDisplayBuilder.startSubMenu("mnuCharacterOrder", true);
        mnuDisplayBuilder.addDirectiveMenuItem("mnuItCharacterOrderBest", new DisplayCharacterOrderBestDirective());
        mnuDisplayBuilder.addDirectiveMenuItem("mnuItCharacterOrderNatural", new DisplayCharacterOrderNaturalDirective());
        mnuDisplayBuilder.addDirectiveMenuItem("mnuItCharacterOrderSeparate", new DisplayCharacterOrderSeparateDirective());
        mnuDisplayBuilder.endSubMenu();

        mnuDisplayBuilder.startSubMenu("mnuComments", true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItCommentsOn", new DisplayCommentsDirective(), true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItCommentsOff", new DisplayCommentsDirective(), false);
        mnuDisplayBuilder.endSubMenu();

        mnuDisplayBuilder.startSubMenu("mnuContinuous", true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItContinuousOn", new DisplayContinuousDirective(), true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItContinuousOff", new DisplayContinuousDirective(), false);
        mnuDisplayBuilder.endSubMenu();

        mnuDisplayBuilder.startSubMenu("mnuEndIdentify", true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItEndIdentifyOn", new DisplayEndIdentifyDirective(), true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItEndIdentifyOff", new DisplayEndIdentifyDirective(), false);
        mnuDisplayBuilder.endSubMenu();

        mnuDisplayBuilder.addDirectiveMenuItem("mnuItImages", new DisplayImagesDirective());

        mnuDisplayBuilder.startSubMenu("mnuInapplicables", true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItInapplicablesOn", new DisplayInapplicablesDirective(), true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItInapplicablesOff", new DisplayInapplicablesDirective(), false);
        mnuDisplayBuilder.endSubMenu();

        mnuDisplayBuilder.startSubMenu("mnuInput", true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItInputOn", new DisplayInputDirective(), true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItInputOff", new DisplayInputDirective(), false);
        mnuDisplayBuilder.endSubMenu();

        mnuDisplayBuilder.startSubMenu("mnuKeywords", true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItKeywordsOn", new DisplayKeywordsDirective(), true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItKeywordsOff", new DisplayKeywordsDirective(), false);
        mnuDisplayBuilder.endSubMenu();

        mnuDisplayBuilder.startSubMenu("mnuLog", true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItLogOn", new DisplayLogDirective(), true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItLogOff", new DisplayLogDirective(), false);
        mnuDisplayBuilder.endSubMenu();

        mnuDisplayBuilder.startSubMenu("mnuNumbering", true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItNumberingOn", new DisplayNumberingDirective(), true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItNumberingOff", new DisplayNumberingDirective(), false);
        mnuDisplayBuilder.endSubMenu();

        mnuDisplayBuilder.startSubMenu("mnuScaled", true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItScaledOn", new DisplayScaledDirective(), true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItScaledOff", new DisplayScaledDirective(), false);
        mnuDisplayBuilder.endSubMenu();

        mnuDisplayBuilder.startSubMenu("mnuUnknowns", true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItUnknownsOn", new DisplayUnknownsDirective(), true);
        mnuDisplayBuilder.addOnOffDirectiveMenuItem("mnuItUnknownsOff", new DisplayUnknownsDirective(), false);
        mnuDisplayBuilder.endSubMenu();

        mnuSettings.add(mnuDisplayBuilder.getMenu());

        // "Define" submenu
        MenuBuilder mnuDefineBuilder = new MenuBuilder("mnuDefine", _context);
        mnuDefineBuilder.addDirectiveMenuItem("mnuItDefineCharacters", new DefineCharactersDirective());
        mnuDefineBuilder.addDirectiveMenuItem("mnuItDefineTaxa", new DefineTaxaDirective());
        mnuDefineBuilder.addDirectiveMenuItem("mnuItDefineNames", new DefineNamesDirective());
        mnuDefineBuilder.addDirectiveMenuItem("mnuItDefineButton", new DefineButtonDirective());
        mnuDefineBuilder.addDirectiveMenuItem("mnuItDefineEndIdentify", new DefineEndIdentifyDirective());
        mnuDefineBuilder.addDirectiveMenuItem("mnuItDefineInformation", new DefineInformationDirective());
        mnuDefineBuilder.addDirectiveMenuItem("mnuItDefineSubjects", new DefineSubjectsDirective());

        mnuSettings.add(mnuDefineBuilder.getMenu());

        // "Include" submenu
        MenuBuilder mnuIncludeBuilder = new MenuBuilder("mnuInclude", _context);
        mnuIncludeBuilder.addDirectiveMenuItem("mnuItIncludeCharacters", new IncludeCharactersDirective());
        mnuIncludeBuilder.addDirectiveMenuItem("mnuItIncludeTaxa", new IncludeTaxaDirective());
        mnuSettings.add(mnuIncludeBuilder.getMenu());

        // "Exclude" submenu
        MenuBuilder mnuExcludeBuilder = new MenuBuilder("mnuExclude", _context);
        mnuExcludeBuilder.addDirectiveMenuItem("mnuItExcludeCharacters", new ExcludeCharactersDirective());
        mnuExcludeBuilder.addDirectiveMenuItem("mnuItExcludeTaxa", new ExcludeTaxaDirective());
        mnuSettings.add(mnuExcludeBuilder.getMenu());

        // "Status" submenu
        MenuBuilder mnuStatusBuilder = new MenuBuilder("mnuStatus", _context);
        mnuStatusBuilder.addDirectiveMenuItem("mnuItStatusDisplay", new StatusDisplayDirective());

        mnuStatusBuilder.startSubMenu("mnuStatusInclude", true);
        mnuStatusBuilder.addDirectiveMenuItem("mnuItStatusIncludeCharacters", new StatusIncludeCharactersDirective());
        mnuStatusBuilder.addDirectiveMenuItem("mnuItStatusIncludeTaxa", new StatusIncludeTaxaDirective());
        mnuStatusBuilder.endSubMenu();

        mnuStatusBuilder.startSubMenu("mnuStatusExclude", true);
        mnuStatusBuilder.addDirectiveMenuItem("mnuItStatusExcludeCharacters", new StatusExcludeCharactersDirective());
        mnuStatusBuilder.addDirectiveMenuItem("mnuItStatusExcludeTaxa", new StatusExcludeTaxaDirective());
        mnuStatusBuilder.endSubMenu();

        mnuStatusBuilder.addDirectiveMenuItem("mnuItStatusFiles", new StatusFilesDirective());
        mnuStatusBuilder.addDirectiveMenuItem("mnuItStatusSet", new StatusSetDirective());
        mnuStatusBuilder.addDirectiveMenuItem("mnuItStatusAll", new StatusAllDirective());
        mnuSettings.add(mnuStatusBuilder.getMenu());

        return mnuSettings;
    }

    private JMenu buildReExecuteMenu(ActionMap actionMap) {
        JMenu mnuReExecute = new JMenu("ReExecute...");
        mnuReExecute.setName("mnuReExecute");

        JMenuItem mnuItReExecute = new JMenuItem();
        mnuItReExecute.setAction(actionMap.get("mnuItReExecute"));
        mnuReExecute.add(mnuItReExecute);

        return mnuReExecute;
    }

    private JMenu buildWindowMenu(ActionMap actionMap) {
        JMenu mnuWindow = new JMenu();
        mnuWindow.setName("mnuWindow");

        JMenuItem mnuItCascade = new JMenuItem();
        mnuItCascade.setAction(actionMap.get("mnuItCascadeWindows"));
        mnuItCascade.setEnabled(true);
        mnuWindow.add(mnuItCascade);

        JMenuItem mnuItTile = new JMenuItem();
        mnuItTile.setAction(actionMap.get("mnuItTileWindows"));
        mnuItTile.setEnabled(true);
        mnuWindow.add(mnuItTile);

        mnuWindow.addSeparator();

        JMenuItem mnuItCloseAll = new JMenuItem();
        mnuItCloseAll.setAction(actionMap.get("mnuItCloseAllWindows"));
        mnuItCloseAll.setEnabled(true);
        mnuWindow.add(mnuItCloseAll);

        mnuWindow.addSeparator();

        JMenu mnuLF = new JMenu();
        mnuLF.setName("mnuLF");
        mnuWindow.add(mnuLF);

        JMenuItem mnuItMetalLF = new JMenuItem();
        mnuItMetalLF.setAction(actionMap.get("metalLookAndFeel"));
        mnuLF.add(mnuItMetalLF);

        JMenuItem mnuItWindowsLF = new JMenuItem();
        mnuItWindowsLF.setAction(actionMap.get("systemLookAndFeel"));
        mnuLF.add(mnuItWindowsLF);

        try {
            // Nimbus L&F was added in update java 6 update 10.
            Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance();
            JMenuItem mnuItNimbusLF = new JMenuItem();
            mnuItNimbusLF.setAction(actionMap.get("nimbusLookAndFeel"));
            mnuLF.add(mnuItNimbusLF);
        } catch (Exception e) {
            // The Nimbus L&F is not available, no matter.
        }

        return mnuWindow;
    }

    private JMenu buildHelpMenu(boolean advancedMode, ActionMap actionMap) {
        JMenu mnuHelp = new JMenu();
        mnuHelp.setName("mnuHelp");
        JMenuItem mnuItHelpTopics = new JMenuItem();
        mnuItHelpTopics.setName("mnuItHelpTopics");
        mnuItHelpTopics.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UIUtils.displayHelpTopic(HELP_ID_TOPICS, getMainFrame(), e);
            }
        });
        mnuHelp.add(mnuItHelpTopics);

        if (advancedMode) {
            JMenuItem mnuItHelpCommands = new JMenuItem();
            mnuItHelpCommands.setName("mnuItHelpCommands");
            mnuItHelpCommands.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    UIUtils.displayHelpTopic(HELP_ID_COMMANDS, getMainFrame(), e);
                }

            });
            mnuHelp.add(mnuItHelpCommands);
        }

        if (isMac()) {
            configureMacAboutBox(actionMap.get("mnuItHelpAbout"));
        } else {
            JMenuItem mnuItAbout = new JMenuItem();
            mnuItAbout.setAction(actionMap.get("mnuItHelpAbout"));
            mnuHelp.add(mnuItAbout);
        }

        return mnuHelp;
    }

    // ============== File menu actions ==============================
    @Action
    public void mnuItNormalMode() {
        toggleAdvancedMode();
    }

    @Action
    public void mnuItAdvancedMode() {
        toggleAdvancedMode();
    }

    private void toggleAdvancedMode() {
        _advancedMode = !_advancedMode;

        if (_advancedMode) {
            JMenuBar menuBar = buildMenus(true);
            getMainFrame().setJMenuBar(menuBar);
            _btnSeparate.setVisible(true);
            _btnSetMatch.setVisible(true);
            _txtFldCmdBar.setVisible(true);
            _context.setImageDisplayMode(ImageDisplayMode.MANUAL);
        } else {
            JMenuBar menuBar = buildMenus(false);
            getMainFrame().setJMenuBar(menuBar);
            _btnSeparate.setVisible(false);
            _btnSetMatch.setVisible(false);
            _txtFldCmdBar.setVisible(false);
            _context.setImageDisplayMode(ImageDisplayMode.AUTO);
        }

        // Need to update available characters because character separating
        // powers
        // are only shown in best ordering when in advanced mode.
        updateAvailableCharacters();

        // Update button toolbar - some buttons are only shown in normal or
        // advanced mode
        updateDynamicButtons();

        ResourceMap resourceMap = getContext().getResourceMap(Intkey.class);
        resourceMap.injectComponents(getMainFrame());
        _rootPanel.revalidate();
    }

    @Action
    public void mnuItExitApplication() {
        exit();
    }

    // ============================ ReExecute menu actions
    // ===========================

    @Action
    public void mnuItReExecute() {
        ReExecuteDialog dlg = new ReExecuteDialog(getMainFrame(), _context.getExecutedDirectives(), _context);
        dlg.setVisible(true);
    }

    // ============================= Window menu actions
    // ==============================
    @Action
    public void mnuItCascadeWindows() {
        IntKeyDialogController.cascadeWindows();
    }

    @Action
    public void mnuItTileWindows() {
        IntKeyDialogController.tileWindows();
    }

    @Action
    public void mnuItCloseAllWindows() {
        IntKeyDialogController.closeWindows();
    }

    // ====================== Help menu actions
    // ====================================
    @Action
    public void mnuItHelpIntroduction() {
    }

    @Action
    public void mnuItHelpCommands() {
    }

    @Action
    public void mnuItHelpAbout() {
        AboutBox aboutBox = new AboutBox(getMainFrame(), IconHelper.createRed32ImageIcon());
        show(aboutBox);
    }

    // ============================== Global option buttons
    // ================================

    @Action
    public void btnContextHelp() {
        // Get the HelpOnItemCursor. This is installed by the java help library.
        Cursor onItemCursor = (Cursor) UIManager.get("HelpOnItemCursor");
        getMainFrame().setCursor(onItemCursor);

        // display the (default) glasspane. This is used to intercept mouse
        // events and determine how to handle help
        // for the component that has been clicked on. See the lister defined
        // for the default glasspane in the
        // startup() method
        getMainFrame().getGlassPane().setVisible(true);
    }

    // ========================= Character toolbar button actions
    // ===================

    @Action
    public void btnRestart() {
        executeDirective(new RestartDirective(), null);
    }

    @Action
    public void btnBestOrder() {
        executeDirective(new DisplayCharacterOrderBestDirective(), null);
    }

    @Action
    public void btnSeparate() {
        Object[] selectedRemainingTaxa = _listRemainingTaxa.getSelectedValues();
        if (selectedRemainingTaxa.length != 1) {
            displayInformationMessage(separateInformationMessage);
            return;
        }

        Item selectedTaxon = (Item) selectedRemainingTaxa[0];

        executeDirective(new DisplayCharacterOrderSeparateDirective(), Integer.toString(selectedTaxon.getItemNumber()));
    }

    @Action
    public void btnNaturalOrder() {
        executeDirective(new DisplayCharacterOrderNaturalDirective(), null);
    }

    @Action
    public void btnDiffSpecimenTaxa() {
        executeDirective(new DifferencesDirective(), "/E (specimen remaining) all");
    }

    @Action
    public void btnSetTolerance() {
        executeDirective(new SetToleranceDirective(), null);
    }

    @Action
    public void btnSetMatch() {
        executeDirective(new SetMatchDirective(), null);
    }

    @Action
    public void btnSubsetCharacters() {
        executeDirective(new IncludeCharactersDirective(), null);
    }

    @Action
    public void btnFindCharacter() {
        new FindInCharactersDialog(this, _context).setVisible(true);
    }

    // ============================= Taxon toolbar button actions
    // ===========================

    @Action
    public void btnTaxonInfo() {
        displayInfoForSelectedTaxa();
    }

    private void displayInfoForSelectedTaxa() {
        List<Item> selectedTaxa = new ArrayList<Item>();

        for (int i : _listRemainingTaxa.getSelectedIndices()) {
            selectedTaxa.add((Item) _availableTaxaListModel.getElementAt(i));
        }

        for (int i : _listEliminatedTaxa.getSelectedIndices()) {
            selectedTaxa.add((Item) _eliminatedTaxaListModel.getElementAt(i));
        }

        // If no taxa were selected, show the information for all available taxa
        if (selectedTaxa.isEmpty()) {
            selectedTaxa.addAll(_context.getAvailableTaxa());
        }

        TaxonInformationDialog dlg = new TaxonInformationDialog(getMainFrame(), selectedTaxa, _context, _context.getImageDisplayMode() != ImageDisplayMode.OFF);
        show(dlg);
    }

    @Action
    public void btnDiffTaxa() {
        List<Item> selectedTaxa = new ArrayList<Item>();

        for (int i : _listRemainingTaxa.getSelectedIndices()) {
            selectedTaxa.add((Item) _availableTaxaListModel.getElementAt(i));
        }

        for (int i : _listEliminatedTaxa.getSelectedIndices()) {
            selectedTaxa.add((Item) _eliminatedTaxaListModel.getElementAt(i));
        }

        StringBuilder directiveTextBuilder = new StringBuilder();
        directiveTextBuilder.append("/E /I /U /X (");
        for (Item taxon : selectedTaxa) {
            directiveTextBuilder.append(" ");
            directiveTextBuilder.append(taxon.getItemNumber());
        }
        directiveTextBuilder.append(") all");

        executeDirective(new DifferencesDirective(), directiveTextBuilder.toString());
    }

    @Action
    public void btnSubsetTaxa() {
        executeDirective(new IncludeTaxaDirective(), null);
    }

    @Action
    public void btnFindTaxon() {
        new FindInTaxaDialog(this).setVisible(true);
    }

    // =========================================================================================

    /**
     * We do this because Desktop.getDesktop() can be very slow
     */
    private void loadDesktopInBackground() {
        _desktopWorker = new SwingWorker<Desktop, Void>() {

            protected Desktop doInBackground() {
                if (Desktop.isDesktopSupported()) {
                    return Desktop.getDesktop();
                } else {
                    return null;
                }
            }
        };
        _desktopWorker.execute();
    }

    private void executeDirective(AbstractDirective<IntkeyContext> dir, String data) {
        try {
            dir.parseAndProcess(_context, data);
        } catch (Exception ex) {
            Logger.error(ex);
            String msg;
            if (ex instanceof IntkeyDirectiveParseException) {
                msg = ex.getMessage();
            } else {
                msg = String.format("Error occurred while processing '%s' command: %s", data.toUpperCase(), ex.getMessage());
            }
            displayErrorMessage(msg);
            Logger.error(msg);
        }
    }

    private void taxonSelectionChanged() {
        int[] remainingTaxaSelectedIndicies = _listRemainingTaxa.getSelectedIndices();
        int[] eliminatedTaxaSelectedIndicies = _listEliminatedTaxa.getSelectedIndices();

        _btnDiffTaxa.setEnabled((remainingTaxaSelectedIndicies.length + eliminatedTaxaSelectedIndicies.length) >= 2);
    }

    private void initializeIdentification() {
        handleUpdateAll();
    }

    private void updateAvailableCharacters() {

        IntkeyCharacterOrder charOrder = _context.getCharacterOrder();

        Item taxonToSeparate = null;
        String formattedTaxonToSeparateName = null;

        switch (charOrder) {
        case SEPARATE:
            taxonToSeparate = _context.getDataset().getItem(_context.getTaxonToSeparate());
            formattedTaxonToSeparateName = _taxonformatter.formatItemDescription(taxonToSeparate);
            if (!_context.getAvailableTaxa().contains(taxonToSeparate)) {
                _listAvailableCharacters.setModel(new DefaultListModel());
                _lblNumAvailableCharacters.setText(MessageFormat.format(separateCharactersCaption, formattedTaxonToSeparateName, 0));
                break;
            }

            // If taxon to separate has not been eliminated, drop through and
            // display the best characters for taxon separation
        case BEST:
            LinkedHashMap<Character, Double> bestCharactersMap = _context.getBestOrSeparateCharacters();
            if (bestCharactersMap != null) {
                if (charOrder == IntkeyCharacterOrder.BEST) {
                    _lblNumAvailableCharacters.setText(MessageFormat.format(bestCharactersCaption, bestCharactersMap.keySet().size()));
                } else {
                    _lblNumAvailableCharacters.setText(MessageFormat.format(separateCharactersCaption, formattedTaxonToSeparateName, bestCharactersMap.keySet().size()));
                }
                if (bestCharactersMap.isEmpty()) {
                    handleNoAvailableCharacters();
                    return;
                } else {
                    _availableCharacterListModel = new DefaultListModel();
                    for (Character ch : bestCharactersMap.keySet()) {
                        _availableCharacterListModel.addElement(ch);
                    }
                    _availableCharacterListModel.copyInto(bestCharactersMap.keySet().toArray());

                    // Only display character separating powers if in advanced
                    // mode.
                    if (_advancedMode) {
                        _availableCharactersListCellRenderer = new BestCharacterCellRenderer(bestCharactersMap, _context.displayNumbering());
                    } else {
                        _availableCharactersListCellRenderer = new CharacterCellRenderer(_context.displayNumbering());
                    }
                    _listAvailableCharacters.setCellRenderer(_availableCharactersListCellRenderer);
                    _listAvailableCharacters.setModel(_availableCharacterListModel);
                }
            } else {
                _availableCharacterListModel = null;

                // The best characters list is not cached and needs to be
                // calculated. This is a
                // long-running operation so use a
                // SwingWorker to do it on a different thread, and update
                // the
                // available characters list when
                // it is complete.
                GetBestCharactersWorker worker = new GetBestCharactersWorker(_context);
                worker.execute();

                // Show the busy glass pane with a message if worker has not
                // completed within
                // 250 milliseconds. This avoids "flickering" of the
                // glasspane
                // when it takes a
                // very short time to calculate the best characters.
                try {
                    Thread.sleep(250);
                    if (!worker.isDone()) {
                        displayBusyMessage(calculatingBestCaption);
                    }
                } catch (InterruptedException ex) {
                    // do nothing
                }

                return;
            }

            break;
        case NATURAL:
            List<Character> availableCharacters = new ArrayList<Character>(_context.getAvailableCharacters());
            _lblNumAvailableCharacters.setText(MessageFormat.format(availableCharactersCaption, availableCharacters.size()));
            if (availableCharacters.size() == 0) {
                handleNoAvailableCharacters();
                return;
            } else {
                _availableCharacterListModel = new DefaultListModel();
                for (Character ch : availableCharacters) {
                    _availableCharacterListModel.addElement(ch);
                }
                _availableCharactersListCellRenderer = new CharacterCellRenderer(_context.displayNumbering());
                _listAvailableCharacters.setCellRenderer(_availableCharactersListCellRenderer);
                _listAvailableCharacters.setModel(_availableCharacterListModel);
            }
            break;
        default:
            throw new RuntimeException("Unrecognized character order");
        }

        // The viewport of the available characters scroll pane may be
        // displaying a
        // message due to an investigation finishing, or no characters being
        // available
        // previously. Ensure that the available characters list is now
        // displayed again.
        if (!_sclPaneAvailableCharacters.getViewport().getView().equals(_listAvailableCharacters)) {
            _sclPaneAvailableCharacters.setViewportView(_listAvailableCharacters);
            _sclPaneAvailableCharacters.revalidate();
        }
    }

    private void handleNoAvailableCharacters() {
        String message = null;

        if (_context.getIncludedCharacters().size() < _context.getDataset().getNumberOfCharacters()) { // characters
            message = charactersExcludedCannotSeparateCaption;
        } else {
            if (_context.getTolerance() > 0) {
                message = mismatchesAllowCannotSeparateCaption;
            } else {
                message = availableCharactersCannotSeparateCaption;
            }
        }

        MessagePanel messagePanel = new MessagePanel(message, HELP_ID_NO_CHARACTERS_REMAINING);
        _sclPaneAvailableCharacters.setViewportView(messagePanel);
        _sclPaneAvailableCharacters.revalidate();
    }

    private void handleNoAvailableTaxa() {

    }

    /**
     * Used to calculate the best characters in a separate thread, then update
     * the UI accordingly when the operation is finished
     * 
     * @author ChrisF
     * 
     */
    /**
     * @author ChrisF
     * 
     */
    private class GetBestCharactersWorker extends SwingWorker<Void, Void> {

        private IntkeyContext _context;

        public GetBestCharactersWorker(IntkeyContext context) {
            super();
            _context = context;
        }

        @Override
        protected Void doInBackground() throws Exception {
            _context.calculateBestOrSeparateCharacters();
            return null;
        }

        @Override
        protected void done() {
            updateAvailableCharacters();
            removeBusyMessage();
        }
    }

    private void updateUsedCharacters() {

        Specimen specimen = _context.getSpecimen();
        List<Character> usedCharacters = specimen.getUsedCharacters();

        List<Attribute> usedCharacterValues = new ArrayList<Attribute>();
        for (Character ch : usedCharacters) {
            usedCharacterValues.add(specimen.getAttributeForCharacter(ch));
        }

        _usedCharacterListModel = new DefaultListModel();
        for (Attribute attr : usedCharacterValues) {
            _usedCharacterListModel.addElement(attr);
        }
        _usedCharactersListCellRenderer = new AttributeCellRenderer(_context.displayNumbering(), _context.getDataset().getOrWord());
        _listUsedCharacters.setCellRenderer(_usedCharactersListCellRenderer);
        _listUsedCharacters.setModel(_usedCharacterListModel);

        _lblNumUsedCharacters.setText(MessageFormat.format(usedCharactersCaption, _usedCharacterListModel.getSize()));
    }

    private void updateAvailableTaxa(List<Item> availableTaxa, Map<Item, Set<Character>> taxaDifferingCharacters) {
        _availableTaxaListModel = new DefaultListModel();

        if (_context.getTolerance() > 0 && taxaDifferingCharacters != null) {
            // sort available taxa by difference count
            Collections.sort(availableTaxa, new DifferenceCountComparator(taxaDifferingCharacters));
            _availableTaxaCellRenderer = new TaxonWithDifferenceCountCellRenderer(taxaDifferingCharacters, _context.displayNumbering(), _context.displayComments());
        } else {
            _availableTaxaCellRenderer = new TaxonCellRenderer(_context.displayNumbering(), _context.displayComments());
        }

        for (Item taxon : availableTaxa) {
            _availableTaxaListModel.addElement(taxon);
        }

        _listRemainingTaxa.setCellRenderer(_availableTaxaCellRenderer);
        _listRemainingTaxa.setModel(_availableTaxaListModel);

        _lblNumRemainingTaxa.setText(MessageFormat.format(remainingTaxaCaption, _availableTaxaListModel.getSize()));

        _listRemainingTaxa.repaint();
    }

    private void updateUsedTaxa(List<Item> eliminatedTaxa, Map<Item, Set<Character>> taxaDifferingCharacters) {
        // sort eliminated taxa by difference count
        Collections.sort(eliminatedTaxa, new DifferenceCountComparator(taxaDifferingCharacters));

        _eliminatedTaxaListModel = new DefaultListModel();

        for (Item taxon : eliminatedTaxa) {
            _eliminatedTaxaListModel.addElement(taxon);
        }

        _eliminatedTaxaCellRenderer = new TaxonWithDifferenceCountCellRenderer(taxaDifferingCharacters, _context.displayNumbering(), _context.displayComments());

        _listEliminatedTaxa.setCellRenderer(_eliminatedTaxaCellRenderer);
        _listEliminatedTaxa.setModel(_eliminatedTaxaListModel);

        _lblEliminatedTaxa.setText(MessageFormat.format(eliminatedTaxaCaption, _eliminatedTaxaListModel.getSize()));

        _listEliminatedTaxa.repaint();
    }

    // ================================== IntkeyUI methods
    // ===========================================================

    @Override
    public void handleNewDataset(IntkeyDataset dataset) {
        _lastOpenedDatasetDirectory = _context.getDatasetStartupFile().getParentFile();

        getMainFrame().setTitle(MessageFormat.format(windowTitleWithDatasetTitle, dataset.getHeading()));

        // display startup images
        if (!_suppressStartupImages) {
            List<Image> startupImages = dataset.getStartupImages();
            if (!startupImages.isEmpty()) {
                ImageUtils.displayStartupScreen(startupImages, _context.getImageSettings(), getMainFrame());
            }
        }

        initializeIdentification();

        _rootPanel.revalidate();
    }

    @Override
    public void handleDatasetClosed() {
        saveCurrentlyOpenedDataset();
        JMenuBar menuBar = buildMenus(_advancedMode); // need to refresh the
                                                      // recent datasets menu
        getMainFrame().setJMenuBar(menuBar);
        ResourceMap resourceMap = getContext().getResourceMap(Intkey.class);
        resourceMap.injectComponents(getMainFrame());
    }

    @Override
    public void handleUpdateAll() {
        if (_context.getDataset() != null) { // Only update if we have a dataset
                                             // loaded.
            List<Item> availableTaxa = _context.getAvailableTaxa();
            List<Item> eliminatedTaxa = _context.getEliminatedTaxa();

            _btnDiffSpecimenTaxa.setEnabled(availableTaxa.size() > 0 && eliminatedTaxa.size() > 0);

            // Need to display a message in place of the list of available
            // characters
            // if there are no remaining taxa (no matching taxa remain), or only
            // 1
            // remaining taxon (identification complete)
            if (availableTaxa.size() > 1) {
                updateAvailableCharacters();
            } else {
                JPanel messagePanel = null;

                if (availableTaxa.size() == 0) {
                    messagePanel = new AllowMismatchMessagePanel(noMatchingTaxaRemainCaption, HELP_ID_NO_MATCHING_TAXA_REMAIN, _context);
                } else {
                    // 1 available taxon
                    messagePanel = new MessagePanel(noMatchingTaxaRemainCaption, HELP_ID_IDENTIFICATION_COMPLETE);
                }

                _sclPaneAvailableCharacters.setViewportView(messagePanel);
                _sclPaneAvailableCharacters.revalidate();

                switch (_context.getCharacterOrder()) {
                case NATURAL:
                    _lblNumAvailableCharacters.setText(MessageFormat.format(availableCharactersCaption, 0));
                    break;
                case BEST:
                    _lblNumAvailableCharacters.setText(MessageFormat.format(bestCharactersCaption, 0));
                    break;
                case SEPARATE:
                    Item taxonToSeparate = _context.getDataset().getItem(_context.getTaxonToSeparate());
                    String formattedTaxonName = _taxonformatter.formatItemDescription(taxonToSeparate);
                    _lblNumAvailableCharacters.setText(MessageFormat.format(separateCharactersCaption, formattedTaxonName, 0));
                    break;
                default:
                    throw new RuntimeException("Unrecognized character order");
                }
            }

            updateUsedCharacters();
            updateAvailableTaxa(availableTaxa, _context.getSpecimen().getTaxonDifferences());
            updateUsedTaxa(eliminatedTaxa, _context.getSpecimen().getTaxonDifferences());

            updateDynamicButtons();
        }
    }

    @Override
    public void handleIdentificationRestarted() {
        _btnDiffSpecimenTaxa.setEnabled(false);
        handleUpdateAll();
        if (_context.isDemonstrationMode()) {
            IntKeyDialogController.closeWindows();
        }
    }

    @Override
    public void displayRTFReportFromFile(File rtfFile, String title) {
        long mbInBytes = 1024 * 1024;

        long fileSizeInMB = rtfFile.length() / mbInBytes;

        // give the user the option of saving the file instead if the file is
        // 5MB or more in size
        if (fileSizeInMB >= 5) {
            boolean saveFile = promptForYesNoOption(MessageFormat.format(saveReportToFilePrompt, fileSizeInMB));
            if (saveFile) {
                List<String> fileExtensions = new ArrayList<String>();
                fileExtensions.add("rtf");
                try {
                    File destinationFile = promptForFile(fileExtensions, UIUtils.getResourceString("RtfReportDisplayDialog.fileFilterDescription"), true);
                    if (destinationFile == null) {
                        // user hit cancel.
                        return;
                    }
                    FileUtils.copyFile(rtfFile, destinationFile);
                } catch (IOException ex) {
                    displayErrorMessage(errorWritingToFileError);
                }
                return;
            }
        }

        // If the file has not been saved, display its contents.
        try {
            String rtfSource = FileUtils.readFileToString(rtfFile);
            displayRTFReport(rtfSource, title);
        } catch (OutOfMemoryError err) {
            displayErrorMessage(rtfFileTooLargeError);
        } catch (Exception ex) {
            displayErrorMessage(errorWritingToFileError);
        }
    }

    @Override
    public void displayRTFReport(String rtfSource, String title) {
        DisplayRTFWorker worker = new DisplayRTFWorker(rtfSource, title);
        worker.execute();

        // Show the busy glass pane with a message if worker has not
        // completed within
        // 250 milliseconds. This avoids "flickering" of the glasspane
        // when it takes a
        // very short time to display the RTF report
        try {
            Thread.sleep(250);
            if (!worker.isDone()) {
                displayBusyMessageAllowCancelWorker(displayingReportCaption, worker);
            }
        } catch (InterruptedException ex) {
            // do nothing
        }
    }

    private class DisplayRTFWorker extends SwingWorker<Void, Void> {

        private String _rtfSource;
        private String _title;
        private RtfReportDisplayDialog _dlg;

        public DisplayRTFWorker(String rtfSource, String title) {
            _rtfSource = rtfSource;
            _title = title;
        }

        @Override
        public Void doInBackground() {
            _dlg = new RtfReportDisplayDialog(getMainFrame(), new SimpleRtfEditorKit(null), _rtfSource, _title);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                Intkey.this.show(_dlg);
            } catch (CancellationException ex) {
                // display of RTF content was cancelled - no action required.
            } catch (Exception ex) {
                // A runtime exception is thrown by the RtfReportDisplayDialog
                // if
                // the RTF was invalid. This will result in the dialog being
                // null.
                displayErrorMessage(badlyFormedRTFContentMessage);
            } finally {
                removeBusyMessage();
            }
        }

    }

    @Override
    public void displayErrorMessage(String message) {
        JOptionPane.showMessageDialog(getMainFrame(), message, errorDlgTitle, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void displayInformationMessage(String message) {
        JOptionPane.showMessageDialog(getMainFrame(), message, informationDlgTitle, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayBusyMessage(String message) {
        if (_busyGlassPane == null) {
            _busyGlassPane = new BusyGlassPane(message);
            getMainFrame().setGlassPane(_busyGlassPane);
            _busyGlassPane.setVisible(true);
        } else {
            _busyGlassPane.setMessage(message);
        }
    }

    @Override
    public void displayBusyMessageAllowCancelWorker(String message, SwingWorker<?, ?> worker) {
        displayBusyMessage(message);
        _busyGlassPane.setWorkerForCancellation(worker);
    }

    @Override
    public void removeBusyMessage() {
        if (_busyGlassPane != null) {
            _busyGlassPane.setVisible(false);
            _busyGlassPane = null;
            getMainFrame().setGlassPane(_defaultGlassPane);
        }
    }

    @Override
    public void displayTaxonInformation(List<Item> taxa, String imagesAutoDisplayText, String otherItemsAutoDisplayText, boolean closePromptAfterAutoDisplay) {
        TaxonInformationDialog dlg = new TaxonInformationDialog(getMainFrame(), taxa, _context, _context.getImageDisplayMode() != ImageDisplayMode.OFF);

        if (imagesAutoDisplayText != null) {
            dlg.displayImagesWithTextInSubject(imagesAutoDisplayText);
        }

        if (otherItemsAutoDisplayText != null) {
            dlg.displayOtherItemsWithTextInDescription(otherItemsAutoDisplayText);
        }

        // Don't bother showing the information dialog if it is just going to be
        // closed again straight away.
        if (!closePromptAfterAutoDisplay || (imagesAutoDisplayText == null && otherItemsAutoDisplayText == null)) {
            show(dlg);
        }

        // TODO need to tile windows!
    }

    @Override
    public void addToolbarButton(boolean advancedModeOnly, boolean normalModeOnly, boolean inactiveUnlessUsedCharacters, String imageFileName, List<String> commands, String shortHelp, String fullHelp) {
        Icon icon = null;

        // Is the image file an absolute file?
        File iconFile = new File(imageFileName);
        if (iconFile.exists() && iconFile.isAbsolute()) {
            try {
                icon = readImageIconFromFile(iconFile);
            } catch (IOException ex) {
                displayErrorMessage("Error reading image from file " + iconFile.getAbsolutePath());
            }
        }

        // Is the image file relative to the dataset directory?
        if (icon == null) {
            File relativeIconFile = new File(_context.getDatasetDirectory(), imageFileName);
            if (relativeIconFile.exists() && relativeIconFile.isAbsolute()) {
                try {
                    icon = readImageIconFromFile(relativeIconFile);
                } catch (IOException ex) {
                    displayErrorMessage("Error reading image from file " + iconFile.getAbsolutePath());
                }
            }
        }

        // try getting an icon with the exact image name from the icon resources
        if (icon == null) {
            try {
                icon = IconHelper.createImageIconFromAbsolutePath(INTKEY_ICON_PATH + "/" + imageFileName);
            } catch (Exception ex) {
                // do nothing
            }
        }

        if (icon == null && imageFileName.toLowerCase().endsWith(".bmp")) {
            // try substituting ".bmp" for ".png" and reading from the icon
            // resources. All the default
            // icons that come with Intkey have been convert to png format.
            String modifiedImageFileName = imageFileName.replaceFirst(".bmp$", ".png");

            try {
                icon = IconHelper.createImageIconFromAbsolutePath(INTKEY_ICON_PATH + "/" + modifiedImageFileName);
            } catch (Exception ex) {
                // do nothing
            }
        }

        if (icon == null) {
            displayErrorMessage("Could not find image " + imageFileName);
            return;
        }

        JButton button = new JButton(icon);
        button.setToolTipText(shortHelp);
        button.setMargin(new Insets(0, 0, 0, 0));
        _pnlDynamicButtons.add(button);

        final List<String> commandsCopy = new ArrayList<String>(commands);

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (String command : commandsCopy) {
                    _context.parseAndExecuteDirective(command);
                }

            }
        });

        if (advancedModeOnly && !normalModeOnly) {
            _advancedModeOnlyDynamicButtons.add(button);
        }

        if (normalModeOnly && !advancedModeOnly) {
            _normalModeOnlyDynamicButtons.add(button);
        }

        if (inactiveUnlessUsedCharacters) {
            _activeOnlyWhenCharactersUsedButtons.add(button);
        }

        _dynamicButtonsFullHelp.put(button, fullHelp);

        updateDynamicButtons();
    }

    private void updateDynamicButtons() {
        for (JButton b : _advancedModeOnlyDynamicButtons) {
            b.setVisible(_advancedMode);
        }

        for (JButton b : _normalModeOnlyDynamicButtons) {
            b.setVisible(!_advancedMode);
        }

        for (JButton b : _activeOnlyWhenCharactersUsedButtons) {
            if (_usedCharacterListModel != null) {
                b.setEnabled(_usedCharacterListModel.size() > 0);
            } else {
                b.setEnabled(false);
            }
        }

        _rootPanel.revalidate();
    }

    private ImageIcon readImageIconFromFile(File iconFile) throws IOException {
        BufferedImage img = ImageIO.read(iconFile);
        ImageIcon imgIcon = new ImageIcon(img);
        return imgIcon;
    }

    @Override
    public void addToolbarSpace() {
        JPanel spacerPanel = new JPanel();
        spacerPanel.setMinimumSize(new Dimension(20, 1));
        _pnlDynamicButtons.add(spacerPanel);
        _rootPanel.revalidate();
    }

    @Override
    public void clearToolbar() {
        _advancedModeOnlyDynamicButtons.clear();
        _normalModeOnlyDynamicButtons.clear();
        _activeOnlyWhenCharactersUsedButtons.clear();
        _pnlDynamicButtons.removeAll();
        _rootPanel.revalidate();
    }

    @Override
    public void illustrateCharacters(List<Character> characters) {
        if (_context.getImageDisplayMode() == ImageDisplayMode.OFF) {
            displayErrorMessage(UIUtils.getResourceString("ImageDisplayDisabled.error"));
        } else {
            try {
                CharacterImageDialog dlg = new CharacterImageDialog(getMainFrame(), characters, _context.getImageSettings(), true, false, _context.displayScaled());
                show(dlg);
                dlg.displayImagesForCharacter(characters.get(0));
            } catch (IllegalArgumentException ex) {
                // Display error message if unable to display
                displayErrorMessage(UIUtils.getResourceString("CouldNotDisplayImage.error", ex.getMessage()));
            }
        }
    }

    @Override
    public void illustrateTaxa(List<Item> taxa) {
        if (_context.getImageDisplayMode() == ImageDisplayMode.OFF) {
            displayErrorMessage(UIUtils.getResourceString("ImageDisplayDisabled.error"));
        } else {
            try {
                TaxonImageDialog dlg = new TaxonImageDialog(getMainFrame(), _context.getImageSettings(), taxa, false, !_context.displayContinuous(), _context.displayScaled(),
                        _context.getImageSubjects(), this);
                show(dlg);
                dlg.displayImagesForTaxon(taxa.get(0), 0);
            } catch (IllegalArgumentException ex) {
                // Display error message if unable to display
                displayErrorMessage(UIUtils.getResourceString("CouldNotDisplayImage.error", ex.getMessage()));
            }
        }
    }

    @Override
    public void displayContents(LinkedHashMap<String, String> contentsMap) {
        final ContentsDialog dlg = new ContentsDialog(getMainFrame(), contentsMap, _context);
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                show(dlg);
            }
        });
    }

    @Override
    public void displayFile(URL fileURL, String description) {
        try {
            UIUtils.displayFileFromURL(fileURL, description, _desktopWorker.get());
        } catch (IllegalArgumentException ex) {
            promptForString(UIUtils.getResourceString("CouldNotDisplayFileDesktopError.error", fileURL.toString()), fileURL.toString(), "");
        } catch (Exception ex) {
            displayErrorMessage(UIUtils.getResourceString("CouldNotDisplayFile.error", fileURL.toString()));
        }
    }

    @Override
    public boolean isLogVisible() {
        return _logDialog.isVisible();
    }

    @Override
    public void setLogVisible(boolean visible) {
        if (visible) {
            show(_logDialog);
        } else {
            _logDialog.setVisible(false);
        }
    }

    @Override
    public void updateLog() {
        List<String> logEntries = _context.getLogEntries();
        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();
        builder.setFont(1);
        for (String line : logEntries) {
            // directive calls are identified by a leading asterisk. They should
            // be colored blue.
            // All other lines should be colored red.
            if (line.trim().startsWith("*")) {
                builder.setTextColor(Color.BLUE);
            } else {
                builder.setTextColor(Color.RED);
            }
            String escapedLine = RTFUtils.escapeRTF(line);
            builder.appendText(escapedLine);
        }
        builder.endDocument();

        _logDialog.setContent(builder.toString());
    }

    @Override
    public void quitApplication() {
        exit();
    }

    @Override
    public List<Item> getSelectedTaxa() {
        List<Item> retList = new ArrayList<Item>();

        for (Object oTaxon : _listRemainingTaxa.getSelectedValues()) {
            retList.add((Item) oTaxon);
        }

        for (Object oTaxon : _listEliminatedTaxa.getSelectedValues()) {
            retList.add((Item) oTaxon);
        }

        return retList;
    }

    @Override
    public void setDemonstrationMode(boolean demonstrationMode) {
        if (demonstrationMode) {
            // If in advanced mode, switch to basic mode
            if (_advancedMode) {
                toggleAdvancedMode();
            }
        }

        getMainFrame().getJMenuBar().setVisible(!demonstrationMode);
    }

    @Override
    public void displayHelpTopic(String topicID) {
        _helpController.displayHelpTopic(getMainFrame(), topicID);
    }

    // ================================== DirectivePopulator methods
    // ===================================================================

    @Override
    public List<Character> promptForCharactersByKeyword(String directiveName, boolean permitSelectionFromIncludedCharactersOnly, boolean noneKeywordAvailable, List<String> returnSelectedKeywords) {
        List<Image> characterKeywordImages = _context.getDataset().getCharacterKeywordImages();
        if (_context.getImageDisplayMode() == ImageDisplayMode.AUTO && characterKeywordImages != null && !characterKeywordImages.isEmpty()) {
            ImageDialog dlg = new ImageDialog(getMainFrame(), _context.getImageSettings(), true, _context.displayScaled());
            dlg.setImages(characterKeywordImages);
            dlg.showImage(0);
            dlg.setTitle(MessageFormat.format(selectCharacterKeywordsCaption, directiveName));

            show(dlg);

            if (!dlg.okButtonPressed()) {
                // user cancelled
                return null;
            }

            Set<String> keywords = dlg.getSelectedKeywords();

            if (!noneKeywordAvailable) {
                keywords.remove(IntkeyContext.CHARACTER_KEYWORD_NONE);
            }

            List<Character> selectedCharacters = new ArrayList<Character>();

            for (String keyword : keywords) {
                selectedCharacters.addAll(_context.getCharactersForKeyword(keyword));
                returnSelectedKeywords.add(keyword);
            }

            if (permitSelectionFromIncludedCharactersOnly) {
                selectedCharacters.retainAll(_context.getIncludedCharacters());
            }

            return selectedCharacters;
        } else {
            CharacterKeywordSelectionDialog dlg = new CharacterKeywordSelectionDialog(getMainFrame(), _context, directiveName.toUpperCase(), permitSelectionFromIncludedCharactersOnly);
            show(dlg);
            returnSelectedKeywords.addAll(dlg.getSelectedKeywords());
            return dlg.getSelectedCharacters();
        }
    }

    @Override
    public List<Character> promptForCharactersByList(String directiveName, boolean selectFromAvailableCharactersOnly, List<String> returnSelectedKeywords) {
        List<Character> charactersToSelect;

        String keyword = null;
        if (selectFromAvailableCharactersOnly) {
            charactersToSelect = _context.getCharactersForKeyword(IntkeyContext.CHARACTER_KEYWORD_AVAILABLE);
            keyword = IntkeyContext.CHARACTER_KEYWORD_AVAILABLE;
        } else {
            charactersToSelect = _context.getCharactersForKeyword(IntkeyContext.CHARACTER_KEYWORD_ALL);
            keyword = IntkeyContext.CHARACTER_KEYWORD_ALL;

        }
        CharacterSelectionDialog dlg = new CharacterSelectionDialog(getMainFrame(), charactersToSelect, directiveName.toUpperCase(), keyword, _context.getImageSettings(), _context.displayNumbering(),
                _context);
        show(dlg);
        returnSelectedKeywords.addAll(dlg.getSelectedKeywords());
        return dlg.getSelectedCharacters();
    }

    @Override
    public List<Item> promptForTaxaByKeyword(String directiveName, boolean permitSelectionFromIncludedTaxaOnly, boolean noneKeywordAvailable, boolean includeSpecimenAsOption,
            MutableBoolean returnSpecimenSelected, List<String> returnSelectedKeywords) {

        List<Image> taxonKeywordImages = _context.getDataset().getTaxonKeywordImages();
        if (_context.getImageDisplayMode() == ImageDisplayMode.AUTO && taxonKeywordImages != null && !taxonKeywordImages.isEmpty()) {
            ImageDialog dlg = new ImageDialog(getMainFrame(), _context.getImageSettings(), true, _context.displayScaled());
            dlg.setImages(taxonKeywordImages);
            dlg.setTitle(MessageFormat.format(selectTaxonKeywordsCaption, directiveName));

            show(dlg);
            dlg.showImage(0);

            if (!dlg.okButtonPressed()) {
                // user cancelled
                return null;
            }

            Set<String> keywords = dlg.getSelectedKeywords();

            if (!noneKeywordAvailable) {
                keywords.remove(IntkeyContext.TAXON_KEYWORD_NONE);
            }

            List<Item> selectedTaxa = new ArrayList<Item>();

            for (String keyword : keywords) {
                selectedTaxa.addAll(_context.getTaxaForKeyword(keyword));
                returnSelectedKeywords.add(keyword);
            }

            if (permitSelectionFromIncludedTaxaOnly) {
                selectedTaxa.retainAll(_context.getIncludedCharacters());
            }

            return selectedTaxa;
        } else {
            TaxonKeywordSelectionDialog dlg = new TaxonKeywordSelectionDialog(getMainFrame(), _context, directiveName.toUpperCase(), permitSelectionFromIncludedTaxaOnly, includeSpecimenAsOption,
                    returnSpecimenSelected);
            show(dlg);
            returnSelectedKeywords.addAll(dlg.getSelectedKeywords());
            return dlg.getSelectedTaxa();
        }
    }

    @Override
    public List<Item> promptForTaxaByList(String directiveName, boolean selectFromRemainingTaxaOnly, boolean autoSelectSingleValue, boolean singleSelect, boolean includeSpecimenAsOption,
            MutableBoolean returnSpecimenSelected, List<String> returnSelectedKeywords) {
        List<Item> taxaToSelect;

        String keyword = null;
        if (selectFromRemainingTaxaOnly) {
            taxaToSelect = _context.getTaxaForKeyword(IntkeyContext.TAXON_KEYWORD_REMAINING);
            keyword = IntkeyContext.TAXON_KEYWORD_REMAINING;
        } else {
            taxaToSelect = _context.getTaxaForKeyword(IntkeyContext.TAXON_KEYWORD_ALL);
            keyword = IntkeyContext.TAXON_KEYWORD_ALL;
        }

        if (taxaToSelect.size() == 1 && autoSelectSingleValue) {
            return taxaToSelect;
        } else {
            TaxonSelectionDialog dlg = new TaxonSelectionDialog(getMainFrame(), taxaToSelect, directiveName.toUpperCase(), keyword, _context.displayNumbering(), singleSelect, _context,
                    includeSpecimenAsOption, returnSpecimenSelected);
            show(dlg);
            returnSelectedKeywords.addAll(dlg.getSelectedKeywords());
            return dlg.getSelectedTaxa();
        }
    }

    @Override
    public Boolean promptForYesNoOption(String message) {
        int selectedOption = JOptionPane.showConfirmDialog(getMainFrame(), message, null, JOptionPane.YES_NO_OPTION);
        if (selectedOption == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String promptForString(String message, String initialValue, String directiveName) {
        return (String) JOptionPane.showInputDialog(getMainFrame(), message, directiveName, JOptionPane.PLAIN_MESSAGE, null, null, initialValue);
    }

    @Override
    public List<String> promptForTextValue(TextCharacter ch) {
        if (_context.getImageDisplayMode() == ImageDisplayMode.AUTO && !ch.getImages().isEmpty()) {
            try {
                CharacterImageDialog dlg = new CharacterImageDialog(getMainFrame(), Arrays.asList(new Character[] { ch }), _context.getImageSettings(), true, true, _context.displayScaled());
                dlg.displayImagesForCharacter(ch);
                show(dlg);
                if (dlg.okButtonPressed()) {
                    return dlg.getInputTextValues();
                } else {
                    return null;
                }
            } catch (IllegalArgumentException ex) {
                // Display error message if unable to display
                displayErrorMessage(UIUtils.getResourceString("CouldNotDisplayImage.error", ex.getMessage()));
                return null;
            }
        } else {
            TextInputDialog dlg = new TextInputDialog(getMainFrame(), ch, _context.getImageSettings(), _context.displayNumbering(), _context.getImageDisplayMode() != ImageDisplayMode.OFF,
                    _context.displayScaled());
            UIUtils.showDialog(dlg);
            return dlg.getInputData();
        }
    }

    @Override
    public Set<Integer> promptForIntegerValue(IntegerCharacter ch) {
        if (_context.getImageDisplayMode() == ImageDisplayMode.AUTO && !ch.getImages().isEmpty()) {
            try {
                CharacterImageDialog dlg = new CharacterImageDialog(getMainFrame(), Arrays.asList(new Character[] { ch }), _context.getImageSettings(), true, true, _context.displayScaled());
                dlg.displayImagesForCharacter(ch);
                show(dlg);
                if (dlg.okButtonPressed()) {
                    return dlg.getInputIntegerValues();
                } else {
                    return null;
                }
            } catch (IllegalArgumentException ex) {
                // Display error message if unable to display
                displayErrorMessage(UIUtils.getResourceString("CouldNotDisplayImage.error", ex.getMessage()));
                return null;
            }
        } else {
            IntegerInputDialog dlg = new IntegerInputDialog(getMainFrame(), ch, _context.getImageSettings(), _context.displayNumbering(), _context.getImageDisplayMode() != ImageDisplayMode.OFF,
                    _context.displayScaled());
            UIUtils.showDialog(dlg);
            return dlg.getInputData();
        }
    }

    @Override
    public FloatRange promptForRealValue(RealCharacter ch) {
        if (_context.getImageDisplayMode() == ImageDisplayMode.AUTO && !ch.getImages().isEmpty()) {
            try {
                CharacterImageDialog dlg = new CharacterImageDialog(getMainFrame(), Arrays.asList(new Character[] { ch }), _context.getImageSettings(), true, true, _context.displayScaled());
                dlg.displayImagesForCharacter(ch);
                show(dlg);
                if (dlg.okButtonPressed()) {
                    return dlg.getInputRealValues();
                } else {
                    return null;
                }
            } catch (IllegalArgumentException ex) {
                // Display error message if unable to display
                displayErrorMessage(UIUtils.getResourceString("CouldNotDisplayImage.error", ex.getMessage()));
                return null;
            }
        } else {
            RealInputDialog dlg = new RealInputDialog(getMainFrame(), ch, _context.getImageSettings(), _context.displayNumbering(), _context.getImageDisplayMode() != ImageDisplayMode.OFF,
                    _context.displayScaled());
            UIUtils.showDialog(dlg);
            return dlg.getInputData();
        }
    }

    @Override
    public Set<Integer> promptForMultiStateValue(MultiStateCharacter ch) {
        if (_context.getImageDisplayMode() == ImageDisplayMode.AUTO && !ch.getImages().isEmpty()) {
            try {
                CharacterImageDialog dlg = new CharacterImageDialog(getMainFrame(), Arrays.asList(new Character[] { ch }), _context.getImageSettings(), true, true, _context.displayScaled());
                dlg.displayImagesForCharacter(ch);
                show(dlg);
                if (dlg.okButtonPressed()) {
                    return dlg.getSelectedStates();
                } else {
                    return null;
                }
            } catch (IllegalArgumentException ex) {
                // Display error message if unable to display
                displayErrorMessage(UIUtils.getResourceString("CouldNotDisplayImage.error", ex.getMessage()));
                return null;
            }
        } else {
            MultiStateInputDialog dlg = new MultiStateInputDialog(getMainFrame(), ch, _context.getImageSettings(), _context.displayNumbering(), _context.getImageDisplayMode() != ImageDisplayMode.OFF,
                    _context.displayScaled());
            UIUtils.showDialog(dlg);
            return dlg.getInputData();
        }
    }

    @Override
    public File promptForFile(List<String> fileExtensions, String description, boolean createFileIfNonExistant) throws IOException {
        String[] extensionsArray = new String[fileExtensions.size()];
        fileExtensions.toArray(extensionsArray);

        JFileChooser chooser = new JFileChooser(_lastOpenedDatasetDirectory);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensionsArray);
        chooser.setFileFilter(filter);

        int returnVal;

        if (createFileIfNonExistant) {
            returnVal = chooser.showSaveDialog(UIUtils.getMainFrame());
        } else {
            returnVal = chooser.showOpenDialog(UIUtils.getMainFrame());
        }

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            if (createFileIfNonExistant) {
                File file = chooser.getSelectedFile();

                if (!file.exists()) {
                    // if only one file extension was supplied and the filename
                    // does
                    // not end with this extension, add it before
                    // creating the file
                    if (fileExtensions.size() == 1) {
                        String extension = fileExtensions.get(0);
                        String filePath = chooser.getSelectedFile().getAbsolutePath();
                        if (!filePath.endsWith(extension)) {
                            file = new File(filePath + "." + extension);
                        }
                    }

                    file.createNewFile();
                }
                return file;
            } else {
                return chooser.getSelectedFile();
            }
        } else {
            return null;
        }
    }

    @Override
    public Boolean promptForOnOffValue(String directiveName, boolean initialValue) {
        OnOffPromptDialog dlg = new OnOffPromptDialog(getMainFrame(), directiveName.toUpperCase(), initialValue);
        show(dlg);
        if (dlg.isOkButtonPressed()) {
            return dlg.getSelectedValue();
        } else {
            return null;
        }
    }

    @Override
    public List<Object> promptForMatchSettings() {
        List<Object> retList = new ArrayList<Object>();

        SetMatchPromptDialog dlg = new SetMatchPromptDialog(getMainFrame(), true, _context.getMatchInapplicables(), _context.getMatchUnknowns(), _context.getMatchType());
        show(dlg);
        if (dlg.wasOkButtonPressed()) {
            boolean matchUnknowns = dlg.getMatchUnknowns();
            boolean matchInapplicables = dlg.getMatchInapplicables();
            MatchType matchType = dlg.getMatchType();
            retList.add(matchUnknowns);
            retList.add(matchInapplicables);
            retList.add(matchType);
        } else {
            return null;
        }

        return retList;
    }

    @Override
    public List<Object> promptForButtonDefinition() {
        List<Object> returnValues = new ArrayList<Object>();

        DefineButtonDialog dlg = new DefineButtonDialog(getMainFrame(), true);
        show(dlg);

        if (dlg.wasOkButtonPressed()) {
            returnValues.add(dlg.isInsertSpace());
            returnValues.add(dlg.isRemoveAllButtons());
            returnValues.add(dlg.getImageFilePath());
            returnValues.add(dlg.getCommands());
            returnValues.add(dlg.getBriefHelp());
            returnValues.add(dlg.getDetailedHelp());
            returnValues.add(dlg.enableIfUsedCharactersOnly());
            returnValues.add(dlg.enableInNormalModeOnly());
            returnValues.add(dlg.enableInAdvancedModeOnly());
            return returnValues;
        } else {
            // cancelled
            return null;
        }
    }

    @Override
    public Pair<ImageDisplayMode, DisplayImagesReportType> promptForImageDisplaySettings() {
        DisplayImagesDialog dlg = new DisplayImagesDialog(getMainFrame(), _context.getImageDisplayMode());
        show(dlg);

        if (dlg.wasOkButtonPressed()) {
            return new Pair<ImageDisplayMode, DisplayImagesReportType>(dlg.getSelectedImageDisplayMode(), dlg.getSelectedReportType());
        } else {
            return null;
        }
    }

    // ======== Methods for "find in characters" and "find in taxa" functions
    // ====================

    // Returns number of taxa matched
    public int findTaxa(String searchText, boolean searchSynonyms, boolean searchEliminatedTaxa) {

        IntkeyDataset dataset = _context.getDataset();

        List<Item> availableTaxa = _context.getAvailableTaxa();
        List<Item> eliminatedTaxa = _context.getEliminatedTaxa();
        _foundAvailableTaxa = new ArrayList<Item>();
        _foundEliminatedTaxa = new ArrayList<Item>();

        Map<Item, List<TextAttribute>> taxaSynonymyAttributes = dataset.getSynonymyAttributesForTaxa();

        for (Item taxon : availableTaxa) {
            if (SearchUtils.taxonMatches(searchText, taxon, SearchUtils.getSynonymyStringsForTaxon(taxon, taxaSynonymyAttributes))) {
                _foundAvailableTaxa.add(taxon);
            }
        }

        for (Item taxon : eliminatedTaxa) {
            if (SearchUtils.taxonMatches(searchText, taxon, SearchUtils.getSynonymyStringsForTaxon(taxon, taxaSynonymyAttributes))) {
                _foundEliminatedTaxa.add(taxon);
            }
        }

        // found available taxa must be sorted by difference count if the
        // tolerance has been
        // set to greater than zero - this mirrors the ordering in which the
        // available taxa are
        // displayed in this situation
        if (_context.getTolerance() > 0) {
            Collections.sort(_foundAvailableTaxa, new DifferenceCountComparator(_context.getSpecimen().getTaxonDifferences()));
        }

        // eliminated taxa must always be sorted by difference count. This
        // mirrors the order in which the
        // eliminated taxa are displayed.
        Collections.sort(_foundEliminatedTaxa, new DifferenceCountComparator(_context.getSpecimen().getTaxonDifferences()));

        _availableTaxaCellRenderer.setTaxaToColor(new HashSet<Item>(_foundAvailableTaxa));
        _eliminatedTaxaCellRenderer.setTaxaToColor(new HashSet<Item>(_foundEliminatedTaxa));

        _listRemainingTaxa.repaint();
        _listEliminatedTaxa.repaint();

        return _foundAvailableTaxa.size() + _foundEliminatedTaxa.size();
    }

    public void selectCurrentMatchedTaxon(int matchedTaxonIndex) {

        if (matchedTaxonIndex < _foundAvailableTaxa.size()) {
            Item taxon = _foundAvailableTaxa.get(matchedTaxonIndex);
            _listRemainingTaxa.setSelectedValue(taxon, true);
            _listEliminatedTaxa.clearSelection();
        } else if (!_foundEliminatedTaxa.isEmpty()) {
            int offsetIndex = matchedTaxonIndex - _foundAvailableTaxa.size();
            if (offsetIndex < _foundEliminatedTaxa.size()) {
                Item taxon = _foundEliminatedTaxa.get(offsetIndex);
                _listEliminatedTaxa.setSelectedValue(taxon, true);
                _listRemainingTaxa.clearSelection();
            }
        }
    }

    public void selectAllMatchedTaxa() {

        int[] availableTaxaSelectedIndices = new int[_foundAvailableTaxa.size()];
        for (int i = 0; i < _foundAvailableTaxa.size(); i++) {
            Item taxon = _foundAvailableTaxa.get(i);
            availableTaxaSelectedIndices[i] = _availableTaxaListModel.indexOf(taxon);
        }

        int[] eliminatedTaxaSelectedIndices = new int[_foundEliminatedTaxa.size()];
        for (int i = 0; i < _foundEliminatedTaxa.size(); i++) {
            Item taxon = _foundEliminatedTaxa.get(i);
            eliminatedTaxaSelectedIndices[i] = _eliminatedTaxaListModel.indexOf(taxon);
        }

        _listRemainingTaxa.setSelectedIndices(availableTaxaSelectedIndices);
        _listEliminatedTaxa.setSelectedIndices(eliminatedTaxaSelectedIndices);
    }

    // Returns number of characters matched
    public int findCharacters(String searchText, boolean searchStates, boolean searchUsedCharacters) {
        List<Character> availableCharacters;

        IntkeyCharacterOrder charOrder = _context.getCharacterOrder();
        switch (charOrder) {
        case NATURAL:
            availableCharacters = _context.getAvailableCharacters();
            break;
        case BEST:
            availableCharacters = new ArrayList<Character>(_context.getBestOrSeparateCharacters().keySet());
            break;
        case SEPARATE:
            throw new NotImplementedException();
        default:
            throw new RuntimeException("Unrecognised character order");
        }

        List<Character> usedCharacters = _context.getUsedCharacters();

        _foundAvailableCharacters = new ArrayList<Character>();
        _foundUsedCharacters = new ArrayList<Character>();

        for (Character ch : availableCharacters) {
            if (SearchUtils.characterMatches(ch, searchText, searchStates)) {
                _foundAvailableCharacters.add(ch);
            }
        }

        if (searchUsedCharacters) {
            for (Character ch : usedCharacters) {
                if (SearchUtils.characterMatches(ch, searchText, searchStates)) {
                    _foundUsedCharacters.add(ch);
                }
            }
        }

        _availableCharactersListCellRenderer.setCharactersToColor(new HashSet<Character>(_foundAvailableCharacters));
        _usedCharactersListCellRenderer.setCharactersToColor(new HashSet<Character>(_foundUsedCharacters));

        _listAvailableCharacters.repaint();
        _listUsedCharacters.repaint();

        return _foundAvailableCharacters.size() + _foundUsedCharacters.size();
    }

    public void selectCurrentMatchedCharacter(int matchedCharacterIndex) {

        if (matchedCharacterIndex < _foundAvailableCharacters.size()) {
            Character ch = _foundAvailableCharacters.get(matchedCharacterIndex);
            _listAvailableCharacters.setSelectedValue(ch, true);
            _listUsedCharacters.clearSelection();
        } else if (!_foundUsedCharacters.isEmpty()) {
            int offsetIndex = matchedCharacterIndex - _foundAvailableCharacters.size();
            if (offsetIndex < _foundUsedCharacters.size()) {
                Character ch = _foundUsedCharacters.get(offsetIndex);
                Attribute attr = _context.getSpecimen().getAttributeForCharacter(ch);
                _listUsedCharacters.setSelectedValue(attr, true);
                _listAvailableCharacters.clearSelection();
            }
        }
    }

    private class DifferenceCountComparator implements Comparator<Item> {

        private Map<Item, Set<Character>> _taxaDifferingCharacters;

        public DifferenceCountComparator(Map<Item, Set<Character>> taxaDifferingCharacters) {
            _taxaDifferingCharacters = taxaDifferingCharacters;
        }

        @Override
        public int compare(Item t1, Item t2) {
            int diffT1 = _taxaDifferingCharacters.get(t1).size();
            int diffT2 = _taxaDifferingCharacters.get(t2).size();

            if (diffT1 == diffT2) {
                return t1.compareTo(t2);
            } else {
                return Integer.valueOf(diffT1).compareTo(Integer.valueOf(diffT2));
            }
        }
    }

    private void openPreviouslyOpenedFile(String fileName) {
        executeDirective(new NewDatasetDirective(), "\"" + fileName + "\"");
    }

    private void saveCurrentlyOpenedDataset() {
        // if the dataset was downloaded, ask the user if they wish to save it
        StartupFileData startupFileData = _context.getStartupFileData();

        File fileToOpenDataset = null;
        if (startupFileData != null && startupFileData.isRemoteDataset()) {
            int chosenOption = JOptionPane.showConfirmDialog(getMainFrame(), "Save downloaded dataset?", "Save", JOptionPane.YES_NO_OPTION);
            if (chosenOption == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int returnVal = fileChooser.showOpenDialog(getMainFrame());

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File saveDir = fileChooser.getSelectedFile();

                    try {
                        File newInkFile = StartupUtils.saveRemoteDataset(_context, saveDir);
                        fileToOpenDataset = newInkFile;

                        // Remove the current startup file from the MRU as a new
                        // file will go in its
                        // place.
                        removeFileFromMRU(_context.getDatasetStartupFile().getAbsolutePath());
                    } catch (IOException ex) {
                        displayErrorMessage("Error saving downloaded dataset");
                        // not much we can do here, just abort saving/adding to
                        // recents list.
                        return;
                    }
                } else {
                    fileToOpenDataset = _context.getDatasetStartupFile();
                }
            } else {
                fileToOpenDataset = _context.getDatasetStartupFile();
            }
        } else {
            fileToOpenDataset = _context.getDatasetStartupFile();
        }

        if (_context.getDataset() != null) {
            String datasetTitle = _context.getDataset().getHeading().trim();

            addFileToMRU(fileToOpenDataset.getAbsolutePath(), datasetTitle);
        }
    }

    public static List<Pair<String, String>> getPreviouslyUsedFiles() {
        List<Pair<String, String>> retList = new ArrayList<Pair<String, String>>();

        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        if (prefs != null) {
            String mru = prefs.get(MRU_FILES_PREF_KEY, "");
            if (!StringUtils.isEmpty(mru)) {
                String[] mruFiles = mru.split(MRU_FILES_SEPARATOR);
                for (String mruFile : mruFiles) {
                    String[] mruFileItems = mruFile.split(MRU_ITEM_SEPARATOR);
                    retList.add(new Pair<String, String>(mruFileItems[0], mruFileItems[1]));
                }
            }
        }

        return retList;
    }

    /**
     * Removes the specified file from the most recently used file list
     * 
     * @param filename
     *            The filename to remove
     */
    public static void removeFileFromMRU(String filename) {

        List<Pair<String, String>> existingFiles = getPreviouslyUsedFiles();

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < existingFiles.size(); ++i) {

            Pair<String, String> fileNameTitlePair = existingFiles.get(i);
            String existingFileName = fileNameTitlePair.getFirst();

            if (!existingFileName.equalsIgnoreCase(filename)) {

                if (b.length() > 0) {
                    b.append(MRU_FILES_SEPARATOR);
                }
                b.append(fileNameTitlePair.getFirst() + MRU_ITEM_SEPARATOR + fileNameTitlePair.getSecond());
            }
        }

        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        prefs.put(MRU_FILES_PREF_KEY, b.toString());
        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Adds the supplied filename to the top of the most recently used files.
     * 
     * @param filename
     */
    public static void addFileToMRU(String filename, String title) {

        Queue<String> q = new LinkedList<String>();

        String newFilePathAndTitle;
        if (StringUtils.isEmpty(title)) {
            newFilePathAndTitle = filename + MRU_ITEM_SEPARATOR + filename;
        } else {
            newFilePathAndTitle = filename + MRU_ITEM_SEPARATOR + title;
        }
        q.add(newFilePathAndTitle);

        List<Pair<String, String>> existingFiles = getPreviouslyUsedFiles();
        if (existingFiles != null) {
            for (Pair<String, String> existingFile : existingFiles) {
                String existingFilePathAndTitle = existingFile.getFirst() + MRU_ITEM_SEPARATOR + existingFile.getSecond();
                if (!q.contains(existingFilePathAndTitle)) {
                    q.add(existingFilePathAndTitle);
                }
            }
        }

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < MAX_SIZE_MRU && q.size() > 0; ++i) {
            if (i > 0) {
                b.append(MRU_FILES_SEPARATOR);
            }
            b.append(q.poll());
        }

        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        prefs.put(MRU_FILES_PREF_KEY, b.toString());
        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return true if the last time the application was closed, advanced mode
     *         was in use
     */
    private static boolean getPreviousApplicationMode() {
        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        if (prefs != null) {
            String previouslyUsedMode = prefs.get(MODE_PREF_KEY, "");
            if (!StringUtils.isEmpty(previouslyUsedMode)) {
                return previouslyUsedMode.equals(ADVANCED_MODE_PREF_VALUE);
            }
        }
        return false;
    }

    private static void savePreviousApplicationMode(boolean advancedMode) {
        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        prefs.put(MODE_PREF_KEY, advancedMode ? ADVANCED_MODE_PREF_VALUE : BASIC_MODE_PREF_VALUE);
        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return true if the last time the application was closed, advanced mode
     *         was in use
     */
    private static File getSavedLastOpenedDatasetDirectory() {
        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        if (prefs != null) {
            String lastOpenedDirectoryPath = prefs.get(LAST_OPENED_DATASET_LOCATION_PREF_KEY, "");
            if (!StringUtils.isEmpty(lastOpenedDirectoryPath)) {
                return new File(lastOpenedDirectoryPath);
            }
        }
        return null;
    }

    private static void saveLastOpenedDatasetDirectory(File lastOpenedDatasetDirectory) {
        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        prefs.put(LAST_OPENED_DATASET_LOCATION_PREF_KEY, lastOpenedDatasetDirectory.getAbsolutePath());
        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public Rectangle getClientBounds() {
        Rectangle r = _rootSplitPane.getBounds();
        // Rectangle outer = getMainFrame().getBounds();
        // r.x = r.x + outer.x;
        // r.y = r.y + _pnlAvailableCharactersHeader.getHeight();
        Point p1 = new Point(0, 0);
        SwingUtilities.convertPointToScreen(p1, _rootSplitPane);
        r.x = p1.x;
        r.y = p1.y + _pnlAvailableCharactersHeader.getHeight();
        r.height = r.height - _pnlAvailableCharactersHeader.getHeight();

        return r;
    }

    @Action
    public void systemLookAndFeel() {
        au.org.ala.delta.ui.util.UIUtils.systemLookAndFeel(getMainFrame());
    }

    @Action
    public void metalLookAndFeel() {
        au.org.ala.delta.ui.util.UIUtils.metalLookAndFeel(getMainFrame());
    }

    @Action
    public void nimbusLookAndFeel() {
        au.org.ala.delta.ui.util.UIUtils.nimbusLookAndFeel(getMainFrame());
    }

}
