package au.org.ala.delta.intkey.model;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.model.ported.Constants;
import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileEncoding;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

public class IntkeyDatasetFileBuilder {

    private static final int sizeIntInBytes = Integer.SIZE / Byte.SIZE;
    private static final int sizeFloatInBytes = Float.SIZE / Byte.SIZE;

    private static final int datasetMajorVersion = 5;
    private static final int datasetMinorVersion = 2;

    private IntkeyDataset _ds;
    private BinFile _charBinFile;
    private BinFile _itemBinFile;
    private CharactersFileHeader _charFileHeader;
    private ItemsFileHeader _itemFileHeader;
    private List<au.org.ala.delta.model.Character> _characters;
    private List<Item> _taxa;

    public IntkeyDataset readDataSet(File charactersFile, File itemsFile) {

        // should modify BinFile so that you can pass in a File.
        _charBinFile = new BinFile(charactersFile.getAbsolutePath(), BinFileMode.FM_READONLY);
        _itemBinFile = new BinFile(itemsFile.getAbsolutePath(), BinFileMode.FM_READONLY);

        _ds = new IntkeyDataset();
        _charFileHeader = new CharactersFileHeader();
        _itemFileHeader = new ItemsFileHeader();
        _characters = new ArrayList<au.org.ala.delta.model.Character>();
        _taxa = new ArrayList<Item>();

        readCharactersFileHeader();
        readItemsFileHeader();

        // Check number of characters is same in two files
        if (_charFileHeader.getNC() != _itemFileHeader.getNChar()) {
            throw new RuntimeException("Characters and taxa files do not match");
        }

        // Check stated record length in items file is correct
        if (_itemFileHeader.getLRec() != Constants.RECORD_LENGTH_INTEGERS) {
            throw new RuntimeException("Record length incorrect");
        }

        // Check file is correct version

        // Not sure why rpOmitOr is being checked here. Original syntax was
        // "fparam[last_used+1] != 0", where
        // last_used was set to 26. fparam was the array holding all of the
        // integers in the record. CPF 4/4/2011.
        if (_itemFileHeader.getMajorVer() != datasetMajorVersion || (_itemFileHeader.getMinorVer() != datasetMinorVersion && _itemFileHeader.getRpOmitOr() != 0)) {
            throw new RuntimeException("Incorrect file version");
        }

        readHeadingsAndValidationString();

        readTaxonData();
        readCharacters();

        _ds.setCharactersFile(charactersFile);
        _ds.setItemsFile(itemsFile);
        _ds.setCharactersFileHeader(_charFileHeader);
        _ds.setItemsFileHeader(_itemFileHeader);
        _ds.setCharacters(_characters);
        _ds.setTaxa(_taxa);

        _charBinFile.close();
        _itemBinFile.close();

        return _ds;
    }

    private void readCharactersFileHeader() {
        // read first record which contains header file information;

        ByteBuffer headerBytes = readRecord(_charBinFile, 1);

        // read first record of characters file
        _charFileHeader.setNC(headerBytes.getInt()); // 0

        headerBytes.getInt(); // 1 - maxDes - not used.

        _charFileHeader.setRpCdes(headerBytes.getInt()); // 2
        _charFileHeader.setRpStat(headerBytes.getInt()); // 3
        _charFileHeader.setRpChlp(headerBytes.getInt()); // 4
        _charFileHeader.setRpChlpGrp(headerBytes.getInt()); // 5
        _charFileHeader.setRpChlpFmt1(headerBytes.getInt()); // 6
        _charFileHeader.setRpChlpFmt2(headerBytes.getInt()); // 7
        _charFileHeader.setRpCImagesC(headerBytes.getInt()); // 8
        _charFileHeader.setRpStartupImages(headerBytes.getInt()); // 9
        _charFileHeader.setRpCKeyImages(headerBytes.getInt()); // 10
        _charFileHeader.setRpTKeyImages(headerBytes.getInt()); // 11
        _charFileHeader.setRpHeading(headerBytes.getInt()); // 12
        _charFileHeader.setRpRegSubHeading(headerBytes.getInt()); // record
                                                                  // pointer to
                                                                  // registration
                                                                  // subheading
                                                                  // (13)
        _charFileHeader.setRpValidationString(headerBytes.getInt()); // record
                                                                     // pointer
                                                                     // to
                                                                     // validation
                                                                     // string
                                                                     // for
                                                                     // registered
                                                                     // dataset
                                                                     // (14)

        headerBytes.getInt(); // 15 - record number for character mask - not
                              // used.

        _charFileHeader.setRpOrWord(headerBytes.getInt()); // 16
        _charFileHeader.setRpCheckForCd(headerBytes.getInt()); // 17
        _charFileHeader.setRpFont(headerBytes.getInt()); // 18
        _charFileHeader.setRpItemSubHead(headerBytes.getInt()); // 19

        headerBytes.position(Constants.RECORD_LENGTH_INTEGERS - 1);

        _charFileHeader.setCptr(headerBytes.getInt());
    }

