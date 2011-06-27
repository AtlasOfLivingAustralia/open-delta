package au.org.ala.delta.ui.image.overlay;

import javax.swing.JTextField;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.ImageViewer;

public class TextFieldOverlay extends JTextField implements OverlayLocationProvider  {

	private static final long serialVersionUID = 2629894270642514618L;

	private ImageOverlay _overlay;
	
	public TextFieldOverlay(ImageOverlay overlay) {
		_overlay = overlay;
	}
	
	@Override
	public OverlayLocation location(ImageViewer viewer) {
		return new FixedCentreOverlayLocation(viewer, this, _overlay.getLocation(0));
	}
}
