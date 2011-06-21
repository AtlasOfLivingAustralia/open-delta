package au.org.ala.delta.ui.image.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.ImageViewer;


public abstract class HotSpot extends JPanel implements OverlayLocationProvider {

	private static final long serialVersionUID = 603361088992199888L;

	private ImageOverlay _overlay;
	
	private boolean _popup;
	
	private int _index;
	
	private boolean _inHotSpotRegion = false;
	
	private boolean _drawHotSpot = true;
	
	private boolean _foregroundSet;
	
	private Color _foreground;
	
	private List<HotSpotObserver> _observers;
	
	public HotSpot(ImageOverlay overlay, int index) {
		_overlay = overlay;
		_index = index;
		_observers = new ArrayList<HotSpotObserver>();
		
		au.org.ala.delta.model.image.OverlayLocation loc = _overlay.getLocation(_index);
		
		_foregroundSet = loc.isColorSet();
		if (_foregroundSet){
			_foreground = new Color(loc.getColor());
		}
		_popup = loc.isPopup();
		
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (_drawHotSpot) {
			
			if (_foregroundSet && _inHotSpotRegion) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setStroke(new BasicStroke(2f));
				g.setColor(_foreground);
			}
			else {
				g.setColor(Color.BLACK);
				g.setXORMode(Color.WHITE);
			}
			drawHotSpot(g);
		}
	}
	
	protected abstract void drawHotSpot(Graphics g);
	
	public void setMouseInHotSpotRegion(boolean inHotSpot) {
		if (inHotSpot != _inHotSpotRegion) {
			_inHotSpotRegion = inHotSpot;
			repaint();
		}
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		_foregroundSet = true;
	}
	
	@Override
	public OverlayLocation location(ImageViewer viewer) {
		return new ScaledOverlayLocation(viewer, _overlay.getLocation(_index));
	}
	
	public void addHotSpotObserver(HotSpotObserver observer) {
		_observers.add(observer);
	}
	
	public void removeHotSpotObserver(HotSpotObserver observer) {
		_observers.remove(observer);
	}
	
	protected void fireHotSpotEntered() {
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).hotSpotEntered(_overlay);
		}
	}
	
	protected void fireHotSpotExited() {
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).hotSpotExited(_overlay);
		}
	}
	
	protected void fireHotSpotSelected() {
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).hotSpotSelected(_overlay);
		}
	}
}
