package au.org.ala.delta.slotfile.model;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VODeltaMasterDesc;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSetFactory;
import au.org.ala.delta.editor.slotfile.model.VOPAdaptor;

/**
 * Tests the SlotFileDataSetFactory.
 */
public class SlotFileDataSetFactoryTest extends TestCase {

	/**
	 * Tests the creation of a new SlotFileDataSet.
	 */
	@Test
	public void testSlotFileDataSetFactory() {
		
		SlotFileDataSetFactory factory = new SlotFileDataSetFactory();
		
		VOPAdaptor dataSet = (VOPAdaptor)factory.createDataSet("unnamed");
		
		DeltaVOP vop = dataSet.getVOP();
		
		assertNotNull(vop.getDeltaMaster());
		
		VODeltaMasterDesc master = vop.getDeltaMaster();
		
		assertEquals(0, master.getNChars());
		assertEquals(0, master.getNItems());
	}

}
