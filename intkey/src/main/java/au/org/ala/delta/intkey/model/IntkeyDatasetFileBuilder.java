package au.org.ala.delta.intkey.model;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.ported.BinFile;
import au.org.ala.delta.intkey.model.ported.BinFileMode;
import au.org.ala.delta.intkey.model.ported.Constants;
import au.org.ala.delta.intkey.model.ported.DataSet;
import au.org.ala.delta.intkey.model.ported.Params;

public class IntkeyDatasetFileBuilder {
    
    private static final int sizeIntInBytes = Integer.SIZE / Byte.SIZE;
    
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
        
        //should modify BinFile so that you can pass in a File.
        _charBinFile = new BinFile(charactersFile.getAbsolutePath(), BinFileMode.FM_READONLY);
        _itemBinFile = new BinFile(itemsFile.getAbsolutePath(), BinFileMode.FM_READONLY);
      
        _ds = new IntkeyDataset();
        _charFileHeader = new CharactersFileHeader();
        _itemFileHeader = new ItemsFileHeader();
        _characters = new ArrayList<Character>();
        _taxa = new ArrayList<Taxon>();
        
        readCharactersFileHeader();
        readItemsFileHeader();
        
        //Check number of characters is same in two files
        if (_charFileHeader.getNC() != _itemFileHeader.getNChar()) {
            throw new RuntimeException("Number of characters does not match");
        }
        
        //Check stated record length in items file is correct
        if (_itemFileHeader.getLRec() != Constants.LREC) {
            throw new RuntimeException("Record length incorrect");
        }
        
        //Check file is correct version
        
        //Not sure why rpOmitOr is being checked here. Original syntax was "fparam[last_used+1] != 0", where
        //last_used was set to 26. fparam was the array holding all of the integers in the record. CPF 4/4/2011. 
        if (_itemFileHeader.getMajorVer() != datasetMajorVersion ||
                (_itemFileHeader.getMinorVer() != datasetMinorVersion && _itemFileHeader.getRpOmitOr() != 0)) {
            throw new RuntimeException("Incorrect file version");
        }
        
        // read and display data heading
        BinFile hFile;
        int recno;
        if (_charFileHeader.getRpHeading() > 0)  // heading is in chars file
        {
          hFile = _charBinFile;
          recno = _charFileHeader.getRpHeading();
        }
        else // heading is in items file
        {
          hFile = _itemBinFile;
          recno = 2;
        }

        seekToRecord(hFile, recno-1);
        int len = hFile.readInt();
        seekToRecord(hFile, recno);
        
        String heading = readString(hFile, len);
        System.out.println(heading);
        //output to log window
        //set as heading of main window
        
        if (_charFileHeader.getRpRegSubHeading() > 0)
        {
          // read and display registered dataset subheading  
          seekToRecord(hFile, _charFileHeader.getRpRegSubHeading()-1);
          len = hFile.readInt();
          seekToRecord(hFile, _charFileHeader.getRpRegSubHeading());
          String regSubHeading = readString(hFile, len);
          System.out.println(regSubHeading);
        }

        if (_charFileHeader.getRpValidationString() > 0)
        {
            // read validation string
            seekToRecord(hFile, _charFileHeader.getRpValidationString()-1);
            len = hFile.readInt();
            seekToRecord(hFile, _charFileHeader.getRpValidationString());
            String validationString = readString(hFile, len);
            System.out.println(validationString);
        }
        
        return null;
        
