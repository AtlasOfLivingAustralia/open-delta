package au.org.ala.delta.key.directives.io;

import java.util.List;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;

public class KeyItemsFileReader {
    private DeltaDataSet _dataset;
    private KeyItemsFile _keyItemsFile;

    public KeyItemsFileReader(DeltaDataSet dataset, KeyItemsFile keyItemsFile) {
        _dataset = dataset;
        _keyItemsFile = keyItemsFile;
    }

    public void readItems() {
        
    }

    public void readCharacterDependencies() {

    }

    public void readCharacterReliabilities() {
        List<Float> reliabilities = _keyItemsFile.readCharacterReliabilities();
        int numberOfCharacters = _keyItemsFile.getNumberOfCharacters();

        for (int i = 0; i < numberOfCharacters; i++) {
            Character ch = _dataset.getCharacter(i + 1);
            ch.setReliability(reliabilities.get(i));
        }
    }

    public void readExcludedCharacters() {

    }

    public void readExcludedItems() {

    }

}
