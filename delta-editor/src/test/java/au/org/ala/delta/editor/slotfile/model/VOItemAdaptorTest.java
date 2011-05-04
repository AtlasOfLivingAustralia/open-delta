package au.org.ala.delta.editor.slotfile.model;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.model.image.Image;

/**
 * Tests the VOItemAdaptor class.
 */
public class VOItemAdaptorTest extends DeltaTestCase {

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
	 * Tests we can add an image to an Item.
	 */
	@Test
	public void testAddImage() {
		
		int itemNumber = 4;
		int id = _vop.getDeltaMaster().uniIdFromItemNo(itemNumber);
		VOItemDesc itemDesc = (VOItemDesc)_vop.getDescFromId(id);
			
		VOItemAdaptor adaptor = new VOItemAdaptor(_vop, itemDesc, 4);
		List<Integer> imageIdsBefore = itemDesc.readImageList();
		
		adaptor.addImage("test", "");
		
		List<Integer> imageIds = itemDesc.readImageList();
		assertEquals(imageIdsBefore.size()+1, imageIds.size());
	
		List<Image> images = adaptor.getImages();
		assertEquals(imageIdsBefore.size()+1, images.size());
		
		assertEquals("test", images.get(imageIdsBefore.size()).getFileName());
	}
		
}
