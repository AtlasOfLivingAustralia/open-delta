package au.org.ala.delta.translation.intkey;

import junit.framework.TestCase;

import org.apache.commons.lang.NotImplementedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.IntkeyFile;
import au.org.ala.delta.intkey.WriteOnceIntkeyItemsFile;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.impl.DefaultDataSet;

/**
 * Tests the IntkeyCharactersFileWriter class.
 */
public class IntkeyItemsFileWriterTest extends TestCase {

	private IntkeyItemsFileWriter _itemsFileWriter;
	private WriteOnceIntkeyItemsFile _itemsFile;
	private DeltaContext _context;
	private DefaultDataSet _dataSet;
	
	@Before
	public void setUp() throws Exception {
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		_dataSet = (DefaultDataSet)factory.createDataSet("test");
		_context = new DeltaContext(_dataSet);
		
		MultiStateCharacter char1 = (MultiStateCharacter)_dataSet.addCharacter(CharacterType.UnorderedMultiState);
		char1.setDescription("character 1 description");
		char1.setNumberOfStates(3);
		char1.setState(1, "state 1");
		char1.setState(2, "This is state 2");
		char1.setState(3, "3");
		
		TextCharacter char2 = (TextCharacter)_dataSet.addCharacter(CharacterType.Text);
		char2.setDescription("this is character 2 description");
		
		_context.setNumberOfCharacters(2);
		
		Item item1 = _dataSet.addItem();
		item1.setDescription("Item 1 description");
		
		Item item2 = _dataSet.addItem();
		item2.setDescription("Description of item 2");
		
		_itemsFile = new WriteOnceIntkeyItemsFile(2, 2, null, BinFileMode.FM_TEMPORARY);
		_itemsFileWriter = new IntkeyItemsFileWriter(_context, _itemsFile);
	}
	
	@After
	public void tearDown() throws Exception {
		_itemsFile.close();
	}
	
	@Test 
	public void testWriteItemDescrptions() {
		
		_itemsFileWriter.writeItemDescrptions();
		
		String item1Description = _dataSet.getItem(1).getDescription();
		String item2Description = _dataSet.getItem(2).getDescription();
		
		int totalLength = item1Description.length()+item2Description.length();
		int offset = readInt(2);
		assertEquals(0, offset);
		assertEquals(item1Description.length(), _itemsFile.readInt());
		assertEquals(totalLength, _itemsFile.readInt());
	
		assertEquals(item1Description+item2Description, readString(3, totalLength));	
		
	}
	
	@Test 
	public void testWriteCharacterSpecs() {
		
		_context.setCharacterReliability(1, 10.1);
		_context.setCharacterReliability(2, 3.3);
		
		_itemsFileWriter.writeCharacterSpecs();
		
		int[] charTypes = readInts(2, 2);
		assertEquals(1, charTypes[0]);
		assertEquals(5, charTypes[1]);
		
		int[] numStates = readInts(3, 2);
		assertEquals(3, numStates[0]);
		assertEquals(0, numStates[1]);
		
		float[] reliabilities = readFloats(4, 2);
		assertEquals(10.1f, reliabilities[0]);
		assertEquals(3.3f, reliabilities[1]);
		
		
	}
	
	@Test 
	public void zztestwriteCharacterDependencies() {
		throw new NotImplementedException();
	}
	
	@Test 
	public void zztestwriteAttributeData() {
		throw new NotImplementedException();
	}
	
	
	@Test
	public void zztestwriteKeyStateBoundaries() {
		throw new NotImplementedException();
	}
	
	@Test
	public void zztestwriteTaxonImages() {
		throw new NotImplementedException();
	}
	
	@Test
	public void zztestwriteEnableDeltaOutput() {
		throw new NotImplementedException();
	}
	
	@Test
	public void zztestwriteChineseFormat() {
		throw new NotImplementedException();
	}
	
	@Test
	public void zztestwriteCharacterSynonomy() {
		throw new NotImplementedException();
	}
	
	@Test
	public void zztestwriteOmitOr() {
		throw new NotImplementedException();
	}
	
	@Test
	public void zztestwriteUseControllingFirst() {
		throw new NotImplementedException();
	}

	@Test
	public void zztestwriteTaxonLinks() {
		throw new NotImplementedException();
	}
	
	@Test
	public void zztestwriteOmitPeriod() {
		throw new NotImplementedException();
	}
	
	@Test
	public void zztestwriteNewParagraph() {
		throw new NotImplementedException();
	}
	
	@Test
	public void zztestwriteNonAutoControllingChars() {
		throw new NotImplementedException();
	}
	
	@Test
	public void zztestwriteSubjectForOutputFiles() {
		throw new NotImplementedException();
	}
	
	
	private String readString(int recordNum, int lengthInBytes) {
		_itemsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES*(recordNum-1));
		byte[] formatBytes = new byte[lengthInBytes];
		_itemsFile.readBytes(formatBytes);
		
		return new String(formatBytes);
	}
	
	private int[] readInts(int recordNum, int numInts) {
		_itemsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES*(recordNum-1));
		
		int[] ints = new int[numInts];
		for (int i=0; i<numInts; i++) {
			ints[i] = _itemsFile.readInt();
		}
		return ints;
	}
	
	private float[] readFloats(int recordNum, int numFloats) {
		_itemsFile.seek(IntkeyFile.RECORD_LENGTH_BYTES*(recordNum-1));
		
		float[] floats = new float[numFloats];
		for (int i=0; i<numFloats; i++) {
			floats[i] = _itemsFile.readFloat();
		}
		return floats;
	}
	
	private int readInt(int recordNum) {
		return readInts(recordNum, 1)[0];
	}
}

