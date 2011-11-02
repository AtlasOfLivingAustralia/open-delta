package au.org.ala.delta.key.directives.io;

import java.util.List;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.ItemsFileHeader;

public class KeyItemsFile extends BinaryKeyFile {
    private ItemsFileHeader _header;

    public KeyItemsFile(String fileName) {
        super(fileName, BinFileMode.FM_READONLY);
        readHeader();
    }

    private void readHeader() {
        _header = new ItemsFileHeader();
        List<Integer> headerInts = readIntegerList(1, ItemsFileHeader.SIZE);
        _header.fromInts(headerInts);
    }
    
    public int getNumberOfItems() {
        return _header.getNumberOfItems();
    }
    
    public int getNumberOfCharacters() {
        return _header.getNumberOfCharacters();
    }
    
    public List<Float> readCharacterReliabilities() {
        return readFloatList(_header.getCharcterReliabilitiesRecord(), _header.getNumberOfCharacters());
    }
}
