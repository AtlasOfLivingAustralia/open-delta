package au.org.ala.delta.intkey;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.jdesktop.application.Action;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.Logger;
import au.org.ala.delta.intkey.directives.ChangeDirective;
import au.org.ala.delta.intkey.directives.DifferencesDirective;
import au.org.ala.delta.intkey.directives.DirectivePopulator;
import au.org.ala.delta.intkey.directives.DisplayCharacterOrderBestDirective;
import au.org.ala.delta.intkey.directives.DisplayCharacterOrderNaturalDirective;
import au.org.ala.delta.intkey.directives.FileCharactersDirective;
import au.org.ala.delta.intkey.directives.FileTaxaDirective;
import au.org.ala.delta.intkey.directives.IncludeCharactersDirective;
import au.org.ala.delta.intkey.directives.IncludeTaxaDirective;
import au.org.ala.delta.intkey.directives.IntkeyDirective;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParseException;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParser;
import au.org.ala.delta.intkey.directives.NewDatasetDirective;
import au.org.ala.delta.intkey.directives.RestartDirective;
import au.org.ala.delta.intkey.directives.SetToleranceDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyCharacterOrder;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.CharacterValue;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.intkey.ui.BestCharacterListModel;
import au.org.ala.delta.intkey.ui.BusyGlassPane;
import au.org.ala.delta.intkey.ui.CharacterKeywordSelectionDialog;
import au.org.ala.delta.intkey.ui.CharacterListModel;
import au.org.ala.delta.intkey.ui.ColoringListCellRenderer;
import au.org.ala.delta.intkey.ui.IntegerInputDialog;
import au.org.ala.delta.intkey.ui.MultiStateInputDialog;
import au.org.ala.delta.intkey.ui.ReExecuteDialog;
import au.org.ala.delta.intkey.ui.RealInputDialog;
import au.org.ala.delta.intkey.ui.RtfReportDisplayDialog;
import au.org.ala.delta.intkey.ui.TaxonKeywordSelectionDialog;
import au.org.ala.delta.intkey.ui.TaxonListModel;
import au.org.ala.delta.intkey.ui.TaxonWithDifferenceCountListModel;
import au.org.ala.delta.intkey.ui.TextInputDialog;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.intkey.ui.UsedCharacterListModel;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.ui.AboutBox;
import au.org.ala.delta.ui.DeltaSingleFrameApplication;
import au.org.ala.delta.ui.image.ImageUtils;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;
import au.org.ala.delta.ui.util.IconHelper;

public class Intkey extends DeltaSingleFrameApplication implements IntkeyUI, DirectivePopulator {

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
    private ColoringListCellRenderer _listCellRenderer;

    private CharacterListModel _availableCharacterListModel;
    private UsedCharacterListModel _usedCharacterListModel;
    private TaxonListModel _availableTaxaListModel;
    private TaxonListModel _eliminatedTaxaListModel;

    private IntkeyDirectiveParser _directiveParser;

    private JLabel _lblNumAvailableCharacters;
    private JLabel _lblNumUsedCharacters;

    private BusyGlassPane _glassPane = null;
    
    boolean normalMode = false;

    @Resource
    String windowTitleWithDatasetTitle;

    @Resource
    String availableCharactersCaption;

    @Resource
    String bestCharactersCaption;

    @Resource
    String usedCharactersCaption;

    @Resource
    String remainingTaxaCaption;

    @Resource
    String eliminatedTaxaCaption;

    @Resource
    String calculatingBestCaption;

    @Resource
    String loadingReportCaption;

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
    private JButton _btnContextHelp;
    private JPanel _globalOptionBar;
    private JScrollPane _sclPaneAvailableCharacters;
    private JPanel _pnlAvailableCharactersHeader;
    private JPanel _pnlDynamicButtons;

    public static void main(String[] args) {
        setupMacSystemProperties(Intkey.class);
        launch(Intkey.class, args);
    }

