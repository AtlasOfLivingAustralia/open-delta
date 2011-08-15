package au.org.ala.delta.editor.ui.image;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.image.ImageOverlayEditorController.ButtonAlignment;
import au.org.ala.delta.editor.ui.util.MenuBuilder;
import au.org.ala.delta.editor.ui.util.PopupMenuListener;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.Image;
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
	private ButtonAlignment _buttonAlignment;
	private Rectangle _lastButtonBorder;
	private EditorViewModel _model;
	private ResourceMap _resources;
	private ActionMap _actions;
	private ImageEditorSelectionModel _selection;
	private ImageOverlayEditorController _controller;
	
	public ImageEditorPanel(Image image, EditorViewModel model) {
		super(image, model.getImageSettings());
		_model = model;
		_editingEnabled = true;
		_editing = false;
		_buttonAlignment = ButtonAlignment.ALIGN_VERTICAL;
		_resources = Application.getInstance().getContext().getResourceMap();
		_selection = new ImageEditorSelectionModel();
		_selection.setSelectedImage(image);
		_controller = new ImageOverlayEditorController(_selection, _model);
		_actions = Application.getInstance().getContext().getActionMap(_controller);
		
		addEventHandlers();
	}
	
	public void setEditingEnabled(boolean enabled) {
		_editingEnabled = enabled;
		if (!_editingEnabled) {
			resetBorder();
		}
	}
	
	private void addEventHandlers() {
		
		new PopupDisplayer(this);
	}
	
	private void addComponentListeners() {
		for (JComponent overlayComp : _components) {
			new PopupDisplayer(overlayComp);
			new OverlayComponentListener(overlayComp);
		}
		
	}
	
	@Override
	public void addOverlays() {
		super.addOverlays();
		addComponentListeners();		
	}
	
	public void select(JComponent overlayComp) {
		if (!_editingEnabled) {
			return;
		}
		
		if (_selectedOverlayComp != overlayComp) {
			
			
			if (_selectedOverlayComp != null) {
				resetBorder();
			}
			
			_selectedOverlayComp = overlayComp;
			_selection.setSelectedOverlayComponent(overlayComp);
			
			if (overlayComp != null) {
				Border border = overlayComp.getBorder();
				Border selectedBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(selectedBorder, border);
				overlayComp.putClientProperty("Opaque", overlayComp.isOpaque());
				// Have to do this or buttons on the MAC don't render properly 
				// with a compound border.
				overlayComp.setOpaque(true);
				
				_selectedOverlayComp.setBorder(compoundBorder);
			}
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
			boundsToOverlayLocation(_selectedOverlayComp);
			repaint();
		}
	}
	
	private void boundsToOverlayLocation(JComponent component) {
		
		 OverlayLocationProvider locationProvider = (OverlayLocationProvider) component;
         OverlayLocation location = locationProvider.location(this);
         location.updateLocationFromBounds(component.getBounds());
         
        _image.updateOverlay(_selection.getSelectedOverlay());
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
		
		if (_selectedOverlayComp instanceof JButton && _buttonAlignment != ButtonAlignment.ALIGN_NONE) {
			drawButtonSelectionBorder(g);
		}
		else {
			_lastButtonBorder = null;
		}
		
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
			g.setColor(Color.LIGHT_GRAY);
			
			_lastButtonBorder = new Rectangle(topLeft.x, topLeft.y, bottomRight.x-topLeft.x, bottomRight.y-topLeft.y);
			g.drawRect(_lastButtonBorder.x, _lastButtonBorder.y, _lastButtonBorder.width, _lastButtonBorder.height);
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
	

	public JPopupMenu buildPopupMenu() {
		boolean itemImage = (_model.getSelectedImage().getSubject() instanceof Item);
		List<String> popupMenuActions = new ArrayList<String>();
		if (_selectedOverlayComp != null) {
			popupMenuActions.add("editSelectedOverlay");
			popupMenuActions.add("deleteSelectedOverlay");
			popupMenuActions.add("-");
		}
		popupMenuActions.add("deleteAllOverlays");
		popupMenuActions.add("-");
		popupMenuActions.add("displayImageSettings");
		popupMenuActions.add("-");
		popupMenuActions.add("cancelPopup");
		
		JPopupMenu popup = new JPopupMenu();
		MenuBuilder.buildMenu(popup, popupMenuActions, _actions);

		if (_selectedOverlayComp != null) {
			List<String> stackOverlayMenuActions = new ArrayList<String>();
			stackOverlayMenuActions.add("stackSelectedOverlayHigher");
			stackOverlayMenuActions.add("stackSelectedOverlayLower");
			stackOverlayMenuActions.add("stackSelectedOverlayOnTop");
			stackOverlayMenuActions.add("stackSelectedOverlayOnBottom");
			JMenu stackOverlayMenu = new JMenu(_resources.getString("overlayPopup.stackOverlayMenu"));
			MenuBuilder.buildMenu(stackOverlayMenu, stackOverlayMenuActions, _actions);
			popup.add(stackOverlayMenu, 2);
		}
		List<String> insertOverlayMenuActions = new ArrayList<String>();
		insertOverlayMenuActions.add("addTextOverlay");
		if (itemImage) {
			insertOverlayMenuActions.add("addItemNameOverlay");
		}
		insertOverlayMenuActions.add("-");
		if (!itemImage) {
			insertOverlayMenuActions.add("addAllUsualOverlays");
			insertOverlayMenuActions.add("addFeatureDescriptionOverlay");
			
			insertOverlayMenuActions.add("addStateOverlay");
			insertOverlayMenuActions.add("addHotspot");
			insertOverlayMenuActions.add("-");
		}
		insertOverlayMenuActions.add("addOkOverlay");
		insertOverlayMenuActions.add("addCancelOverlay");
		if (!itemImage) {
			insertOverlayMenuActions.add("addNotesOverlay");
		}
		else {
			insertOverlayMenuActions.add("addImageNotesOverlay");
		}
		
		JMenu insertOverlayMenu = new JMenu(_resources.getString("overlayPopup.insertOverlayMenu"));
		MenuBuilder.buildMenu(insertOverlayMenu, insertOverlayMenuActions, _actions);
		int indexModifier = _selectedOverlayComp == null ? 4 : 0;
		popup.add(insertOverlayMenu, 5-indexModifier);
		
		List<String> alignButtonsMenuActions = new ArrayList<String>();
		alignButtonsMenuActions.add("useDefaultButtonAlignment");
		alignButtonsMenuActions.add("alignButtonsVertically");
		alignButtonsMenuActions.add("alignButtonsHorizontally");
		alignButtonsMenuActions.add("dontAlignButtons");
		JMenu alignButtonsMenu = new JMenu(_resources.getString("overlayPopup.alignButtonsMenu"));
		MenuBuilder.buildMenu(alignButtonsMenu, alignButtonsMenuActions, _actions);
		popup.add(alignButtonsMenu, 7-indexModifier);
		
		return popup;
	}
	
	
	class OverlayComponentListener extends MouseAdapter implements MouseMotionListener {

		private JComponent _overlayComp;
		private MouseEvent _pressedEvent;
		private int corner = 6;
		private OverlayComponentEditor _editor;
		
		public OverlayComponentListener(JComponent overlayComp) {
			_overlayComp = overlayComp;
			_overlayComp.addMouseListener(this);
			_overlayComp.addMouseMotionListener(this);
		}
		
		@Override
		public void mousePressed(MouseEvent e) {		
			select(_overlayComp);
			_pressedEvent = SwingUtilities.convertMouseEvent(_overlayComp, e, ImageEditorPanel.this);
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
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (_pressedEvent != null) {
				_pressedEvent = null;
				_editor = null;
				stopEdit();
			}
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
			else {
				
			}
			_pressedEvent = e2;
			
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
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
			return buildPopupMenu();
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
			Rectangle bounds = _selectedOverlayComp.getBounds();
			bounds.x+=dx;
			bounds.y+=dy;
			
			_selectedOverlayComp.setBounds(bounds);
			repaint();
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
