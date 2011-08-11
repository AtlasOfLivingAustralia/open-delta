package au.org.ala.delta.editor.ui.image;

import java.awt.Color;
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
	private boolean _dragging;
	private boolean _editingEnabled;
	private ButtonAlignment _buttonAlignment;
	private Rectangle _lastButtonBorder;
	private EditorViewModel _model;
	private ResourceMap _resources;
	private ActionMap _actions;
	private Point _popupMenuLocation;
	
	public ImageEditorPanel(Image image, EditorViewModel model) {
		super(image, model.getImageSettings());
		_model = model;
		_editingEnabled = true;
		_buttonAlignment = ButtonAlignment.ALIGN_VERTICAL;
		_resources = Application.getInstance().getContext().getResourceMap();
		_actions = Application.getInstance().getContext().getActionMap(this);
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
		new ImageOverlayEditorController(this, _model);
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
		
		boundsToOverlayLocation(_selectedOverlayComp);
		
		setLayout(this);
		revalidate();
	}
	
	private void boundsToOverlayLocation(JComponent component) {
		
		 OverlayLocationProvider locationProvider = (OverlayLocationProvider) component;
         OverlayLocation location = locationProvider.location(this);
         location.updateLocationFromBounds(component.getBounds());
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
		popupMenuActions.add("editSelectedOverlay");
		popupMenuActions.add("deleteSelectedOverlay");
		popupMenuActions.add("-");
		popupMenuActions.add("deleteAllOverlays");
		popupMenuActions.add("-");
		popupMenuActions.add("displayImageSettings");
		popupMenuActions.add("-");
		popupMenuActions.add("cancelPopup");
		
		JPopupMenu popup = new JPopupMenu();
		MenuBuilder.buildMenu(popup, popupMenuActions, _actions);

		List<String> stackOverlayMenuActions = new ArrayList<String>();
		stackOverlayMenuActions.add("stackSelectedOverlayHigher");
		stackOverlayMenuActions.add("stackSelectedOverlayLower");
		stackOverlayMenuActions.add("stackSelectedOverlayOnTop");
		stackOverlayMenuActions.add("stackSelectedOverlayOnBottom");
		JMenu stackOverlayMenu = new JMenu(_resources.getString("overlayPopup.stackOverlayMenu"));
		MenuBuilder.buildMenu(stackOverlayMenu, stackOverlayMenuActions, _actions);
		popup.add(stackOverlayMenu, 2);
		
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
		popup.add(insertOverlayMenu, 5);
		
		List<String> alignButtonsMenuActions = new ArrayList<String>();
		alignButtonsMenuActions.add("useDefaultButtonAlignment");
		alignButtonsMenuActions.add("alignButtonsVertically");
		alignButtonsMenuActions.add("alignButtonsHorizontally");
		alignButtonsMenuActions.add("dontAlignButtons");
		JMenu alignButtonsMenu = new JMenu(_resources.getString("overlayPopup.alignButtonsMenu"));
		MenuBuilder.buildMenu(alignButtonsMenu, alignButtonsMenuActions, _actions);
		popup.add(alignButtonsMenu, 7);
		
		return popup;
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
	
	class PopupDisplayer extends PopupMenuListener {
		public PopupDisplayer() {
			super(null, ImageEditorPanel.this);
		}

		@Override
		protected void showPopup(Point p) {
			_popupMenuLocation = p;
			super.showPopup(p);
		}

		@Override
		protected JPopupMenu getPopup() {
			return buildPopupMenu();
		}
		
	}
	
}
