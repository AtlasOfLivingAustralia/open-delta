package au.org.ala.delta.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;


/**
 * Provides access to the contents of a file formatted for use by the
 * Intkey and Key programs.  It currently mostly only supports sequential writes.
 */
public class BinaryKeyFile extends BinFile {

	// TODO - I nicked these from the Intkey project Constants class.
	public static final int RECORD_LENGTH_INTEGERS = 32;
    public static final int RECORD_LENGTH_BYTES = 128;
    
    public static final int SIZE_INT_IN_BYTES = Integer.SIZE / Byte.SIZE;
    public static final int SIZE_FLOAT_IN_BYTES = Float.SIZE / Byte.SIZE;
    
    public static final int DATASET_MAJOR_VERSION = 5;
    public static final int DATASET_MINOR_VERSION = 2;
    
    private Set<Integer> _occupiedRecords;
    
	public BinaryKeyFile(String fileName, BinFileMode mode) {
		super(fileName, mode);
		_occupiedRecords = new HashSet<Integer>();
	}
	
	/**
	 * Writes the supplied array of ints to the record identified by
	 * <code>recordNumber</code>.  If the number of values is larger than
	 * will fit into a single record (>128) the write will continue into 
	 * the next record.  
	 * @param recordNumber the record number to write to.
	 * @param values the data to write to the record.
	 * @return the number of records written.
	 */
	public int writeToRecord(int recordNumber, int[] values) {
		checkForOverwrite(recordNumber, 0, values.length * SIZE_INT_IN_BYTES);
		
		// Zero pad the values out to fill a full record.
		if (values.length % RECORD_LENGTH_INTEGERS != 0) {
			int newLength = (values.length / RECORD_LENGTH_INTEGERS + 1) * RECORD_LENGTH_INTEGERS;
			int[] newValues = new int[newLength];
			Arrays.fill(newValues, 0);
			System.arraycopy(values, 0, newValues, 0, values.length);
			values = newValues;
		}
		
		seekToRecord(recordNumber);
		writeInts(values);
		
		return values.length / RECORD_LENGTH_INTEGERS;
	}
	
	public int writeToRecord(int recordNumber, int offset, byte[] values) {
		int numRecords = checkForOverwrite(recordNumber, offset, values.length);
		
		if (values.length % RECORD_LENGTH_BYTES != 0) {
			int newLength = (values.length / RECORD_LENGTH_BYTES + 1) * RECORD_LENGTH_BYTES;
			byte[] newValues = new byte[newLength];
			Arrays.fill(newValues, (byte)0);
			System.arraycopy(values, 0, newValues, 0, values.length);
			values = newValues;
		}
		
		seekToRecord(recordNumber, offset);
		write(values);
		
		return numRecords;
	}

	private int checkForOverwrite(int recordNumber, int offset, int numBytes) {
		int numRecords = (offset+numBytes / RECORD_LENGTH_BYTES);
		if ((offset+numBytes) % RECORD_LENGTH_BYTES > 0) {
			numRecords++;
		}
		System.out.println("Writing :"+numRecords+", starting at record: "+recordNumber);
		
		for (int i=recordNumber; i<recordNumber+numRecords; i++) {
			if (_occupiedRecords.contains(i)) {
				throw new RuntimeException("Writing "+(numBytes + offset)+ " bytes to a record will overwrite the next record");
			}
			_occupiedRecords.add(i);
		}
		return numRecords;
	}
	
	public void writeToRecord(int recordNumber, int value) {
		seekToRecord(recordNumber);
		writeInts(new int[]{value});
	}
	
	
	public int writeToRecord(int recordNumber, String value) {
		return writeToRecord(recordNumber, 0, value);
	}
	