    private void readItemsFileHeader() {

        ByteBuffer headerBytes = readRecord(_itemBinFile, 1);

        _itemFileHeader.setNItem(headerBytes.getInt()); // number of items (0)
        _itemFileHeader.setNChar(headerBytes.getInt()); // number of characters
                                                        // (1)
        _itemFileHeader.setMs(headerBytes.getInt()); // maximum number of states
                                                     // (2)

        headerBytes.getInt(); // 3 - MaxDat - not used
        _itemFileHeader.setLRec(headerBytes.getInt()); // 4 - record length used
                                                       // in items file

        _itemFileHeader.setRpTnam(headerBytes.getInt()); // record pointer to
                                                         // taxon names (5)
        _itemFileHeader.setRpSpec(headerBytes.getInt()); // record pointer to
                                                         // specifications (6)
        _itemFileHeader.setRpMini(headerBytes.getInt()); // record pointer to
                                                         // minima of integer
                                                         // characters (7)
        _itemFileHeader.setLDep(headerBytes.getInt()); // length of dependency
                                                       // array (8)
        _itemFileHeader.setRpCdep(headerBytes.getInt()); // record pointer to
                                                         // character dependency
                                                         // array (9)
        _itemFileHeader.setLinvdep(headerBytes.getInt()); // length of inverted
                                                          // dependency array
                                                          // (10)
        _itemFileHeader.setRpInvdep(headerBytes.getInt()); // record pointer to
                                                           // inverted
                                                           // dependency array
                                                           // (11)
        _itemFileHeader.setRpCdat(headerBytes.getInt()); // record pointer to
                                                         // data for each
                                                         // character (12)
        _itemFileHeader.setLSbnd(headerBytes.getInt()); // length of state
                                                        // bounds array (13)
        _itemFileHeader.setLkstat(Math.max(1, headerBytes.getInt())); // length
                                                                      // of key
                                                                      // states
                                                                      // array
                                                                      // (14)

        _itemFileHeader.setMajorVer(headerBytes.getInt()); // 15

        _itemFileHeader.setRpNkbd(headerBytes.getInt()); // record pointer to
                                                         // key state bounds
                                                         // array (16)
        _itemFileHeader.setMaxInt(headerBytes.getInt()); // maximum integer
                                                         // value (17)

        headerBytes.getInt(); // 18 - Maxtxt1 - not used
        headerBytes.getInt(); // 19 - Maxtxt2 - not used
        _itemFileHeader.setMinorVer(headerBytes.getInt()); // 20

        _itemFileHeader.setTaxonImageChar(headerBytes.getInt()); // character
                                                                 // specifying
                                                                 // taxon images
                                                                 // (21)
        _itemFileHeader.setRpCimagesI(headerBytes.getInt()); // pointer to
                                                             // character images
                                                             // (22)
        _itemFileHeader.setRpTimages(headerBytes.getInt()); // pointer to taxon
                                                            // images (23)
        _itemFileHeader.setEnableDeltaOutput(headerBytes.getInt()); // whether
                                                                    // to allow
                                                                    // DELTA
                                                                    // output
                                                                    // via
                                                                    // OUTPUT
                                                                    // SUMMARY
                                                                    // command
                                                                    // (24)
        _itemFileHeader.setChineseFmt(headerBytes.getInt()); // whether chinese
                                                             // character set
                                                             // (25)
        _itemFileHeader.setRpCsynon(headerBytes.getInt()); // record pointer to
                                                           // characters for
                                                           // synonomy (26)
        _itemFileHeader.setRpOmitOr(headerBytes.getInt()); // record pointer to
                                                           // "omit or" list of
                                                           // characters (27)
        _itemFileHeader.setRpNext(headerBytes.getInt()); // pointer to second
                                                         // parameter record
                                                         // (28)

        _itemFileHeader.setDupItemPtr(headerBytes.getInt()); // pointer to
                                                             // duplicated item
                                                             // name mask (29:
                                                             // Constants.LREC -
                                                             // 3)
        _itemFileHeader.setTptr(headerBytes.getInt()); // pointer to b-tree and
                                                       // image masks appended
                                                       // to items file (30:
                                                       // Constants.LREC - 2)
        _itemFileHeader.setLbtree(headerBytes.getInt()); // length of btree in
                                                         // bytes (31:
                                                         // Constants.LREC - 1)

        if (_itemFileHeader.getRpNext() > 0) {
            ByteBuffer secondHeaderBytes = readRecord(_itemBinFile, _itemFileHeader.getRpNext());

            _itemFileHeader.setRpUseCc(secondHeaderBytes.getInt());
            int rpTlinks1 = secondHeaderBytes.getInt();
            _itemFileHeader.setRpOmitPeriod(secondHeaderBytes.getInt());
            _itemFileHeader.setRpNewPara(secondHeaderBytes.getInt());
            _itemFileHeader.setRpNonAutoCc(secondHeaderBytes.getInt());
            int rpTlinks2 = secondHeaderBytes.getInt();

            _itemFileHeader.setRpTlinks(new int[] { rpTlinks1, rpTlinks2 });

        } else {
            _itemFileHeader.setRpUseCc(0);
            _itemFileHeader.setRpTlinks(new int[] { 0, 0 });
            _itemFileHeader.setRpOmitPeriod(0);
            _itemFileHeader.setRpNewPara(0);
            _itemFileHeader.setRpNonAutoCc(0);
        }
    }

