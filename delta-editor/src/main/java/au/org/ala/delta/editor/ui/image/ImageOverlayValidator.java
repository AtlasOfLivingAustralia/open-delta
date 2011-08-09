package au.org.ala.delta.editor.ui.image;

import au.org.ala.delta.model.image.ImageOverlay;

/**
 * Validates an ImageOverlay.
 */
public class ImageOverlayValidator {

	
	
	public void validate(ImageOverlay overlay) {
		validateDimension(overlay.getX());
		
	}
	
	public void validateDimension(int dimension) {
		if ((dimension < 0) || (dimension > 1000)) {
			
		}
	}
	
	public void validateLineHeight(int lineHeight) {
		if ((lineHeight < 0) || (lineHeight > 50)) {
			
		}
	}
	
	private void validateState(int state) {
		
	}
}
