package au.org.ala.delta.ui.image.overlay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.model.image.ImageOverlay;

/**
 * Aggregates a set of HotSpots to allow them to act as a single region.
 */
public class HotSpotGroup implements HotSpotObserver {

	private Set<HotSpot> _hotspots;
	private List<HotSpotObserver> _observers;
	private SelectableTextOverlay _overlay;
	
	public HotSpotGroup(SelectableTextOverlay overlay) {
		_hotspots = new HashSet<HotSpot>();
		_observers = new ArrayList<HotSpotObserver>();
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
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).hotSpotEntered(overlay);
		}
	}

	@Override
	public void hotSpotExited(ImageOverlay overlay) {
		setMouseInHotSpotRegion(false);
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).hotSpotExited(overlay);
		}
	}

	@Override
	public void hotSpotSelected(ImageOverlay overlay) {
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).hotSpotSelected(overlay);
		}
		_overlay.setSelected(!_overlay.isSelected());
	}
	
	public void addHotSpotObserver(HotSpotObserver observer) {
		_observers.add(observer);
	}
	
	public void removeHotSpotObserver(HotSpotObserver observer) {
		_observers.remove(observer);
	}

	
}
