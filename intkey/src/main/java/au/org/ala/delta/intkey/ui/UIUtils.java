package au.org.ala.delta.intkey.ui;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.UUID;

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

    /**
     * Displays the file specified by the supplied URL. If the URL specifies a
     * remote file, the file will be downloaded first. The thread will block
     * while the download occurs.
     * 
     * @param fileURL
     *            A URL pointing to the file of interest
     * @param description
     *            A description of the file
     * @param desktop
     *            A reference to the AWT Desktop
     * @throws Exception
     *             If an unrecoverable error occurred while downloading or
     *             displaying the file.
     */
    public static void displayFileFromURL(URL fileURL, String description, Desktop desktop) throws Exception {
        String fileName = fileURL.getFile();

        if (fileName.toLowerCase().endsWith(".rtf")) {
            File file = convertURLToFile(fileURL, 60000);
            String rtfSource = FileUtils.readFileToString(file);
            RtfReportDisplayDialog dlg = new RtfReportDisplayDialog(getMainFrame(), new SimpleRtfEditorKit(null), rtfSource, description);
            ((SingleFrameApplication) Application.getInstance()).show(dlg);
        } else if (fileName.toLowerCase().endsWith(".html")) {
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(fileURL.toURI());
            }
        } else if (fileName.toLowerCase().endsWith(".ink")) {
            System.out.println(fileURL);
        } else if (fileName.toLowerCase().endsWith(".wav")) {
            AudioPlayer.playClip(fileURL);
        } else {
            // Open a http link that does not point to a .rtf, .ink or .wav in
            // the browser
            if (fileURL.getProtocol().equalsIgnoreCase("http")) {
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(fileURL.toURI());
                }
            } else {
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    File file = convertURLToFile(fileURL, 60000);
                    desktop.open(file);
                }
            }
        }
    }

    /**
     * Converts a URL into a file reference. If the URL is a file:/// url, then
     * simply return the underlying file. Otherwise, first copy the URL content
     * to a local temp file, then return the temp file.
     * 
     * @param url
     * @param timeout
     * @return
     * @throws Exception
     */
    public static File convertURLToFile(URL url, int timeout) throws Exception {
        if (url.getProtocol().equalsIgnoreCase("file")) {
            return new File(url.toURI());
        } else {
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), null);
            tempFile.deleteOnExit();
            FileUtils.copyURLToFile(url, tempFile, timeout, timeout);
            return tempFile;
        }
    }

    public static File findFile(String filePath, File datasetDirectory) {
        File file = null;
        // If the supplied file path starts with one of the file system
        // roots, then it is absolute. Otherwise, assume that
        // it is relative to the directory in which the dataset is located.
        boolean fileAbsolute = false;
        for (File root : File.listRoots()) {
            if (filePath.toLowerCase().startsWith(root.getAbsolutePath().toLowerCase())) {
                fileAbsolute = true;
                break;
            }
        }

        if (fileAbsolute) {
            file = new File(filePath);
        } else {
            file = new File(datasetDirectory, filePath);
        }

        return file;
    }
}
