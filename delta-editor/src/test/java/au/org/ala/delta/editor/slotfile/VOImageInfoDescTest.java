package au.org.ala.delta.editor.slotfile;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.slotfile.VOImageInfoDesc.LOGFONT;
import au.org.ala.delta.model.image.ImageSettings.OverlayFontType;
import au.org.ala.delta.util.Pair;


/**
 * Tests the VOImageInfoDesc class.
 */ 
public class VOImageInfoDescTest extends DeltaTestCase {
 
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
	public void testReadWriteImageInfoDesc() {
		
		VOImageInfoDesc imageInfo = _vop.getImageInfo();
		
		
		imageInfo.writeImagePath("uhoh");
		String imagePath = imageInfo.readImagePath();
		assertEquals("uhoh", imagePath);
		
		Pair<LOGFONT, String> fontInfo = imageInfo.readOverlayFont(OverlayFontType.OF_DEFAULT);
		
		LOGFONT font = fontInfo.getFirst();
		font.lfHeight = -15;
		font.lfItalic = 1;
		imageInfo.writeOverlayFont(OverlayFontType.OF_DEFAULT, "Test", font);
		
		fontInfo = imageInfo.readOverlayFont(OverlayFontType.OF_DEFAULT);
		
		assertEquals("Test", fontInfo.getSecond());
		assertEquals(-15, fontInfo.getFirst().lfHeight);
		assertEquals(1, fontInfo.getFirst().lfItalic);
		
	}
}
