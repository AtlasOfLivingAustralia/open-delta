package au.org.ala.delta.directives.args;

import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.image.ImageInfo;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.model.image.OverlayLocation;

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

    @Test
    public void testMultipleImagesSingleId() throws ParseException {

        ImageParser parser = parserFor("#1. test.jpg <@feature x=1 y=2 w=3 h=-1>\n" + "test2.jpg <@feature x=6 y=7 w=8 h=-10>", ImageType.IMAGE_CHARACTER);

        parser.parse();

        List<ImageInfo> imageInfoList = parser.getImageInfo();

        assertEquals(2, imageInfoList.size());

        ImageInfo imageInfo = imageInfoList.get(0);
        assertEquals(1, imageInfo.getId());
        assertEquals("test.jpg", imageInfo.getFileName());
        assertEquals(1, imageInfo.getOverlays().size());
        ImageOverlay overlay = imageInfo.getOverlays().get(0);
        assertEquals(1, overlay.getX());
        assertEquals(2, overlay.getY());
        assertEquals(3, overlay.getWidth(0));
        assertEquals(-1, overlay.getLocation(0).H);

        imageInfo = imageInfoList.get(1);
        assertEquals(1, imageInfo.getId());
        assertEquals("test2.jpg", imageInfo.getFileName());
        assertEquals(1, imageInfo.getOverlays().size());
        overlay = imageInfo.getOverlays().get(0);
        assertEquals(6, overlay.getX());
        assertEquals(7, overlay.getY());
        assertEquals(8, overlay.getWidth(0));
        assertEquals(-10, overlay.getLocation(0).H);

    }

    /**
     * This test checks the parser can handle correctly formatted text.
     */
    @Test
    public void testMultipleOverlaysWithStringId() throws ParseException {
        ImageParser parser = parserFor("#Test/ test.jpg <@feature x=1 y=2 w=3 h=-1> \n" + "<@text x=4 y=5 w=6 h=7>", ImageType.IMAGE_CHARACTER);

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

    @Test
    public void testSingleStateMultipleHotspots() throws Exception {
        ImageParser parser = parserFor(
                "#Test/ char_vertic_b2.jpg <@subject Vertic B2 horizon> <@feature x=50 y=8 w=405 h=-1> <@state 1 x=50 y=50 w=405 h=-1 x=500 y=11 w=464 h=899 p f=FFFFFF x=56 y=708 w=383 h=110 p f=400040 x=48 y=182 w=409 h=463 p f=FFFFFF> <@state 2 x=50 y=90 w=405 h=-1 x=112 y=97 w=196 h=12> <@ok x=50 y=135> <@cancel x=205 y=135> <@notes x=360 y=135>",
                ImageType.IMAGE_CHARACTER);

        parser.parse();

        List<ImageInfo> imageInfoList = parser.getImageInfo();
        assertEquals(1, imageInfoList.size());
        ImageInfo imageInfo = imageInfoList.get(0);
        assertEquals(7, imageInfo.getOverlays().size());
        ImageOverlay overlay = imageInfo.getOverlays().get(2);

        List<OverlayLocation> locations = new ArrayList<OverlayLocation>();
        for (int i = 0; i < overlay.getNHotSpots(); i++) {
            OverlayLocation location = overlay.getLocation(i);
            if (locations.contains(location)) {
                fail("The same hotspot location appears multiple times in the overlay's location list");
            }
            locations.add(location);
        }
    }
    
    @Test
    public void testMultipleFiles() throws Exception {
    	ImageParser parser = parserFor("#Test/ pet01.gif <@subject Drawings> <@feature x=8 y=8 w=170 h=-1 m> " +
    			"<@state 1 x=233 y=40 w=233 h=-1 x=30 y=106 w=573 h=880 p f=FF8040 m> " +
    			"<@state 2 x=711 y=40 w=266 h=-1 x=713 y=106 w=267 h=880 p f=FF8040 m> " +
    			"<@ok x=608 y=304> <@cancel x=608 y=380> " +
    			"pet02.gif <@subject Photo> <@feature x=150 y=31 w=170 h=-1 m> " +
    			"<@state 1 x=17 y=529 w=225 h=-1  x=5 y=6 w=486 h=990 p f=FF8040 m> " +
    			"<@state 2 x=611 y=510 w=266 h=-2 m t=(not illustrated)> " +
    			"<@ok x=632 y=850> " +
    			"<@cancel x=768 y=850>", ImageType.IMAGE_CHARACTER);
    	parser.parse();
    	
    	List<ImageInfo> imageInfoList = parser.getImageInfo();
        assertEquals(2, imageInfoList.size());
        

    }

}
