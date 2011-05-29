package au.org.ala.delta.intkey.model;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileEncoding;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.model.AttributeFactory;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.TextCharacter;

public class IntkeyDatasetFileBuilder {

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
        if (_itemFileHeader.getMajorVer() != Constants.DATASET_MAJOR_VERSION || (_itemFileHeader.getMinorVer() != Constants.DATASET_MINOR_VERSION && _itemFileHeader.getRpOmitOr() != 0)) {
            throw new RuntimeException("Incorrect file version");
        }

        _ds.setChineseFormat(_itemFileHeader.getChineseFmt() != 0);

        readHeadingsAndValidationString();

        readTaxonData();
        readCharacters();

        readCharacterImages();
        readStartupImages();
        readCharacterKeywordImages();
        readTaxonKeywordImages();
        readOrWord();
        readOverlayFonts();
        readCharacterItemSubheadings();
        readRealCharacterStateBoundaries();
        readTaxonImages();

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

        String heading = readReferencedString(hFile, recno);
        _ds.setHeading(heading);
        // output to log window
        // set as heading of main window

        if (_charFileHeader.getRpRegSubHeading() > 0) {
            // read and display registered dataset subheading
            _ds.setSubHeading(readReferencedString(hFile, _charFileHeader.getRpRegSubHeading()));
        }

