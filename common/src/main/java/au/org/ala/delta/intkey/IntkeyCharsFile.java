package au.org.ala.delta.intkey;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import au.org.ala.delta.io.BinFileMode;

/**
 * Encapsulates the way the various bits of data in the character file
 * are stored and accessed.
 */
public class IntkeyCharsFile extends IntkeyFile {

	private CharactersFileHeader _header;
	
	public IntkeyCharsFile(String fileName, BinFileMode mode) {
		super(fileName, mode);
	}
	
	public void createHeader() {
		newRecord();
		_header = new CharactersFileHeader();
	}
	
	public void writeCharacterNotes(String[] notes) {
		checkEmpty(_header.getRpChlp());
		
		int notesIndexRecord = newRecord();
		_header.setRpChlp(notesIndexRecord);
		
		writeIndexedValues(notesIndexRecord, notes);
	}
	
	
	public void writeCharacterFeatures(List<List<String>> features) {
		checkEmpty(_header.getRpCdes());
		int indexRecord = newRecord();
		int[] indicies = new int[features.size()];
		_header.setRpCdes(indexRecord);
		
		int recordNum = newRecord();
		for (int i=0; i<features.size(); i++) {
			indicies[i] = recordNum;
			recordNum += writeAsContinousString(recordNum, features.get(i).toArray(new String[0]));
		}
	}
	
	public void writeCharacterNotesFormat(String format) {
		writeCharacterNotesFormat(_header.getRpChlpFmt1(), format);
	}
	
	public void writeCharacterNotesHelpFormat(String format) {
		writeCharacterNotesFormat(_header.getRpChlpFmt2(), format);
	}
	
	private void writeCharacterNotesFormat(int recordNum, String format) {
		writeStringWithLength(recordNum, format);
	}
	
	public void writeCharacterImages(String charImages) {
		checkEmpty(_header.getRpCImagesC());
		
		int recordNum = newRecord();
		_header.setRpCImagesC(recordNum);
		
		writeStringWithLength(recordNum, charImages);
	}
	
	public void writeStartupImages(String startupImages) {
		checkEmpty(_header.getRpStartupImages());
		
		int recordNum = newRecord();
		_header.setRpStartupImages(recordNum);
		
		writeStringWithLength(recordNum, startupImages);
	}
	
	public void writeCharacterKeyImages(String characterKeyImages) {
		checkEmpty(_header.getRpCKeyImages());
		
		int recordNum = newRecord();
		_header.setRpCKeyImages(recordNum);
		
		writeStringWithLength(recordNum, characterKeyImages);
	}
	
	public void writeRegistrationSubHeading(String subHeading) {
		checkEmpty(_header.getRpRegSubHeading());
		
		int recordNum = newRecord();
		_header.setRpRegSubHeading(recordNum);
		
		writeStringWithLength(recordNum, subHeading);
	}
	
	public void writeCharacterMask(BitSet characters) {
		checkEmpty(_header.getRpCharacterMask());
	
		int recordNum = newRecord();
		_header.setRpCharacterMask(recordNum);
		
		List<Integer> values = new ArrayList<Integer>();
		values.add(characters.size());
		
		int i=0;
		while (i<characters.size()) {
			int value = 0;
			
			while (i%32 < 32) {
				if (characters.get(i)) {
					value |= 1 << i%32;
				}
				i++;
			}
			
			values.add(value);
		}
	}
	
	public void writeOrWord(String orWord) {
		checkEmpty(_header.getRpOrWord());
		
		int recordNum = newRecord();
		_header.setRpOrWord(recordNum);
		
		writeStringWithLength(recordNum, orWord);
	}
	
	public void writeFonts(String[] fonts) {
		checkEmpty(_header.getRpFont());
		
		int recordNum = newRecord();
		_header.setRpFont(recordNum);
		
		writeToRecord(recordNum, fonts.length);
		writeAsContinousString(recordNum+1, fonts);
	}
	
	public void writeItemSubheadings(String[] itemSubHeadings) {
		checkEmpty(_header.getRpItemSubHead());
		int recordNum = newRecord();
		_header.setRpItemSubHead(recordNum);
		writeIndexedValues(recordNum, itemSubHeadings);
	}
	
	private void checkEmpty(int recordNum) {
		if (recordNum > 0) {
			throw new RuntimeException("Character images already exit");
		}
	}
}
