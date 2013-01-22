/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.key.directives.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.CharactersFileHeader;
import au.org.ala.delta.key.KeyContext;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;

public class KeyCharactersFileReader {

    private CharactersFileHeader _header;
    
    private MutableDeltaDataSet _dataset;
    private BinaryKeyFile _keyCharsFile;
    
    private KeyContext _context;

    public KeyCharactersFileReader(KeyContext context, MutableDeltaDataSet dataset, BinaryKeyFile keyCharsFile) {
        _context = context;
        _dataset = dataset;
        _keyCharsFile = keyCharsFile;
        
        _header = new CharactersFileHeader();
        List<Integer> headerInts = _keyCharsFile.readIntegerList(1, CharactersFileHeader.SIZE);
        _header.fromInts(headerInts);
    }

    public void createCharacters() {
        int numberOfCharacters = _header.getNumberOfCharacters();
        List<Integer> numbersOfStates = _keyCharsFile.readIntegerList(_header.getKeyStatesRecord(), _header.getNumberOfCharacters());
        List<Integer> characterDetailRecords = _keyCharsFile.readIntegerList(_header.getCharacterDetailsRecord(), _header.getNumberOfCharacters());

        for (int i = 0; i < numberOfCharacters; i++) {
            int characterNumber = i + 1;
            int numberOfStates = numbersOfStates.get(i);

            int characterDetailRecordNumber = characterDetailRecords.get(i);

            List<String> characterDetails = readCharacterDetails(characterDetailRecordNumber, numberOfStates);

            MultiStateCharacter msChar = (MultiStateCharacter) _dataset.addCharacter(characterNumber, CharacterType.OrderedMultiState);
            msChar.setDescription(characterDetails.get(0));
            msChar.setNumberOfStates(numberOfStates);

            for (int j = 0; j < numberOfStates; j++) {
                int stateNumber = j + 1;
                String stateDescription = characterDetails.get(j + 1);
                msChar.setState(stateNumber, stateDescription);
            }
        }
        
        _context.setNumberOfCharacters(_dataset.getNumberOfCharacters());
    }
    
    private List<String> readCharacterDetails(int recordNumber, int numStates) {
        List<String> detailsList = new ArrayList<String>();

        // Record contains the length of the character description, plus the
        // length of the description for each character state.
        List<Integer> characterDetailLengths = _keyCharsFile.readIntegerList(recordNumber, numStates + 1);

        int totalNumberOfCharacters = 0;
        for (int detailStringLength : characterDetailLengths) {
            totalNumberOfCharacters += detailStringLength;
        }

        int recordsSpannedByCharacterDetailLengths = (int) Math.ceil((double) characterDetailLengths.size() / BinaryKeyFile.RECORD_LENGTH_INTEGERS);
        
        String characterDetailsAsContiguousString = _keyCharsFile.readString(recordNumber + recordsSpannedByCharacterDetailLengths, totalNumberOfCharacters);

        int offset = 0;
        for (int detailStringLength : characterDetailLengths) {
            String detail = characterDetailsAsContiguousString.substring(offset, offset + detailStringLength);
            detailsList.add(detail);
            offset = offset + detailStringLength;
        }

        return detailsList;
    }
}