        if (_charFileHeader.getRpValidationString() > 0) {
            // read validation string
            _ds.setValidationString(readReferencedString(hFile, _charFileHeader.getRpValidationString()));
        }
    }

    private void readCharacters() {

        int numChars = _charFileHeader.getNC();
        DefaultDataSetFactory dsFactory = new DefaultDataSetFactory();

        // READ NUMBER OF CHARACTER STATES
        seekToRecord(_charBinFile, _charFileHeader.getRpStat());
        List<Integer> numCharacterStates = readIntegerList(_charBinFile, numChars);

        // READ CHARACTER TYPES
        seekToRecord(_itemBinFile, _itemFileHeader.getRpSpec());
        List<Integer> charTypesList = readIntegerList(_itemBinFile, numChars);

        // Used to determine whether or not output to delta format is permitted
        // - see below.
        int charTypeSum = 0;

        for (int i = 0; i < numChars; i++) {
            charTypeSum += charTypesList.get(i);

            // Type for corresponding character is indicated by the absolute
            // value of the supplied integer value
            int charType = Math.abs(charTypesList.get(i));

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

        // A checksum is supplied in the items file. If this checksum matches
        // the sum of the
        // integers used to specify the character types, delta output is
        // enabled. Otherwise
        // delta output is disabled.
        readEnableDeltaOutput(charTypeSum);

        int recordsSpannedByCharTypes = recordsSpannedByBytes(numChars * Constants.SIZE_INT_IN_BYTES);

        // read numbers of states from items file and check for compatability
        // (only compare multistates because if ICHARS and IITEMS are generated
        // separately, numerics characters with units will differ)
        seekToRecord(_itemBinFile, _itemFileHeader.getRpSpec() + recordsSpannedByCharTypes);
        List<Integer> itemsFileNumCharacterStates = readIntegerList(_itemBinFile, numChars);

        for (int i = 0; i < numChars; i++) {
            Character ch = _characters.get(i);
            if (ch instanceof MultiStateCharacter) {
                int numStatesFromCharsFile = numCharacterStates.get(i);
                int numStatesFromItemsFile = itemsFileNumCharacterStates.get(i);
                if (numStatesFromItemsFile != numStatesFromCharsFile) {
                    throw new RuntimeException("Numbers of states for characters differ between characters file and items file");
                }
            }
        }

        int recordsSpannedByNumCharStates = recordsSpannedByBytes(numChars * Constants.SIZE_INT_IN_BYTES);

        // READ CHARACTER RELIABILITIES
        seekToRecord(_itemBinFile, _itemFileHeader.getRpSpec() + (recordsSpannedByCharTypes + recordsSpannedByNumCharStates));

        List<Float> reliabilityList = readFloatList(_itemBinFile, numChars);
        for (int i = 0; i < numChars; i++) {
            Character ch = _characters.get(i);
            float reliability = reliabilityList.get(i);
            ch.setReliability(reliability);
        }

        readCharacterDescriptionsAndStates(numCharacterStates);
        readCharacterNotes();
        readCharacterMinimumsAndMaximums();
        readCharacterDependencies();
        readCharacterTaxonData();

        // READ CONTAINS SYNONMY INFORMATION
        List<Integer> synonmyInfoList = null;
        if (_itemFileHeader.getRpCsynon() != 0) {
            seekToRecord(_itemBinFile, _itemFileHeader.getRpCsynon());
            synonmyInfoList = readIntegerList(_itemBinFile, numChars);
        }

        // READ OMIT OR
        List<Integer> omitOrList = null;
        if (_itemFileHeader.getRpOmitOr() != 0) {
            seekToRecord(_itemBinFile, _itemFileHeader.getRpOmitOr());
            omitOrList = readIntegerList(_itemBinFile, numChars);
        }

        // READ USE CONTROLLING CHARACTERS FIRST
        List<Integer> useCcList = null;
        if (_itemFileHeader.getRpUseCc() != 0) {
            seekToRecord(_itemBinFile, _itemFileHeader.getRpUseCc());
            useCcList = readIntegerList(_itemBinFile, numChars);
        }

        // READ OMIT PERIOD
        List<Integer> omitPeriodList = null;
        if (_itemFileHeader.getRpOmitPeriod() != 0) {
            seekToRecord(_itemBinFile, _itemFileHeader.getRpOmitPeriod());
            omitPeriodList = readIntegerList(_itemBinFile, numChars);
        }

        // READ NEW PARAGRAPH
        List<Integer> newParagraphList = null;
        if (_itemFileHeader.getRpNewPara() != 0) {
            seekToRecord(_itemBinFile, _itemFileHeader.getRpNewPara());
            newParagraphList = readIntegerList(_itemBinFile, numChars);
        }

        // READ NON AUTOMATIC CONTROLLING CHARACTERS
        List<Integer> nonAutoCcList = null;
        if (_itemFileHeader.getRpNonAutoCc() != 0) {
            seekToRecord(_itemBinFile, _itemFileHeader.getRpNonAutoCc());
            nonAutoCcList = readIntegerList(_itemBinFile, numChars);
        }

        for (int i = 0; i < numChars; i++) {
            Character ch = _characters.get(i);

            if (synonmyInfoList != null) {
                ch.setContainsSynonmyInformation(synonmyInfoList.get(i) != 0);
            }

            if (omitOrList != null) {
                ch.setOmitOr(omitOrList.get(i) != 0);
            }

            if (useCcList != null) {
                ch.setUseCc(useCcList.get(i) != 0);
            }

            if (omitPeriodList != null) {
                ch.setOmitPeriod(omitPeriodList.get(i) != 0);
            }

            if (newParagraphList != null) {
                ch.setNewParagraph(newParagraphList.get(i) != 0);
            }

            if (nonAutoCcList != null) {
                ch.setNonAutoCc(nonAutoCcList.get(i) != 0);
            }
        }

    }

    private void readCharacterDescriptionsAndStates(List<Integer> numCharacterStates) {
        int numChars = _charFileHeader.getNC();

        // READ CHARACTER DESCRIPTIONS
        seekToRecord(_charBinFile, _charFileHeader.getRpCdes());

        List<Integer> charDescriptionRecordIndicies = readIntegerList(_charBinFile, numChars);

        for (int i = 0; i < numChars; i++) {
            au.org.ala.delta.model.Character ch = _characters.get(i);

            int descRecordIndex = charDescriptionRecordIndicies.get(i);
            seekToRecord(_charBinFile, descRecordIndex);

            int numStatesForChar = numCharacterStates.get(i);
            List<Integer> charDescriptionsLengths = readIntegerList(_charBinFile, numStatesForChar + 1);
            int lengthTotal = 0;

            for (int charDescriptionLength : charDescriptionsLengths) {
                lengthTotal += charDescriptionLength;
            }

            int recordsSpannedByDescLengths = recordsSpannedByBytes((numStatesForChar + 1) * Constants.SIZE_INT_IN_BYTES);

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

    private void readCharacterNotes() {
        int numChars = _charFileHeader.getNC();

        // READ TEXT OF CHARACTER NOTES
        if (_charFileHeader.getRpChlp() > 0) {
            List<String> characterNotes = readStringList(_charBinFile, _charFileHeader.getRpChlp(), numChars);

            for (int i = 0; i < numChars; i++) {
                _characters.get(i).setNotes(characterNotes.get(i));
            }

        }

        // READ CHARACTER NOTES FORMATTING INFORMATION

        // Formatting information for when character notes are output to main
        // intkey window
        if (_charFileHeader.getRpChlpFmt1() > 0) {
            _ds.setMainCharNotesFormattingInfo(readReferencedString(_charBinFile, _charFileHeader.getRpChlpFmt1()));
        }

        // Formatting information for when character notes are output to help
        // window
        if (_charFileHeader.getRpChlpFmt2() > 0) {
            _ds.setHelpCharNotesFormattingInfo(readReferencedString(_charBinFile, _charFileHeader.getRpChlpFmt2()));
        }
    }

    private void readCharacterMinimumsAndMaximums() {
        int numChars = _itemFileHeader.getNChar();

        seekToRecord(_itemBinFile, _itemFileHeader.getRpMini());

        List<Integer> minimumValues = readIntegerList(_itemBinFile, numChars);

        int recordsSpannedByMinimumValues = recordsSpannedByBytes(numChars * Constants.SIZE_INT_IN_BYTES);

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

                            for (int k = 0; k < numDependentCharRanges * 2; k = k + 2) {
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
                            for (int idxDependentChar : dependentChars) {
                                // need to subtract one from the index because
                                // the data file uses 1 based indexes while
                                // java uses zero based indexes.
                                Character dependentCharacter = _characters.get(idxDependentChar - 1);
                                dependentCharacter.addControllingCharacters(charDep);
                            }
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

                    // Taxon data consists of a bit for each state, indicating
                    // the states presence, followed by
                    // a final bit signifying whether or not the character is
                    // inapplicable for the taxon.
                    boolean inapplicable = taxonData.get(taxonData.size() - 1);
                    if (!inapplicable) {
                        IntkeyAttributeData attrData = new IntkeyAttributeData(inapplicable);
                        MultiStateAttribute msAttr = new MultiStateAttribute(multiStateChar, attrData);
                        msAttr.setItem(t);

                        HashSet<Integer> presentStates = new HashSet<Integer>();
                        for (int k = 0; k < taxonData.size() - 1; k++) {
                            boolean statePresent = taxonData.get(k);
                            if (statePresent) {
                                presentStates.add(k + 1);
                            }
                        }
                        msAttr.setPresentStates(presentStates);

                        t.addAttribute(multiStateChar, msAttr);
                    }
                }

            } else if (c instanceof IntegerCharacter) {
                IntegerCharacter intChar = (IntegerCharacter) c;
                int charMinValue = intChar.getMinimumValue();
                int charMaxValue = intChar.getMaximumValue();

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

                    boolean inapplicable = taxonData.get(taxonData.size() - 1);
                    if (!inapplicable) {
                        Set<Integer> presentValues = new HashSet<Integer>();
                        for (int k = 0; k < taxonData.size() - 1; k++) {
                            boolean present = taxonData.get(k);
                            if (present) {
                                presentValues.add(k + charMinValue - 1);
                            }
                        }

                        IntegerAttribute intAttr = new IntegerAttribute(intChar, new IntkeyAttributeData(inapplicable));
                        intAttr.setItem(t);
                        intAttr.setPresentValues(presentValues);

                        t.addAttribute(intChar, intAttr);
                    }
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

                    boolean inapplicable = taxaInapplicabilityData.get(j);
                    if (!inapplicable) {
                        RealAttribute realAttr = new RealAttribute((RealCharacter) c, new IntkeyAttributeData(inapplicable));
                        if (!inapplicable) {
                            if (lowerFloat <= upperFloat) {
                                FloatRange range = new FloatRange(lowerFloat, upperFloat);
                                realAttr.setPresentRange(range);
                            }
                        }
                        realAttr.setItem(t);

                        t.addAttribute(c, realAttr);
                    }
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

                int recordsSpannedByOffsets = recordsSpannedByBytes((numTaxa + 1) * Constants.SIZE_INT_IN_BYTES);

                seekToRecord(_itemBinFile, charTaxonDataRecordIndex + recordsSpannedByInapplicabilityData + recordsSpannedByOffsets);

                ByteBuffer taxonTextData = _itemBinFile.readByteBuffer(taxonTextDataOffsets.get(taxonTextDataOffsets.size() - taxonTextDataOffsets.get(0)));

                for (int j = 0; j < numTaxa; j++) {
                    Item t = _taxa.get(j);

                    int lowerOffset = taxonTextDataOffsets.get(j);
                    int upperOffset = taxonTextDataOffsets.get(j + 1);
                    int textLength = upperOffset - lowerOffset;

                    String txt = "";
                    if (textLength > 0) {
                        byte[] textBytes = new byte[textLength];
                        taxonTextData.get(textBytes);

                        txt = BinFileEncoding.decode(textBytes);
                    }

                    boolean inapplicable = taxaInapplicabilityData.get(j);
                    if (!inapplicable) {
                        TextAttribute txtAttr = new TextAttribute(textChar, new IntkeyAttributeData(inapplicable));
                        txtAttr.setText(txt);
                        txtAttr.setItem(t);
                        t.addAttribute(textChar, txtAttr);
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

        List<Integer> taxonNameOffsets = readIntegerList(_itemBinFile, numItems + 1);

        int recordsSpannedByOffsets = recordsSpannedByBytes(numItems * Constants.SIZE_INT_IN_BYTES);

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

        readTaxonLinksFiles();
    }

    private void readCharacterImages() {
        int numChars = _charFileHeader.getNC();

        // Character image info has been shifted from items file to characters
        // file.
        // However, to maintain compatability with older datasets, need to
        // determine
        // in which file the information resides
        int rpCImages = 0;
        BinFile imagesFile = null;
        if (_charFileHeader.getRpCImagesC() != 0) {
            rpCImages = _charFileHeader.getRpCImagesC();
            imagesFile = _charBinFile;
        } else if (_itemFileHeader.getRpCimagesI() != 0) {
            rpCImages = _itemFileHeader.getRpCimagesI();
            imagesFile = _itemBinFile;
        }

        if (rpCImages != 0) {
            List<String> charactersImageData = readStringList(imagesFile, rpCImages, numChars);

            for (int i = 0; i < numChars; i++) {
                _characters.get(i).setImageData(charactersImageData.get(i));
            }
        }
    }

    private void readTaxonImages() {
        int numItems = _itemFileHeader.getNItem();
        int recNo = _itemFileHeader.getRpTimages();

        if (recNo != 0) {
            List<String> taxaImageData = readStringList(_itemBinFile, recNo, numItems);
            for (int i = 0; i < numItems; i++) {
                _taxa.get(i).setImageData(taxaImageData.get(i));
            }
        }
    }

    private void readStartupImages() {
        if (_charFileHeader.getRpStartupImages() > 0) {
            seekToRecord(_charBinFile, _charFileHeader.getRpStartupImages());

            int imageDataRecord = _charBinFile.readInt();
            _ds.setStartupImageData(readReferencedString(_charBinFile, imageDataRecord));
        }
    }

    private void readCharacterKeywordImages() {
        if (_charFileHeader.getRpCKeyImages() > 0) {
            seekToRecord(_charBinFile, _charFileHeader.getRpCKeyImages());

            int imageDataRecord = _charBinFile.readInt();
            _ds.setCharacterKeywordImageData(readReferencedString(_charBinFile, imageDataRecord));
        }
    }

    private void readTaxonKeywordImages() {
        if (_charFileHeader.getRpTKeyImages() > 0) {
            seekToRecord(_charBinFile, _charFileHeader.getRpTKeyImages());

            int imageDataRecord = _charBinFile.readInt();
            _ds.setTaxonKeywordImageData(readReferencedString(_charBinFile, imageDataRecord));
        }
    }

    private void readOrWord() {
        int recordNo = _charFileHeader.getRpOrWord();
        String orWord = null;
        if (recordNo != 0) {
            seekToRecord(_charBinFile, recordNo);
            int orWordLength = _charBinFile.readInt();
            seekToRecord(_charBinFile, recordNo + 1);
            orWord = readString(_charBinFile, orWordLength);
        } else {
            // TODO need to put this literal somewhere else
            orWord = "or";
        }

        _ds.setOrWord(orWord);
    }

    private void readOverlayFonts() {
        int recordNo = _charFileHeader.getRpFont();
        if (recordNo != 0) {
            seekToRecord(_charBinFile, recordNo);
            int numFonts = _charBinFile.readInt();
            List<Integer> fontTextLengths = readIntegerList(_charBinFile, numFonts);

            int totalFontsLength = 0;
            for (int fontLength : fontTextLengths) {
                totalFontsLength += fontLength;
            }

            int recordsSpannedByFontTextLengths = recordsSpannedByBytes(numFonts * Constants.SIZE_INT_IN_BYTES);
            seekToRecord(_charBinFile, recordNo + recordsSpannedByFontTextLengths);

            List<String> fonts = new ArrayList<String>();
            ByteBuffer fontTextData = _charBinFile.readByteBuffer(totalFontsLength);
            for (int fontLength : fontTextLengths) {
                byte[] fontTextBytes = new byte[fontLength];
                fontTextData.get(fontTextBytes);
                String fontText = BinFileEncoding.decode(fontTextBytes);
                fonts.add(fontText);
            }

            _ds.setOverlayFonts(fonts);
        }
    }

    private void readCharacterItemSubheadings() {
        int numChars = _charFileHeader.getNC();
        int recordNo = _charFileHeader.getRpItemSubHead();
        if (recordNo != 0) {
            List<String> itemSubheadings = readStringList(_charBinFile, recordNo, numChars);

            for (int i = 0; i < numChars; i++) {
                _characters.get(i).setItemSubheading(itemSubheadings.get(i));
            }
        }
    }

    private void readRealCharacterStateBoundaries() {
        int numChars = _itemFileHeader.getNChar();
        int recNo = _itemFileHeader.getRpNkbd();

        if (recNo != 0) {
            seekToRecord(_itemBinFile, recNo);
            List<Integer> keyStateBoundariesRecordIndicies = readIntegerList(_itemBinFile, numChars);

            for (int i = 0; i < numChars; i++) {
                Character ch = _characters.get(i);
                if (ch instanceof RealCharacter) {
                    RealCharacter realChar = (RealCharacter) ch;

                    int keyStateBoundariesRecord = keyStateBoundariesRecordIndicies.get(i);
                    seekToRecord(_itemBinFile, keyStateBoundariesRecord);
                    int numKeyStateBoundaries = _itemBinFile.readInt();
                    seekToRecord(_itemBinFile, keyStateBoundariesRecord + 1);

                    List<Float> keyStateBoundaries = readFloatList(_itemBinFile, numKeyStateBoundaries);
                    realChar.setKeyStateBoundaries(keyStateBoundaries);
                }
            }
        }
    }

    // A checksum is supplied in the items file. If this checksum matches the
    // sum of the
    // integers used to specify the character types, delta output is enabled.
    // Otherwise
    // delta output is disabled.
    public void readEnableDeltaOutput(int calculatedChecksum) {
        boolean deltaOutputEnabled = false;

        int fileChecksum = _itemFileHeader.getEnableDeltaOutput();

        if (fileChecksum != 0) {
            if (fileChecksum == calculatedChecksum) {
                deltaOutputEnabled = true;
            }
        }

        _ds.setDeltaOutputPermitted(deltaOutputEnabled);
    }

    public void readTaxonLinksFiles() {
        int numItems = _itemFileHeader.getNItem();

        List<String> linksFileDataWithSubjects = null;
        List<String> linksFileDataNoSubjects = null;

        if (_itemFileHeader.getRpTlinks()[0] != 0) {
            linksFileDataWithSubjects = readStringList(_itemBinFile, _itemFileHeader.getRpTlinks()[0], numItems);
        }

        if (_itemFileHeader.getRpTlinks()[1] != 0) {
            linksFileDataNoSubjects = readStringList(_itemBinFile, _itemFileHeader.getRpTlinks()[1], numItems);
        }

        for (int i = 0; i < numItems; i++) {
            Item it = _taxa.get(i);

            if (linksFileDataWithSubjects != null) {
                it.setLinkFileDataWithSubjects(linksFileDataWithSubjects.get(i));
            }

            if (linksFileDataNoSubjects != null) {
                it.setLinkFileDataNoSubjects(linksFileDataNoSubjects.get(i));
            }
        }

    }

    // --------------- UTILITY METHODS
    // --------------------------------------------------------

    // Note that records are 1 indexed.
    private static void seekToRecord(BinFile bFile, int recordNumber) {
        bFile.seek((recordNumber - 1) * Constants.RECORD_LENGTH_INTEGERS * Constants.SIZE_INT_IN_BYTES);
    }

    // Read the designed record from the file. Note that records are 1 indexed.
    private static ByteBuffer readRecord(BinFile bFile, int recordNumber) {
        seekToRecord(bFile, recordNumber);
        return bFile.readByteBuffer(Constants.RECORD_LENGTH_INTEGERS * Constants.SIZE_INT_IN_BYTES);
    }

    private static String readString(BinFile bFile, int numBytes) {
        byte[] bytes = bFile.read(numBytes);
        return BinFileEncoding.decode(bytes);
    }

    // Helper method to deal with a common pattern in intkey data files - one
    // record
    // contains a single integer which is the length of the string in bytes, the
    // following
    // record contains the text of the string
    private static String readReferencedString(BinFile bFile, int recordNumber) {
        seekToRecord(bFile, recordNumber);
        int stringLength = bFile.readInt();
        seekToRecord(bFile, recordNumber + 1);
        return readString(bFile, stringLength);
    }

    // Helper method to deak with a common pattern in intkey data files - a
    // record contains
    // N integer values, each of which, if non-zero point to records from which
    // a string can be
    // read using readReferencedString (see above)
    private static List<String> readStringList(BinFile bFile, int recordNumber, int listSize) {
        List<String> returnList = new ArrayList<String>();

        seekToRecord(bFile, recordNumber);
        List<Integer> stringReferences = readIntegerList(bFile, listSize);

        for (int stringReference : stringReferences) {
            if (stringReference != 0) {
                returnList.add(readReferencedString(bFile, stringReference));
            } else {
                returnList.add(null);
            }
        }

        return returnList;
    }

    private static List<Integer> readIntegerList(BinFile bFile, int numInts) {
        ByteBuffer bb = bFile.readByteBuffer(numInts * Constants.SIZE_INT_IN_BYTES);

        List<Integer> retList = new ArrayList<Integer>();
        for (int i = 0; i < numInts; i++) {
            retList.add(bb.getInt());
        }
        return retList;
    }

    private static List<Float> readFloatList(BinFile bFile, int numFloats) {
        ByteBuffer bb = bFile.readByteBuffer(numFloats * Constants.SIZE_INT_IN_BYTES);

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
