package au.org.ala.delta.intkey.model;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.ported.Constants;
import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileEncoding;
import au.org.ala.delta.io.BinFileMode;

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
    private List<Character> _characters;
    private List<Taxon> _taxa;

    public IntkeyDataset readDataSet(File charactersFile, File itemsFile) {

        // should modify BinFile so that you can pass in a File.
        _charBinFile = new BinFile(charactersFile.getAbsolutePath(), BinFileMode.FM_READONLY);
        _itemBinFile = new BinFile(itemsFile.getAbsolutePath(), BinFileMode.FM_READONLY);

        _ds = new IntkeyDataset();
        _charFileHeader = new CharactersFileHeader();
        _itemFileHeader = new ItemsFileHeader();
        _characters = new ArrayList<Character>();
        _taxa = new ArrayList<Taxon>();

        readCharactersFileHeader();
        readItemsFileHeader();

        // Check number of characters is same in two files
        if (_charFileHeader.getNC() != _itemFileHeader.getNChar()) {
            throw new RuntimeException("Number of characters does not match");
        }

        // Check stated record length in items file is correct
        if (_itemFileHeader.getLRec() != Constants.LREC) {
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

        seekToRecord(hFile, recno - 1);
        int len = hFile.readInt();
        seekToRecord(hFile, recno);

        String heading = readString(hFile, len);
        _ds.setHeading(heading);
        // output to log window
        // set as heading of main window

        if (_charFileHeader.getRpRegSubHeading() > 0) {
            // read and display registered dataset subheading
            seekToRecord(hFile, _charFileHeader.getRpRegSubHeading() - 1);
            len = hFile.readInt();
            seekToRecord(hFile, _charFileHeader.getRpRegSubHeading());
            String regSubHeading = readString(hFile, len);
            _ds.setSubHeading(regSubHeading);
        }

        if (_charFileHeader.getRpValidationString() > 0) {
            // read validation string
            seekToRecord(hFile, _charFileHeader.getRpValidationString() - 1);
            len = hFile.readInt();
            seekToRecord(hFile, _charFileHeader.getRpValidationString());
            String validationString = readString(hFile, len);
            _ds.setValidationString(validationString);
        }

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

        ByteBuffer headerBytes = readRecord(_charBinFile, 0);

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

        headerBytes.position(Constants.LREC - 1);

        _charFileHeader.setCptr(headerBytes.getInt());
    }

    private void readItemsFileHeader() {

        ByteBuffer headerBytes = readRecord(_itemBinFile, 0);

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
            ByteBuffer secondHeaderBytes = readRecord(_charBinFile, 0);

            _itemFileHeader.setRpUseCc(0);
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

    private void readCharacters() {

        int numChars = _charFileHeader.getNC();

        // READ CHARACTER TYPES
        seekToRecord(_itemBinFile, _itemFileHeader.getRpSpec() - 1);
        ByteBuffer charTypeData = _itemBinFile.readByteBuffer(numChars * sizeIntInBytes);

        for (int i = 0; i < numChars; i++) {
            // Type for corresponding character is indicated by the absolute
            // value of the supplied integer value
            int charType = Math.abs(charTypeData.getInt());
            Character newChar = null;

            switch (charType) {
            case 1:
                newChar = new MultistateCharacter(false);
                break;
            case 2:
                newChar = new MultistateCharacter(true);
                break;
            case 3:
                newChar = new IntegerNumericCharacter();
                break;
            case 4:
                newChar = new RealNumericCharacter();
                break;
            case 5:
                newChar = new TextCharacter();
                break;
            default:
                throw new RuntimeException("Unrecognized character type");
            }

            _characters.add(newChar);
        }

        // READ CHARACTER RELIABILITIES
        
        // Records with character values are followed by records containing NC integers designating character states. This information is read from elsewhere so 
        // skip all these records to get to the NC real values representing the reliabilities.
        
        //TODO need to read out characters states (only from multistates) and compare.
        int recordsSpannedByCharTypes = Double.valueOf(Math.ceil(Integer.valueOf(numChars).doubleValue() / Integer.valueOf(Constants.LREC).doubleValue())).intValue();
        
        
        seekToRecord(_itemBinFile, _itemFileHeader.getRpSpec() - 1 + (recordsSpannedByCharTypes * 2));
        ByteBuffer charReliabilityData = _itemBinFile.readByteBuffer(numChars * sizeFloatInBytes);
        for (Character ch : _characters) {
            Float reliability = charReliabilityData.getFloat();
            ch.setReliablity(reliability);
        }

        readCharacterDescriptionsAndStates();

    }

    private void readCharacterDescriptionsAndStates() {
        int numChars = _charFileHeader.getNC();

        // READ CHARACTER DESCRIPTIONS
        seekToRecord(_charBinFile, _charFileHeader.getRpCdes() - 1);

        List<Integer> charDescriptionRecordIndicies = new ArrayList<Integer>();
        ByteBuffer recordIndiciesData = _charBinFile.readByteBuffer(numChars * sizeIntInBytes);

        for (int i = 0; i < numChars; i++) {
            charDescriptionRecordIndicies.add(recordIndiciesData.getInt());
        }

        // READ NUMBER OF CHARACTER STATES
        seekToRecord(_charBinFile, _charFileHeader.getRpStat() - 1);

        List<Integer> numStates = new ArrayList<Integer>();
        ByteBuffer numStatesData = _charBinFile.readByteBuffer(numChars * sizeIntInBytes);
        for (int i = 0; i < numChars; i++) {
            numStates.add(numStatesData.getInt());
        }

        for (int i = 0; i < numChars; i++) {

            Character ch = _characters.get(i);
            List<String> charStates = new ArrayList<String>();

            int descRecordIndex = charDescriptionRecordIndicies.get(i);

            seekToRecord(_charBinFile, descRecordIndex - 1);

            int numStatesForChar = numStates.get(i);
            ByteBuffer charDescriptionsTextData = _charBinFile.readByteBuffer((numStatesForChar + 1) * sizeIntInBytes);

            List<Integer> charDescriptionsLengths = new ArrayList<Integer>();

            int lengthTotal = 0;

            for (int j = 0; j < numStatesForChar + 1; j++) {
                int descLength = charDescriptionsTextData.getInt();
                charDescriptionsLengths.add(descLength);
                lengthTotal += descLength;
            }

            int recordsSpannedByDescLengths = Double.valueOf(Math.ceil(Integer.valueOf(numStatesForChar + 1).doubleValue() / Integer.valueOf(Constants.LREC).doubleValue())).intValue();

            seekToRecord(_charBinFile, descRecordIndex - 1 + recordsSpannedByDescLengths);
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
                    charStates.add(descriptionText);
                }
            }

            if (ch instanceof IntegerNumericCharacter) {
                if (charStates.size() == 1) {
                    ((IntegerNumericCharacter) ch).setUnitsDescription(charStates.get(0));
                } else if (charStates.size() > 1) {
                    throw new RuntimeException("Integer characters should only have one state listed which represents the units description.");
                }
            } else if (ch instanceof RealNumericCharacter) {
                if (charStates.size() == 1) {
                    ((RealNumericCharacter) ch).setUnitsDescription(charStates.get(0));
                } else if (charStates.size() > 1) {
                    throw new RuntimeException("Real numeric characters should only have one state listed which represents the units description.");
                }
            } else if (ch instanceof MultistateCharacter) {
                ((MultistateCharacter) ch).setStates(charStates);
            } else {
                if (charStates.size() > 0) {
                    throw new RuntimeException("Text characters should not have a state specified");
                }
            }
        }
    }

    private void readTaxonData() {

        int numItems = _itemFileHeader.getNItem();

        for (int i = 0; i < numItems; i++) {
            _taxa.add(new Taxon());
        }

        // READ TAXON NAMES - rpTnam
        seekToRecord(_itemBinFile, _itemFileHeader.getRpTnam() - 1);

        List<Integer> taxonNameOffsets = new ArrayList<Integer>();
        for (int i = 0; i < numItems + 1; i++) {
            taxonNameOffsets.add(_itemBinFile.readInt());
        }

        int recordsSpannedByOffsets = Double.valueOf(Math.ceil(Integer.valueOf(numItems).doubleValue() / Integer.valueOf(Constants.LREC).doubleValue())).intValue();

        seekToRecord(_itemBinFile, _itemFileHeader.getRpTnam() - 1 + recordsSpannedByOffsets);

        ByteBuffer nameBuffer = _itemBinFile.readByteBuffer(taxonNameOffsets.get(taxonNameOffsets.size() - 1));
        nameBuffer.position(0);

        for (int i = 0; i < numItems; i++) {
            int start = taxonNameOffsets.get(i);
            int end = taxonNameOffsets.get(i + 1);
            int nameLength = end - start;
            byte[] nameArray = new byte[nameLength];
            nameBuffer.get(nameArray);
            _taxa.get(i).setName(BinFileEncoding.decode(nameArray));
        }

        //
    }

    private static void seekToRecord(BinFile bFile, int recordNumber) {
        bFile.seek(recordNumber * Constants.LREC * sizeIntInBytes);
    }

    private static ByteBuffer readRecord(BinFile bFile, int recordNumber) {
        seekToRecord(bFile, recordNumber);
        return bFile.readByteBuffer(Constants.LREC * sizeIntInBytes);
    }

    private static String readString(BinFile bFile, int length) {
        byte[] bytes = bFile.read(length);
        return BinFileEncoding.decode(bytes);
    }

}
