package au.org.ala.delta.editor.slotfile.model;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOControllingDesc;
import au.org.ala.delta.model.MutableDeltaDataSet;

/**
 * Tests the VOControllingAdaptor class.
 */
public class VOControllingAdaptorTest extends DeltaTestCase {

	/** Holds the data set we obtain the data from to back our test*/
	private DeltaVOP _vop;
	
	private MutableDeltaDataSet _dataSet;
	
	@Before
	public void setUp() throws Exception {
		File f = copyURLToFile("/SAMPLE.DLT");
			
		_vop = new DeltaVOP(f.getAbsolutePath(), false);
		
		SlotFileDataSetFactory factory = new SlotFileDataSetFactory(_vop);
		
		_dataSet = factory.createDataSet("test");
	}

	@After
	public void tearDown() throws Exception {
		if (_vop != null) {
			_vop.close();
		}
		super.tearDown();
	}
	
	/**
	 * Tests the read methods work on the VOControllingAdaptor.
	 */
	@Test
	public void testReadMethods() {
		
		VOControllingAdapter desc = getControllingAdaptor(10, 0);
		
		assertEquals("", desc.getDescription());
		assertEquals(10, desc.getControllingCharacterId());
		assertEquals(1, desc.getStates().size());
		assertTrue(desc.getStates().contains(2));
		assertEquals(1, desc.getDependentCharacterIds().size());
		assertTrue(desc.getDependentCharacterIds().contains(11));
	}
	
	public void testAddDependentCharacter() {
		VOControllingAdapter desc = getControllingAdaptor(10, 0);
		
		au.org.ala.delta.model.Character character = _dataSet.getCharacter(20);
		desc.addDependentCharacter(character);
		
		assertEquals(2, desc.getDependentCharacterIds().size());
		assertTrue(desc.getDependentCharacterIds().contains(11));
		assertTrue(desc.getDependentCharacterIds().contains(20));
	}

	public void testRemoveDependentCharacter() {
		VOControllingAdapter desc = getControllingAdaptor(10, 0);
		
		au.org.ala.delta.model.Character character = _dataSet.getCharacter(11);
		desc.removeDependentCharacter(character);
		
		assertEquals(0, desc.getDependentCharacterIds().size());
	}
		
	
	protected VOControllingDesc getControllingDescForCharacter(int characterNum, int controllingIndex) {
		int id = _vop.getDeltaMaster().uniIdFromCharNo(characterNum);
		VOCharBaseDesc charBase = (VOCharBaseDesc)_vop.getDescFromId(id);
		
		List<Integer> ids = charBase.readDependentContAttrs();
		return (VOControllingDesc)_vop.getDescFromId(ids.get(0));
	}
	
	protected VOControllingAdapter getControllingAdaptor(int characterNum, int controllingIndex) {
		
		VOControllingDesc desc = getControllingDescForCharacter(characterNum, controllingIndex);
		return new VOControllingAdapter(_vop, desc);
	}
}
