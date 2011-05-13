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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import org.jdesktop.application.Action;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.Logger;
import au.org.ala.delta.intkey.directives.FileCharactersDirective;
import au.org.ala.delta.intkey.directives.FileTaxaDirective;
import au.org.ala.delta.intkey.directives.IntkeyDirective;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParser;
import au.org.ala.delta.intkey.directives.NewDatasetDirective;
import au.org.ala.delta.intkey.directives.RestartDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.CharacterValue;
import au.org.ala.delta.intkey.ui.CharacterListModel;
import au.org.ala.delta.intkey.ui.DirectiveAction;
import au.org.ala.delta.intkey.ui.ItemListModel;
import au.org.ala.delta.intkey.ui.ReExecuteDialog;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.ui.AboutBox;
import au.org.ala.delta.ui.DeltaSingleFrameApplication;
import au.org.ala.delta.ui.util.IconHelper;

public class Intkey extends DeltaSingleFrameApplication {

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

    private CharacterListModel _availableCharacterListModel;
    private UsedCharacterListModel _usedCharacterListModel;
    private ItemListModel _itemListModel;

    private JMenu _mnuReExecute;
    private JLabel _lblNumAvailableCharacters;
    private JLabel _lblNumUsedCharacters;

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
    private JLabel _lblNumRemainingTaxa;
    private JLabel _lblEliminatedTaxa;
    private JButton _btnRestart;
    private JButton _btnBestOrder;
    private JButton _btnSeparate;
    private JButton _btnBtnNaturalOrder;
    private JButton _btnDiffSpecimenTaxa;
    private JButton _btnSetTolerance;
    private JButton _btnSetMatch;
    private JButton _btnUseSubset;
    private JButton _btnFindCharacter;
    private JButton _btnTaxonInfo;
    private JButton _btnDiffTaxa;
    private JButton _btnSubsetTaxa;
    private JButton _btnFindTaxon;

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

        _context = new IntkeyContext(this);

        _rootPanel = new JPanel();
        _rootPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        _rootPanel.setBackground(SystemColor.control);
        _rootPanel.setLayout(new BorderLayout(0, 0));

        JPanel globalOptionBar = new JPanel();
        _rootPanel.add(globalOptionBar, BorderLayout.NORTH);
        globalOptionBar.setLayout(new BorderLayout(0, 0));

        JButton btnContextHelp = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/helpa.png"));
        btnContextHelp.setEnabled(false);
        btnContextHelp.setPreferredSize(new Dimension(30, 30));
        btnContextHelp.setMargin(new Insets(2, 5, 2, 5));
        globalOptionBar.add(btnContextHelp, BorderLayout.EAST);

        _rootSplitPane = new JSplitPane();
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
        _innerSplitPaneLeft.setResizeWeight(0.5);

        _innerSplitPaneLeft.setContinuousLayout(true);
        _innerSplitPaneLeft.setOrientation(JSplitPane.VERTICAL_SPLIT);
        _rootSplitPane.setLeftComponent(_innerSplitPaneLeft);

        JPanel pnlAvailableCharacters = new JPanel();
        _innerSplitPaneLeft.setLeftComponent(pnlAvailableCharacters);
        pnlAvailableCharacters.setLayout(new BorderLayout(0, 0));

        JScrollPane sclPaneAvailableCharacters = new JScrollPane();
        pnlAvailableCharacters.add(sclPaneAvailableCharacters, BorderLayout.CENTER);