    private void readHeadingsAndValidationString() {
        // read and display data heading
        BinFile hFile;
        int recno;
        if (_charFileHeader.getRpHeading() > 0) // heading is in chars file
        {
            hFile = _charBinFile;
            recno = _charFileHeader.getRpHeading();
        } else // heading is in items file
        {
            hFile = _itemBinFile;
            recno = 2;
        }

        seekToRecord(hFile, recno);
        int headingLength = hFile.readInt();
        seekToRecord(hFile, recno + 1);

        String heading = readString(hFile, headingLength);
        _ds.setHeading(heading);
        // output to log window
        // set as heading of main window

        if (_charFileHeader.getRpRegSubHeading() > 0) {
            // read and display registered dataset subheading
            seekToRecord(hFile, _charFileHeader.getRpRegSubHeading());
            int subheadingLength = hFile.readInt();
            seekToRecord(hFile, _charFileHeader.getRpRegSubHeading() + 1);
            String regSubHeading = readString(hFile, subheadingLength);
            _ds.setSubHeading(regSubHeading);
        }

        if (_charFileHeader.getRpValidationString() > 0) {
            // read validation string
            seekToRecord(hFile, _charFileHeader.getRpValidationString());
            int validationStringLength = hFile.readInt();
            seekToRecord(hFile, _charFileHeader.getRpValidationString() + 1);
            String validationString = readString(hFile, validationStringLength);
            _ds.setValidationString(validationString);
        }
    }

