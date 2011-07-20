package au.org.ala.delta.intkey;

import au.org.ala.delta.intkey.model.IntkeyDataset;

public interface IntkeyUI {

    void handleNewDataset(IntkeyDataset dataset);
    
    void handleCharacterOrderChanged();
    
    void handleSpecimenUpdated();
    
    void handleIdentificationRestarted();
    
    void displayRTFReport(String rtfSource, String title);
    
    void displayErrorMessage(String message);
    
    void displayWarningMessage(String message);
    
    void displayBusyMessage(String message);
    
    void removeBusyMessage(String message);
}
