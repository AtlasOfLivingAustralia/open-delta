package au.org.ala.delta.intkey;

import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyDataset;

public interface IntkeyUI {

    void handleNewDataset(IntkeyDataset dataset);
    
    void handleDatasetClosed();
    
    void handleUpdateAll();
    
    void handleIdentificationRestarted();
    
    void displayRTFReport(String rtfSource, String title);
    
    void displayErrorMessage(String message);
    
    void displayWarningMessage(String message);
    
    void displayBusyMessage(String message);
    
    void removeBusyMessage();
    
    void addToolbarButton(boolean advancedModeOnly, boolean normalModeOnly, boolean inactiveUnlessUsedCharacters, String imageFileName, List<String> commands, String shortHelp, String fullHelp);
    
    void addToolbarSpace();
    
    void clearToolbar();
}