    private void readCharacters() {

        int numChars = _charFileHeader.getNC();
        DefaultDataSetFactory dsFactory = new DefaultDataSetFactory();

        // READ NUMBER OF CHARACTER STATES
        seekToRecord(_charBinFile, _charFileHeader.getRpStat());
        List<Integer> numCharacterStates = new ArrayList<Integer>();
        ByteBuffer numStatesData = _charBinFile.readByteBuffer(numChars * sizeIntInBytes);
        for (int i = 0; i < numChars; i++) {
            numCharacterStates.add(numStatesData.getInt());
        }

        // READ CHARACTER TYPES
        seekToRecord(_itemBinFile, _itemFileHeader.getRpSpec());
        ByteBuffer charTypeData = _itemBinFile.readByteBuffer(numChars * sizeIntInBytes);

        for (int i = 0; i < numChars; i++) {
            // Type for corresponding character is indicated by the absolute
            // value of the supplied integer value
            int charType = Math.abs(charTypeData.getInt());
            au.org.ala.delta.model.Character newChar = null;

            switch (charType) {
            case 1:
                newChar = dsFactory.createCharacter(CharacterType.UnorderedMultiState, i + 1);
                break;
            case 2:
                newChar = dsFactory.createCharacter(CharacterType.OrderedMultiState, i + 1);
                break;
            case 3:
                newChar = dsFactory.createCharacter(CharacterType.IntegerNumeric, i + 1);
                break;
            case 4:
                newChar = dsFactory.createCharacter(CharacterType.RealNumeric, i + 1);
                break;
            case 5:
                newChar = dsFactory.createCharacter(CharacterType.Text, i + 1);
                break;
            default:
                throw new RuntimeException("Unrecognized character type");
            }

            _characters.add(newChar);
        }

        int recordsSpannedByCharTypes = recordsSpannedByBytes(numChars * sizeIntInBytes);

        seekToRecord(_itemBinFile, _itemFileHeader.getRpSpec() + recordsSpannedByCharTypes);
        List<Integer> itemsFileNumCharacterStates = new ArrayList<Integer>();
        ByteBuffer numCharacterStatesData = _itemBinFile.readByteBuffer(numChars * sizeIntInBytes);
        for (int i = 0; i < numChars; i++) {
            int numStates = numCharacterStatesData.getInt();
            itemsFileNumCharacterStates.add(numStates);
        }

        if (!itemsFileNumCharacterStates.equals(numCharacterStates)) {
            throw new RuntimeException("Numbers of states for characters differ between characters file and items file");
        }

        int recordsSpannedByNumCharStates = recordsSpannedByBytes(numChars * sizeIntInBytes);

        // READ CHARACTER RELIABILITIES
        seekToRecord(_itemBinFile, _itemFileHeader.getRpSpec() + (recordsSpannedByCharTypes + recordsSpannedByNumCharStates));

        ByteBuffer charReliabilityData = _itemBinFile.readByteBuffer(numChars * sizeFloatInBytes);
        for (au.org.ala.delta.model.Character ch : _characters) {
            Float reliability = charReliabilityData.getFloat();
            ch.setReliability(Float.valueOf(reliability).doubleValue());
        }

        readCharacterDescriptionsAndStates(numCharacterStates);
        readCharacterMinimumsAndMaximums();
        readCharacterDependencies();
        readCharacterTaxonData();

    }

