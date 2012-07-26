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
package au.org.ala.delta.intkey.ui;

import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.ui.help.HelpController;
import au.org.ala.delta.ui.image.AudioPlayer;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;
import au.org.ala.delta.util.Utils;

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

    public static String getResourceString(String key, Object... arguments) {
        try {
            Application app = Application.getInstance();
            String str = app.getContext().getResourceMap().getString(key);
            if (str == null) {
                return key;
            } else {
                return MessageFormat.format(str, arguments);
            }
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
            String rtfSource = FileUtils.readFileToString(file, "cp1252"); // RTF
                                                                           // must
                                                                           // always
                                                                           // be
                                                                           // read
                                                                           // as
                                                                           // windows
                                                                           // cp1252
                                                                           // encoding
            RtfReportDisplayDialog dlg = new RtfReportDisplayDialog(getMainFrame(), new SimpleRtfEditorKit(null), rtfSource, description);
            ((SingleFrameApplication) Application.getInstance()).show(dlg);
        } else if (fileName.toLowerCase().endsWith(".html")) {
            if (desktop == null || !desktop.isSupported(Desktop.Action.BROWSE)) {
                throw new IllegalArgumentException("Desktop is null or browse not supported");
            }
            desktop.browse(fileURL.toURI());
        } else if (fileName.toLowerCase().endsWith(".ink")) {
            File file = convertURLToFile(fileURL, 60000);
            Utils.launchIntkeyInSeparateProcess(System.getProperty("user.dir"), file.getAbsolutePath());
        } else if (fileName.toLowerCase().endsWith(".wav")) {
            AudioPlayer.playClip(fileURL);
        } else {
            // Open a http link that does not point to a .rtf, .ink or .wav in
            // the browser
            if (fileURL.getProtocol().equalsIgnoreCase("http")) {
                if (desktop == null || !desktop.isSupported(Desktop.Action.BROWSE)) {
                    throw new IllegalArgumentException("Desktop is null or browse not supported");
                }
                desktop.browse(fileURL.toURI());
            } else {
                if (desktop == null || !desktop.isSupported(Desktop.Action.OPEN)) {
                    throw new IllegalArgumentException("Desktop is null or open not supported");
                }
                File file = convertURLToFile(fileURL, 60000);
                desktop.open(file);
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

    /**
     * Display a help topic in the help viewer
     * 
     * @param helpID
     *            the ID of the desired help topic
     * @param activationWindow
     *            the activation (parent) window for the help viewer
     * @param event
     *            the ActionEvent causing the help to be opened. This is
     *            required to pass to the helpController's "helpAction" listener
     */
    public static void displayHelpTopic(String helpID, Window activationWindow, ActionEvent event) {
        HelpController helpController = new HelpController(Intkey.HELPSET_PATH);
        helpController.helpAction().actionPerformed(event);
        helpController.displayHelpTopic(activationWindow, helpID);
    }

    /**
     * For a given directive, return the helpID for the directive
     * 
     * @param directiveName
     * @return the helpID for the directive
     */
    public static String getHelpIDForDirective(String directiveName) {
        return "directive_" + directiveName.split(" ")[0].toLowerCase();
    }
}
