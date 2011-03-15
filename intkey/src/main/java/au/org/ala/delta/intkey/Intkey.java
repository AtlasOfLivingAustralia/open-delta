package au.org.ala.delta.intkey;

import java.awt.Dimension;
import java.awt.SystemColor;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

import org.jdesktop.application.SingleFrameApplication;

public class Intkey extends SingleFrameApplication {
    
    JDesktopPane _desktop;
    
    public static void main(String[] args) {
        launch(Intkey.class, args);
    }

    @Override
    protected void startup() {
        JFrame mainFrame = getMainFrame();
        mainFrame.setTitle("Intkey");
        mainFrame.setPreferredSize(new Dimension(800,600));
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        _desktop = new JDesktopPane();
        _desktop.setBackground(SystemColor.control);

        show(_desktop);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }

}
