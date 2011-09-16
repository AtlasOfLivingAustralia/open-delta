package au.org.ala.delta.intkey.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.ui.image.AudioPlayer;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;

public class UIUtils {

    /**
     * Needs to be called instead of dlg.setVisible so that BSAF can inject
     * resources/actions as needed.
     * 
     * @param dlg
     */
    public static void showDialog(JDialog dlg) {
        Intkey appUI = (Intkey) Application.getInstance();
        appUI.show(dlg);
    }

    public static JFrame getMainFrame() {
        return ((SingleFrameApplication) Application.getInstance()).getMainFrame();
    }

    public static String getResourceString(String key) {
        try {
            Application app = Application.getInstance();
            String str = app.getContext().getResourceMap().getString(key);
            return str;
        } catch (IllegalStateException ex) {
            // To help with unit testing, return empty string if the Swing
            // Application
            // Framework's Application
            // singleton is not
            // launched.
            return StringUtils.EMPTY;
        }
    }

    public static void displayFile(File file, String description, Desktop desktop) throws IOException {

        String fileName = file.getName();
        
        if (fileName.toLowerCase().endsWith(".rtf")) {
            String rtfSource = FileUtils.readFileToString(file);
            RtfReportDisplayDialog dlg = new RtfReportDisplayDialog(getMainFrame(), new SimpleRtfEditorKit(null), rtfSource, description);
            ((SingleFrameApplication) Application.getInstance()).show(dlg);
        } else if (fileName.toLowerCase().endsWith(".html")) {
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(file.toURI());
            }
        } else if (fileName.toLowerCase().endsWith(".ink")) {
            System.out.println(file.getAbsolutePath());
        } else if (fileName.toLowerCase().endsWith(".wav")) {
            AudioPlayer.playClip(file.toURI().toURL());
        } else {
            if (desktop.isSupported(Desktop.Action.OPEN)) {
                desktop.open(file);
            }
        }
    }
}
