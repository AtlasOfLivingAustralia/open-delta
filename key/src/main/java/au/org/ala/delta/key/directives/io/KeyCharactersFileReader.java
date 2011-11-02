package au.org.ala.delta.key.directives.io;

import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;

public class KeyCharactersFileReader {

    private DeltaDataSet _dataset;
    private KeyCharactersFile _keyCharsFile;

    public KeyCharactersFileReader(DeltaDataSet dataset, KeyCharactersFile keyCharsFile) {
        _dataset = dataset;
        _keyCharsFile = keyCharsFile;
    }

    public void createCharacters() {
        int numberOfCharacters = _keyCharsFile.getNumberOfCharacters();
        List<Integer> numbersOfStates = _keyCharsFile.readNumbersOfStates();
        List<Integer> characterDetailRecords = _keyCharsFile.readCharacterDetailRecords();

        for (int i = 0; i < numberOfCharacters; i++) {
            int characterNumber = i + 1;
            int numberOfStates = numbersOfStates.get(i);

            int characterDetailRecordNumber = characterDetailRecords.get(i);

            List<String> characterDetails = _keyCharsFile.readCharacterDetails(characterDetailRecordNumber, numberOfStates);

            MultiStateCharacter msChar = (MultiStateCharacter) _dataset.addCharacter(characterNumber, CharacterType.OrderedMultiState);
            msChar.setDescription(characterDetails.get(0));
            msChar.setNumberOfStates(numberOfStates);

            for (int j = 0; j < numberOfStates; j++) {
                int stateNumber = j + 1;
                String stateDescription = characterDetails.get(j + 1);
                msChar.setState(stateNumber, stateDescription);
            }

            System.out.println(msChar);
            System.out.println(Arrays.asList(msChar.getStates()));
        }
    }
}
