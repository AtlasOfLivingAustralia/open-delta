package au.org.ala.delta.intkey.ui;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.Intkey;

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
}
