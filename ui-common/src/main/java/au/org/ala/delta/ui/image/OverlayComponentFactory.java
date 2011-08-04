package au.org.ala.delta.ui.image;

import javax.swing.JComponent;

import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayLocation.OLDrawType;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.image.overlay.HotSpot;
import au.org.ala.delta.ui.image.overlay.OvalHotSpot;
import au.org.ala.delta.ui.image.overlay.OverlayButton;
import au.org.ala.delta.ui.image.overlay.OverlayTextBuilder;
import au.org.ala.delta.ui.image.overlay.RectangleHotSpot;
import au.org.ala.delta.ui.image.overlay.RelativePositionedTextOverlay;
import au.org.ala.delta.ui.image.overlay.RichTextLabel;
import au.org.ala.delta.ui.image.overlay.SelectableTextOverlay;
import au.org.ala.delta.ui.image.overlay.TextFieldOverlay;

/**
 * The OverlayComponentFactory is responsible for creating components
 * appropriate to display the various types of ImageOverlay.
 */
public class OverlayComponentFactory {

    /** Knows how to generate appropriate text for the overlay */
    private OverlayTextBuilder _textBuilder;

    /** Tells us what Fonts to use for the components we create */
    private ImageSettings _imageSettings;

    public OverlayComponentFactory(ResourceMap resources, ImageSettings imageSettings) {
        _imageSettings = imageSettings;
        _textBuilder = new OverlayTextBuilder(resources);
    }

    /**
     * Returns a JComponent suitable for displaying an overlay on an image
     * illustrating the supplied owner.
     * 
     * @param overlay
     *            describes the type of overlay to create.
     * @param imageOwner
     *            the Character/Item that is being Illustrated.
     * @return a component capable of displaying the ImageOverlay.
     */
    public JComponent createOverlayComponent(ImageOverlay overlay, Image image) {
        Illustratable imageOwner = image.getSubject();
        JComponent component = null;
        String text = _textBuilder.getText(overlay, imageOwner);
        switch (overlay.type) {
        case OverlayType.OLTEXT: // Use a literal text string
        case OverlayType.OLITEM: // Use name of the item
            component = new RichTextLabel(overlay, text);
            break;
        case OverlayType.OLFEATURE: // Use name of the character
            component = new RichTextLabel(overlay, text);
            component.setFont(_imageSettings.getDefaultFeatureFont());
            break;
        case OverlayType.OLUNITS: // Use units (for numeric characters)
            component = new RelativePositionedTextOverlay(overlay, text);
            break;
        case OverlayType.OLSTATE: // Use name of the state (selectable)
        case OverlayType.OLVALUE: // Use specified values or ranges (selectable)
        case OverlayType.OLKEYWORD: // Use specified keyword(s)
            component = new SelectableTextOverlay(overlay, text);
            break;
        case OverlayType.OLENTER: // Create edit box for data entry
            component = new TextFieldOverlay(overlay);
            break;
        case OverlayType.OLOK: // Create OK pushbutton
        case OverlayType.OLCANCEL: // Create Cancel pushbutton
        case OverlayType.OLNOTES: // Create Notes pushbutton (for character
                                  // notes)
        case OverlayType.OLIMAGENOTES: // Create Notes pushbutton (for notes
                                       // about the image)
            component = new OverlayButton(overlay, text);
            component.setFont(_imageSettings.getDefaultButtonFont());
            break;
        case OverlayType.OLCOMMENT: // Not a "real" overlay type, but used to
                                    // save comments addressed
            // to images rather than overlays
        case OverlayType.OLBUTTONBLOCK: // Used only when modifying aligned
                                        // push-buttons
        case OverlayType.OLHOTSPOT: // Not a "real" overlay type; used for
                                    // convenience in editing
        case OverlayType.OLNONE: // Undefined; the remaining values MUST
                                 // correspond with array OLKeywords.
        case OverlayType.OLSUBJECT: // Has text for menu entry
        case OverlayType.OLSOUND: // Has name of .WAV sound file
        case OverlayType.OLHEADING: // Using heading string for the data-set
            break;
        default:
            System.out.println("Unsupported overlay type: " + overlay.type);
        }

        if (component != null) {
            setFont(overlay.type, component);
        }
        return component;
    }

    private void setFont(int overlayType, JComponent overlayComp) {
        if (overlayType == OverlayType.OLFEATURE) {
            overlayComp.setFont(_imageSettings.getDefaultFeatureFont());
        } else if (overlayComp instanceof OverlayButton) {
            overlayComp.setFont(_imageSettings.getDefaultButtonFont());
        } else {
            overlayComp.setFont(_imageSettings.getDefaultFont());
        }
    }

    public HotSpot createHotSpot(ImageOverlay overlay, int index) {
        if (overlay.location.get(index).drawType == OLDrawType.ellipse) {
            return new OvalHotSpot(overlay, index);
        } else {
            return new RectangleHotSpot(overlay, index);
        }
    }
}