    private void readCharacterDescriptionsAndStates(List<Integer> numCharacterStates) {
        int numChars = _charFileHeader.getNC();

        // READ CHARACTER DESCRIPTIONS
        seekToRecord(_charBinFile, _charFileHeader.getRpCdes());

        List<Integer> charDescriptionRecordIndicies = new ArrayList<Integer>();
        ByteBuffer recordIndiciesData = _charBinFile.readByteBuffer(numChars * sizeIntInBytes);

        for (int i = 0; i < numChars; i++) {
            charDescriptionRecordIndicies.add(recordIndiciesData.getInt());
        }

        for (int i = 0; i < numChars; i++) {

            au.org.ala.delta.model.Character ch = _characters.get(i);

            int descRecordIndex = charDescriptionRecordIndicies.get(i);

            seekToRecord(_charBinFile, descRecordIndex);

            int numStatesForChar = numCharacterStates.get(i);
            ByteBuffer charDescriptionsTextData = _charBinFile.readByteBuffer((numStatesForChar + 1) * sizeIntInBytes);

            List<Integer> charDescriptionsLengths = new ArrayList<Integer>();

            int lengthTotal = 0;

            for (int j = 0; j < numStatesForChar + 1; j++) {
                int descLength = charDescriptionsTextData.getInt();
                charDescriptionsLengths.add(descLength);
                lengthTotal += descLength;
            }

            int recordsSpannedByDescLengths = Double.valueOf(Math.ceil(Integer.valueOf(numStatesForChar + 1).doubleValue() / Integer.valueOf(Constants.RECORD_LENGTH_INTEGERS).doubleValue()))
                    .intValue();

            List<String> charStateDescriptions = new ArrayList<String>();

            seekToRecord(_charBinFile, descRecordIndex + recordsSpannedByDescLengths);
            ByteBuffer descBuffer = _charBinFile.readByteBuffer(lengthTotal);

            for (int k = 0; k < charDescriptionsLengths.size(); k++) {
                int len = charDescriptionsLengths.get(k);
                byte[] descArray = new byte[len];
                descBuffer.get(descArray);

                String descriptionText = BinFileEncoding.decode(descArray);

                if (k == 0) {
                    // First description listed is the character description
                    ch.setDescription(descriptionText);
                } else {
                    charStateDescriptions.add(descriptionText);
                }
            }

            if (ch instanceof IntegerCharacter) {
                if (charStateDescriptions.size() == 1) {
                    ((IntegerCharacter) ch).setUnits(charStateDescriptions.get(0));
                } else if (charStateDescriptions.size() > 1) {
                    throw new RuntimeException("Integer characters should only have one state listed which represents the units description.");
                }
            } else if (ch instanceof RealCharacter) {
                if (charStateDescriptions.size() == 1) {
                    ((RealCharacter) ch).setUnits(charStateDescriptions.get(0));
                } else if (charStateDescriptions.size() > 1) {
                    throw new RuntimeException("Real numeric characters should only have one state listed which represents the units description.");
                }
            } else if (ch instanceof MultiStateCharacter) {
                MultiStateCharacter multiStateChar = (MultiStateCharacter) ch;

                multiStateChar.setNumberOfStates(charStateDescriptions.size());

                for (int l = 0; l < charStateDescriptions.size(); l++) {
                    multiStateChar.setState(l + 1, charStateDescriptions.get(l));
                }
            } else {
                if (charStateDescriptions.size() > 0) {
                    throw new RuntimeException("Text characters should not have a state specified");
                }
            }
        }
    }

    private void readCharacterMinimumsAndMaximums() {
        int numChars = _itemFileHeader.getNChar();

        seekToRecord(_itemBinFile, _itemFileHeader.getRpMini());

        List<Integer> minimumValues = readIntegerList(_itemBinFile, numChars);

        int recordsSpannedByMinimumValues = recordsSpannedByBytes(numChars * sizeIntInBytes);

        seekToRecord(_itemBinFile, _itemFileHeader.getRpMini() + recordsSpannedByMinimumValues);

        List<Integer> maximumValues = readIntegerList(_itemBinFile, numChars);

        for (int i = 0; i < numChars; i++) {
            Character c = _characters.get(i);

            if (c instanceof IntegerCharacter) {
                IntegerCharacter intChar = (IntegerCharacter) c;

                int minValue = minimumValues.get(i);
                int maxValue = maximumValues.get(i);

                intChar.setMinimumValue(minValue);
                intChar.setMaximumValue(maxValue);
            }
        }
    }

