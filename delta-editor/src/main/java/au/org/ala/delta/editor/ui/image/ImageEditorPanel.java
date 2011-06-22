package au.org.ala.delta.editor.ui.image;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.ui.image.ImageViewer;

/**
 * Extends the functionality of the ImageView to allow editing (as well as
 * display) of the image overlays.
 */
public class ImageEditorPanel extends ImageViewer {

	private static final long serialVersionUID = -4018788405322108640L;

	private JComponent _selectedOverlayComp;
	private boolean _dragging;
	
	public ImageEditorPanel(String imagePath, Image image, DeltaDataSet dataSet) {
		super(imagePath, image, dataSet);
		
		addEventHandlers();
	}
	
	private void addEventHandlers() {
		for (JComponent overlayComp : _components) {
			new GrowBaby(overlayComp);
		}
	}
	
	
	public void select(JComponent overlayComp) {
		if (_selectedOverlayComp != overlayComp) {
			Border border = overlayComp.getBorder();
			overlayComp.putClientProperty("OldBorder", border);
			if (_selectedOverlayComp != null) {
				_selectedOverlayComp.setBorder((Border)_selectedOverlayComp.getClientProperty("OldBorder"));
			}
			_selectedOverlayComp = overlayComp;
			_selectedOverlayComp.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		}
	}
	
	public void move(JComponent overlayComp, int dx, int dy) {
		if (_selectedOverlayComp == overlayComp) {
			if (!_dragging) {
				_dragging = true;
				setLayout(null);
			}
			
			Rectangle bounds = _selectedOverlayComp.getBounds();
			bounds.x+=dx;
			bounds.y+=dy;
			
			_selectedOverlayComp.setBounds(bounds);
			repaint();
		}
	}
	
	public void stopMove() {
		_dragging = false;
		setLayout(this);
		revalidate();
	}
	
	
	class GrowBaby extends MouseAdapter implements MouseMotionListener {

		private JComponent _overlayComp;
		private MouseEvent _pressedEvent;
		
		public GrowBaby(JComponent overlayComp) {
			_overlayComp = overlayComp;
			_overlayComp.addMouseListener(this);
			_overlayComp.addMouseMotionListener(this);
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			select(_overlayComp);
			_pressedEvent = SwingUtilities.convertMouseEvent(_overlayComp, e, ImageEditorPanel.this);
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (_pressedEvent != null) {
				_pressedEvent = null;
				stopMove();
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			MouseEvent e2 = SwingUtilities.convertMouseEvent(_overlayComp, e, ImageEditorPanel.this);
			
			int dx = e2.getX() - _pressedEvent.getX();
			int dy = e2.getY() - _pressedEvent.getY();
			move(_overlayComp, dx, dy);
			_pressedEvent = e2;
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
