package au.org.ala.delta.editor.directives;

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DirOutCharImages;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayLocation;
import au.org.ala.delta.model.image.OverlayType;

/**
 * Tests the DirOutCharImages class.
 */
public class DirOutCharImagesTest extends DirOutImageOverlayTest {

	protected Directive getDirective() {
		return ConforDirType.ConforDirArray[ConforDirType.CHARACTER_IMAGES];
	}
	
	protected void initialiseDataSet() {
		CharacterType[] characterTypes = new CharacterType[] {
				CharacterType.Text, 
				CharacterType.Text,
				CharacterType.UnorderedMultiState,
				CharacterType.UnorderedMultiState,
				CharacterType.RealNumeric,
				CharacterType.IntegerNumeric,
				CharacterType.OrderedMultiState,
				CharacterType.UnorderedMultiState
		};
		
		
		for (int i=0; i<characterTypes.length; i++) {
			_dataSet.addCharacter(characterTypes[i]);
		}
	}
	
	protected Image addImage(String fileName, int toCharacter) {
		Character character = _dataSet.getCharacter(toCharacter);
		Image image = character.addImage(fileName, "");
		return image;
	}
	
	/**
	 * Tests the export of a single image with a feature overlay to a 
	 * CHARACTER IMAGES directive using our sample dataset.
	 */
	public void testDirOutCharImagesFeatureOverlay() throws Exception {
		
		initialiseDataSet();
		
		Image image = addImage("image 1", 3);
		addOverlay(image, OverlayType.OLFEATURE, 10, 20, 30, 40);
		
		DirOutCharImages dirOut = new DirOutCharImages();
		
		dirOut.process(_state);
		
		assertEquals("*CHARACTER IMAGES\n#3. image 1\n"+
				"         <@feature x=10 y=20 w=30 h=40>\n", output());
	}
	
	/**
	 * Tests the export of a single image with a subject overlay to a 
	 * CHARACTER IMAGES directive using our sample dataset.
	 */
	public void testDirOutCharImagesSubjectOverlay() throws Exception {
		
		initialiseDataSet();
		
		Image image = addImage("image 1", 4);
		ImageOverlay overlay = addOverlay(image, OverlayType.OLSUBJECT);
		overlay.overlayText = "Drawings";
		
		DirOutCharImages dirOut = new DirOutCharImages();
		
		dirOut.process(_state);
		
		assertEquals("*CHARACTER IMAGES\n#4. image 1\n"+
				"         <@subject Drawings>\n", output());
	}
	
	/**
	 * Tests the export of a single image with a state overlay to a 
	 * CHARACTER IMAGES directive using our sample dataset.
	 * It also tests the additional text, centered text, omit description
	 * and include comments flags.
	 */
	public void testDirOutCharImagesStateOverlay() throws Exception {
		
		initialiseDataSet();
		
		Image image = addImage("image 1", 4);
		ImageOverlay overlay = addOverlay(image, OverlayType.OLSTATE, 1, 2, 3, 4);
		overlay.stateId = 1;
		overlay.overlayText="extra";
		overlay.getLocation(0).setIncludeComments(true);
		overlay.getLocation(0).setCentreText(true);
		overlay.getLocation(0).setOmitDescription(true);
		
		DirOutCharImages dirOut = new DirOutCharImages();
		
		dirOut.process(_state);
		
		assertEquals("*CHARACTER IMAGES\n#4. image 1\n"+
             "         <@state 1 x=1 y=2 w=3 h=4 n c m\n          t=extra>\n", output());
	}
	
