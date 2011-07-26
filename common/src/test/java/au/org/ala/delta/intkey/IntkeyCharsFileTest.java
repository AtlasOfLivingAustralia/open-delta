package au.org.ala.delta.intkey;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.io.BinFileMode;

import junit.framework.TestCase;

/**
 * The class <code>IntkeyCharsFileTest</code> contains tests for the class
 * {@link <code>IntkeyCharsFile</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 7/25/11 4:13 PM
 */
public class IntkeyCharsFileTest extends TestCase {

	private WriteOnceIntkeyCharsFile _charsFile;
	
	
	@Before
	public void setUp() throws Exception {
		_charsFile = new WriteOnceIntkeyCharsFile(2, null, BinFileMode.FM_TEMPORARY);
	}
	
	@After
	public void tearDown() throws Exception {
		_charsFile.close();
	}
	
	/**
	 * Tests character features are written correctly to the intkey 
	 * chars file.
	 */
	@Test
	public void testWriteFeatures() throws Exception {
		List<List<String>> features = new ArrayList<List<String>>();
		List<String> featureList = new ArrayList<String>();
		features.add(featureList);
		
		String featureDescription = "feature";
		
		featureList.add(featureDescription);
		featureList.add("state1");
		
		featureList = new ArrayList<String>();
		features.add(featureList);
		featureList.add("feature2");
		
		_charsFile.writeCharacterFeatures(features);
		
		// First record should be the header.
		_charsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES);
		
		// first record should be our feature index, then feature length
		// of first feature, then text of first feature.
		byte[] data = _charsFile.read(3*IntkeyFile.RECORD_LENGTH_BYTES);
		ByteBuffer dataBuffer = ByteBuffer.wrap(data);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		
		int offset = dataBuffer.getInt();
		assertEquals(3, offset);
		
		dataBuffer.position(IntkeyFile.RECORD_LENGTH_BYTES);
		int length = dataBuffer.getInt();
		assertEquals(featureDescription.length(), length);
		length = dataBuffer.getInt();
		assertEquals("state1".length(), length);
		
		byte[] text = new byte[featureDescription.length()];
		dataBuffer.position(IntkeyFile.RECORD_LENGTH_BYTES*2);
		
		dataBuffer.get(text);
		assertEquals(featureDescription, new String(text));
		
