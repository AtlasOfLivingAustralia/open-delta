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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.ItemsFileHeader;
import au.org.ala.delta.key.KeyContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.util.Utils;

public class KeyItemsFileReader {

    // Attribute data is stored as 32 bit words
    private static int ATTRIBUTE_DATA_LENGTH = 32 / Byte.SIZE;

    public static final int INAPPLICABLE_BIT = 20;

    private KeyContext _context;
    private ItemsFileHeader _header;
    private MutableDeltaDataSet _dataset;
    private BinaryKeyFile _keyItemsFile;

    public KeyItemsFileReader(KeyContext context, MutableDeltaDataSet dataset, BinaryKeyFile keyItemsFile) {
        _context = context;
        _dataset = dataset;
        _keyItemsFile = keyItemsFile;

        _header = new ItemsFileHeader();
        List<Integer> headerInts = _keyItemsFile.readIntegerList(1, ItemsFileHeader.SIZE);
        _header.fromInts(headerInts);
    }

    public void readAll() {
        readHeading();
        readCharacterReliabilities();
        readCharacterDependencies();
        readItems();
        readItemAbundances();
        readIncludedCharacters();
        readIncludedItems();
    }

    public void readHeading() {
        List<Integer> headingLengthInList = _keyItemsFile.readIntegerList(_header.getHeadingRecord(), 1);
        int headingLength = headingLengthInList.get(0);
        String heading = _keyItemsFile.readString(_header.getHeadingRecord() + 1, headingLength);
        _context.setHeading(HeadingType.HEADING, heading);
    }

    public void readItems() {
        List<Integer> itemNameLengths = _keyItemsFile.readIntegerList(_header.getItemNameLengthsRecord(), _header.getNumberOfItems());

        int currentRecord = 2;

        for (int i = 0; i < _header.getNumberOfItems(); i++) {
            _keyItemsFile.seekToRecord(currentRecord);

            Item item = _dataset.addItem();

            int itemNameLength = itemNameLengths.get(i);
            String itemName = _keyItemsFile.readString(currentRecord, itemNameLength);

            item.setDescription(itemName);

            currentRecord += recordsSpannedByBytes(itemNameLength);
            _keyItemsFile.seekToRecord(currentRecord);

            byte[] allAttributesData = _keyItemsFile.read(_header.getNumberOfCharacters() * ATTRIBUTE_DATA_LENGTH);

            processItemAttributes(item, allAttributesData);

            currentRecord += recordsSpannedByBytes(allAttributesData.length);
        }

        _context.setMaximumNumberOfItems(_dataset.getMaximumNumberOfItems());
    }

    public void readItemAbundances() {
        List<Float> itemAbundances = _keyItemsFile.readFloatList(_header.getItemAbundancesRecord(), _header.getNumberOfItems());

        for (int i = 0; i < _header.getNumberOfItems(); i++) {
            int itemNumber = i + 1;
            float abundance = itemAbundances.get(i);

            // Item abundancy may have already been set in the directives file
            if (!_context.itemAbundancySet(itemNumber)) {
                _context.addItemAbundancy(itemNumber, abundance);
            }
        }
    }

