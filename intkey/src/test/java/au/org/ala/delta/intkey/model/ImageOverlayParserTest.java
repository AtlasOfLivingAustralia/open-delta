package au.org.ala.delta.intkey.model;

import java.util.List;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageOverlayParser;
import au.org.ala.delta.model.image.ImageType;
import junit.framework.TestCase;

public class ImageOverlayParserTest extends TestCase {

    public void testImageNotesWithAtSymbolInContent() throws Exception {
        String overlayText = "<@subject Adiantum raddianum (Starr)> <@imagenotes x=469 y=24 t=\\i{}Adiantum raddianum\\i0{} K. Presl. Photos by Forest and Kim Starr (starrimages@hear.org)>";
        ImageOverlayParser parser = new ImageOverlayParser();
        parser.setColorsBGR(true);
        List<ImageOverlay> overlayList = parser.parseOverlays(overlayText, ImageType.IMAGE_TAXON);
    }
}
