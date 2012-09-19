package au.org.ala.delta.intkey.ui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import au.org.ala.delta.Logger;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * This class wraps another instance of IntkeyUI. Any methods called from a
 * thread other than the Swing event dispatch thread will be either marshaled
 * onto the Swing event dispatch thread, or ignored.
 * 
 * @author ChrisF
 * 
 */
public class IntkeyUIInterceptor implements IntkeyUI {

    private final IntkeyUI _wrappedUI;

    public IntkeyUIInterceptor(IntkeyUI wrappedUI) {
        _wrappedUI = wrappedUI;
    }

    @Override
    public void handleNewDataset(IntkeyDataset dataset) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.handleNewDataset(dataset);
        } else {
            Logger.error("Ignoring IntkeyUI.handleNewDataset() called from background thread");
        }
    }

    @Override
    public void handleDatasetClosed() {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.handleDatasetClosed();
        } else {
            Logger.error("Ignoring IntkeyUI.handleDatasetClosed() called from background thread");
        }
    }

    @Override
    public void handleUpdateAll() {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.handleUpdateAll();
        } else {
            Logger.error("Ignoring IntkeyUI.handleUpdateAll() called from background thread");
        }
    }

    @Override
    public void handleIdentificationRestarted() {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.handleIdentificationRestarted();
        } else {
            Logger.error("Ignoring IntkeyUI.handleIdentificationRestarted() called from background thread");
        }
    }

    @Override
    public void displayRTFReport(final String rtfSource, final String title) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.displayRTFReport(rtfSource, title);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.displayRTFReport(rtfSource, title);
                }

            });
        }
    }

    @Override
    public void displayRTFReportFromFile(final File rtfFile, final String title) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.displayRTFReportFromFile(rtfFile, title);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.displayRTFReportFromFile(rtfFile, title);
                }
            });
        }
    }

    @Override
    public void displayErrorMessage(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.displayErrorMessage(message);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.displayErrorMessage(message);
                }
            });
        }
    }

    @Override
    public void displayInformationMessage(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.displayInformationMessage(message);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.displayInformationMessage(message);
                }
            });
        }
    }

    @Override
    public void displayBusyMessage(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.displayBusyMessage(message);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.displayBusyMessage(message);
                }
            });
        }
    }

    @Override
    public void displayBusyMessageAllowCancelWorker(final String message, final SwingWorker<?, ?> worker) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.displayBusyMessageAllowCancelWorker(message, worker);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.displayBusyMessageAllowCancelWorker(message, worker);
                }
            });
        }
    }

    @Override
    public void removeBusyMessage() {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.removeBusyMessage();
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.removeBusyMessage();
                }
            });
        }
    }

    @Override
    public void displayTaxonInformation(final List<Item> taxa, final String imagesAutoDisplayText, final String otherItemsAutoDisplayText, final boolean closePromptAfterAutoDisplay) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.displayTaxonInformation(taxa, imagesAutoDisplayText, otherItemsAutoDisplayText, closePromptAfterAutoDisplay);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.displayTaxonInformation(taxa, imagesAutoDisplayText, otherItemsAutoDisplayText, closePromptAfterAutoDisplay);
                }
            });
        }
    }

    @Override
    public void addToolbarButton(final boolean advancedModeOnly, final boolean normalModeOnly, final boolean inactiveUnlessUsedCharacters, final String imageFileName, final List<String> commands,
            final String shortHelp, final String fullHelp) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.addToolbarButton(advancedModeOnly, normalModeOnly, inactiveUnlessUsedCharacters, imageFileName, commands, shortHelp, fullHelp);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.addToolbarButton(advancedModeOnly, normalModeOnly, inactiveUnlessUsedCharacters, imageFileName, commands, shortHelp, fullHelp);
                }
            });
        }
    }

    @Override
    public void addToolbarSpace() {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.addToolbarSpace();
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.addToolbarSpace();
                }
            });
        }
    }

    @Override
    public void clearToolbar() {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.clearToolbar();
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.clearToolbar();
                }
            });
        }
    }

    @Override
    public void illustrateCharacters(final List<Character> characters) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.illustrateCharacters(characters);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.illustrateCharacters(characters);
                }
            });
        }
    }

    @Override
    public void illustrateTaxa(final List<Item> taxa) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.illustrateTaxa(taxa);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.illustrateTaxa(taxa);
                }
            });
        }
    }

    @Override
    public void displayContents(final LinkedHashMap<String, String> contentsMap) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.displayContents(contentsMap);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.displayContents(contentsMap);
                }
            });
        }
    }

    @Override
    public void displayFile(final URL fileURL, final String description) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.displayFile(fileURL, description);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.displayFile(fileURL, description);
                }
            });
        }
    }

    @Override
    public boolean isLogVisible() {
        return _wrappedUI.isLogVisible();
    }

    @Override
    public void setLogVisible(final boolean visible) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.setLogVisible(visible);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.setLogVisible(visible);
                }
            });
        }
    }

    @Override
    public void updateLog() {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.updateLog();
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.updateLog();
                }
            });
        }
    }

    @Override
    public void quitApplication() {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.quitApplication();
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.quitApplication();
                }
            });
        }
    }

    @Override
    public List<Item> getSelectedTaxa() {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedUI.getSelectedTaxa();
        } else {
            return new ArrayList<Item>();
        }
    }

    @Override
    public List<Character> getSelectedCharacters() {
        if (SwingUtilities.isEventDispatchThread()) {
            return _wrappedUI.getSelectedCharacters();
        } else {
            return new ArrayList<Character>();
        }
    }

    @Override
    public void setDemonstrationMode(final boolean demonstrationMode) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.setDemonstrationMode(demonstrationMode);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.setDemonstrationMode(demonstrationMode);
                }
            });
        }
    }

    @Override
    public void displayHelpTopic(final String topicID) {
        if (SwingUtilities.isEventDispatchThread()) {
            _wrappedUI.displayHelpTopic(topicID);
        } else {
            // Marshal the call onto the event dispatch thread
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _wrappedUI.displayHelpTopic(topicID);
                }
            });
        }
    }

    @Override
    public boolean isAdvancedMode() {
        return _wrappedUI.isAdvancedMode();
    }

}
