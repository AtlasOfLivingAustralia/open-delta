package au.org.ala.delta.ui.image;

import au.org.ala.delta.model.image.ImageOverlay;

/**
 * Allows interested observers to receive notification of when an Overlay
 * has been selected by the user and take appropriate action.
 */
public interface OverlaySelectionObserver {
	public void overlaySelected(ImageOverlay overlay);
}
