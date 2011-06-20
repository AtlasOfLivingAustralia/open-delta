package au.org.ala.delta.ui.image.overlay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.ImageViewer;

/**
 * An OverlayButton is used to display the image overlay types:
 * OK, Cancel and Notes.
 */
public class OverlayButton extends JButton implements ActionListener, OverlayLocationProvider {
	private static final long serialVersionUID = 7019370330547978789L;

	private ImageOverlay _overlay;
	
	public OverlayButton(ImageOverlay overlay, String text) {
		super(text);
		_overlay = overlay;
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//getParent().close();
	}

	@Override
	public OverlayLocation location(ImageViewer viewer) {
		return new ScaledOverlayLocation(viewer, _overlay.getLocation(0));
	}
}
