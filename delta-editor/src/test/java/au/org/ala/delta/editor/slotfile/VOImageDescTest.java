package au.org.ala.delta.editor.slotfile;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.model.image.ImageOverlay;


/**
 * Tests the VOImageDesc class.
 */ 
public class VOImageDescTest extends DeltaTestCase {
 
	/** Holds the instance of the class we are testing */
	private DeltaVOP _vop;
	
	@Before
	public void setUp() throws Exception {
		File f = copyURLToFile("/SAMPLE.DLT");
			
		_vop = new DeltaVOP(f.getAbsolutePath(), false);
	}

	@After
	public void tearDown() throws Exception {
		if (_vop != null) {
			_vop.close();
		}
		super.tearDown();
	}
	
	/**
	 * Since we are working with the sample data set we know which taxa have images.
	 */
	public void testReadWriteImageDesc() {
		//for (int i=1; i<14; i++) {
			int id = _vop.getDeltaMaster().uniIdFromItemNo(4);
			VOItemDesc item = (VOItemDesc)_vop.getDescFromId(id);
			List<Integer> imageIds = item.readImageList();
			//System.out.println("Item: "+i+ " has "+ imageIds.size());
		//}
		
		VOImageDesc desc = (VOImageDesc)_vop.getDescFromId(imageIds.get(0));
		List<ImageOverlay> overlays = desc.readAllOverlays();
		
		overlays.get(0).comment = "blah";
		desc.writeAllOverlays(overlays);
		
		overlays = desc.readAllOverlays();
		assertEquals("blah", overlays.get(0).comment);
	}
}
