package au.org.ala.delta.intkey;

import au.org.ala.delta.intkey.model.IntkeyDataset;

public interface IntkeyUI {

    void handleNewDataset(IntkeyDataset dataset);
    
    void handleCharacterOrderChanged();
    
    void handleSpecimenUpdated();
    
    void handleIdentificationRestarted();

}
