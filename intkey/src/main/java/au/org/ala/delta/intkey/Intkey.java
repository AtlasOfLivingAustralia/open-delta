package au.org.ala.delta.intkey;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.ActionMap;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.ui.AboutBox;
import au.org.ala.delta.ui.DeltaSingleFrameApplication;
import au.org.ala.delta.ui.util.IconHelper;

public class Intkey extends DeltaSingleFrameApplication {
    
    private JPanel _rootPanel;
    private JFrame _mainFrame;
    private JSplitPane _rootSplitPane;
    private JSplitPane _innerSplitPane1;
    private JSplitPane _innerSplitPane2;
    private ActionMap _actionMap;
    
    public static void main(String[] args) {
        launch(Intkey.class, args);
    }

    @Override
    protected void startup() {
        _actionMap = getContext().getActionMap(this);
        //
        _mainFrame = getMainFrame();
        _mainFrame.setTitle("Intkey");
        _mainFrame.setPreferredSize(new Dimension(1000,600));
        _mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        _mainFrame.setIconImages(IconHelper.getRedIconList());
        
        _rootPanel = new JPanel();
        _rootPanel.setBackground(SystemColor.control);
        _rootPanel.setLayout(new BorderLayout());
        
        _innerSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JList(new String[] {"one", "two", "three", "four"}), new JList(new String[] {"five", "six", "seven", "eight"}));
        _innerSplitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JList(new String[] {"nine", "ten", "eleven", "twelve"}), new JList(new String[] {"thirteen", "fourteen", "fifteen", "sixteen"}));
        _innerSplitPane1.setContinuousLayout(true);
        _innerSplitPane2.setContinuousLayout(true);
        _innerSplitPane1.setDividerLocation(0.5);
        _innerSplitPane2.setDividerLocation(0.5);
        
        _rootSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, _innerSplitPane1, _innerSplitPane2);
        _rootSplitPane.setContinuousLayout(true);
        _rootSplitPane.setDividerLocation(0.5);
        
        _rootPanel.add(_rootSplitPane, BorderLayout.CENTER);
        
        _rootPanel.addComponentListener(new ComponentListener() {

            @Override
            public void componentHidden(ComponentEvent e) {
                // do nothing
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // do nothing
            }

            @Override
            public void componentResized(ComponentEvent e) {
                _rootSplitPane.setDividerLocation(2.0/3.0);
                _innerSplitPane1.setDividerLocation(2.0/3.0);
                _innerSplitPane2.setDividerLocation(2.0/3.0);
                
            }

            @Override
            public void componentShown(ComponentEvent e) {
                // do nothing
            }
            
        });
        
        getMainView().setMenuBar(buildMenus());

        show(_rootPanel);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }
    
    private JMenuBar buildMenus() {
        

        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu mnuFile = new JMenu();
        mnuFile.setName("mnuFile");

        JMenuItem mnuItFileExit = new JMenuItem();
        mnuItFileExit.setAction(_actionMap.get("exitApplication"));

        mnuFile.addSeparator();
        mnuFile.add(mnuItFileExit);
        menuBar.add(mnuFile);

        // Help menu
        JMenu mnuHelp = new JMenu();
        mnuHelp.setName("mnuHelp");
        JMenuItem mnuItHelpContents = new JMenuItem();
        mnuItHelpContents.setName("mnuItHelpContents");
        mnuHelp.add(mnuItHelpContents);
        //mnuItHelpContents.addActionListener(_helpController.helpAction());
        
        JMenuItem mnuItHelpOnSelection = new JMenuItem(IconHelper.createImageIcon("help_cursor.png"));
        mnuItHelpOnSelection.setName("mnuItHelpOnSelection");
        
        //mnuItHelpOnSelection.addActionListener(_helpController.helpOnSelectionAction());
        mnuHelp.add(mnuItHelpOnSelection);

        
        JMenuItem mnuItAbout = new JMenuItem();
        mnuItAbout.setAction(_actionMap.get("openAbout"));
        
        mnuHelp.addSeparator();
        mnuHelp.add(mnuItAbout);
        
        menuBar.add(mnuHelp);
        
        return menuBar;
    }
    
    @Action
    public void exitApplication() {
        exit();
    }
    
    @Action
    public void openAbout() {
        AboutBox aboutBox = new AboutBox(getMainFrame());
        show(aboutBox);
    }

}
