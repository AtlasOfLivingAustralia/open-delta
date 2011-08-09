package au.org.ala.delta.editor.ui.image;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.ui.image.ImageViewer;

/**
 * Extends the functionality of the ImageView to allow editing (as well as
 * display) of the image overlays.
 */
public class ImageEditorPanel extends ImageViewer {

	private static final long serialVersionUID = -4018788405322108640L;

	private JComponent _selectedOverlayComp;
	private boolean _dragging;
	private boolean _editingEnabled;
	
	public ImageEditorPanel(Image image, ImageSettings imageSettings) {
		super(image, imageSettings);
		_editingEnabled = true;
		addEventHandlers();
	}
	
	public void setEditingEnabled(boolean enabled) {
		_editingEnabled = enabled;
		if (!_editingEnabled) {
			resetBorder();
		}
	}
	
	private void addEventHandlers() {
		for (JComponent overlayComp : _components) {
			new OverlayComponentListener(overlayComp);
		}
	}
	
	public void select(JComponent overlayComp) {
		if (!_editingEnabled) {
			return;
		}
		
		if (_selectedOverlayComp != overlayComp) {
			Border border = overlayComp.getBorder();
			
			if (_selectedOverlayComp != null) {
				resetBorder();
			}
			Border selectedBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
			CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(selectedBorder, border);
			overlayComp.putClientProperty("Opaque", overlayComp.isOpaque());
			// Have to do this or buttons on the MAC don't render properly 
			// with a compound border.
			overlayComp.setOpaque(true);
			_selectedOverlayComp = overlayComp;
			_selectedOverlayComp.setBorder(compoundBorder);
		}
	}
	

	public Insets borderInsets() {
		CompoundBorder compoundBorder = (CompoundBorder)_selectedOverlayComp.getBorder();
		Border b = compoundBorder.getOutsideBorder();
		Insets insets = null;
		if (b != null) {
			insets = b.getBorderInsets(_selectedOverlayComp);
		}
		else {
			insets = new Insets(0, 0, 0, 0);
		}
		return insets;
	}
	
	private void resetBorder() {
		if (_selectedOverlayComp != null) {
			boolean opaque = (Boolean)_selectedOverlayComp.getClientProperty("Opaque");
			_selectedOverlayComp.setOpaque(opaque);
			CompoundBorder compoundBorder = (CompoundBorder)_selectedOverlayComp.getBorder();
			_selectedOverlayComp.setBorder(compoundBorder.getInsideBorder());
		}
	}
	
	public void move(JComponent overlayComp, int dx, int dy) {
		if (!_editingEnabled) {
			return;
		}
		
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
		if (!_editingEnabled) {
			return;
		}
		_dragging = false;
		setLayout(this);
		revalidate();
	}
	
	/**
	 * Overrides layoutOverlays to adjust the bounds of the selected component
	 * to handle the compound border that indicates selection.
	 */
	@Override
	protected void layoutOverlays() { 
		super.layoutOverlays();
		if (_selectedOverlayComp != null) {
			Insets insets = borderInsets();
			Rectangle bounds = _selectedOverlayComp.getBounds();
			bounds.x -= insets.left;
			bounds.width += insets.left + insets.right;
			bounds.y -= insets.top;
			bounds.height += insets.bottom+insets.top;
			
			_selectedOverlayComp.setBounds(bounds);
		}
	}
	
	
	class OverlayComponentListener extends MouseAdapter implements MouseMotionListener {

		private JComponent _overlayComp;
		private MouseEvent _pressedEvent;
		
		public OverlayComponentListener(JComponent overlayComp) {
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
		public void mouseMoved(MouseEvent e) {}
	}
}
