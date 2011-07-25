package au.org.ala.delta.intkey;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileEncoding;
import au.org.ala.delta.io.BinFileMode;

/**
 * Provides access to the contents of a file formatted for use by the
 * Intkey program.
 */
public class IntkeyFile extends BinFile {

	// TODO - I nicked these from the Intkey project Constants class.
	public static final int RECORD_LENGTH_INTEGERS = 32;
    public static final int RECORD_LENGTH_BYTES = 128;
    
    public static final int SIZE_INT_IN_BYTES = Integer.SIZE / Byte.SIZE;
    public static final int SIZE_FLOAT_IN_BYTES = Float.SIZE / Byte.SIZE;
    
    public static final int DATASET_MAJOR_VERSION = 5;
    public static final int DATASET_MINOR_VERSION = 2;
    
    private int _recordCount;
    
	public IntkeyFile(String fileName, BinFileMode mode) {
		super(fileName, mode);
		_recordCount = 0;
	}
	
	
	
	public int writeToRecord(int recordNumber, String value) {
		return writeToRecord(recordNumber, 0, value);
	}
	
	/**
	 * Writes to consecutive records the length of the supplied string,
	 * then, in the following record, the String (encoded using default
	 * BinFileEncoding)
	 * @param recordNumber the record number to write the length to.  The
	 * string itself will be written to record recordNumber+1
	 * @param value the string to write.
	 */
	public int writeStringWithLength(int recordNumber, String value) {
		
		writeToRecord(recordNumber, value.length());
		int numRecords = writeToRecord(recordNumber++, 0, value);
		return numRecords+1;
	}
	
	/**
	 * Writes a String to the identified record.  Optionally w
	 * @param recordNumber the record to write to.
	 * @param offset
	 * @param value
	 * @param writeLength
	 */
	public int writeToRecord(int recordNumber, int offset, String value) {
		byte[] notesBytes = BinFileEncoding.encode(value);
		if (notesBytes.length > Byte.MAX_VALUE) {
			throw new RuntimeException("Maximum allowed size is :"+Byte.MAX_VALUE);
		}
		
		return writeToRecord(recordNumber, offset, notesBytes);
	}
	
	public int writeToRecord(int recordNumber, List<Integer> values) {
		ByteBuffer bytes = ByteBuffer.allocate(values.size() * SIZE_INT_IN_BYTES);
		bytes.order(ByteOrder.LITTLE_ENDIAN);
		for (int value : values) {
			bytes.putInt(value);
		}
		return writeToRecord(recordNumber, 0, bytes.array());
	}
	
	public int writeToRecord(int recordNumber, int[] values) {
		seekToRecord(recordNumber);
		writeInts(values);
		
		return values.length / RECORD_LENGTH_INTEGERS + 1;
	}
	
	public void writeToRecord(int recordNumber, int value) {
		seekToRecord(recordNumber);
		writeInts(new int[]{value});
	}
	
	public void writeToRecord(int recordNumber, byte value) {
		writeToRecord(recordNumber, 0, new byte[] {value});
	}
	
	public int writeToRecord(int recordNumber, int offset, byte[] values) {
		int numRecords = (offset+values.length / RECORD_LENGTH_BYTES);
		if (numRecords > 1 && recordNumber != _recordCount) {
			throw new RuntimeException("Writing "+(values.length + offset)+ " bytes to a record will overwrite the next record");
		}
		
		seekToRecord(recordNumber, offset);
		write(values);
		
		return numRecords+1;
	}

	public int getRecordCount() {
		return _recordCount;
	}
	
	public int newRecord() {
		return ++_recordCount;
	}
	
	// Note that records are 1 indexed.
    private void seekToRecord(int recordNum) {
       seekToRecord(recordNum, 0);
    }
    
    private void seekToRecord(int recordNum, int offset) {
    	 seek(recordOffset(recordNum)+offset);
    }
    
    protected int getRecordNumber(int indexRecordNum, int offset) {
    	int pos = recordOffset(indexRecordNum) + offset;
    	seek(pos);
    	return readInt();
    }
    
    private int recordOffset(int recordNum) {
    	return (recordNum - 1) * RECORD_LENGTH_INTEGERS * SIZE_INT_IN_BYTES;
    }
    
    
    public List<Integer> readIntegerList(int recordNum, int numInts) {
    	
        ByteBuffer bb = readByteBuffer(numInts * SIZE_INT_IN_BYTES);

        List<Integer> retList = new ArrayList<Integer>();
        for (int i = 0; i < numInts; i++) {
            retList.add(bb.getInt());
        }
        
        return retList;
    }
    
    /**
     * Writes:
     * 1) An index record that contains one entry per supplied value which
     * indicates which record that value is written to.
     * 2) For each value:
     * 2.1) A record containing the length of the value.
     * 2.2) The subsequent record(s) containing the value.
     * 
     * @param indexRecordNum the record number to contain the index.
     * @param values the values to write.
     */
    public void writeIndexedValues(int indexRecordNum, String[] values) {
    	int[] indicies = new int[values.length];
    	int recordNum = newRecord();
    	for (int i=0; i<values.length; i++) {
    		indicies[i] = recordNum;
    		recordNum += writeStringWithLength(recordNum, values[i]);
    	}
    	writeToRecord(indexRecordNum, indicies);
    }
    
    /**
     * Writes the supplied values in the form:
     * 1) The record at startRecord will contain values.length integers,
     * each of which is the length of the value.
     * 2) The following record will contain the text from values,
     * concatenated into a single continuous string.
     * @param startRecord the record number for the lengths.
     * @param values the values to write.
     */
    public int writeAsContinousString(int startRecord, String[] values) {
    	
    	int[] lengths = new int[values.length];
    	StringBuilder text = new StringBuilder();
    	for (int i=0; i<values.length; i++) {
    		lengths[i] = values[i].length();
    		text.append(values[i]);
    	}
    	int numRecords = writeToRecord(startRecord, lengths);
    	numRecords += writeToRecord(startRecord+numRecords, text.toString());
    	
    	return numRecords;
    }

}