        _listAvailableCharacters = new JList();
        _listAvailableCharacters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _listAvailableCharacters.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int selectedIndex = _listAvailableCharacters.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        try {
                            Character ch = _availableCharacterListModel.getCharacterAt(selectedIndex);
                            IntkeyDirectiveInvocation invoc = new UseDirective().doProcess(_context, Integer.toString(ch.getCharacterId()));
                            _context.executeDirective(invoc);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        sclPaneAvailableCharacters.setViewportView(_listAvailableCharacters);

        JPanel pnlAvailableCharactersHeader = new JPanel();
        pnlAvailableCharacters.add(pnlAvailableCharactersHeader, BorderLayout.NORTH);
        pnlAvailableCharactersHeader.setLayout(new BorderLayout(0, 0));

        _lblNumAvailableCharacters = new JLabel();
        _lblNumAvailableCharacters.setFont(new Font("Tahoma", Font.PLAIN, 15));
        _lblNumAvailableCharacters.setText(String.format(availableCharactersCaption, 0));
        pnlAvailableCharactersHeader.add(_lblNumAvailableCharacters, BorderLayout.WEST);

        JPanel pnlAvailableCharactersButtons = new JPanel();
        FlowLayout flowLayout = (FlowLayout) pnlAvailableCharactersButtons.getLayout();
        flowLayout.setVgap(2);
        flowLayout.setHgap(2);
        pnlAvailableCharactersHeader.add(pnlAvailableCharactersButtons, BorderLayout.EAST);

        _btnRestart = new JButton();
        Icon restartIcon = IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/restarts.png");
        _btnRestart.setAction(new DirectiveAction(new RestartDirective(), _context, null, restartIcon));
        _btnRestart.setPreferredSize(new Dimension(30, 30));
        pnlAvailableCharactersButtons.add(_btnRestart);

        _btnBestOrder = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/obests.png"));
        _btnBestOrder.setEnabled(false);
        _btnBestOrder.setPreferredSize(new Dimension(30, 30));
        pnlAvailableCharactersButtons.add(_btnBestOrder);

        _btnSeparate = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/oseps.png"));
        _btnSeparate.setEnabled(false);
        _btnSeparate.setPreferredSize(new Dimension(30, 30));
        pnlAvailableCharactersButtons.add(_btnSeparate);

        _btnBtnNaturalOrder = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/onats.png"));
        _btnBtnNaturalOrder.setEnabled(false);
        _btnBtnNaturalOrder.setPreferredSize(new Dimension(30, 30));
        pnlAvailableCharactersButtons.add(_btnBtnNaturalOrder);

        _btnDiffSpecimenTaxa = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/diff_ss.png"));
        _btnDiffSpecimenTaxa.setEnabled(false);
        _btnDiffSpecimenTaxa.setPreferredSize(new Dimension(30, 30));
        pnlAvailableCharactersButtons.add(_btnDiffSpecimenTaxa);

        _btnSetTolerance = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/set_tols.png"));
        _btnSetTolerance.setEnabled(false);
        _btnSetTolerance.setPreferredSize(new Dimension(30, 30));
        pnlAvailableCharactersButtons.add(_btnSetTolerance);

        _btnSetMatch = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/set_mats.png"));
        _btnSetMatch.setEnabled(false);
        _btnSetMatch.setPreferredSize(new Dimension(30, 30));
        pnlAvailableCharactersButtons.add(_btnSetMatch);

        _btnUseSubset = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/inc_cs.png"));
        _btnUseSubset.setEnabled(false);
        _btnUseSubset.setPreferredSize(new Dimension(30, 30));
        pnlAvailableCharactersButtons.add(_btnUseSubset);

        _btnFindCharacter = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/finds.png"));
        _btnFindCharacter.setEnabled(false);
        _btnFindCharacter.setPreferredSize(new Dimension(30, 30));
        pnlAvailableCharactersButtons.add(_btnFindCharacter);

        JPanel pnlUsedCharacters = new JPanel();
        _innerSplitPaneLeft.setRightComponent(pnlUsedCharacters);
        pnlUsedCharacters.setLayout(new BorderLayout(0, 0));

        JScrollPane sclPnUsedCharacters = new JScrollPane();
        pnlUsedCharacters.add(sclPnUsedCharacters, BorderLayout.CENTER);

        _listUsedCharacters = new JList();
        _listUsedCharacters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sclPnUsedCharacters.setViewportView(_listUsedCharacters);

        JPanel pnlUsedCharactersHeader = new JPanel();
        pnlUsedCharacters.add(pnlUsedCharactersHeader, BorderLayout.NORTH);
        pnlUsedCharactersHeader.setLayout(new BorderLayout(0, 0));

        _lblNumUsedCharacters = new JLabel();
        _lblNumUsedCharacters.setBorder(new EmptyBorder(7, 0, 7, 0));
        _lblNumUsedCharacters.setFont(new Font("Tahoma", Font.PLAIN, 15));
        _lblNumUsedCharacters.setText(String.format(usedCharactersCaption, 0));
        pnlUsedCharactersHeader.add(_lblNumUsedCharacters, BorderLayout.WEST);

        _innerSplitPaneRight = new JSplitPane();
        _innerSplitPaneRight.setResizeWeight(0.5);
        _innerSplitPaneRight.setContinuousLayout(true);
        _innerSplitPaneRight.setOrientation(JSplitPane.VERTICAL_SPLIT);
        _rootSplitPane.setRightComponent(_innerSplitPaneRight);

        JPanel pnlRemainingTaxa = new JPanel();
        _innerSplitPaneRight.setLeftComponent(pnlRemainingTaxa);
        pnlRemainingTaxa.setLayout(new BorderLayout(0, 0));

        JScrollPane sclPnRemainingTaxa = new JScrollPane();
        pnlRemainingTaxa.add(sclPnRemainingTaxa, BorderLayout.CENTER);

        _listRemainingTaxa = new JList();
        sclPnRemainingTaxa.setViewportView(_listRemainingTaxa);

        JPanel pnlRemainingTaxaHeader = new JPanel();
        pnlRemainingTaxa.add(pnlRemainingTaxaHeader, BorderLayout.NORTH);
        pnlRemainingTaxaHeader.setLayout(new BorderLayout(0, 0));

        _lblNumRemainingTaxa = new JLabel();
        _lblNumRemainingTaxa.setFont(new Font("Tahoma", Font.PLAIN, 15));
        _lblNumRemainingTaxa.setText(String.format(remainingTaxaCaption, 0));
        pnlRemainingTaxaHeader.add(_lblNumRemainingTaxa, BorderLayout.WEST);

        JPanel pnlRemainingTaxaButtons = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) pnlRemainingTaxaButtons.getLayout();
        flowLayout_1.setVgap(2);
        flowLayout_1.setHgap(2);
        pnlRemainingTaxaHeader.add(pnlRemainingTaxaButtons, BorderLayout.EAST);

