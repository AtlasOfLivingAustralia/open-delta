package au.org.ala.delta.ui.image;

import au.org.ala.delta.model.image.ImageOverlay;

public interface SelectableOverlay {

	public boolean isSelected();
	public void setSelected(boolean selected);
	
	public ImageOverlay getImageOverlay();
	
	public void addOverlaySelectionObserver(OverlaySelectionObserver observer);
	public void removeOverlaySelectionObserver(OverlaySelectionObserver observer);
	
}
