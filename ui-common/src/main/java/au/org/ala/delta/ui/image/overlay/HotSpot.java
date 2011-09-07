package au.org.ala.delta.ui.image.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayLocation;
import au.org.ala.delta.ui.image.ImageViewer;

/**
 * A HotSpot is a selectable region on an Image which is associated with 
 * a particular image overlay.  Multiple HotSpots can be associated with 
 * a single Overlay - see HotSpotGroup.
 */
public abstract class HotSpot extends JPanel implements OverlayLocationProvider, MouseListener {

	private static final long serialVersionUID = 603361088992199888L;

	private ImageOverlay _overlay;
	
	/** If popup is true the HotSpot will draw the outline of the region when
	 * the mouse in inside the HotSpot region (or a HotSpot region in the 
	 * same group. */
	private boolean _popup;
	
	private int _index;
	
	/** True while the mouse in inside the HotSpot region */
	private boolean _inHotSpotRegion = false;
	
	/** True if the region outline should be drawn regardless of the mouse position */
	private boolean _drawHotSpot = true;
	
	/** True if the foreground colour of the HotSpot has been specified. */
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
	}

	@Override
	protected void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (_popup && _inHotSpotRegion) {	
			g2.setStroke(new BasicStroke(2f));
			if (_foregroundSet) {
				g.setColor(_foreground);
				g.setPaintMode();
			}
			else {
				g.setColor(Color.BLACK);
				g.setXORMode(Color.WHITE);
			}
			drawHotSpot(g);
		}
		else if (_drawHotSpot){
			g.setColor(Color.BLACK);
			g.setXORMode(Color.WHITE);
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
	
	/**
	 * If alwaysDrawHotspot is true the popup will be drawn even if the
	 * mouse is not in the hot spot region.
	 * @param drawHotspot true if the hotspot outline should be drawn 
	 * regardless of the position of the mouse.
	 */
	public void setAlwaysDrawHotSpot(boolean drawHotSpot) {
		_drawHotSpot = drawHotSpot;
	}
	
	@Override
	public au.org.ala.delta.ui.image.overlay.OverlayLocation location(ImageViewer viewer) {
		return new ScaledOverlayLocation(viewer, _overlay.getLocation(_index));
	}
	
	@Override
	public boolean isOpaque() {
		return false;
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

	@Override
	public void mouseClicked(MouseEvent e) {
		if (_inHotSpotRegion) {
			fireHotSpotSelected();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	public OverlayLocation getOverlayLocation() {
		return _overlay.getLocation(_index);
	}
	
}