        _btnTaxonInfo = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/infos.png"));
        _btnTaxonInfo.setEnabled(false);
        _btnTaxonInfo.setPreferredSize(new Dimension(30, 30));
        pnlRemainingTaxaButtons.add(_btnTaxonInfo);

        _btnDiffTaxa = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/diff_ts.png"));
        _btnDiffTaxa.setEnabled(false);
        _btnDiffTaxa.setPreferredSize(new Dimension(30, 30));
        pnlRemainingTaxaButtons.add(_btnDiffTaxa);

        _btnSubsetTaxa = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/inc_ts.png"));
        _btnSubsetTaxa.setEnabled(false);
        _btnSubsetTaxa.setPreferredSize(new Dimension(30, 30));
        pnlRemainingTaxaButtons.add(_btnSubsetTaxa);

        _btnFindTaxon = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/finds.png"));
        _btnFindTaxon.setEnabled(false);
        _btnFindTaxon.setPreferredSize(new Dimension(30, 30));
        pnlRemainingTaxaButtons.add(_btnFindTaxon);

        JPanel pnlEliminatedTaxa = new JPanel();
        _innerSplitPaneRight.setRightComponent(pnlEliminatedTaxa);
        pnlEliminatedTaxa.setLayout(new BorderLayout(0, 0));

        JScrollPane sclPnEliminatedTaxa = new JScrollPane();
        pnlEliminatedTaxa.add(sclPnEliminatedTaxa, BorderLayout.CENTER);

        _listEliminatedTaxa = new JList();
        sclPnEliminatedTaxa.setViewportView(_listEliminatedTaxa);

        JPanel pnlEliminatedTaxaHeader = new JPanel();
        pnlEliminatedTaxa.add(pnlEliminatedTaxaHeader, BorderLayout.NORTH);
        pnlEliminatedTaxaHeader.setLayout(new BorderLayout(0, 0));

        _lblEliminatedTaxa = new JLabel();
        _lblEliminatedTaxa.setBorder(new EmptyBorder(7, 0, 7, 0));
        _lblEliminatedTaxa.setFont(new Font("Tahoma", Font.PLAIN, 15));
        _lblEliminatedTaxa.setText(String.format(eliminatedTaxaCaption, 0));
        pnlEliminatedTaxaHeader.add(_lblEliminatedTaxa, BorderLayout.WEST);

