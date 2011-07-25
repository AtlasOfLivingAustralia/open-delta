package au.org.ala.delta.directives.args;

import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.image.ImageInfo;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageType;

/**
 * Tests the ImageParser class.
 */
public class ImageParserTest extends TestCase {

	
	private ImageParser parserFor(String directiveArgs, int imageType) {
		DeltaContext context = new DeltaContext();
		
		StringReader reader = new StringReader(directiveArgs);
		
		return new ImageParser(context, reader, imageType);
	}
	
	/**
	 * This test checks the parser can handle correctly formatted text.
	 */
	@Test
	public void testSingleArgWithIntegerId() throws ParseException {
		
		ImageParser parser = parserFor("#1. test.jpg <@feature x=1 y=2 w=3 h=-1>", ImageType.IMAGE_CHARACTER);
		
		parser.parse();
		
		List<ImageInfo> imageInfoList = parser.getImageInfo();
		
		assertEquals(1, imageInfoList.size());
		
		ImageInfo imageInfo = imageInfoList.get(0);
		assertEquals(1, imageInfo.getId());
		assertEquals("test.jpg", imageInfo.getFileName());
		assertEquals(1, imageInfo.getOverlays().size());
		ImageOverlay overlay = imageInfo.getOverlays().get(0);
		assertEquals(1, overlay.getX());
		assertEquals(2, overlay.getY());
		assertEquals(3, overlay.getWidth(0));
		assertEquals(-1, overlay.getLocation(0).H);
		
	}
	
	/**
	 * This test checks the parser can handle correctly formatted text.
	 */
	@Test
	public void testMultipleArgsWithStringId() throws ParseException {
		ImageParser parser = parserFor(
				"#Test/ test.jpg <@feature x=1 y=2 w=3 h=-1> \n"
			                    +"<@text x=4 y=5 w=6 h=7>", ImageType.IMAGE_CHARACTER);
		
		parser.parse();
		
		List<ImageInfo> imageInfoList = parser.getImageInfo();
		
		assertEquals(1, imageInfoList.size());
		
		ImageInfo imageInfo = imageInfoList.get(0);
		assertEquals("Test", imageInfo.getId());
		assertEquals("test.jpg", imageInfo.getFileName());
		assertEquals(2, imageInfo.getOverlays().size());
		ImageOverlay overlay = imageInfo.getOverlays().get(0);
		assertEquals(1, overlay.getX());
		assertEquals(2, overlay.getY());
		assertEquals(3, overlay.getWidth(0));
		assertEquals(-1, overlay.getLocation(0).H);
		
		overlay = imageInfo.getOverlays().get(1);
		assertEquals(4, overlay.getX());
		assertEquals(5, overlay.getY());
		assertEquals(6, overlay.getWidth(0));
		assertEquals(7, overlay.getLocation(0).H);
		
		
	}
	
}