		dataBuffer.position(IntkeyFile.RECORD_LENGTH_BYTES*2+featureDescription.length());
		text = new byte["state1".length()];
		dataBuffer.get(text);
		assertEquals("state1", new String(text));
		
		
		// This should also have written the num states record.
		_charsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES*6);
		int numStates = _charsFile.readInt();
		assertEquals(1, numStates);
		assertEquals(0, _charsFile.readInt());
		
	}
	
	/**
	 * Tests character notes are written correctly to the intkey 
	 * chars file.
	 */
	@Test
	public void testWriteNotes() throws Exception {
		List<String> notes = new ArrayList<String>();
		
		String notes1 = "notes 1";
		String notes2 = "this is notes 2";
		notes.add(notes1);
		notes.add(notes2);
		
		_charsFile.writeCharacterNotes(notes);
		
		// First record should be the header.
		_charsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES);
		
		assertEquals(3, _charsFile.readInt());
		assertEquals(5, _charsFile.readInt());
		
		assertEquals(notes1.length(), readInt(3));
		assertEquals(notes1, readString(4, notes1.length()));
		
		assertEquals(notes2.length(), readInt(5));
		assertEquals(notes2, readString(6, notes2.length()));
	}
	
	/**
	 * Tests the character notes format is written correctly to the intkey 
	 * chars file.
	 */
	@Test
	public void testWriteCharacterNotesFormat() {
		String format = "format!";
		
		_charsFile.writeCharacterNotesFormat(format);
		assertEquals(format.length(), readInt(2));	
		assertEquals(format,readString(3, format.length()));
	}
	
	/**
	 * Tests the character notes help format is written correctly to the intkey 
	 * chars file.
	 */
	@Test
	public void testWriteCharacterNotesHelpFormat() {
		String format = "help format!";
		
		_charsFile.writeCharacterNotesFormat(format);
		assertEquals(format.length(), readInt(2));	
		assertEquals(format,readString(3, format.length()));
	}
	
	@Test
	public void testWriteCharacterImages() {
		List<String> characterImages = new ArrayList<String>();
		String image1 = "file1.jpg <@feature x=1 y=2 w=3 h=4>";
		String image2 = "file2.jpg <@text x=1 y=2 w=3 h=4>";
		characterImages.add(image1);
		characterImages.add(image2);
		
		_charsFile.writeCharacterImages(characterImages);
		
		_charsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES);
		assertEquals(3, _charsFile.readInt());
		assertEquals(5, _charsFile.readInt());
		
		assertEquals(image1.length(), readInt(3));
		assertEquals(image1, readString(4, image1.length()));
		
		assertEquals(image2.length(), readInt(5));
		assertEquals(image2, readString(6, image2.length()));	
	}
	
	@Test
	public void testWriteStartupImages() {
		
		String startupImages = "file1.jpg <@feature x=1 y=2 w=3 h=4> "+
		    "file2.jpg <@text x=1 y=2 w=3 h=4>";
		
		_charsFile.writeStartupImages(startupImages);

		assertEquals(startupImages.length(), readInt(2));
		assertEquals(startupImages, readString(3, startupImages.length()));
	}
	
	@Test
	public void testWriteCharacterKeyImages() {
		
		String characterKeyImages = "file1.jpg <@feature x=1 y=2 w=3 h=4>";
		
		_charsFile.writeStartupImages(characterKeyImages);

		assertEquals(characterKeyImages.length(), readInt(2));
		assertEquals(characterKeyImages, readString(3, characterKeyImages.length()));
	}
	
	@Test
	public void testWriteHeading() {
		String heading = "this is a heading";
		
		_charsFile.writeHeading(heading);

		assertEquals(heading.length(), readInt(2));
		assertEquals(heading, readString(3, heading.length()));
	}
	
	@Test
	public void testWriteSubHeading() {
		String heading = "this is a sub heading";
		
		_charsFile.writeSubHeading(heading);

		assertEquals(heading.length(), readInt(2));
		assertEquals(heading, readString(3, heading.length()));
	}
	
	@Test
	public void testWriteValidationString() {
		String validationString = "this is a validation string";
		
		_charsFile.writeValidationString(validationString);

		assertEquals(validationString.length(), readInt(2));
		assertEquals(validationString, readString(3, validationString.length()));
	}
	
	@Test
	public void testWriteCharacterMask() {
		BitSet charMask = new BitSet(2);
		charMask.set(0);
		charMask.set(1);
		
		_charsFile.writeCharacterMask(2, charMask);
		assertEquals(2, readInt(2));
		assertEquals(3, _charsFile.readInt());
	}
	
	@Test
	public void testWriteOrWord() {
		String orWord = "or";
		
		_charsFile.writeOrWord(orWord);

		assertEquals(orWord.length(), readInt(2));
		assertEquals(orWord, readString(3, orWord.length()));
	}
	
	@Test
	public void testWriteFonts() {
		List<String> fonts = new ArrayList<String>();
		String font1 = "font 1";
		String font2 = "font 2 with extra";
		String font3 = "font number 3";
		fonts.add(font1);
		fonts.add(font2);
		fonts.add(font3);
		
		_charsFile.writeFonts(fonts);
		
		_charsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES);
		assertEquals(fonts.size(), _charsFile.readInt());
		
		
		_charsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES*2);
		assertEquals(font1.length(), _charsFile.readInt());
		assertEquals(font2.length(), _charsFile.readInt());
		assertEquals(font3.length(), _charsFile.readInt());
		
		assertEquals(font1+font2+font3, readString(4, font1.length()+font2.length()+font3.length()));
	}
	
	@Test
	public void testWriteItemSubHeadings() {
		List<String> itemSubHeadings = new ArrayList<String>();
		String heading1 = "heading1";
		String heading2 = "heading2";
		itemSubHeadings.add(heading1);
		itemSubHeadings.add(heading2);
		
		_charsFile.writeItemSubheadings(itemSubHeadings);
		
		_charsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES);
		assertEquals(3, _charsFile.readInt());
		assertEquals(5, _charsFile.readInt());
		
		assertEquals(heading1.length(), readInt(3));
		assertEquals(heading1, readString(4, heading1.length()));
		
		assertEquals(heading2.length(), readInt(5));
		assertEquals(heading2, readString(6, heading2.length()));
	}
	
	
	private String readString(int recordNum, int lengthInBytes) {
		_charsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES*(recordNum-1));
		byte[] formatBytes = new byte[lengthInBytes];
		_charsFile.readBytes(formatBytes);
		
		return new String(formatBytes);
	}
	
	private int[] readInts(int recordNum, int numInts) {
		_charsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES*(recordNum-1));
		
		int[] ints = new int[numInts];
		for (int i=0; i<numInts; i++) {
			ints[i] = _charsFile.readInt();
		}
		return ints;
	}
	
	private int readInt(int recordNum) {
		return readInts(recordNum, 1)[0];
	}
}

