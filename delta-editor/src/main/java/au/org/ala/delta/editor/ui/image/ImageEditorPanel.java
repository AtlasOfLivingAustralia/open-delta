package au.org.ala.delta.editor.ui.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.util.PopupMenuListener;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings.ButtonAlignment;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.image.overlay.OverlayLocation;
import au.org.ala.delta.ui.image.overlay.OverlayLocationProvider;

/**
 * Extends the functionality of the ImageView to allow editing (as well as
 * display) of the image overlays.
 */
public class ImageEditorPanel extends ImageViewer {

	private static final long serialVersionUID = -4018788405322108640L;

	private JComponent _selectedOverlayComp;
	/** A flag that indicates a component is being dragged to a new location */
	private boolean _editing;
	
	private boolean _editingEnabled;
	private Rectangle _lastButtonBorder;
	private EditorViewModel _model;
	private ImageEditorSelectionModel _selection;
	private ImageOverlayEditorController _controller;
	
	public ImageEditorPanel(Image image, EditorViewModel model) {
		super(image, model.getImageSettings());
		_model = model;
		_editingEnabled = true;
		_editing = false;
		_selection = new ImageEditorSelectionModel();
		_selection.setSelectedImage(image);
		_controller = new ImageOverlayEditorController(_selection, _model);
		
		addEventHandlers();
	}
	
	public void setEditingEnabled(boolean enabled) {
		_editingEnabled = enabled;
		if (!_editingEnabled) {
			select(null);
		}
	}
	
	private void addEventHandlers() {
		
		new PopupDisplayer(this);
	}
	
	@Override
	protected void addMouseListeners() {
		for (JComponent overlayComp : _components) {
			new OverlayMouseListener(overlayComp);
		}
		
	}
	
	public void select(JComponent overlayComp) {
		if (!_editingEnabled) {
			return;
		}
		_selectedOverlayComp = overlayComp;
		_selection.setSelectedOverlayComponent(overlayComp);
		repaint();
	}
		
	public void startEdit(JComponent overlayComp) {
		if (!_editingEnabled) {
			return;
		}
		
		if (_selectedOverlayComp == overlayComp) {
			if (!_editing) {
				_editing = true;
				setLayout(null);
			}
			
			repaint();
		}
	}
	
	public void stopEdit() {
		if (!_editingEnabled) {
			return;
		}
		if (_editing) {
			_editing = false;
		
			// The layout has to be reset before the overlay is updated.
			setLayout(this);
			if (_selectedOverlayComp instanceof JButton && (_controller.getButtonAlignment() != ButtonAlignment.NO_ALIGN)) {
				List<JComponent> buttons = getButtons();
				for (JComponent comp : buttons) {
					boundsToOverlayLocation(comp);
				}
			}
			else {
				boundsToOverlayLocation(_selectedOverlayComp);
				
			}
			repaint();
		}
	}
	
	private void boundsToOverlayLocation(JComponent component) {
		
		 OverlayLocationProvider locationProvider = (OverlayLocationProvider) component;
         OverlayLocation location = locationProvider.location(this);
         location.updateLocationFromBounds(component.getBounds());
         ImageOverlay overlay = (ImageOverlay)component.getClientProperty("ImageOverlay");
        _image.updateOverlay(overlay);
	}
	
