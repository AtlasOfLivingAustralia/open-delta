package au.org.ala.delta.editor.directives;

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DirOutTaxonImages;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayType;

/**
 * Tests the DirOutTaxonImages class.
 */
public class DirOutTaxonImagesTest extends DirOutImageOverlayTest {

	protected Directive getDirective() {
		return ConforDirType.ConforDirArray[ConforDirType.TAXON_IMAGES];
	}
	
	protected void initialiseDataSet() {

		for (int i=0; i<10; i++) {
			Item item = _dataSet.addItem();
			item.setDescription("item "+(i+1));
		}
	}
	
	protected Image addImage(String fileName, int toItem) {
		Item item = _dataSet.getItem(toItem);
		Image image = item.addImage(fileName, "");
		return image;
	}
	
	/**
	 * Tests the export of a single image with a feature overlay to a 
	 * CHARACTER IMAGES directive using our sample dataset.
	 */
	public void testDirOutCharImagesFeatureOverlay() throws Exception {
		
		initialiseDataSet();
		
		Image image = addImage("image 1", 3);
		ImageOverlay overlay = addOverlay(image, OverlayType.OLSUBJECT);
		overlay.overlayText="Subject";
		DirOutTaxonImages dirOut = new DirOutTaxonImages();
		
		dirOut.process(_state);
		
		assertEquals("*TAXON IMAGES\n# item 3/\n"+
				"     image 1\n"+
				"          <@subject Subject>\n", output());
	}
	
	
}