    @Override
    protected void initialize(String[] args) {
        ResourceMap resourceMap = getContext().getResourceMap(Intkey.class);
        resourceMap.injectFields(this);
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    protected void startup() {
        JFrame mainFrame = getMainFrame();
        mainFrame.setTitle("Intkey");
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setIconImages(IconHelper.getRedIconList());

        _context = new IntkeyContext(this, this);
        _directiveParser = IntkeyDirectiveParser.createInstance();
        
        _listCellRenderer = new ColoringListCellRenderer();

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
        _globalOptionBar.add(_pnlDynamicButtons, BorderLayout.WEST);

        _btnContextHelp = new JButton();
        _btnContextHelp.setAction(actionMap.get("btnContextHelp"));
        _btnContextHelp.setEnabled(false);
        _btnContextHelp.setPreferredSize(new Dimension(30, 30));
        _btnContextHelp.setMargin(new Insets(2, 5, 2, 5));
        _globalOptionBar.add(_btnContextHelp, BorderLayout.EAST);

        _rootSplitPane = new JSplitPane();
        _rootSplitPane.setDividerSize(3);
        _rootSplitPane.setResizeWeight(0.5);
        _rootSplitPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent arg0) {
                System.out.println("root split pane shown");
            }
        });
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
        _listAvailableCharacters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _listAvailableCharacters.setCellRenderer(_listCellRenderer);
        _listAvailableCharacters.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int selectedIndex = _listAvailableCharacters.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        try {
                            Character ch = _availableCharacterListModel.getCharacterAt(selectedIndex);
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
        _lblNumAvailableCharacters.setText(String.format(availableCharactersCaption, 0));
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
        _btnSeparate.setEnabled(false);
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
        _btnSetMatch.setEnabled(false);
        _btnSetMatch.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnSetMatch);

        _btnSubsetCharacters = new JButton();
        _btnSubsetCharacters.setAction(actionMap.get("btnSubsetCharacters"));
        _btnSubsetCharacters.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnSubsetCharacters);

        _btnFindCharacter = new JButton();
        _btnFindCharacter.setAction(actionMap.get("btnFindCharacter"));
        _btnFindCharacter.setEnabled(false);
        _btnFindCharacter.setPreferredSize(new Dimension(30, 30));
        _pnlAvailableCharactersButtons.add(_btnFindCharacter);

        _pnlUsedCharacters = new JPanel();
        _innerSplitPaneLeft.setRightComponent(_pnlUsedCharacters);
        _pnlUsedCharacters.setLayout(new BorderLayout(0, 0));

        _sclPnUsedCharacters = new JScrollPane();
        _pnlUsedCharacters.add(_sclPnUsedCharacters, BorderLayout.CENTER);

        _listUsedCharacters = new JList();
        _listUsedCharacters.setCellRenderer(_listCellRenderer);
        _listUsedCharacters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _listUsedCharacters.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int selectedIndex = _listUsedCharacters.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        try {
                            Character ch = _usedCharacterListModel.getCharacterAt(selectedIndex);
                            executeDirective(new ChangeDirective(), Integer.toString(ch.getCharacterId()));
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
        _lblNumUsedCharacters.setText(String.format(usedCharactersCaption, 0));
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

        _sclPnRemainingTaxa.setViewportView(_listRemainingTaxa);

        _pnlRemainingTaxaHeader = new JPanel();
        _pnlRemainingTaxa.add(_pnlRemainingTaxaHeader, BorderLayout.NORTH);
        _pnlRemainingTaxaHeader.setLayout(new BorderLayout(0, 0));

        _lblNumRemainingTaxa = new JLabel();
        _lblNumRemainingTaxa.setFont(new Font("Tahoma", Font.PLAIN, 15));
        _lblNumRemainingTaxa.setText(String.format(remainingTaxaCaption, 0));
        _pnlRemainingTaxaHeader.add(_lblNumRemainingTaxa, BorderLayout.WEST);

        _pnlRemainingTaxaButtons = new JPanel();
        FlowLayout fl_pnlRemainingTaxaButtons = (FlowLayout) _pnlRemainingTaxaButtons.getLayout();
        fl_pnlRemainingTaxaButtons.setVgap(2);
        fl_pnlRemainingTaxaButtons.setHgap(2);
        _pnlRemainingTaxaHeader.add(_pnlRemainingTaxaButtons, BorderLayout.EAST);

        _btnTaxonInfo = new JButton();
        _btnTaxonInfo.setAction(actionMap.get("btnTaxonInfo"));
        _btnTaxonInfo.setEnabled(false);
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
        _btnFindTaxon.setEnabled(false);
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

        _sclPnEliminatedTaxa.setViewportView(_listEliminatedTaxa);

        _pnlEliminatedTaxaHeader = new JPanel();
        _pnlEliminatedTaxa.add(_pnlEliminatedTaxaHeader, BorderLayout.NORTH);
        _pnlEliminatedTaxaHeader.setLayout(new BorderLayout(0, 0));

        _lblEliminatedTaxa = new JLabel();
        _lblEliminatedTaxa.setBorder(new EmptyBorder(7, 0, 7, 0));
        _lblEliminatedTaxa.setFont(new Font("Tahoma", Font.PLAIN, 15));
        _lblEliminatedTaxa.setText(String.format(eliminatedTaxaCaption, 0));
        _pnlEliminatedTaxaHeader.add(_lblEliminatedTaxa, BorderLayout.WEST);

        JMenuBar menuBar = buildMenus();
        getMainView().setMenuBar(menuBar);

        _txtFldCmdBar = new JTextField();
        _txtFldCmdBar.setCaretColor(Color.WHITE);
        _txtFldCmdBar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String cmdStr = _txtFldCmdBar.getText();

                cmdStr = cmdStr.toLowerCase().trim();
                if (_cmdMenus.containsKey(cmdStr)) {
                    JMenu cmdMenu = _cmdMenus.get(cmdStr);
                    cmdMenu.doClick();
                } else {
                    try {
                        _directiveParser.parse(new InputStreamReader(new ByteArrayInputStream(cmdStr.getBytes())), _context);
                    } catch (Exception ex) {
                        Logger.log("Exception thrown while processing directive \"%s\"", cmdStr);
                        ex.printStackTrace();
                    }
                }
                _txtFldCmdBar.setText(null);
            }
        });

        _txtFldCmdBar.setFont(new Font("Courier New", Font.BOLD, 13));
        _txtFldCmdBar.setForeground(SystemColor.text);
        _txtFldCmdBar.setBackground(Color.BLACK);
        _rootPanel.add(_txtFldCmdBar, BorderLayout.SOUTH);
        _txtFldCmdBar.setColumns(10);
        
        HashSet<Integer> selectedChars = new HashSet<Integer>();
        selectedChars.add(0);
        selectedChars.add(9);
        _listCellRenderer.setIndicesToColor(selectedChars);

        show(_rootPanel);
    }

    @Override
    protected void ready() {
        super.ready();
        _rootSplitPane.setDividerLocation(2.0 / 3.0);
        _innerSplitPaneLeft.setDividerLocation(2.0 / 3.0);
        _innerSplitPaneRight.setDividerLocation(2.0 / 3.0);
    }

    @Override
    protected void shutdown() {
        _context.cleanupForShutdown();
        super.shutdown();
    }

    private void showBusyMessage(String message) {
        _glassPane = new BusyGlassPane(message);
        getMainFrame().setGlassPane(_glassPane);
        _glassPane.setVisible(true);
    }

    private void removeBusyMessage() {
        if (_glassPane != null) {
            _glassPane.setVisible(false);
            _glassPane = null;
        }
    }

    private JMenuBar buildMenus() {

        _cmdMenus = new HashMap<String, JMenu>();

        ActionMap actionMap = getContext().getActionMap();

        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu mnuFile = new JMenu();
        mnuFile.setName("mnuFile");

        JMenuItem mnuItNewDataSet = new JMenuItem();
        mnuItNewDataSet.setAction(actionMap.get("mnuItNewDataSet"));
        mnuFile.add(mnuItNewDataSet);

        JMenuItem mnuItPreferences = new JMenuItem();
        mnuItPreferences.setAction(actionMap.get("mnuItPreferences"));
        mnuItPreferences.setEnabled(false);
        mnuFile.add(mnuItPreferences);

        JMenuItem mnuItContents = new JMenuItem();
        mnuItContents.setAction(actionMap.get("mnuItContents"));
        mnuItContents.setEnabled(false);
        mnuFile.add(mnuItContents);

        mnuFile.addSeparator();

        JMenu mnuFileCmds = new JMenu();
        mnuFileCmds.setName("mnuFileCmds");

        JMenuItem mnuItFileCharactersCmd = new JMenuItem();
        mnuItFileCharactersCmd.setAction(actionMap.get("mnuItFileCharacters"));
        mnuFileCmds.add(mnuItFileCharactersCmd);

        JMenuItem mnuItFileTaxaCmd = new JMenuItem();
        mnuItFileTaxaCmd.setAction(actionMap.get("mnuItFileTaxa"));
        mnuFileCmds.add(mnuItFileTaxaCmd);

        mnuFile.add(mnuFileCmds);

        JMenu mnuOutputCmds = new JMenu();
        mnuOutputCmds.setName("mnuOutputCmds");
        mnuOutputCmds.setEnabled(false);
        mnuFile.add(mnuOutputCmds);

        mnuFile.addSeparator();

        JMenuItem mnuItComment = new JMenuItem();
        mnuItComment.setAction(actionMap.get("mnuItComment"));
        mnuItComment.setEnabled(false);
        mnuFile.add(mnuItComment);

        JMenuItem mnuItShow = new JMenuItem();
        mnuItShow.setAction(actionMap.get("mnuItShow"));
        mnuItShow.setEnabled(false);
        mnuFile.add(mnuItShow);

        mnuFile.addSeparator();

        JMenuItem mnuItNormalMode = new JMenuItem();
        mnuItNormalMode.setAction(actionMap.get("mnuItNormalMode"));
        mnuItNormalMode.setEnabled(false);
        mnuFile.add(mnuItNormalMode);

        mnuFile.addSeparator();

        JMenuItem mnuItEditIndex = new JMenuItem("Edit Index...");
        mnuItEditIndex.setAction(actionMap.get("mnuItEditIndex"));
        mnuItEditIndex.setEnabled(false);
        mnuFile.add(mnuItEditIndex);

        _cmdMenus.put("file", mnuFileCmds);

        mnuFile.addSeparator();

        JMenuItem mnuItFileExit = new JMenuItem();
        mnuItFileExit.setAction(actionMap.get("mnuItExitApplication"));
        mnuFile.add(mnuItFileExit);
        menuBar.add(mnuFile);

        JMenu mnuQueries = new JMenu();
        mnuQueries.setName("mnuQueries");
        mnuQueries.setEnabled(false);
        menuBar.add(mnuQueries);

        JMenu mnuBrowsing = new JMenu();
        mnuBrowsing.setName("mnuBrowsing");
        mnuBrowsing.setEnabled(false);
        menuBar.add(mnuBrowsing);

        JMenu mnuSettings = new JMenu();
        mnuSettings.setName("mnuSettings");
        mnuSettings.setEnabled(false);
        menuBar.add(mnuSettings);

        JMenu mnuReExecute = new JMenu("ReExecute...");
        mnuReExecute.setName("mnuReExecute");
        menuBar.add(mnuReExecute);

        JMenuItem mnuItReExecute = new JMenuItem();
        mnuItReExecute.setAction(actionMap.get("mnuItReExecute"));
        mnuReExecute.add(mnuItReExecute);

        // Window menu
        JMenu mnuWindow = new JMenu();
        mnuWindow.setName("mnuWindow");

        JMenuItem mnuItCascade = new JMenuItem();
        mnuItCascade.setAction(actionMap.get("mnuItCascadeWindows"));
        mnuItCascade.setEnabled(false);
        mnuWindow.add(mnuItCascade);

        JMenuItem mnuItTile = new JMenuItem();
        mnuItTile.setAction(actionMap.get("mnuItTileWindows"));
        mnuItTile.setEnabled(false);
        mnuWindow.add(mnuItTile);

        mnuWindow.addSeparator();

        JMenuItem mnuItCloseAll = new JMenuItem();
        mnuItCloseAll.setAction(actionMap.get("mnuItCloseAllWindows"));
        mnuItCloseAll.setEnabled(false);
        mnuWindow.add(mnuItCloseAll);
        menuBar.add(mnuWindow);

        // Help menu
        JMenu mnuHelp = new JMenu();
        mnuHelp.setName("mnuHelp");
        JMenuItem mnuItHelpIntroduction = new JMenuItem();
        mnuItHelpIntroduction.setAction(actionMap.get("mnuItHelpIntroduction"));
        mnuItHelpIntroduction.setEnabled(false);
        mnuHelp.add(mnuItHelpIntroduction);

        JMenuItem mnuItHelpCommands = new JMenuItem();
        mnuItHelpCommands.setAction(actionMap.get("mnuItHelpCommands"));
        mnuItHelpCommands.setEnabled(false);
        mnuHelp.add(mnuItHelpCommands);

        if (isMac()) {
            configureMacAboutBox(actionMap.get("mnuItHelpAbout"));
        } else {
            JMenuItem mnuItAbout = new JMenuItem();
            mnuItAbout.setAction(actionMap.get("mnuItHelpAbout"));
            mnuHelp.add(mnuItAbout);
        }

        menuBar.add(mnuHelp);

        return menuBar;
    }

    // ============== File menu actions ==============================
    @Action
    public void mnuItNewDataSet() {
        executeDirective(new NewDatasetDirective(), null);
    }

    @Action
    public void mnuItPreferences() {

    }

    @Action
    public void mnuItContents() {

    }

    @Action
    public void mnuItFileCharacters() {
        executeDirective(new FileCharactersDirective(), null);
    }

    @Action
    public void mnuItFileTaxa() {
        executeDirective(new FileTaxaDirective(), null);
    }

    @Action
    public void mnuItComment() {
    }

    @Action
    public void mnuItShow() {
    }

    @Action
    public void mnuItNormalMode() {
        if (!normalMode) {
            _pnlAvailableCharactersButtons.remove(_btnSeparate);
            _pnlAvailableCharactersButtons.remove(_btnSetTolerance);
            _rootPanel.remove(_txtFldCmdBar);
            _rootPanel.revalidate();
            normalMode = true;
        } else {
            _rootPanel.add(_txtFldCmdBar, BorderLayout.SOUTH);
            _rootPanel.revalidate();
            normalMode = false;
        }
    }

    @Action
    public void mnuItEditIndex() {
    }

    @Action
    public void mnuItExitApplication() {
        exit();
    }

    // ============================ ReExecute menu actions
    // ===========================

    @Action
    public void mnuItReExecute() {
        ReExecuteDialog dlg = new ReExecuteDialog(getMainFrame(), _context.getExecutedDirectives());
        dlg.setVisible(true);
        IntkeyDirectiveInvocation directive = dlg.getDirectiveToExecute();
        if (directive != null) {
            _context.executeDirective(directive);
        }
    }

    // ============================= Window menu actions
    // ==============================
    @Action
    public void mnuItCascadeWindows() {
    }

    @Action
    public void mnuItTileWindows() {
    }

    @Action
    public void mnuItCloseAllWindows() {
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
        AboutBox aboutBox = new AboutBox(getMainFrame());
        show(aboutBox);
    }

    // ============================== Global option buttons
    // ================================

    @Action
    public void btnContextHelp() {
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
    }

    @Action
    public void btnSubsetCharacters() {
        executeDirective(new IncludeCharactersDirective(), null);
    }

    @Action
    public void btnFindCharacter() {
    }

    // ============================= Taxon toolbar button actions
    // ===========================

    @Action
    public void btnTaxonInfo() {
    }

    @Action
    public void btnDiffTaxa() {
        List<Item> selectedTaxa = new ArrayList<Item>();

        for (int i : _listRemainingTaxa.getSelectedIndices()) {
            selectedTaxa.add(_availableTaxaListModel.getTaxonAt(i));
        }

        for (int i : _listEliminatedTaxa.getSelectedIndices()) {
            selectedTaxa.add(_eliminatedTaxaListModel.getTaxonAt(i));
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
        
    }

    // =========================================================================================

    private void executeDirective(IntkeyDirective dir, String data) {
        try {
            dir.parseAndProcess(_context, data);
        } catch (IntkeyDirectiveParseException ex) {
            ex.printStackTrace();
            String msg = ex.getMessage();
            JOptionPane.showMessageDialog(UIUtils.getMainFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
            Logger.error(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
            String msg = String.format("Error occurred while processing '%s' command: %s", StringUtils.join(dir.getControlWords(), " ").toUpperCase(), ex.getMessage());
            JOptionPane.showMessageDialog(UIUtils.getMainFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
            Logger.error(msg);
            Logger.error(ex);
        }
    }

    private void executeDirectiveInvocation(IntkeyDirectiveInvocation invoc) {
        _context.executeDirective(invoc);
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

        switch (charOrder) {

        case BEST:
            LinkedHashMap<Character, Double> bestCharactersMap = _context.getBestCharacters();
            if (bestCharactersMap != null) {
                _lblNumAvailableCharacters.setText(String.format(bestCharactersCaption, bestCharactersMap.keySet().size()));
                if (bestCharactersMap.isEmpty()) {
                    handleNoAvailableCharacters();
                    return;
                } else {
                    _availableCharacterListModel = new BestCharacterListModel(new ArrayList<Character>(bestCharactersMap.keySet()), bestCharactersMap);
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
                        showBusyMessage(calculatingBestCaption);
                    }
                } catch (InterruptedException ex) {
                    // do nothing
                }

                return;
            }

            break;
        case NATURAL:
            List<Character> availableCharacters = new ArrayList<Character>(_context.getIncludedCharacters());
            availableCharacters.removeAll(_context.getSpecimen().getUsedCharacters());
            Collections.sort(availableCharacters);
            _lblNumAvailableCharacters.setText(String.format(availableCharactersCaption, availableCharacters.size()));
            if (availableCharacters.size() == 0) {
                handleNoAvailableCharacters();
                return;
            } else {
                _availableCharacterListModel = new CharacterListModel(availableCharacters);
                _listAvailableCharacters.setModel(_availableCharacterListModel);
            }
            break;
        case SEPARATE:
            throw new NotImplementedException();
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
                                                                                                       // excluded?
            message = charactersExcludedCannotSeparateCaption;
        } else {
            if (_context.getTolerance() > 0) {
                message = mismatchesAllowCannotSeparateCaption;
            } else {
                message = availableCharactersCannotSeparateCaption;
            }
        }

        JLabel lbl = new JLabel(message);
        lbl.setHorizontalAlignment(JLabel.CENTER);
        lbl.setBackground(Color.WHITE);
        lbl.setOpaque(true);
        _sclPaneAvailableCharacters.setViewportView(lbl);
        _sclPaneAvailableCharacters.revalidate();
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
            _context.calculateBestCharacters();
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

        List<CharacterValue> usedCharacterValues = new ArrayList<CharacterValue>();
        for (Character ch : usedCharacters) {
            usedCharacterValues.add(specimen.getValueForCharacter(ch));
        }

        _usedCharacterListModel = new UsedCharacterListModel(usedCharacterValues);

        _listUsedCharacters.setModel(_usedCharacterListModel);

        _lblNumUsedCharacters.setText(String.format(usedCharactersCaption, _usedCharacterListModel.getSize()));
    }

    private void updateAvailableTaxa(List<Item> availableTaxa, Map<Item, Integer> taxaDifferenceCounts) {
        if (_context.getTolerance() > 0) {
            _availableTaxaListModel = new TaxonWithDifferenceCountListModel(availableTaxa, taxaDifferenceCounts);
        } else {
            _availableTaxaListModel = new TaxonListModel(availableTaxa);
        }

        _listRemainingTaxa.setModel(_availableTaxaListModel);

        _lblNumRemainingTaxa.setText(String.format(remainingTaxaCaption, _availableTaxaListModel.getSize()));
    }

    private void updateUsedTaxa(List<Item> eliminatedTaxa, Map<Item, Integer> taxaDifferenceCounts) {
        _eliminatedTaxaListModel = new TaxonWithDifferenceCountListModel(eliminatedTaxa, taxaDifferenceCounts);
        _listEliminatedTaxa.setModel(_eliminatedTaxaListModel);

        _lblEliminatedTaxa.setText(String.format(eliminatedTaxaCaption, _eliminatedTaxaListModel.getSize()));
    }

    // ================================== IntkeyUI methods
    // ===========================================================

    @Override
    public void handleNewDataset(IntkeyDataset dataset) {
        getMainFrame().setTitle(String.format(windowTitleWithDatasetTitle, dataset.getHeading()));

        // display startup images
        List<Image> startupImages = dataset.getStartupImages();
        if (startupImages != null) {
            ImageUtils.displayImagesFullScreen(startupImages, _context.getImageSettings(), getMainFrame());
        }

        initializeIdentification();
    }

    @Override
    public void handleUpdateAll() {

        Specimen specimen = _context.getSpecimen();

        // Build the lists of available and eliminated taxa. The tolerance
        // setting
        // needs to be accounted for
        int tolerance = _context.getTolerance();
        Map<Item, Integer> taxaDifferenceCounts = specimen.getTaxonDifferences();

        Set<Item> includedTaxa = _context.getIncludedTaxa();
        List<Item> availableTaxa = new ArrayList<Item>();
        List<Item> eliminatedTaxa = new ArrayList<Item>();

        if (taxaDifferenceCounts != null) {
            for (Item taxon : includedTaxa) {
                if (taxaDifferenceCounts.containsKey(taxon)) {
                    int diffCount = taxaDifferenceCounts.get(taxon);
                    if (diffCount > tolerance) {
                        eliminatedTaxa.add(taxon);
                    } else {
                        availableTaxa.add(taxon);
                    }
                } else {
                    availableTaxa.add(taxon);
                }
            }
        } else {
            availableTaxa.addAll(includedTaxa);
        }

        _btnDiffSpecimenTaxa.setEnabled(availableTaxa.size() > 0 && eliminatedTaxa.size() > 0);

        // Need to display a message in place of the list of available
        // characters
        // if there are no remaining taxa (no matching taxa remain), or only 1
        // remaining taxon (identification complete)
        if (availableTaxa.size() > 1) {
            updateAvailableCharacters();
        } else {
            String message = null;

            if (availableTaxa.size() == 0) {
                message = noMatchingTaxaRemainCaption;
            } else if (availableTaxa.size() == 1) {
                message = identificationCompleteCaption;
            }

            JLabel lbl = new JLabel(message);
            lbl.setHorizontalAlignment(JLabel.CENTER);
            lbl.setBackground(Color.WHITE);
            lbl.setOpaque(true);
            _sclPaneAvailableCharacters.setViewportView(lbl);
            _sclPaneAvailableCharacters.revalidate();

            switch (_context.getCharacterOrder()) {
            case NATURAL:
                _lblNumAvailableCharacters.setText(String.format(availableCharactersCaption, 0));
                break;
            case BEST:
                _lblNumAvailableCharacters.setText(String.format(bestCharactersCaption, 0));
                break;
            case SEPARATE:
                throw new NotImplementedException();
            default:
                throw new RuntimeException("Unrecognized character order");
            }
        }

        updateUsedCharacters();
        updateAvailableTaxa(availableTaxa, taxaDifferenceCounts);
        updateUsedTaxa(eliminatedTaxa, taxaDifferenceCounts);
    }

    @Override
    public void handleIdentificationRestarted() {
        _btnDiffSpecimenTaxa.setEnabled(false);
        handleUpdateAll();
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
                showBusyMessage(loadingReportCaption);
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
            _dlg = new RtfReportDisplayDialog(getMainFrame(), new SimpleRtfEditorKit(), _rtfSource, _title);
            return null;
        }

        @Override
        protected void done() {
            Intkey.this.show(_dlg);
            removeBusyMessage();
        }

    }

    @Override
    public void displayErrorMessage(String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void displayWarningMessage(String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void displayBusyMessage(String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeBusyMessage(String message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addToolbarButton(boolean advancedModeOnly, boolean normalModeOnly, boolean inactiveUnlessUsedCharacters, String imageFileName, String commands, String shortHelp, String fullHelp) {
        Icon icon;

        File initializationFile = _context.getInitializationFile();
        if (initializationFile != null) {
            File parentDir = initializationFile.getParentFile();

        } else {

        }

    }

    @Override
    public void addToolbarSpace() {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearToolbar() {
        // TODO Auto-generated method stub

    }

    // ================================== DirectivePopulator methods
    // ===================================================================

    @Override
    public List<Character> promptForCharacters(String directiveName, boolean permitSelectionFromIncludedCharactersOnly) {
        CharacterKeywordSelectionDialog dlg = new CharacterKeywordSelectionDialog(getMainFrame(), _context, directiveName, permitSelectionFromIncludedCharactersOnly);
        show(dlg);
        return dlg.getSelectedCharacters();
    }

    @Override
    public List<Item> promptForTaxa(String directiveName, boolean permitSelectionFromIncludedTaxaOnly) {
        TaxonKeywordSelectionDialog dlg = new TaxonKeywordSelectionDialog(getMainFrame(), _context, directiveName, permitSelectionFromIncludedTaxaOnly);
        show(dlg);
        return dlg.getSelectedTaxa();
    }

    @Override
    public Boolean promptForYesNoOption(String message) {
        int selectedOption = JOptionPane.showConfirmDialog(getMainFrame(), message, null, JOptionPane.YES_NO_CANCEL_OPTION);
        if (selectedOption == JOptionPane.YES_OPTION) {
            return true;
        } else if (selectedOption == JOptionPane.NO_OPTION) {
            return false;
        } else {
            return null;
        }
    }

    @Override
    public String promptForString(String message, String initialValue, String directiveName) {
        return (String) JOptionPane.showInputDialog(getMainFrame(), message, directiveName, JOptionPane.PLAIN_MESSAGE, null, null, initialValue);
    }

    @Override
    public List<String> promptForTextValue(TextCharacter ch) {
        TextInputDialog dlg = new TextInputDialog(getMainFrame(), ch, _context.getImageSettings());
        UIUtils.showDialog(dlg);
        return dlg.getInputData();
    }

    @Override
    public Set<Integer> promptForIntegerValue(IntegerCharacter ch) {
        IntegerInputDialog dlg = new IntegerInputDialog(getMainFrame(), ch, _context.getImageSettings());
        UIUtils.showDialog(dlg);
        return dlg.getInputData();
    }

    @Override
    public FloatRange promptForRealValue(RealCharacter ch) {
        RealInputDialog dlg = new RealInputDialog(getMainFrame(), ch, _context.getImageSettings());
        UIUtils.showDialog(dlg);
        return dlg.getInputData();
    }

    @Override
    public Set<Integer> promptForMultiStateValue(MultiStateCharacter ch) {
        MultiStateInputDialog dlg = new MultiStateInputDialog(getMainFrame(), ch, _context.getImageSettings());
        UIUtils.showDialog(dlg);
        return dlg.getInputData();
    }
    
    //=====================================================================================================
    
    /*int findAllTaxa(String searchText, boolean selectAll, boolean searchSynonyms, boolean searchEliminatedTaxa) {
        //IntkeyDataset dataset = _context.getDataset();
        //_context.get
        
        //return 0;
    }*/
    
    
    

}