    private void processItemAttributes(Item item, byte[] allAttributesData) {

        // First pass, read attribute data from file
        for (int i = 0; i < _header.getNumberOfCharacters(); i++) {
            MultiStateCharacter ch = (MultiStateCharacter) _dataset.getCharacter(i + 1);

            MultiStateAttribute msAttr = (MultiStateAttribute) _dataset.addAttribute(item.getItemNumber(), ch.getCharacterId());

            int arrayOffset = i * ATTRIBUTE_DATA_LENGTH;
            byte[] attributeDataAsBytes = ArrayUtils.subarray(allAttributesData, arrayOffset, arrayOffset + ATTRIBUTE_DATA_LENGTH);

            boolean[] attributeDataAsBooleans = Utils.byteArrayToBooleanArray(attributeDataAsBytes);

            List<String> presentStatesStrings = new ArrayList<String>();
            for (int j = 0; j < ch.getNumberOfStates(); j++) {
                int stateNumber = j + 1;
                if (attributeDataAsBooleans[j]) {
                    presentStatesStrings.add(Integer.toString(stateNumber));
                }
            }

            boolean inapplicable = attributeDataAsBooleans[INAPPLICABLE_BIT];

            if (inapplicable) {
                presentStatesStrings.add("-");
            }

            // TODO bit of a hack here, as DefaultAttributeData can currently
            // only be set using a String.
            // Will be able to fix this up when we switch to using a SlotFile
            // based dataset.
            try {
                msAttr.setValueFromString(StringUtils.join(presentStatesStrings, "/"));
            } catch (DirectiveException e) {
                throw new RuntimeException(e);
            }
        }

        // Second pass, set inapplicable any dependent characters that are made
        // inapplicable by the values of their
        // controlling characters
        // First pass, read attribute data from file
        for (int i = 0; i < _header.getNumberOfCharacters(); i++) {
            MultiStateCharacter ch = (MultiStateCharacter) _dataset.getCharacter(i + 1);

            MultiStateAttribute msAttr = (MultiStateAttribute) _dataset.getAttribute(item.getItemNumber(), ch.getCharacterId());

            if (!msAttr.isInapplicable()) {
                ControllingInfo controllingInfo = _dataset.checkApplicability(ch, item);
                // if (controllingInfo.isStrictlyInapplicable() ||
                // (controllingInfo.isMaybeInapplicable() &&
                // !msAttr.isUnknown())) {
                if (controllingInfo.isInapplicable()) {

                    // Maintain existing state information - any attribute with
                    // state information already set becomes
                    // "maybe inapplicable"
                    List<Integer> presentStates = msAttr.getPresentStatesAsList();
                    List<String> presentStatesStrings = new ArrayList<String>();
                    for (int stateNum : presentStates) {
                        presentStatesStrings.add(Integer.toString(stateNum));
                    }

                    // add inapplicable value
                    presentStatesStrings.add("-");

                    // TODO bit of a hack here, as DefaultAttributeData can
                    // currently
                    // only be set using a String.
                    // Will be able to fix this up when we switch to using a
                    // SlotFile
                    // based dataset.
                    try {
                        msAttr.setValueFromString(StringUtils.join(presentStatesStrings, "/"));
                    } catch (DirectiveException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        // Third pass, unknown values should be variable unless the
        // TREAT UNKNOWN AS INAPPLICABLE directive has been used
        if (!_context.getTreatUnknownAsInapplicable()) {
            for (int i = 0; i < _header.getNumberOfCharacters(); i++) {
                MultiStateCharacter ch = (MultiStateCharacter) _dataset.getCharacter(i + 1);

                MultiStateAttribute msAttr = (MultiStateAttribute) _dataset.getAttribute(item.getItemNumber(), ch.getCharacterId());

                if (msAttr.getPresentStates().isEmpty() && !msAttr.isInapplicable()) {
                    // Set attribute as variable - all states are present
                    List<String> presentStatesStrings = new ArrayList<String>();
                    for (int j = 0; j < ch.getNumberOfStates(); j++) {
                        int stateNumber = j + 1;
                        presentStatesStrings.add(Integer.toString(stateNumber));
                    }

                    // TODO bit of a hack here, as DefaultAttributeData can
                    // currently
                    // only be set using a String.
                    // Will be able to fix this up when we switch to using a
                    // SlotFile
                    // based dataset.
                    try {
                        msAttr.setValueFromString(StringUtils.join(presentStatesStrings, "/"));
                    } catch (DirectiveException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void readCharacterDependencies() {
        if (_header.getCharacterDependenciesLength() > _header.getNumberOfCharacters()) {
            List<Integer> characterDependencyData = _keyItemsFile.readIntegerList(_header.getCharacterDependencyRecord(), _header.getCharacterDependenciesLength());

            List<Integer> dependencyInfoPointers = characterDependencyData.subList(0, _header.getNumberOfCharacters());

            // At the start of the dependency data there is an integer value for
            // each character.
            // If non zero, the value is an offset further down the list where
            // its dependency data is.
            // Otherwise the character does not have any dependent characters.
            for (int i = 0; i < _header.getNumberOfCharacters(); i++) {
                int charDepIndex = dependencyInfoPointers.get(i);
                if (charDepIndex > 0) {

                    MultiStateCharacter controllingChar = (MultiStateCharacter) _dataset.getCharacter(i + 1);

                    int numStates = controllingChar.getStates().length;

                    // The dependency data for each character consists of one
                    // integer for each of the character's states. If the
                    // integer
                    // value listed for a state is non-zero, the value is an
                    // offset pointing to further down the list where
                    // the state's dependency data is.
                    int stateDepIndiciesStart = charDepIndex - 1;
                    int stateDepIndiciesEnd = charDepIndex - 1 + numStates;
                    List<Integer> stateDepRecordIndicies = characterDependencyData.subList(stateDepIndiciesStart, stateDepIndiciesEnd);

                    // We need to coalesce the dependency data so that we have
                    // one CharacterDependency object per
                    // controlling character and set of states that make a set
                    // of dependent characters inapplicable.
                    // Use this map to keep track of the state ids that make the
                    // same set of dependent characters
                    // inapplicable.
                    Map<Set<Integer>, Set<Integer>> depCharsToStateIds = new HashMap<Set<Integer>, Set<Integer>>();

                    for (int j = 0; j < numStates; j++) {
                        Integer stateId = j + 1;

                        int stateDepRecordIndex = stateDepRecordIndicies.get(j);

                        if (stateDepRecordIndex > 0) {
                            // First value listed in the state's dependency data
                            // is the number of character ranges dependent on
                            // that state.
                            int numDependentCharRanges = characterDependencyData.get(stateDepRecordIndex - 1);

                            // Immediately after the range information is listed
                            // - the upper and lower bound is listed for each
                            // range.
                            List<Integer> rangeNumbers = characterDependencyData.subList(stateDepRecordIndex, stateDepRecordIndex + (numDependentCharRanges * 2));

                            Set<Integer> dependentChars = new HashSet<Integer>();

                            for (int k = 0; k < numDependentCharRanges * 2; k = k + 2) {
                                int lowerBound = rangeNumbers.get(k);
                                int upperBound = rangeNumbers.get(k + 1);

                                IntRange r = new IntRange(lowerBound, upperBound);

                                for (int dependentChar : r.toArray()) {
                                    dependentChars.add(dependentChar);
                                }
                            }

                            if (depCharsToStateIds.containsKey(dependentChars)) {
                                Set<Integer> stateSet = depCharsToStateIds.get(dependentChars);
                                stateSet.add(stateId);
                            } else {
                                Set<Integer> stateSet = new HashSet<Integer>();
                                stateSet.add(stateId);
                                depCharsToStateIds.put(dependentChars, stateSet);
                            }
                        }
                    }

                    // Now that we have coalesced the dependency data into the
                    // form we need, we can
                    // create the CharacterDependency objects.
                    for (Set<Integer> depCharsSet : depCharsToStateIds.keySet()) {
                        Set<Integer> stateSet = depCharsToStateIds.get(depCharsSet);
                        CharacterDependency charDep = _dataset.addCharacterDependency(controllingChar, stateSet, depCharsSet);
                    }
                }
            }
        }
    }

    public void readCharacterReliabilities() {
        List<Float> reliabilities = _keyItemsFile.readFloatList(_header.getCharcterReliabilitiesRecord(), _header.getNumberOfCharacters());
        int numberOfCharacters = _header.getNumberOfCharacters();

        for (int i = 0; i < numberOfCharacters; i++) {
            Character ch = _dataset.getCharacter(i + 1);
            ch.setReliability(reliabilities.get(i));
        }
    }

    public void readIncludedCharacters() {
        List<Integer> characterMask = _keyItemsFile.readIntegerList(_header.getCharacterMaskRecord(), _header.getNumberOfCharacters());

        List<Integer> includedCharacterNumbers = new ArrayList<Integer>();
        for (int i = 0; i < _header.getNumberOfCharacters(); i++) {
            int characterNumber = i + 1;
            if (characterMask.get(i) == 0) {
                _context.excludeCharacter(characterNumber);
            }
        }
    }

    public void readIncludedItems() {
        List<Integer> taxonMask = _keyItemsFile.readIntegerList(_header.getTaxonMaskRecord(), _header.getNumberOfItems());

        List<Integer> includedItemNumbers = new ArrayList<Integer>();
        for (int i = 0; i < _header.getNumberOfItems(); i++) {
            int itemNumber = i + 1;
            if (taxonMask.get(i) == 0) {
                _context.excludeItem(itemNumber);
            }
        }
    }

    private int recordsSpannedByBytes(int numBytes) {
        return (int) (Math.ceil((double) numBytes / (double) BinaryKeyFile.RECORD_LENGTH_BYTES));
    }

}
