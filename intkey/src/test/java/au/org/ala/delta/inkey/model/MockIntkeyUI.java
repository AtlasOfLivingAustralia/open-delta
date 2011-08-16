package au.org.ala.delta.inkey.model;

import java.util.List;

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
    }

    @Override
    public void removeBusyMessage(String message) {
    }

    @Override
    public void addToolbarButton(boolean advancedModeOnly, boolean normalModeOnly, boolean inactiveUnlessUsedCharacters, String imageFileName, List<String> commands, String shortHelp, String fullHelp) {
    }

    @Override
    public void addToolbarSpace() {
    }

    @Override
    public void clearToolbar() {
    }

}
