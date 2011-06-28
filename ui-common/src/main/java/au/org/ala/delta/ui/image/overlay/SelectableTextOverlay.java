package au.org.ala.delta.ui.image.overlay;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;

/**
 * A SelectableTextOverlay is a specialized version of a RichTextLabel which
 * also has a "selected" property.  It indicates the state of the selected
 * property by inverting the foregound and background colours.
 *
 */
public class SelectableTextOverlay extends RichTextLabel implements MouseListener, SelectableOverlay {

	private static final long serialVersionUID = 2451885327158264330L;

	private boolean _selected;
	private SelectableOverlaySupport _support;
	
	public SelectableTextOverlay(ImageOverlay overlay, String text) {
		super(overlay, text);
		_selected = false;
		_support = new SelectableOverlaySupport();
		_editor.addMouseListener(this);
	}
	
	public void setSelected(boolean selected) {
		if (selected != _selected) {
			_selected = selected;
			// Toggle the foreground and background to indicate selection
			// state.
			Color foreground = _editor.getForeground();
			_editor.setForeground(_editor.getBackground());
			_editor.setBackground(foreground);
			setBorder(BorderFactory.createLineBorder(_editor.getForeground()));
		}
	}
	
	public boolean isSelected() {
		return _selected;
	}


	@Override
	public ImageOverlay getImageOverlay() {
		return _overlay;
	}

	@Override
	public void addOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_support.addOverlaySelectionObserver(observer);
	}

	@Override
	public void removeOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_support.removeOverlaySelectionObserver(observer);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		_support.fireOverlaySelected(this);
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
