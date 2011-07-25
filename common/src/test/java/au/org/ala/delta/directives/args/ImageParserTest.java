package au.org.ala.delta.directives.args;

import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.image.ImageInfo;
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
		
		ImageParser parser = parserFor("#1. test.jpg <@feature x=1 y=1 w=3 h=-1>", ImageType.IMAGE_CHARACTER);
		
		parser.parse();
		
		List<ImageInfo> imageInfoList = parser.getImageInfo();
		
		assertEquals(1, imageInfoList.size());
		
		ImageInfo imageInfo = imageInfoList.get(0);
		assertEquals(1, imageInfo.getId());
		assertEquals("test.jpg", imageInfo.getFileName());
		assertEquals(1, imageInfo.getOverlays().size());
	
	}
	
	/**
	 * This test checks the parser can handle correctly formatted text.
	 */
	@Test
	public void testMultipleArgsWithStringId() throws ParseException {
		
		
	}
	
}
