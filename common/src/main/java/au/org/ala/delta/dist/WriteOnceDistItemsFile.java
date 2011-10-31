package au.org.ala.delta.dist;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;

/**
 * Knows the format of the keys items file.  This class is designed to 
 * write the file once, not provide random access to it.
 * PROCEDURE FOR CALCULATION OF VALUES.
 *  1. ONLY NORMAL VALUES OF NUMERIC CHARACTERS SRE USED. EXTREME VALUES ARE IGNORED.
 *  2. KEY STATES ARE APPLIED IF SPECIFIED. NUMERIC CHARACTERS TO WHICH KEY
 *     STATES HAVE BEEN APPLIED ARE SUBSEQUENTLY TREATED AS ORDERED MULTISTATES.
 *  3. FOR UNORDERED MULTISTATES, ALL THE STATES PRESENT ARE OUTPUT.
 *  4. FOR ORDERED MULTISTATES (INCLUDING FORMER NUMERIC CHARACTERS), THE MEAN
 *     OF THE VALUES PRESENT IS OUTPUT.
 *  5. FOR NUMERIC CHARACTERS WITHOUT KEY STATES, AS SINGLE VALUE IS OBTAINED
 *     AS FOLLOWS. IF A RANGE OF VALUES HAS A MIDDLE VALUE, THE RANGE IS
 *     REPLACED BY THAT VALUE; OTHERWISE, IT IS REPLACED BY ITS MIDPOINT.
 *     THE OVERALL MEAN OF THE RESULTING VALUES IS THEN CALCULATED AND OUTPUT.
 
 *  STRUCTURE OF THE OUTPUT FILE.
 
 *  THE OUTPUT FILE IS A DIRECT-ACCESS FILE WITH RECORD LENGTH LRECDA
 *  WORDS (CURRENTLY 32).
 *  EACH ITEM CONSISTS OF A NAME AND A LIST OF ATTRIBUTES.
 *  THE NAME OF ITEM JI IS ON RECORD ITMPRT(JI). THE NAME IS PACKED AS
 *  CHARACTERS (USUALLY 4 PER WORD), AND, IF NECESSARY, IS TRUNCATED
 *  TO FIT ON THE RECORD. ITS LENGTH IS LNAME(JI) CHARACTERS.
 *  THE LIST OF ATTRIBUTES STARTS ON THE NEXT RECORD, AND ITS LENGTH IS
 *  LITM WORDS.
 *  THE STRUCTURE OF THE LIST OF ATTRIBUTES IS AS FOLLOWS.
 *    TEXT CHARACTERS ARE NOT REPRESENTED.
 *
 *    EACH UNORDERED MULTISTATE CHARACTER IS REPRESENTED BY A STRING OF BITS,
 *    THE NUMBER OF BITS BEING EQUAL TO THE NUMBER OF STATES. A BIT IS SET
 *    IF THE CORRESPONDING STATE IS PRESENT IN THE ITEM, THE LEAST-
 *    SIGNIFICANT BIT CORRESPONDING TO STATE 1. THE BIT STRINGS ARE
 *    PACKED INTO WORDS, STARTING AT THE LEAST-SIGNIFICANT BIT OF EACH
 *    WORD. THE BIT STRINGS MAY CROSS WORD BOUNDARIES.
 *    THE NUMBER OF BITS USED PER WORD IS NBITS (WHICH MAY
 *    BE DIFFERENT IN DIFFERENT VERSIONS OF THE PROGRAM).
 *
 *    EACH ORDERED MULTISTATE AND NUMERIC CHARACTER IS REPRESENTED BY A SINGLE
 *    REAL WORD CONTAINING THE MEAN OF THE VALUES.
 *    THE POSITION OF ATTRIBUTE IC WITHIN THE ATTRIBUTE LIST IS GIVEN BY
 *    ICPTW(IC), THE WORD IN WHICH THE ATTRIBUTE STARTS, AND ICPTB(IC),
 *    THE BIT OFFSET WITHIN THE WORD. FOR MULTISTATE CHARACTERS, THE BIT
 *    NUMBER FOR STATE I IS ICPTB(IC)+I. FOR NUMERIC CHARACTERS, THE BIT
 *    OFFSET IS ALWAYS 0.
 *    (EXAMPLE. 5-STATE CHARACTER, STATES 1, 4, AND 5 PRESENT, ICPTW=1,
 *    ICPTB=30, NBITS=32.
 *      FIRST WORD  : 01000000000000000000000000000000
 *      SECOND WORD : 00000000000000000000000000000110)

 */
