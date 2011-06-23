package au.org.ala.delta.ui.image.overlay;

import javax.swing.JComponent;

import au.org.ala.delta.ui.image.ImageViewer;

/**
 * The PreferredSizeFixedCentreOverlayLocation behaves the same as the
 * FixedCentreOverlayLocation except that is always uses the components
 * preferred size rather than the data in the OverlayLocation.
 */
public class PreferredSizeFixedCentreOverlayLocation extends FixedCentreOverlayLocation {

	public PreferredSizeFixedCentreOverlayLocation(ImageViewer image, JComponent component, au.org.ala.delta.model.image.OverlayLocation location) {
		super(image, component, location);
	}
	
	@Override
	public int getHeight() {
		return _component.getPreferredSize().height;
	}
	
	@Override
	public int getWidth() {
		return _component.getPreferredSize().width;
	}
}
