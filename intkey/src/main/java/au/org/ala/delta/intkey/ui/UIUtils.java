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

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.ui.help.HelpController;
import au.org.ala.delta.ui.image.AudioPlayer;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;
import au.org.ala.delta.util.Pair;
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
            File file = Utils.saveURLToTempFile(fileURL, "Intkey", 30000);
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
            File file = Utils.saveURLToTempFile(fileURL, "Intkey", 30000);
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
                File file = Utils.saveURLToTempFile(fileURL, "Intkey", 30000);
                desktop.open(file);
            }
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

    /**
     * Prompts for a file using the file chooser dialog
     * 
     * @param fileExtensions
     *            Accepted file extensions
     * @param description
     *            Description of the acceptable files
     * @param createFileIfNonExistant
     *            if true, the file will be created if it does not exist. Also,
     *            the system's "save" will be used instead of the "open" dialog.
     * @param startBrowseDirectory
     *            The directory that the file chooser should start in
     * @param parent
     *            parent component for the file chooser
     * @return the selected file, or null if no file was selected.
     * @throws IOException
     */
    public static File promptForFile(List<String> fileExtensions, String description, boolean createFileIfNonExistant, File startBrowseDirectory, Component parent) throws IOException {
        String[] extensionsArray = new String[fileExtensions.size()];
        fileExtensions.toArray(extensionsArray);

        JFileChooser chooser = new JFileChooser(startBrowseDirectory);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensionsArray);
        chooser.setFileFilter(filter);

        int returnVal;

        if (createFileIfNonExistant) {
            returnVal = chooser.showSaveDialog(parent);
        } else {
            returnVal = chooser.showOpenDialog(parent);
        }

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            if (createFileIfNonExistant) {
                File file = chooser.getSelectedFile();

                if (!file.exists()) {
                    // if only one file extension was supplied and the filename
                    // does
                    // not end with this extension, add it before
                    // creating the file
                    if (fileExtensions.size() == 1) {
                        String extension = fileExtensions.get(0);
                        String filePath = chooser.getSelectedFile().getAbsolutePath();
                        if (!filePath.endsWith(extension)) {
                            file = new File(filePath + "." + extension);
                        }
                    }

                    file.createNewFile();
                }
                return file;
            } else {
                return chooser.getSelectedFile();
            }
        } else {
            return null;
        }
    }

    /**
     * Constants for handling of Intkey preferences values
     */
    public static String MRU_FILES_PREF_KEY = "MRU";
    public static String MRU_FILES_SEPARATOR = "\n";
    public static String MRU_ITEM_SEPARATOR = ";";
    public static int MAX_SIZE_MRU = 10;

    public static String MODE_PREF_KEY = "MODE";
    public static String BASIC_MODE_PREF_VALUE = "BASIC";
    public static String ADVANCED_MODE_PREF_VALUE = "ADVANCED";

    public static String LAST_OPENED_DATASET_LOCATION_PREF_KEY = "LAST_OPENED_DATASET_LOCATION";

    public static String DATASET_INDEX_PREF_KEY = "DATASET_INDEX";

    public static List<Pair<String, String>> getPreviouslyUsedFiles() {
        List<Pair<String, String>> retList = new ArrayList<Pair<String, String>>();

        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        if (prefs != null) {
            String mru = prefs.get(MRU_FILES_PREF_KEY, "");
            if (!StringUtils.isEmpty(mru)) {
                String[] mruFiles = mru.split(MRU_FILES_SEPARATOR);
                for (String mruFile : mruFiles) {
                    String[] mruFileItems = mruFile.split(MRU_ITEM_SEPARATOR);
                    retList.add(new Pair<String, String>(mruFileItems[0], mruFileItems[1]));
                }
            }
        }

        return retList;
    }

    /**
     * Removes the specified file from the most recently used file list
     * 
     * @param filename
     *            The filename to remove
     */
    public static void removeFileFromMRU(String filename) {

        List<Pair<String, String>> existingFiles = getPreviouslyUsedFiles();

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < existingFiles.size(); ++i) {

            Pair<String, String> fileNameTitlePair = existingFiles.get(i);
            String existingFileName = fileNameTitlePair.getFirst();

            if (!existingFileName.equalsIgnoreCase(filename)) {

                if (b.length() > 0) {
                    b.append(MRU_FILES_SEPARATOR);
                }
                b.append(fileNameTitlePair.getFirst() + MRU_ITEM_SEPARATOR + fileNameTitlePair.getSecond());
            }
        }

        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        prefs.put(MRU_FILES_PREF_KEY, b.toString());
        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Adds the supplied filename to the top of the most recently used files.
     * 
     * @param filename
     */
    public static void addFileToMRU(String filename, String title, List<Pair<String, String>> existingFiles) {

        Queue<String> q = new LinkedList<String>();

        String newFilePathAndTitle;
        if (StringUtils.isEmpty(title)) {
            newFilePathAndTitle = filename + MRU_ITEM_SEPARATOR + filename;
        } else {
            newFilePathAndTitle = filename + MRU_ITEM_SEPARATOR + title;
        }
        q.add(newFilePathAndTitle);

        if (existingFiles != null) {
            for (Pair<String, String> existingFile : existingFiles) {
                String existingFilePathAndTitle = existingFile.getFirst() + MRU_ITEM_SEPARATOR + existingFile.getSecond();
                if (!q.contains(existingFilePathAndTitle)) {
                    q.add(existingFilePathAndTitle);
                }
            }
        }

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < MAX_SIZE_MRU && q.size() > 0; ++i) {
            if (i > 0) {
                b.append(MRU_FILES_SEPARATOR);
            }
            b.append(q.poll());
        }

        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        prefs.put(MRU_FILES_PREF_KEY, b.toString());
        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return true if the last time the application was closed, advanced mode
     *         was in use
     */
    public static boolean getPreviousApplicationMode() {
        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        if (prefs != null) {
            String previouslyUsedMode = prefs.get(MODE_PREF_KEY, "");
            if (!StringUtils.isEmpty(previouslyUsedMode)) {
                return previouslyUsedMode.equals(ADVANCED_MODE_PREF_VALUE);
            }
        }
        return false;
    }

    /**
     * Save the mode in which the application was last used before shutdown -
     * advanced or basic - to the preferences
     * 
     * @param advancedMode
     *            true if application was last used in advanced mode
     */
    public static void savePreviousApplicationMode(boolean advancedMode) {
        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        prefs.put(MODE_PREF_KEY, advancedMode ? ADVANCED_MODE_PREF_VALUE : BASIC_MODE_PREF_VALUE);
        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the parent directory for the dataset that was most recently
     *         opened using intkey
     */
    public static File getSavedLastOpenedDatasetDirectory() {
        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        if (prefs != null) {
            String lastOpenedDirectoryPath = prefs.get(LAST_OPENED_DATASET_LOCATION_PREF_KEY, "");
            if (!StringUtils.isEmpty(lastOpenedDirectoryPath)) {
                return new File(lastOpenedDirectoryPath);
            }
        }
        return null;
    }

    /**
     * Save the parent directory for the most recently opened dataset to
     * preferences
     * 
     * @param lastOpenedDatasetDirectory
     */
    public static void saveLastOpenedDatasetDirectory(File lastOpenedDatasetDirectory) {
        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);
        prefs.put(LAST_OPENED_DATASET_LOCATION_PREF_KEY, lastOpenedDatasetDirectory.getAbsolutePath());
        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the dataset index from preferences
     * 
     * @return The dataset index - a list of dataset description, dataset path
     *         value pairs
     */
    public static List<Pair<String, String>> readDatasetIndex() {
        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);

        List<Pair<String, String>> indexList = new ArrayList<Pair<String, String>>();

        if (prefs != null) {
            String datasetIndexJSON = prefs.get(DATASET_INDEX_PREF_KEY, "");
            if (!StringUtils.isEmpty(datasetIndexJSON)) {
                List<List<String>> deserializedJSON = (List<List<String>>) JSONSerializer.toJava(JSONArray.fromObject(datasetIndexJSON));
                for (List<String> datasetInfoList : deserializedJSON) {
                    indexList.add(new Pair<String, String>(datasetInfoList.get(0), datasetInfoList.get(1)));
                }
            }
        }

        return indexList;
    }

    /**
     * Save the dataset index to preferences.
     * 
     * @param datasetIndexList
     *            The dataset index - a list of dataset description, dataset
     *            path value pairs
     */
    public static void writeDatasetIndex(List<Pair<String, String>> datasetIndexList) {
        Preferences prefs = Preferences.userNodeForPackage(Intkey.class);

        List<List<String>> listToSerialize = new ArrayList<List<String>>();

        for (Pair<String, String> datasetInfoPair : datasetIndexList) {
            listToSerialize.add(Arrays.asList(datasetInfoPair.getFirst(), datasetInfoPair.getSecond()));
        }

        String jsonList = JSONSerializer.toJSON(listToSerialize).toString();
        prefs.put(DATASET_INDEX_PREF_KEY, jsonList);
        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the dataset index as a map
     * 
     * @return a map of dataset path to dataset description key value pairs.
     *         NOTE this is different from readDatasetIndex and
     *         writeDatasetIndex - in both those methods, the dataset
     *         description appears first in the value pairs for each index in
     *         the index.
     */
    public static Map<String, String> getDatasetIndexAsMap() {
        Map<String, String> map = new HashMap<String, String>();

        for (Pair<String, String> descriptionFilePathPair : readDatasetIndex()) {
            map.put(descriptionFilePathPair.getSecond(), descriptionFilePathPair.getFirst());
        }

        return map;
    }

}