public class WriteOnceDistItemsFile extends BinaryKeyFile {
	
	private ItemsFileHeader _header;
	
	public WriteOnceDistItemsFile(int numItems, int numCharacters, String fileName, BinFileMode mode) {
		super(fileName, mode);
		createHeader(numItems, numCharacters);
	}
	
	public void createHeader(int numItems, int numCharacters) {
		nextAvailableRecord();
		_header = new ItemsFileHeader();
		_header.setNumberOfCharacters(numCharacters);
		_header.setNumberOfItems(numItems);
		
		// This is done to allocate the first record to the header.
		writeToRecord(1, _header.toInts());
	}
	
	public void writeHeader() {
		overwriteRecord(1, _header.toInts());
	}

	public int writeItem(String description, ByteBuffer work) {
		
		int record = nextAvailableRecord();
		if (description.length() > RECORD_LENGTH_BYTES) {
			description = description.substring(0, RECORD_LENGTH_BYTES);
		}
		writeToRecord(record, description);
		
	
		List<Integer> ints = new ArrayList<Integer>();
		for (int i=0;i<RECORD_LENGTH_INTEGERS; i++) {
			ints.add(work.getInt(i*4));
		}
		writeToRecord(record+1, ints);
		
		return record;
	}

	public void setLengthOfAttributeLists(int itemLength) {
		_header.setLengthOfAttributeLists(itemLength);
	}
	
	public void writeHeading(String heading) {
		checkEmpty(_header.getHeadingRecord());
		int record = nextAvailableRecord();
		_header.setHeadingRecord(record);
		_header.setLengthOfHeading(heading.length());
		
		writeToRecord(record, heading);
	}
	
	private void checkEmpty(int recordNum) {
		if (recordNum > 0) {
			throw new RuntimeException("The record has already been allocated.");
		}
	}

	public void writeCharacterTypes(List<Integer> types) {
		int record = nextAvailableRecord();
		_header.setCharacterTypesRecord(record);
		writeToRecord(record, types);
	}
	
	public void writeNumbersOfStates(List<Integer> states) {
		int max = 0;
		for (int numStates : states) {
			max = Math.max(max, numStates);
		}
		_header.setMaximumNumberOfStates(max);
		int record = nextAvailableRecord();
		_header.setStateNumbersRecord(record);
		writeToRecord(record, states);
	}

	public void writeCharacterMask(List<Boolean> mask) {
		int record = nextAvailableRecord();
		_header.setCharacterMaskRecord(record);
		writeBooleansToRecord(record, mask);
	}

	public void writeItemMask(List<Boolean> mask) {
		int record = nextAvailableRecord();
		_header.setItemMaskRecord(record);
		writeBooleansToRecord(record, mask);
	}

	public void writeCharacterWeights(List<Float> weights) {
		int record = nextAvailableRecord();
		_header.setCharcterWeightsRecord(record);
		writeFloatsToRecord(record, weights);
	}
	
	public void writeAttributeOffsets(int[] wordOffsets, int[] bitOffsets) {
		int record = nextAvailableRecord();
		_header.setItemCharacterIndexRecord(record);
		record += writeToRecord(record, wordOffsets);
		
		_header.setItemCharacterBitOffsetsRecord(record);
		writeToRecord(record, bitOffsets);
	}

	public void writeItemRecordsAndNameLengths(List<Integer> itemRecords, List<Integer> nameLengths) {
		int record = nextAvailableRecord();
		_header.setItemsRecord(record);
		writeToRecord(record, itemRecords);
		
		record = nextAvailableRecord();
		_header.setLengthsOfItemNamesRecord(record);
		writeToRecord(record, nameLengths);
	}

}
