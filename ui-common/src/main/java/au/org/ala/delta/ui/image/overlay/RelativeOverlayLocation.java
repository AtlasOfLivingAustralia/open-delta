package au.org.ala.delta.ui.image.overlay;

import javax.swing.JComponent;

import au.org.ala.delta.ui.image.ImageViewer;

/**
 * The RelativeOverlayLocation determines the location of the overlay
 * component based on the location of another component.
 */
public class RelativeOverlayLocation extends FixedCentreOverlayLocation {

	protected OverlayLocation _relativeTo;
	
	public RelativeOverlayLocation(ImageViewer image, JComponent component, 
			au.org.ala.delta.model.image.OverlayLocation location,
			OverlayLocation relativeTo) {
		super(image, component, location);
		_relativeTo = relativeTo;
	}
	
	@Override
	public int getX() {
		if (_location.X == Short.MIN_VALUE) {
			return _relativeTo.getX() + _relativeTo.getWidth() + 1;
		}
		else {
			return super.getX();
		}
	}
	
	@Override
	public int getY() {
		if (_location.X == Short.MIN_VALUE) {
			return _relativeTo.getY();
		}
		else {
			return super.getY();
		}
	}
	
	
}