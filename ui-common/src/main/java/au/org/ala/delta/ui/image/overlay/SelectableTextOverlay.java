package au.org.ala.delta.ui.image.overlay;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;

import au.org.ala.delta.model.image.ImageOverlay;

public class SelectableTextOverlay extends RichTextLabel implements MouseListener {

	private static final long serialVersionUID = 2451885327158264330L;

	private boolean _selected;
	
	public SelectableTextOverlay(ImageOverlay overlay, String text) {
		super(overlay, text);
		_selected = false;
		addMouseListener(this);
	}
	
	public void setSelected(boolean selected) {
		if (selected != _selected) {
			_selected = selected;
			// Toggle the foreground and background to indicate selection
			// state.
			Color foreground = getForeground();
			setForeground(getBackground());
			setBackground(foreground);
			setBorder(BorderFactory.createLineBorder(getForeground()));
		}
	}
	
	public boolean isSelected() {
		return _selected;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		setSelected(!_selected);
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
