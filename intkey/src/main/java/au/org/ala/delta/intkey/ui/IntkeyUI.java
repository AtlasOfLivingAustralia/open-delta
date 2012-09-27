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

import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.SwingWorker;

import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Item;

/**
 * Interface for the Main Intkey UI.
 * @author ChrisF
 *
 */
public interface IntkeyUI {

    /**
     * Updates the UI when a new dataset has been loaded
     * 
     * @param dataset
     */
    void handleNewDataset(IntkeyDataset dataset);

    /**
     * Performs cleanup and related tasks when a dataset is closed
     */
    void handleDatasetClosed();

    /**
     * Called to do a complete refresh of the GUI
     */
    void handleUpdateAll();

    /**
     * Called when the identification is restarted (via the RESTART) directive
     */
    void handleIdentificationRestarted();

    /**
     * Displays RTF content in a dialog
     * 
     * @param rtfSource
     *            The source of the RTF to display
     * @param title
     *            The title for the dialog
     */
    void displayRTFReport(String rtfSource, String title);

    /**
     * Displays RTF content from a file in a dialog
     * 
     * @param rtfFile
     *            The file containing RTF content
     * @param title
     *            The title for the dialog
     */
    void displayRTFReportFromFile(File rtfFile, String title);

    /**
     * Displays an error message
     * 
     * @param message
     *            The message to display
     */
    void displayErrorMessage(String message);

    /**
     * Displays an error message
     * 
     * @param message
     *            The message to display
     */
    void displayInformationMessage(String message);

    /**
     * Displays a busy message for when a background task is being carried out
     * 
     * @param message
     *            The message to display
     */
    void displayBusyMessage(String message);

    /**
     * Displays a busy message for when a background tasks is being carried out.
     * The user can cancel the background task if desired
     * 
     * @param message
     *            The message to display
     * @param worker
     *            The swing worker responsible for running the background task
     */
    void displayBusyMessageAllowCancelWorker(String message, SwingWorker<?, ?> worker);

    /**
     * Removes a previously displayed busy message
     */
    void removeBusyMessage();

    /**
     * Display information for the supplied taxa
     * 
     * @param taxa
     *            the list of taxa
     * @param imagesAutoDisplayText
     *            for the first taxon whose information is shown, display all
     *            images whose subjects contain this text. If the text is an
     *            empty string, display all available images for the taxon. If
     *            the text is null, do not display any images.
     * @param otherItemsAutoDisplayText
     *            for the first taxon whose information is shown, display all
     *            non-image information items (with the exception of linked
     *            files that are not RTF files) whose subjects contain the
     *            supplied text. If the text is an empty string, display all
     *            available non-image information items (aside from linked files
     *            that are not RTF files). If the text is null, do not display
     *            any non-image information items
     * @param closePromptAfterAutoDisplay
     *            if true, and imagesAutoDisplay text and or
     *            otherItemsAutoDisplayText are not null, close the window after
     *            displaying images and non-image information items
     */
    void displayTaxonInformation(List<Item> taxa, String imagesAutoDisplayText, String otherItemsAutoDisplayText, boolean closePromptAfterAutoDisplay);

    /**
     * Add a button to the main GUI's toolbar
     * 
     * @param advancedModeOnly
     *            True if the button is visible in advanced mode only
     * @param normalModeOnly
     *            True if the button is visible in normal mode only
     * @param inactiveUnlessUsedCharacters
     *            True if the button should be disabled unless characters have
     *            been used
     * @param imageFileName
     *            The name of the image file to use for the button's icon
     * @param commands
     *            A semi-colon delimited string of directives to run when the
     *            button is pressed
     * @param shortHelp
     *            Short (tooltip) help for the button
     * @param fullHelp
     *            Longer help for the button
     */
    void addToolbarButton(boolean advancedModeOnly, boolean normalModeOnly, boolean inactiveUnlessUsedCharacters, String imageFileName, List<String> commands, String shortHelp, String fullHelp);

    /**
     * Insert a space on the button toolbar
     */
    void addToolbarSpace();

    /**
     * Clear the toolbar
     */
    void clearToolbar();

    /**
     * Show images for the specified characters
     * 
     * @param characters
     *            The characters
     */
    void illustrateCharacters(List<au.org.ala.delta.model.Character> characters);

    /**
     * Show images for the specified taxa
     * 
     * @param taxa
     *            The taxa
     */
    void illustrateTaxa(List<Item> taxa);

    /**
     * Display list of topics. Each topic has an associated directive. The user
     * can select a topic and associated directive will be run.
     * 
     * @param contentsMap
     *            A map of topic name to directive command.
     */
    void displayContents(LinkedHashMap<String, String> contentsMap);

    /**
     * Display the file at the specified URL.
     * 
     * @param filePath
     *            URL to the file to open.
     * @param description
     *            the description of the file
     */
    void displayFile(URL fileURL, String description);

    /**
     * @return True if the directive log window is visible
     */
    boolean isLogVisible();

    /**
     * Set the directive log visible or invisible
     * 
     * @param visible
     *            if true, the log window will be made visible, otherwise it
     *            will be made invisible
     */
    void setLogVisible(boolean visible);

    /**
     * Update the content of the directive log window
     */
    void updateLog();

    /**
     * Called to quit the application
     */
    void quitApplication();

    /**
     * @return The list of taxa that are currently selected
     */
    List<Item> getSelectedTaxa();

    /**
     * @return The list of characters that are currently selected
     */
    List<au.org.ala.delta.model.Character> getSelectedCharacters();

    /**
     * Sets demonstrationMode on or off
     * 
     * @param demonstrationMode
     *            if true, demonstration mode is turned on, if false it is
     *            turned off
     */
    void setDemonstrationMode(boolean demonstrationMode);

    /**
     * Display help for the supplied help topic id
     * @param topicID the help topic id
     */
    void displayHelpTopic(String topicID);

    /**
     * Returns true if the UI is in "advanced" mode
     * 
     * @return
     */
    boolean isAdvancedMode();
}
