package au.org.ala.delta.ui.image.overlay;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;

import au.org.ala.delta.model.image.ImageOverlay;

public class OvalHotSpot extends HotSpot implements MouseMotionListener {

	private static final long serialVersionUID = -5472490468706567843L;

	public OvalHotSpot(ImageOverlay overlay, int index) {
		super(overlay, index);
		addMouseMotionListener(this);
	}
	
	protected void drawHotSpot(Graphics g) {
		Rectangle bounds = getBounds();
		g.drawOval(0, 0, bounds.width-1, bounds.height-1);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		Rectangle bounds = getBounds();
		Shape ellipse = new Ellipse2D.Double(0d, 0d, (double)bounds.width-1, (double)bounds.height-1);
		
		setMouseInHotSpotRegion(ellipse.contains(e.getX(), e.getY()));
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

}
