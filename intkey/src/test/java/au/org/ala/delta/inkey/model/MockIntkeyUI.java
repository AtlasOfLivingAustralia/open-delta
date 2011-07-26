package au.org.ala.delta.inkey.model;

import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.intkey.model.IntkeyDataset;

public class MockIntkeyUI implements IntkeyUI {

    @Override
    public void handleNewDataset(IntkeyDataset dataset) {
        // do nothing
    }

    @Override
    public void handleUpdateAll() {
        // do nothing
    }

    @Override
    public void handleIdentificationRestarted() {
        // do nothing
    }

    @Override
    public void displayRTFReport(String rtfSource, String title) {
        // do nothing
    }

    @Override
    public void displayErrorMessage(String message) {
        // do nothing
    }

    @Override
    public void displayWarningMessage(String message) {
        // do nothing
    }

    @Override
    public void displayBusyMessage(String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeBusyMessage(String message) {
        // TODO Auto-generated method stub
        
    }

}
