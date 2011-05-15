package au.org.ala.delta.editor.slotfile;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
	@Test
	public void testReadWriteImageDesc() {
		
		VOImageDesc desc = getImageDesc(4, 0);
		List<ImageOverlay> overlays = desc.readAllOverlays();
		
		overlays.get(0).comment = "blah";
		desc.writeAllOverlays(overlays);
		
		overlays = desc.readAllOverlays();
		assertEquals("blah", overlays.get(0).comment);
	}
	
	/**
	 * Tests an overlay can updated correctly.
	 */
	@Test
	public void testReplaceOverlay() {
		
		VOImageDesc desc = getImageDesc(7, 0);
		List<ImageOverlay> overlays = desc.readAllOverlays();
		ImageOverlay overlay = overlays.get(0);
		overlay.overlayText = "Test";
		
		desc.replaceOverlay(overlay, true);
		
		overlay = desc.readOverlay(overlay.getId());
		
		assertEquals(desc.readAllOverlays().size(), overlays.size());
		
		assertEquals("Test", overlay.overlayText);
		
	}
	
	private VOImageDesc getImageDesc(int itemNumber, int imageNumber) {
		int id = _vop.getDeltaMaster().uniIdFromItemNo(itemNumber);
		VOItemDesc item = (VOItemDesc)_vop.getDescFromId(id);
		List<Integer> imageIds = item.readImageList();
		
		VOImageDesc desc = (VOImageDesc)_vop.getDescFromId(imageIds.get(imageNumber));
		
		return desc;
	}
}
