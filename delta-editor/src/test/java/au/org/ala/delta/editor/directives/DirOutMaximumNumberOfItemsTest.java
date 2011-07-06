package au.org.ala.delta.editor.directives;

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DirOutMaxNumberItems;

/**
 * Tests the DirOutMaximumNumberOfItems class.
 */
public class DirOutMaximumNumberOfItemsTest extends DirOutTest {

	protected Directive getDirective() {
		return ConforDirType.ConforDirArray[ConforDirType.MAXIMUM_NUMBER_OF_ITEMS];
	}
	
	/**
	 * Tests the export of the MAXIMUM NUMBER OF ITEMS directive using our 
	 * sample dataset.
	 */
	public void testDirOutMaximumNumberOfItems() throws Exception {
		
		int maxNumItems = 11;
		
		for (int i=0; i<maxNumItems; i++) {
			_dataSet.addItem();
		}
		DirOutMaxNumberItems dirOut = new DirOutMaxNumberItems();
		
		dirOut.process(_state);
		
		assertEquals("*MAXIMUM NUMBER OF ITEMS 11\n", output());
		
	}
}