        JMenuBar menuBar = buildMenus(true);
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
                        IntkeyDirectiveParser.createInstance().parse(new InputStreamReader(new ByteArrayInputStream(cmdStr.getBytes())), _context);
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
        super.shutdown();
    }

    private JMenuBar buildMenus(boolean advancedMode) {

        _cmdMenus = new HashMap<String, JMenu>();

        ActionMap actionMap = getContext().getActionMap();

        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu mnuFile = new JMenu();
        mnuFile.setName("mnuFile");

        JMenuItem mnuItNewDataSet = buildMenuItemForDirective(new NewDatasetDirective(), "mnuDirectiveNewDataSet");
        mnuFile.add(mnuItNewDataSet);

        if (advancedMode) {
            JMenuItem mnuItPreferences = new JMenuItem();
            mnuItPreferences.setAction(actionMap.get("setPreferences"));
            mnuItPreferences.setEnabled(false);
            mnuFile.add(mnuItPreferences);

            JMenuItem mnuItContents = new JMenuItem();
            mnuItContents.setAction(actionMap.get("setContents"));
            mnuItContents.setEnabled(false);
            mnuFile.add(mnuItContents);

            mnuFile.addSeparator();

            JMenu mnuFileCmds = new JMenu();
            mnuFileCmds.setName("mnuFileCmds");

            JMenuItem mnuItFileCharactersCmd = new JMenuItem();
            mnuItFileCharactersCmd.setAction(new DirectiveAction(new FileCharactersDirective(), _context, "mnuDirectiveFileCharacters"));
            mnuFileCmds.add(mnuItFileCharactersCmd);

            JMenuItem mnuItFileTaxaCmd = new JMenuItem();
            mnuItFileTaxaCmd.setAction(new DirectiveAction(new FileTaxaDirective(), _context, "mnuDirectiveFileTaxa"));
            mnuFileCmds.add(mnuItFileTaxaCmd);

            mnuFile.add(mnuFileCmds);

            JMenu mnuOutput = new JMenu("Output");
            mnuOutput.setEnabled(false);
            mnuFile.add(mnuOutput);

            mnuFile.addSeparator();

            JMenuItem mnuItComment = new JMenuItem("Comment...");
            mnuItComment.setEnabled(false);
            mnuFile.add(mnuItComment);

            JMenuItem mnuItShow = new JMenuItem("Show...");
            mnuItShow.setEnabled(false);
            mnuFile.add(mnuItShow);

            mnuFile.addSeparator();

            JMenuItem mnuItNormalMode = new JMenuItem("Normal Mode");
            mnuItNormalMode.setEnabled(false);
            mnuItNormalMode.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    toggleAdvancedMode(true);
                }
            });
            mnuFile.add(mnuItNormalMode);

            mnuFile.addSeparator();

            JMenuItem mnuItEditIndex = new JMenuItem("Edit Index...");
            mnuItEditIndex.setEnabled(false);
            mnuFile.add(mnuItEditIndex);

            _cmdMenus.put("file", mnuFileCmds);
        } else {
            JMenuItem mnuItAdvancedMode = new JMenuItem("Advanced Mode");
            mnuItAdvancedMode.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    toggleAdvancedMode(true);
                }
            });
            mnuFile.add(mnuItAdvancedMode);
        }

        mnuFile.addSeparator();

        JMenuItem mnuItFileExit = new JMenuItem();
        mnuItFileExit.setAction(actionMap.get("exitApplication"));
        mnuFile.add(mnuItFileExit);
        menuBar.add(mnuFile);

        if (advancedMode) {
            _mnuReExecute = new JMenu("ReExecute...");
            _mnuReExecute.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ReExecuteDialog dlg = new ReExecuteDialog(_context.getMainFrame(), _context.getExecutedDirectives());
                    dlg.setVisible(true);
                    _mnuReExecute.setSelected(false);
                    IntkeyDirectiveInvocation directive = dlg.getDirectiveToExecute();
                    if (directive != null) {
                        _context.executeDirective(directive);
                    }
                }
            });
            menuBar.add(_mnuReExecute);

            JMenu mnuQuery = new JMenu("Queries");
            mnuQuery.setEnabled(false);
            menuBar.add(mnuQuery);

            JMenu mnuBrowsing = new JMenu("Browsing");
            mnuBrowsing.setEnabled(false);
            menuBar.add(mnuBrowsing);

            JMenu mnuSettings = new JMenu("Settings");
            mnuSettings.setEnabled(false);
            menuBar.add(mnuSettings);
        }

        // Window menu
        JMenu mnuWindow = new JMenu();
        mnuWindow.setName("mnuWindow");

        JMenuItem mnuItCascade = new JMenuItem();
        mnuItCascade.setAction(actionMap.get("cascadeWindows"));
        mnuItCascade.setEnabled(false);
        mnuWindow.add(mnuItCascade);

        JMenuItem mnuItTile = new JMenuItem();
        mnuItTile.setAction(actionMap.get("tileWindows"));
        mnuItTile.setEnabled(false);
        mnuWindow.add(mnuItTile);

        mnuWindow.addSeparator();

        JMenuItem mnuItCloseAll = new JMenuItem();
        mnuItCloseAll.setAction(actionMap.get("closeAllWindows"));
        mnuItCloseAll.setEnabled(false);
        mnuWindow.add(mnuItCloseAll);
        menuBar.add(mnuWindow);

        // Help menu
        JMenu mnuHelp = new JMenu();
        mnuHelp.setName("mnuHelp");
        JMenuItem mnuItHelpIntroduction = new JMenuItem();
        mnuItHelpIntroduction.setEnabled(false);
        mnuItHelpIntroduction.setName("mnuItHelpIntroduction");
        mnuHelp.add(mnuItHelpIntroduction);
        // mnuItHelpContents.addActionListener(_helpController.helpAction());

        if (advancedMode) {
            JMenuItem mnuItCommands = new JMenuItem("Commands...");
            mnuItCommands.setEnabled(false);
            mnuHelp.add(mnuItCommands);
        }

        if (isMac()) {
            configureMacAboutBox(actionMap.get("openAbout"));
        } else {
            JMenuItem mnuItAbout = new JMenuItem();
            mnuItAbout.setAction(actionMap.get("openAbout"));
            mnuHelp.add(mnuItAbout);
        }

        menuBar.add(mnuHelp);

        return menuBar;
    }

    // File menu actions
    @Action
    public void setAdvancedMode() {
        toggleAdvancedMode(true);
    }

    @Action
    public void setNormalMode() {
        toggleAdvancedMode(false);
    }

    @Action
    public void exitApplication() {
        exit();
    }

    @Action
    public void setContents() {
    }

    @Action
    public void setPreferences() {
    }

    @Action
    public void setFileCharacters() {
    }

    // Window menu actions
    @Action
    public void cascadeWindows() {
    }

    @Action
    public void tileWindows() {
    }

    @Action
    public void closeAllWindows() {
    }

    // Help menu actions

    @Action
    public void openAbout() {
        AboutBox aboutBox = new AboutBox(getMainFrame());
        show(aboutBox);
    }

    private JMenuItem buildMenuItemForDirective(IntkeyDirective dir, String itemName) {
        JMenuItem mnuItNewDataSet = new JMenuItem();
        mnuItNewDataSet.setName(itemName);

        mnuItNewDataSet.setAction(new DirectiveAction(dir, _context, "insert caption here"));

        return mnuItNewDataSet;
    }

    public void handleNewDataSet(IntkeyDataset dataset) {
        getMainFrame().setTitle(String.format(windowTitleWithDatasetTitle, dataset.getHeading()));

        _availableCharacterListModel = new CharacterListModel(dataset.getCharacters());
        _usedCharacterListModel = new UsedCharacterListModel();
        _itemListModel = new ItemListModel(dataset.getTaxa());

        _listAvailableCharacters.setModel(_availableCharacterListModel);
        _listUsedCharacters.setModel(_usedCharacterListModel);
        _listRemainingTaxa.setModel(new ItemListModel(dataset.getTaxa()));

        updateListCaptions();
    }

    public void handleCharacterUsed(Character ch, CharacterValue value) {
        // remove from top list
        _availableCharacterListModel.removeCharacter(ch);

        // add to bottom list
        _usedCharacterListModel.addCharacterValue(ch, value);

        updateListCaptions();
    }

    public void handleCharacterChanged(Character ch, CharacterValue value) {

    }

    public void handleCharacterDeleted(Character ch) {

    }

    public void handleRestartIdentification() {
        // TODO do this properly
        handleNewDataSet(_context.getDataset());
    }

    private void updateListCaptions() {
        _lblNumAvailableCharacters.setText(String.format(availableCharactersCaption, _availableCharacterListModel.getSize()));
        _lblNumUsedCharacters.setText(String.format(usedCharactersCaption, _usedCharacterListModel.getSize()));
        _lblNumRemainingTaxa.setText(String.format(remainingTaxaCaption, _itemListModel.getSize()));
    }

    private void toggleAdvancedMode(boolean advancedMode) {
        _btnSeparate.setVisible(advancedMode);
        _btnSetMatch.setVisible(advancedMode);
        _txtFldCmdBar.setVisible(advancedMode);
        JMenuBar menuBar = buildMenus(advancedMode);
        getMainView().setMenuBar(menuBar);
        getMainFrame().getRootPane().revalidate();
    }

    private class UsedCharacterListModel extends AbstractListModel {

        private List<CharacterValue> _values;
        private HashMap<Character, CharacterValue> _characterValueMap;

        public UsedCharacterListModel() {
            _values = new ArrayList<CharacterValue>();
            _characterValueMap = new HashMap<Character, CharacterValue>();
        }

        @Override
        public int getSize() {
            return _values.size();
        }

        @Override
        public Object getElementAt(int index) {
            return _values.get(index).toString();
        }

        public CharacterValue getCharacterValueAt(int index) {
            return _values.get(index);
        }

        public void addCharacterValue(Character ch, CharacterValue value) {
            _values.add(value);
            _characterValueMap.put(ch, value);
            fireIntervalAdded(this, _values.size() - 1, _values.size() - 1);
        }

        public void removeValueForCharacter(Character ch) {

        }

    }

}
