package au.org.ala.delta.ui.image.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import au.org.ala.delta.model.image.ImageOverlay;


public abstract class HotSpot extends JPanel {

	private static final long serialVersionUID = 603361088992199888L;

	private ImageOverlay _overlay;
	
	private int _index;
	
	private boolean _drawHotspot = true;
	
	private boolean _foregroundSet;
	
	public HotSpot(ImageOverlay overlay, int index) {
		_overlay = overlay;
		_foregroundSet = false;
		_index = index;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (_drawHotspot) {
			
			if (_foregroundSet) {
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
	public Dimension getPreferredSize() {
		// Returning null means the OverlayLocation class will calculate
		// it.
		return null;
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		_foregroundSet = true;
	}
}
