package au.org.ala.delta.ui.image.overlay;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.ImageViewer;

/**
 * The RelativePositionedTextOverlay behaves the same as a RichTextLabel
 * except that it's position can be expressed relative to another overlay.
 * This is used mainly for a units overlay.
 */
public class RelativePositionedTextOverlay extends RichTextLabel {

	private static final long serialVersionUID = 4782350523854302175L;
	
	private OverlayLocationProvider _relativeTo;
	
	public RelativePositionedTextOverlay(ImageOverlay overlay, String text) {
		super(overlay, text);
	}
	
	@Override
	public OverlayLocation location(ImageViewer viewer) {
		OverlayLocation location = null;
		if (_relativeTo != null) {
			location = _relativeTo.location(viewer);
		}
		return new RelativeOverlayLocation(viewer, this, _overlay.getLocation(0), location);
	
	}

	public void makeRelativeTo(OverlayLocationProvider relativeTo) {
		_relativeTo = relativeTo;
	}
}