        //create a ported DataSet and initialise it to read in header information etc.
        /*
        BinFile cBinFile = new BinFile(charactersFile.getAbsolutePath(), BinFileMode.FM_READONLY);
        BinFile iBinFile = new BinFile(itemsFile.getAbsolutePath(), BinFileMode.FM_READONLY);
        
        DataSet ds = new DataSet();
        ds.init(charactersFile, itemsFile);
        
        
        //READ AND OUTPUT CHARACTER DESCRIPTIONS
        int numChars = ds.Nchar();
        
        seekToRecord(cBinFile, ds.getRpCdes());
        
        List<Integer> charDescriptionRecordIndicies = new ArrayList<Integer>();
        ByteBuffer recordIndiciesData = cBinFile.readByteBuffer(numChars);
        
        for (int i = 0; i < numChars; i++) {
            charDescriptionRecordIndicies.add(recordIndiciesData.getInt());
        }
        
        for(int index: charDescriptionRecordIndicies) {
            seekToRecord(cBinFile, index);
            
            ByteBuffer charDescriptionTextData = cBinFile.readByteBuffer(numChars);
            
        }
        
        
        // READ AND OUTPUT TAXON NAMES;
        int numItems = ds.Nitem();
        
        seekToRecord(iBinFile, ds.getRpTnam() - 1);
        
        List<Integer> taxonNameOffsets = new ArrayList<Integer>(); 
        for (int i = 0; i < numItems + 1; i++) {
            taxonNameOffsets.add(iBinFile.readInt());
        }
        
        int recordsSpannedByOffsets = Double.valueOf(Math.ceil(Integer.valueOf(numItems).doubleValue() / Integer.valueOf(Constants.LREC).doubleValue())).intValue();
        
        seekToRecord(iBinFile, ds.getRpTnam() - 1 + recordsSpannedByOffsets);
        
        ByteBuffer nameBuffer = iBinFile.readByteBuffer(taxonNameOffsets.get(taxonNameOffsets.size() - 1));
        nameBuffer.position(0);
        
        for (int i = 0; i < numItems; i++) {
            int start = taxonNameOffsets.get(i);
            int end = taxonNameOffsets.get(i + 1);
            int nameLength = end - start;
            byte[] nameArray = new byte[nameLength];
            nameBuffer.get(nameArray);
            System.out.println(new String(nameArray));
        }
        
        return null;
        */
    }
    
    private void readCharactersFileHeader() {
        //read first record which contains header file information;
        
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
        _charFileHeader.setRpRegSubHeading(headerBytes.getInt()); // record pointer to registration subheading (13)
        _charFileHeader.setRpValidationString(headerBytes.getInt()); // record pointer to validation string for registered dataset (14)
        
        headerBytes.getInt(); // 15 - record number for character mask - not used.
        
        _charFileHeader.setRpOrWord(headerBytes.getInt()); // 16
        _charFileHeader.setRpCheckForCd(headerBytes.getInt()); // 17
        _charFileHeader.setRpFont(headerBytes.getInt()); //18
        _charFileHeader.setRpItemSubHead(headerBytes.getInt()); // 19
        
        headerBytes.position(Constants.LREC - 1);
        
        _charFileHeader.setCptr(headerBytes.getInt());
    }
    
    private void readItemsFileHeader() {
        
        ByteBuffer headerBytes = readRecord(_itemBinFile, 0);
        
        _itemFileHeader.setNItem(headerBytes.getInt()); // number of items (0)
        _itemFileHeader.setNChar(headerBytes.getInt());  // number of characters (1) 
        _itemFileHeader.setMs(headerBytes.getInt()); // maximum number of states (2)
        
        headerBytes.getInt(); // 3 - MaxDat - not used
        _itemFileHeader.setLRec(headerBytes.getInt()); // 4 - record length used in items file
        
        _itemFileHeader.setRpTnam(headerBytes.getInt()); // record pointer to taxon names (5) 
        _itemFileHeader.setRpSpec(headerBytes.getInt()); // record pointer to specifications  (6)
        _itemFileHeader.setRpMini(headerBytes.getInt()); // record pointer to minima of integer characters (7)
        _itemFileHeader.setLDep(headerBytes.getInt()); // length of dependency array (8)
        _itemFileHeader.setRpCdep(headerBytes.getInt()); // record pointer to character dependency array (9)
        _itemFileHeader.setLinvdep(headerBytes.getInt()); // length of inverted dependency array (10)
        _itemFileHeader.setRpInvdep(headerBytes.getInt()); // record pointer to inverted dependency array (11)
        _itemFileHeader.setRpCdat(headerBytes.getInt());  // record pointer to data for each character (12)
        _itemFileHeader.setLSbnd(headerBytes.getInt());            // length of state bounds array (13)
        _itemFileHeader.setLkstat(Math.max(1, headerBytes.getInt()));  // length of key states array (14)
        
        _itemFileHeader.setMajorVer(headerBytes.getInt()); // 15
        
        _itemFileHeader.setRpNkbd(headerBytes.getInt()); // record pointer to key state bounds array (16)
        _itemFileHeader.setMaxInt(headerBytes.getInt()); // maximum integer value (17)
        
        headerBytes.getInt(); // 18 - Maxtxt1 - not used
        headerBytes.getInt(); // 19 - Maxtxt2 - not used
        _itemFileHeader.setMinorVer(headerBytes.getInt()); // 20
        
        _itemFileHeader.setTaxonImageChar(headerBytes.getInt()); // character specifying taxon images (21)
        _itemFileHeader.setRpCimagesI(headerBytes.getInt()); // pointer to character images (22)
        _itemFileHeader.setRpTimages(headerBytes.getInt()); // pointer to taxon images (23)
        _itemFileHeader.setEnableDeltaOutput(headerBytes.getInt()); // whether to allow DELTA output via OUTPUT SUMMARY command (24)
        _itemFileHeader.setChineseFmt(headerBytes.getInt()); // whether chinese character set (25)
        _itemFileHeader.setRpCsynon(headerBytes.getInt()); // record pointer to characters for synonomy (26)
        _itemFileHeader.setRpOmitOr(headerBytes.getInt()); // record pointer to "omit or" list of characters (27)
        _itemFileHeader.setRpNext(headerBytes.getInt());  // pointer to second parameter record (28)
        
        _itemFileHeader.setDupItemPtr(headerBytes.getInt()); // pointer to duplicated item name mask (29: Constants.LREC - 3)
        _itemFileHeader.setTptr(headerBytes.getInt()); // pointer to b-tree and image masks appended to items file (30: Constants.LREC - 2)
        _itemFileHeader.setLbtree(headerBytes.getInt()); // length of btree in bytes (31: Constants.LREC - 1)
        
        if (_itemFileHeader.getRpNext() > 0) {
            ByteBuffer secondHeaderBytes = readRecord(_charBinFile, 0);
            
            _itemFileHeader.setRpUseCc(0);
            int rpTlinks1 = secondHeaderBytes.getInt();
            _itemFileHeader.setRpOmitPeriod(secondHeaderBytes.getInt());
            _itemFileHeader.setRpNewPara(secondHeaderBytes.getInt());
            _itemFileHeader.setRpNonAutoCc(secondHeaderBytes.getInt());
            int rpTlinks2 = secondHeaderBytes.getInt();
            
            _itemFileHeader.setRpTlinks(new int[] {rpTlinks1, rpTlinks2});
            
        } else {
            _itemFileHeader.setRpUseCc(0);
            _itemFileHeader.setRpTlinks(new int[] {0, 0});
            _itemFileHeader.setRpOmitPeriod(0);
            _itemFileHeader.setRpNewPara(0);
            _itemFileHeader.setRpNonAutoCc(0);
        }
        
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
        return new String(bytes);
    }
    
}