    private void readCharacterDependencies() {
        int numChars = _itemFileHeader.getNChar();

        // If LDep is 0, there are no dependencies. Otherwise dependency data
        // consists of LDep integers, starting at record
        // rpCdep.
        if (_itemFileHeader.getLDep() >= numChars) {
            seekToRecord(_itemBinFile, _itemFileHeader.getRpCdep());
            List<Integer> dependencyData = readIntegerList(_itemBinFile, _itemFileHeader.getLDep());

            // At the start of the dependency data there is an integer value for
            // each character.
            // If non zero, the value is an offset further down the list where
            // its dependency data is.
            // Otherwise the character does not have any dependent characters.
            for (int i = 0; i < numChars; i++) {
                int charDepIndex = dependencyData.get(i);
                if (charDepIndex > 0) {
                    au.org.ala.delta.model.Character c = _characters.get(i);
                    if (!(c instanceof MultiStateCharacter)) {
                        throw new RuntimeException("Only multistate characters can be controlling characters");
                    }

                    MultiStateCharacter controllingChar = (MultiStateCharacter) c;

                    int numStates = controllingChar.getStates().length;

                    // The dependency data for each character consists of one
                    // integer for each of the character's states. If the
                    // integer
                    // value listed for a state is non-zero, the value is an
                    // offset pointing to further down the list where
                    // the state's dependency data is.
                    int stateDepIndiciesStart = charDepIndex - 1;
                    int stateDepIndiciesEnd = charDepIndex - 1 + numStates;
                    List<Integer> stateDepRecordIndicies = dependencyData.subList(stateDepIndiciesStart, stateDepIndiciesEnd);

                    for (int j = 0; j < numStates; j++) {
                        Integer stateId = j + 1;

                        int stateDepRecordIndex = stateDepRecordIndicies.get(j);

                        if (stateDepRecordIndex > 0) {
                            // First value listed in the state's dependency data
                            // is the number of character ranges dependent on
                            // that state.
                            int numDependentCharRanges = dependencyData.get(stateDepRecordIndex - 1);

                            // Immediately after the range information is listed
                            // - the upper and lower bound is listed for each
                            // range.
                            List<Integer> rangeNumbers = dependencyData.subList(stateDepRecordIndex, stateDepRecordIndex + (numDependentCharRanges * 2));

                            Set<Integer> dependentChars = new HashSet<Integer>();

                            for (int k = 0; k < numDependentCharRanges; k = k + 2) {
                                int lowerBound = rangeNumbers.get(k);
                                int upperBound = rangeNumbers.get(k + 1);

                                IntRange r = new IntRange(lowerBound, upperBound);

                                for (int dependentChar : r.toArray()) {
                                    dependentChars.add(dependentChar);
                                }

                                // System.out.println(String.format("Character: %s State: %d Range lower bound: %d, Range upper bound: %d",
                                // c.getDescription(), stateId, lowerBound,
                                // upperBound));
                            }

                            Set<Integer> stateSet = new HashSet<Integer>();
                            stateSet.add(stateId);
                            CharacterDependency charDep = new CharacterDependency(c.getCharacterId(), stateSet, dependentChars);
                            c.addDependentCharacters(charDep);
                        }
                    }
                }
            }
        }
    }