	public void writeToRecord(int recordNumber, byte value) {
		writeToRecord(recordNumber, 0, new byte[] {value});
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
		int numRecords = writeToRecord(recordNumber+1, 0, value);
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
	
	/**
	 * Designed to allow headers and index records to be overwritten.
	 * @param recordNumber the first (of possibily many, depending on the
	 * number of values) record to be overwritten.
	 * @param values the values to write, starting at record, recordNumber..
	 */
	public void overwriteRecord(int recordNumber, List<Integer> values) {
		if (!_occupiedRecords.contains(recordNumber)) {
			throw new IllegalArgumentException("Record "+recordNumber+" has not been allocated.");
		}
		ByteBuffer bytes = ByteBuffer.allocate(values.size() * SIZE_INT_IN_BYTES);
		bytes.order(ByteOrder.LITTLE_ENDIAN);
		for (int value : values) {
			bytes.putInt(value);
		}
		seekToRecord(recordNumber, 0);
		write(bytes.array());
	}
	
	public int writeBooleansToRecord(int recordNumber, List<Boolean> values) {
		ByteBuffer bytes = ByteBuffer.allocate(values.size() * SIZE_INT_IN_BYTES);
		bytes.order(ByteOrder.LITTLE_ENDIAN);
		for (boolean value : values) {
			bytes.putInt(value ? 1 : 0);
		}
		return writeToRecord(recordNumber, 0, bytes.array());
	}
	
	public int writeFloatsToRecord(int recordNumber, List<Float> values) {
		ByteBuffer bytes = ByteBuffer.allocate(values.size() * SIZE_INT_IN_BYTES);
		bytes.order(ByteOrder.LITTLE_ENDIAN);
		for (float value : values) {
			bytes.putFloat(value);
		}
		return writeToRecord(recordNumber, 0, bytes.array());
	}
	
	public int nextAvailableRecord() {
		int max = 0;
		if (_occupiedRecords.size() > 0) {
		    max = Collections.max(_occupiedRecords);
		}
		seekToRecord(max+1);
		return max+1;
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
    	return (recordNum - 1) * RECORD_LENGTH_BYTES;
    }
    
    
    public List<Integer> readIntegerList(int recordNum, int numInts) {
    	seek(recordOffset(recordNum));
        ByteBuffer bb = readByteBuffer(numInts * SIZE_INT_IN_BYTES);

        List<Integer> retList = new ArrayList<Integer>();
        for (int i = 0; i < numInts; i++) {
            retList.add(bb.getInt());
        }
        
        return retList;
    }
    
    public List<Float> readFloatList(int recordNum, int numFloats) {
    	seek(recordOffset(recordNum));
        ByteBuffer bb = readByteBuffer(numFloats * SIZE_INT_IN_BYTES);

        List<Float> retList = new ArrayList<Float>();
        for (int i = 0; i < numFloats; i++) {
            retList.add(bb.getFloat());
        }
        
        return retList;
    }
    
    public String readString(int recordNum, int numChars) {
    	seek(recordOffset(recordNum));
        ByteBuffer bb = readByteBuffer(numChars);
        
        return BinFileEncoding.decode(bb.array());
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
    	Arrays.fill(indicies, 0);
    	int recordNum = indexRecordNum + (int)Math.floor(indicies.length/RECORD_LENGTH_INTEGERS) + 1;
    	for (int i=0; i<values.length; i++) {
    		if (StringUtils.isNotEmpty(values[i])) {
	    		indicies[i] = recordNum;
	    		recordNum += writeStringWithLength(recordNum, values[i]);
    		}
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

	protected List<Integer> bitSetToInts(BitSet set, int numValues) {
		List<Integer> values = new ArrayList<Integer>();
		int i=0; 
		while (i<numValues) {
			int value = 0;
			while (i<numValues && i%32 < 32) {
				if (set.get(i)) {
					value |= 1<< i%32;
				}
				i++;
			}
			values.add(value);
		}
		return values;
	}

	protected void writeStringsWithOffsetsToRecord(int startRecord, List<String> descriptions) {
		int[] offsets = new int[descriptions.size()+1];
		StringBuilder buffer = new StringBuilder();
		// FORTRAN arrays are "1" indexed so any location pointers are off by 1.
		offsets[0] = 1;
		for (int i=0; i<descriptions.size(); i++) {
			offsets[i+1] = offsets[i] + descriptions.get(i).length();
			buffer.append(descriptions.get(i));
		}
		startRecord += writeToRecord(startRecord, offsets);
		writeToRecord(startRecord, buffer.toString());
	}

}
