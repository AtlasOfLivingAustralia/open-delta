package au.org.ala.delta.ui.image.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.ImageViewer;


public abstract class HotSpot extends JPanel implements OverlayLocationProvider {

	private static final long serialVersionUID = 603361088992199888L;

	private ImageOverlay _overlay;
	
	private int _index;
	
	private boolean _drawHotspot = true;
	
	private boolean _foregroundSet;
	
	public HotSpot(ImageOverlay overlay, int index) {
		_overlay = overlay;
		_index = index;
		
		au.org.ala.delta.model.image.OverlayLocation loc = _overlay.getLocation(_index);
		
		_foregroundSet = loc.colorSet();
		if (_foregroundSet){
			setForeground(new Color(loc.getColor()));
		}
		
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (_drawHotspot) {
			
			if (_foregroundSet) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setStroke(new BasicStroke(2f));
				g.setColor(getForeground());
			}
			else {
				g.setXORMode(Color.WHITE);
			}
			drawHotSpot(g);
		}
	}
	
	protected abstract void drawHotSpot(Graphics g);
	
	protected void setMouseInHotSpotRegion(boolean inHotSpot) {
		_drawHotspot = true;
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
}
