package au.org.ala.delta.inkey.model;

import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.intkey.model.IntkeyDataset;

public class MockIntkeyUI implements IntkeyUI {

    @Override
    public void handleNewDataset(IntkeyDataset dataset) {
        // do nothing
    }

    @Override
    public void handleCharacterOrderChanged() {
        // do nothing
    }

    @Override
    public void handleSpecimenUpdated() {
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

}