	/**
	 * Tests the export of a single image with a value overlay to a 
	 * CHARACTER IMAGES directive using our sample dataset.
	 * It also tests a hotspot and additional text.
	 */
	public void testDirOutCharImagesValueOverlayWithHotspots() throws Exception {
		
		initialiseDataSet();
		
		Image image = addImage("image 1", 5);
		ImageOverlay overlay = addOverlay(image, OverlayType.OLVALUE, 1, 2, 3, 4);
		overlay.minVal = "1";
		overlay.maxVal = "3";
		overlay.overlayText="extra";
		addLocation(overlay, 2, 3, 4, 5);
		
		DirOutCharImages dirOut = new DirOutCharImages();
		
		dirOut.process(_state);
		
		assertEquals("*CHARACTER IMAGES\n#5. image 1\n"+
             "         <@value 1-3 x=1 y=2 w=3 h=4\n"+
             "                     x=2 y=3 w=4 h=5\n"+
             "          t=extra>\n", output());
	}
	
	/**
	 * Tests the export of a single image with a state overlay to a 
	 * CHARACTER IMAGES directive using our sample dataset.
	 * It also tests a hotspot and the colour and popup attributes of the hotspot.
	 */
	public void testDirOutCharImagesStateOverlayWithHotspots() throws Exception {
		
		initialiseDataSet();
		
		Image image = addImage("image 1", 4);
		ImageOverlay overlay = addOverlay(image, OverlayType.OLSTATE, 1, 2, 3, 4);
		overlay.stateId = 2;
		overlay.overlayText="extra";
		OverlayLocation hotSpot = addLocation(overlay, 2, 3, 4, 5);
		hotSpot.setPopup(true);
		hotSpot.setColor(0x121ff);
		
		DirOutCharImages dirOut = new DirOutCharImages();
		
		dirOut.process(_state);
		
		assertEquals("*CHARACTER IMAGES\n#4. image 1\n"+
             "         <@state 2 x=1 y=2 w=3 h=4\n"+
             "                   x=2 y=3 w=4 h=5 p f=FF2101\n"+
             "          t=extra>\n", output());
	}
	
	/**
	 * Tests the export of a single image with more than one overlay.
	 */
	public void testDirOutCharImagesMulitpleOverlays() throws Exception {
		
		initialiseDataSet();
		
		Image image = addImage("image 1", 4);
		ImageOverlay overlay = addOverlay(image, OverlayType.OLSTATE, 1, 2, 3, 4);
		overlay.stateId = 2;
		overlay.overlayText="extra";
		OverlayLocation hotSpot = addLocation(overlay, 2, 3, 4, 5);
		hotSpot.setPopup(true);
		hotSpot.setColor(0x121ff);
		
		overlay = addOverlay(image, OverlayType.OLSUBJECT);
		overlay.overlayText = "Subject";
		
		DirOutCharImages dirOut = new DirOutCharImages();
		
		dirOut.process(_state);
		
		assertEquals("*CHARACTER IMAGES\n#4. image 1\n"+
             "         <@state 2 x=1 y=2 w=3 h=4\n"+
             "                   x=2 y=3 w=4 h=5 p f=FF2101\n"+
             "          t=extra>\n"+
             "         <@subject Subject>\n", output());
	}
	
	/**
	 * Tests the export of a single image with more than one image.
	 */
	public void testDirOutCharImagesMulitpleImages() throws Exception {
		
		initialiseDataSet();
		
		Image image = addImage("image 1", 4);
		ImageOverlay overlay = addOverlay(image, OverlayType.OLSTATE, 1, 2, 3, 4);
		overlay.stateId = 2;
		overlay.overlayText="extra";
		OverlayLocation hotSpot = addLocation(overlay, 2, 3, 4, 5);
		hotSpot.setPopup(true);
		hotSpot.setColor(0x121ff);
		
		image = addImage("image 2", 7);
		overlay = addOverlay(image, OverlayType.OLSUBJECT);
		overlay.overlayText = "Subject";
		
		DirOutCharImages dirOut = new DirOutCharImages();
		
		dirOut.process(_state);
		
		assertEquals("*CHARACTER IMAGES\n#4. image 1\n"+
             "         <@state 2 x=1 y=2 w=3 h=4\n"+
             "                   x=2 y=3 w=4 h=5 p f=FF2101\n"+
             "          t=extra>\n"+
             "#7. image 2\n"+
             "         <@subject Subject>\n", output());
	}
}
