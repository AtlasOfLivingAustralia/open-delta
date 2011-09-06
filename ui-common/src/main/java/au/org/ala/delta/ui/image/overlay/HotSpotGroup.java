package au.org.ala.delta.ui.image.overlay;

import java.util.HashSet;
import java.util.Set;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;

/**
 * Aggregates a set of HotSpots to allow them to act as a single region.
 */
public class HotSpotGroup implements HotSpotObserver, SelectableOverlay {

	private Set<HotSpot> _hotspots;
	private SelectableTextOverlay _overlay;
	private SelectableOverlaySupport _support;
	
	public HotSpotGroup(SelectableTextOverlay overlay) {
		_hotspots = new HashSet<HotSpot>();
		_support = new SelectableOverlaySupport();
		_overlay = overlay;
	}
	
	public void setDisplayHotSpots(boolean displayHotSpots) {
		
		for (HotSpot hotSpot : _hotspots) {
			hotSpot.setAlwaysDrawHotSpot(displayHotSpots);
		}
	}
	
	public void add(HotSpot hotSpot) {
		_hotspots.add(hotSpot);
		hotSpot.addHotSpotObserver(this);
	}
	
	public void setMouseInHotSpotRegion(boolean inHotSpotRegion) {
		for (HotSpot hotSpot : _hotspots) {
			hotSpot.setMouseInHotSpotRegion(inHotSpotRegion);
		}
	}

	@Override
	public void hotSpotEntered(ImageOverlay overlay) {
		setMouseInHotSpotRegion(true);
		_overlay.handleMouseEntered();
	}

	@Override
	public void hotSpotExited(ImageOverlay overlay) {
		setMouseInHotSpotRegion(false);
		_overlay.handleMouseExited();
	}

	@Override
	public void hotSpotSelected(ImageOverlay overlay) {
		_support.fireOverlaySelected(_overlay);
	}
	
	@Override
	public void addOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_support.addOverlaySelectionObserver(observer);
	}

	@Override
	public void removeOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_support.removeOverlaySelectionObserver(observer);
	}

	@Override
	public boolean isSelected() {
		return _overlay.isSelected();
	}

	@Override
	public void setSelected(boolean selected) {
		_overlay.setSelected(selected);
	}

	@Override
	public ImageOverlay getImageOverlay() {
		return _overlay.getImageOverlay();
	}
	
	public Set<HotSpot> getHotSpots() {
	    return _hotspots;
	}
}
