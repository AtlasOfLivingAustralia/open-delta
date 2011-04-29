package au.org.ala.delta.intkey;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
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

import org.jdesktop.application.Action;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.Logger;
import au.org.ala.delta.intkey.directives.FileCharactersDirective;
import au.org.ala.delta.intkey.directives.FileTaxaDirective;
import au.org.ala.delta.intkey.directives.IntkeyContext;
import au.org.ala.delta.intkey.directives.IntkeyDirective;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParser;
import au.org.ala.delta.intkey.directives.NewDatasetDirective;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
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
    private JList _listBestCharacters;
    private JList _listUsedCharacters;
    private JList _listRemainingTaxa;
    private JList _listEliminatedTaxa;

    @Resource
    String windowTitleWithDatasetTitle;

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

        mainFrame.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing window");
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.out.println("Window closed");
            }

            @Override
            public void windowActivated(WindowEvent arg0) {
                // TODO Auto-generated method stub

            }
        });

        _context = new IntkeyContext(this);

        _rootPanel = new JPanel();
        _rootPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        _rootPanel.setBackground(SystemColor.control);
        _rootPanel.setLayout(new BorderLayout(0, 0));

        JPanel globalOptionBar = new JPanel();
        _rootPanel.add(globalOptionBar, BorderLayout.NORTH);
        globalOptionBar.setLayout(new BorderLayout(0, 0));

        JButton btnContextHelp = new JButton(IconHelper.createImageIconFromAbsolutePath("/au/org/ala/delta/intkey/resources/icons/helpa.png"));
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

        JPanel pnlBestCharacters = new JPanel();
        _innerSplitPaneLeft.setLeftComponent(pnlBestCharacters);
        pnlBestCharacters.setLayout(new BorderLayout(0, 0));

        JScrollPane sclPaneBestCharacters = new JScrollPane();
        pnlBestCharacters.add(sclPaneBestCharacters, BorderLayout.CENTER);

        _listBestCharacters = new JList();
        sclPaneBestCharacters.setViewportView(_listBestCharacters);

        JPanel pnlBestCharactersHeader = new JPanel();
        pnlBestCharacters.add(pnlBestCharactersHeader, BorderLayout.NORTH);
        pnlBestCharactersHeader.setLayout(new BorderLayout(0, 0));

        JLabel lblNumBestCharacters = new JLabel("Best Characters");
        lblNumBestCharacters.setFont(new Font("Tahoma", Font.PLAIN, 15));
        pnlBestCharactersHeader.add(lblNumBestCharacters, BorderLayout.WEST);

        JPanel pnlUsedCharacters = new JPanel();
        _innerSplitPaneLeft.setRightComponent(pnlUsedCharacters);
        pnlUsedCharacters.setLayout(new BorderLayout(0, 0));

        JScrollPane sclPnUsedCharacters = new JScrollPane();
        pnlUsedCharacters.add(sclPnUsedCharacters, BorderLayout.CENTER);

        _listUsedCharacters = new JList();
        sclPnUsedCharacters.setViewportView(_listUsedCharacters);

        JPanel pnlUsedCharactersHeader = new JPanel();
        pnlUsedCharacters.add(pnlUsedCharactersHeader, BorderLayout.NORTH);
        pnlUsedCharactersHeader.setLayout(new BorderLayout(0, 0));

        JLabel lblNumUsedCharacters = new JLabel("Used Characters");
        lblNumUsedCharacters.setFont(new Font("Tahoma", Font.PLAIN, 15));
        pnlUsedCharactersHeader.add(lblNumUsedCharacters, BorderLayout.WEST);

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

        JLabel lblNumRemainingTaxa = new JLabel("Remaining Taxa");
        lblNumRemainingTaxa.setFont(new Font("Tahoma", Font.PLAIN, 15));
        pnlRemainingTaxaHeader.add(lblNumRemainingTaxa, BorderLayout.WEST);

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

        JLabel lblNewLabel = new JLabel("Eliminated Taxa");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        pnlEliminatedTaxaHeader.add(lblNewLabel, BorderLayout.WEST);

        getMainView().setMenuBar(buildMenus());
        
        _txtFldCmdBar = new JTextField();
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
                        ex.printStackTrace();
                    }
                }
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

    private JMenuBar buildMenus() {

        ActionMap actionMap = getContext().getActionMap();

        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu mnuFile = new JMenu();
        mnuFile.setName("mnuFile");

        JMenuItem mnuItNewDataSet = buildMenuItemForDirective(new NewDatasetDirective(), "mnuDirectiveNewDataSet");
        mnuFile.add(mnuItNewDataSet);

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

        JMenuItem mnuItFileCharactersCmd = buildMenuItemForDirective(new FileCharactersDirective(), "mnuDirectiveFileCharacters");
        mnuFileCmds.add(mnuItFileCharactersCmd);

        JMenuItem mnuItFileTaxaCmd = buildMenuItemForDirective(new FileTaxaDirective(), "mnuDirectiveFileTaxa");

        mnuFileCmds.add(mnuItFileTaxaCmd);

        mnuFile.add(mnuFileCmds);

        mnuFile.addSeparator();

        /*JMenuItem mnuItAdvancedMode = new JMenuItem();
        mnuItAdvancedMode.setAction(actionMap.get("switchAdvancedMode"));
        mnuItAdvancedMode.setEnabled(false);
        mnuFile.add(mnuItAdvancedMode);

        mnuFile.addSeparator();*/

        JMenuItem mnuItFileExit = new JMenuItem();
        mnuItFileExit.setAction(actionMap.get("exitApplication"));
        mnuFile.add(mnuItFileExit);
        menuBar.add(mnuFile);

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

        if (isMac()) {
            configureMacAboutBox(actionMap.get("openAbout"));
        } else {
            JMenuItem mnuItAbout = new JMenuItem();
            mnuItAbout.setAction(actionMap.get("openAbout"));
            mnuHelp.add(mnuItAbout);
        }

        menuBar.add(mnuHelp);

        _cmdMenus = new HashMap<String, JMenu>();
        _cmdMenus.put("file", mnuFileCmds);

        return menuBar;
    }

    // File menu actions
    @Action
    public void switchAdvancedMode() {
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
        JMenuItem mnuIt = new JMenuItem();
        mnuIt.setName(itemName);

        mnuIt.addActionListener(new DirectiveMenuActionListener(dir, _context));

        return mnuIt;
    }

    private class DirectiveMenuActionListener implements ActionListener {

        private IntkeyDirective _dir;
        private IntkeyContext _context;

        public DirectiveMenuActionListener(IntkeyDirective dir, IntkeyContext context) {
            _dir = dir;
            _context = context;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                _dir.process(_context, null);
            } catch (Exception ex) {
                Logger.log("Error while running directive from menu - %s %s", _dir.toString(), ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public void handleNewDataSet(IntkeyDataset dataset) {
        getMainFrame().setTitle(String.format(windowTitleWithDatasetTitle, dataset.getHeading()));
        _listBestCharacters.setModel(new CharacterListModel(dataset.getCharacters()));
        _listRemainingTaxa.setModel(new ItemListModel(dataset.getTaxa()));
    }

    private class CharacterListModel extends AbstractListModel {

        List<Character> _characters;
        CharacterFormatter _formatter;

        public CharacterListModel(List<Character> characters) {
            _characters = new ArrayList<Character>(characters);
            _formatter = new CharacterFormatter(false, false, true, true);
        }

        @Override
        public int getSize() {
            return _characters.size();
        }

        @Override
        public Object getElementAt(int index) {
            return _formatter.formatCharacterDescription(_characters.get(index));
        }
    }

    private class ItemListModel extends AbstractListModel {

        List<Item> _items;
        ItemFormatter _formatter;

        public ItemListModel(List<Item> items) {
            _items = new ArrayList<Item>(items);
            _formatter = new ItemFormatter(false, true, false, true, false);
        }

        @Override
        public int getSize() {
            return _items.size();
        }

        @Override
        public Object getElementAt(int index) {
            return _formatter.formatItemDescription(_items.get(index));
        }
    }

}
