package au.org.ala.delta.intkey;

import java.awt.Dimension;
import java.awt.SystemColor;

import javax.swing.ActionMap;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.ui.AboutBox;
import au.org.ala.delta.ui.DeltaSingleFrameApplication;
import au.org.ala.delta.ui.util.IconHelper;

public class Intkey extends DeltaSingleFrameApplication {
    
    private JDesktopPane _desktop;
    private ActionMap _actionMap;
    
    public static void main(String[] args) {
        launch(Intkey.class, args);
    }

    @Override
    protected void startup() {
        _actionMap = getContext().getActionMap(this);
        
        JFrame mainFrame = getMainFrame();
        mainFrame.setTitle("Intkey");
        mainFrame.setPreferredSize(new Dimension(800,600));
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setIconImages(IconHelper.getRedIconList());
       
        _desktop = new JDesktopPane();
        _desktop.setBackground(SystemColor.control);
        
        ResourceMap rm = getContext().getResourceMap(AboutBox.class);
        String foo = rm.getString("AboutBox.windowTitle");
        
        getMainView().setMenuBar(buildMenus());

        show(_desktop);
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
