package au.org.ala.delta.ui.image.overlay;

import au.org.ala.delta.model.image.ImageOverlay;

public interface HotSpotObserver {
	public void hotSpotEntered(ImageOverlay overlay);
	public void hotSpotExited(ImageOverlay overlay);
	public void hotSpotSelected(ImageOverlay overlay);
}