    private void readCharacterTaxonData() {
        int numChars = _itemFileHeader.getNChar();
        int numTaxa = _itemFileHeader.getNItem();

        seekToRecord(_itemBinFile, _itemFileHeader.getRpCdat());
        List<Integer> charTaxonDataRecordIndicies = readIntegerList(_itemBinFile, numChars);

        for (int i = 0; i < numChars; i++) {
            int charTaxonDataRecordIndex = charTaxonDataRecordIndicies.get(i);
            au.org.ala.delta.model.Character c = _characters.get(i);

            seekToRecord(_itemBinFile, charTaxonDataRecordIndex);

            if (c instanceof MultiStateCharacter) {

                MultiStateCharacter multiStateChar = (MultiStateCharacter) c;

                int bitsPerTaxon = multiStateChar.getStates().length + 1;
                int totalBitsNeeded = bitsPerTaxon * _taxa.size();

                int bytesToRead = Double.valueOf(Math.ceil(Double.valueOf(totalBitsNeeded) / Double.valueOf(Byte.SIZE))).intValue();

                byte[] bytes = new byte[bytesToRead];
                _itemBinFile.readBytes(bytes);
                List<Boolean> taxaData = byteArrayToBooleanList(bytes);

                for (int j = 0; j < numTaxa; j++) {
                    Item t = _taxa.get(j);

                    int startIndex = j * bitsPerTaxon;
                    int endIndex = startIndex + bitsPerTaxon;

                    List<Boolean> taxonData = taxaData.subList(startIndex, endIndex);
                    // System.out.println(c.getDescription() + " " +
                    // t.getDescription() + " " + taxonData.toString());
                }

            } else if (c instanceof IntegerCharacter) {
                IntegerCharacter intChar = (IntegerCharacter) c;

                int bitsPerTaxon = intChar.getMaximumValue() - intChar.getMinimumValue() + 4;
                int totalBitsNeeded = bitsPerTaxon * _taxa.size();

                int bytesToRead = Double.valueOf(Math.ceil(Double.valueOf(totalBitsNeeded) / Double.valueOf(Byte.SIZE))).intValue();

                byte[] bytes = new byte[bytesToRead];
                _itemBinFile.readBytes(bytes);
                List<Boolean> taxaData = byteArrayToBooleanList(bytes);

                for (int j = 0; j < numTaxa; j++) {
                    Item t = _taxa.get(j);

                    int startIndex = j * bitsPerTaxon;
                    int endIndex = startIndex + bitsPerTaxon;

                    List<Boolean> taxonData = taxaData.subList(startIndex, endIndex);
                    // System.out.println(c.getDescription() + " " +
                    // t.getDescription() + " " + taxonData.toString());
                }

            } else if (c instanceof RealCharacter) {
                // Read NI inapplicability bits
                int bytesToRead = Double.valueOf(Math.ceil(Double.valueOf(_taxa.size()) / Double.valueOf(Byte.SIZE))).intValue();
                byte[] bytes = new byte[bytesToRead];
                _itemBinFile.readBytes(bytes);

                List<Boolean> taxaInapplicabilityData = byteArrayToBooleanList(bytes);

                int recordsSpannedByInapplicabilityData = recordsSpannedByBytes(bytesToRead);

                seekToRecord(_itemBinFile, charTaxonDataRecordIndex + recordsSpannedByInapplicabilityData);

                // Read two float values per taxon
                List<Float> taxonData = readFloatList(_itemBinFile, numTaxa * 2);

                for (int j = 0; j < numTaxa; j++) {
                    Item t = _taxa.get(j);

                    float lowerFloat = taxonData.get(j * 2);
                    float upperFloat = taxonData.get((j * 2) + 1);

                    // System.out.println(String.format("%s %s %f %f",
                    // c.getDescription(), t.getDescription(), lowerFloat,
                    // upperFloat));
                }

            } else if (c instanceof TextCharacter) {
                TextCharacter textChar = (TextCharacter) c;

                // Read NI inapplicability bits
                int bytesToRead = Double.valueOf(Math.ceil(Double.valueOf(_taxa.size()) / Double.valueOf(Byte.SIZE))).intValue();
                byte[] bytes = new byte[bytesToRead];
                _itemBinFile.readBytes(bytes);

                List<Boolean> taxaInapplicabilityData = byteArrayToBooleanList(bytes);

                int recordsSpannedByInapplicabilityData = recordsSpannedByBytes(bytesToRead);

                seekToRecord(_itemBinFile, charTaxonDataRecordIndex + recordsSpannedByInapplicabilityData);

                List<Integer> taxonTextDataOffsets = readIntegerList(_itemBinFile, numTaxa + 1);

                int recordsSpannedByOffsets = recordsSpannedByBytes((numTaxa + 1) * sizeIntInBytes);

                seekToRecord(_itemBinFile, charTaxonDataRecordIndex + recordsSpannedByInapplicabilityData + recordsSpannedByOffsets);

                ByteBuffer taxonTextData = _itemBinFile.readByteBuffer(taxonTextDataOffsets.get(taxonTextDataOffsets.size() - taxonTextDataOffsets.get(0)));

                for (int j = 0; j < numTaxa; j++) {
                    Item t = _taxa.get(j);

                    int lowerOffset = taxonTextDataOffsets.get(j);
                    int upperOffset = taxonTextDataOffsets.get(j + 1);
                    int textLength = upperOffset - lowerOffset;

                    if (textLength > 0) {
                        byte[] textBytes = new byte[textLength];
                        taxonTextData.get(textBytes);

                        String txt = new String(textBytes);
                        // System.out.println(txt);
                        // System.out.println();
                    }
                }

            }
        }
    }

