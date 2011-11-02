package au.org.ala.delta.key.directives.io;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.CharactersFileHeader;

public class KeyCharactersFile extends BinaryKeyFile {

    private CharactersFileHeader _header;

    public KeyCharactersFile(String fileName) {
        super(fileName, BinFileMode.FM_READONLY);
        readHeader();
    }

    private void readHeader() {
        _header = new CharactersFileHeader();
        List<Integer> headerInts = readIntegerList(1, CharactersFileHeader.SIZE);
        _header.fromInts(headerInts);
    }

    public int getNumberOfCharacters() {
        return _header.getNumberOfCharacters();
    }

    public List<Integer> readNumbersOfStates() {
        return readIntegerList(_header.getKeyStatesRecord(), _header.getNumberOfCharacters());
    }

    public List<Integer> readCharacterDetailRecords() {
        List<Integer> characterDetailRecords = readIntegerList(_header.getCharacterDetailsRecord(), _header.getNumberOfCharacters());
        return characterDetailRecords;
    }

    public List<String> readCharacterDetails(int recordNumber, int numStates) {
        List<String> detailsList = new ArrayList<String>();

        // Record contains the length of the character description, plus the
        // length of the description for each character state.
        List<Integer> characterDetailLengths = readIntegerList(recordNumber, numStates + 1);

        int totalNumberOfCharacters = 0;
        for (int detailStringLength : characterDetailLengths) {
            totalNumberOfCharacters += detailStringLength;
        }

        String characterDetailsAsContiguousString = readString(recordNumber + 1, totalNumberOfCharacters);

        int offset = 0;
        for (int detailStringLength : characterDetailLengths) {
            String detail = characterDetailsAsContiguousString.substring(offset, offset + detailStringLength);
            detailsList.add(detail);
            offset = offset + detailStringLength;
        }

        return detailsList;
    }
}
