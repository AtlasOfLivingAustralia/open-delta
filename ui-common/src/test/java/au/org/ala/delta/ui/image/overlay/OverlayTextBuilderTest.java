package au.org.ala.delta.ui.image.overlay;

import org.jdesktop.application.ResourceMap;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayLocation;
import au.org.ala.delta.model.image.OverlayType;

import junit.framework.TestCase;

/**
 * Tests the OverlayTextBuilder class.
 */
public class OverlayTextBuilderTest extends TestCase {

	private OverlayTextBuilder _builder;
	private ImageOverlay _overlay;
	
	class ItemStub extends Item {
		private String _description;
		public ItemStub(int itemNumber, String description) {
			super(itemNumber);
			_description = description;
		}
		
		@Override
		public String getDescription() {
			return _description;
		}
		
		@Override
		public boolean isVariant() {
			return false;
		}
	}
	
	@Before
	protected void setUp() {
		ResourceMap resources = new ResourceMap(null, getClass().getClassLoader(), "au.org.ala.delta.ui.resources.DeltaSingleFrameApplication");
		
		_builder = new OverlayTextBuilder(resources);	
		_overlay = new ImageOverlay();
		_overlay.addLocation(new OverlayLocation());
	}
	
	@Test
	public void testTextOverlay() {
		
		_overlay.overlayText = "Test text overlay";
		_overlay.type = OverlayType.OLTEXT;
		String text = _builder.getText(_overlay, null);
		
		assertEquals(_overlay.overlayText, text);
	}
	
	@Test
	public void testItemOverlay() {
		
		// TODO the space should be removed as a result of the comment removal...
		_overlay.overlayText = " Additional text";
		_overlay.type = OverlayType.OLITEM;
		Item item = new ItemStub(1, "Item description <Comment>");
		
		// Defaults. (no comments, use item description).
		String text = _builder.getText(_overlay, item);
		assertEquals("Item description Additional text", text);
		
		// Include comments
		_overlay.getLocation(0).flags += ImageOverlay.OL_INCLUDE_COMMENTS;
		text = _builder.getText(_overlay, item);
		assertEquals("Item description <Comment> Additional text", text);
		
		
		
	}
	
	
//case OverlayType.OLTEXT: // Use a literal text string
//	text = overlay.overlayText;
//	break;
//case OverlayType.OLITEM: // Use name of the item
//	text = _itemFormatter.formatItemDescription((Item) imageOwner);
//	break;
//case OverlayType.OLFEATURE: // Use name of the character
//	String description = _characterFormatter
//			.formatCharacterDescription((au.org.ala.delta.model.Character) imageOwner);
//	text = WordUtils.capitalize(description);
//	break;
//case OverlayType.OLSTATE: // Use name of the state (selectable)
//	text = _stateFormatter.formatState(
//			(MultiStateCharacter) imageOwner, overlay.stateId + 1); // TODO convert from id to number inside slotfile code
//	break;
//case OverlayType.OLVALUE: // Use specified values or ranges (selectable)
//	String value = overlay.getValueString();
//	String units = getUnits(imageOwner);
//	if (StringUtils.isNotEmpty(units)) {
//		value += " " + units;
//	}
//	text = value;
//	break;
//case OverlayType.OLUNITS: // Use units (for numeric characters)
//	text = getUnits(imageOwner);
//	break;
//case OverlayType.OLENTER: // Create edit box for data entry
//case OverlayType.OLOK: // Create OK pushbutton
//	text = _resources.getString("imageOverlay.okButton.text");
//	break;
//case OverlayType.OLCANCEL: // Create Cancel pushbutton
//	text = _resources.getString("imageOverlay.cancelButton.text");
//	break;
//case OverlayType.OLNOTES: // Create Notes pushbutton (for character notes)
//	text = _resources.getString("imageOverlay.notesButton.text");
//	break;
//case OverlayType.OLIMAGENOTES: // Create Notes pushbutton (for notes about the image)
//	text = _resources.getString("imageOverlay.imageNotesButton.text");
//	break;
}