	/**
	 * Draws the image (scaled if requested) onto the background of the
	 * panel.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		if (_lastButtonBorder != null) {
			// Extend the clip to ensure the old rectangle is overdrawn 
			// correctly.
			Rectangle clip = SwingUtilities.computeUnion(
					_lastButtonBorder.x,_lastButtonBorder.y, _lastButtonBorder.width+1, _lastButtonBorder.height+1, g.getClipBounds());
			g.setClip(clip);
		}
		super.paintComponent(g);
		
		if (_selectedOverlayComp instanceof JButton && _controller.getButtonAlignment() != ButtonAlignment.NO_ALIGN) {
			drawButtonSelectionBorder(g);
		}
		else {
			if (_selectedOverlayComp != null) {
			drawSelectionBorder(_selectedOverlayComp.getBounds(), g);
			}
			_lastButtonBorder = null;
		}
		
	}
	
	private void drawSelectionBorder(Rectangle bounds, Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Stroke old = g2.getStroke();
		int thick = 3;
		Stroke borderStroke = new BasicStroke(thick);
		g2.setStroke(borderStroke);
		g2.setColor(Color.RED);
		g2.drawRect(bounds.x-thick, bounds.y-thick, bounds.width+2*thick-1, bounds.height+2*thick-1);
		
		g2.setStroke(old);
	}

	private void drawButtonSelectionBorder(Graphics g) {
		// Draw a border around both buttons...
		List<JComponent> buttons = getButtons();
		
		// We are assuming the buttons have already been aligned before
		// doing this check.
		if (!buttons.isEmpty()) {
			Point topLeft = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
			Point bottomRight = new Point(-1, -1);
			for (JComponent button : buttons) {
				Rectangle buttonBounds = button.getBounds();
				if (buttonBounds.x < topLeft.x) {
					topLeft.x = buttonBounds.x;
				}
				if (buttonBounds.y < topLeft.y) {
					topLeft.y = buttonBounds.y;
				}
				if (buttonBounds.x+buttonBounds.width > bottomRight.x) {
					bottomRight.x = buttonBounds.x+buttonBounds.width;
				}
				if (buttonBounds.y+buttonBounds.height > bottomRight.y) {
					bottomRight.y = buttonBounds.y+buttonBounds.height;
				}
			}
			Rectangle bounds = new Rectangle(topLeft.x, topLeft.y, bottomRight.x-topLeft.x, bottomRight.y-topLeft.y);
			drawSelectionBorder(bounds, g);
		}
	}
	
	private List<JComponent> getButtons() {
		List<JComponent> buttons = new ArrayList<JComponent>();
		for (JComponent comp : _components) {
			if (comp instanceof JButton) {
				buttons.add(comp);
			}
		}
		return buttons;
	}
	
	class OverlayMouseListener extends PopupDisplayer implements MouseMotionListener {
		private JComponent _overlayComp;
		private MouseEvent _pressedEvent;
		private int corner = 6;
		private OverlayComponentEditor _editor;
		
		public OverlayMouseListener(JComponent overlayComp) {
			super(overlayComp);
			_overlayComp = overlayComp;
			_overlayComp.addMouseMotionListener(this);
		}
		
		@Override
		public void mousePressed(MouseEvent e) {		
			if (!_editingEnabled) {
				return;
			}
			select(_overlayComp);
			super.mousePressed(e);
			_pressedEvent = SwingUtilities.convertMouseEvent(_overlayComp, e, ImageEditorPanel.this);
			if (!(_overlayComp instanceof JButton)) {
				if (inTopLeft(e)) {
					_editor = new OverlayComponentResizer(_overlayComp, SwingConstants.SOUTH_EAST);
				}
				else if (inTopRight(e)) {
					_editor = new OverlayComponentResizer(_overlayComp, SwingConstants.SOUTH_WEST);
				}
				else if (inBottomLeft(e)) {
					_editor = new OverlayComponentResizer(_overlayComp, SwingConstants.NORTH_EAST);
				}
				else if (inBottomRight(e)) {
					_editor = new OverlayComponentResizer(_overlayComp, SwingConstants.NORTH_WEST);
				}
				else {
					_editor = new OverlayComponentMover(_overlayComp);
				}	
			}
			else {
				_editor = new OverlayComponentMover(_overlayComp);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (_pressedEvent != null) {
				_pressedEvent = null;
				_editor = null;
				stopEdit();
			}
			super.mouseReleased(e);
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			
			if (!_editing) {
				startEdit(_overlayComp);
			}
			MouseEvent e2 = SwingUtilities.convertMouseEvent(_overlayComp, e, ImageEditorPanel.this);
			
			int dx = e2.getX() - _pressedEvent.getX();
			int dy = e2.getY() - _pressedEvent.getY();
			
			if (_editor != null) {
				_editor.mouseDragged(dx, dy);
			}
			
			_pressedEvent = e2;
			
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (!_editingEnabled) {
				return;
			}
			if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
				_controller.editSelectedOverlay();
			}
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			if (_selectedOverlayComp == _overlayComp) {
				if (!(_selectedOverlayComp instanceof JButton)) {
			
					if (inTopLeft(e)) {
						setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
					}
					else if (inTopRight(e)) {
						setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
					}
					else if (inBottomLeft(e)) {
						setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
					}
					else if (inBottomRight(e)) {
						setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					}
					else {
						setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					}
				}
				else {
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
			}
			else {
				setCursor(Cursor.getDefaultCursor());
			}
		}
		
		private boolean inTopLeft(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			return (x <= corner && y <= corner);
		}
		private boolean inTopRight(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int w = _overlayComp.getWidth();
			return (x >= w-corner && y <= corner);
		}
		private boolean inBottomLeft(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int h = _overlayComp.getHeight();
			return (x <= corner && y >= h - corner);
		}
		private boolean inBottomRight(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int w = _overlayComp.getWidth();
			int h = _overlayComp.getHeight();
			return (x >= w-corner && y >= h - corner);
		}
	}
	
	class PopupDisplayer extends PopupMenuListener {
		public PopupDisplayer(JComponent comp) {
			super(null, comp);
		}

		@Override
		protected void showPopup(Point p) {
			if (_component == ImageEditorPanel.this) {
				select(null);
			}
			_selection.setSelectedPoint(p);
			super.showPopup(p);
		}

		@Override
		protected JPopupMenu getPopup() {
			return _controller.buildPopupMenu();
		}
		
	}
	
	abstract class OverlayComponentEditor {
		JComponent _overlayComp;
		Rectangle _originalBounds;
		
		
		public OverlayComponentEditor(JComponent overlayComp) {
			_overlayComp = overlayComp;
			_originalBounds = overlayComp.getBounds();
		}
		
		public abstract void mouseDragged(int dx, int dy);
	}
	
	class OverlayComponentMover extends OverlayComponentEditor {
		public OverlayComponentMover(JComponent overlayComp) {
			super(overlayComp);
		}
		
		public void mouseDragged(int dx, int dy) {
			moveBounds(dx, dy, _selectedOverlayComp);
			// Special case for aligned buttons - move the whole lot.
			if (_selectedOverlayComp instanceof JButton && _controller.getButtonAlignment() != ButtonAlignment.NO_ALIGN) {
				List<JComponent> buttons = getButtons();
				for (JComponent comp : buttons) {
					if (comp != _selectedOverlayComp) {
						moveBounds(dx, dy, comp);
					}
				}
			}
			repaint();
		}
		
		private void moveBounds(int dx, int dy, JComponent comp) {
			Rectangle bounds = comp.getBounds();
			bounds.x+=dx;
			bounds.y+=dy;
			
			comp.setBounds(bounds);
		}
	}
	
	class OverlayComponentResizer extends OverlayComponentEditor {
		
		int _anchor;
		
		public OverlayComponentResizer(JComponent overlayComp, int anchor) {
			super(overlayComp);
			_anchor = anchor;
		}
		
		public void mouseDragged(int dx, int dy) {
			Dimension min = _overlayComp.getMinimumSize();
			Rectangle bounds = _overlayComp.getBounds();
			if (_anchor == SwingConstants.NORTH_WEST) {
				int width = Math.max(min.width, bounds.width + dx);
				int height = Math.max(min.height, bounds.height + dy);
				
				_overlayComp.setBounds(bounds.x, bounds.y, width, height);
			}
			else if (_anchor == SwingConstants.NORTH_EAST) {
				int x = bounds.x + dx;
				x = Math.min(x, _originalBounds.x+_originalBounds.width-min.width);
				int width = Math.max(min.width, bounds.width-dx);
				int height = Math.max(min.height, bounds.height + dy);
				_overlayComp.setBounds(x, bounds.y, width, height);
			}
			else if (_anchor == SwingConstants.SOUTH_WEST) {
				int width = Math.max(min.width, bounds.width + dx);
				int y = bounds.y+dy;
				y = Math.min(_originalBounds.y+_originalBounds.height-min.height, y);
				int height = Math.max(min.height, bounds.height - dy);
				
				_overlayComp.setBounds(bounds.x, y, width, height);
			}
			else {
				int x = bounds.x+dx;
				x = Math.min(x, _originalBounds.x+_originalBounds.width-min.width);
				int y = bounds.y+dy;
				y = Math.min(y, _originalBounds.y+_originalBounds.height-min.height);
				int width = bounds.width-dx;
				int height = bounds.height-dy;
				_overlayComp.setBounds(x, y, width, height);
			}
			_overlayComp.repaint();
		}
	}
	
}
