package au.org.ala.delta.ui.image;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayLocation.OLDrawType;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.image.overlay.HotSpot;
import au.org.ala.delta.ui.image.overlay.OvalHotSpot;
import au.org.ala.delta.ui.image.overlay.RectangleHotSpot;
import au.org.ala.delta.ui.image.overlay.RichTextLabel;

/** 
 * The OverlayComponentFactory is responsible for creating components appropriate to
 * display the various types of ImageOverlay.
 */
public class OverlayComponentFactory {

	private DeltaDataSet _dataSet;
	private ResourceMap _resources;
	private ItemFormatter _itemFormatter;
	private CharacterFormatter _characterFormatter;
	
	public OverlayComponentFactory(DeltaDataSet dataSet, ResourceMap resources) {
		_dataSet = dataSet;
		_resources = resources;
		_itemFormatter = new ItemFormatter();
		_characterFormatter = new CharacterFormatter();
	}
	
	public JComponent createOverlayComponent(ImageOverlay overlay, Illustratable imageOwner) {
		
		JComponent component = null;
		
		switch (overlay.type) {
		case OverlayType.OLTEXT: // Use a literal text string
			component = new RichTextLabel(overlay, overlay.overlayText);
			break;
		case OverlayType.OLBUTTONBLOCK: // Used only when modifying aligned push-buttons
		case OverlayType.OLHOTSPOT: // Not a "real" overlay type; used for convenience in editing
		case OverlayType.OLNONE: // Undefined; the remaining values MUST correspond with array OLKeywords. 
		
		case OverlayType.OLITEM: // Use name of the item
			component = new RichTextLabel(overlay, _itemFormatter.formatItemDescription((Item)imageOwner));
			break;
		case OverlayType.OLFEATURE: // Use name of the character
			component = new RichTextLabel(overlay, _characterFormatter.formatCharacterDescription(
					(au.org.ala.delta.model.Character)imageOwner));
			break;
		case OverlayType.OLSTATE: // Use name of the state (selectable)
			component = new RichTextLabel(overlay, _characterFormatter.formatState(
					(MultiStateCharacter)imageOwner, overlay.stateId+1)); // TODO need to convert this state id to a number
			break;
		case OverlayType.OLVALUE: // Use specified values or ranges (selectable)
		case OverlayType.OLUNITS: // Use units (for numeric characters)
		case OverlayType.OLENTER: // Create edit box for data entry
		case OverlayType.OLSUBJECT: // Has text for menu entry
		case OverlayType.OLSOUND: // Has name of .WAV sound file
			break;
		case OverlayType.OLHEADING: // Using heading string for the data-set
		case OverlayType.OLKEYWORD: // Use specified keyword(s)
		case OverlayType.OLOK: // Create OK pushbutton
		case OverlayType.OLCANCEL: // Create Cancel pushbutton
		case OverlayType.OLNOTES: // Create Notes pushbutton (for character notes)
			component = new JButton("Notes");
			break;
		case OverlayType.OLIMAGENOTES: // Create Notes pushbutton (for notes about the image)
		case OverlayType.OLCOMMENT: // Not a "real" overlay type, but used to save comments addressed
		// to images rather than overlays
		default : 
			System.out.println("Unsupported overlay type: "+overlay.type);
		}
		
		if (component != null) {
			component.setOpaque(false);
		}
		return component;
	}
	
	public HotSpot createHotSpot(ImageOverlay overlay, int index) {
		if (overlay.location.get(index).drawType == OLDrawType.ellipse) {
			return new OvalHotSpot(overlay, index);
		}
		else {
			return new RectangleHotSpot(overlay, index);
		}
	}
}