    private void readTaxonData() {

        int numItems = _itemFileHeader.getNItem();
        DefaultDataSetFactory dsFactory = new DefaultDataSetFactory();

        for (int i = 0; i < numItems; i++) {
            _taxa.add(dsFactory.createItem(i));
        }

        // READ TAXON NAMES - rpTnam
        seekToRecord(_itemBinFile, _itemFileHeader.getRpTnam());

        List<Integer> taxonNameOffsets = new ArrayList<Integer>();
        for (int i = 0; i < numItems + 1; i++) {
            taxonNameOffsets.add(_itemBinFile.readInt());
        }

        int recordsSpannedByOffsets = Double.valueOf(Math.ceil(Integer.valueOf(numItems).doubleValue() / Integer.valueOf(Constants.RECORD_LENGTH_INTEGERS).doubleValue())).intValue();

        seekToRecord(_itemBinFile, _itemFileHeader.getRpTnam() + recordsSpannedByOffsets);

        ByteBuffer nameBuffer = _itemBinFile.readByteBuffer(taxonNameOffsets.get(taxonNameOffsets.size() - 1));
        nameBuffer.position(0);

        for (int i = 0; i < numItems; i++) {
            int start = taxonNameOffsets.get(i);
            int end = taxonNameOffsets.get(i + 1);
            int nameLength = end - start;
            byte[] nameArray = new byte[nameLength];
            nameBuffer.get(nameArray);
            _taxa.get(i).setDescription(BinFileEncoding.decode(nameArray));
        }
    }

    // --------------- UTILITY METHODS
    // --------------------------------------------------------

    // Note that records are 1 indexed.
    private static void seekToRecord(BinFile bFile, int recordNumber) {
        bFile.seek((recordNumber - 1) * Constants.RECORD_LENGTH_INTEGERS * sizeIntInBytes);
    }

    // Read the designed record from the file. Note that records are 1 indexed.
    private static ByteBuffer readRecord(BinFile bFile, int recordNumber) {
        seekToRecord(bFile, recordNumber);
        return bFile.readByteBuffer(Constants.RECORD_LENGTH_INTEGERS * sizeIntInBytes);
    }

    private static String readString(BinFile bFile, int numBytes) {
        byte[] bytes = bFile.read(numBytes);
        return BinFileEncoding.decode(bytes);
    }

    private static List<Integer> readIntegerList(BinFile bFile, int numInts) {
        ByteBuffer bb = bFile.readByteBuffer(numInts * sizeIntInBytes);

        List<Integer> retList = new ArrayList<Integer>();
        for (int i = 0; i < numInts; i++) {
            retList.add(bb.getInt());
        }
        return retList;
    }

    private static List<Float> readFloatList(BinFile bFile, int numFloats) {
        ByteBuffer bb = bFile.readByteBuffer(numFloats * sizeIntInBytes);

        List<Float> retList = new ArrayList<Float>();
        for (int i = 0; i < numFloats; i++) {
            retList.add(bb.getFloat());
        }
        return retList;
    }

    private static List<Boolean> byteArrayToBooleanList(byte[] bArray) {
        List<Boolean> boolList = new ArrayList<Boolean>();

        for (byte b : bArray) {
            List<Boolean> l = byteToBooleanList(b);
            boolList.addAll(l);
        }

        return boolList;
    }

    private static List<Boolean> byteToBooleanList(byte b) {
        List<Boolean> boolList = new ArrayList<Boolean>();

        for (int i = 0; i < Byte.SIZE; i++) {
            if ((b & (1 << i)) > 0) {
                boolList.add(true);
            } else {
                boolList.add(false);
            }
        }

        return boolList;
    }

    private int recordsSpannedByBytes(int numBytes) {
        return Double.valueOf(Math.ceil(Integer.valueOf(numBytes).doubleValue() / Integer.valueOf(Constants.RECORD_LENGTH_BYTES).doubleValue())).intValue();
    }
}
