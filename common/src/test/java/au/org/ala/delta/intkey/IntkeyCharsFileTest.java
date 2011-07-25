package au.org.ala.delta.intkey;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

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
		_charsFile = new WriteOnceIntkeyCharsFile(null, BinFileMode.FM_TEMPORARY);
	}
	
	
	
	/**
	 * Tests character features are written correctly to the intkey 
	 * chars file.
	 */
	public void testWriteFeatures() throws Exception {
		List<List<String>> features = new ArrayList<List<String>>();
		List<String> featureList = new ArrayList<String>();
		features.add(featureList);
		
		// Single text character...
		featureList.add("test");
		featureList.add("state1");
		
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
		assertEquals("test".length(), length);
		length = dataBuffer.getInt();
		assertEquals("state1".length(), length);
		
		byte[] text = new byte["test".length()];
		dataBuffer.position(IntkeyFile.RECORD_LENGTH_BYTES*2);
		
		dataBuffer.get(text);
		assertEquals("test", new String(text));
		
		dataBuffer.position(IntkeyFile.RECORD_LENGTH_BYTES*2+"test".length());
		text = new byte["state1".length()];
		dataBuffer.get(text);
		assertEquals("state1", new String(text));
		
		
		// This should also have written the num states record.
		_charsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES*4);
		int numStates = _charsFile.readInt();
		assertEquals(1, numStates);
		
	}
	
	/**
	 * Tests character notes are written correctly to the intkey 
	 * chars file.
	 */
	public void testWriteNotes() throws Exception {
		
	}
}

