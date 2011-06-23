package au.org.ala.delta.ui.image.overlay;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;

public class SelectableOverlaySupport {

	private List<OverlaySelectionObserver> _observers;

	public SelectableOverlaySupport() {
		_observers = new ArrayList<OverlaySelectionObserver>();
	}
	
	public void addOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_observers.add(observer);
	}
	
	public void removeOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_observers.remove(observer);
	}
	
	public void fireOverlaySelected(SelectableOverlay overlay) {
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).overlaySelected(overlay);
		}
	}
}
